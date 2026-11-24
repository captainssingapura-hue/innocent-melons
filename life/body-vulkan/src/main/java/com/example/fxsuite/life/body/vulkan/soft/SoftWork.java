package com.example.fxsuite.life.body.vulkan.soft;

import com.example.fxsuite.avatar.bench.kit.Bus;
import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.muscle.Measured;
import com.example.fxsuite.life.body.muscle.Muscle;
import com.example.fxsuite.life.body.muscle.MuscleSurface;
import com.example.fxsuite.life.body.muscle.Musculature;
import com.example.fxsuite.life.body.muscle.MuscleShape;
import com.example.fxsuite.life.body.soft.CageLattice;
import com.example.fxsuite.life.body.soft.BoneWalls;
import com.example.fxsuite.life.body.soft.Lattice;
import com.example.fxsuite.life.body.soft.TissueMap;
import com.example.fxsuite.life.body.soft.LatticeMaker;
import com.example.fxsuite.life.body.soft.RegionPieces;
import com.example.fxsuite.life.body.soft.SoftScene;
import com.example.fxsuite.life.body.soft.SoftSolve;
import com.example.fxsuite.life.body.soft.SolveStore;
import com.example.fxsuite.life.body.vulkan.BodyPieces;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * What the soft bench works out away from the screen, one thing at a time: every muscle, once; each region asked, what
 * it takes in and its elements; and, when asked, the region's muscles solved at rest ({@link SoftSolving}), what is
 * drawn said as it goes, and each solve kept ({@link SolveStore}), so a region solved before is shown solved at once
 * without solving it afresh; in {@code -Dsoft.cache}, else a folder of the user's. What is drawn is left to be
 * taken up by the card's thread; what is said goes out on the bench's bus. A region asked afresh stops a solve.
 */
public final class SoftWork implements AutoCloseable {

    private static final V3 HEEL = new V3(0, -6.4, -4.0);
    private static final double BILLION = 1e9;
    /** How big the cells of one piece are, cm; and how wide a gap between muscles connective tissue fills, a half. */
    private static final double ONE_CELL = 0.2;
    private static final double CLOSE = 0.3;

    /** What is made once: every muscle on both sides, ready for the card; the bones; how far the body is lifted. */
    public record Made(List<RegionPieces.Piece> all, SoftPieces pieces, List<BodyPieces.Bone> bones, double lift) {
    }

    /** A region found: as asked, what it takes in, their elements, and the name its solve is kept by. */
    private record Region(SoftAsked asked, RegionPieces.Taken taken, List<Lattice> lattices, String name) {
    }

