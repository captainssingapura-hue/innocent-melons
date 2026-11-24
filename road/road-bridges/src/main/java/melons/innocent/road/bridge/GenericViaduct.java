package melons.innocent.road.bridge;

import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.ThingImpl;
import com.example.fxsuite.existence.Bridges;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;
import melons.innocent.road.surface.ExpresswaySpec;
import melons.innocent.road.surface.ExpresswaySurface;
import melons.innocent.terrain.Ground;

import java.util.ArrayList;
import java.util.List;

/**
 * How anyone builds a viaduct today ({@link Bridge}): an expressway's surface on a box deck {@link #DEPTH} deep, as
 * high over the ground under its middle as asked; parted into as many equal spans as bring each nearest the span
 * asked, two at least, a pier where each meets the next and an abutment at each end; on the ground it is given, level
 * ground if none.
 *
 * @param ground what it stands on
 */
public record GenericViaduct(Ground ground)
        implements ThingImpl<Bridges.Viaduct, Civilization, CommonEras.Contemporary, ViaductSpec<Civilization,
        CommonEras.Contemporary>> {

    /** How deep its deck is. */
    static final double DEPTH = 200;

    /** One on level ground, where height is nought. */
    public GenericViaduct() {
        this(new Flat(0));
    }

    @Override
    public Bridges.Viaduct thing() {
        return new Bridges.Viaduct();
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
    public Structure build(final ViaductSpec<Civilization, CommonEras.Contemporary> s) {
        return bridge(s).structure();
    }

    /** {@code s} as a bridge. */
    public Bridge bridge(final ViaductSpec<?, ?> s) {
        final int spans = Math.max(2, (int) Math.round(s.length() / s.span()));
        final List<Double> piers = new ArrayList<>();
        for (int i = 1; i < spans; i++) {
            piers.add(-s.length() / 2 + i * s.length() / spans);
        }
        return new Bridge(new ExpresswaySurface().section(new ExpresswaySpec<>(s.lanes(), s.lane(), s.shoulder(),
                s.length())), s.length(), ground.height(0, 0) + s.height(), DEPTH, piers, s.piers(), ground);
    }
}
