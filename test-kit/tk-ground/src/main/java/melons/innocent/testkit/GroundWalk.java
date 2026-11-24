package melons.innocent.testkit;

import com.example.fxsuite.places.yard.Homestead;

import java.io.IOException;

/**
 * The kit's own walk, which shows how a thing is tried by itself: the brick house stood on the base map the run
 * says, a circle reaching a kilometre from its middle unless said otherwise ({@code -Dtest.outline=},
 * {@code -Dtest.reach=}, {@code -Dtest.ground=}), and walked round.
 */
public final class GroundWalk {

    public static void main(final String[] args) throws IOException {
        new TestKit("The test ground, with the brick house on it", new BaseMap()).with(Homestead::new).walked();
    }
}
