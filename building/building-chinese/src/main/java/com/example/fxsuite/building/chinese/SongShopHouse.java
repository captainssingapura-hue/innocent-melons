package com.example.fxsuite.building.chinese;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.ThingImpl;
import com.example.fxsuite.building.Timbers;
import com.example.fxsuite.building.spec.Roofing;
import com.example.fxsuite.building.spec.ShopHouseSpec;
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
 * How the Northern Song built a shop on a street, as the Qingming scroll draws them by the dozen and the Yingzao Fashi
 * (营造法式, 1103) sets how a timber frame is made: of one storey, its front open to the street. Across x along the
 * street, along z, its front toward +z.
 *
 * <ul>
 * <li><b>Its frame</b>, of squared timber on a low platform: a row of columns along its front and one along its back,
 * a bay apart, no taller than a bay is wide (柱高不越间之广); a lintel (阑额) between their heads along each row; a
 * beam across from front to back over each pair (梁), carrying short posts (蜀柱) that carry the purlins (槫).
 * <li><b>Its roof</b>, an overhanging gable (悬山), pitched as the book's 举折 has it for such buildings: its ridge a
 * quarter of its span over its eaves, its slope not straight but folded, the purlin between eave and ridge a tenth of
 * that rise below the straight line, so it is gentle at the eaves and steeper to the ridge; tiled or thatched.
 * <li><b>Its walls</b>, plastered, along its back and up both gables to the roof; its front open, a bay wide between
 * each two columns, come in by.
 * </ul>
 * Estimates, the book giving proportions and the scroll no sizes: its columns 24 cm thick and 3.2 m at most, its
 * platform 18 cm high, its eaves 90 cm out and its gables' 50 cm.
 */
