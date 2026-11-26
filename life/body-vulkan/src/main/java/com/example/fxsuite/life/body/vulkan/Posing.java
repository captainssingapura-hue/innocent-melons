package com.example.fxsuite.life.body.vulkan;

import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.flesh.Flesh;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The body's skin and muscles, and what it wears of cloth, posed away from the screen, a stopped body at a time:
 * each of their points moved as the body is held, by the bones it follows ({@link Flesh#posed}), and cut into the
 * pieces the card draws. Only the last asked is posed: one asked while another is being posed takes its place, and
 * one asked before is let go. Closed, the worker stops.
 */
public final class Posing implements AutoCloseable {

    /**
     * The skin and muscles posed for {@code still}, as pieces to be drawn.
     *
     * @param still   where the body stands, stopped
     * @param flesh   its skin and muscles, posed
     * @param clothes what it wears of cloth, posed
     * @param ms      how long it took, thousandths of a second
     */
    public record Posed(Motion.Still still, List<BodyPieces.Piece> flesh, List<BodyPieces.Cloth> clothes,
                        long ms) {
    }

    private final BodyMaker.Made body;
    private final BodyPieces pieces = new BodyPieces();
    private final AtomicReference<Motion.Still> asked = new AtomicReference<>();
    private final AtomicReference<Posed> done = new AtomicReference<>();
    private final ExecutorService worker = Executors.newSingleThreadExecutor(r -> {
        final Thread t = new Thread(r, "posing");
        t.setDaemon(true);
        return t;
    });

    /** Posing {@code body}. */
    public Posing(final BodyMaker.Made body) {
        this.body = body;
    }

    /** The skin and muscles posed for {@code still}, the body held as {@code rig} holds it: begun, not waited for. */
    public void ask(final Motion.Still still, final Rig rig) {
        asked.set(still);
        worker.submit(() -> {
            if (!still.equals(asked.get())) {
                return;
            }
            final long began = System.nanoTime();
            final List<BodyPieces.Piece> flesh = new ArrayList<>(pieces.skin(body.skin(), posed(body.skin(), rig)));
            flesh.addAll(pieces.muscles(body.muscles(), posed(body.muscles().flesh(), rig)));
            final List<BodyPieces.Cloth> clothes = body.clothes().stream()
                    .map(g -> pieces.cloth(g, posed(g.cloth(), rig))).toList();
            done.set(new Posed(still, flesh, clothes, (System.nanoTime() - began) / 1_000_000));
        });
    }

    /** The last posed, if any has been since this was last asked; taken. */
    public Optional<Posed> taken() {
        return Optional.ofNullable(done.getAndSet(null));
    }

    /** {@code f}'s points, held as {@code rig} holds the body, three numbers each. */
    private float[] posed(final Flesh f, final Rig rig) {
        final float[] at = new float[3 * f.size()];
        f.posed(rig, at, new float[at.length]);
        return at;
    }

    @Override
    public void close() {
        worker.shutdownNow();
    }
}
