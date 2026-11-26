package melons.innocent.terrain.melon;

import com.example.fxsuite.math.graph.Edge;
import melons.innocent.terrain.BreakLine;
import melons.innocent.terrain.Contour;
import melons.innocent.terrain.Course;
import melons.innocent.terrain.Extent;
import melons.innocent.terrain.Loop;
import melons.innocent.terrain.Spot;
import melons.innocent.terrain.SpotHeight;
import melons.innocent.terrain.Surveyor;
import melons.innocent.terrain.Terrain;
import melons.innocent.terrain.Topography;
import melons.innocent.terrain.TopographyText;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The Melon country's topographic map drafted from its first draft, the landforms of {@link MelonCountry}, but for
 * Melon Harbor's deep, which is dredged, a made work and no part of the natural ground: surveyed a height every 10 m
 * over the country and a kilometre round it; a contour traced at each of its levels, kept to within a metre and a
 * half of where it was traced, none going round less than a fiftieth of a hectare; a spot height marked at the top of
 * every contour with nothing inside it, or the bottom; the river's thalweg and the range's crests along their courses
 * on the map, as high as the ground there. The plain, level at 12 m, is told by two contours both written 12 m, one
 * traced just under it round where it falls away, one just over it round where the ground rises from it, so the
 * ground between them is level. Written as text ({@link TopographyText}) where it is asked to be, the map's resource
 * by default; once written, the map stands on its own and is the country's ground.
 */
public final class MelonSurvey {

    private static final double M = 100;
    /** How far round the country it is surveyed, so contours running off it close well outside it. */
    private static final double ROUND = 1_000 * M;

    /** The levels traced, and the height each is written at, in metres, in turn. */
    private static final double[][] LEVELS = {{-28, -28}, {-25, -25}, {-20, -20}, {-15, -15}, {-10, -10}, {-5, -5},
        {-2, -2}, {0, 0}, {2, 2}, {5, 5}, {8, 8}, {11, 11}, {11.9, 12}, {12.1, 12}, {15, 15}, {20, 20}, {30, 30},
        {40, 40}, {50, 50}, {60, 60}, {70, 70}, {80, 80}, {100, 100}, {125, 125}, {150, 150}, {175, 175}, {200, 200},
        {250, 250}, {300, 300}, {350, 350}, {400, 400}, {450, 450}, {500, 500}, {550, 550}, {600, 600}, {650, 650},
        {700, 700}, {750, 750}};

    /** Surveyed and written to the file {@code args[0]} names, or to the map's resource. */
    public static void main(final String[] args) throws IOException {
        final Path to = Path.of(args.length > 0 ? args[0]
                : "terrain/melon-country/src/main/resources/melons/innocent/terrain/melon/melon-country.topo");
        new MelonSurvey().written(to);
    }

    /** The map surveyed and written to {@code to}. */
    void written(final Path to) throws IOException {
        final Terrain draft = MelonCountry.INSTANCE.terrain();
        final Terrain natural = new Terrain(draft.name(), draft.extent(), draft.map(),
                draft.landforms().stream().filter(form -> !form.name().equals("Melon Harbor")).toList());
        final Extent e = draft.extent();
        final Surveyor surveyor = new Surveyor(natural, new Extent(e.x0() - ROUND, e.z0() - ROUND, e.x1() + ROUND,
                e.z1() + ROUND), 10 * M);
        final List<Contour> contours = new ArrayList<>();
        final List<Double> traced = new ArrayList<>();
        for (final double[] level : LEVELS) {
            for (final Loop loop : surveyor.loops(level[0] * M, 1.5 * M, 200 * M * M)) {
                contours.add(new Contour(level[1] * M, loop));
                traced.add(level[0] * M);
            }
        }
        final List<SpotHeight> spots = new ArrayList<>();
        for (int i = 0; i < contours.size(); i++) {
            if (empty(contours, i)) {
                final Optional<SpotHeight> extreme = surveyor.extreme(contours.get(i).loop(), traced.get(i), 50);
                extreme.ifPresent(spots::add);
            }
        }
        final List<BreakLine> lines = List.of(
                line("the Melon River", BreakLine.Kind.THALWEG, MelonCountry.INSTANCE.river(), draft, natural),
                line("the Melon Range", BreakLine.Kind.CREST, MelonCountry.INSTANCE.range(), draft, natural),
                line("the Melon Range's east spur", BreakLine.Kind.CREST, MelonCountry.INSTANCE.eastSpur(), draft,
                        natural),
                line("the Melon Range's west ridge", BreakLine.Kind.CREST, MelonCountry.INSTANCE.westRidge(), draft,
                        natural));
        final Topography map = new Topography(draft.name(), e, -30 * M, contours, spots, lines);
        final String text = "# " + draft.name() + ": its natural ground as a topographic map, drafted by MelonSurvey"
                + "\n# from its first draft's landforms, without Melon Harbor's dredged deep. Metres.\n"
                + TopographyText.INSTANCE.text(map);
        Files.createDirectories(to.toAbsolutePath().getParent());
        Files.writeString(to, text.replace("\n", "\r\n"), StandardCharsets.UTF_8);
        System.out.println(contours.size() + " contours, " + spots.size() + " spot heights, " + lines.size()
                + " break lines, " + text.length() / 1024 + " KiB, written to " + to.toAbsolutePath());
    }

    /** Whether no other of {@code contours} lies inside contour {@code i}. */
    private boolean empty(final List<Contour> contours, final int i) {
        final Loop loop = contours.get(i).loop();
        for (int j = 0; j < contours.size(); j++) {
            final Loop other = contours.get(j).loop();
            final Spot first = other.spots().getFirst();
            if (j != i && other.area() < loop.area() && other.boxedIn(loop) && loop.inside(first.x(), first.z())) {
                return false;
            }
        }
        return true;
    }

    /** A break line along {@code spots} on {@code draft}'s map, a point every 25 m, as high as {@code ground}. */
    private BreakLine line(final String name, final BreakLine.Kind kind, final List<Spot> spots, final Terrain draft,
                           final Terrain ground) {
        final List<Spot> along = new ArrayList<>(List.of(spots.getFirst()));
        for (int i = 1; i < spots.size(); i++) {
            along.addAll(followed(spots.get(i - 1), spots.get(i), draft));
        }
        return new BreakLine(name, kind, along, along.stream().map(s -> ground.height(s.x(), s.z())).toList());
    }

    /** The points along the map's line from {@code from} to {@code to}, a point every 25 m, {@code to} the last. */
    private List<Spot> followed(final Spot from, final Spot to, final Terrain draft) {
        final Edge<Spot, Course> edge = draft.map().edges().leaving(from).stream()
                .filter(line -> line.beyond(from).equals(to)).findFirst().orElseThrow();
        final Spot a = edge.one();
        final Spot b = edge.other();
        final double length = a.to(b);
        final double ux = (b.x() - a.x()) / length;
        final double uz = (b.z() - a.z()) / length;
        final int steps = (int) Math.ceil(length / (25 * M));
        final List<Spot> points = new ArrayList<>();
        for (int s = 1; s < steps; s++) {
            final double d = length * s / steps;
            final double off = edge.label() instanceof Course.Bowed(final var offset) ? offset.held(d) : 0;
            points.add(new Spot(a.x() + ux * d - uz * off, a.z() + uz * d + ux * off));
        }
        final List<Spot> way = edge.one().equals(from) ? points : points.reversed();
        final List<Spot> out = new ArrayList<>(way);
        out.add(to);
        return out;
    }
}
