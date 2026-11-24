package melons.innocent.road.surface;

import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.ThingImpl;
import com.example.fxsuite.existence.Highways;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;

import java.util.ArrayList;
import java.util.List;

/**
 * How anyone surfaces a street today: its lanes between kerbs, those on the left going along the street's line and
 * those on the right against it, the traffic keeping left; its pavement falling 2.5% each way from its crown on its
 * line to the kerbs; lane lines between lanes going the same way, a centre line between the two ways, and an edge line
 * along each kerb.
 */
public final class StreetSurface
        implements ThingImpl<Highways.Street, Civilization, CommonEras.Contemporary, StreetSpec<Civilization,
        CommonEras.Contemporary>> {

    /** How steeply its pavement falls from its line; how wide its kerbs are. */
    static final double FALL = 0.025;
    static final double KERB = 15;

    @Override
    public Highways.Street thing() {
        return new Highways.Street();
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
    public Structure build(final StreetSpec<Civilization, CommonEras.Contemporary> s) {
        return new Stretch(section(s), s.length()).structure();
    }

    /** {@code s}'s surface across, left to right going along it. */
    public Section section(final StreetSpec<?, ?> s) {
        final List<Strip> strips = new ArrayList<>();
        strips.add(new Strip(Strip.Kind.KERB, KERB));
        for (int i = 0; i < s.lanes(); i++) {
            strips.add(new Strip(Strip.Kind.LANE, s.lane()));
        }
        for (int i = 0; i < s.lanes(); i++) {
            strips.add(new Strip(Strip.Kind.LANE, s.lane(), true));
        }
        strips.add(new Strip(Strip.Kind.KERB, KERB));
        return new Section(strips, FALL, true);
    }
}