    private final Bus bus;
    private final Rig rig;
    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        final Thread t = new Thread(r, "soft");
        t.setDaemon(true);
        return t;
    });
    private final AtomicReference<Made> made = new AtomicReference<>();
    private final AtomicReference<SoftPieces.Found> found = new AtomicReference<>();
    private volatile SoftAsked askedFor;
    private volatile Region region;
    private volatile boolean stopping;
    private volatile boolean solving;
    private volatile boolean solved;
    private final SolveStore store = new SolveStore(Path.of(System.getProperty("soft.cache",
            System.getProperty("user.home") + "/.cache/innocent-melons/soft")));
    /** What is drawn of the region as it was found, and as solved if it has been; which is shown. */
    private volatile SoftPieces.Found atRest;
    private volatile SoftPieces.Found asSolved;
    private volatile boolean showingSolved;

    /** Working for a bench on {@code bus}, the body {@code rig} holds. */
    public SoftWork(final Bus bus, final Rig rig) {
        this.bus = bus;
        this.rig = rig;
    }

    /** Every muscle made, and the bones: begun, not waited for. */
    public void begun() {
        worker.submit(said(() -> {
            bus.publish(new Bus.Working(true, "every muscle"));
            final long began = System.nanoTime();
            final List<RegionPieces.Piece> all = all();
            made.set(new Made(all, new SoftPieces(all), new BodyPieces().bones(rig, new Detail(0.4)),
                    -rig.frame(Part.RIGHT_FOOT).place(HEEL).y()));
            bus.publish(new Bus.Working(false, "every muscle"));
            System.out.printf(Locale.ROOT, "every muscle made in %.1f s%n", (System.nanoTime() - began) / BILLION);
        }));
    }

    /** What is made once, when it is. */
    public Optional<Made> made() {
        return Optional.ofNullable(made.get());
    }

    /** What is to be drawn of the region, if anything new has been found since it was last taken; taken. */
    public Optional<SoftPieces.Found> taken() {
        return Optional.ofNullable(found.getAndSet(null));
    }

    /** Whether a solve has ended, since the bench began. */
    public boolean solved() {
        return solved;
    }

    /** The region found afresh, if {@code a} asks for it otherwise than it was last found; a solve stopped. */
    public void asked(final SoftAsked a) {
        if (made.get() == null) {
            return;
        }
        if (a.sameRegion(askedFor)) {
            final boolean wantSolved = a.solved() && asSolved != null;
            if (wantSolved != showingSolved && atRest != null) {
                showingSolved = wantSolved;
                found.set(wantSolved ? asSolved : atRest);
            }
            return;
        }
        askedFor = a;
        stopping = true;
        final Made m = made.get();
        bus.publish(new Bus.Working(true, "the region"));
        worker.submit(said(() -> {
            if (askedFor != a) {
                return;
            }
            stopping = false;
            final RegionPieces.Taken taken = RegionPieces.INSTANCE.taken(a.region(), m.all(), rig, a.share());
            final RegionPieces.Filled filled = RegionPieces.INSTANCE.filled(a.region(), taken.taken());
            final List<Lattice> lattices = taken.taken().parallelStream().map(p -> lattice(a, p)).toList();
            final String name = store.name(a.region(), a.share(), SoftSolve.Settings.first(), lattices);
            region = new Region(a, taken, lattices, name);
            atRest = m.pieces().found(a.region(), taken, lattices);
            asSolved = store.kept(name).map(k -> {
                bus.publish(new SoftConsole.Solving(k.said() + " Kept from before: solving it at rest solves it"
                        + " afresh."));
                return m.pieces().found(a.region(), taken, k.on(lattices), true, k.sleeve());
            }).orElse(null);
            showingSolved = a.solved() && asSolved != null;
            found.set(showingSolved ? asSolved : atRest);
            if (a.onePiece()) {
                onePiece(a, taken, m);
                bus.publish(new Bus.Working(false, "the region"));
                return;
            }
            System.out.println(elements(lattices, filled, a.cell()));
            bus.publish(new SoftConsole.Listed(said(taken, filled) + "\n\n" + elements(lattices, filled, a.cell()),
                    SoftScene.INSTANCE.listed(taken, filled)));
            bus.publish(new Bus.Working(false, "the region"));
        }));
    }

    /**
     * {@code p}'s elements in the region {@code a} asks: made from its own cage, its rings no further apart than the
     * cubes asked; or, if {@code -Dsoft.lattice=cubes}, cut from a grid of cubes.
     */
    private Lattice lattice(final SoftAsked a, final RegionPieces.Piece p) {
        if ("cubes".equals(System.getProperty("soft.lattice"))) {
            return LatticeMaker.INSTANCE.of(a.region(), p.surface().mesh(), a.cell());
        }
        final Measured bulk = new Measured(Proportions.DRAWN.stature() / 100, Proportions.DRAWN.mass());
        return CageLattice.INSTANCE.of(a.region(), MuscleShape.INSTANCE.cage(p.muscle(), bulk, rig, p.left()),
                p.muscle().ring(), p.surface().mesh(), a.cell());
    }

    /**
     * The region {@code a} asks for as one piece ({@link TissueMap}): each of its cells given to one tissue, the
     * taken muscles, connective tissue between them, or bone; drawn, and how much each muscle kept said.
     */
    private void onePiece(final SoftAsked a, final RegionPieces.Taken taken, final Made m) {
        final long began = System.nanoTime();
        final TissueMap map = new TissueMap(a.region(), ONE_CELL,
                taken.taken().stream().map(p -> p.surface().mesh()).toList(), new BoneWalls(a.region(), rig, 0.25),
                CLOSE);
        found.set(m.pieces().oneFound(a.region(), taken, map));
        double kept = 0;
        double drawn = 0;
        final List<String> lost = new ArrayList<>();
        for (int k = 0; k < taken.taken().size(); k++) {
            kept += map.room(k);
            drawn += map.drawn(k);
            final RegionPieces.Piece p = taken.taken().get(k);
            lost.add(String.format(Locale.ROOT, "%5.1f %s%s: %.1f of %.1f cm3", map.drawn(k) - map.room(k),
                    p.muscle().name(), p.muscle().paired() ? p.left() ? ", left" : ", right" : "", map.room(k),
                    map.drawn(k)));
        }
        lost.sort(java.util.Comparator.reverseOrder());
        final String said = String.format(Locale.ROOT, "As one piece, cells %.2f cm a side, made in %.1f s: the %d"
                        + " muscles keep %.0f cm3 of the %.0f cm3 drawn in the region (%.0f%%), none of it shared;"
                        + " connective tissue fills %.0f cm3 between them.", ONE_CELL,
                (System.nanoTime() - began) / BILLION, taken.taken().size(), kept, drawn, 100 * kept / drawn,
                map.filler());
        System.out.println(said);
        bus.publish(new SoftConsole.Listed(said, "Given up most, cm3:\n" + String.join("\n",
                lost.subList(0, Math.min(25, lost.size())))));
    }

    /** The region last found solved at rest, if it is not being: begun, not waited for. */
    public void solve() {
        if (solving || made.get() == null) {
            return;
        }
        solving = true;
        stopping = false;
        worker.submit(said(() -> {
            final Region r = region;
            if (r == null) {
                solving = false;
                return;
            }
            bus.publish(new Bus.Working(true, "the solve"));
            new SoftSolving().solved(r.asked().region(), r.taken(), r.lattices(), rig, made.get().pieces(),
                    SoftSolve.Settings.first(), told -> {
                        if (told.found() != null) {
                            found.set(told.found());
                        }
                        System.out.println(told.said());
                        bus.publish(new SoftConsole.Solving(told.said()));
                    }, () -> stopping).ifPresent(e -> kept(r, e));
            solved = !stopping;
            solving = false;
            bus.publish(new Bus.Working(false, "the solve"));
        }));
    }

    /** How {@code r}'s solve ended, {@code e}, kept, and shown as solved. */
    private void kept(final Region r, final SoftSolving.Ended e) {
        asSolved = made.get().pieces().found(r.asked().region(), r.taken(), e.lattices(), true, e.sleeve());
        showingSolved = true;
        found.set(asSolved);
        try {
            store.keep(r.name(), e.lattices(), e.said(), e.sleeve());
        } catch (final IOException failed) {
            bus.publish(new SoftConsole.Solving(e.said() + " Not kept: " + failed.getMessage()));
        }
    }

    /** A solve stopped, if one is going. */
    public void stop() {
        stopping = true;
    }

    /** {@code work}, any failure in it said, on the bus and printed, not lost in the worker. */
    private Runnable said(final Runnable work) {
        return () -> {
            try {
                work.run();
            } catch (final RuntimeException failed) {
                failed.printStackTrace();
                bus.publish(new Bus.Problems("The soft bench's work failed: " + failed));
            }
        };
    }

    /** How much the region takes in, in a line. */
    private String said(final RegionPieces.Taken taken, final RegionPieces.Filled filled) {
        return String.format(Locale.ROOT, "%d muscles taken in, %d held round them; cut in %,d places, held to their"
                        + " bones in %d. In the region they fill %.0f cm3, %.0f cm3 of it two or more at once (%.0f%%),"
                        + " which the simulation is to bring to nothing.", taken.taken().size(), taken.held().size(),
                taken.cuts().size(), taken.attached().size(), filled.together(), filled.shared(),
                100 * filled.shared() / Math.max(1e-9, filled.together()));
    }

    /** How many elements the taken have, how many of their nodes are fixed, and how much room the lattices take. */
    private String elements(final List<Lattice> lattices, final RegionPieces.Filled filled, final double cell) {
        int nodes = 0;
        int fixed = 0;
        int tets = 0;
        double room = 0;
        for (final Lattice l : lattices) {
            nodes += l.nodeCount();
            fixed += l.fixedCount();
            tets += l.tetCount();
            room += l.volume();
        }
        double muscle = 0;
        for (final double e : filled.each()) {
            muscle += e;
        }
        return String.format(Locale.ROOT, "Their elements: %.2f cm apart at most along them; %,d tetrahedra,"
                + " %,d nodes, %,d of them fixed where the muscles are cut. The lattices take %.0f cm3 for the"
                + " muscles' %.0f cm3 in the region (%.2f times).", cell, tets, nodes, fixed, room, muscle,
                room / Math.max(1e-9, muscle));
    }

    /** Every muscle on both sides, sized as measured, as the body is bound. */
    private List<RegionPieces.Piece> all() {
        final Measured bulk = new Measured(Proportions.DRAWN.stature() / 100, Proportions.DRAWN.mass());
        final List<RegionPieces.Piece> all = new ArrayList<>();
        for (final Muscle m : Musculature.INSTANCE.muscles()) {
            for (final boolean left : m.paired() ? List.of(false, true) : List.of(false)) {
                all.add(new RegionPieces.Piece(m, left, MuscleSurface.INSTANCE.surface(m, bulk, rig, left, 1)));
            }
        }
        return all;
    }

    @Override
    public void close() {
        stopping = true;
        worker.shutdownNow();
    }
}
