package com.example.fxsuite.building.chinese;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.Timbers;
import com.example.fxsuite.building.spec.StallSpec;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.existence.Spaces;
import com.example.fxsuite.existence.Uses;
import com.example.fxsuite.historian.Chinese;
import com.example.fxsuite.historian.ChineseEras;

import java.util.ArrayList;
import java.util.List;

/**
 * How the Northern Song put up a stall, as the Qingming scroll draws them at the bridge's ends and along the street: a
 * counter under a shade, put up for the day. Under an awning, a mat on four posts, higher at its front; or under a
 * great umbrella on one pole behind the counter, its canopy in two tiers. Its front toward +z.
 * Estimates, all its sizes: its counter 75 cm high, its awning 2.3 m at its front, its umbrella 2.5 m.
 */
public final class SongStall
        implements NorthernSongImpl<Buildings.Stall, StallSpec<Chinese, ChineseEras.NorthernSong>> {

    private static final Timbers TIMBERS = Timbers.INSTANCE;

    static final double COUNTER = 75;
    static final double FRONT = 230;
    static final double REAR = 190;
    static final double UMBRELLA = 250;

    @Override
    public Buildings.Stall thing() {
        return new Buildings.Stall();
    }

    @Override
    public Structure build(final StallSpec<Chinese, ChineseEras.NorthernSong> s) {
        final List<Part> parts = new ArrayList<>();
        final double a = s.wide() / 2;
        final double d = s.deep() / 2;
        parts.add(new Part(new Elements.Counter(), TIMBERS.block("counter", Role.DRESSING, Material.TIMBER, -a + 15,
                a - 15, -d / 2, d / 2, 0, COUNTER)));
        if (s.shade() == StallSpec.Shade.AWNING) {
            for (final int side : new int[] {-1, 1}) {
                parts.add(new Part(new Elements.Column(), TIMBERS.block("post", Role.STRUCTURE, Material.TIMBER,
                        side * a - 4, side * a + 4, d - 4, d + 4, 0, FRONT - 4)));
                parts.add(new Part(new Elements.Column(), TIMBERS.block("post", Role.STRUCTURE, Material.TIMBER,
                        side * a - 4, side * a + 4, -d - 4, -d + 4, 0, REAR - 4)));
            }
            parts.add(new Part(new Elements.Canopy(), TIMBERS.board("awning", Role.ROOF, Material.REED, -a - 20,
                    a + 20, -d - 20, d + 20, REAR, FRONT, 4)));
        } else {
            final double reach = Math.max(a, d) + 30;
            parts.add(new Part(new Elements.Pole(), TIMBERS.block("pole", Role.STRUCTURE, Material.TIMBER, -4, 4,
                    -d - 4, -d + 4, 0, UMBRELLA + 22)));
            parts.add(new Part(new Elements.Canopy(), TIMBERS.block("umbrella", Role.ROOF, Material.PALE_CLAY, -reach,
                    reach, -d - reach, -d + reach, UMBRELLA, UMBRELLA + 8)));
            parts.add(new Part(new Elements.Canopy(), TIMBERS.block("umbrella top", Role.ROOF, Material.PALE_CLAY,
                    -reach * 0.55, reach * 0.55, -d - reach * 0.55, -d + reach * 0.55, UMBRELLA + 8, UMBRELLA + 18)));
        }
        return new Structure(parts, StandIns.prisms(parts.stream().map(Part::solid).toList()),
                List.of(new Structure.Room(new Spaces.ShopFloor(), new Uses.Market(), List.of(new Vec2(-a, -d),
                        new Vec2(a, -d), new Vec2(a, d), new Vec2(-a, d)), 0)),
                List.of(new Structure.WayIn(0, d, 0)));
    }
}
