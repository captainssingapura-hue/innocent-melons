package com.example.fxsuite.life.body.vulkan.soft;

import com.example.fxsuite.life.body.vulkan.BodyPieces;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.bench.Cast;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.device.Target;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.render.Looked;
import com.example.fxsuite.vulkan.render.Shaded;
import com.example.fxsuite.vulkan.render.Shadowing;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Beam;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.worked.Daylight;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * The soft bench's view, on the card: the ground, the body still as it is bound and stood on it, turned to face the
 * noon sun; its bones; what a region takes in, as {@link SoftPieces} has it; and the muscles held round it, seen
 * through, as glass is, drawn last and the furthest first. Every muscle whole and the bones are sent once; what a
 * region takes in is sent each time the region is found afresh, what was sent for the last let go. Closed, all of it
 * is gone from the card.
 */
public final class SoftView implements AutoCloseable {

    private static final int PIXELS = 2048;
    private static final double SHADED = 320;
    private static final double MIDDLE = 90;
    private static final Mat4 TURNED = new Mat4(new Vec3(-1, 0, 0), new Vec3(0, 1, 0), new Vec3(0, 0, -1));

    private final Card card;
    private final Laid laid;
    private final Map<Material, Looked> looks = new LinkedHashMap<>();
    private final List<Sent> bones = new ArrayList<>();
    private final List<Sent> wholes = new ArrayList<>();
    private final List<Vec3> middles = new ArrayList<>();
    private final List<Sent> found = new ArrayList<>();
    private final Set<Integer> taken = new HashSet<>();
    private final Cast cast;
    private final Shaded shadows;
    private final List<AutoCloseable> closing = new ArrayList<>();
    private Mat4 stands = TURNED;
    private double lift;

    /** Something sent: in {@code material}, of the colour {@code tint}, its mesh {@code shaped}. */
    private record Sent(SoftPieces.Kind kind, Material material, Vec3 tint, Shaped shaped) {
    }

    /** The view, on {@code card}, its layouts as {@code laid} says. */
    public SoftView(final Card card, final Laid laid) {
        this.card = card;
        this.laid = laid;
        final Shades shades = new Shades();
        try {
            for (final Material m : List.of(new Materials.Muscle(), new Materials.Tendon(), new Materials.Bone(),
                    new Materials.Cartilage(), new Materials.ToothEnamel(), new Materials.GlossPaint(),
                    new Materials.Glass())) {
                final Looked look = new Looked(card, new Look(laid, shades.of(m)));
                closing.add(look);
                looks.put(m, look);
            }
            this.cast = new Cast(card, laid);
            closing.add(cast);
            this.shadows = new Shaded(card, laid, PIXELS);
            closing.add(shadows);
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /** The ways its shadows can be found, on the card it draws on. */
    public List<Shadowing> offered() {
        return shadows.offered();
    }

    /** The body's {@code skeleton} and every muscle {@code whole}, sent once, stood lifted {@code lift} cm. */
    public void made(final List<BodyPieces.Bone> skeleton, final List<Mesh> whole, final double lift) {
        this.lift = lift;
        stands = new Mat4(new Vec3(0, lift, 0)).times(TURNED);
        for (final BodyPieces.Bone b : skeleton) {
            bones.add(sent(SoftPieces.Kind.MARK, b.piece().material(), new Vec3(1, 1, 1), b.piece().mesh()));
        }
        for (final Mesh m : whole) {
            wholes.add(sent(SoftPieces.Kind.MARK, new Materials.Glass(), new Vec3(1, 1, 1), m));
            Vec3 sum = new Vec3(0, 0, 0);
            for (final Vec3 p : m.points()) {
                sum = sum.plus(p);
            }
            middles.add(sum.times(1.0 / Math.max(1, m.points().size())));
        }
    }

    /** What a region takes in, {@code f}, sent in place of what was sent for the last. */
    public void found(final SoftPieces.Found f) {
        for (final Sent s : found) {
            closing.remove(s.shaped());
            s.shaped().close();
        }
        found.clear();
        taken.clear();
        taken.addAll(f.taken());
        for (final SoftPieces.Drawn d : f.drawn()) {
            found.add(sent(d.kind(), d.material(), d.tint(), d.mesh()));
        }
    }

    /**
     * One frame drawn on {@code target}: as {@code asked} says, seen by {@code camera} in {@code time}'s light and
     * under its sky; with shadows found {@code by} that way, where {@code shadowed}.
     */
    public void drawn(final Target target, final SoftAsked asked, final Camera camera, final Daylight time,
                      final boolean shadowed, final Shadowing by) {
        final Framed framed = new Framed(laid, camera, time.light());
        final Consumer<Pass> body = pass -> stood(pass, asked, camera);
        final Consumer<Pass> all = pass -> {
            cast.ground(pass, camera);
            body.accept(pass);
        };
        if (shadowed) {
            final Beam beam = new Beam(new Vec3(0, MIDDLE, 0), time.light().sunTowards(), SHADED, SHADED);
            shadows.drawn(target, time.sky(), framed, by, beam, body, all);
        } else {
            target.drawn(time.sky(), framed.filling((double) target.width() / target.height()), all);
        }
    }

    /** Where in the world a point of the body's is: turned round, and lifted. */
    public Vec3 world(final Vec3 body) {
        return new Vec3(-body.x(), body.y() + lift, -body.z());
    }

    /**
     * The bones, what the region takes in (the taken muscles' surfaces and their elements, as asked), then the muscles
     * held round it, seen through, the furthest first.
     */
    private void stood(final Pass pass, final SoftAsked asked, final Camera camera) {
        if (asked.bones()) {
            bones.forEach(s -> drawn(pass, s, camera));
        }
        for (final Sent s : found) {
            if (s.kind() == SoftPieces.Kind.MARK || s.kind() == SoftPieces.Kind.SURFACE && asked.surfaces()
                    || s.kind() == SoftPieces.Kind.ELEMENTS && asked.elements()) {
                drawn(pass, s, camera);
            }
        }
        // what is seen through after all that is not: the fascia, then the muscles held round
        for (final Sent s : found) {
            if (s.kind() == SoftPieces.Kind.FASCIA && asked.fascia()) {
                drawn(pass, s, camera);
            }
        }
        if (!asked.held()) {
            return;
        }
        final List<Integer> held = new ArrayList<>();
        for (int i = 0; i < wholes.size(); i++) {
            if (!taken.contains(i)) {
                held.add(i);
            }
        }
        final Vec3 eye = camera.eye();
        held.sort(Comparator.comparingDouble((Integer i) -> world(middles.get(i)).minus(eye).length()).reversed());
        held.forEach(i -> drawn(pass, wholes.get(i), camera));
    }

    private void drawn(final Pass pass, final Sent s, final Camera camera) {
        looks.get(s.material()).drawn(pass, s.shaped(), stands, camera, 1, s.tint());
    }

    /** {@code mesh} sent, in {@code material} and {@code tint}, to be let go when the view is closed. */
    private Sent sent(final SoftPieces.Kind kind, final Material material, final Vec3 tint, final Mesh mesh) {
        final Shaped shaped = new Shaped(card, laid, mesh);
        closing.add(shaped);
        return new Sent(kind, material, tint, shaped);
    }

    @Override
    public void close() {
        for (final AutoCloseable c : closing.reversed()) {
            try {
                c.close();
            } catch (final Exception e) {
                throw new IllegalStateException("not let go from the card: " + c, e);
            }
        }
        closing.clear();
    }
}
