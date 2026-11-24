package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.bench.Walk;

import java.io.IOException;

/**
 * A walk in a forest without end, made of a set of samples: {@code -Dplants.main=EndlessForestWalk}. How many
 * samples, how big, how far each level reaches and how far shadows are cast are the run's to say
 * ({@link EndlessForestPlace}).
 */
public final class EndlessForestWalk {

    public static void main(final String[] args) throws IOException {
        new Walk("A forest without end, in winter", EndlessForestPlace::new).run();
    }
}
