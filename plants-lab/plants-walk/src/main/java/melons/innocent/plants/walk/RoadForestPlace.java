package melons.innocent.plants.walk;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.bench.Place;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.plants.tree.Bough;
import melons.innocent.plants.tree.Kind;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A road through a forest without end: the forest of samples ({@link Endless}, {@link SampledDrawn}), in winter,
 * standing in a strip either side of a road that wanders on for ever the way the world's third number grows
 * ({@link Road}), with no tree on the road or close beside it. The road is made as it is travelled and is another
 * road every time; the forest it runs through is the one forest, the same wherever the road happens to cross it.
 *
 * <p>A square of the forest the road touches cannot be a copy of its sample, some of its trees being gone, so its
 * trees that are left are drawn one by one ({@link WoodDrawn}); every other square in the strip is its sample, as
 * in the forest without a road. Where the road is no longer had, far behind, the forest stands whole again: it
 * closes over where the road was.
 *
 * <p>The avatar is kept near the road: a wall nobody sees runs along each side of it some way out among the trees,
 * and another across it behind, which comes on as the avatar goes on, so the road can be gone back along only a
 * little. Those, and the trunks near the eye, are said again whenever the eye has gone two squares on.
 */
public final class RoadForestPlace implements Place {

    private static final double METRE = 100;
    private static final int EACH = 2;
    private static final long CHANCE = 20261124L;
    /** How far either side of the road's middle the forest stands, and within how far of it no tree does. */
    private static final double STRIP = 15_000;
    private static final double CLEAR = 500;
    /** How far a road may be from a square's middle, across, and still touch it; beyond that it is not looked at. */
    private static final double TOUCHING = 4_500;
    /** How many squares each way from the eye's own the trunks are in the way, and how far it goes before more. */
    private static final long POSTED = 6;
    private static final long STRAYED = 2;

    private final List<WoodDrawn.Grown> grown = new ArrayList<>();
    /** Each grown tree's trunk: kept, since asking a tree for its boughs grows it afresh. */
    private final List<Bough> trunks = new ArrayList<>();
    /** Each sample's trees, in its own frame: kept, so nothing of a sample is worked out again. */
    private final List<List<WoodDrawn.Planted>> planted = new ArrayList<>();
    private final Endless endless;
    private final Reaches reaches;
    private final Road road;
    private final RoadDrawn drawn;
    private final SampledDrawn forest;
    private final WoodDrawn wood;
    /** The squares the road touched when their trees were last planted, each as its two numbers in one. */
    private Set<String> touched = new HashSet<>();
    private Vec3 eye = new Vec3(0, 0, 0);
    private double furthest;
    private long postedAcross;
    private long postedAlong;

    /** The forest and a road nobody chose, on {@code card}, as the run says the forest. */
    public RoadForestPlace(final Card card, final Laid laid) {
        this(card, laid, new Road(new Wandering()), Integer.getInteger("forest.samples", 32), new Reaches());
    }

    /** The forest of {@code samples} samples on {@code card}, with {@code road} through it. */
    public RoadForestPlace(final Card card, final Laid laid, final Road road, final int samples,
                           final Reaches reaches) {
        for (final Kind kind : Kind.values()) {
            for (int number = 1; number <= EACH; number++) {
                grown.add(new WoodDrawn.Grown(kind, kind.tree(number)));
                trunks.add(grown.getLast().tree().boughs().getFirst());
            }
        }
        this.road = road;
        this.reaches = reaches;
        this.endless = new Endless(new Samples(samples, 25 * METRE, grown.size(), CHANCE), CHANCE);
        for (int sample = 0; sample < samples; sample++) {
            planted.add(endless.samples().planted(sample));
        }
        this.drawn = new RoadDrawn(card, laid, road);
        try {
            this.wood = new WoodDrawn(card, laid, grown, List.of(), Optional.empty());
        } catch (final RuntimeException refused) {
            drawn.close();
            throw refused;
        }
        try {
            this.forest = new SampledDrawn(card, laid, endless, grown, reaches, reaches.far() * METRE, true);
        } catch (final RuntimeException refused) {
            wood.close();
            drawn.close();
            throw refused;
        }
    }

    /** The road through it. */
    public Road road() {
        return road;
    }

