package melons.innocent.core.walk;

import melons.innocent.core.motion.Spot;

import java.util.Optional;

/**
 * What is under a walker, as its engine may ask it: the one thing of the world round it that it is handed. The
 * core makes it for each walker from the world as it is, and the engine reaches for nothing else.
 */
public interface Under {

    /** The surface nearest under {@code spot}, no further below it than {@code reach}; none, where there is none. */
    Optional<Stood> under(Spot spot, double reach);
}
