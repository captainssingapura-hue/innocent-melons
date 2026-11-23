package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.bench.Walk;

import java.io.IOException;

/**
 * The walk into the forest, opened: four woods with no leaves, an oak wood, a walnut wood, a birch wood and one of
 * all three mixed, ahead of the avatar across rides it can walk down or fly over, each tree drawn by the card as
 * finely as its distance calls for ({@link ForestPlace}). The walk, its keys and its mouse are the renderer's
 * bench's ({@code Walk}).
 */
public final class ForestWalk {

    public static void main(final String[] args) throws IOException {
        new Walk("A forest in winter", ForestPlace::new).run();
    }
}
