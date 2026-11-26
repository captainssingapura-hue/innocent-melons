package melons.innocent.core.surface;

import com.example.fxsuite.taxonomy.Node;
import melons.innocent.core.motion.Spot;

/**
 * What a provider's surface is made of and what state it is in, at a spot: the two facts of a surface that are not
 * its geometry, kept by whoever makes the world and asked by each provider as it answers. The condition is a fact of
 * the spot and not of the whole surface, so a puddle lies on a dry road.
 */
public interface Facts {

    /** What the surface of the thing called {@code provider} is made of at {@code spot}, in its own frame. */
    Node material(String provider, Spot spot);

    /** What state the surface of the thing called {@code provider} is in at {@code spot}, in its own frame. */
    Node condition(String provider, Spot spot);
}
