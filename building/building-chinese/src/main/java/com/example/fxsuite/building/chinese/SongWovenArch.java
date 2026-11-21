package com.example.fxsuite.building.chinese;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.avatar.sim.Extrusion;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.avatar.sim.Volume;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.ThingImpl;
import com.example.fxsuite.building.Timbers;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.existence.Spaces;
import com.example.fxsuite.existence.Uses;
import com.example.fxsuite.historian.Chinese;
import com.example.fxsuite.historian.ChineseEras;
import com.example.fxsuite.taxonomy.Leaf;

import java.util.ArrayList;
import java.util.List;

/**
 * How the Northern Song built a bridge with no pier in the river, "其橋無柱，皆以巨木虛架" (东京梦华录): a woven timber arch
 * ({@link WovenArch}), as the scroll draws it and its historians read it. Across x, along z from bank to bank.
 *
 * <ul>
 * <li><b>Its arch</b>, two systems of squared logs woven: groups of four members, two long from the banks and two
 * short to the crown, and between them groups of three, two from the banks and one level across the top; tied by five
 * logs across the whole width where the systems meet. Its feet on stone end walls (金刚墙).
 * <li><b>Its deck</b>, boards across the arch, rising from the street at each end to its crown; walked up and over, the
 * river's passage open under the arch.
 * <li><b>Its railings</b>, posts with a sill, a middle rail and a top rail between each two, the end posts taller;
 * <b>four poles</b>, two at each end, a cross-bar, a disc and a crane at the top of each.
 * </ul>
 * Estimates, nothing published saying: its railings 1 m high, its deck's boards 15 cm thick.
 */
