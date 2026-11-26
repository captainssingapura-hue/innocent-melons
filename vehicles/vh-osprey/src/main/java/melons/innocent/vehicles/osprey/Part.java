package melons.innocent.vehicles.osprey;

import com.example.fxsuite.vulkan.form.Mesh;

/**
 * One part of the Osprey: what it is called, what it is finished in, what it turns with, and its form, in the frame
 * of what it turns with (the body's own, a nacelle's about its pivot, a rotor's about its hub).
 *
 * @param name   what it is
 * @param finish what its surface is
 * @param moves  what it turns with
 * @param form   its form
 */
public record Part(String name, Finish finish, Moves moves, Mesh form) {

    /** What a part's surface is: the material lab is asked for each, by whoever draws it. */
    public enum Finish {
        /** Its grey paint. */
        PAINT,
        /** Its windows. */
        GLASS,
        /** Its tyres. */
        RUBBER,
        /** Bare metal: its rotors' hubs. */
        METAL
    }

    /** What a part turns with. */
    public enum Moves {
        /** The body itself. */
        BODY,
        /** The left nacelle, about its pivot; the right. */
        LEFT_NACELLE,
        RIGHT_NACELLE,
        /** The left rotor, about its hub, as its nacelle turns it; the right. */
        LEFT_ROTOR,
        RIGHT_ROTOR
    }
}
