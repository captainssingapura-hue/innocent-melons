package melons.innocent.core.surface;

import melons.innocent.core.motion.Spot;
import melons.innocent.core.rest.Box;
import melons.innocent.core.rest.Ground;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.ShapeImpl;
import melons.innocent.core.rest.Slab;
import melons.innocent.core.rest.World;

import java.util.Optional;

/**
 * What is under a spot of the world: the question a mover asks, answered from the world at rest. The world's things
 * that stand at rest are the providers, each a box or a slab by its faces, with the ground under all; the spot is
 * taken into each one's own frame, each is asked there, and the surface nearest under the spot is the answer, in its
 * provider's frame. A thing in motion provides nothing until it has come to rest; a round thing nothing at all, as
 * nothing stands on one yet.
 *
 * <p>The answer comes with the frame it is in, where its provider stands in the world, so that whoever aggregates
 * can put a place on the provider back into the world. The frame is the world's to keep and to compose; a mover's
 * engine is given the surface alone.
 */
public final class Underfoot {

    private final World world;
    private final Ground ground;
    private final Facts facts;

    /** What is under a spot of {@code world}, which lies on {@code ground}, its surfaces being as {@code facts} says. */
    public Underfoot(final World world, final Ground ground, final Facts facts) {
        this.world = world;
        this.ground = ground;
        this.facts = facts;
    }

    /**
     * A surface with the frame it is in.
     *
     * @param patch the surface, in its provider's own frame
     * @param frame where the provider stands in the world, and how it is turned
     */
    public record Found(Patch patch, Placed frame) {

        /** Where a spot on the provider is in the world. */
        public Spot inWorld(final Spot onProvider) {
            return frame.at().plus(frame.turned().turned(onProvider));
        }

        /** Where the surface is under the spot asked about, in the world. */
        public Spot at() {
            return inWorld(patch.at());
        }
    }

    /** The surface nearest under {@code spot} of the world, no further down than {@code reach}; none, where none is. */
    public Optional<Found> under(final Spot spot, final double reach) {
        final Spot down = new Spot(0, -1, 0);
        Optional<Found> nearest = new Levelled(ground.name(), ground.level(), facts).under(spot, down, reach)
                .map(patch -> new Found(patch, new Placed(new Spot(0, 0, 0))));
        for (final String name : world.things()) {
            final Optional<Box> box = boxed(world.thing(name).shape());
            if (box.isEmpty() || !world.resting(name)) {
                continue;
            }
            final Placed frame = world.placed(name).orElseThrow();
            final Optional<Patch> met = new Faced(name, box.get(), facts).under(frame.within(spot),
                    frame.lying(down), reach);
            if (met.isPresent() && (nearest.isEmpty() || met.get().below() < nearest.get().patch().below())) {
                nearest = Optional.of(new Found(met.get(), frame));
            }
        }
        return nearest;
    }

    /** The box a shape offers faces of: itself, or a slab's; none, of a round thing. */
    private Optional<Box> boxed(final ShapeImpl<?> shape) {
        return switch (shape) {
            case Box box -> Optional.of(box);
            case Slab slab -> Optional.of(slab.box());
            default -> Optional.empty();
        };
    }
}
