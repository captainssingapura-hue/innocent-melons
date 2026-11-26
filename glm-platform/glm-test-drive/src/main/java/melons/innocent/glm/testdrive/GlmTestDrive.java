package melons.innocent.glm.testdrive;

import java.io.IOException;

/**
 * The GLM car test driven on a test track: the 2 km straight ({@link StraightTrack}), or with {@code
 * -Dglm.track=ramp} the ramp, viaduct and spiral ({@link RampTrack}). The bare vehicle, its engine in its bay and
 * covered, its front wheels steering and every wheel turning, its engine heard but in a run that ends by itself, is
 * filmed or has its time set, driven from the keyboard by the avatar engine's own land vehicle, its test engine
 * ({@link TestDriving}), seen in the views a racing game has, in the walks' windows, with their sky, shadows, kept runs,
 * scripts and films. The car as the run asks for it ({@link GlmDrive}): {@code -Dglm.car=santana} the retro saloon on
 * its own steel shell, the large SUV unless; {@code -Dglm.engine=}, {@code -Dbench.hidden=} and {@code -Dglm.view=}
 * as it says.
 */
public final class GlmTestDrive {

    private static final String TITLE = "The GLM car, test driven";

    public static void main(final String[] args) throws IOException {
        new GlmTestDrive().run();
    }

    private void run() throws IOException {
        final GlmDrive car = new GlmDrive();
        final Track track = "ramp".equalsIgnoreCase(System.getProperty("glm.track", "straight")) ? new RampTrack()
                : new StraightTrack();
        track.kit(TITLE).walk(car.roamer(track.start(car.chassis().ride()))).run();
    }
}
