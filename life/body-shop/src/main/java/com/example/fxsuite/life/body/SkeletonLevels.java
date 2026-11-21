package com.example.fxsuite.life.body;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The game's skeleton at each level of detail asked for ({@link Detail}), part by part ({@link PartBones}): each level
 * found once, its parts at once on as many threads as there are, and kept, so that going back to a level is at once.
 * Only the right side's parts and those down the middle; a left part is its right's in a mirror. Safe to ask from any
 * thread, a level asked for twice at once found twice.
 */
public final class SkeletonLevels {

    private final Map<Detail, Map<Part, List<PartBones.Piece>>> made = new ConcurrentHashMap<>();

    /** Every part's bones at {@code detail}: found now, if not yet. */
    public Map<Part, List<PartBones.Piece>> at(final Detail detail) {
        final Map<Part, List<PartBones.Piece>> kept = made.get(detail);
        if (kept != null) {
            return kept;
        }
        final Map<Part, List<PartBones.Piece>> found = new ConcurrentHashMap<>();
        java.util.Arrays.stream(Part.values()).parallel().filter(p -> p.side() >= 0)
                .forEach(p -> found.put(p, PartBones.INSTANCE.pieces(p, detail)));
        final Map<Part, List<PartBones.Piece>> level = new EnumMap<>(found);
        made.putIfAbsent(detail, level);
        return made.get(detail);
    }

    /** {@code p}'s bones at {@code detail}: a left part's its right's. */
    public List<PartBones.Piece> of(final Part p, final Detail detail) {
        return at(detail).get(p.side() < 0 ? p.other() : p);
    }

    /** How many triangles the whole skeleton has at {@code detail}, both sides. */
    public long triangles(final Detail detail) {
        long n = 0;
        for (final Map.Entry<Part, List<PartBones.Piece>> e : at(detail).entrySet()) {
            for (final PartBones.Piece piece : e.getValue()) {
                n += (long) piece.mesh().triangleCount() * (e.getKey().side() > 0 ? 2 : 1);
            }
        }
        return n;
    }
}
