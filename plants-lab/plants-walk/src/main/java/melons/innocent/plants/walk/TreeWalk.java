package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.bench.Walk;

import java.io.IOException;

/**
 * The walk round a tree, opened: the avatar walking up to an oak with no leaves and round its trunk, or taking
 * off on its jets to look at its boughs from among them, drawn by the card ({@link TreePlace}). The walk, its
 * keys and its mouse are the renderer's bench's ({@code Walk}).
 */
public final class TreeWalk {

    public static void main(final String[] args) throws IOException {
        new Walk("An oak in winter", TreePlace::new).run();
    }
}
