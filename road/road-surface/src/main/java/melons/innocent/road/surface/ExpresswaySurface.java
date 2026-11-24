package melons.innocent.road.surface;

import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.ThingImpl;
import com.example.fxsuite.existence.Highways;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;

import java.util.ArrayList;
import java.util.List;

/**
 * How anyone surfaces an expressway today: a concrete barrier down its middle, a narrow shoulder either side of it, and
 * either side of those a carriageway of so many lanes, the left going along the road's line and the right against it,
 * the traffic keeping left, a shoulder along its outer edge; each carriageway's pavement falling 2.5% away from the
 * barrier; lane lines between its lanes and an edge line along each lane next to a shoulder.
 */
public final class ExpresswaySurface
        implements ThingImpl<Highways.Expressway, Civilization, CommonEras.Contemporary, ExpresswaySpec<Civilization,
        CommonEras.Contemporary>> {

    /** How steeply its pavement falls from its middle; how wide its barrier is, and the shoulder either side of it. */
    static final double FALL = 0.025;
    static final double BARRIER = 60;
    static final double INNER = 60;

    @Override
    public Highways.Expressway thing() {
        return new Highways.Expressway();
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
    public Structure build(final ExpresswaySpec<Civilization, CommonEras.Contemporary> s) {
        return new Stretch(section(s), s.length()).structure();
    }

    /** {@code s}'s surface across, left to right going along it. */
    public Section section(final ExpresswaySpec<?, ?> s) {
        final List<Strip> strips = new ArrayList<>();
        strips.add(new Strip(Strip.Kind.SHOULDER, s.shoulder()));
        for (int i = 0; i < s.lanes(); i++) {
            strips.add(new Strip(Strip.Kind.LANE, s.lane()));
        }
        strips.add(new Strip(Strip.Kind.SHOULDER, INNER));
        strips.add(new Strip(Strip.Kind.BARRIER, BARRIER));
        strips.add(new Strip(Strip.Kind.SHOULDER, INNER));
        for (int i = 0; i < s.lanes(); i++) {
            strips.add(new Strip(Strip.Kind.LANE, s.lane(), true));
        }
        strips.add(new Strip(Strip.Kind.SHOULDER, s.shoulder()));
        return new Section(strips, FALL, true);
    }
}
