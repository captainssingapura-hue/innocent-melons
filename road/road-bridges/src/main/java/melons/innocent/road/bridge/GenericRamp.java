package melons.innocent.road.bridge;

import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.ThingImpl;
import com.example.fxsuite.existence.Bridges;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;
import melons.innocent.road.surface.Straight;
import melons.innocent.terrain.Ground;

import java.util.ArrayList;
import java.util.List;

/**
 * How anyone builds a straight ramp today ({@link Bridge}): a road one way up it on a deck {@link #DEPTH} deep,
 * climbing straight along from the ground where it starts, its road's top there on the ground, up to as high as asked,
 * on spans about as long as asked, a pier between each two; the lowest of them, where the deck is too low for a
 * column, a plinth, and none where the deck is on the ground; an abutment at its top. On the ground it is given, level
 * ground if none.
 *
 * @param ground what it stands on
 */
public record GenericRamp(Ground ground)
        implements ThingImpl<Bridges.Ramp, Civilization, CommonEras.Contemporary, RampSpec<Civilization,
        CommonEras.Contemporary>> {

    /** How deep its deck is. */
    static final double DEPTH = 150;

    /** One on level ground, where height is nought. */
    public GenericRamp() {
        this(new Flat(0));
    }

    @Override
    public Bridges.Ramp thing() {
        return new Bridges.Ramp();
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
    public Structure build(final RampSpec<Civilization, CommonEras.Contemporary> s) {
        return bridge(s).structure();
    }

    /** {@code s} as a bridge, climbing. */
    public Bridge bridge(final RampSpec<?, ?> s) {
        final double length = s.length();
        final int spans = Math.max(2, (int) Math.round(length / s.span()));
        final List<Double> piers = new ArrayList<>();
        for (int i = 1; i < spans; i++) {
            piers.add(i * length / spans);
        }
        final double start = ground.height(0, -length / 2);
        return new Bridge(new OneWay(s.lanes(), s.lane(), s.shoulder()).section(),
                new Straight(length, start, s.grade()), DEPTH, piers, s.piers(), ground);
    }
}
