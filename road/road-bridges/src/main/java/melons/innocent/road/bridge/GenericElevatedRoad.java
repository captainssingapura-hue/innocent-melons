package melons.innocent.road.bridge;

import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.ThingImpl;
import com.example.fxsuite.existence.Bridges;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;
import melons.innocent.road.surface.Joint;
import melons.innocent.road.surface.Placed;
import melons.innocent.road.surface.Section;
import melons.innocent.road.surface.Straight;
import melons.innocent.terrain.Ground;

import java.util.ArrayList;
import java.util.List;

/**
 * How anyone builds an elevated road today: a viaduct along, its middle at nought, as high as asked over the ground
 * there ({@link Bridge}); a straight ramp climbing to its start from the ground before it, heading the same way, its
 * end where the viaduct begins; and a spiral ramp ({@link Spiral}) climbing round from the ground beyond its far end,
 * its top turning onto the viaduct's end heading back along it. Each joined to the viaduct where the one ends and the
 * other begins, as high and heading the same way, so its deck and the road on it run on without a gap. The same road
 * all along, on decks {@link #DEPTH} deep. The viaduct's piers stop a span short of its far end, and it has no
 * abutment there, the spiral's lower turns passing under it; its end rests on the spiral's top. On the ground it is
 * given, level ground if none. Come onto at the foot of its straight ramp.
 *
 * @param ground what it stands on
 */
public record GenericElevatedRoad(Ground ground)
        implements ThingImpl<Bridges.ElevatedRoad, Civilization, CommonEras.Contemporary,
        ElevatedRoadSpec<Civilization, CommonEras.Contemporary>> {

    /** How deep its decks are; how far, besides, its viaduct's last pier stands clear of its spiral. */
    static final double DEPTH = 150;
    static final double MARGIN = 100;

    /** One on level ground, where height is nought. */
    public GenericElevatedRoad() {
        this(new Flat(0));
    }

    @Override
    public Bridges.ElevatedRoad thing() {
        return new Bridges.ElevatedRoad();
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
    public Structure build(final ElevatedRoadSpec<Civilization, CommonEras.Contemporary> s) {
        final Bridge ramp = ramp(s);
        final List<Part> parts = new ArrayList<>(ramp.parts(false));
        parts.addAll(viaduct(s).parts(false));
        parts.addAll(spiral(s).parts());
        final Point foot = ramp.course().at(0);
        return new Structure(parts, StandIns.prisms(parts.stream().map(Part::solid).toList()), List.of(),
                List.of(new Structure.WayIn(foot.across(), foot.along(), foot.up())));
    }

    /** Its viaduct: its piers a span short of its far end, where its spiral's turns pass under it. */
    public Bridge viaduct(final ElevatedRoadSpec<?, ?> s) {
        final Section section = section(s);
        final double clear = s.radius() + section.wide() / 2 + Deck.PARAPET + Spiral.GAP + Spiral.COLUMN
                + Bridge.CAP_ALONG / 2 + MARGIN;
        final double reach = s.length() - clear;
        final int spans = Math.max(1, (int) Math.round(reach / s.span()));
        final List<Double> piers = new ArrayList<>();
        for (int i = 1; i <= spans; i++) {
            piers.add(i * reach / spans);
        }
        return new Bridge(section, new Straight(s.length(), ground.height(0, 0) + s.height()), DEPTH, piers,
                s.piers(), ground);
    }

    /** Its straight ramp, its end where its viaduct begins. */
    public Bridge ramp(final ElevatedRoadSpec<?, ?> s) {
        final double length = s.height() / s.grade();
        final Straight straight = new Straight(length, ground.height(0, 0), s.grade());
        final Placed<Straight> course = new Joint(viaduct(s).course(), 0).placing(straight, length);
        final int spans = Math.max(2, (int) Math.round(length / s.span()));
        final List<Double> piers = new ArrayList<>();
        for (int i = 1; i < spans; i++) {
            piers.add(i * length / spans);
        }
        return new Bridge(section(s), course, DEPTH, piers, s.piers(), ground);
    }

    /** Its spiral ramp, its top turning onto its viaduct's far end. */
    public Spiral spiral(final ElevatedRoadSpec<?, ?> s) {
        final Helix helix = new Helix(s.radius(), s.height() / s.turns(), s.turns(), ground.height(0, 0));
        final Joint end = new Joint(viaduct(s).course(), s.length());
        final Placed<Helix> line = new Joint(end.at(), end.heading() + Math.PI).placing(helix, helix.length());
        return new Spiral(section(s), line, DEPTH, s.carried(), ground);
    }

    private Section section(final ElevatedRoadSpec<?, ?> s) {
        return new OneWay(s.lanes(), s.lane(), s.shoulder()).section();
    }
}
