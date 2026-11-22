package melons.innocent.terrain.melon;

import com.example.fxsuite.math.graph.Directed;
import com.example.fxsuite.math.graph.Edge;
import com.example.fxsuite.math.graph.Graph;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import melons.innocent.math.curves.Knot;
import melons.innocent.math.curves.Knots;
import melons.innocent.math.curves.Monotone;
import melons.innocent.terrain.Channel;
import melons.innocent.terrain.Coast;
import melons.innocent.terrain.Course;
import melons.innocent.terrain.Extent;
import melons.innocent.terrain.Mount;
import melons.innocent.terrain.Path;
import melons.innocent.terrain.Plain;
import melons.innocent.terrain.Ridge;
import melons.innocent.terrain.Spot;
import melons.innocent.terrain.Terrain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The Melon country: the dry ground the melons' village stands on, six kilometres square, the village's lane at its
 * middle, running north. The Melon plain, 12 m over the sea, level where the village is. The Melon Range across the
 * country's north-east: a main ridge in from beyond its north-east corner, a saddle on it and then Mount Melon, its
 * highest peak, 760 m, from which a spur runs south-east; and over the river's valley from Mount Melon, a lower
 * ridge in from beyond the north edge, falling away south-west toward the plain. The Melon Downs to the west, low and
 * round. The Melon River coming in from beyond the country's north edge down its valley between them, then winding
 * over the plain east of the village, some three or four metres below it, to its mouth at the head of Melon Bay,
 * its bed going on out into the bay. The bay held between Melon Point to the west and Melon Head to the east,
 * shelving from its beaches to 13 m a little over half a kilometre out and to 30 m over the open sea. Melon Harbor,
 * east of the river's mouth along the bay's head: a deep 16 m down and some 260 m wide, its side to the shore steep,
 * 16 m down in some 45 m, so a quay along that shore may one day have big ships at it; and from it, 200 m wide, a way
 * out as deep to the open sea. No water: the river's bed and the bay's lie dry, as deep as they are. Sizes in metres
 * here, the terrain's own in centimetres. Its map has the lines its land features lie within, too
 * ({@link MelonFeatures}).
 */
public record MelonCountry() implements StatelessFunctionalObject {

    public static final MelonCountry INSTANCE = new MelonCountry();

    private static final double M = 100;

    /** The country, made. */
    public Terrain terrain() {
        final List<Edge<Spot, Course>> lines = new ArrayList<>();
        lines.addAll(lines(shore(), List.of(0.0, 0.0, -80.0, -80.0, 0.0, 60.0, 60.0, 0.0, -80.0, -80.0, 0.0, 0.0)));
        lines.addAll(lines(river(), List.of(0.0, 50.0, -60.0, 40.0, 70.0, -90.0, 60.0, -40.0, 0.0)));
        lines.addAll(lines(harbour(), List.of(0.0, 0.0, 0.0, 0.0)));
        lines.addAll(lines(range(), List.of(80.0, -60.0)));
        lines.addAll(lines(eastSpur(), List.of(60.0, -70.0)));
        lines.addAll(lines(westRidge(), List.of(40.0, -50.0, 40.0)));
        for (final List<Spot> area : MelonFeatures.INSTANCE.areas()) {
            lines.addAll(lines(area, Collections.nCopies(area.size() - 1, 0.0)));
        }
        final Set<Spot> spots = new LinkedHashSet<>(shore());
        spots.addAll(river());
        spots.addAll(harbour());
        spots.addAll(range());
        spots.addAll(eastSpur());
        spots.addAll(westRidge());
        MelonFeatures.INSTANCE.areas().forEach(spots::addAll);
        return new Terrain("the Melon country", new Extent(-3_000 * M, -2_600 * M, 3_000 * M, 3_400 * M),
                new Graph<>(List.copyOf(spots), lines), List.of(
                        new Plain("the Melon plain", 12 * M),
                        new Ridge("the Melon Range", new Path(range()),
                                curve(0, 420, 450, 600, 800, 520, 1_300, 760), fall()),
                        new Ridge("the Melon Range's east spur", new Path(eastSpur()),
                                curve(0, 760, 400, 620, 900, 540, 1_250, 420, 1_650, 220), fall()),
                        new Ridge("the Melon Range's west ridge", new Path(westRidge()),
                                curve(0, 520, 700, 640, 1_500, 470, 2_130, 160), fall()),
                        new Mount("the Melon Downs", spot(-1_900, 900),
                                curve(0, 85, 50, 84.7, 300, 70, 800, 30, 1_200, 0.2, 1_300, 0),
                                0, curve(0, 0, 1, 0)),
                        new Channel("the Melon River", new Path(river()), curve(0, 215, 400, 190, 1_350, 140, 1_900, 95,
                                2_470, 48, 3_060, 11, 3_640, 9, 4_080, 7.5, 4_520, -1.5, 4_950, -8),
                                curve(0, 6, 1_500, 10, 3_000, 18, 4_000, 30, 4_950, 45),
                                curve(0, 0, 6, 2.5, 40, 6, 150, 25, 250, 70, 400, 250, 600, 600, 1_000, 1_200)),
                        new Channel("Melon Harbor", new Path(harbour()), curve(0, -16, 2_200, -16),
                                curve(0, 260, 650, 260, 800, 200, 2_200, 200),
                                curve(0, 0, 15, 8, 35, 16, 60, 24, 100, 45)),
                        new Coast("Melon Bay", new Path(shore()),
                                curve(0, 0, 40, -2, 200, -7, 600, -13, 1_500, -22, 3_000, -30),
                                curve(0, 0, 30, 1.2, 120, 5, 260, 13, 320, 16))));
    }

