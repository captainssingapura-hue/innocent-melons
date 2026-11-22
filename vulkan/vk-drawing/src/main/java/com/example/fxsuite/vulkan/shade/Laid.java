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
public record Laid(boolean rays) {

    /** The layouts of looks that ask no rays: what any card draws. */
    public Laid() {
        this(false);
    }

    /**
     * The same layouts, for looks that ask rays where {@code able}: whether anything stands along a straight line
     * from a point, asked of whatever stands in the frame as the card keeps it. Only a card that answers rays
     * builds such looks.
     */
    public Laid asking(final boolean able) {
        return new Laid(able);
    }

    /**
     * A point of a mesh: where it is, the way the thing's surface faces there, and where on the thing's surface it
     * is, which is what the surface's terms are worked out from.
     */
    public Layout point() {
        return new Layout("Point", List.of(new Field("point", Kind.TRIPLE), new Field("facing", Kind.TRIPLE),
                new Field("on", Kind.TRIPLE)));
    }

    /** What of a point a look that colours nothing takes: where it is, and no more. */
    public List<String> placing() {
        return List.of("point");
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
     * light that falls on everything alike; what is mirrored from above and from below; and, for shadows, where the
     * sun's own picture of the frame has a point the camera sees ({@code beam}), with how far a point is eased
     * before it is looked up there: so many centimetres the way its surface faces, and so much nearer the sun.
     * Or, for shadows found by rays ({@code rays}): how far a point is eased the way its surface faces before a
     * ray is sent from it toward the sun, and how far the ray reaches, both in centimetres; no reach, for a frame
     * whose shadows are not found so.
     */
    public Layout frame() {
        return new Layout("Frame", List.of(new Field("seen", Kind.MATRIX), new Field("sunTowards", Kind.TRIPLE),
                new Field("sun", Kind.TRIPLE), new Field("ambient", Kind.TRIPLE), new Field("sky", Kind.TRIPLE),
                new Field("ground", Kind.TRIPLE), new Field("beam", Kind.MATRIX), new Field("eased", Kind.PAIR),
                new Field("rays", Kind.PAIR)));
    }

    /** Where a look's frame is bound, as its shaders declare it and the card is told. */
    public int frameBound() {
        return 0;
    }

    /** Where what stands in the frame is bound for a look that asks rays of it, the same way. */
    public int standingBound() {
        return 2;
    }

    /**
     * Which of what a thing drawn is told says where it stands: what whoever keeps the things of a frame, to
     * answer rays, takes of each.
     */
    public String stands() {
        return "placed";
    }

    /** Where the picture of depths a look looks things up in is bound, the same way. */
    public int depthsBound() {
        return 1;
    }

    /**
     * Told once for each thing drawn: where it stands, as the camera sees it; and how many times as far off it is to
     * seem as it is, each pixel on it counted as covering that many times as much of its surface. 1, for a thing
     * shown as it is; more, for a bench to show what the game would from further off without the thing shrinking.
     * And the thing's own colour, for a look whose surface takes its colour from the thing and not from its
     * material: one look then draws a thing of any colour.
     */
    public Layout draw() {
        return new Layout("Draw", List.of(new Field("placed", Kind.MATRIX), new Field("coarsened", Kind.REAL),
                new Field("tint", Kind.TRIPLE)));
    }
}
