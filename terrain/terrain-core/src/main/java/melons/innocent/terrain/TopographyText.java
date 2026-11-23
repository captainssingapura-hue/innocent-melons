package melons.innocent.terrain;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * A topographic map written as text, in metres, a line to each thing on it, and read back. Blank lines are passed
 * over, and {@code #} starts a remark that runs to the line's end. First the map itself, then its contours, spot
 * heights and break lines, in any order:
 * <pre>
 * topography "the Melon country" x=-3000..3000m z=-2600..3400m datum=-30m
 * contour 12m 120,-450 130.5,-452 ...          # its height, then the spots round it, across,along
 * spot 760m 2100,2600                          # its height, then where
 * crest "the Melon Range" 3300,3100,420 ...    # its name, then its spots, across,along,height
 * thalweg "the Melon River" 1500,3800,215 ...
 * </pre>
 * Measures are written to the centimetre.
 */
public record TopographyText() implements StatelessFunctionalObject {

    public static final TopographyText INSTANCE = new TopographyText();

    private static final double M = 100;

    /** The map {@code text} writes. */
    public Topography parse(final String text) {
        Topography map = null;
        final List<Contour> contours = new ArrayList<>();
        final List<SpotHeight> spots = new ArrayList<>();
        final List<BreakLine> lines = new ArrayList<>();
        int number = 0;
        for (final String raw : text.split("\\R")) {
            number++;
            final int remark = raw.indexOf('#');
            final String line = (remark < 0 ? raw : raw.substring(0, remark)).strip();
            if (line.isEmpty()) {
                continue;
            }
            try {
                final String[] words = line.split("\\s+", 2);
                final String rest = words.length > 1 ? words[1] : "";
                switch (words[0]) {
                    case "topography" -> map = header(rest);
                    case "contour" -> contours.add(contour(rest));
                    case "spot" -> spots.add(spot(rest));
                    case "crest", "thalweg" -> lines.add(breakLine(words[0], rest));
                    default -> throw new IllegalArgumentException("nothing on a map is a " + words[0]);
                }
            } catch (final RuntimeException e) {
                throw new IllegalArgumentException("line " + number + ": " + e.getMessage(), e);
            }
        }
        if (map == null) {
            throw new IllegalArgumentException("a map begins: topography \"its name\" x=..m z=..m datum=..m");
        }
        return new Topography(map.name(), map.extent(), map.datum(), contours, spots, lines);
    }

    /** The map {@code rest} names and measures, with nothing on it yet. */
    private Topography header(final String rest) {
        final String[] measures = afterQuote(rest).split("\\s+");
        final double[] across = span(value(measures, "x="));
        final double[] along = span(value(measures, "z="));
        return new Topography(quoted(rest), new Extent(across[0], along[0], across[1], along[1]),
                centimetres(value(measures, "datum=")), List.of(), List.of(), List.of());
    }

    /** The contour {@code rest} gives: its height, then the spots round it. */
    private Contour contour(final String rest) {
        final String[] parts = rest.split("\\s+");
        final List<Spot> round = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            final double[] at = numbers(parts[i], 2);
            round.add(new Spot(at[0], at[1]));
        }
        return new Contour(centimetres(parts[0]), new Loop(round));
    }

    /** The spot height {@code rest} gives: its height, then where. */
    private SpotHeight spot(final String rest) {
        final String[] parts = rest.split("\\s+");
        final double[] at = numbers(parts[1], 2);
        return new SpotHeight(new Spot(at[0], at[1]), centimetres(parts[0]));
    }

    /** {@code map} written as text, that {@link #parse} reads back. */
    public String text(final Topography map) {
        final Extent e = map.extent();
        final StringBuilder out = new StringBuilder("topography \"").append(map.name()).append("\" x=")
                .append(metres(e.x0())).append("..").append(metres(e.x1())).append("m z=").append(metres(e.z0()))
                .append("..").append(metres(e.z1())).append("m datum=").append(metres(map.datum())).append("m\n");
        for (final Contour contour : map.contours()) {
            out.append("contour ").append(metres(contour.height())).append('m');
            contour.loop().spots().forEach(s -> out.append(' ').append(metres(s.x())).append(',')
                    .append(metres(s.z())));
            out.append('\n');
        }
        for (final SpotHeight spot : map.spotHeights()) {
            out.append("spot ").append(metres(spot.height())).append("m ").append(metres(spot.at().x())).append(',')
                    .append(metres(spot.at().z())).append('\n');
        }
        for (final BreakLine line : map.breakLines()) {
            out.append(line.kind().name().toLowerCase()).append(" \"").append(line.name()).append('"');
            for (int i = 0; i < line.spots().size(); i++) {
                out.append(' ').append(metres(line.spots().get(i).x())).append(',')
                        .append(metres(line.spots().get(i).z())).append(',').append(metres(line.heights().get(i)));
            }
            out.append('\n');
        }
        return out.toString();
    }

    /** A break line of {@code kind}, {@code rest} its name and its spots. */
    private BreakLine breakLine(final String kind, final String rest) {
        final List<Spot> spots = new ArrayList<>();
        final List<Double> heights = new ArrayList<>();
        for (final String word : afterQuote(rest).split("\\s+")) {
            final double[] at = numbers(word, 3);
            spots.add(new Spot(at[0], at[1]));
            heights.add(at[2]);
        }
        return new BreakLine(quoted(rest), BreakLine.Kind.valueOf(kind.toUpperCase()), spots, heights);
    }

    /** What stands between the first two quotes in {@code words}. */
    private String quoted(final String words) {
        final int open = words.indexOf('"');
        final int close = words.indexOf('"', open + 1);
        if (open < 0 || close < 0) {
            throw new IllegalArgumentException("a name in quotes, not " + words);
        }
        return words.substring(open + 1, close);
    }

    /** What follows the second quote in {@code words}, stripped. */
    private String afterQuote(final String words) {
        return words.substring(words.indexOf('"', words.indexOf('"') + 1) + 1).strip();
    }

    /** The value of the measure {@code key} among {@code measures}. */
    private String value(final String[] measures, final String key) {
        for (final String measure : measures) {
            if (measure.startsWith(key)) {
                return measure.substring(key.length());
            }
        }
        throw new IllegalArgumentException("no " + key + " given");
    }

    /** {@code "-3000..3000m"}: from and to, in centimetres. */
    private double[] span(final String word) {
        final String[] ends = (word.endsWith("m") ? word.substring(0, word.length() - 1) : word).split("\\.\\.");
        if (ends.length != 2) {
            throw new IllegalArgumentException("a span is from..to in metres, not " + word);
        }
        return new double[] {centimetres(ends[0]), centimetres(ends[1])};
    }

    /** {@code count} measures in metres, written with commas between, in centimetres. */
    private double[] numbers(final String word, final int count) {
        final String[] parts = word.split(",");
        if (parts.length != count) {
            throw new IllegalArgumentException(count + " measures with commas between, not " + word);
        }
        final double[] out = new double[count];
        for (int i = 0; i < count; i++) {
            out[i] = centimetres(parts[i]);
        }
        return out;
    }

    /** {@code "12.5m"} or {@code "12.5"} metres, in centimetres, exactly as written. */
    private double centimetres(final String metres) {
        final String number = metres.endsWith("m") ? metres.substring(0, metres.length() - 1) : metres;
        try {
            return new BigDecimal(number).movePointRight(2).doubleValue();
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("not a measure in metres: " + metres, e);
        }
    }

    /** Centimetres in metres, to the centimetre, without trailing noughts. */
    private String metres(final double centimetres) {
        final BigDecimal m = BigDecimal.valueOf(centimetres).movePointLeft(2).setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros();
        return m.signum() == 0 ? "0" : m.toPlainString();
    }
}
