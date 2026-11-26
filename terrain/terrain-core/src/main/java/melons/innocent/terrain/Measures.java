package melons.innocent.terrain;

import com.example.fxsuite.existence.Features;
import com.example.fxsuite.existence.Made;
import com.example.fxsuite.existence.Terrains;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Taxonomy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * How a country's text writes what it measures, and reads it back: lengths in metres to the centimetre, a {@code m}
 * after them or not; plain numbers; {@code none} for a slope of no end; spots as {@code across,along}; a colour as
 * {@code #rrggbb}; a kind of what exists as its tree's name and its own, {@code features:pond}.
 */
final class Measures {

    private final List<Taxonomy> trees = List.of(Terrains.TREE, Features.TREE, Made.TREE);

    /** {@code "12.5m"} or {@code "12.5"} metres, in centimetres, exactly as written. */
    double centimetres(final String metres) {
        final String number = metres.endsWith("m") ? metres.substring(0, metres.length() - 1) : metres;
        try {
            return new BigDecimal(number).movePointRight(2).doubleValue();
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("not a measure in metres: " + metres, e);
        }
    }

    /** Centimetres in metres, to the centimetre, without trailing noughts. */
    String metres(final double centimetres) {
        final BigDecimal m = BigDecimal.valueOf(centimetres).movePointLeft(2).setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros();
        return m.signum() == 0 ? "0" : m.toPlainString();
    }

    /** A plain number, or {@code none}, a slope of no end. */
    double number(final String written) {
        return written.equals("none") ? Double.POSITIVE_INFINITY : Double.parseDouble(written);
    }

    /** {@code number} written plainly, or as {@code none} if it has no end. */
    String number(final double number) {
        return Double.isInfinite(number) ? "none" : BigDecimal.valueOf(number).stripTrailingZeros().toPlainString();
    }

    /** The spots {@code words} give, from the {@code from}th on, each {@code across,along} in metres. */
    List<Spot> spots(final String[] words, final int from) {
        final List<Spot> spots = new ArrayList<>();
        for (int i = from; i < words.length; i++) {
            final String[] parts = words[i].split(",");
            if (parts.length != 2) {
                throw new IllegalArgumentException("a spot is across,along in metres, not " + words[i]);
            }
            spots.add(new Spot(centimetres(parts[0]), centimetres(parts[1])));
        }
        return spots;
    }

    /** {@code spots} written, each after a space, {@code across,along} in metres. */
    String spots(final List<Spot> spots) {
        final StringBuilder out = new StringBuilder();
        spots.forEach(spot -> out.append(' ').append(metres(spot.x())).append(',').append(metres(spot.z())));
        return out.toString();
    }

    /** The colour {@code #rrggbb} says. */
    Hue hue(final String written) {
        return new Hue(written);
    }

    /** {@code hue} written {@code #rrggbb}. */
    String hue(final Hue hue) {
        return String.format(Locale.ROOT, "#%02x%02x%02x", Math.round(hue.red() * 255), Math.round(hue.green() * 255),
                Math.round(hue.blue() * 255));
    }

    /** The kind of what exists {@code tree:node} names. */
    Leaf<?> kind(final String written) {
        final String[] parts = written.split(":");
        for (final Taxonomy tree : trees) {
            if (parts.length == 2 && tree.name().equals(parts[0])) {
                if (tree.named(parts[1]).orElse(null) instanceof Leaf<?> leaf) {
                    return leaf;
                }
            }
        }
        throw new IllegalArgumentException("no kind of what exists is called " + written);
    }

    /** {@code kind} written {@code tree:node}. */
    String kind(final Leaf<?> kind) {
        for (final Taxonomy tree : trees) {
            if (tree.has(kind)) {
                return tree.name() + ":" + kind.name();
            }
        }
        throw new IllegalArgumentException(kind.title() + " is in none of the trees a country's text knows");
    }
}
