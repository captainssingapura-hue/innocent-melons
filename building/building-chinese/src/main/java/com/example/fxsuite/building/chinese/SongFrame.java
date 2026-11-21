package com.example.fxsuite.building.chinese;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.Timbers;
import com.example.fxsuite.building.spec.Framed;
import com.example.fxsuite.building.spec.Roofing;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.taxonomy.Leaf;

import java.util.List;

/**
 * The timber frame the Northern Song built its ordinary buildings of, as the Yingzao Fashi (营造法式, 1103) sets it and
 * the Qingming scroll draws it: what its shophouses, tea houses, warehouses and wine houses share, each made of these
 * parts as it needs them. Across x along the front, along z, the front toward +z.
 *
 * <ul>
 * <li><b>A storey</b>: a row of columns along the front and one along the back, a bay apart, no taller than a bay is
 * wide (柱高不越间之广); a lintel (阑额) between their heads along each row; a beam (梁) across from front to back over
 * each pair.
 * <li><b>The roof</b>, an overhanging gable (悬山) on purlins (槫) carried by short posts (蜀柱) on the beams, pitched as
 * the book's 举折 has it: its ridge a quarter of its span over its eaves, its slope folded, the purlin between eave and
 * ridge a tenth of that rise below the straight line; tiled or thatched.
 * <li><b>Walls</b>, plastered, where a building has them: along its back, up its gables, across its front round a
 * doorway.
 * </ul>
 * Estimates, the book giving proportions and the scroll no sizes: columns 24 cm thick and 3.2 m at most, the platform
 * 18 cm high, the eaves 90 cm out and the gables' 50 cm.
 */
record SongFrame() implements StatelessFunctionalObject {

    private static final Timbers TIMBERS = Timbers.INSTANCE;

    static final SongFrame INSTANCE = new SongFrame();

    /** The platform: so high, so far out past the columns, so far into the ground. */
    static final double PLATFORM = 18;
    static final double APRON = 40;
    static final double FOOTING = 40;
    /** The timbers: columns so thick and so tall at most; lintels and beams so deep; purlins so thick. */
    static final double COLUMN = 24;
    static final double TALLEST = 320;
    static final double LINTEL = 24;
    static final double BEAM = 30;
    static final double PURLIN = 18;
    /** The roof: so far out at its eaves and past its gables; so thick, tiled or thatched; its rise and its fold. */
    static final double EAVE = 90;
    static final double GABLE = 50;
    static final double TILES = 14;
    static final double THATCH = 30;
    static final double RISE = 0.25;
    static final double FOLD = 0.1;
    /** A wall so thick; a doorway so wide and so high. */
    static final double WALL = 16;
    static final double DOORWAY = 140;
    static final double DOOR_HEAD = 210;

    /** How tall {@code s}'s columns are: a bay's width, 3.2 m at most. */
    double column(final Framed<?, ?, ?> s) {
        return Math.min(s.bay(), TALLEST);
    }

    /** How high {@code s}'s ridge is over its eaves' edge: a quarter of its roof's span, eave to eave. */
    double rise(final Framed<?, ?, ?> s) {
        return RISE * (s.depth() + 2 * EAVE);
    }

    double thick(final Framed<?, ?, ?> s) {
        return s.roofing() == Roofing.TILE ? TILES : THATCH;
    }

    /**
     * {@code s}'s roof's top at {@code z} along, over a storey whose floor is at {@code base}: from its eave's edge up
     * to the fold, half way in, a tenth of its rise under the straight line, and on up to its ridge.
     */
    double roof(final Framed<?, ?, ?> s, final double base, final double z) {
        final double half = s.depth() / 2 + EAVE;
        final double rise = rise(s);
        final double slope = (0.5 - FOLD) * rise / (half / 2);
        // its eave's edge so high that its underside clears the beams where it passes over the columns
        final double eave = base + column(s) + BEAM + thick(s) - slope * EAVE;
        final double in = Math.max(0, half - Math.abs(z));
        return in <= half / 2 ? eave + slope * in
                : eave + (0.5 - FOLD) * rise + (0.5 + FOLD) * rise * (in - half / 2) / (half / 2);
    }

