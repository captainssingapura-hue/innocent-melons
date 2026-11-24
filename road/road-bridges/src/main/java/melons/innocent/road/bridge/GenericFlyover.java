package melons.innocent.road.bridge;

import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.ThingImpl;
import com.example.fxsuite.existence.Bridges;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;
import melons.innocent.road.surface.RoadSpec;
import melons.innocent.road.surface.RoadSurface;
import melons.innocent.terrain.Ground;

import java.util.List;

/**
 * How anyone builds a flyover today ({@link Bridge}): a road of two lanes on a deck {@link #DEPTH} deep, leaving as
 * much room under it as asked over the ground under its middle; three spans, the middle one over what it crosses, a
 * pier {@link #CLEAR} clear of it either side on one column, and a span {@link #SIDE} long either side of those to an
 * abutment at each end; on the ground it is given, level ground if none. Its approaches, the ground raised to it, are
 * the road's base, not its.
 *
 * @param ground what it stands on
 */
public record GenericFlyover(Ground ground)
        implements ThingImpl<Bridges.Flyover, Civilization, CommonEras.Contemporary, FlyoverSpec<Civilization,
        CommonEras.Contemporary>> {

    /** How deep its deck is; how far its piers stand clear of what it crosses; how long its side spans are. */
    static final double DEPTH = 150;
    static final double CLEAR = 200;
    static final double SIDE = 2_000;

    /** One on level ground, where height is nought. */
    public GenericFlyover() {
        this(new Flat(0));
    }

    @Override
    public Bridges.Flyover thing() {
        return new Bridges.Flyover();
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
    public Structure build(final FlyoverSpec<Civilization, CommonEras.Contemporary> s) {
        return bridge(s).structure();
    }

    /** {@code s} as a bridge. */
    public Bridge bridge(final FlyoverSpec<?, ?> s) {
        final double pier = s.crossing() / 2 + CLEAR;
        final double length = 2 * (pier + SIDE);
        return new Bridge(new RoadSurface().section(new RoadSpec<>(s.lane(), s.shoulder(), length)), length,
                ground.height(0, 0) + s.clearance() + DEPTH, DEPTH, List.of(-pier, pier), Piers.SINGLE, ground);
    }
}
