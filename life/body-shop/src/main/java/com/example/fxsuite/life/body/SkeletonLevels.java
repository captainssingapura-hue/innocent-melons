package com.example.fxsuite.life.body;

import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.life.body.flesh.Fleshing;
import com.example.fxsuite.life.body.flesh.Physique;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The game's skeleton at each level of detail asked for ({@link Detail}), part by part ({@link PartBones}); or a body
 * of a physique, its flesh one surface over its bones ({@link Physique}, {@link Fleshing}), its bones where it has no
 * flesh, a man's or a woman's: each level found once, its parts at once on as many threads as there are, and kept,
 * so that going back to a level is at once. Only the right side's parts and those down the middle; a left part is its
 * right's in a mirror. Safe to ask from any thread, a level asked for twice at once found twice.
 */
public final class SkeletonLevels {

    /** A level: how finely, whose body (none, the bare skeleton), how defined its muscles. */
    private record Level(Detail detail, Physique physique, double definition) {
    }

    private final Map<Level, Map<Part, List<PartBones.Piece>>> made = new ConcurrentHashMap<>();
    private final Map<Level, Flesh> skins = new ConcurrentHashMap<>();

    /** Every part's bones at {@code detail}: found now, if not yet. */
    public Map<Part, List<PartBones.Piece>> at(final Detail detail) {
        return at(detail, null);
    }

    /** Every part at {@code detail}, the bare skeleton's or {@code physique}'s, if any: found now if not yet. */
    public Map<Part, List<PartBones.Piece>> at(final Detail detail, final Physique physique) {
        final Level level = new Level(detail, physique, 0);
        final Map<Part, List<PartBones.Piece>> kept = made.get(level);
        if (kept != null) {
            return kept;
        }
        final Map<Part, List<PartBones.Piece>> found = new ConcurrentHashMap<>();
        java.util.Arrays.stream(Part.values()).parallel().filter(p -> p.side() >= 0)
                .forEach(p -> found.put(p, physique == null ? PartBones.INSTANCE.pieces(p, detail)
                        : physique.hidden(p) ? List.<PartBones.Piece>of()
                        : PartBones.INSTANCE.pieces(p, detail, physique.female())));
        made.putIfAbsent(level, new EnumMap<>(found));
        return made.get(level);
    }

    /** {@code physique}'s flesh, one surface, at {@code detail}: found now, if not yet. */
    public Flesh flesh(final Detail detail, final Physique physique) {
        return flesh(detail, physique, physique.torso().definition());
    }

    /** {@code physique}'s flesh at {@code detail}, its muscles as defined as {@code definition} says. */
    public Flesh flesh(final Detail detail, final Physique physique, final double definition) {
        final Level level = new Level(detail, physique, definition);
        final Flesh kept = skins.get(level);
        if (kept != null) {
            return kept;
        }
        skins.putIfAbsent(level, Fleshing.INSTANCE.make(physique, detail, definition));
        return skins.get(level);
    }

    /** {@code p}'s bones at {@code detail}: a left part's its right's. */
    public List<PartBones.Piece> of(final Part p, final Detail detail) {
        return of(p, detail, null);
    }

    /** {@code p} at {@code detail}, the bare skeleton's or {@code physique}'s: a left part's its right's. */
    public List<PartBones.Piece> of(final Part p, final Detail detail, final Physique physique) {
        return at(detail, physique).get(p.side() < 0 ? p.other() : p);
    }

    /** How many triangles the whole skeleton has at {@code detail}, both sides. */
    public long triangles(final Detail detail) {
        return triangles(detail, null);
    }

    /** How many triangles the whole body has at {@code detail}, the bare skeleton or {@code physique}, both sides. */
    public long triangles(final Detail detail, final Physique physique) {
        long n = 0;
        for (final Map.Entry<Part, List<PartBones.Piece>> e : at(detail, physique).entrySet()) {
            for (final PartBones.Piece piece : e.getValue()) {
                n += (long) piece.mesh().triangleCount() * (e.getKey().side() > 0 ? 2 : 1);
            }
        }
        if (physique != null) {
            n += flesh(detail, physique).triangleCount();
        }
        return n;
    }
}
