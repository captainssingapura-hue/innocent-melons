package com.example.fxsuite.vulkan.shade;

import java.util.ArrayList;
import java.util.List;

/**
 * A thing drawn as where it is and nothing more: the one shader that places each point of a mesh, with none to
 * colour it. What draws the sun's own picture of a frame, which keeps only how near each thing is; every mesh is
 * drawn into it by this, whatever it looks like, since it is laid out and told as every look is.
 */
public record Outlined(Laid laid) {

    private static final String VERSION = "#version 450";
    private static final String FRAME = "frame";
    private static final String DRAW = "draw";

    public Outlined {
        // asked now: the shader's own line names these, so a layout without one is refused here
        laid.placing().forEach(laid.point()::field);
        laid.frame().field("seen");
        laid.draw().field("placed");
    }

    /** The shader run for each point of the mesh. */
    public Shader vertex() {
        final List<String> lines = new ArrayList<>(List.of(VERSION));
        lines.addAll(new Declared(laid.point()).taken(laid.placing()));
        lines.addAll(new Declared(laid.frame()).block(laid.frameBound(), FRAME));
        lines.addAll(new Declared(laid.draw()).pushed(DRAW));
        lines.addAll(List.of(
                "void main() {",
                "    gl_Position = frame.seen * (draw.placed * vec4(point, 1.0));",
                "}"));
        return new Shader(Stage.VERTEX, String.join("\n", lines) + "\n");
    }
}
