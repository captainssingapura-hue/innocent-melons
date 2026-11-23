package melons.innocent.terrain;

import com.example.fxsuite.math.graph.Directed;
import com.example.fxsuite.math.graph.Edge;
import com.example.fxsuite.math.graph.Graph;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A whole country written as one text, in metres, and read back: its natural ground, a topographic map as
 * {@link TopographyText} writes one; the works made over it, in the order made; and its land features, in the order
 * laid ({@link FeatureText}), the lines of its map they lie along written with them. Blank lines are passed over, and
 * {@code #} at a line's start or after a space starts a remark, so a colour's {@code #} is not one. A work is its
 * name, its level, how far in its bank is, how steep its cut and its fill
 * ({@code none}, a work that never reaches out that way), whether its footprint is what lies outside its loops, and
 * its loops, each its spots in turn:
 * <pre>
 * work "the bay, dredged" level=-8m bank=12m cut=0.6667 fill=none outside loop -476.2,-2000 ... loop -2905.6,-71.8 ...
 * </pre>
 * What a country's text gives is what the benches draw and the simulation will stand on: its features over its ground,
 * the works over its map. So a country made anywhere, in any project, is drawn by writing it out and reading it here.
 */
public record LandText() implements StatelessFunctionalObject {

    public static final LandText INSTANCE = new LandText();

    /** The country {@code text} writes: its features over its ground, along its map's lines. */
    public LandFeatures parse(final String text) {
        final FeatureText features = new FeatureText();
        final StringBuilder topography = new StringBuilder();
        final List<Work> works = new ArrayList<>();
        final List<Feature> laid = new ArrayList<>();
        final List<List<Spot>> ways = new ArrayList<>();
        int number = 0;
        for (final String raw : text.split("\\R")) {
            number++;
            final String line = unremarked(raw).strip();
            final String[] words = line.split("\\s+", 2);
            final String rest = words.length > 1 ? words[1] : "";
            final boolean own = words[0].equals("work") || features.reads(words[0]);
            topography.append(own ? "" : raw).append('\n');
            try {
                if (words[0].equals("work")) {
                    works.add(work(rest));
                } else if (features.reads(words[0])) {
                    laid.add(features.read(words[0], rest, ways));
                }
            } catch (final RuntimeException e) {
                throw new IllegalArgumentException("line " + number + ": " + e.getMessage(), e);
            }
        }
        final Topography base = TopographyText.INSTANCE.parse(topography.toString());
        final Ground ground = works.isEmpty() ? base : new Worked(base, works);
        return new LandFeatures(base.name(), ground, map(ways), laid);
    }

    /** {@code raw} without its remark: from a {@code #} at its start or after a space, to its end. */
    private String unremarked(final String raw) {
        for (int i = 0; i < raw.length(); i++) {
            if (raw.charAt(i) == '#' && (i == 0 || Character.isWhitespace(raw.charAt(i - 1)))) {
                return raw.substring(0, i);
            }
        }
        return raw;
    }

    /** {@code country} written as text, that {@link #parse} reads back: its ground a map, and any works over it. */
    public String text(final LandFeatures country) {
        final Topography base;
        final List<Work> works;
        if (country.ground() instanceof Worked(final Topography found, final List<Work> made)) {
            base = found;
            works = made;
        } else if (country.ground() instanceof Topography found) {
            base = found;
            works = List.of();
        } else {
            throw new IllegalArgumentException(country.name() + ": only a topographic map, and works over it, is"
                    + " written as a country's text");
        }
        final StringBuilder out = new StringBuilder(TopographyText.INSTANCE.text(base));
        works.forEach(work -> out.append(work(work)).append('\n'));
        final FeatureText features = new FeatureText();
        country.laid().forEach(feature -> out.append(features.text(feature, country.map())).append('\n'));
        return out.toString();
    }

    /** The work {@code rest} gives: its name, its measures, whether outside, and its loops. */
    private Work work(final String rest) {
        final Measures measures = new Measures();
        final int open = rest.indexOf('"');
        final int close = rest.indexOf('"', open + 1);
        final String[] words = rest.substring(close + 1).strip().split("\\s+");
        double level = Double.NaN;
        double bank = 0;
        double cut = Double.POSITIVE_INFINITY;
        double fill = Double.POSITIVE_INFINITY;
        boolean outside = false;
        final List<List<Spot>> rings = new ArrayList<>();
        for (final String word : words) {
            if (word.startsWith("level=")) {
                level = measures.centimetres(word.substring(6));
            } else if (word.startsWith("bank=")) {
                bank = measures.centimetres(word.substring(5));
            } else if (word.startsWith("cut=")) {
                cut = measures.number(word.substring(4));
            } else if (word.startsWith("fill=")) {
                fill = measures.number(word.substring(5));
            } else if (word.equals("outside")) {
                outside = true;
            } else if (word.equals("loop")) {
                rings.add(new ArrayList<>());
            } else if (!rings.isEmpty()) {
                rings.getLast().addAll(measures.spots(new String[] {word}, 0));
            }
        }
        final List<Loop> loops = rings.stream().map(Loop::new).toList();
        return new Work(rest.substring(open + 1, close), new Region(loops, outside), level, bank, cut, fill);
    }

    /** {@code work} written as its line. */
    private String work(final Work work) {
        final Measures measures = new Measures();
        final StringBuilder out = new StringBuilder("work \"").append(work.name()).append("\" level=")
                .append(measures.metres(work.level())).append("m bank=").append(measures.metres(work.bank()))
                .append("m cut=").append(measures.number(work.cut())).append(" fill=")
                .append(measures.number(work.fill())).append(work.footprint().outside() ? " outside" : "");
        work.footprint().loops().forEach(loop -> out.append(" loop").append(measures.spots(loop.spots())));
        return out.toString();
    }

    /** The map whose lines join each of {@code ways}' spots to the next, straight, each line once. */
    private Graph<Spot, Course> map(final List<List<Spot>> ways) {
        final Set<Spot> spots = new LinkedHashSet<>();
        final Set<List<Spot>> joined = new LinkedHashSet<>();
        final List<Edge<Spot, Course>> lines = new ArrayList<>();
        for (final List<Spot> way : ways) {
            spots.addAll(way);
            for (int i = 1; i < way.size(); i++) {
                if (joined.add(List.of(way.get(i - 1), way.get(i)))) {
                    lines.add(new Directed<>(way.get(i - 1), way.get(i), new Course.Straight()));
                }
            }
        }
        return new Graph<>(List.copyOf(spots), lines);
    }
}
