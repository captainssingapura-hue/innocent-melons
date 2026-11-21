package com.example.fxsuite.building.chinese;

import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.spec.ShopHouseSpec;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.existence.Spaces;
import com.example.fxsuite.existence.Uses;
import com.example.fxsuite.historian.Chinese;
import com.example.fxsuite.historian.ChineseEras;

import java.util.ArrayList;
import java.util.List;

/**
 * How the Northern Song built a shop on a street, as the Qingming scroll draws them by the dozen: of one storey, a
 * timber frame on a low platform under a folded gable roof ({@link SongFrame}); its back and its gables walled, its
 * front open to the street, a bay wide between each two columns, come in by.
 */
public final class SongShopHouse
        implements NorthernSongImpl<Buildings.ShopHouse, ShopHouseSpec<Chinese, ChineseEras.NorthernSong>> {

    @Override
    public Buildings.ShopHouse thing() {
        return new Buildings.ShopHouse();
    }

    @Override
    public Structure build(final ShopHouseSpec<Chinese, ChineseEras.NorthernSong> s) {
        final List<Part> parts = new ArrayList<>();
        SongFrame.platform(parts, s);
        SongFrame.storey(parts, s, SongFrame.PLATFORM, null);
        SongFrame.back(parts, s, SongFrame.PLATFORM);
        SongFrame.gables(parts, s, SongFrame.PLATFORM);
        SongFrame.pediments(parts, s, SongFrame.PLATFORM);
        SongFrame.roofing(parts, s, SongFrame.PLATFORM);
        return new Structure(parts, StandIns.prisms(parts.stream().map(Part::solid).toList()),
                List.of(new Structure.Room(new Spaces.ShopFloor(), new Uses.Shops(), SongFrame.floor(s),
                        SongFrame.PLATFORM)), List.of(SongFrame.wayIn(s)));
    }
}
