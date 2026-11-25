package melons.innocent.doors.swing;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.core.mechanism.Hinged;

import java.util.List;

/**
 * A swing door: one leaf hung on hinges in a doorway. It is all in its one typed description, a design with what it
 * is made of, the leaf's size, and how it swings; whatever is wanted of it comes from that: its parts as meshes,
 * the hole a wall must leave for it, what of it is in the way, and the hinge it turns on. Its design is in its
 * type, so a place that wants a glass door says {@code SwingDoor<Design.Frameless>} and can be handed no other.
 *
 * <p>Front and back are told by the description alone: the hung side and the way it opens are as one standing at
 * its front has them. Nothing here knows a card, a wall or a walk.
 *
 * @param design its design, and what it is made of
 * @param size   how big its leaf is
 * @param swing  which side it is hung on, which way it opens, and how wide
 * @param <D>    its design
 */
public record SwingDoor<D extends Design>(D design, Size size, Swing swing) {

    /** How the leaf hangs: where a place on it is, and how it is turned when open. */
    public Hanging hanging() {
        return new Hanging(size, swing);
    }

    /** The hinge it turns on: shut to as wide as it opens, at a door's pace. */
    public Hinged hinged() {
        return new Hinged(swing.widest());
    }

    /** Its parts: all of it that is of one material, one colour and moves one way as one mesh, the leaf shut. */
    public List<DoorPart> parts() {
        final Boxes boxes = new Boxes();
        final Leaves leaves = new Leaves(new Joinery(boxes, hanging()), size);
        switch (design) {
            case Design.Glazed glazed -> {
                new Framing(hanging()).framed(boxes, glazed.wood(), glazed.fittings());
                leaves.glazed(glazed);
            }
            case Design.Solid solid -> {
                new Framing(hanging()).framed(boxes, solid.wood(), solid.fittings());
                leaves.solid(solid);
            }
            case Design.Frameless frameless -> leaves.frameless(frameless);
        }
        return boxes.parts();
    }

    /** How wide a hole a wall must leave for it: the leaf, and its frame where it has one. */
    public double holeWide() {
        final Framing framing = new Framing(hanging());
        return design.framed() ? 2 * framing.aside() + framing.jamb() : size.wide() + 1;
    }

    /** How high a hole a wall must leave for it, from the floor. */
    public double holeHigh() {
        final Framing framing = new Framing(hanging());
        return design.framed() ? framing.head() + framing.jamb() : hanging().clears() + size.high() + 0.5;
    }

    /**
     * How far behind the middle of its frame's depth the middle of its leaf's thickness is, away from its front: so
     * a wall as deep as its frame knows where to stand it. Nothing, of a door with no frame.
     */
    public double set() {
        return design.framed() ? -new Framing(hanging()).back() : 0;
    }

    /** It, standing with the middle of its doorway's floor {@code at}, turned so many degrees about the upright. */
    public Stood stood(final Vec3 at, final double turned) {
        return new Stood(this, at, turned);
    }
}
