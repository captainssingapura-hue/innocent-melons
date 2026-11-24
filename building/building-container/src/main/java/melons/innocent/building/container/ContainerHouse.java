package melons.innocent.building.container;

import com.example.fxsuite.avatar.build.StandIns;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.ThingImpl;
import com.example.fxsuite.existence.Buildings;
import com.example.fxsuite.historian.Civilization;
import com.example.fxsuite.historian.CommonEras;

import java.util.ArrayList;
import java.util.List;

/**
 * How anyone builds a house of shipping containers today: three high cubes, two raised side by side end to end with a
 * deck between them and one across them over it ({@link Layout}), each a room to live in ({@link Container}); its
 * stairs outside, steel; held up by pillars wherever nothing else holds it up ({@link Pillars}); each part finished as
 * asked. Come onto at the foot of its first flight, on the ground; its containers come into from the deck and the
 * landing, their doors onto them.
 */
public final class ContainerHouse
        implements ThingImpl<Buildings.House, Civilization, CommonEras.Contemporary, ContainerHouseSpec<Civilization,
        CommonEras.Contemporary>> {

    @Override
    public Buildings.House thing() {
        return new Buildings.House();
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
    public Structure build(final ContainerHouseSpec<Civilization, CommonEras.Contemporary> s) {
        final Layout layout = new Layout(s);
        final List<Container> boxes = layout.boxes();
        final Pillars pillars = new Pillars(boxes, s.frame());
        final List<Part> parts = new ArrayList<>();
        boxes.forEach(b -> parts.addAll(b.parts()));
        parts.addAll(pillars.all());
        parts.addAll(layout.decks(pillars));
        parts.addAll(layout.first().parts());
        parts.addAll(layout.second().parts());
        final Flight first = layout.first();
        final List<Structure.WayIn> ways = new ArrayList<>();
        ways.add(new Structure.WayIn(0, first.start() + Flight.GOING, 0));
        boxes.forEach(b -> ways.add(b.wayIn()));
        return new Structure(parts, StandIns.prisms(parts.stream().map(Part::solid).toList()),
                boxes.stream().map(Container::room).toList(), ways, List.of(first.stair(), layout.second().stair()));
    }
}
