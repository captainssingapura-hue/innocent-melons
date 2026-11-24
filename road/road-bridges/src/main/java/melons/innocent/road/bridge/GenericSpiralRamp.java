package melons.innocent.road.bridge;

import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.ThingImpl;
import com.example.fxsuite.existence.Bridges;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;
import melons.innocent.road.surface.Placed;
import melons.innocent.terrain.Ground;

/**
 * How anyone builds a spiral ramp today ({@link Spiral}): a road one way round it on a deck {@link #DEPTH} deep,
 * climbing round its middle at nought from the ground where it starts, its road's top there on the ground, as far round
 * and as many turns as asked, each turn held up over the one under it as asked. On the ground it is given, level
 * ground if none.
 *
 * @param ground what it stands on
 */
public record GenericSpiralRamp(Ground ground)
        implements ThingImpl<Bridges.SpiralRamp, Civilization, CommonEras.Contemporary, SpiralRampSpec<Civilization,
        CommonEras.Contemporary>> {

    /** How deep its deck is. */
    static final double DEPTH = 120;

    /** One on level ground, where height is nought. */
    public GenericSpiralRamp() {
        this(new Flat(0));
    }

    @Override
    public Bridges.SpiralRamp thing() {
        return new Bridges.SpiralRamp();
    }

    @Override
    public Civilization civilization() {
        return Civilization.ROOT;
    }

    @Override
    public CommonEras.Contemporary era() {
        return new CommonEras.Contemporary();
    }

    @Override
    public Structure build(final SpiralRampSpec<Civilization, CommonEras.Contemporary> s) {
        return spiral(s).structure();
    }

    /** {@code s} as a spiral. */
    public Spiral spiral(final SpiralRampSpec<?, ?> s) {
        final double start = ground.height(-s.radius(), 0);
        return new Spiral(new OneWay(s.lanes(), s.lane(), s.shoulder()).section(),
                new Placed<>(new Helix(s.radius(), s.rise(), s.turns(), start)), DEPTH, s.carried(), ground);
    }
}
