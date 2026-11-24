package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.V3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The muscles of a region found at rest, together, by finite elements: their lattices ({@link Lattice}) one soft
 * material ({@link Elastic}, corotated), kept apart by their own surfaces and out of the bones
 * ({@link SurfaceContact}), each held where it is cut and where it is held to its bones. Crossing muscles cannot be
 * pulled apart from where they overlap at first, so they are first let draw themselves in, their rest shape made
 * small, till each is little more than a line between where it is held (drawn in); then grown back to their size
 * with each kept from the others and from the bones, so each finds its way round the others as it swells (growing);
 * then let come to rest (settling). A step at a time, each so many sweeps.
 */
public final class SoftSolve {

    /** How free a node is: as free as any, or fixed. */
    private static final double FREE = 1;
    /** How far apart the fascia's rings are, cm; and the points of the grid the muscles as walls are known on. */
    private static final double FASCIA_STEP = 0.5;
    private static final double FROZEN_CELL = 0.25;

    /** Where the solve has got to. */
    public enum Phase { DRAWN_IN, GROWING, SETTLING, DONE }

    /**
     * How it is solved.
     *
     * @param youngs  the material's Young's modulus, kPa
     * @param poisson its Poisson's ratio
     * @param least   how small the rest shape is made at first, of its size
     * @param drawIn  how many steps it draws itself in for
     * @param grow    how many it grows for
     * @param settle  how many it settles for
     * @param sweeps  how many sweeps a step
     * @param quick   how many times as stiff as the material a step holds it, for a solve at rest
     * @param apart   how far apart two muscles' surfaces are kept, cm
     * @param clear   how far outside the bones a muscle's surface is kept, cm
     * @param hold    how near a place a muscle is held to its bones a node is fixed, cm
     * @param tension how much shorter than drawn a muscle is at rest along its fibres, so that it pulls
     * @param fascia  whether, as they settle, a sleeve of fascia wraps them and presses them together ({@link Fascia})
     * @param layered whether they are solved a layer at a time, the deepest first, each layer on those under it, as
     *                they lie, each drawn in, grown and settled so many steps; else all at once
     */
    public record Settings(double youngs, double poisson, double least, int drawIn, int grow, int settle, int sweeps,
                           double quick, double apart, double clear, double hold, double tension, boolean fascia,
                           boolean layered) {

        /** As the neck is first tried: a layer at a time. */
        public static Settings first() {
            return new Settings(10, 0.45, 0.35, 30, 90, 60, 2, 1, 0.02, 0.05, 0.75, 0.08, true, true);
        }
    }

    /**
     * How a step left it.
     *
     * @param phase    where it has got to
     * @param step     how many steps it has taken
     * @param steps    how many it takes in all
     * @param scale    how big the rest shape is, of its size
     * @param touching how many points of a muscle's surface were inside another, in its last sweep
     * @param inBone   how many points of the muscles' surfaces were too near the bones, in its last sweep
     * @param room     how much room the elements take now, of their room at rest at full size
     * @param pressed  how many points of the muscles' surfaces pressed on the fascia, in its last sweep
     * @param sleeve   how much room the fascia holds, cm3; none, before it wraps them
     * @param depth    how deep the layer being solved lies, the outermost 1; none, all at once
     */
    public record Moment(Phase phase, int step, int steps, double scale, int touching, int inBone, double room,
                         int pressed, double sleeve, int depth) {
    }

    private final List<Lattice> lattices;
    private final int[] first;
    private final double[] x;
    private final double[] w;
    private final Elastic elastic;
    private final SurfaceContact contact;
    private final SurfaceContact.Surfaces surfaceOf;
    private final SoftRegion region;
    private final List<Mesher.Mesh> surfaces;
    /** The muscles round them and the layers come to rest, as walls. */
    private final MuscleWalls frozen;
    private Optional<Fascia> fascia = Optional.empty();
    private final Settings settings;
    private final double rest;
    /** Each node's muscle, each muscle's depth, and the depths there are, the deepest first. */
    private final int[] ownerOf;
    private final int[] depthOf;
    private final int[] ranks;
    /** How free each node is in the layer being solved, and which muscles take part in it; for which layer. */
    private double[] free;
    private boolean[] taking;
    private boolean[] firm;
    private int freeFor = -1;
    private int step;

