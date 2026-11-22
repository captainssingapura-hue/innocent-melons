package com.example.fxsuite.texture.lab;

import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.texture.materials.Shades;
import com.example.fxsuite.vulkan.bench.Cast;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.device.Target;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.render.Looked;
import com.example.fxsuite.vulkan.render.Shadows;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.view.Beam;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.worked.Daylight;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The bench's view, on the card: the shapes on show, standing in a row on level ground, in whichever material is
 * on show. Every material of the tree and every shape is made ready when the view is: each material's look built
 * once, each shape sent once, and any shape drawn with any look. So nothing is built while frames are drawn, and
 * a change of material or of shapes shows in the next frame. A shape that has windows, as the car body has, has
 * them drawn in glass whatever material is on show, and its inside drawn dark behind them. What is seen through
 * is drawn after everything else, the furthest first. The shapes cast shadows on the ground and on each other
 * when shadows are asked for; what is seen through casts none. Closed, all of it is gone from the card.
 */
public final class MaterialView implements AutoCloseable {

    /** How many pixels wide and tall the sun's picture is, and how much ground it covers, in centimetres. */
    private static final int PIXELS = 2048;
    private static final double SHADED = 1600;

    private final Laid laid;
    private final Map<Material, Looked> looks = new LinkedHashMap<>();
    private final Map<Shape, Shaped> shapes = new EnumMap<>(Shape.class);
    /** The windows of the shapes that have any: drawn in glass, whatever material is on show. */
    private final Map<Shape, Shaped> glazing = new EnumMap<>(Shape.class);
    /** The insides of the shapes that are hollow and looked into: drawn in rubber, dark, whatever is on show. */
    private final Map<Shape, Shaped> lining = new EnumMap<>(Shape.class);
    private final Cast cast;
    private final Shadows shadows;
    /** What undoes each thing made here, in the order it was made. */
    private final List<Runnable> closing = new ArrayList<>();

    /** The view, on {@code card}, its layouts as {@code laid} says. */
    public MaterialView(final Card card, final Laid laid) {
        this.laid = laid;
        final Shades shades = new Shades();
        try {
            for (final Leaf<?> leaf : new Materials().tree().leaves()) {
                final Looked look = new Looked(card, new Look(laid, shades.of((Material) leaf)));
                closing.add(look::close);
                looks.put((Material) leaf, look);
            }
            for (final Shape shape : Shape.values()) {
                final Shaped form = new Shaped(card, laid, shape.formed());
                closing.add(form::close);
                shapes.put(shape, form);
                shape.glazed().ifPresent(windows -> {
                    final Shaped panes = new Shaped(card, laid, windows);
                    closing.add(panes::close);
                    glazing.put(shape, panes);
                });
                shape.lined().ifPresent(inside -> {
                    final Shaped skin = new Shaped(card, laid, inside);
                    closing.add(skin::close);
                    lining.put(shape, skin);
                });
            }
            this.cast = new Cast(card, laid);
            closing.add(cast::close);
            this.shadows = new Shadows(card, laid, PIXELS);
            closing.add(shadows::close);
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /**
     * One frame drawn on {@code target}: {@code shown}'s shapes in its material, seeming as far off as it says,
     * seen by {@code camera} in {@code time}'s light and under its sky; with shadows, where {@code shadowed}.
     */
    public void drawn(final Target target, final OnShow shown, final Camera camera, final Daylight time,
                      final boolean shadowed) {
        final Stand stand = new Stand(shown.shapes());
        final double aspect = (double) target.width() / target.height();
        final Framed framed = new Framed(laid, camera, time.light());
        final Consumer<Pass> standing = pass -> stood(pass, shown, stand, camera);
        final Consumer<Pass> all = pass -> {
            cast.ground(pass, camera);
            standing.accept(pass);
        };
        if (shadowed) {
            final Beam beam = new Beam(stand.middle(), time.light().sunTowards(), SHADED, SHADED).settled(PIXELS);
            target.drawn(time.sky(), framed.filling(aspect, beam, PIXELS), shadows.cast(framed, beam, standing), all);
        } else {
            target.drawn(time.sky(), framed.filling(aspect), all);
        }
    }

    /**
     * The shapes on show, drawn by {@code pass}. First whatever hides what is behind it: each shape in the
     * material on show, unless that is seen through, and the inside of each that has one. Then whatever is seen
     * through, the shape furthest from the eye first, so that each is laid over what is behind it: a shape in a
     * material that is seen through, and the windows of each shape that has any.
     */
    private void stood(final Pass pass, final OnShow shown, final Stand stand, final Camera camera) {
        final Looked look = looks.get(shown.material());
        for (final Shape shape : stand.shown()) {
            if (!look.seenThrough()) {
                look.drawn(pass, shapes.get(shape), new Mat4(stand.stands(shape)), camera, shown.further(),
                        shown.tint());
                if (lining.containsKey(shape)) {
                    looks.get(new Materials.Rubber()).drawn(pass, lining.get(shape), new Mat4(stand.stands(shape)),
                            camera, shown.further());
                }
            }
        }
        final List<Shape> furthestFirst = new ArrayList<>(stand.shown());
        furthestFirst.sort(Comparator.comparingDouble(
                (Shape shape) -> stand.stands(shape).minus(camera.eye()).length()).reversed());
        for (final Shape shape : furthestFirst) {
            if (look.seenThrough()) {
                look.drawn(pass, shapes.get(shape), new Mat4(stand.stands(shape)), camera, shown.further(),
                        shown.tint());
            }
            if (glazing.containsKey(shape)) {
                looks.get(new Materials.Glass()).drawn(pass, glazing.get(shape), new Mat4(stand.stands(shape)),
                        camera, shown.further());
            }
        }
    }

    @Override
    public void close() {
        closing.reversed().forEach(Runnable::run);
        closing.clear();
    }
}
