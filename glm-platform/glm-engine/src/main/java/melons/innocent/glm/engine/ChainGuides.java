package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Point;
import melons.innocent.glm.existences.EngineParts;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.List;

/**
 * What keeps a bank's timing chain on its line ({@link ChainFigures}): beside its tight side a fixed guide, a blade
 * of glass-filled plastic along the most of the run, just outside the chain; and pressing on its slack side the
 * tensioner, its blade the same, a plunger of steel pushing on its middle from outside, as the engine's oil pushes
 * it, so the chain never runs slack. Each bolted to the block's front. Their point: the crank's axis at the block's
 * front, where they are bolted, as the timing cover is.
 *
 * @param figures the engine's figures
 * @param bank    its bank, from nought
 */
record ChainGuides(EngineFigures figures, int bank) {

    /** How thick a blade is; how far its middle stands out from the chain's line; how much of the run it covers. */
    private static final double BLADE = 0.5;
    private static final double OUT = 0.3 + 0.1 + BLADE / 2;
    private static final double FROM = 0.15;
    private static final double TO = 0.85;
    /** How far the tensioner's plunger stands out from its blade, and how far round it is. */
    private static final double PLUNGER = 1.0;
    private static final double PLUNGER_ROUND = 0.45;

    /** The fixed guide, beside its tight side. */
    Part guide() {
        return new Part(called("chain guide"), Group.ENGINE, false, point(), blade(new ChainFigures(figures, bank)
                .tight()), new Made(Material.PLASTIC, true)).as(new EngineParts.ChainGuide());
    }

    /** The tensioner, pressing on its slack side. */
    Part tensioner() {
        final ChainFigures chain = new ChainFigures(figures, bank);
        final Belt.Run run = chain.slack();
        final Vec3 out = out(run);
        final Vec3 middle = along(run, 0.5).plus(out.times(OUT + BLADE / 2));
        final Shapes shapes = new Shapes(12);
        return new Part(called("chain tensioner"), Group.ENGINE, false, point(), shapes.joined(List.of(blade(run),
                shapes.drum(middle, middle.plus(out.times(PLUNGER)), PLUNGER_ROUND))),
                new Made(Material.STEEL, true)).as(new EngineParts.ChainTensioner());
    }

    /** A blade along {@code run}, just outside the chain's line, across the chain's plane as wide as the chain. */
    private Mesh blade(final Belt.Run run) {
        final double length = run.length();
        final Vec3 from = along(run, 0).plus(out(run).times(OUT));
        final Vec3 way = along(run, 1).minus(along(run, 0));
        return new Plate(List.of(new Spot(FROM * length, -BLADE / 2), new Spot(TO * length, -BLADE / 2),
                new Spot(TO * length, BLADE / 2), new Spot(FROM * length, BLADE / 2)), List.of(), from, way,
                out(run), ChainFigures.WIDE + 0.2).mesh();
    }

    /** The place {@code t} of the way along {@code run}, in its chain's plane. */
    private Vec3 along(final Belt.Run run, final double t) {
        return new Vec3(run.from().u() + t * (run.to().u() - run.from().u()), run.from().v() + t * (run.to().v()
                - run.from().v()), new ChainFigures(figures, bank).plane());
    }

    /** The way out of the chain's loop from {@code run}: to the right of the way it is wrapped. */
    private Vec3 out(final Belt.Run run) {
        final double h = run.heading();
        return new Vec3(Math.sin(h), -Math.cos(h), 0);
    }

    private List<Point> point() {
        return List.of(new Point("block", new Vec3(0, 0, 0)));
    }

    private String called(final String what) {
        return figures.name() + " " + what + (figures.banks() == 2 ? ", bank " + (bank + 1) : "");
    }
}
