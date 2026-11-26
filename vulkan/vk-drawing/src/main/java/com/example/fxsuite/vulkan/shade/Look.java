package com.example.fxsuite.vulkan.shade;

import com.example.fxsuite.vulkan.term.Averaged;
import com.example.fxsuite.vulkan.term.Node;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Writing;

import java.util.ArrayList;
import java.util.List;

/**
 * How a thing looks, whole: a surface, and the two shaders that draw it, written here from the layouts and the
 * surface's terms. The vertex shader places each point as the camera sees it and hands on what the fragment shader
 * reads; the fragment shader works the surface's colour out at each pixel and lights it, having looked up in the
 * sun's own picture of the frame how much of the sun reaches the pixel. Their declarations are made
 * from the layouts, and what one hands on is what the other takes, since both are made from the one layout.
 */
public record Look(Laid laid, Surface surface) {

    private static final String VERSION = "#version 450";
    private static final String FRAME = "frame";
    private static final String DRAW = "draw";
    private static final String DEPTHS = "depths";
    /** What a look that asks rays is written in: the version and the extension that have them. */
    private static final String RAYED = "#version 460\n#extension GL_EXT_ray_query : require";
    private static final String STANDING = "standing";

    public Look {
        // asked now: the vertex shader's own lines name these, so a layout without one is refused here
        laid.point().field("point");
        laid.point().field("facing");
        laid.point().field("on");
        laid.passed().field("at");
        laid.frame().field("seen");
        laid.draw().field("placed");
        laid.copy().field("copied");
        laid.draw().field("coarsened");
        laid.draw().field("tint");
        laid.frame().field("beam");
        laid.frame().field("eased");
        laid.frame().field("rays");
    }

    /** The light on the surface at a point, as an expression: what the fragment shader is written from. */
    public Node lit() {
        return lighting().of(surface).node().folded();
    }

    /**
     * How much of what is behind the surface it hides at a point, as an expression: one everywhere, unless the
     * surface is seen through.
     */
    public Node hides() {
        return lighting().hides(surface).node().folded();
    }

    /**
     * Whether what is drawn with this look is mixed with what is already drawn, not written over it: so for a
     * surface that is seen through. Such a thing is drawn after everything it may be seen in front of.
     */
    public boolean mixed() {
        return surface.seenThrough();
    }

    /** The lighting, for whoever works {@link #lit} out in Java and has to give it what it reads. */
    public Lit lighting() {
        return new Lit(laid.passed(), laid.frame(), FRAME);
    }

    /** The shader run for each point of the mesh. */
    public Shader vertex() {
        final List<String> lines = new ArrayList<>(List.of(VERSION));
        lines.addAll(new Declared(laid.point()).taken());
        lines.addAll(new Declared(laid.copy()).takenFrom(laid.copyPlace()));
        lines.addAll(new Declared(laid.frame()).block(laid.frameBound(), FRAME));
        lines.addAll(new Declared(laid.draw()).pushed(DRAW));
        lines.addAll(new Declared(laid.passed()).handedOn());
        lines.addAll(List.of(
                "void main() {",
                "    vec4 seenFrom = draw.placed * (copied * vec4(point, 1.0));",
                "    at = on;",
                "    faces = mat3(draw.placed) * (mat3(copied) * facing);",
                "    from = seenFrom.xyz;",
                "    gl_Position = frame.seen * seenFrom;",
                "}"));
        return new Shader(Stage.VERTEX, String.join("\n", lines) + "\n");
    }

    /** The shader run for each pixel the mesh covers. */
    public Shader fragment() {
        final Seen seen = new Seen();
        final Writing writing = new Writing();
        final String lit = writing.named(lit());
        final List<String> lines = new ArrayList<>(List.of(laid.rays() ? RAYED : VERSION));
        lines.addAll(new Declared(laid.passed()).handedIn());
        lines.addAll(new Declared(laid.frame()).block(laid.frameBound(), FRAME));
        lines.addAll(new Declared(laid.draw()).pushed(DRAW));
        lines.add("layout(set = 0, binding = " + laid.depthsBound() + ") uniform sampler2DShadow " + DEPTHS + ";");
        if (laid.rays()) {
            lines.add("layout(set = 0, binding = " + laid.standingBound() + ") uniform accelerationStructureEXT "
                    + STANDING + ";");
        }
        lines.add("layout(location = 0) out vec4 shown;");
        lines.addAll(writing.declared());
        lines.add("void main() {");
        lines.add("    float " + seen.coversName() + " = length(fwidth(" + seen.atName() + ")) * draw.coarsened;");
        // where the sun's own picture has the point, eased a little the way its surface faces and toward the sun
        lines.add("    vec4 inBeam = frame.beam * vec4(from + normalize(faces) * frame.eased.x, 1.0);");
        lines.add("    float " + lighting().sunlit() + " = texture(" + DEPTHS
                + ", vec3(inBeam.xy * 0.5 + 0.5, clamp(inBeam.z + frame.eased.y, 0.0, 1.0)));");
        lines.addAll(rayed());
        // laid over what is already drawn as far as it hides it: wholly, unless its surface is seen through
        final String hidden = mixed() ? "clamp(" + writing.named(hides()) + ", 0.0, 1.0)" : "1.0";
        writing.lines().forEach(line -> lines.add("    " + line));
        lines.add("    shown = vec4(clamp(" + lit + ", 0.0, 1.0), " + hidden + ");");
        lines.add("}");
        return new Shader(Stage.FRAGMENT, String.join("\n", lines) + "\n");
    }

    /**
     * The fragment shader's lines for a frame whose shadows are found by rays, where the look asks any: a frame
     * that says how far a ray reaches has the point's share of the sun found by one, sent from the point, eased a
     * little the way its surface faces, toward the sun. Anything standing along it within reach leaves the point
     * in shadow. A frame that says no reach leaves what the sun's picture said.
     */
    private List<String> rayed() {
        if (!laid.rays()) {
            return List.of();
        }
        final String sunlit = lighting().sunlit();
        return List.of(
                "    if (frame.rays.y > 0.0) {",
                "        rayQueryEXT asked;",
                "        rayQueryInitializeEXT(asked, " + STANDING + ", gl_RayFlagsTerminateOnFirstHitEXT"
                        + " | gl_RayFlagsOpaqueEXT, 0xFF, from + normalize(faces) * frame.rays.x, 0.0,"
                        + " -normalize(frame.sunTowards), frame.rays.y);",
                "        while (rayQueryProceedEXT(asked)) {",
                "        }",
                "        " + sunlit + " = rayQueryGetIntersectionTypeEXT(asked, true)"
                        + " == gl_RayQueryCommittedIntersectionNoneEXT ? 1.0 : 0.0;",
                "    }");
    }

    /**
     * The look for where a pixel covers {@code covers} centimetres of the thing or more: the same, with whatever of
     * its surface can no longer be seen worked out once by {@code averaged} and left out of its shader.
     */
    public Look coarser(final double covers, final Averaged averaged) {
        return new Look(laid, surface.coarser(covers, averaged));
    }
}
