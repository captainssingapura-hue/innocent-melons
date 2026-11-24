package com.example.fxsuite.life.body.vulkan.soft;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.muscle.MuscleSurface;
import com.example.fxsuite.life.body.muscle.Section;
import com.example.fxsuite.life.body.soft.Along;
import com.example.fxsuite.life.body.soft.BoneWalls;
import com.example.fxsuite.life.body.soft.Fascia;
import com.example.fxsuite.life.body.soft.Fibres;
import com.example.fxsuite.life.body.soft.Lattice;
import com.example.fxsuite.life.body.soft.RegionPieces;
import com.example.fxsuite.life.body.soft.SoftRegion;
import com.example.fxsuite.life.body.soft.SoftSolve;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * A region's muscles solved at rest, away from the screen ({@link SoftSolve}): the bones round it found as walls, each
 * muscle held where it is held to its bones in the region, then step after step, what is drawn said every few steps,
 * and how it went said as it goes; at the end, how much of the region two muscles or more still fill, against before.
 * Stopped whenever asked.
 */
public final class SoftSolving {

    /** How many steps between what is drawn being said afresh. */
    private static final int SHOWN = 3;
    /** How fine the bones' walls are found, cm. */
    private static final double WALLS = 0.25;

    /**
     * What a solve says as it goes: what is drawn, how it went, in a line.
     *
     * @param found what is drawn now, or none
     * @param said  how it went
     */
    public record Told(SoftPieces.Found found, String said) {
    }

    /**
     * How a solve ended: each muscle's lattice as it ended, by the taken's order, what was said of it, and the fascia.
     *
     * @param lattices each muscle's lattice
     * @param said     what was said
     * @param sleeve   the fascia wrapping them, if one did
     */
    public record Ended(List<Lattice> lattices, String said, Optional<Fascia.Sleeve> sleeve) {
    }

    /**
     * {@code taken} of {@code region}, their elements {@code lattices}, solved as {@code settings} say, on the body
     * {@code rig} holds; what is drawn made by {@code pieces}; each thing to say told to {@code told}; stopped as soon
     * as {@code stopped} says. How it ended; nothing, if it was stopped.
     */
    public Optional<Ended> solved(final SoftRegion region, final RegionPieces.Taken taken, final List<Lattice> lattices,
                       final Rig rig, final SoftPieces pieces, final SoftSolve.Settings settings,
                       final Consumer<Told> told, final BooleanSupplier stopped) {
        long began = System.nanoTime();
        told.accept(new Told(null, "Finding the bones round the region as walls"));
        final BoneWalls walls = new BoneWalls(region, rig, WALLS);
        final List<List<V3>> holds = new ArrayList<>();
        for (final RegionPieces.Piece p : taken.taken()) {
            final List<V3> mine = new ArrayList<>();
            final List<Section> s = p.muscle().sections();
            for (final Section end : List.of(s.getFirst(), s.getLast())) {
                final V3 v = end.at().in(rig, p.left());
                if (region.has(v)) {
                    mine.add(v);
                }
            }
            holds.add(mine);
        }
        final List<Along> fibres = new ArrayList<>();
        for (final RegionPieces.Piece p : taken.taken()) {
            fibres.add(new Fibres(p.muscle(), rig, p.left()));
        }
        final List<Mesher.Mesh> surfaces = taken.taken().stream().map(p -> p.surface().mesh()).toList();
        final List<Integer> depths = taken.taken().stream().map(p -> p.muscle().depth()).toList();
        final SoftRegion near = new SoftRegion(region.lo().minus(new V3(1, 1, 1)), region.hi().plus(new V3(1, 1, 1)));
        final List<Mesher.Mesh> around = new ArrayList<>();
        for (final RegionPieces.Piece p : taken.held()) {
            final float[] at = p.surface().mesh().points();
            for (int i = 0; i < at.length; i += 3) {
                if (near.has(new V3(at[i], at[i + 1], at[i + 2]))) {
                    around.add(p.surface().mesh());
                    break;
                }
            }
        }
        final SoftSolve solve = new SoftSolve(region, lattices, surfaces, holds, fibres, depths, around, walls,
                settings);
        final String walled = String.format(Locale.ROOT, "walls found in %.1f s", seconds(began));
        began = System.nanoTime();
        SoftSolve.Moment m = null;
        while (!solve.done() && !stopped.getAsBoolean()) {
            m = solve.stepped();
            if (m.step() % SHOWN == 0 || solve.done()) {
                final String said = String.format(Locale.ROOT, "%s, layer %d deep: step %d of %d, rest shape %.0f%%"
                                + " of its size; %,d surface points inside another muscle, %,d too near bone, %,d"
                                + " pressing on the fascia; room %.0f%% of rest; fascia holds %.0f cm3. %.0f ms a step;"
                                + " %s.",
                        m.phase(), m.depth(), m.step(),
                        m.steps(), 100 * m.scale(), m.touching(), m.inBone(), m.pressed(), 100 * m.room(),
                        m.sleeve(), 1000 * seconds(began) / m.step(), walled);
                told.accept(new Told(pieces.found(region, taken, solve.lattices(), true,
                        solve.fascia().map(Fascia::sleeve)), said));
            }
        }
        if (m == null || !solve.done()) {
            return Optional.empty();
        }
        final String said = ended(region, taken, solve, m, seconds(began));
        told.accept(new Told(null, said));
        return Optional.of(new Ended(solve.lattices(), said, solve.fascia().map(Fascia::sleeve)));
    }

