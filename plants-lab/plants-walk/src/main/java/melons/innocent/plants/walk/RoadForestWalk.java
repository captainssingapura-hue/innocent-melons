package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.bench.Afoot;
import com.example.fxsuite.vulkan.bench.Walk;

import java.io.IOException;

/**
 * A walk along a road through a forest without end: {@code -Dplants.main=RoadForestWalk}. The road wanders on for
 * ever ahead, another road every time, and can be gone back along only a little ({@link RoadForestPlace}). The
 * avatar starts on it, facing along it; its jets take it no higher than ten metres, under the trees' crowns, unless
 * {@code -Dbench.ceiling=} says otherwise. A run of it kept is of this road only and will not replay on another.
 */
public final class RoadForestWalk {

    public static void main(final String[] args) throws IOException {
        new Walk("A road through a forest without end", RoadForestPlace::new, new Afoot(
                System.getProperty("bench.at", ""), Double.parseDouble(System.getProperty("bench.ceiling", "10"))))
                .run();
    }
}
