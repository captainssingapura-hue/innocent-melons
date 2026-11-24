package melons.innocent.plants.bench;

import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Looked;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Tint;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.plants.tree.Kind;
import melons.innocent.plants.walk.Barks;
import melons.innocent.plants.walk.Baked;
import melons.innocent.plants.walk.WoodDrawn;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * What is on show as one mesh, on the card: the whole of it merged ahead ({@link Baked}), its wood one mesh for each
 * kind of tree, drawn in its bark, and at a coarser level its crowns' haze one mesh for each colour and level of
 * hiding, drawn after all else. The merging is done away from the card's thread, on a worker of its own, so frames
 * go on meanwhile; what is made is sent to the card between frames, and what it replaces let go. Closed, all of it is
 * gone from the card and the worker stopped.
 */
final class OneMesh implements AutoCloseable {

    /** How much the crowns' haze hides, each clump at the nearest of these, as a wood's are. */
    private final List<Double> levels = List.of(0.01, 0.02, 0.04, 0.08, 0.16, 0.32, 0.64, 0.9);

    /** One mesh made: what of, what it came to, or why none could be made. */
    private record Made(OnShow asked, Baked.Whole whole, String failed) {
    }

    /** A haze on the card, with its colour and the look of its level of hiding. */
    private record Hazed(Shaped shape, Vec3 hue, Looked look) {
    }

    private final Card card;
    private final Laid laid;
    private final Baked baked;
    private final Map<Kind, Looked> barks = new EnumMap<>(Kind.class);
    private final Map<Double, Looked> hazes = new LinkedHashMap<>();
    private final Map<Kind, Shaped> wood = new EnumMap<>(Kind.class);
    private final List<Hazed> hazed = new ArrayList<>();
    private final ExecutorService worker = Executors.newSingleThreadExecutor(work -> {
        final Thread thread = new Thread(work, "the one mesh's worker");
        thread.setDaemon(true);
        return thread;
    });
    private final AtomicReference<Made> made = new AtomicReference<>();
    private OnShow asked;
    private OnShow sent;
    private String failed = "";
    private long triangles;

    /** Ready to make {@code grown} trees into one mesh, on {@code card}, their layouts as {@code laid} says. */
    OneMesh(final Card card, final Laid laid, final List<WoodDrawn.Grown> grown) {
        this.card = card;
        this.laid = laid;
        this.baked = new Baked(grown, levels);
        try {
            for (final Kind kind : Kind.values()) {
                barks.put(kind, new Looked(card, new Look(laid, new Shades().of(new Barks().of(kind)))));
            }
            for (final double hides : levels) {
                final Surface haze = new Surface(new Tint().colour(), List.of(), new Colour("#000000"), 1);
                hazes.put(hides, new Looked(card, new Look(laid, haze.clear(1 - hides).hazy())));
            }
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /** {@code shown} as one mesh, if that is how it is to be drawn: set making, unless it is made or being made. */
    void wanted(final OnShow shown) {
        if (shown.joined() != Joined.AS_ONE) {
            return;
        }
        // where it stands and what stands beside it change nothing of the mesh
        final OnShow now = new OnShow(shown.subject(), shown.kinds(), java.util.Optional.of(shown.level()), false,
                shown.rows(), Joined.AS_ONE, shown.outline(), shown.across());
        if (now.equals(asked)) {
            return;
        }
        asked = now;
        worker.submit(() -> {
            try {
                made.set(new Made(now, baked.whole(now.merged(), now.level()), ""));
            } catch (final RuntimeException | OutOfMemoryError refused) {
                made.set(new Made(now, null, String.valueOf(refused.getMessage())));
            }
        });
    }

    /** What has been made since, sent to the card, if it is what is asked for: between frames, never during one. */
    void readied() {
        final Made done = made.getAndSet(null);
        if (done == null || !done.asked().equals(asked)) {
            return;
        }
        let();
        failed = done.failed();
        triangles = 0;
        if (done.whole() != null) {
            done.whole().wood().forEach((kind, mesh) -> {
                wood.put(kind, new Shaped(card, laid, mesh));
                triangles += mesh.triangles();
            });
            for (final Baked.Haze haze : done.whole().hazes()) {
                hazed.add(new Hazed(new Shaped(card, laid, haze.mesh()), haze.hue(), hazes.get(haze.hides())));
                triangles += haze.mesh().triangles();
            }
        }
        sent = done.asked();
    }

    /** What is on the card of the last one mesh, let go. */
    private void let() {
        hazed.forEach(haze -> haze.shape().close());
        hazed.clear();
        wood.values().forEach(Shaped::close);
        wood.clear();
    }

    /** The one mesh's wood drawn by {@code pass} as {@code camera} sees it, standing at {@code at}, once made. */
    void drawn(final Pass pass, final Camera camera, final Vec3 at) {
        if (sent != null && sent.equals(asked)) {
            wood.forEach((kind, shape) -> barks.get(kind).drawn(pass, shape, new Mat4(at), camera, 1));
        }
    }

    /** Its crowns' haze, where its level has any, drawn by {@code pass}: after everything that is not seen through. */
    void seenThrough(final Pass pass, final Camera camera, final Vec3 at) {
        if (Boolean.getBoolean("bench.trees.haze") && sent != null && sent.equals(asked)) {
            hazed.forEach(haze -> haze.look().drawn(pass, haze.shape(), new Mat4(at), camera, 1, haze.hue()));
        }
    }

    /** What it has to say: how big the one mesh is, or that it is being made, or why it could not be. */
    String said() {
        if (asked == null) {
            return "";
        }
        if (sent != null && sent.equals(asked) && !failed.isEmpty()) {
            return "The one mesh could not be made: " + failed;
        }
        return sent != null && sent.equals(asked)
                ? String.format(Locale.ENGLISH, "One mesh: %,d triangles in %d draws", triangles,
                wood.size() + hazed.size())
                : "Making the one mesh...";
    }

    @Override
    public void close() {
        worker.shutdownNow();
        let();
        hazes.values().forEach(Looked::close);
        hazes.clear();
        barks.values().forEach(Looked::close);
        barks.clear();
    }
}
