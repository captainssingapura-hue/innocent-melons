package melons.innocent.demos.village;

import com.example.fxsuite.vulkan.bench.Walk;

import java.io.IOException;

/**
 * The village, opened: the avatar walking down its lane and in at its gateways, or taking off on its jets to look
 * at it from the sky, drawn by the card ({@link VillagePlace}). The walk, its keys and its mouse are the renderer's
 * bench's ({@code Walk}).
 */
public final class VillageWalk {

    public static void main(final String[] args) throws IOException {
        new Walk("A village of melon houses in walled yards", VillagePlace::new).run();
    }
}
