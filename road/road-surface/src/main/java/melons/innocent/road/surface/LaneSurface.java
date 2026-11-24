package melons.innocent.road.surface;

import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.ThingImpl;
import com.example.fxsuite.existence.Highways;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;

import java.util.ArrayList;
import java.util.List;

/**
 * How anyone surfaces a country lane today: one lane, shared by both ways, its pavement falling 2.5% each way from its
 * crown on its line, nothing painted on it.
 */
public final class LaneSurface
        implements ThingImpl<Highways.Lane, Civilization, CommonEras.Contemporary, LaneSpec<Civilization,
        CommonEras.Contemporary>> {

    /** How steeply its pavement falls from its line. */
    static final double FALL = 0.025;

    @Override
    public Highways.Lane thing() {
        return new Highways.Lane();
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
    public Structure build(final LaneSpec<Civilization, CommonEras.Contemporary> s) {
        return new Stretch(section(s), s.length()).structure();
    }

    /** {@code s}'s surface across, left to right going along it. */
    public Section section(final LaneSpec<?, ?> s) {
        final List<Strip> strips = new ArrayList<>();
        strips.add(new Strip(Strip.Kind.LANE, s.wide()));
        return new Section(strips, FALL, false);
    }
}
