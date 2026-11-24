package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.bench.Afoot;
import com.example.fxsuite.vulkan.bench.Walk;

import java.io.IOException;

/**
 * A walk in a forest without end, made of a set of samples: {@code -Dplants.main=EndlessForestWalk}. How many
 * samples, how big, how far each level reaches and how far shadows are cast are the run's to say
 * ({@link EndlessForestPlace}). The avatar's jets take it no higher than thirty metres over the ground, a little
 * over the trees' tops, unless {@code -Dbench.ceiling=} says otherwise: the forest is not yet fit to be seen from
 * high up.
 */
public final class EndlessForestWalk {

    public static void main(final String[] args) throws IOException {
        new Walk("A forest without end, in winter", EndlessForestPlace::new, new Afoot(
                System.getProperty("bench.at", ""), Double.parseDouble(System.getProperty("bench.ceiling", "30"))))
                .run();
    }
}
