package melons.innocent.glm.bench;

import com.example.fxsuite.vulkan.bench.Walk;

import java.io.IOException;

/**
 * The GLM platform walked round, as an avatar ({@link GlmPlace}): the walk, its keys, its mouse, its window and its
 * runs kept, played and filmed are the renderer's bench's ({@code Walk}). {@code -Dglm.engine=} the engine to begin
 * with, L4, L5, V6, V8 or V12, or none; the V6 unless.
 */
public final class GlmWalk {

    public static void main(final String[] args) throws IOException {
        new Walk("The GLM platform, walked round", GlmPlace::new).run();
    }
}
