package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.bench.Afoot;
import com.example.fxsuite.vulkan.bench.Walk;

import java.io.IOException;

/**
 * A walk in a forest without end, made of a set of samples: {@code -Dplants.main=EndlessForestWalk}. How many
 * samples, how big, how far each level reaches and how far shadows are cast are the run's to say
 * ({@link EndlessForestPlace}). The avatar's jets take it no higher than ten metres over the ground, under the
 * trees' crowns, unless {@code -Dbench.ceiling=} says otherwise: unless the run says other reaches, the forest is
 * drawn for an eye among its trunks and cut off three hundred metres out, which shows from higher up.
 */
public final class EndlessForestWalk {

    public static void main(final String[] args) throws IOException {
        new Walk("A forest without end, in winter", EndlessForestPlace::new, new Afoot(
                System.getProperty("bench.at", ""), Double.parseDouble(System.getProperty("bench.ceiling", "10"))))
                .run();
    }
}
