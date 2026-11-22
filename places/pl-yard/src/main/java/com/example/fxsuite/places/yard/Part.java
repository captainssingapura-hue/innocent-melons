package com.example.fxsuite.places.yard;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Form;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.vulkan.form.Ball;
import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Tube;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * A part of a machine, by itself: its shape drawn as a graph, the way the engine draws a building's skeleton
 * ({@code Form}: named points, and the lines between them, with no thickness); how thick its lines are made, in
 * centimetres from a line to the bar's surface; and what it is made of, a leaf of the tree of materials. The graph
 * is in the part's own frame, as the engine has it: across to its right, along ahead, up from the ground.
 *
 * <p>One part gives both what is drawn of it and what is in the way of whoever walks round it.
 */
public record Part(String name, Form shape, double thick, Material of) {

    /** How many flat sides a bar is drawn with. */
    private static final int ROUND = 10;

    /** How far it must be lifted to rest on the ground: its lowest bar's underside brought up to nothing. */
    public double lifted() {
        return thick - shape.bounds().low();
    }

    /**
     * Its bars, and a ball where bars meet or end, added to {@code forming}, the part standing with its frame's
     * middle at {@code stands} and turned {@code turned} degrees about the upright through it.
     */
    public void formed(final Forming forming, final Vec3 stands, final double turned) {
        for (final Form.Edge edge : shape.edges()) {
            new Tube(place(edge.from(), stands, turned), place(edge.to(), stands, turned), thick, ROUND)
                    .formed(forming);
        }
        for (int point = 0; point < shape.vertices().size(); point++) {
            new Ball(place(point, stands, turned), thick, ROUND).formed(forming);
        }
    }

    /** Where the point numbered {@code point} then is, as the renderer has it: across, up, and along. */
    private Vec3 place(final int point, final Vec3 stands, final double turned) {
        final Form.Vertex at = shape.vertices().get(point);
        final double cos = Math.cos(Math.toRadians(turned));
        final double sin = Math.sin(Math.toRadians(turned));
        return stands.plus(new Vec3(at.across() * cos + at.along() * sin, at.up(),
                at.along() * cos - at.across() * sin));
    }

    /**
     * The part as what is in the way, standing and turned the same way: a box round all of it, square to the
     * ground's own lines.
     */
    public Block inTheWay(final Vec3 stands, final double turned) {
        double left = Double.POSITIVE_INFINITY;
        double right = Double.NEGATIVE_INFINITY;
        double back = Double.POSITIVE_INFINITY;
        double front = Double.NEGATIVE_INFINITY;
        for (int point = 0; point < shape.vertices().size(); point++) {
            final Vec3 at = place(point, stands, turned);
            left = Math.min(left, at.x());
            right = Math.max(right, at.x());
            back = Math.min(back, at.z());
            front = Math.max(front, at.z());
        }
        final Form.Bounds bounds = shape.bounds();
        return new Block((left + right) / 2, (back + front) / 2, (right - left) / 2 + thick,
                (front - back) / 2 + thick, 0, stands.y() + bounds.low() - thick, stands.y() + bounds.high() + thick);
    }
}
