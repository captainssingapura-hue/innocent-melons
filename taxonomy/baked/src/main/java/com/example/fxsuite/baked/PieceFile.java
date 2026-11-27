package com.example.fxsuite.baked;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Pieces as they are written to be kept: a heading saying what they were kept as (what made them, the tree, the leaf,
 * how finely), then each piece, what is said of it, its points and its triangles, the numbers as they lie. Read
 * back only if it is all there and its heading says what is asked.
 */
final class PieceFile {

    private static final int MAGIC = 0x4241_4B44;
    private static final int VERSION = 1;
    private static final int WORD = 4;

    /** {@code pieces}, kept as {@code asked}, as the bytes to write, ready to be read from their start. */
    ByteBuffer written(final Asked asked, final List<Piece> pieces) {
        final List<byte[]> heading = heading(asked).stream().map(s -> s.getBytes(StandardCharsets.UTF_8)).toList();
        long size = 3L * WORD;
        for (final byte[] h : heading) {
            size += WORD + h.length;
        }
        for (final Piece p : pieces) {
            size += 3L * WORD + (long) WORD * (p.points().length + p.triangles().length);
            for (final String l : p.labels()) {
                size += WORD + l.getBytes(StandardCharsets.UTF_8).length;
            }
        }
        if (size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("too much to keep in one file: " + size + " bytes");
        }
        final ByteBuffer b = ByteBuffer.allocate((int) size);
        b.putInt(MAGIC).putInt(VERSION);
        heading.forEach(h -> b.putInt(h.length).put(h));
        b.putInt(pieces.size());
        for (final Piece p : pieces) {
            b.putInt(p.labels().size());
            p.labels().forEach(l -> {
                final byte[] w = l.getBytes(StandardCharsets.UTF_8);
                b.putInt(w.length).put(w);
            });
            b.putInt(p.points().length).putInt(p.triangles().length);
            b.asFloatBuffer().put(p.points());
            b.position(b.position() + WORD * p.points().length);
            b.asIntBuffer().put(p.triangles());
            b.position(b.position() + WORD * p.triangles().length);
        }
        return b.flip();
    }

    /** The pieces {@code b} holds, if it holds them whole, kept as {@code asked}; none otherwise. */
    Optional<List<Piece>> read(final ByteBuffer b, final Asked asked) {
        if (b.remaining() < 2 * WORD || b.getInt() != MAGIC || b.getInt() != VERSION) {
            return Optional.empty();
        }
        for (final String h : heading(asked)) {
            if (!h.equals(word(b))) {
                return Optional.empty();
            }
        }
        final int count = b.getInt();
        final List<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            final int said = b.getInt();
            final List<String> labels = new ArrayList<>();
            for (int l = 0; l < said; l++) {
                labels.add(word(b));
            }
            final int points = b.getInt();
            final int triangles = b.getInt();
            if (points < 0 || triangles < 0 || (long) WORD * (points + (long) triangles) > b.remaining()) {
                return Optional.empty();
            }
            final float[] p = new float[points];
            b.asFloatBuffer().get(p);
            b.position(b.position() + WORD * points);
            final int[] t = new int[triangles];
            b.asIntBuffer().get(t);
            b.position(b.position() + WORD * triangles);
            pieces.add(new Piece(labels, p, t));
        }
        return b.hasRemaining() ? Optional.empty() : Optional.of(List.copyOf(pieces));
    }

    /** What a file kept as {@code asked} says first: what made it, its tree, its leaf, how finely. */
    private List<String> heading(final Asked asked) {
        return List.of(asked.madeBy(), asked.tree().name(), asked.leaf().title(), asked.resolution().named());
    }

    /** The next word of {@code b}: how long it is, then it. */
    private String word(final ByteBuffer b) {
        final int length = b.getInt();
        if (length < 0 || length > b.remaining()) {
            throw new IllegalArgumentException("a word " + length + " bytes long, with " + b.remaining() + " left");
        }
        final byte[] w = new byte[length];
        b.get(w);
        return new String(w, StandardCharsets.UTF_8);
    }
}
