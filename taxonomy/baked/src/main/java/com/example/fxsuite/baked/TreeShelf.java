package com.example.fxsuite.baked;

import com.example.fxsuite.taxonomy.Node;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;

/**
 * Things kept on disk in folders laid out as the trees are: under its root, a folder to each tree, and within it a
 * folder to each node from the tree's root down to the leaf, by their names; in a leaf's folder, a file to each
 * resolution it is kept at. So each thing kept has one place, and each place one thing:
 * {@code figures/figure/robot/shelled-robot/fine.baked}. Each file written whole, then put in place, so that no
 * half-written file is ever taken; one that cannot be read whole, or was made by other code, taken for none.
 */
public final class TreeShelf implements Shelf {

    private static final String KEPT = ".baked";

    private final Path root;
    private final PieceFile file = new PieceFile();

    /** The shelf in the folder {@code -Dbaked.dir} says, else in the user's {@code .cache/innocent-melons/baked}. */
    public TreeShelf() {
        this(Path.of(System.getProperty("baked.dir",
                Path.of(System.getProperty("user.home"), ".cache", "innocent-melons", "baked").toString())));
    }

    /** The shelf in the folder {@code root}. */
    public TreeShelf(final Path root) {
        this.root = root;
    }

    /** Where what is kept as {@code asked} is kept. */
    public Path where(final Asked asked) {
        Path at = root.resolve(asked.tree().name());
        for (final Node n : asked.leaf().lineage().reversed()) {
            at = at.resolve(n.name());
        }
        return at.resolve(asked.resolution().named() + KEPT);
    }

    @Override
    public Optional<List<Piece>> taken(final Asked asked) {
        final Path at = where(asked);
        if (!Files.isRegularFile(at)) {
            return Optional.empty();
        }
        try {
            return file.read(ByteBuffer.wrap(Files.readAllBytes(at)), asked);
        } catch (final IOException | RuntimeException unread) {
            return Optional.empty();
        }
    }

    @Override
    public boolean put(final Asked asked, final List<Piece> pieces) {
        final Path to = where(asked);
        Path part = null;
        try {
            Files.createDirectories(to.getParent());
            part = Files.createTempFile(to.getParent(), to.getFileName().toString(), ".part");
            try (FileChannel out = FileChannel.open(part, StandardOpenOption.WRITE)) {
                final ByteBuffer b = file.written(asked, pieces);
                while (b.hasRemaining()) {
                    out.write(b);
                }
            }
            Files.move(part, to, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (final IOException unwritten) {
            left(part);
            return false;
        }
    }

    /** The file {@code part} taken away, if there is one, half-written. */
    private void left(final Path part) {
        if (part == null) {
            return;
        }
        try {
            Files.deleteIfExists(part);
        } catch (final IOException stays) {
            // left where it is: a .part file is never taken for what is kept
        }
    }
}
