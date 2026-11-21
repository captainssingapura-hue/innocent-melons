package com.example.fxsuite.building.chinese;

import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.spec.HallSpec;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.existence.Spaces;
import com.example.fxsuite.existence.Uses;
import com.example.fxsuite.historian.Chinese;
import com.example.fxsuite.historian.ChineseEras;

import java.util.ArrayList;
import java.util.List;

/**
 * How the Northern Song built a warehouse, as the Qingming scroll draws them by the river: a hall of one storey
 * ({@link SongFrame}) walled on all four sides, a doorway in the middle of its front, goods kept in it.
 */
public final class SongWarehouse implements NorthernSongImpl<Buildings.Warehouse,
        HallSpec<Buildings.Warehouse, Chinese, ChineseEras.NorthernSong>> {

    private static final SongFrame FRAME = SongFrame.INSTANCE;

    @Override
    public Buildings.Warehouse thing() {
        return new Buildings.Warehouse();
    }

    @Override
    public Structure build(final HallSpec<Buildings.Warehouse, Chinese, ChineseEras.NorthernSong> s) {
        final List<Part> parts = new ArrayList<>();
        final double floor = SongFrame.PLATFORM;
        FRAME.platform(parts, s);
        FRAME.storey(parts, s, floor, null);
        FRAME.back(parts, s, floor);
        FRAME.gables(parts, s, floor);
        FRAME.front(parts, s, floor);
        FRAME.pediments(parts, s, floor);
        FRAME.roofing(parts, s, floor);
        return new Structure(parts, StandIns.prisms(parts.stream().map(Part::solid).toList()),
                List.of(new Structure.Room(new Spaces.Hall(), new Uses.Storage(), FRAME.floor(s), floor)),
                List.of(FRAME.wayIn(s)));
    }
}
