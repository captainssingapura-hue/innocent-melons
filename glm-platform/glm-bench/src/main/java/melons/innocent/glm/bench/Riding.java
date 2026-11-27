package melons.innocent.glm.bench;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.motorcycle.BikeFigures;
import melons.innocent.glm.motorcycle.Motorcycle;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The motorcycle round the twin on the bench, as the cars are: put together, and stood on its wheels on the bench's
 * ground, its engine's frame lifted as far as its crank stands off the ground ({@link BikeFigures}).
 */
record Riding() {

    /** It, put together and stood on the ground. */
    Assembled assembled() {
        final Assembled built = new Assembly(new Motorcycle().platform()).assembled();
        final Vec3 lift = new Vec3(0, -figures().ground(), 0);
        final Map<PartAt, Vec3> placed = new LinkedHashMap<>();
        built.placed().forEach((part, at) -> placed.put(part, at.plus(lift)));
        return new Assembled(placed, built.gaps(), built.unreached());
    }

    /** Where it is looked at round: between its wheels, at its seat's height. */
    Vec3 middle() {
        final BikeFigures figures = figures();
        final double between = (figures.frontAxle().x() + figures.rearAxle().x()) / 2;
        return new Vec3(between, 50, figures.centre());
    }

    private BikeFigures figures() {
        return new Motorcycle().figures();
    }
}
