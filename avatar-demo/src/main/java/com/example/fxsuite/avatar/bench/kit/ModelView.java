package com.example.fxsuite.avatar.bench.kit;

import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.avatar.look.BuildingModel;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

import java.util.List;

/**
 * A bench's model view: a model, still, on level ground, to be looked at from any side. What it shows is only ever
 * {@link Model}s, each drawn once (from its solids, or made afresh) and never touched again: nothing in it moves, nothing in it is
 * redrawn but by {@link #show} putting other models in its place, and nothing about it changes from frame to frame but
 * where it is seen from. A bench that would see something move plays it ({@link Player}) in a window of its own.
 *
 * <p>Seen through an {@link Orbit}: dragged, it turns round what it looks at; right-dragged, moves across; scrolled,
 * nearer or further.
 */
public final class ModelView {

    /**
     * A model drawn, still: made from its solids anywhere (on a workshop's worker, so the screen waits on nothing),
     * shown by a view; nothing of it to reach but to show it.
     */
    public static final class Model {

        private final Node drawn;

        private Model(final Node drawn) {
            this.drawn = drawn;
        }

        /** {@code solids} drawn, each material's in one mesh. */
        public static Model of(final List<Solid> solids) {
            return of(BuildingModel.of(solids));
        }

        /**
         * {@code drawn}, made afresh to be shown and handed over: whoever made it keeps nothing of it to move or
         * change after. A bench changed makes another and shows that in its place.
         */
        public static Model of(final Node drawn) {
            // looked at, never picked: the view's dragging is all that answers the mouse
            drawn.setMouseTransparent(true);
            return new Model(drawn);
        }

        /**
         * {@code drawn}, made afresh as {@link #of(Node)} is, but to be taken hold of: pressed on, what it is made of
         * is picked, for the view's grip ({@link ModelView#grip}) to take; whatever of it is not to be, made mouse
         * transparent by whoever made it.
         */
        public static Model touchable(final Node drawn) {
            return new Model(drawn);
        }
    }

    /** {@code grip} given what is pressed on in a touchable model, to take hold of and move ({@link Grip}). */
    public void grip(final Grip grip) {
        orbit.grip(grip);
    }

    private final Orbit orbit;
    private final Group ground = new Group();
    private final Group shown = new Group();

    /** A view as the roads bench has had it ({@link Orbit#standard}), on grass 200 m square. */
    public ModelView() {
        this(Orbit.standard());
        ground(0, 0, 0, 20_000);
    }

    /**
     * A view seen through {@code orbit}, its sky and lights, with no ground till it is given one ({@link #ground}) or
     * what it shows brings its own; {@code orbit} not to be used but by this view.
     */
    public ModelView(final Orbit orbit) {
        this.orbit = orbit;
        ground.setMouseTransparent(true);
        orbit.add(ground, shown);
    }

    /** What to put in the window: the view, filling what holds it. */
    public Pane node() {
        return orbit.node();
    }

    /** {@code models} shown in place of whatever was. */
    public void show(final Model... models) {
        show(List.of(models));
    }

    /** {@code models} shown in place of whatever was. */
    public void show(final List<Model> models) {
        shown.getChildren().setAll(models.stream().map(m -> m.drawn).toList());
    }

    /**
     * Level ground seen through, in place of grass: a grid of lines only, {@code size} cm square, its middle at
     * ({@code x}, {@code z}), at {@code y} up; a line every {@code step} cm, every {@code every}th darker and broader.
     * What stands on it is seen from below as from above.
     */
    public void grid(final double x, final double y, final double z, final double size, final double step,
                     final int every) {
        final PhongMaterial fine = new PhongMaterial(Color.web("#8c949b"));
        final PhongMaterial bold = new PhongMaterial(Color.web("#4d555c"));
        final Group lines = new Group();
        final int n = (int) Math.round(size / step / 2);
        for (int k = -n; k <= n; k++) {
            final boolean major = k % every == 0;
            final double wide = major ? 0.6 : 0.25;
            final Box across = new Box(2 * n * step, 0.1, wide);
            across.setTranslateX(x);
            across.setTranslateZ(z + k * step);
            final Box along = new Box(wide, 0.1, 2 * n * step);
            along.setTranslateX(x + k * step);
            along.setTranslateZ(z);
            for (final Box line : new Box[] {across, along}) {
                line.setTranslateY(-y);
                line.setMaterial(major ? bold : fine);
                lines.getChildren().add(line);
            }
        }
        ground.getChildren().setAll(lines);
    }

    /** Level ground, grass, {@code size} cm square, its middle at ({@code x}, {@code z}), its top at {@code y} up. */
    public void ground(final double x, final double y, final double z, final double size) {
        final Box grass = new Box(size, 2, size);
        grass.setTranslateX(x);
        grass.setTranslateY(-y + 1.5);
        grass.setTranslateZ(z);
        final PhongMaterial look = new PhongMaterial(Color.web("#9fae8f"));
        look.setSpecularColor(Color.web("#3a3f46"));
        grass.setMaterial(look);
        ground.getChildren().setAll(grass);
    }

    /** Turned round ({@code x}, {@code y}, {@code z}), in the game's frame (y up). */
    public void lookAt(final double x, final double y, final double z) {
        orbit.lookAt(x, -y, z);
    }

    /** Seen from as {@code prefix}'s properties say ({@link Orbit#from}), or else as given: degrees, degrees down, m. */
    public void from(final String prefix, final double yawDegrees, final double pitchDegrees, final double metres) {
        orbit.from(prefix, yawDegrees, pitchDegrees, metres);
    }

    /** Seen from {@code yawDegrees} round, {@code pitchDegrees} down. */
    public void turned(final double yawDegrees, final double pitchDegrees) {
        orbit.turned(yawDegrees, pitchDegrees);
    }

    /** How far round it is seen from, degrees. */
    public double yawDegrees() {
        return orbit.yawDegrees();
    }

    /** How far down it is seen from, degrees. */
    public double pitchDegrees() {
        return orbit.pitchDegrees();
    }

    /** Seen from {@code cm} off. */
    public void distance(final double cm) {
        orbit.distance(cm);
    }

    /** Scrolled no nearer than {@code nearestCm}, no further than {@code furthestCm}. */
    public void zoom(final double nearestCm, final double furthestCm) {
        orbit.zoom(nearestCm, furthestCm);
    }
}