    /** Where across the way in is: the middle of the front, between two columns. */
    double door(final Framed<?, ?, ?> s) {
        return s.bays() % 2 == 0 ? s.bay() / 2 : 0;
    }

    /** {@code s}'s floor's outline, column to column. */
    List<Vec2> floor(final Framed<?, ?, ?> s) {
        final double a = s.width() / 2;
        final double d = s.depth() / 2;
        return List.of(new Vec2(-a, -d), new Vec2(a, -d), new Vec2(a, d), new Vec2(-a, d));
    }

    /** The way in at the middle of {@code s}'s front, onto its platform. */
    Structure.WayIn wayIn(final Framed<?, ?, ?> s) {
        return new Structure.WayIn(door(s), s.depth() / 2, PLATFORM);
    }

    /** The platform, of stone. */
    void platform(final List<Part> parts, final Framed<?, ?, ?> s) {
        final double a = s.width() / 2;
        final double d = s.depth() / 2;
        parts.add(new Part(new Elements.Platform(), TIMBERS.block("platform", Role.BASE, Material.STONE, -a - APRON,
                a + APRON, -d - APRON, d + APRON, -FOOTING, PLATFORM)));
    }

    /**
     * A storey's frame, its floor at {@code base}: at each line across, a column front and back and a beam over them;
     * a lintel between the columns' heads along each row. Over a stair's {@code well} (from so far across, to so far,
     * as far forward as so far along; none, if null) the beams are cut short, to leave it clear.
     */
    void storey(final List<Part> parts, final Framed<?, ?, ?> s, final double base, final double[] well) {
        final double a = s.width() / 2;
        final double d = s.depth() / 2;
        final double head = base + column(s);
        final Leaf<?> column = new Elements.Column();
        final Leaf<?> beam = new Elements.Beam();
        for (int i = 0; i <= s.bays(); i++) {
            final double x = -a + i * s.bay();
            for (final double z : new double[] {-d, d}) {
                parts.add(new Part(column, TIMBERS.block("column", Role.STRUCTURE, Material.TIMBER, x - COLUMN / 2,
                        x + COLUMN / 2, z - COLUMN / 2, z + COLUMN / 2, base, head)));
            }
            final boolean cut = well != null && x > well[0] - 10 && x < well[1] + 10;
            parts.add(new Part(beam, TIMBERS.block("beam", Role.STRUCTURE, Material.TIMBER, x - 10, x + 10,
                    cut ? well[2] : -d, d, head, head + BEAM)));
        }
        for (final double z : new double[] {-d, d}) {
            parts.add(new Part(beam, TIMBERS.block("lintel", Role.STRUCTURE, Material.TIMBER, -a, a, z - 9, z + 9,
                    head - LINTEL, head)));
        }
    }

    /**
     * The roof over a storey whose floor is at {@code base}: posts on each beam to the purlins, the purlins gable to
     * gable, each side a lower board and an upper, folded where they meet, the ridge along the top.
     */
    void roofing(final List<Part> parts, final Framed<?, ?, ?> s, final double base) {
        final double a = s.width() / 2;
        final double half = s.depth() / 2 + EAVE;
        final double over = base + column(s) + BEAM;
        final double thick = thick(s);
        final Material cover = s.roofing() == Roofing.TILE ? Material.GREY_TILE : Material.THATCH;
        for (final double z : new double[] {-half / 2, 0, half / 2}) {
            final double top = roof(s, base, z) - thick;
            for (int i = 0; i <= s.bays() && top - PURLIN - over > 2; i++) {
                final double x = -a + i * s.bay();
                parts.add(new Part(new Elements.Column(), TIMBERS.block("post", Role.STRUCTURE, Material.TIMBER, x - 8,
                        x + 8, z - 8, z + 8, over, top - PURLIN)));
            }
            parts.add(new Part(new Elements.Beam(), TIMBERS.block("purlin", Role.STRUCTURE, Material.TIMBER,
                    -a - GABLE, a + GABLE, z - PURLIN / 2, z + PURLIN / 2, top - PURLIN, top)));
        }
        final double[] along = {-half, -half / 2, 0, half / 2, half};
        for (int k = 0; k + 1 < along.length; k++) {
            parts.add(new Part(new Elements.Roof(), TIMBERS.board("roof", Role.ROOF, cover, -a - GABLE, a + GABLE,
                    along[k], along[k + 1], roof(s, base, along[k]), roof(s, base, along[k + 1]), thick)));
        }
        parts.add(new Part(new Elements.Roof(), TIMBERS.block("ridge", Role.ROOF, cover, -a - GABLE, a + GABLE, -12,
                12, roof(s, base, 0) - 4, roof(s, base, 0) + 22)));
    }

