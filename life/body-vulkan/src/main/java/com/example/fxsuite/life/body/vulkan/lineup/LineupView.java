package com.example.fxsuite.life.body.vulkan.lineup;

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
 * The line-up's view, on the card: the ground, and on it its figures side by side, each where the line-up says and as
 * tall ({@link LineupShown}), facing the noon sun. Each figure's pieces are sent once, when it is made, each drawn
 * every frame moved as its part has moved since the body was bound, so that it goes as the body does; all of them
 * moving alike. Each material's look is built once, when the view is. They cast shadows on the ground and on each
 * other when shadows are asked for. Closed, all of it is gone from the card.
 */
public final class LineupView implements AutoCloseable {

    /** How many pixels wide and tall the sun's picture is, and how high the middle of what is shaded is, cm. */
    private static final int PIXELS = 2048;
    private static final double MIDDLE = 100;
    /** How much the sun's picture covers beyond the line's ends, cm. */
    private static final double MARGIN = 160;
    /** The figures turned round to face the way the world's third number falls, toward the noon sun. */
    private static final Mat4 TURNED = new Mat4(new Vec3(-1, 0, 0), new Vec3(0, 1, 0), new Vec3(0, 0, -1));

    private final Card card;
    private final Laid laid;
    private final Map<Material, Looked> looks = new LinkedHashMap<>();
    private final List<List<Sent>> figures = new ArrayList<>();
    private final List<Rig> bound = new ArrayList<>();
    private final Cast cast;
    private final Shaded shadows;
    private final List<Runnable> closing = new ArrayList<>();

    /** A piece sent to the card. */
    private record Sent(Avatar.Piece piece, Shaped shaped) {
    }

    /** The view, on {@code card}, its layouts as {@code laid} says. */
    public LineupView(final Card card, final Laid laid) {
        this.card = card;
        this.laid = laid;
        final Shades shades = new Shades();
        try {
            for (final Material m : List.of(new Materials.Steel(), new Materials.Gunmetal(),
                    new Materials.GlossPaint(), new Materials.LitGlass())) {
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

    /** {@code avatar}'s pieces sent to the card, to be drawn from now on, the next figure along the line. */
    public void add(final Avatar avatar) {
        final List<Sent> sent = new ArrayList<>();
        for (final Avatar.Piece p : avatar.pieces()) {
            final Shaped shaped = new Shaped(card, laid, p.mesh());
            closing.add(shaped::close);
            sent.add(new Sent(p, shaped));
        }
        figures.add(sent);
        bound.add(avatar.bound());
    }

    /** How many of its figures are on the card. */
    public int count() {
        return figures.size();
    }

    /**
     * One frame drawn on {@code target}: those of its figures that are made, where and as tall as {@code shown} says,
     * each held as {@code rig} holds the body, its soles lifted {@code lift} cm; seen by {@code camera} in
     * {@code time}'s light and under its sky; with shadows found {@code by} that way, where {@code shadowed}.
     */
    public void drawn(final Target target, final LineupShown shown, final Rig rig, final double lift,
                      final Camera camera, final Daylight time, final boolean shadowed, final Shadowing by) {
        final Consumer<Pass> standing = pass -> {
            for (int i = 0; i < figures.size() && i < shown.tall().size(); i++) {
                stood(pass, figures.get(i), moves(rig, bound.get(i), placed(shown, i, lift)), camera);
            }
        };
        final Consumer<Pass> all = pass -> {
            cast.ground(pass, camera);
            standing.accept(pass);
        };
        if (shadowed) {
            final double wide = shown.along(shown.tall().size() - 1) - shown.along(0) + 2 * MARGIN;
            final Beam beam = new Beam(new Vec3(0, MIDDLE, 0), time.light().sunTowards(), wide, wide);
            shadows.drawn(target, time.sky(), new Framed(laid, camera, time.light()), by, beam, standing, all);
        } else {
            target.drawn(time.sky(), new Framed(laid, camera, time.light())
                    .filling((double) target.width() / target.height()), all);
        }
    }

    /** {@code figure}'s pieces drawn by {@code pass}, each moved as its part has, {@code moved}. */
    private void stood(final Pass pass, final List<Sent> figure, final Map<Part, Mat4> moved, final Camera camera) {
        for (final Sent s : figure) {
            looks.get(s.piece().material()).drawn(pass, s.shaped(), moved.get(s.piece().part()), camera, 1,
                    s.piece().colour());
        }
    }

    /** Where figure {@code i} stands, as {@code shown} says: along the line, as large, lifted {@code lift}, turned. */
    private Mat4 placed(final LineupShown shown, final int i, final double lift) {
        final double s = shown.scale(i);
        final Mat4 scaled = new Mat4(new Vec3(s, 0, 0), new Vec3(0, s, 0), new Vec3(0, 0, s));
        return new Mat4(new Vec3(shown.along(i), 0, 0)).times(scaled).times(new Mat4(new Vec3(0, lift, 0)))
                .times(TURNED);
    }

    /** How each part has moved from as {@code then} holds the body to as {@code rig} holds it, then {@code stands}. */
    private Map<Part, Mat4> moves(final Rig rig, final Rig then, final Mat4 stands) {
        final Map<Part, Mat4> moved = new EnumMap<>(Part.class);
        for (final Part p : Part.values()) {
            final Rig.Frame now = rig.frame(p);
            final Rig.Frame was = then.frame(p);
            final V3 o = now.place(was.local(V3.ZERO));
            final V3 x = now.place(was.local(new V3(1, 0, 0))).minus(o);
            final V3 y = now.place(was.local(new V3(0, 1, 0))).minus(o);
            final V3 z = now.place(was.local(new V3(0, 0, 1))).minus(o);
            moved.put(p, stands.times(new Mat4(new double[] {x.x(), y.x(), z.x(), o.x(), x.y(), y.y(), z.y(), o.y(),
                x.z(), y.z(), z.z(), o.z(), 0, 0, 0, 1})));
        }
        return moved;
    }

    /** How many figures it draws, and how many triangles they have in all. */
    public String said() {
        final long triangles = figures.stream().flatMap(List::stream)
                .mapToLong(s -> s.piece().mesh().corners().size() / 3).sum();
        return String.format(java.util.Locale.ROOT, "%d figures, %,d triangles", figures.size(), triangles);
    }

    @Override
    public void close() {
        closing.reversed().forEach(Runnable::run);
        closing.clear();
    }
}
