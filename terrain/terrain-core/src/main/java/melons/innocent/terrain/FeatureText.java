package melons.innocent.terrain;

import com.example.fxsuite.math.graph.Graph;
import melons.innocent.math.curves.CurveImpl;
import melons.innocent.math.curves.Knot;
import melons.innocent.math.curves.Knots;
import melons.innocent.math.curves.Line;
import melons.innocent.math.curves.Monotone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A land feature written as a line of a country's text, and read back: its sort, its label in quotes, its kind and
 * its colour, what measures it, and the line of the map it lies along, if any, its spots in turn, in metres:
 * <pre>
 * band "the meadows" kind=terrains:meadow hue=#8cbf5a low=-2m high=70m
 * steep "the bare crags" kind=terrains:rock hue=#8a8580 rise=0.7
 * area "the West Wood" kind=terrains:forest hue=#3a7a3c boundary -200,1500 500,1400 ... -200,1500
 * sea "Melon Bay" kind=features:bay hue=#2a6496 level=0m shore -3300,-950 ...
 * river "the river" kind=features:river hue=#3b7fc0 depth=0:1,20000:1 lowest=none spread=8.5m course ...
 * lake "the pond" kind=features:pond hue=#4a8fc8 level=3m shore ... (its first spot again at its end)
 * </pre>
 * A line the map bows is written as the points it is traced through, so read back it runs straight between them.
 */
final class FeatureText {

    private final Measures measures = new Measures();

    /** Whether {@code word} begins a feature's line. */
    boolean reads(final String word) {
        return List.of("band", "steep", "area", "sea", "river", "lake").contains(word);
    }

    /** The feature a line beginning {@code word} gives, {@code rest} the rest of it; its line added to {@code ways}. */
    Feature read(final String word, final String rest, final List<List<Spot>> ways) {
        final int open = rest.indexOf('"');
        final int close = rest.indexOf('"', open + 1);
        if (open < 0 || close < 0) {
            throw new IllegalArgumentException("a feature's label in quotes, not " + rest);
        }
        final String label = rest.substring(open + 1, close);
        final String[] words = rest.substring(close + 1).strip().split("\\s+");
        final Map<String, String> said = new HashMap<>();
        int at = 0;
        while (at < words.length && words[at].contains("=")) {
            said.put(words[at].substring(0, words[at].indexOf('=')), words[at].substring(words[at].indexOf('=') + 1));
            at++;
        }
        final List<Spot> way = measures.spots(words, at + 1);
        if (!way.isEmpty()) {
            ways.add(way);
        }
        return feature(word, label, said, way);
    }

    /** The feature of sort {@code word}, labelled, measured as {@code said} says, along {@code way}. */
    private Feature feature(final String word, final String label, final Map<String, String> said,
                            final List<Spot> way) {
        final var kind = measures.kind(must(said, "kind"));
        final Hue hue = measures.hue(must(said, "hue"));
        return switch (word) {
            case "band" -> new Band(label, kind, hue, measures.centimetres(must(said, "low")),
                    measures.centimetres(must(said, "high")));
            case "steep" -> new Steep(label, kind, hue, measures.number(must(said, "rise")));
            case "area" -> new Area(label, kind, hue, new Path(way));
            case "sea" -> new Sea(label, kind, hue, new Path(way), measures.centimetres(must(said, "level")));
            case "lake" -> new Lake(label, kind, hue, new Path(way), measures.centimetres(must(said, "level")));
            case "river" -> new Watercourse(label, kind, hue, new Path(way), depth(must(said, "depth")),
                    must(said, "lowest").equals("none") ? Double.NEGATIVE_INFINITY
                            : measures.centimetres(must(said, "lowest")),
                    measures.centimetres(must(said, "spread")));
            default -> throw new IllegalArgumentException("no feature is a " + word);
        };
    }

    /** What {@code said} says {@code key} is; refused if it says nothing. */
    private String must(final Map<String, String> said, final String key) {
        final String value = said.get(key);
        if (value == null) {
            throw new IllegalArgumentException("no " + key + "= given");
        }
        return value;
    }

    /** The curve {@code written} gives, {@code along:depth} in metres, with commas between, monotone through them. */
    private Monotone depth(final String written) {
        final List<Knot> knots = new ArrayList<>();
        for (final String pair : written.split(",")) {
            final String[] parts = pair.split(":");
            knots.add(new Knot(measures.centimetres(parts[0]), measures.centimetres(parts[1])));
        }
        return new Monotone(new Knots(knots));
    }

    /** {@code feature} written as its line, its line of {@code map} as the points it is traced through. */
    String text(final Feature feature, final Graph<Spot, Course> map) {
        final String head = " \"" + feature.label() + "\" kind=" + measures.kind(feature.kind()) + " hue="
                + measures.hue(feature.hue());
        return switch (feature) {
            case Band band -> "band" + head + " low=" + measures.metres(band.low()) + "m high="
                    + measures.metres(band.high()) + "m";
            case Steep steep -> "steep" + head + " rise=" + measures.number(steep.rise());
            case Area area -> "area" + head + " boundary" + way(area.boundary(), map);
            case Sea sea -> "sea" + head + " level=" + measures.metres(sea.level()) + "m shore" + way(sea.shore(), map);
            case Lake lake -> "lake" + head + " level=" + measures.metres(lake.level()) + "m shore"
                    + way(lake.shore(), map);
            case Watercourse river -> "river" + head + " depth=" + depth(river.depth()) + " lowest="
                    + (Double.isInfinite(river.lowest()) ? "none" : measures.metres(river.lowest()) + "m")
                    + " spread=" + measures.metres(river.spread()) + "m course" + way(river.course(), map);
        };
    }

    /** {@code path}'s points, as {@code map}'s lines trace it. */
    private String way(final Path path, final Graph<Spot, Course> map) {
        return measures.spots(new Traced(path, map).points());
    }

    /** {@code curve}'s knots, {@code along:depth} in metres, with commas between. */
    private String depth(final CurveImpl<?> curve) {
        final List<Knot> knots = switch (curve) {
            case Monotone monotone -> monotone.knots().listed();
            case Line line -> List.of(line.from(), line.to());
            default -> throw new IllegalArgumentException("a river's depth written as a monotone curve or a line");
        };
        final StringBuilder out = new StringBuilder();
        for (final Knot knot : knots) {
            out.append(out.isEmpty() ? "" : ",").append(measures.metres(knot.x())).append(':')
                    .append(measures.metres(knot.y()));
        }
        return out.toString();
    }
}
