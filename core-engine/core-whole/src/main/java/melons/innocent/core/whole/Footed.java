package melons.innocent.core.whole;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.State;
import melons.innocent.core.surface.Underfoot;
import melons.innocent.core.walk.Stood;
import melons.innocent.core.walk.Under;

import java.util.Optional;

/**
 * What is under a walker, made from a world as it is: the surface every mover is answered with, put into the world
 * and given how its provider is going, which the provider itself never says and only the world knows.
 *
 * @param underfoot the question, asked of the world's things at rest and its ground
 * @param state     the world's things, for how each provider is going
 */
record Footed(Underfoot underfoot, State state) implements Under {

    @Override
    public Optional<Stood> under(final Spot spot, final double reach) {
        return underfoot.under(spot, reach).map(found -> new Stood(found.patch().provider(), found.at().up(),
                found.faces(), state.moved(found.patch().provider()).velocity(), found.patch().form(),
                found.patch().condition()));
    }
}