    /** The wall along the back of a storey whose floor is at {@code base}, under its lintel. */
    void back(final List<Part> parts, final Framed<?, ?, ?> s, final double base) {
        final double a = s.width() / 2;
        final double d = s.depth() / 2;
        parts.add(new Part(new Elements.Wall(), TIMBERS.block("back wall", Role.WALL, Material.PALE_CLAY, -a, a,
                -d - WALL / 2, -d + WALL / 2, base, base + column(s) - LINTEL)));
    }

    /** The walls up both gables of a storey whose floor is at {@code base}, to its columns' heads. */
    void gables(final List<Part> parts, final Framed<?, ?, ?> s, final double base) {
        final double d = s.depth() / 2;
        for (final double x : new double[] {-s.width() / 2, s.width() / 2}) {
            parts.add(new Part(new Elements.Wall(), TIMBERS.block("gable wall", Role.WALL, Material.PALE_CLAY,
                    x - WALL / 2, x + WALL / 2, -d, d, base, base + column(s))));
        }
    }

    /** The gables' tops, over the beams to the roof, over a storey whose floor is at {@code base}. */
    void pediments(final List<Part> parts, final Framed<?, ?, ?> s, final double base) {
        final double d = s.depth() / 2;
        final double half = d + EAVE;
        final double over = base + column(s) + BEAM;
        final double[] along = {-d, -half / 2, 0, half / 2, d};
        for (final double x : new double[] {-s.width() / 2, s.width() / 2}) {
            for (int k = 0; k + 1 < along.length; k++) {
                parts.add(new Part(new Elements.Wall(), TIMBERS.slab("gable", Role.WALL, Material.PALE_CLAY,
                        x - WALL / 2, x + WALL / 2, along[k], along[k + 1], over, over,
                        roof(s, base, along[k]) - thick(s), roof(s, base, along[k + 1]) - thick(s))));
            }
        }
    }

    /** The wall across the front of a storey whose floor is at {@code base}, a doorway in it at {@link #door}. */
    void front(final List<Part> parts, final Framed<?, ?, ?> s, final double base) {
        final double a = s.width() / 2;
        final double d = s.depth() / 2;
        final double top = base + column(s) - LINTEL;
        final double door = door(s);
        final Leaf<?> wall = new Elements.Wall();
        parts.add(new Part(wall, TIMBERS.block("front wall", Role.WALL, Material.PALE_CLAY, -a, door - DOORWAY / 2,
                d - WALL / 2, d + WALL / 2, base, top)));
        parts.add(new Part(wall, TIMBERS.block("front wall", Role.WALL, Material.PALE_CLAY, door + DOORWAY / 2, a,
                d - WALL / 2, d + WALL / 2, base, top)));
        parts.add(new Part(wall, TIMBERS.block("over the door", Role.WALL, Material.PALE_CLAY, door - DOORWAY / 2,
                door + DOORWAY / 2, d - WALL / 2, d + WALL / 2, base + DOOR_HEAD, top)));
    }
}
