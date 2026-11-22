package com.example.fxsuite.vulkan.shade;

import com.example.fxsuite.vulkan.form.Ellipsoid;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Averaged;
import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Flakes;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Sample;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Term;
import com.example.fxsuite.vulkan.term.Tint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
                + "    vec3 sunTowards;\n    vec3 sun;\n    vec3 ambient;\n    vec3 sky;\n    vec3 ground;\n"
                + "    mat4 beam;\n    vec2 eased;\n} frame;\n"));
        assertTrue(code.contains("layout(push_constant) uniform Draw {\n    mat4 placed;\n    float coarsened;\n"
                + "    vec3 tint;\n} draw;\n"));
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
                + "    vec3 tint;\n} draw;\n"), "told, as the vertex shader is, what each thing drawn is told");
        assertTrue(code.contains("frame.sunTowards"));
        assertTrue(code.contains("layout(set = 0, binding = 1) uniform sampler2DShadow depths;\n"),
                "the sun's own picture of the frame, beside the frame's block");
        assertTrue(code.contains("    float sunlit = texture(depths, vec3(inBeam.xy * 0.5 + 0.5, "),
                "how much of the sun reaches the pixel, looked up before it is lit");
        assertTrue(code.indexOf("float sunlit") < code.lastIndexOf("sunlit"), "and then read by the lighting");
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
    void aSurfaceThatTakesItsColourFromTheThingIsToldItWithEachThingDrawnAndIsWhiteUntilTold() {
        final Look painted = new Look(new Laid(), new Surface(new Tint().colour(), List.of(),
                new Colour("#000000"), 8));
        assertTrue(painted.fragment().code().contains("draw.tint"), "read from what each thing drawn is told");
        assertFalse(look.fragment().code().contains("draw.tint"), "and not read by a look that takes no colour");
        final Light even = new Light(new Vec3(0, -1, 0), new Vec3(0, 0, 0), new Vec3(1, 1, 1));
        final Sample told = painted.lighting().given(seen.sample(new Vec3(0, 10, 0), 0), new Vec3(0, 1, 0),
                new Vec3(0, -90, 100), even, new Vec3(0.2, 0.4, 0.6));
        assertEquals(0.2, painted.lit().worked(told)[0], 1e-9);
        assertEquals(0.4, painted.lit().worked(told)[1], 1e-9);
        assertEquals(0.6, painted.lit().worked(told)[2], 1e-9);
        final Sample untold = painted.lighting().given(seen.sample(new Vec3(0, 10, 0), 0), new Vec3(0, 1, 0),
                new Vec3(0, -90, 100), even);
        assertEquals(1, painted.lit().worked(untold)[2], 1e-9, "white, told no colour");
    }

    @Test
    void aSurfaceWithAClearSkinMirrorsLittleSeenSquareOnAndNearlyAllSeenAtAGlance() {
        // black, with no shine: all that leaves it is what it mirrors, of a sky and a ground both white
        final Light around = new Light(new Vec3(0, -1, 0), new Vec3(0, 0, 0), new Vec3(0, 0, 0), new Vec3(1, 1, 1),
                new Vec3(1, 1, 1));
        final Surface plain = new Surface(new Colour("#000000"), List.of(), new Colour("#000000"), 8)
                .mirroring(new Colour(0.1, 0.1, 0.1));
        final Look skinned = new Look(new Laid(), plain.glancing(new Colour(0.9, 0.9, 0.9)));
        final Look even = new Look(new Laid(), plain);
        final Vec3 up = new Vec3(0, 1, 0);
        // the eye straight above the point, then half way down, then all but level with it
        final double square = mirrored(skinned, up, new Vec3(0, -100, 0), around);
        final double half = mirrored(skinned, up, new Vec3(86.6, -50, 0), around);
        final double glance = mirrored(skinned, up, new Vec3(100, -1, 0), around);
        assertEquals(0.1, square, 1e-9, "square on: as its mirroring says");
        assertEquals(0.1 + 0.8 * Math.pow(0.5, 5), half, 1e-4, "half way round, hardly more");
        assertTrue(glance > 0.85 && glance < 0.9, "at a glance, nearly all: " + glance);
        assertEquals(0.1, mirrored(even, up, new Vec3(100, -1, 0), around), 1e-9, "never told, the same all round");
    }

    @Test
    void aPaintWithFlakesInItHasTheirWideShineBesidesAndIsDullerSeenEdgeOnAndOneWithNoneIsAsItWas() {
        final Surface plain = new Surface(new Colour("#ffffff"), List.of(), new Colour("#000000"), 8);
        final Look flaked = new Look(new Laid(),
                plain.flaked(new Flakes(new Colour(0.5, 0.5, 0.5), 2, 0.5, 0.08, 0.3)));
        final Look unflaked = new Look(new Laid(), plain);
        assertTrue(flaked.fragment().code().contains("scattered("), "the flakes are told apart in the shader");
        assertFalse(unflaked.fragment().code().contains("scattered("), "and a paint with none has nothing of them");
        final Vec3 up = new Vec3(0, 1, 0);
        // no sun: only the light on everything, so only the paint's own colour, duller the more nearly edge on
        final Light dull = new Light(new Vec3(0, -1, 0), new Vec3(0, 0, 0), new Vec3(1, 1, 1));
        assertEquals(1, far(flaked, up, new Vec3(0, -100, 0), dull), 1e-9, "square on, as it is");
        assertEquals(1 - 0.5 * 0.25, far(flaked, up, new Vec3(86.6, -50, 0), dull), 1e-4, "half way round");
        assertEquals(1, far(unflaked, up, new Vec3(86.6, -50, 0), dull), 1e-9, "with no flakes, the same all round");
        // the sun straight overhead and nothing else: the flakes' shine alone over what the sun lights of the paint
        final Light sunny = new Light(new Vec3(0, -1, 0), new Vec3(1, 1, 1), new Vec3(0, 0, 0));
        assertEquals(1 + 0.5, far(flaked, up, new Vec3(0, -100, 0), sunny), 1e-9, "where a mirror would show the sun");
        assertEquals((1 - 0.5 * 0.25) + 0.5 * 0.25, far(flaked, up, new Vec3(86.6, -50, 0), sunny), 1e-4,
                "half way round from there, a quarter of the shine: it is wide");
        assertEquals(1, far(unflaked, up, new Vec3(0, -100, 0), sunny), 1e-9, "with no flakes, no shine of theirs");
        assertThrows(IllegalArgumentException.class,
                () -> flaked.surface().above(plain, new Real(0.5)), "flakes are of a whole surface");
    }

    @Test
    void aSurfaceSeenThroughHidesWhatItDoesNotLetThroughAndLetsThroughLessTheMoreItMirrors() {
        final Surface solid = new Surface(new Colour("#ffffff"), List.of(), new Colour("#000000"), 8);
        final Look opaque = new Look(new Laid(), solid);
        final Look clear = new Look(new Laid(), solid.clear(0.75));
        final Look skinned = new Look(new Laid(), solid.mirroring(new Colour(0.1, 0.1, 0.1))
                .glancing(new Colour(0.9, 0.9, 0.9)).clear(0.75));
        assertTrue(clear.mixed() && skinned.mixed() && !opaque.mixed());
        assertTrue(opaque.fragment().code().contains(", 0.0, 1.0), 1.0);\n"), "a solid look hides all behind it");
        assertFalse(clear.fragment().code().contains(", 0.0, 1.0), 1.0);\n"), "one seen through says how much");
        final Vec3 up = new Vec3(0, 1, 0);
        assertEquals(1, hidden(opaque, up, new Vec3(0, -100, 0)), 1e-12);
        assertEquals(0.25, hidden(clear, up, new Vec3(0, -100, 0)), 1e-9, "what it does not let through");
        assertEquals(0.25, hidden(clear, up, new Vec3(100, -1, 0)), 1e-9, "mirroring nothing, the same at a glance");
        assertEquals(1 - 0.75 * 0.9, hidden(skinned, up, new Vec3(0, -100, 0)), 1e-9, "less what it mirrors");
        final double glance = hidden(skinned, up, new Vec3(100, -1, 0));
        assertTrue(glance > 0.85 && glance < 0.93, "at a glance it mirrors nearly all and shows little: " + glance);
        // lit evenly, with no sun: its own colour shows only as much as is not let through
        final Light even = new Light(new Vec3(0, -1, 0), new Vec3(0, 0, 0), new Vec3(1, 1, 1));
        assertEquals(0.25, far(clear, up, new Vec3(0, -100, 0), even), 1e-9);
        assertEquals(1, far(opaque, up, new Vec3(0, -100, 0), even), 1e-9);
        assertThrows(IllegalArgumentException.class, () -> solid.clear(0.5).above(solid, new Real(0.5)));
        assertThrows(IllegalArgumentException.class, () -> solid.clear(1.5));
    }

    /** How much of what is behind a point of {@code of} it hides, the point {@code from} away from the eye. */
    private double hidden(final Look of, final Vec3 faces, final Vec3 from) {
        final Light any = new Light(new Vec3(0, -1, 0), new Vec3(0, 0, 0), new Vec3(1, 1, 1));
        return of.hides().worked(of.lighting().given(seen.sample(new Vec3(0, 0, 0), 0), faces, from, any))[0];
    }

    /** How much red leaves a point of {@code of}, seen from so far that a pixel covers a great deal of it. */
    private double far(final Look of, final Vec3 faces, final Vec3 from, final Light light) {
        return of.lit().worked(of.lighting().given(seen.sample(new Vec3(0, 0, 0), 50), faces, from, light))[0];
    }

    /** How much red leaves a point of {@code of} that faces {@code faces}, the point {@code from} away from the eye. */
    private double mirrored(final Look of, final Vec3 faces, final Vec3 from, final Light light) {
        return of.lit().worked(of.lighting().given(seen.sample(new Vec3(0, 0, 0), 0), faces, from, light))[0];
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
