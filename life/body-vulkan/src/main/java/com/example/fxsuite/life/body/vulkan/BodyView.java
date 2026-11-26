package com.example.fxsuite.life.body.vulkan;

import com.example.fxsuite.life.body.Layer;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.bench.Cast;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.device.Target;
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
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The body bench's view, on the card: the ground, and on it the body's pieces ({@link BodyPieces}), each group of its
 * parts as deep as is on show, each piece in its tissue, the skin in the colour on show. Each tissue's look is built
 * once, when the view is. Its bones are sent once, as the body is bound, and each frame drawn moved as their part has
 * moved since, so the skeleton goes as the body does. Its skin and muscles are sent posed, a stopped body at a time,
 * those posed before let go; drawn only when asked, while they are the body's as it stands, and its bones drawn in
 * their place meanwhile, whatever group is drawn as deep as its skin or muscles. The body is turned to
 * face the noon sun and stands on the ground. It casts shadows on the ground and on itself when shadows are asked for.
 * Closed, all of it is gone from the card.
 */
public final class BodyView implements AutoCloseable {

    /** How many pixels wide and tall the sun's picture is, and how much it covers, in centimetres. */
    private static final int PIXELS = 2048;
    private static final double SHADED = 320;
    /** How high the middle of what is shaded is, in centimetres. */
    private static final double MIDDLE = 90;
    /** The body turned round to face the way the world's third number falls, toward the noon sun. */
    private static final Mat4 TURNED = new Mat4(new Vec3(-1, 0, 0), new Vec3(0, 1, 0), new Vec3(0, 0, -1));

    private final Card card;
    private final Laid laid;
    private final Map<Material, Looked> looks = new LinkedHashMap<>();
    private final List<Bone> bones = new ArrayList<>();
    private final List<Sent> flesh = new ArrayList<>();
    private final Cast cast;
    private final Shaded shadows;
    private final List<Runnable> closing = new ArrayList<>();
    private final List<Sent> helmet = new ArrayList<>();
    private Rig bound;

    /** A piece sent to the card. */
    private record Sent(BodyPieces.Piece piece, Shaped shaped) {
    }

    /** A bone sent to the card, moving with its part. */
    private record Bone(Part part, Sent sent) {
    }

    /**
     * How the body is drawn this frame: held as {@code rig} holds it, lifted {@code lift} cm, its skin and muscles
     * drawn if {@code fleshed}.
     */
    public record Held(Rig rig, double lift, boolean fleshed) {
    }

