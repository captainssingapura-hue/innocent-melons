package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;
import melons.innocent.physics.existences.Shapes;

/**
 * A slab that never moves: flat ground of its own, level or tilted as it is placed. A ramp, a bank, a deck. It is of
 * the kind physics calls a slope, which is unmoving, so the world never checks it and never lets it go: it holds up
 * whatever stands on it and is held up by nothing. It meets other things as a box of its size would.
 *
 * @param box how big it is, as a box
 */
public record Slab(Box box) implements ShapeImpl<Shapes.Slope> {

    /** A slab so far across, so thick and so far along. */
    public Slab(final double across, final double thick, final double along) {
        this(new Box(across, thick, along));
    }

    @Override
    public Shapes.Slope kind() {
        return new Shapes.Slope();
    }

    @Override
    public double room() {
        return box.room();
    }

    @Override
    public double reaches() {
        return box.reaches();
    }

    @Override
    public double turnsHard(final Spot along, final double kilograms) {
        return box.turnsHard(along, kilograms);
    }
}
