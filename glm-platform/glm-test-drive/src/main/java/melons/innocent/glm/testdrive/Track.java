package melons.innocent.glm.testdrive;

import com.example.fxsuite.avatar.sim.Place;
import melons.innocent.testkit.TestKit;

/** A test track: the test kit it is laid on, and where a car starts on it. */
public interface Track {

    /** The test kit it is laid on, called {@code title}. */
    TestKit kit(String title);

    /** Where a car riding {@code ride} high starts on it, and which way it heads. */
    Place start(double ride);
}
