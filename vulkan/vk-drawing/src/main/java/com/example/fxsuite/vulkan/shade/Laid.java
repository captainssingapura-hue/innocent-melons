package com.example.fxsuite.vulkan.shade;

import com.example.fxsuite.vulkan.layout.Field;
import com.example.fxsuite.vulkan.layout.Kind;
import com.example.fxsuite.vulkan.layout.Layout;

import java.util.List;

/**
 * The layouts a look is laid out by, each described here once: what a point of a mesh brings, what the vertex shader
 * hands on to the fragment shader, what every frame is told, and what each thing drawn is told. The bytes Java
 * fills, the shaders' declarations and what the pipeline is told all come from these.
 */
public record Laid() {

    /** A point of a mesh: where it is on the thing, and the way the thing's surface faces there. */
    public Layout point() {
        return new Layout("Point", List.of(new Field("point", Kind.TRIPLE), new Field("facing", Kind.TRIPLE)));
    }

    /**
     * Handed from the vertex shader on to the fragment shader: where on the thing the point is, in the thing's own
     * frame; the way its surface faces, in the world's; and where it is as the camera sees it, the eye at the middle.
     */
    public Layout passed() {
        return new Layout("Passed", List.of(new Field("at", Kind.TRIPLE), new Field("faces", Kind.TRIPLE),
                new Field("from", Kind.TRIPLE)));
    }

    /**
     * Told once a frame: the camera's turning and flattening together; the way the sun shines, its light, and the
     * light that falls on everything alike.
     */
    public Layout frame() {
        return new Layout("Frame", List.of(new Field("seen", Kind.MATRIX), new Field("sunTowards", Kind.TRIPLE),
                new Field("sun", Kind.TRIPLE), new Field("ambient", Kind.TRIPLE)));
    }

    /**
     * Told once for each thing drawn: where it stands, as the camera sees it; and how many times as far off it is to
     * seem as it is, each pixel on it counted as covering that many times as much of its surface. 1, for a thing
     * shown as it is; more, for a bench to show what the game would from further off without the thing shrinking.
     */
    public Layout draw() {
        return new Layout("Draw", List.of(new Field("placed", Kind.MATRIX), new Field("coarsened", Kind.REAL)));
    }
}