    /** How it ended: how long, how many elements turned inside out, how much the muscles share, before and after. */
    private String ended(final SoftRegion region, final RegionPieces.Taken taken, final SoftSolve solve,
                         final SoftSolve.Moment m, final double took) {
        final RegionPieces.Filled before = RegionPieces.INSTANCE.filled(region, taken.taken());
        final List<RegionPieces.Piece> moved = new ArrayList<>();
        for (int k = 0; k < taken.taken().size(); k++) {
            final RegionPieces.Piece p = taken.taken().get(k);
            final MuscleSurface.Surface s = p.surface();
            final float[] now = solve.lattices().get(k).carrying(s.mesh().points());
            moved.add(new RegionPieces.Piece(p.muscle(), p.left(), new MuscleSurface.Surface(
                    new Mesher.Mesh(now, s.mesh().triangles()), s.tendon(), s.parts(), s.shares(), s.depth())));
        }
        final RegionPieces.Filled after = RegionPieces.INSTANCE.filled(region, moved);
        final StringBuilder most = new StringBuilder();
        for (final RegionPieces.Shared s : RegionPieces.INSTANCE.pairs(region, moved, 8)) {
            most.append(String.format(Locale.ROOT, "%n  %.1f cm3 %s (%d deep) and %s (%d deep)", s.room(),
                    named(s.one()), s.one().muscle().depth(), named(s.other()), s.other().muscle().depth()));
        }
        return String.format(Locale.ROOT, "Solved in %.1f s, %d steps; %,d elements turned inside out; room %.0f%% of"
                        + " rest. Two muscles or more fill %.0f cm3 of the region (%.0f%% of what they fill), %.0f cm3"
                        + " before (%.0f%%). Most shared:%s", took, m.step(), solve.inverted(), 100 * m.room(),
                after.shared(), 100 * after.shared() / Math.max(1e-9, after.together()), before.shared(),
                100 * before.shared() / Math.max(1e-9, before.together()), most);
    }

    /** {@code p}'s name, and its side. */
    private String named(final RegionPieces.Piece p) {
        return p.muscle().name() + (p.muscle().paired() ? p.left() ? ", left" : ", right" : "");
    }

    private double seconds(final long since) {
        return (System.nanoTime() - since) / 1e9;
    }
}