    @Override
    public String said() {
        return String.format(java.util.Locale.ENGLISH, "A road: %.0f m along, %.0f m across; %d squares of it "
                + "tree by tree%n", eye.z() / METRE, road.has(eye.z()) ? road.across(eye.z()) / METRE : 0,
                touched.size()) + forest.said();
    }

    @Override
    public boolean onTheFlat() {
        return false;
    }

    /** Whether a tree standing at {@code across}, {@code along} is gone: on the road, or close beside it. */
    private boolean cleared(final double across, final double along) {
        return road.has(along) && road.off(across, along) <= CLEAR;
    }

    @Override
    public void readied(final Camera camera) {
        eye = camera.eye();
        furthest = Math.max(furthest, eye.z());
        if (road.reached(furthest)) {
            drawn.remade();
            touched = new HashSet<>();
        }
        final double side = endless.samples().side();
        final List<Endless.Seen> samples = new ArrayList<>();
        final List<WoodDrawn.Planted> trees = new ArrayList<>();
        final Set<String> touching = new HashSet<>();
        for (final Endless.Seen seen : endless.seen(eye, reaches)) {
            final Vec3 middle = endless.middle(seen.across(), seen.along());
            final double off = Math.abs(middle.x() - road.across(Math.clamp(middle.z(), road.from(), road.to())));
            if (off > STRIP + side) {
                continue;
            }
            final List<WoodDrawn.Planted> stood = off > TOUCHING || !road.has(middle.z()) ? List.of()
                    : endless.placed(seen.across(), seen.along(), planted.get(seen.sample()));
            if (stood.stream().anyMatch(tree -> cleared(tree.placed().at(0, 3), tree.placed().at(2, 3)))) {
                touching.add(seen.across() + " " + seen.along());
                stood.stream().filter(tree -> !cleared(tree.placed().at(0, 3), tree.placed().at(2, 3)))
                        .forEach(trees::add);
            } else {
                samples.add(seen);
            }
        }
        forest.readied(camera, samples);
        if (!touching.equals(touched)) {
            wood.replanted(trees);
            touched = touching;
        }
    }

    @Override
    public Obstacles inTheWay() {
        final double side = endless.samples().side();
        postedAcross = (long) Math.floor(eye.x() / side);
        postedAlong = (long) Math.floor(eye.z() / side);
        final List<Block> blocks = new ArrayList<>(drawn.walls(eye.z(), furthest));
        for (long b = postedAlong - POSTED; b <= postedAlong + POSTED; b++) {
            for (long a = postedAcross - POSTED; a <= postedAcross + POSTED; a++) {
                for (final WoodDrawn.Planted tree : endless.placed(a, b, planted.get(endless.sample(a, b)))) {
                    final double thick = trunks.get(tree.grown()).thick().getFirst() * tree.times();
                    if (!cleared(tree.placed().at(0, 3), tree.placed().at(2, 3))) {
                        blocks.add(new Block(tree.placed().at(0, 3), tree.placed().at(2, 3), thick, thick, 0, 0,
                                trunks.get(tree.grown()).ends().y() * tree.times()));
                    }
                }
            }
        }
        return new Obstacles(blocks);
    }

    @Override
    public Optional<Obstacles> rearranged() {
        final double side = endless.samples().side();
        return Math.abs(Math.floor(eye.x() / side) - postedAcross) >= STRAYED
                || Math.abs(Math.floor(eye.z() / side) - postedAlong) >= STRAYED
                ? Optional.of(inTheWay()) : Optional.empty();
    }

    @Override
    public boolean settled() {
        return forest.settled();
    }

    @Override
    public void drawn(final Pass pass, final Camera camera) {
        drawn.drawn(pass, camera, new Mat4(new Vec3(eye.x(), 0, eye.z())));
        forest.drawn(pass, camera);
        wood.drawn(pass, camera);
    }

    @Override
    public void seenThrough(final Pass pass, final Camera camera) {
        wood.seenThrough(pass, camera);
    }

    /** How many squares the road touches, of those in reach: each drawn tree by tree. */
    public int touched() {
        return touched.size();
    }

    @Override
    public void close() {
        forest.close();
        wood.close();
        drawn.close();
    }
}
