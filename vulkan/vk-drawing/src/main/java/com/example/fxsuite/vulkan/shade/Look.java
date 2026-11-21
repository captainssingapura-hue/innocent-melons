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
 * reads; the fragment shader works the surface's colour out at each pixel and lights it. Their declarations are made
 * from the layouts, and what one hands on is what the other takes, since both are made from the one layout.
 */
public record Look(Laid laid, Surface surface) {

    private static final String VERSION = "#version 450";
    private static final String FRAME = "frame";
    private static final String DRAW = "draw";

    public Look {
        // asked now: the vertex shader's own lines name these, so a layout without one is refused here
        laid.point().field("point");
        laid.point().field("facing");
        laid.passed().field("at");
        laid.frame().field("seen");
        laid.draw().field("placed");
        laid.draw().field("coarsened");
    }

    /** The light on the surface at a point, as an expression: what the fragment shader is written from. */
    public Node lit() {
        return lighting().of(surface).node().folded();
    }

    /** The lighting, for whoever works {@link #lit} out in Java and has to give it what it reads. */
    public Lit lighting() {
        return new Lit(laid.passed(), laid.frame(), FRAME);
    }

    /** The shader run for each point of the mesh. */
    public Shader vertex() {
        final List<String> lines = new ArrayList<>(List.of(VERSION));
        lines.addAll(new Declared(laid.point()).taken());
        lines.addAll(new Declared(laid.frame()).block(0, FRAME));
        lines.addAll(new Declared(laid.draw()).pushed(DRAW));
        lines.addAll(new Declared(laid.passed()).handedOn());
        lines.addAll(List.of(
                "void main() {",
                "    vec4 seenFrom = draw.placed * vec4(point, 1.0);",
                "    at = point;",
                "    faces = mat3(draw.placed) * facing;",
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
        final List<String> lines = new ArrayList<>(List.of(VERSION));
        lines.addAll(new Declared(laid.passed()).handedIn());
        lines.addAll(new Declared(laid.frame()).block(0, FRAME));
        lines.addAll(new Declared(laid.draw()).pushed(DRAW));
        lines.add("layout(location = 0) out vec4 shown;");
        lines.addAll(writing.declared());
        lines.add("void main() {");
        lines.add("    float " + seen.coversName() + " = length(fwidth(" + seen.atName() + ")) * draw.coarsened;");
        writing.lines().forEach(line -> lines.add("    " + line));
        lines.add("    shown = vec4(clamp(" + lit + ", 0.0, 1.0), 1.0);");
        lines.add("}");
        return new Shader(Stage.FRAGMENT, String.join("\n", lines) + "\n");
    }

    /**
     * The look for where a pixel covers {@code covers} centimetres of the thing or more: the same, with whatever of
     * its surface can no longer be seen worked out once by {@code averaged} and left out of its shader.
     */
    public Look coarser(final double covers, final Averaged averaged) {
        return new Look(laid, surface.coarser(covers, averaged));
    }
}
