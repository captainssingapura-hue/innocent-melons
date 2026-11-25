package melons.innocent.glm.core;

import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A platform put together from its connections, at rest: its root where its own frame has it, then, one at a time, a
 * part not yet placed met at a connection of one that is, placed so its point there lies on its neighbour's; until
 * every part that can be reached is placed. Of the connections that could place a part next, the one the platform
 * lists first is walked, so the same platform always comes out the same. A connection one of a pair is walked on the
 * right and on the left, a part on the left placed as the mirror of the right one. Where parts meet again by a
 * connection not walked, how far apart their points are is told, not mended; and every part not reached is told.
 *
 * @param platform what is put together
 */
public record Assembly(Platform platform) {

    /** One side of a connection, walked: from a part on a side at its point, to another at its. */
    private record Edge(Connection connection, PartAt from, String at, PartAt to, String toPoint) {
    }

    /** It put together. */
    public Assembled assembled() {
        final List<Edge> edges = edges();
        final Map<PartAt, Vec3> placed = new LinkedHashMap<>();
        placed.put(new PartAt(platform.root(), Side.MIDDLE), new Vec3(0, 0, 0));
        final Set<Edge> walked = new HashSet<>();
        boolean more = true;
        while (more) {
            more = false;
            for (final Edge e : edges) {
                final boolean from = placed.containsKey(e.from());
                final boolean to = placed.containsKey(e.to());
                if (from != to) {
                    if (from) {
                        placed.put(e.to(), where(placed, e.from(), e.at()).minus(e.to().point(e.toPoint()).at()));
                    } else {
                        placed.put(e.from(), where(placed, e.to(), e.toPoint()).minus(e.from().point(e.at()).at()));
                    }
                    walked.add(e);
                    more = true;
                    break;
                }
            }
        }
        final List<Assembled.Gap> gaps = new ArrayList<>();
        for (final Edge e : edges) {
            if (!walked.contains(e) && placed.containsKey(e.from()) && placed.containsKey(e.to())) {
                gaps.add(new Assembled.Gap(e.connection().name() + (e.from().side() == Side.LEFT
                        || e.to().side() == Side.LEFT ? ", left" : e.connection().paired() ? ", right" : ""),
                        where(placed, e.from(), e.at()).minus(where(placed, e.to(), e.toPoint())).length()));
            }
        }
        final List<PartAt> all = new ArrayList<>();
        edges.forEach(e -> {
            all.add(e.from());
            all.add(e.to());
        });
        final List<PartAt> unreached = all.stream().distinct().filter(p -> !placed.containsKey(p)).toList();
        return new Assembled(placed, gaps, unreached);
    }

    /** Where {@code part}'s point {@code named} is, the part placed. */
    private Vec3 where(final Map<PartAt, Vec3> placed, final PartAt part, final String named) {
        return placed.get(part).plus(part.point(named).at());
    }

    /** Each connection's sides: once down the middle, or on the right and then on the left. */
    private List<Edge> edges() {
        final List<Edge> edges = new ArrayList<>();
        for (final Connection c : platform.connections()) {
            if (!c.paired()) {
                edges.add(new Edge(c, new PartAt(c.carrier(), Side.MIDDLE), c.at(), new PartAt(c.carried(),
                        Side.MIDDLE), c.to()));
            } else {
                for (final Side side : List.of(Side.RIGHT, Side.LEFT)) {
                    edges.add(new Edge(c, at(c.carrier(), side), named(c.carrier(), side, c.at()), at(c.carried(),
                            side), named(c.carried(), side, c.to())));
                }
            }
        }
        return edges;
    }

    private PartAt at(final Part part, final Side side) {
        return new PartAt(part, part.paired() ? side : Side.MIDDLE);
    }

    private String named(final Part part, final Side side, final String point) {
        return part.paired() ? point : side.named(point);
    }
}
