package melons.innocent.building.container;

import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.building.Structure.Part;
import com.example.fxsuite.building.Timbers;
import com.example.fxsuite.existence.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * A container house laid out as {@code spec} asks, its front toward along: two containers lying across, end to end
 * {@code gap} apart, raised over the ground, their doors at their inner ends and their long windows on their fronts;
 * between them a deck as high as their floors, {@link #DECK} deep; over the deck, square across the two, a third
 * container, its back end's door onto a landing behind it. A flight from the ground up to the front of the deck; a
 * second from the deck beside the upper container up to its landing; railings round the deck and the landing where
 * they are open. Under all of it, pillars wherever it is not otherwise held up ({@link Pillars}).
 *
 * @param spec what is asked for
 */
record Layout(ContainerHouseSpec<?, ?> spec) {

    /** How deep its deck is; how wide its first flight, its second, how far its second stands clear of the upper. */
    static final double DECK = 400;
    static final double FIRST = 100;
    static final double SECOND = 80;
    static final double CLEAR = 13;
    /** How deep its landing is; how far in from an edge a deck's pillars stand. */
    static final double LANDING = 120;

    /** Its containers: the lower left, the lower right, the upper. */
    List<Container> boxes() {
        final double l = spec.size().length();
        final double y = spec.raised();
        final double x = spec.gap() / 2 + l / 2;
        return List.of(new Container(spec.size(), new Placing(-x, 0, y, true), 1, 1, spec.lower(), spec.frame()),
                new Container(spec.size(), new Placing(x, 0, y, true), -1, 1, spec.lower(), spec.frame()),
                new Container(spec.size(), new Placing(0, 0, y + Size.HIGH, false), -1, 1, spec.upper(),
                        spec.frame()));
    }

    /** How high its deck's top is; its landing's. */
    double deck() {
        return spec.raised() + Container.FLOOR;
    }

    double landing() {
        return deck() + Size.HIGH;
    }

    /** Its first flight, up from the ground to its deck's front. */
    Flight first() {
        return new Flight(-FIRST / 2, FIRST / 2, DECK / 2, -1, 0, deck(), spec.frame());
    }

    /** Its second flight, from its deck beside the upper container up to its landing behind it. */
    Flight second() {
        final double x0 = Size.WIDE / 2 + CLEAR;
        return new Flight(x0, x0 + SECOND, -spec.size().length() / 2, -1, deck(), landing(), spec.frame());
    }

    /** Its deck, its landing, their railings, and the pillars under them. */
    List<Part> decks(final Pillars pillars) {
        final double g = spec.gap() / 2;
        final double d = DECK / 2;
        final double w = Size.WIDE / 2;
        final double back = -spec.size().length() / 2;
        final double side = second().x1();
        final List<Part> parts = new ArrayList<>();
        parts.add(deck(-g, g, -d, d, spec.raised()));
        parts.add(deck(-w, side, back - LANDING, back, landing() - Container.FLOOR));
        parts.addAll(pillars.under(corners(-g, g, -d, d), spec.raised()));
        parts.addAll(pillars.under(corners(-w, side, back - LANDING, back), landing() - Container.FLOOR));
        final List<Rail> rails = List.of(new Rail(-g, d, -FIRST / 2, d, deck(), spec.frame()),
                new Rail(FIRST / 2, d, g, d, deck(), spec.frame()),
                new Rail(-g, -d, second().x0(), -d, deck(), spec.frame()),
                new Rail(side, -d, g, -d, deck(), spec.frame()),
                new Rail(-g, w, -g, d, deck(), spec.frame()), new Rail(-g, -d, -g, -w, deck(), spec.frame()),
                new Rail(g, w, g, d, deck(), spec.frame()), new Rail(g, -d, g, -w, deck(), spec.frame()),
                new Rail(-w, back - LANDING, side, back - LANDING, landing(), spec.frame()),
                new Rail(-w, back - LANDING, -w, back, landing(), spec.frame()),
                new Rail(side, back - LANDING, side, back, landing(), spec.frame()));
        rails.forEach(r -> parts.addAll(r.parts()));
        return parts;
    }

    /** A deck of timber from {@code x0} to {@code x1}, {@code z0} to {@code z1}, its underside {@code y} high. */
    private Part deck(final double x0, final double x1, final double z0, final double z1, final double y) {
        return new Part(new Elements.Decking(), Finish.TIMBER.on(Timbers.INSTANCE.block("deck", Role.STRUCTURE,
                Finish.TIMBER.material(), x0, x1, z0, z1, y, y + Container.FLOOR)));
    }

    /** Where a deck from {@code x0} to {@code x1}, {@code z0} to {@code z1} wants pillars: its corners. */
    private List<Vec2> corners(final double x0, final double x1, final double z0, final double z1) {
        final double in = Pillars.SQUARE / 2;
        return List.of(new Vec2(x0 + in, z0 + in), new Vec2(x1 - in, z0 + in), new Vec2(x0 + in, z1 - in),
                new Vec2(x1 - in, z1 - in));
    }
}
