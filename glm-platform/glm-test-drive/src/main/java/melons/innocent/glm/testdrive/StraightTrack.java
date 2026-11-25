package melons.innocent.glm.testdrive;

import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.texture.materials.Grounds;
import melons.innocent.testkit.BaseMap;
import melons.innocent.testkit.Outline;
import melons.innocent.testkit.TestKit;

/**
 * The straight: a flat strip of asphalt 2 km long and 12 m wide on the test kit's turf, its middle the kit's ({@link
 * Strip}), for running the car up through its gears to its top speed and stopping it again. The car starts in the
 * right hand lane 20 m short of the start line at its near end, heading along it.
 */
public record StraightTrack() implements Track {

    /** How far the turf reaches from the middle, in metres: past the strip's ends. */
    private static final double TURF = 1_200;
    /** Where the car starts: across, in its lane, and how far short of the start line. */
    private static final double LANE = 175;
    private static final double SHORT = 2_000;

    @Override
    public TestKit kit(final String title) {
        return new TestKit(title, new BaseMap(Outline.CIRCLE, TURF, Grounds.TURF)).with(Strip::new);
    }

    @Override
    public Place start(final double ride) {
        return new Place(LANE, new Strip.Lines().start() - SHORT, ride, 0);
    }
}
