package com.example.fxsuite.building.chinese;

import com.example.fxsuite.existence.Earthworks;
import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.sim.Extrusion;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.Timbers;
import com.example.fxsuite.building.spec.CityWallSpec;
import com.example.fxsuite.existence.Elements;
import com.example.fxsuite.existence.Spaces;
import com.example.fxsuite.existence.Uses;
import com.example.fxsuite.historian.Chinese;
import com.example.fxsuite.historian.ChineseEras;

import java.util.ArrayList;
import java.util.List;

/**
 * How the Northern Song built a city's wall: of rammed earth, as the Yingzao Fashi's rule for walls has it (筑城之制):
 * half as thick again at its foot as it is high, and narrower at its top than at its foot by half its height, so as
 * thick at its top as it is high, its faces battered. No battlements: the Qingming scroll draws its wall a bare
 * earthen bank. Its length across (x), its outer face toward +z; walked along its top.
 */
public final class SongCityWall
        implements NorthernSongImpl<Earthworks.CityWall, CityWallSpec<Chinese, ChineseEras.NorthernSong>> {

    private static final Timbers TIMBERS = Timbers.INSTANCE;

    /** How thick it is at its foot and at its top, for each of its height; a length rammed at a time; its footing. */
    static final double FOOT = 1.5;
    static final double TOP = 1.0;
    static final double LIFT = 1_000;
    static final double FOOTING = 40;

    @Override
    public Earthworks.CityWall thing() {
        return new Earthworks.CityWall();
    }

    @Override
    public Structure build(final CityWallSpec<Chinese, ChineseEras.NorthernSong> s) {
        final double half = s.length() / 2;
        final double foot = FOOT * s.high() / 2;
        final double top = TOP * s.high() / 2;
        final List<Part> parts = new ArrayList<>();
        final int lifts = (int) Math.ceil(s.length() / LIFT);
        for (int k = 0; k < lifts; k++) {
            parts.add(new Part(new Elements.Wall(), TIMBERS.battered("rampart", Role.WALL, Material.RAMMED_EARTH,
                    -half + k * s.length() / lifts, -half + (k + 1) * s.length() / lifts, foot, top, -FOOTING,
                    s.high())));
        }
        // in the way as its section carried along it: too steep to climb, stood on along its top
        final List<Vec2> section = List.of(new Vec2(-foot, -FOOTING), new Vec2(foot, -FOOTING), new Vec2(top, s.high()),
                new Vec2(-top, s.high()));
        return new Structure(parts, List.of(new Extrusion(0, 0, 90, half, section)),
                List.of(new Structure.Room(new Spaces.Terrace(), new Uses.Watch(), List.of(new Vec2(-half, -top),
                        new Vec2(half, -top), new Vec2(half, top), new Vec2(-half, top)), s.high())), List.of());
    }
}
