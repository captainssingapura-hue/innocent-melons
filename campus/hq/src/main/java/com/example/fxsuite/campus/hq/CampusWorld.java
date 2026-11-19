package com.example.fxsuite.campus.hq;

import com.example.fxsuite.avatar.bench.kit.PlayWorld;
import com.example.fxsuite.avatar.bench.kit.Spot;
import com.example.fxsuite.avatar.build.Village;
import com.example.fxsuite.avatar.look.RoadScenery;
import com.example.fxsuite.avatar.look.Scenery;
import com.example.fxsuite.avatar.sim.Course;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.avatar.sim.Vec3;
import com.example.fxsuite.avatar.sim.Volume;
import com.example.fxsuite.avatar.worlds.Meadow;

import java.util.ArrayList;
import java.util.List;

/**
 * The campus to play ({@link Campus}): the meadow as it lies, bare of its village's huts, its main office and its
 * departments standing east of the green, its road on past the well up to the church. The avatar
 * starts where the meadow's does, south of the river before the bridge, the campus beyond it; vehicles parked in a row
 * west of the road there, people standing in a row east of it.
 */
public record CampusWorld() implements PlayWorld {

    /** How far apart people stand in a row; the room between vehicles parked; how far either side of the start each row is. */
    static final double PEOPLE_APART = 150;
    static final double ROOM = 200;
    static final double ASIDE = 1_200;
    static final double AHEAD = 600;

    @Override
    public String title() {
        return "The campus";
    }

    @Override
    public String description() {
        return "The virtual headquarters on the meadow: its main office east of the green, its "
                + Department.ALL.size() + " departments round it, over the river from where you start; the road on"
                + " past the well up to the church.";
    }

    @Override
    public void lay(final Course.Layout world) {
        Campus.register();
        world.start(Village.start()).land(Meadow.land(Campus.HOUSED)).pieces(Campus.pieces())
                .rivers(Village.rivers()).ponds(Village.ponds()).ruled(false)
                .ways(Meadow.roads(Campus.HOUSED).roads().stream().flatMap(r -> r.pavements().stream())
                        .map(p -> (Volume) p).toList());
    }

    @Override
    public Scenery scenery() {
        return RoadScenery.of(Meadow.roads(Campus.HOUSED));
    }

    @Override
    public Spot start() {
        return new Spot("before the bridge", on(Village.start(), 0, 0, 0));
    }

    /** In a row west of the start, nose to tail with room between, facing the river. */
    @Override
    public List<Spot> parked(final List<Double> lengths) {
        final List<Spot> spots = new ArrayList<>();
        double at = 0;
        for (int k = 0; k < lengths.size(); k++) {
            if (k > 0) {
                at += (lengths.get(k - 1) + lengths.get(k)) / 2 + ROOM;
            }
            spots.add(new Spot("parked " + (k + 1), on(Village.start(), -ASIDE - at, AHEAD, 0)));
        }
        return spots;
    }

    /** In a row east of the start, facing the river. */
    @Override
    public List<Spot> standing(final int count) {
        final List<Spot> spots = new ArrayList<>();
        for (int k = 0; k < count; k++) {
            spots.add(new Spot("standing " + (k + 1), on(Village.start(), ASIDE + k * PEOPLE_APART, AHEAD, 0)));
        }
        return spots;
    }

    /** So far east and north of {@code from}, on the meadow's ground there, facing {@code yaw}. */
    private static Place on(final Vec3 from, final double east, final double north, final double yaw) {
        final double x = from.x() + east;
        final double z = from.z() + north;
        return new Place(x, z, Meadow.land(Campus.HOUSED).height(x, z), yaw);
    }
}
