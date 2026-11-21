package com.example.fxsuite.building.chinese;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.Timbers;
import com.example.fxsuite.building.spec.WineHouseSpec;
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
 * How the Northern Song built a wine house (酒楼, 脚店), as the Qingming scroll draws the one by the Rainbow Bridge: of
 * two storeys ({@link SongFrame}), a frame over a frame under one folded gable roof.
 *
 * <ul>
 * <li><b>Its hall</b>, on the ground: walled along its back and up its gables, open to the street along its front.
 * <li><b>Its rooms</b>, over the hall: a floor of boards on the hall's beams; walled along the back and up the gables,
 * a railing along the front, open over it, to drink at and look down on the street from.
 * <li><b>Its stair</b>, in the hall along its back wall, a straight flight up to the rooms through a well in their
 * floor, the hall's beams cut short over it; its steps no higher than a step is taken; the well railed off above.
 * </ul>
 * Estimates: its steps 26 cm deep, its stair 1.1 m wide, its floor's boards 8 cm thick, its railings 1 m high.
 */
public final class SongWineHouse
        implements NorthernSongImpl<Buildings.WineHouse, WineHouseSpec<Chinese, ChineseEras.NorthernSong>> {

    private static final SongFrame FRAME = SongFrame.INSTANCE;

    private static final Timbers TIMBERS = Timbers.INSTANCE;

    /** Its floor's boards so thick; a step so high at most and so deep; its stair so wide, so far from the back. */
    static final double BOARDS = 8;
    static final double RISER = 18;
    static final double TREAD = 26;
    static final double STAIR = 110;
    static final double FROM_BACK = 20;
    static final double RAIL = 100;

    @Override
    public Buildings.WineHouse thing() {
        return new Buildings.WineHouse();
    }

    /** How high {@code s}'s rooms' floor is over its hall's: its columns, its beams, its boards. */
    double storey(final WineHouseSpec<?, ?> s) {
        return FRAME.column(s) + SongFrame.BEAM + BOARDS;
    }

    /** How many steps its stair has: as few as are each no higher than {@link #RISER}. */
    int steps(final WineHouseSpec<?, ?> s) {
        return (int) Math.ceil(storey(s) / RISER);
    }

    @Override
    public Structure build(final WineHouseSpec<Chinese, ChineseEras.NorthernSong> s) {
        final double run = steps(s) * TREAD;
        if (s.width() < run + 300) {
            throw new IllegalArgumentException("a wine house wide enough for its stair: " + (run + 300) / 100 + " m");
        }
        final List<Part> parts = new ArrayList<>();
        final double a = s.width() / 2;
        final double d = s.depth() / 2;
        final double hall = SongFrame.PLATFORM;
        final double rooms = hall + storey(s);
        // the stair's well: from its foot to its head across, from the back wall forward
        final double foot = -run / 2;
        final double head = run / 2;
        final double near = -d + FROM_BACK;
        final double far = near + STAIR;
        FRAME.platform(parts, s);
        FRAME.storey(parts, s, hall, new double[] {foot, head, far});
        FRAME.back(parts, s, hall);
        FRAME.gables(parts, s, hall);
        stair(parts, s, foot, near, far, hall);
        // the rooms' floor, round the well; the well railed along its front and across its foot
        final Leaf<?> floor = new Elements.Floor();
        final double[][] boards = {{-a, foot, -d, d}, {head, a, -d, d}, {foot, head, far, d}, {foot, head, -d, near}};
        for (final double[] b : boards) {
            parts.add(new Part(floor, TIMBERS.block("floor", Role.STRUCTURE, Material.TIMBER, b[0], b[1], b[2], b[3],
                    rooms - BOARDS, rooms)));
        }
        final Leaf<?> railing = new Elements.Railing();
        parts.add(new Part(railing, TIMBERS.block("well rail", Role.FRAME, Material.TIMBER, foot - 6, head, far,
                far + 6, rooms, rooms + RAIL)));
        parts.add(new Part(railing, TIMBERS.block("well rail", Role.FRAME, Material.TIMBER, foot - 6, foot, near,
                far, rooms, rooms + RAIL)));
        FRAME.storey(parts, s, rooms, null);
        FRAME.back(parts, s, rooms);
        FRAME.gables(parts, s, rooms);
        FRAME.pediments(parts, s, rooms);
        FRAME.roofing(parts, s, rooms);
        front(parts, s, rooms);
        final double middle = (near + far) / 2;
        return new Structure(parts, StandIns.prisms(parts.stream().map(Part::solid).toList()),
                List.of(new Structure.Room(new Spaces.Hall(), new Uses.Restaurant(), FRAME.floor(s), hall),
                        new Structure.Room(new Spaces.Hall(), new Uses.Bar(), FRAME.floor(s), rooms)),
                List.of(FRAME.wayIn(s)),
                List.of(new Structure.Stair(new Structure.WayIn(foot, middle, hall),
                        new Structure.WayIn(head + 30, middle, rooms))));
    }

    /** Its stair, from {@code foot} across, between {@code near} and {@code far} along, up from the hall's floor. */
    private void stair(final List<Part> parts, final WineHouseSpec<?, ?> s, final double foot, final double near,
                       final double far, final double hall) {
        final double riser = storey(s) / steps(s);
        for (int j = 1; j <= steps(s); j++) {
            parts.add(new Part(new Elements.Steps(), TIMBERS.block("step", Role.STRUCTURE, Material.TIMBER,
                    foot + (j - 1) * TREAD, foot + j * TREAD, near, far, hall, hall + j * riser)));
        }
    }

    /** The rooms' front, their floor at {@code rooms}: a railing between its columns, a sill, a middle, a top rail. */
    private void front(final List<Part> parts, final WineHouseSpec<?, ?> s, final double rooms) {
        final double a = s.width() / 2;
        final double d = s.depth() / 2;
        for (final double at : new double[] {10, RAIL / 2, RAIL}) {
            parts.add(new Part(new Elements.Railing(), TIMBERS.block("front rail", Role.FRAME, Material.TIMBER, -a, a,
                    d - 5, d + 5, rooms + at - 8, rooms + at)));
        }
    }
}
