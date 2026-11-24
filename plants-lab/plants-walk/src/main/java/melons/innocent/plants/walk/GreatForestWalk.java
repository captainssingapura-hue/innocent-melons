package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.bench.Walk;

import java.io.IOException;

/**
 * The walk in the great forest, opened: two kilometres of bare trees each way, the avatar at its middle, to walk
 * among and fly over ({@link GreatForestPlace}). The walk, its keys, its camera and its options are the renderer's
 * bench's ({@code Walk}).
 */
public final class GreatForestWalk {

    public static void main(final String[] args) throws IOException {
        new Walk("A great forest in winter", GreatForestPlace::new).run();
    }
}