    /**
     * The muscles of {@code region}, their {@code lattices} and the {@code surfaces} carried in them, each held near
     * each of its {@code holds}, its fibres running as its {@code fibres} say and lying as deep as {@code depths} says
     * (by the same order), kept out of {@code walls} and of the muscles held round them, {@code around}, solved as
     * {@code settings} say. Solved a layer at a time, each layer kept out of those under it as of bone.
     */
    public SoftSolve(final SoftRegion region, final List<Lattice> lattices, final List<Mesher.Mesh> surfaces,
                     final List<List<V3>> holds, final List<Along> fibres, final List<Integer> depths,
                     final List<Mesher.Mesh> around, final Walls walls, final Settings settings) {
        this.lattices = List.copyOf(lattices);
        this.settings = settings;
        first = new int[lattices.size() + 1];
        for (int k = 0; k < lattices.size(); k++) {
            first[k + 1] = first[k] + lattices.get(k).nodeCount();
        }
        x = new double[3 * first[lattices.size()]];
        w = new double[first[lattices.size()]];
        ownerOf = new int[w.length];
        depthOf = depths.stream().mapToInt(Integer::intValue).toArray();
        ranks = depths.stream().distinct().sorted(java.util.Comparator.reverseOrder()).mapToInt(Integer::intValue)
                .toArray();
        final List<Integer> tets = new ArrayList<>();
        final List<V3> ways = new ArrayList<>();
        for (int k = 0; k < lattices.size(); k++) {
            final Lattice l = lattices.get(k);
            for (int i = 0; i < l.nodeCount(); i++) {
                final int g = first[k] + i;
                final V3 v = l.node(i);
                x[3 * g] = v.x();
                x[3 * g + 1] = v.y();
                x[3 * g + 2] = v.z();
                w[g] = l.fixed()[i] || held(v, holds.get(k), settings.hold()) ? 0 : FREE;
                ownerOf[g] = k;
            }
            for (final int t : l.tets()) {
                tets.add(first[k] + t);
            }
            for (int t = 0; t < l.tetCount(); t++) {
                V3 middle = V3.ZERO;
                for (int c = 0; c < 4; c++) {
                    middle = middle.plus(l.node(l.tets()[4 * t + c]).times(0.25));
                }
                ways.add(fibres.get(k).at(middle));
            }
        }
        final double mu = settings.youngs() / (2 * (1 + settings.poisson()));
        final double lambda = settings.youngs() * settings.poisson()
                / ((1 + settings.poisson()) * (1 - 2 * settings.poisson()));
        final double[] fibre = new double[3 * ways.size()];
        for (int t = 0; t < ways.size(); t++) {
            fibre[3 * t] = ways.get(t).x();
            fibre[3 * t + 1] = ways.get(t).y();
            fibre[3 * t + 2] = ways.get(t).z();
        }
        elastic = new Elastic(tets.stream().mapToInt(Integer::intValue).toArray(), x, mu, lambda,
                settings.quick(), fibre, settings.tension());
        this.region = region;
        surfaceOf = new SurfaceContact.Surfaces(surfaces, lattices, first);
        this.surfaces = List.copyOf(surfaces);
        frozen = new MuscleWalls(region, FROZEN_CELL);
        around.forEach(frozen::add);
        contact = new SurfaceContact(surfaceOf, new EitherWalls(walls, frozen));
        rest = elastic.room(x);
    }

    private boolean held(final V3 v, final List<V3> holds, final double near) {
        for (final V3 h : holds) {
            if (v.minus(h).length() <= near) {
                return true;
            }
        }
        return false;
    }

    /** How many steps it takes in all. */
    public int steps() {
        return perLayer() * (settings.layered() ? ranks.length : 1);
    }

    /** How many steps a layer, or all at once, take. */
    private int perLayer() {
        return settings.drawIn() + settings.grow() + settings.settle();
    }