public final class SongWovenArch
        implements ThingImpl<Buildings.Bridge, Chinese, ChineseEras.NorthernSong, WovenArch> {

    private static final Timbers TIMBERS = Timbers.INSTANCE;

    /** Its deck's boards so thick, each so long along; its railings so high. */
    static final double BOARD = 15;
    static final double PLANK = 50;
    static final double RAIL = 100;
    /** Where the systems meet, as shares of half its span: the three-member system's joints, the four-member's. */
    static final double THREE = 0.56;
    static final double FOUR = 0.30;

    @Override
    public Buildings.Bridge thing() {
        return new Buildings.Bridge();
    }

    @Override
    public Chinese civilization() {
        return Chinese.IT;
    }

    @Override
    public ChineseEras.NorthernSong era() {
        return new ChineseEras.NorthernSong();
    }

    /** {@code s}'s deck's top at {@code z} along: rising from the street at its landing each way to its crown. */
    public double deck(final WovenArch s, final double z) {
        final double t = Math.abs(z) / s.landing();
        return t >= 1 ? 0 : s.crown() * (1 - t * t);
    }

    /** Where its arch's members are under the deck at {@code z}: their middles, so the deck's boards lie on them. */
    private double under(final WovenArch s, final double z) {
        return deck(s, z) - BOARD - s.log() / 2;
    }

    @Override
    public Structure build(final WovenArch s) {
        final List<Part> parts = new ArrayList<>();
        final double half = s.width() / 2;
        final double foot = s.span() / 2;
        final double three = THREE * foot;
        final double four = FOUR * foot;
        final Leaf<?> rib = new Elements.Rib();
        // the arch: the four-member groups on the even places across, the outermost among them
        for (int g = 0; g < s.groups(); g++) {
            final double x = (g - (s.groups() - 1) / 2.0) * s.pitch();
            if (g % 2 == 0) {
                for (final int side : new int[] {-1, 1}) {
                    parts.add(new Part(rib, TIMBERS.member("arch four long", Role.STRUCTURE, Material.TIMBER, x,
                            side * foot, s.springing(), side * four, under(s, four), s.log())));
                    parts.add(new Part(rib, TIMBERS.member("arch four short", Role.STRUCTURE, Material.TIMBER, x,
                            side * four, under(s, four), 0, under(s, 0), s.log())));
                }
            } else {
                for (final int side : new int[] {-1, 1}) {
                    parts.add(new Part(rib, TIMBERS.member("arch three side", Role.STRUCTURE, Material.TIMBER, x,
                            side * foot, s.springing(), side * three, under(s, three), s.log())));
                }
                parts.add(new Part(rib, TIMBERS.member("arch three top", Role.STRUCTURE, Material.TIMBER, x, -three,
                        under(s, three), three, under(s, three), s.log())));
            }
        }
        // the five logs across where the systems meet
        final double across = s.groups() * s.pitch() / 2;
        for (final double z : new double[] {-three, -four, 0, four, three}) {
            final double y = under(s, z) - s.log();
            parts.add(new Part(new Elements.Beam(), TIMBERS.block("arch across", Role.STRUCTURE, Material.TIMBER,
                    -across, across, z - s.log() / 2, z + s.log() / 2, y - s.log() / 2, y + s.log() / 2)));
        }
        boards(s, parts);
        ends(s, parts);
        railings(s, parts);
        poles(s, parts);
        final double end = s.landing();
        return new Structure(parts, shape(s, parts),
                List.of(new Structure.Room(new Spaces.Deck(), new Uses.Crossing(), List.of(new Vec2(-half, -end),
                        new Vec2(half, -end), new Vec2(half, end), new Vec2(-half, end)), 0)),
                List.of(new Structure.WayIn(0, -end, 0), new Structure.WayIn(0, end, 0)));
    }

    /** Its railings along both edges: posts; a sill, a middle rail and a top rail between each two; tall end posts. */
    private void railings(final WovenArch s, final List<Part> parts) {
        final Leaf<?> railing = new Elements.Railing();
        for (final int side : new int[] {-1, 1}) {
            final double x = side * (s.width() / 2 - 8);
            for (int p = 0; p < s.posts(); p++) {
                final double z = -s.landing() + p * 2 * s.landing() / (s.posts() - 1);
                final boolean end = p == 0 || p == s.posts() - 1;
                parts.add(new Part(railing, TIMBERS.block(end ? "end post" : "post", Role.FRAME, Material.TIMBER,
                        x - 8, x + 8, z - 8, z + 8, deck(s, z), deck(s, z) + (end ? RAIL + 50 : RAIL))));
                if (p + 1 < s.posts()) {
                    final double z1 = -s.landing() + (p + 1) * 2 * s.landing() / (s.posts() - 1);
                    for (final double at : new double[] {8, RAIL / 2, RAIL - 6}) {
                        parts.add(new Part(railing, TIMBERS.board("rail", Role.FRAME, Material.TIMBER, x - 5, x + 5,
                                z + 8, z1 - 8, deck(s, z + 8) + at, deck(s, z1 - 8) + at, 8)));
                    }
                }
            }
        }
    }

    /** Its four poles, two at each end beside it: a pole, a cross-bar, a disc on it, a crane on the disc. */
    private void poles(final WovenArch s, final List<Part> parts) {
        if (s.poles() <= 0) {
            return;
        }
        final Leaf<?> pole = new Elements.Pole();
        final Leaf<?> finial = new Elements.Finial();
        final double top = s.poles();
        for (final int end : new int[] {-1, 1}) {
            for (final int side : new int[] {-1, 1}) {
                final double x = side * (s.width() / 2 + 200);
                final double z = end * (s.landing() + 150);
                parts.add(new Part(pole, TIMBERS.block("pole", Role.DRESSING, Material.TIMBER, x - 12, x + 12, z - 12,
                        z + 12, 0, top)));
                parts.add(new Part(pole, TIMBERS.block("pole bar", Role.DRESSING, Material.TIMBER, x - 80, x + 80,
                        z - 5, z + 5, top - 60, top - 50)));
                parts.add(new Part(pole, TIMBERS.block("pole bar", Role.DRESSING, Material.TIMBER, x - 5, x + 5,
                        z - 80, z + 80, top - 60, top - 50)));
                parts.add(new Part(finial, TIMBERS.block("pole disc", Role.DRESSING, Material.TIMBER, x - 30, x + 30,
                        z - 30, z + 30, top, top + 6)));
                parts.add(new Part(finial, TIMBERS.block("pole crane", Role.DRESSING, Material.PAINTED, x - 8, x + 8,
                        z - 25, z + 25, top + 6, top + 40)));
            }
        }
    }

    /**
     * What it is in the way: its deck and arch one thing, its side a profile along it carried across its width (an
     * {@link Extrusion}), walked up and over as a ramp is, the river's passage under the arch open through it; its
     * railings and poles each what it covers seen from above ({@link StandIns#prisms}).
     */
    private List<Volume> shape(final WovenArch s, final List<Part> parts) {
        final double foot = s.span() / 2;
        final List<Vec2> side = new ArrayList<>();
        for (double z = -s.landing(); z <= s.landing() + 1; z += PLANK) {
            side.add(new Vec2(-z, deck(s, z)));
        }
        side.add(new Vec2(-s.landing(), -s.bed() - 10));
        side.add(new Vec2(s.landing(), -s.bed() - 10));
        // under the arch: from the bed up the walls' faces to the long members' undersides, level under the crown
        final double low = under(s, FOUR * foot) - s.log();
        final List<Vec2> passage = List.of(new Vec2(-(foot - 20), -s.bed() - 5), new Vec2(foot - 20, -s.bed() - 5),
                new Vec2(foot - 20, s.springing() - s.log()), new Vec2(FOUR * foot, low), new Vec2(-FOUR * foot, low),
                new Vec2(-(foot - 20), s.springing() - s.log()));
        final List<Volume> shape = new ArrayList<>(List.of(new Extrusion(0, 0, 90, s.width() / 2, side,
                List.of(passage))));
        final List<Solid> upright = parts.stream().filter(p -> p.element() instanceof Elements.Railing
                || p.element() instanceof Elements.Pole || p.element() instanceof Elements.Finial).map(Part::solid)
                .toList();
        shape.addAll(StandIns.prisms(upright));
        return shape;
    }

    /** Its deck: boards across, on the slope. */
    private void boards(final WovenArch s, final List<Part> parts) {
        final double half = s.width() / 2;
        final int boards = (int) Math.round(2 * s.landing() / PLANK);
        for (int b = 0; b < boards; b++) {
            final double z0 = -s.landing() + b * 2 * s.landing() / boards;
            final double z1 = -s.landing() + (b + 1) * 2 * s.landing() / boards;
            parts.add(new Part(new Elements.Decking(), TIMBERS.board("deck", Role.STRUCTURE, Material.TIMBER, -half,
                    half, z0, z1, deck(s, z0), deck(s, z1), BOARD)));
        }
    }

    /** Its end walls, stone, from the bed to the street. */
    private void ends(final WovenArch s, final List<Part> parts) {
        final double half = s.width() / 2;
        final double foot = s.span() / 2;
        for (final int side : new int[] {-1, 1}) {
            final double back = side * (foot + 150);
            parts.add(new Part(new Elements.Abutment(), TIMBERS.block("end wall", Role.BASE, Material.STONE,
                    -half - 80, half + 80, Math.min(side * foot, back), Math.max(side * foot, back), -s.bed(), 0)));
        }
    }
}
