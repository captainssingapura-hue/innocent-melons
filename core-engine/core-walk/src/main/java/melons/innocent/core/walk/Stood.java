package melons.innocent.core.walk;

import com.example.fxsuite.taxonomy.Node;
import melons.innocent.core.motion.Spot;

/**
 * The surface under a spot, as a walker's engine is handed it, in the world: what every mover is answered, put
 * together by the core with how the surface's provider is itself going, which the provider never says.
 *
 * @param provider  what provides the surface
 * @param high      how high the surface is there
 * @param faces     which way the surface faces there: straight up, where it is level
 * @param going     how its provider is going over the ground
 * @param form      what form of surface it is: a leaf of the surfaces' tree
 * @param condition what condition it is in there: a leaf of the conditions' tree
 */
public record Stood(String provider, double high, Spot faces, Spot going, Node form, Node condition) {

    /** How steep it is there: rise over run, nothing where it is level. */
    public double grade() {
        return Math.hypot(faces.across(), faces.along()) / faces.up();
    }
}