    /** Whether it has taken them all. */
    public boolean done() {
        return step >= steps();
    }

    /** One step on: so many sweeps of the elements, and, once it grows, of what keeps them apart and out of bone. */
    public Moment stepped() {
        final int local = step % perLayer();
        final int layer = step / perLayer();
        final Phase phase = phase(local);
        final double scale = scale(local);
        final int depth = settings.layered() ? ranks[layer] : 0;
        layered(layer, depth);
        elastic.begun();
        int near = 0;
        int bone = 0;
        int pressed = 0;
        final boolean outermost = !settings.layered() || layer == ranks.length - 1;
        if (phase == Phase.SETTLING && outermost && settings.fascia() && fascia.isEmpty()) {
            fascia = Optional.of(new Fascia(region, FASCIA_STEP, surfaceOf.placed(x), surfaceOf.carried()));
        }
        for (int s = 0; s < settings.sweeps(); s++) {
            elastic.swept(x, free, scale);
            final int[] kept = contact.kept(x, free, settings.apart(), settings.clear(), phase != Phase.DRAWN_IN,
                    fascia, taking, firm);
            near = kept[0];
            bone = kept[1];
            pressed = kept[2];
        }
        step++;
        return new Moment(done() ? Phase.DONE : phase(step % perLayer()), step, steps(), scale, near, bone,
                elastic.room(x) / rest, pressed, fascia.map(Fascia::room).orElse(0.0), depth);
    }

    /**
     * Ready for layer {@code layer}, its muscles {@code depth} deep: only they free and taking part; those deeper held
     * where they came to rest, made walls to them as the bones are, and those shallower not yet taking part. All, if
     * solved all at once.
     */
    private void layered(final int layer, final int depth) {
        if (layer == freeFor) {
            return;
        }
        if (settings.layered() && layer > 0) {
            final List<Lattice> now = lattices();
            for (int k = 0; k < depthOf.length; k++) {
                if (depthOf[k] == ranks[layer - 1]) {
                    frozen.add(new Mesher.Mesh(now.get(k).carrying(surfaces.get(k).points()),
                            surfaces.get(k).triangles()));
                }
            }
        }
        freeFor = layer;
        free = w.clone();
        taking = new boolean[depthOf.length];
        firm = new boolean[depthOf.length];
        for (int k = 0; k < depthOf.length; k++) {
            taking[k] = !settings.layered() || depthOf[k] == depth;
            firm[k] = settings.layered() && depthOf[k] > depth;
        }
        for (int g = 0; g < free.length; g++) {
            if (settings.layered() && depthOf[ownerOf[g]] != depth) {
                free[g] = 0;
            }
        }
    }

    private Phase phase(final int at) {
        if (at < settings.drawIn()) {
            return Phase.DRAWN_IN;
        }
        if (at < settings.drawIn() + settings.grow()) {
            return Phase.GROWING;
        }
        return at < steps() ? Phase.SETTLING : Phase.DONE;
    }

    /** How big the rest shape is at step {@code at}: small while drawn in, growing evenly, then whole. */
    private double scale(final int at) {
        if (at < settings.drawIn()) {
            return settings.least();
        }
        final double grown = Math.min(1, (at - settings.drawIn() + 1.0) / settings.grow());
        return settings.least() + (1 - settings.least()) * grown;
    }

    /** The fascia wrapping the muscles, once it does. */
    public Optional<Fascia> fascia() {
        return fascia;
    }

    /** How many elements are turned inside out now. */
    public int inverted() {
        return elastic.inverted(x);
    }

    /** The lattices as they are now, by the order they were given. */
    public List<Lattice> lattices() {
        final List<Lattice> now = new ArrayList<>();
        for (int k = 0; k < lattices.size(); k++) {
            final Lattice l = lattices.get(k);
            final float[] nodes = new float[3 * l.nodeCount()];
            for (int i = 0; i < nodes.length; i++) {
                nodes[i] = (float) x[3 * first[k] + i];
            }
            now.add(new Lattice(l.cell(), nodes, l.tets(), l.fixed(), l.holder(), l.weights()));
        }
        return now;
    }
}
