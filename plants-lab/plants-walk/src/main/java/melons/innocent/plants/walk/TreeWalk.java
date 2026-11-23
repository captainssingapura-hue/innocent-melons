package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.bench.Walk;

import java.io.IOException;

/**
 * The walk round the trees, opened: the avatar walking up to an oak, a walnut and a white birch with no leaves and
 * round their trunks, or taking
 * off on its jets to look at its boughs from among them, drawn by the card ({@link TreePlace}). The walk, its
 * keys and its mouse are the renderer's bench's ({@code Walk}).
 */
public final class TreeWalk {

    public static void main(final String[] args) throws IOException {
        new Walk("Trees in winter", TreePlace::new).run();
    }
}
