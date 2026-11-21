package com.example.fxsuite.vulkan.shade;

import com.example.fxsuite.vulkan.form.Ellipsoid;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Averaged;
import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Sample;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Term;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A look's two shaders written from its layouts and its surface: what one hands on the other takes; the lighting
 * the same in the shader's code and in Java's working; and, far enough off, a term worked out once over the thing
 * and gone from the code.
 */
class LookTest {

    private final Seen seen = new Seen();
    private final Mesh ball = new Ellipsoid(20, 20, 20).formed(32, 16);
    /** White over black on the ball's upper half, the edge softened over what a pixel covers. */
    private final Term upper = new Term("upper", new Colour("#ffffff"),
            seen.at().up().over(seen.covers().most(new Real(1e-4))).plus(new Real(0.5)).held(0, 1), 2);
    private final Look look = new Look(new Laid(), new Surface(new Colour("#000000"), List.of(upper),
            new Colour("#000000"), 32));

    @Test
    void theVertexShaderDeclaresWhatTheLayoutsSayAndPlacesEachPointAsTheCameraSeesIt() {
        final String code = look.vertex().code();
        assertEquals(Stage.VERTEX, look.vertex().stage());
        assertTrue(code.startsWith("#version 450\n"));
        assertTrue(code.contains("layout(location = 0) in vec3 point;\nlayout(location = 1) in vec3 facing;\n"));
        assertTrue(code.contains("layout(std140, set = 0, binding = 0) uniform Frame {\n    mat4 seen;\n"
                + "    vec3 sunTowards;\n    vec3 sun;\n    vec3 ambient;\n} frame;\n"));
        assertTrue(code.contains("layout(push_constant) uniform Draw {\n    mat4 placed;\n    float coarsened;\n"
                + "} draw;\n"));
        assertTrue(code.contains("gl_Position = frame.seen * seenFrom;"));
    }

    @Test
    void whatTheVertexShaderHandsOnIsWhatTheFragmentShaderTakes() {
        final List<String> handedOn = look.vertex().code().lines().filter(line -> line.contains(") out ")).toList();
        final List<String> taken = look.fragment().code().lines().filter(line -> line.contains(") in ")).toList();
        assertEquals(3, handedOn.size());
        assertEquals(handedOn.stream().map(line -> line.replace(") out ", ") in ")).toList(), taken);
    }

    @Test
    void theFragmentShaderReckonsWhatAPixelCoversAsFarOffAsToldThenWorksTheSurfaceOutAndLightsIt() {
        final String code = look.fragment().code();
        assertTrue(code.contains("    float covers = length(fwidth(at)) * draw.coarsened;\n"),
                "what a pixel covers, as many times over as the thing is to seem far off");
        assertTrue(code.contains("layout(push_constant) uniform Draw {\n    mat4 placed;\n    float coarsened;\n"
                + "} draw;\n"), "told, as the vertex shader is, what each thing drawn is told");
        assertTrue(code.contains("frame.sunTowards"));
        assertTrue(code.contains("at.y"), "the term reads where the point is");
        assertTrue(code.strip().endsWith("}"));
        assertTrue(code.contains("    shown = vec4(clamp("));
    }

    @Test
    void javaLightsASurfaceAsTheShaderIsWrittenTo() {
        final Light light = new Light(new Vec3(0, -1, 0), new Vec3(0.6, 0.6, 0.6), new Vec3(0.25, 0.25, 0.25));
        final Sample top = look.lighting().given(seen.sample(new Vec3(0, 10, 0), 0), new Vec3(0, 1, 0),
                new Vec3(0, -90, 100), light);
        final Sample side = look.lighting().given(seen.sample(new Vec3(10, 5, 0), 0), new Vec3(1, 0, 0),
                new Vec3(10, -95, 100), light);
        assertEquals(0.85, look.lit().worked(top)[0], 1e-9, "white, facing the sun: all of it and the ambient");
        assertEquals(0.25, look.lit().worked(side)[0], 1e-9, "white, edge on to the sun: the ambient alone");
    }

    @Test
    void farEnoughOffATermIsWorkedOutOnceOverTheThingAndGoneFromTheCode() {
        final Look far = look.coarser(100, new Averaged(ball));
        final String code = far.fragment().code();
        assertFalse(code.contains("at.y"), "the term no longer reads where the point is");
        assertTrue(look.coarser(1, new Averaged(ball)).fragment().code().contains("at.y"), "near, it still does");
        final Light light = new Light(new Vec3(0, -1, 0), new Vec3(0, 0, 0), new Vec3(1, 1, 1));
        final Sample anywhere = far.lighting().given(seen.sample(new Vec3(3, -7, 2), 5), new Vec3(0, 1, 0),
                new Vec3(0, 0, 50), light);
        assertEquals(0.5, far.lit().worked(anywhere)[0], 0.01, "half white, half black: grey everywhere");
    }
}
