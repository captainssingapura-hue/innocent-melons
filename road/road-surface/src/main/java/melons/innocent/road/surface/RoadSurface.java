package melons.innocent.road.surface;

import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.ThingImpl;
import com.example.fxsuite.existence.Highways;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;

import java.util.ArrayList;
import java.util.List;

/**
 * How anyone surfaces a road today: two lanes, the left going along the road's line and the right against it, the
 * traffic keeping left; a shoulder beside each, where one is asked for; its pavement falling 2.5% each way from its
 * crown on its line; a centre line between its lanes and an edge line along each lane's outer edge.
 */
public final class RoadSurface
        implements ThingImpl<Highways.Road, Civilization, CommonEras.Contemporary, RoadSpec<Civilization,
        CommonEras.Contemporary>> {

    /** How steeply its pavement falls from its line. */
    static final double FALL = 0.025;

    @Override
    public Highways.Road thing() {
        return new Highways.Road();
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
    public Structure build(final RoadSpec<Civilization, CommonEras.Contemporary> s) {
        return new Stretch(section(s), s.length()).structure();
    }

    /** {@code s}'s surface across, left to right going along it. */
    public Section section(final RoadSpec<?, ?> s) {
        final List<Strip> strips = new ArrayList<>();
        if (s.shoulder() > 0) {
            strips.add(new Strip(Strip.Kind.SHOULDER, s.shoulder()));
        }
        strips.add(new Strip(Strip.Kind.LANE, s.lane()));
        strips.add(new Strip(Strip.Kind.LANE, s.lane(), true));
        if (s.shoulder() > 0) {
            strips.add(new Strip(Strip.Kind.SHOULDER, s.shoulder()));
        }
        return new Section(strips, FALL, true);
    }
}
