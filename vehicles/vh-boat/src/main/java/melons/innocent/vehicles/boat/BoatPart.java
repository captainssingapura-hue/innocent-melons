package melons.innocent.vehicles.boat;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * One part of a boat: what it is called, what it is finished in, its colour where its finish takes one (paint, a
 * lantern), whether it is the wheel, which turns about its own hub as the boat is steered, and its form: the wheel's
 * about its hub, its spokes square to its third way; every other part's in the boat's own frame.
 *
 * @param name   what it is
 * @param finish what its surface is
 * @param colour its colour, where its finish takes one
 * @param wheel  whether it is the wheel
 * @param form   its form
 */
public record BoatPart(String name, Finish finish, Vec3 colour, boolean wheel, Mesh form) {

    /** What a part's surface is: the material lab is asked for each, by whoever draws it. */
    public enum Finish {
        /** Gloss paint, in the part's colour. */
        PAINT,
        /** Varnished timber. */
        TIMBER,
        /** Rubber: the tyres hung for fenders. */
        RUBBER,
        /** Steel: the rails and the bollard. */
        STEEL,
        /** A paper lantern, lit, in the part's colour. */
        LANTERN
    }
}
