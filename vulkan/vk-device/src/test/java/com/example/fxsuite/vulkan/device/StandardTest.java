package com.example.fxsuite.vulkan.device;

import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.shade.Shader;
import com.example.fxsuite.vulkan.shade.Stage;
import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Scatter;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Term;
import com.example.fxsuite.vulkan.term.Turns;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The shaders Java writes, put through the standard compiler, with no card: a look's two shaders compile, with a
 * stripe and with something scattered in them; and a shader that is not right is refused with what the compiler
 * said of it.
 */
class StandardTest {

    /** The first word of anything compiled for the card. */
    private static final int MAGIC = 0x07230203;

    private final Seen seen = new Seen();
    private final Standard standard = new Standard();

    @Test
    void aLooksTwoShadersCompileWithAStripeAndWithSomethingScattered() {
        final Turns stripes = new Turns(seen.at().across().times(0.5), new Real(0.5));
        final Scatter grains = new Scatter(seen.at().across().floored(), seen.at().up().floored(), 1, 1);
        final Look look = new Look(new Laid(), new Surface(new Colour(0.2, 0.4, 0.2), List.of(
                new Term("stripes", new Colour(0.5, 0.8, 0.4), stripes.pulse(0.3, seen.covers()), 1),
                new Term("grains", new Colour(0, 0, 0), grains.some(0.2, seen.covers()), 1)),
                new Colour(0.2, 0.2, 0.2), 16));
        for (final Shader shader : List.of(look.vertex(), look.fragment())) {
            final ByteBuffer words = standard.compiled(shader);
            assertEquals(MAGIC, words.getInt(0), shader.stage() + " begins as compiled shaders do");
            assertEquals(0, words.remaining() % Integer.BYTES, "whole words");
        }
    }

    @Test
    void aShaderThatIsNotRightIsRefusedWithWhatTheCompilerSaidOfIt() {
        final Shader wrong = new Shader(Stage.FRAGMENT, "#version 450\nvoid main() {\n    nothing = 1.0;\n}\n");
        final Refused refused = assertThrows(Refused.class, () -> standard.compiled(wrong));
        assertTrue(refused.getMessage().contains("nothing"), refused.getMessage());
    }
}