public final class SongShopHouse implements ThingImpl<Buildings.ShopHouse, Chinese, ChineseEras.NorthernSong,
        ShopHouseSpec<Chinese, ChineseEras.NorthernSong>> {

    /** Its platform: so high, so far out past its columns, so far into the ground. */
    static final double PLATFORM = 18;
    static final double APRON = 40;
    static final double FOOTING = 40;
    /** Its timbers: its columns so thick and so tall at most; its lintels and beams so deep; its purlins so thick. */
    static final double COLUMN = 24;
    static final double TALLEST = 320;
    static final double LINTEL = 24;
    static final double BEAM = 30;
    static final double PURLIN = 18;
    /** Its roof: so far out at its eaves and past its gables; so thick, tiled or thatched; its fold. */
    static final double EAVE = 90;
    static final double GABLE = 50;
    static final double TILES = 14;
    static final double THATCH = 30;
    static final double RISE = 0.25;
    static final double FOLD = 0.1;
    static final double WALL = 16;

    @Override
    public Buildings.ShopHouse thing() {
        return new Buildings.ShopHouse();
    }

    @Override
    public Chinese civilization() {
        return Chinese.IT;
    }

    @Override
    public ChineseEras.NorthernSong era() {
        return new ChineseEras.NorthernSong();
    }

    /** How tall {@code s}'s columns are: a bay's width, 3.2 m at most. */
    static double column(final ShopHouseSpec<?, ?> s) {
        return Math.min(s.bay(), TALLEST);
    }

    /** How high {@code s}'s ridge is over its eaves' edge: a quarter of its roof's span, eave to eave. */
    static double rise(final ShopHouseSpec<?, ?> s) {
        return RISE * (s.depth() + 2 * EAVE);
    }

    /**
     * {@code s}'s roof's top at {@code z} along: from its eave's edge up to the fold, half way in, a tenth of its rise
     * under the straight line, and on up to its ridge.
     */
    static double roof(final ShopHouseSpec<?, ?> s, final double z) {
        final double half = s.depth() / 2 + EAVE;
        final double rise = rise(s);
        final double slope = (0.5 - FOLD) * rise / (half / 2);
        // its eave's edge so high that its underside clears the beams where it passes over the columns
        final double eave = PLATFORM + column(s) + BEAM + thick(s) - slope * EAVE;
        final double in = Math.max(0, half - Math.abs(z));
        return in <= half / 2 ? eave + slope * in
                : eave + (0.5 - FOLD) * rise + (0.5 + FOLD) * rise * (in - half / 2) / (half / 2);
    }

    private static double thick(final ShopHouseSpec<?, ?> s) {
        return s.roofing() == Roofing.TILE ? TILES : THATCH;
    }

    @Override
    public Structure build(final ShopHouseSpec<Chinese, ChineseEras.NorthernSong> s) {
        final List<Part> parts = new ArrayList<>();
        final double a = s.width() / 2;
        final double d = s.depth() / 2;
        final double head = PLATFORM + column(s);
        final double over = head + BEAM;
        final double half = d + EAVE;
        final double thick = thick(s);
        final Material cover = s.roofing() == Roofing.TILE ? Material.GREY_TILE : Material.THATCH;
        final Leaf<?> column = new Elements.Column();
        final Leaf<?> beam = new Elements.Beam();
        parts.add(new Part(new Elements.Platform(), Timbers.block("platform", Role.BASE, Material.STONE, -a - APRON,
                a + APRON, -d - APRON, d + APRON, -FOOTING, PLATFORM)));
        // its frame: at each line across, a column front and back, a beam over them, posts on it to the purlins
        for (int i = 0; i <= s.bays(); i++) {
            final double x = -a + i * s.bay();
            for (final double z : new double[] {-d, d}) {
                parts.add(new Part(column, Timbers.block("column", Role.STRUCTURE, Material.TIMBER, x - COLUMN / 2,
                        x + COLUMN / 2, z - COLUMN / 2, z + COLUMN / 2, PLATFORM, head)));
            }
            parts.add(new Part(beam, Timbers.block("beam", Role.STRUCTURE, Material.TIMBER, x - 10, x + 10, -d, d,
                    head, over)));
            for (final double z : new double[] {-half / 2, 0, half / 2}) {
                final double top = roof(s, z) - thick - PURLIN;
                if (top - over > 2) {
                    parts.add(new Part(column, Timbers.block("post", Role.STRUCTURE, Material.TIMBER, x - 8, x + 8,
                            z - 8, z + 8, over, top)));
                }
            }
        }
        // a lintel between the columns' heads along each row; the purlins under the roof, gable to gable
        for (final double z : new double[] {-d, d}) {
            parts.add(new Part(beam, Timbers.block("lintel", Role.STRUCTURE, Material.TIMBER, -a, a, z - 9, z + 9,
                    head - LINTEL, head)));
        }
        for (final double z : new double[] {-half / 2, 0, half / 2}) {
            final double top = roof(s, z) - thick;
            parts.add(new Part(beam, Timbers.block("purlin", Role.STRUCTURE, Material.TIMBER, -a - GABLE, a + GABLE,
                    z - PURLIN / 2, z + PURLIN / 2, top - PURLIN, top)));
        }
        walls(s, parts);
        // its roof: each side a lower board and an upper, folded where they meet; its ridge along the top
        final double[] along = {-half, -half / 2, 0, half / 2, half};
        for (int k = 0; k + 1 < along.length; k++) {
            parts.add(new Part(new Elements.Roof(), Timbers.board("roof", Role.ROOF, cover, -a - GABLE, a + GABLE,
                    along[k], along[k + 1], roof(s, along[k]), roof(s, along[k + 1]), thick)));
        }
        parts.add(new Part(new Elements.Roof(), Timbers.block("ridge", Role.ROOF, cover, -a - GABLE, a + GABLE, -12,
                12, roof(s, 0) - 4, roof(s, 0) + 22)));
        // come in by the middle of its front, between two columns
        final double door = s.bays() % 2 == 0 ? s.bay() / 2 : 0;
        return new Structure(parts, StandIns.prisms(parts.stream().map(Part::solid).toList()),
                List.of(new Structure.Room(new Spaces.ShopFloor(), new Uses.Shops(), List.of(new Vec2(-a, -d),
                        new Vec2(a, -d), new Vec2(a, d), new Vec2(-a, d)), PLATFORM)),
                List.of(new Structure.WayIn(door, d, PLATFORM)));
    }

    /** Its walls, plastered: along its back under the lintel; up each gable, under the beam and over it to the roof. */
    private static void walls(final ShopHouseSpec<?, ?> s, final List<Part> parts) {
        final double a = s.width() / 2;
        final double d = s.depth() / 2;
        final double head = PLATFORM + column(s);
        final double over = head + BEAM;
        final double half = d + EAVE;
        final Leaf<?> wall = new Elements.Wall();
        parts.add(new Part(wall, Timbers.block("back wall", Role.WALL, Material.PALE_CLAY, -a, a, -d - WALL / 2,
                -d + WALL / 2, PLATFORM, head - LINTEL)));
        for (final double x : new double[] {-a, a}) {
            parts.add(new Part(wall, Timbers.block("gable wall", Role.WALL, Material.PALE_CLAY, x - WALL / 2,
                    x + WALL / 2, -d, d, PLATFORM, head)));
            final double[] along = {-d, -half / 2, 0, half / 2, d};
            for (int k = 0; k + 1 < along.length; k++) {
                parts.add(new Part(wall, Timbers.slab("gable", Role.WALL, Material.PALE_CLAY, x - WALL / 2,
                        x + WALL / 2, along[k], along[k + 1], over, over, roof(s, along[k]) - thick(s),
                        roof(s, along[k + 1]) - thick(s))));
            }
        }
    }
}
