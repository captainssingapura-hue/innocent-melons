package melons.innocent.glm.lab;

import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.form.Ball;
import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Sized;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.core.Point;
import melons.innocent.vehiclelab.drawn.Coats;
import melons.innocent.vehiclelab.forms.Coat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * What the lab draws, on the card: every part of the platform sent once, in its own frame, the left as the right's
 * mirror; a ball to mark points with, drawn as big as wanted; and the grid's lines, sent afresh whenever the scene
 * changes, the old ones gone. Each drawn in its finish ({@link Coats}): the grid's light lines a light grey, its heavy
 * ones dark, its axes red across, green up and blue along; the points lit amber. Closed, all of it is gone.
 */
public final class LabDrawn implements AutoCloseable {

    /** The grid's lines and axes' finishes; the points'. */
    private static final Coat LIGHT = new Coat(Coat.Kind.GLOSS_PAINT, "#8a9099");
    private static final Coat HEAVY = new Coat(Coat.Kind.GLOSS_PAINT, "#3d4248");
    private static final List<Coat> AXES = List.of(new Coat(Coat.Kind.GLOSS_PAINT, "#d03a2f"),
            new Coat(Coat.Kind.GLOSS_PAINT, "#3aa845"), new Coat(Coat.Kind.GLOSS_PAINT, "#2f6fd0"));
    private static final Coat MARK = new Coat(Coat.Kind.LAMP, "#ffc23d");
    /** How big a point's mark is, as a share of the grid's box, and at least. */
    private static final double MARK_SHARE = 0.008;
    private static final double MARK_LEAST = 0.3;

    /** A line of the grid, and its finish. */
    private record Line(Shaped form, Coat coat) {
    }

    private final Card card;
    private final Laid laid;
    private final Coats coats;
    private final Map<PartAt, Shaped> parts = new HashMap<>();
    private final Shaped ball;
    private final List<Line> grid = new ArrayList<>();

    /** {@code built}'s parts on {@code card}, its layouts as {@code laid} says. */
    public LabDrawn(final Card card, final Laid laid, final Assembled built) {
        this.card = card;
        this.laid = laid;
        try {
            this.coats = new Coats(card, laid);
            built.placed().keySet().forEach(p -> parts.put(p, new Shaped(card, laid, p.shape().form())));
            final Forming forming = new Forming(new Vec3(0, 0, 0));
            new Ball(new Vec3(0, 0, 0), 1, 12).formed(forming);
            this.ball = new Shaped(card, laid, forming.formed());
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    /** The grid sent afresh for {@code scene}, the old one gone. */
    public void ruled(final LabScene.Settled scene) {
        grid.forEach(l -> l.form().close());
        grid.clear();
        final Grid ruled = scene.grid();
        grid.add(new Line(new Shaped(card, laid, ruled.light()), LIGHT));
        grid.add(new Line(new Shaped(card, laid, ruled.heavy()), HEAVY));
        for (int way = 0; way < 3; way++) {
            grid.add(new Line(new Shaped(card, laid, ruled.axis(way)), AXES.get(way)));
        }
    }

    /** What of {@code scene} casts a shadow: its parts that are not seen through. */
    public void solid(final Pass pass, final Camera camera, final LabScene.Settled scene) {
        for (final LabScene.Placement p : scene.placements()) {
            if (!coats.seenThrough(p.coat())) {
                coats.drawn(pass, parts.get(p.part()), new Mat4(p.at().plus(scene.lift())), camera, p.coat());
            }
        }
    }

    /** Its grid and its points' marks, as {@code scene} shows them. */
    public void marked(final Pass pass, final Camera camera, final LabScene.Settled scene) {
        if (scene.show().grid()) {
            grid.forEach(l -> coats.drawn(pass, l.form(), new Mat4(scene.lift()), camera, l.coat()));
        }
        if (scene.show().points()) {
            final double size = Math.max(MARK_LEAST, MARK_SHARE * scene.size());
            for (final Point p : scene.points()) {
                final Mat4 at = new Mat4(p.at().plus(scene.lift())).times(new Sized(size).matrix());
                coats.drawn(pass, ball, at, camera, MARK);
            }
        }
    }

    /** What of {@code scene} is seen through, after everything else. */
    public void seenThrough(final Pass pass, final Camera camera, final LabScene.Settled scene) {
        for (final LabScene.Placement p : scene.placements()) {
            if (coats.seenThrough(p.coat())) {
                coats.drawn(pass, parts.get(p.part()), new Mat4(p.at().plus(scene.lift())), camera, p.coat());
            }
        }
    }

    @Override
    public void close() {
        grid.forEach(l -> l.form().close());
        grid.clear();
        parts.values().forEach(Shaped::close);
        parts.clear();
        if (ball != null) {
            ball.close();
        }
        if (coats != null) {
            coats.close();
        }
    }
}
