package com.example.fxsuite.building.chinese;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.Timbers;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.existence.Spaces;
import com.example.fxsuite.existence.Uses;
import com.example.fxsuite.taxonomy.Leaf;

import java.util.ArrayList;
import java.util.List;

/**
 * How the Northern Song put up a festive gateway (彩楼欢门, {@link FestiveGate}) before a wine house's door: a scaffold
 * of poles lashed together, two rows of four uprights, the middle pair of each row taller and a doorway's width apart;
 * rails across and between them from over head height up, a tier every 80 cm; a finial on every pole; a red board
 * across the front over the way through. Gone under, between the middle poles, from the street to the door.
 * Estimates, all its sizes, the scroll drawing it and nothing measuring it.
 */
public final class SongFestiveGate implements NorthernSongImpl<Buildings.FestiveGateway, FestiveGate> {

    /** The way through so wide, between the middle poles; the lowest rails so high; a tier so far above the last. */
    static final double WAY = 240;
    static final double LOWEST = 250;
    static final double TIER = 80;
    /** How much shorter the outer poles are than the middle ones. */
    static final double OUTER = 0.8;

    @Override
    public Buildings.FestiveGateway thing() {
        return new Buildings.FestiveGateway();
    }

    @Override
    public Structure build(final FestiveGate s) {
        final List<Part> parts = new ArrayList<>();
        final double a = s.wide() / 2;
        final double d = s.deep() / 2;
        final Leaf<?> pole = new Elements.Pole();
        final Leaf<?> beam = new Elements.Beam();
        final double[] across = {-a, -WAY / 2, WAY / 2, a};
        for (final double x : across) {
            final double top = Math.abs(x) < a ? s.high() : OUTER * s.high();
            for (final double z : new double[] {-d, d}) {
                parts.add(new Part(pole, Timbers.block("pole", Role.STRUCTURE, Material.TIMBER, x - 5, x + 5, z - 5,
                        z + 5, 0, top)));
                parts.add(new Part(new Elements.Finial(), Timbers.block("finial", Role.DRESSING, Material.LACQUER,
                        x - 3, x + 3, z - 3, z + 3, top, top + 50)));
            }
            // between the two rows, at every tier this pole reaches
            for (double y = LOWEST; y < top - 20; y += TIER) {
                parts.add(new Part(beam, Timbers.block("tie", Role.STRUCTURE, Material.TIMBER, x - 3, x + 3, -d, d,
                        y - 3, y + 3)));
            }
        }
        // along each row: across the whole width as high as the outer poles go, between the middle pair above that
        for (final double z : new double[] {-d, d}) {
            for (double y = LOWEST; y < s.high() - 20; y += TIER) {
                final double reach = y < OUTER * s.high() - 20 ? a : WAY / 2;
                parts.add(new Part(beam, Timbers.block("rail", Role.STRUCTURE, Material.TIMBER, -reach, reach, z - 3,
                        z + 3, y + 3, y + 9)));
            }
        }
        parts.add(new Part(new Elements.Sign(), Timbers.block("board", Role.DRESSING, Material.LACQUER, -WAY / 2,
                WAY / 2, d + 3, d + 7, LOWEST + 15, LOWEST + 65)));
        final List<Part> upright = parts.stream().filter(p -> p.element().equals(pole)).toList();
        return new Structure(parts, StandIns.prisms(upright.stream().map(Part::solid).toList()),
                List.of(new Structure.Room(new Spaces.Gateway(), new Uses.Passage(), List.of(new Vec2(-WAY / 2, -d),
                        new Vec2(WAY / 2, -d), new Vec2(WAY / 2, d), new Vec2(-WAY / 2, d)), 0)),
                List.of(new Structure.WayIn(0, d, 0), new Structure.WayIn(0, -d, 0)));
    }
}
