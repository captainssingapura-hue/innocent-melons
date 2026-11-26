package melons.innocent.bay.terrain;

import com.example.fxsuite.bay.land.MarinaBay;
import melons.innocent.terrain.Extent;
import melons.innocent.terrain.Spot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The bay's lines as its land has them ({@link MarinaBay#LINES}), read for the terrain, in centimetres: the world's
 * edges; the land's shores and its bounds at the world's edge, joined in turn into the land's loop; the basin's loop,
 * the bay, its channel and the river's mouth; and the lakes, each by the name the remark before it gives. Derived
 * from OpenStreetMap (map data (c) OpenStreetMap contributors, ODbL), and so licensed.
 *
 * @param text the lines, as written
 */
public record BayLines(String text) {

    private static final double M = 100;

    /** The bay's own lines. */
    public BayLines() {
        this(MarinaBay.LINES);
    }

    /** The world's extent. */
    public Extent world() {
        for (final String line : text.split("\\R")) {
            final String[] words = line.strip().split("\\s+");
            if (words[0].equals("world")) {
                return new Extent(Double.parseDouble(words[1]) * M, Double.parseDouble(words[3]) * M,
                        Double.parseDouble(words[2]) * M, Double.parseDouble(words[4]) * M);
            }
        }
        throw new IllegalStateException("the bay's lines give no world");
    }

    /** The land's loop: its shores and bounds joined in the order they are written, each joint once. */
    public List<Spot> land() {
        final List<Spot> loop = new ArrayList<>();
        for (final List<Spot> line : lines(List.of("land", "bound")).values()) {
            for (final Spot spot : line) {
                if (loop.isEmpty() || !loop.getLast().equals(spot)) {
                    loop.add(spot);
                }
            }
        }
        if (loop.getLast().equals(loop.getFirst())) {
            loop.removeLast();
        }
        return List.copyOf(loop);
    }

    /** The basin's loop: the bay, its channel and the river's mouth. */
    public List<Spot> basin() {
        return open(lines(List.of("basin")).values().iterator().next());
    }

    /** The lakes' loops, each by its name, or by where it is if it has none. */
    public Map<String, List<Spot>> lakes() {
        final Map<String, List<Spot>> lakes = new LinkedHashMap<>();
        lines(List.of("lake")).forEach((name, line) -> lakes.put(name, open(line)));
        return lakes;
    }

    /** {@code line} without its last spot, if that is its first again. */
    private List<Spot> open(final List<Spot> line) {
        return line.getLast().equals(line.getFirst()) ? List.copyOf(line.subList(0, line.size() - 1)) : line;
    }

    /** The lines of {@code kinds}, in the order written, each by the name the remark before it gives, or by where. */
    private Map<String, List<Spot>> lines(final List<String> kinds) {
        final Map<String, List<Spot>> lines = new LinkedHashMap<>();
        String name = null;
        for (final String raw : text.split("\\R")) {
            final String line = raw.strip();
            if (line.startsWith("#")) {
                name = line.substring(1).strip();
                continue;
            }
            final String[] words = line.split("\\s+");
            if (kinds.contains(words[0])) {
                final List<Spot> spots = new ArrayList<>();
                for (int i = 1; i < words.length; i++) {
                    final String[] xz = words[i].split(",");
                    spots.add(new Spot(Double.parseDouble(xz[0]) * M, Double.parseDouble(xz[1]) * M));
                }
                final String called = name == null || name.equals("a lake") || lines.containsKey(name)
                        ? words[0] + " at " + words[1] : name;
                lines.put(called, spots);
            }
            name = null;
        }
        return lines;
    }
}
