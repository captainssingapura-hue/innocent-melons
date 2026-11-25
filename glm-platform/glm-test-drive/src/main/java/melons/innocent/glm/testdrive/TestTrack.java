package melons.innocent.glm.testdrive;

import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;
import com.example.fxsuite.texture.materials.Grounds;
import melons.innocent.building.bench.LabLooks;
import melons.innocent.building.walk.BuildingPlace;
import melons.innocent.building.walk.WalkRound;
import melons.innocent.road.bridge.Carried;
import melons.innocent.road.bridge.ElevatedRoadSpec;
import melons.innocent.road.bridge.GenericElevatedRoad;
import melons.innocent.road.bridge.Piers;
import melons.innocent.road.surface.Course;
import melons.innocent.testkit.BaseMap;
import melons.innocent.testkit.TestKit;

/**
 * The test track, on the test kit: the bridges bench's elevated road stood on a broad round of gravel, a road of two
 * lanes up a straight ramp climbing 6% to 11 m, along a viaduct 120 m long on spans of 30 m, and down a spiral of a
 * turn and a half round a core at its far end, back to the ground; the gravel all round to drive back on to the ramp's
 * foot. The car starts on the ground 25 m short of the ramp's foot, heading up it.
 */
public record TestTrack() {

    /** How far short of the ramp's foot the car starts, and how far up the ramp it looks to find which way it runs. */
    private static final double SHORT = 2_500;
    private static final double AHEAD = 1_000;
    private static final ElevatedRoadSpec<Civilization, CommonEras.Contemporary> SPEC = new ElevatedRoadSpec<>(2, 350,
            100, 1_100, 0.06, 12_000, 3_000, Piers.SINGLE, 1_800, 1.5, Carried.CORE);

    /** The elevated road, as it is built. */
    public Structure road() {
        return new GenericElevatedRoad().build(SPEC);
    }

    /** The test kit, called {@code title}: its base map of gravel, the road stood on it, as the lab looks it. */
    public TestKit kit(final String title) {
        final Structure road = road();
        final LabLooks looks = new LabLooks();
        final BaseMap round = new WalkRound(title, road, looks::surface).map();
        return new TestKit(title, new BaseMap(round.outline(), round.metres(), Grounds.GRAVEL))
                .with((card, laid) -> new BuildingPlace(card, laid, road, looks::surface));
    }

    /** Where the car starts: on the ground short of the ramp's foot, heading up it, as high as it rides. */
    public Place start(final double ride) {
        final Course course = new GenericElevatedRoad().ramp(SPEC).course();
        final Point foot = course.at(0);
        final Point on = course.at(AHEAD);
        final double across = on.across() - foot.across();
        final double along = on.along() - foot.along();
        final double length = Math.hypot(across, along);
        return new Place(foot.across() - across / length * SHORT, foot.along() - along / length * SHORT, ride,
                Math.toDegrees(Math.atan2(across, along)));
    }
}