    /** The view, on {@code card}, its layouts as {@code laid} says. */
    public BodyView(final Card card, final Laid laid) {
        this.card = card;
        this.laid = laid;
        final Shades shades = new Shades();
        try {
            final List<Material> all = new ArrayList<>(List.of(new Materials.Skin(), new Materials.Muscle(),
                    new Materials.Tendon(), new Materials.Bone(), new Materials.Cartilage(),
                    new Materials.ToothEnamel()));
            all.addAll(new HelmetShown(false, new Materials.Steel(), new Vec3(1, 1, 1), new Vec3(0, 0, 0))
                    .offered());
            for (final Material m : all) {
                final Looked look = new Looked(card, new Look(laid, shades.of(m)));
                closing.add(look::close);
                looks.put(m, look);
            }
            this.cast = new Cast(card, laid);
            closing.add(cast::close);
            this.shadows = new Shaded(card, laid, PIXELS);
            closing.add(shadows::close);
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /** The ways its shadows can be found, on the card it draws on. */
    public List<Shadowing> offered() {
        return shadows.offered();
    }

    /** {@code made}'s bones sent to the card, to be drawn from now on, each moving with its part from as bound. */
    public void boned(final BodyMaker.Made made) {
        bound = made.bound();
        for (final BodyPieces.Bone b : made.bones()) {
            bones.add(new Bone(b.part(), sent(b.piece())));
        }
        helmet.add(sent(made.helmet().shell().piece()));
        helmet.add(sent(made.helmet().visor().piece()));
    }

    /** {@code pieces}, the skin and muscles posed, sent to the card in place of those posed before. */
    public void fleshed(final List<BodyPieces.Piece> pieces) {
        for (final Sent s : flesh) {
            closing.remove(new Closer(s.shaped()));
            s.shaped().close();
        }
        flesh.clear();
        for (final BodyPieces.Piece p : pieces) {
            flesh.add(sent(p));
        }
    }

    /**
     * One frame drawn on {@code target}: the body as {@code shown} says and {@code held} holds it, seen by
     * {@code camera} in {@code time}'s light and under its sky; with shadows found {@code by} that way, where
     * {@code shadowed}.
     */
    public void drawn(final Target target, final BodyShown shown, final Held held, final Camera camera,
                      final Daylight time, final boolean shadowed, final Shadowing by) {
        final double aspect = (double) target.width() / target.height();
        final Framed framed = new Framed(laid, camera, time.light());
        final Mat4 stands = new Mat4(new Vec3(0, held.lift(), 0)).times(TURNED);
        final Map<Part, Mat4> moved = moves(held.rig(), stands);
        final Consumer<Pass> body = pass -> stood(pass, shown, held, stands, moved, camera);
        final Consumer<Pass> all = pass -> {
            cast.ground(pass, camera);
            body.accept(pass);
        };
        if (shadowed) {
            final Beam beam = new Beam(new Vec3(0, MIDDLE, 0), time.light().sunTowards(), SHADED, SHADED);
            shadows.drawn(target, time.sky(), framed, by, beam, body, all);
        } else {
            target.drawn(time.sky(), framed.filling(aspect), all);
        }
    }

    /** The pieces of each group drawn as deep as {@code shown} says, by {@code pass}. */
    private void stood(final Pass pass, final BodyShown shown, final Held held, final Mat4 stands,
                       final Map<Part, Mat4> moved, final Camera camera) {
        for (final Bone b : bones) {
            final Layer l = shown.of(b.sent().piece().group());
            if (b.sent().piece().shownIn().contains(l) || !held.fleshed() && l != Layer.HIDDEN) {
                looks.get(b.sent().piece().material()).drawn(pass, b.sent().shaped(), moved.get(b.part()), camera, 1,
                        shown.skin());
            }
        }
        if (held.fleshed()) {
            for (final Sent s : flesh) {
                drawn(pass, s, stands, shown, camera);
            }
        }
        worn(pass, shown.helmet(), moved.get(Part.HEAD), camera);
    }

    /** The helmet, if it is worn, moved as the head has: its shell in what it is made of, its visor in its glass. */
    private void worn(final Pass pass, final HelmetShown h, final Mat4 head, final Camera camera) {
        if (!h.on() || helmet.isEmpty() || head == null) {
            return;
        }
        looks.get(h.material()).drawn(pass, helmet.get(0).shaped(), head, camera, 1, h.colour());
        looks.get(h.glass()).drawn(pass, helmet.get(1).shaped(), head, camera, 1, h.visor());
    }

    private void drawn(final Pass pass, final Sent s, final Mat4 at, final BodyShown shown, final Camera camera) {
        if (s.piece().shownIn().contains(shown.of(s.piece().group()))) {
            looks.get(s.piece().material()).drawn(pass, s.shaped(), at, camera, 1, shown.skin());
        }
    }

    /** How each part has moved from as the body is bound to as {@code rig} holds it, then stood by {@code stands}. */
    private Map<Part, Mat4> moves(final Rig rig, final Mat4 stands) {
        final Map<Part, Mat4> moved = new EnumMap<>(Part.class);
        if (bound == null) {
            return moved;
        }
        for (final Part p : Part.values()) {
            final Rig.Frame now = rig.frame(p);
            final Rig.Frame then = bound.frame(p);
            final V3 o = now.place(then.local(V3.ZERO));
            final V3 x = now.place(then.local(new V3(1, 0, 0))).minus(o);
            final V3 y = now.place(then.local(new V3(0, 1, 0))).minus(o);
            final V3 z = now.place(then.local(new V3(0, 0, 1))).minus(o);
            moved.put(p, stands.times(new Mat4(new double[] {x.x(), y.x(), z.x(), o.x(), x.y(), y.y(), z.y(), o.y(),
                x.z(), y.z(), z.z(), o.z(), 0, 0, 0, 1})));
        }
        return moved;
    }

    /** {@code p} sent to the card, to be let go when the view is closed. */
    private Sent sent(final BodyPieces.Piece p) {
        final Shaped shaped = new Shaped(card, laid, p.mesh());
        final Sent s = new Sent(p, shaped);
        closing.add(new Closer(shaped));
        return s;
    }

    /** What lets a piece go from the card, the piece's own, so it can be found again among what is to be closed. */
    private record Closer(Shaped shaped) implements Runnable {
        @Override
        public void run() {
            shaped.close();
        }
    }

    /** How many pieces it draws from, and how many triangles they have in all. */
    public String said() {
        long triangles = 0;
        for (final Bone b : bones) {
            triangles += b.sent().piece().mesh().corners().size() / 3;
        }
        for (final Sent s : flesh) {
            triangles += s.piece().mesh().corners().size() / 3;
        }
        return String.format(java.util.Locale.ROOT, "%d bones and %d pieces of skin and muscle, %,d triangles",
                bones.size(), flesh.size(), triangles);
    }

    @Override
    public void close() {
        closing.reversed().forEach(Runnable::run);
        closing.clear();
    }
}