    /**
     * The shore, west to east, the sea on its right: in from past the map's west edge, out round Melon Point, in to
     * the bay's head where the river meets it, out round Melon Head, and on past the map's east edge.
     */
    List<Spot> shore() {
        return List.of(spot(-3_300, -950), spot(-2_000, -880), spot(-1_350, -1_150), spot(-1_050, -1_700),
                spot(-750, -1_350), spot(-350, -750), spot(300, -450), spot(950, -680), spot(1_350, -1_250),
                spot(1_550, -1_800), spot(1_950, -1_400), spot(2_600, -1_100), spot(3_300, -1_050));
    }

    /**
     * The river, downstream: in from beyond the country's north edge, down its valley between Mount Melon and the
     * west ridge, over the plain to its mouth, and on out over the bay's bed.
     */
    List<Spot> river() {
        return List.of(spot(1_500, 3_800), spot(1_450, 3_050), spot(1_400, 2_450), spot(1_250, 1_900),
                spot(1_250, 1_350), spot(950, 850), spot(680, 330), spot(540, -80),
                spot(300, -450), spot(260, -850));
    }

    /**
     * The Melon Range's main ridge, east to west: in from past the country's north-east corner, over a saddle, to
     * Mount Melon above the river's valley, where its spur leaves it.
     */
    List<Spot> range() {
        return List.of(spot(3_300, 3_100), spot(2_700, 2_800), spot(2_100, 2_600));
    }

    /** The range's east spur, from Mount Melon south-east toward the coast. */
    List<Spot> eastSpur() {
        return List.of(spot(2_100, 2_600), spot(2_250, 1_800), spot(2_600, 1_100));
    }

    /** The range's west ridge, over the river's valley: in from past the north edge, south-west toward the plain. */
    List<Spot> westRidge() {
        return List.of(spot(650, 3_700), spot(850, 3_050), spot(650, 2_300), spot(250, 1_900));
    }

    /** How far a ridge's flanks have fallen from its top, for how far from its crest: steep near it, easing out. */
    private Monotone fall() {
        return curve(0, 0, 150, 90, 400, 230, 800, 400, 1_400, 560, 2_000, 680, 2_600, 780);
    }

    /**
     * Melon Harbor's deep, along its middle: some 175 m off the bay's head east of the river's mouth, following the
     * shore as it bows in, then out south to the open sea.
     */
    List<Spot> harbour() {
        return List.of(spot(382, -633), spot(587, -673), spot(802, -790), spot(720, -1_300), spot(620, -2_100));
    }

    /** Lines from each of {@code spots} to the next, each bowed so many metres to its left as {@code bows} says. */
    private List<Edge<Spot, Course>> lines(final List<Spot> spots, final List<Double> bows) {
        final List<Edge<Spot, Course>> lines = new ArrayList<>();
        for (int i = 0; i + 1 < spots.size(); i++) {
            final Spot a = spots.get(i);
            final Spot b = spots.get(i + 1);
            final double length = a.to(b);
            final Course course = bows.get(i) == 0 ? new Course.Straight() : new Course.Bowed(new Monotone(
                    new Knots(List.of(new Knot(0, 0), new Knot(length / 2, bows.get(i) * M), new Knot(length, 0)))));
            lines.add(new Directed<>(a, b, course));
        }
        return lines;
    }

    /** A smooth curve through {@code metres}, given as so many metres along and so many metres of value, in turn. */
    private Monotone curve(final double... metres) {
        final List<Knot> knots = new ArrayList<>();
        for (int i = 0; i < metres.length; i += 2) {
            knots.add(new Knot(metres[i] * M, metres[i + 1] * M));
        }
        return new Monotone(new Knots(knots));
    }

    /** The spot so many metres across and along. */
    private Spot spot(final double across, final double along) {
        return new Spot(across * M, along * M);
    }
}
