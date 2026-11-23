package com.example.fxsuite.life.body.soft;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

/**
 * Solves kept, so that one found once can be looked at again without solving it afresh: where each muscle's nodes
 * ended, and what was said of how it went, in a file of its own in a folder kept for them. Not an asset: only what
 * was worked out from the body, kept against working it out again, and named by all that went into it (the region,
 * how much of a muscle it takes in, how the solve went, and every node of every muscle at rest), so a body, a region
 * or a solve made otherwise is never given another's, and is solved afresh.
 */
public final class SolveStore {

    /** What a kept file begins with, and what it is: the second way of keeping one, with its fascia. */
    private static final int KIND = 0x534f4632;

    private final Path folder;

    /**
     * A solve kept: where each muscle's nodes ended, by the muscles' order, what was said of how it went, and the
     * fascia wrapping them, if one did.
     *
     * @param nodes  each muscle's nodes, three numbers each
     * @param said   what was said
     * @param sleeve the fascia, if any
     */
    public record Kept(List<float[]> nodes, String said, Optional<Fascia.Sleeve> sleeve) {

        /** {@code rest}, each muscle's lattice, with its nodes where they ended. */
        public List<Lattice> on(final List<Lattice> rest) {
            final List<Lattice> now = new ArrayList<>();
            for (int k = 0; k < rest.size(); k++) {
                final Lattice l = rest.get(k);
                now.add(new Lattice(l.cell(), nodes.get(k), l.tets(), l.fixed(), l.holder(), l.weights()));
            }
            return now;
        }
    }

    /** Solves kept in {@code folder}, made when one is first kept. */
    public SolveStore(final Path folder) {
        this.folder = folder;
    }

    /**
     * The name of the solve of {@code region}, taking in {@code share} of a muscle, solved as {@code settings} say,
     * of muscles whose elements at rest are {@code rest}.
     */
    public String name(final SoftRegion region, final double share, final SoftSolve.Settings settings,
                       final List<Lattice> rest) {
        try {
            final MessageDigest d = MessageDigest.getInstance("SHA-256");
            d.update((region + " " + share + " " + settings).getBytes(java.nio.charset.StandardCharsets.UTF_8));
            for (final Lattice l : rest) {
                final ByteBuffer b = ByteBuffer.allocate(4 * (l.nodes().length + l.tets().length) + 8);
                b.putDouble(l.cell());
                for (final float f : l.nodes()) {
                    b.putFloat(f);
                }
                for (final int t : l.tets()) {
                    b.putInt(t);
                }
                d.update(b.array());
            }
            return HexFormat.of().formatHex(d.digest()).substring(0, 32);
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is always there", e);
        }
    }

    /** {@code solved}, each muscle's lattice as it ended, kept as {@code name}, with {@code said}, {@code sleeve}. */
    public void keep(final String name, final List<Lattice> solved, final String said,
                     final Optional<Fascia.Sleeve> sleeve) throws IOException {
        Files.createDirectories(folder);
        final Path made = Files.createTempFile(folder, name, ".part");
        try (OutputStream o = Files.newOutputStream(made); DataOutputStream out = new DataOutputStream(o)) {
            out.writeInt(KIND);
            out.writeUTF(said);
            out.writeInt(solved.size());
            for (final Lattice l : solved) {
                out.writeInt(l.nodes().length);
                for (final float f : l.nodes()) {
                    out.writeFloat(f);
                }
            }
            out.writeBoolean(sleeve.isPresent());
            if (sleeve.isPresent()) {
                final Fascia.Sleeve s = sleeve.get();
                out.writeInt(s.rings());
                out.writeInt(s.round());
                for (final float f : s.points()) {
                    out.writeFloat(f);
                }
                for (final boolean b : s.filled()) {
                    out.writeBoolean(b);
                }
            }
        }
        Files.move(made, file(name), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    /** The solve kept as {@code name}, if one is, and is whole. */
    public Optional<Kept> kept(final String name) {
        final Path f = file(name);
        if (!Files.isRegularFile(f)) {
            return Optional.empty();
        }
        try (InputStream i = Files.newInputStream(f); DataInputStream in = new DataInputStream(i)) {
            if (in.readInt() != KIND) {
                return Optional.empty();
            }
            final String said = in.readUTF();
            final List<float[]> nodes = new ArrayList<>();
            final int count = in.readInt();
            for (int k = 0; k < count; k++) {
                final float[] n = new float[in.readInt()];
                for (int j = 0; j < n.length; j++) {
                    n[j] = in.readFloat();
                }
                nodes.add(n);
            }
            Optional<Fascia.Sleeve> sleeve = Optional.empty();
            if (in.readBoolean()) {
                final int rings = in.readInt();
                final int round = in.readInt();
                final float[] points = new float[3 * rings * round];
                for (int j = 0; j < points.length; j++) {
                    points[j] = in.readFloat();
                }
                final boolean[] filled = new boolean[rings];
                for (int j = 0; j < rings; j++) {
                    filled[j] = in.readBoolean();
                }
                sleeve = Optional.of(new Fascia.Sleeve(rings, round, points, filled));
            }
            return Optional.of(new Kept(nodes, said, sleeve));
        } catch (final IOException e) {
            return Optional.empty();
        }
    }

    private Path file(final String name) {
        return folder.resolve(name + ".solve");
    }
}
