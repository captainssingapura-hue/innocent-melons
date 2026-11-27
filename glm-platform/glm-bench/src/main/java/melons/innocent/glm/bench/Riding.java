package melons.innocent.glm.bench;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.motorcycle.BikeFigures;
import melons.innocent.glm.motorcycle.Motorcycle;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The motorcycle round the twin on the bench, as the cars are: put together, and stood on its wheels on the bench's
 * ground, its engine's frame lifted as far as its crank stands off the ground ({@link BikeFigures}); leaned over as
 * far as asked, on its tyres' crowns, and told which of its parts would touch the ground so leaned.
 */
record Riding() {

    /** How far it is leaned at most either way, in degrees; how finely the lean its parts first touch at is found. */
    static final double MOST = 60;
    private static final double STEP = 0.5;

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

    /**
     * What leans it {@code degrees} over, to its right as it is more: about the line along it its tyres' crowns roll
     * on, as high over the ground as the back tyre's tread is round across, so its tyres stay on the ground.
     */
    Mat4 leaned(final double degrees) {
        return new Turned(new Vec3(1, 0, 0), degrees).matrix(new Vec3(0, BikeFigures.REAR.wide() / 2,
                figures().centre()));
    }

    /** What it says of {@code built} leaned {@code degrees}: which of its parts touch the ground. */
    String said(final Assembled built, final double degrees) {
        final List<String> touching = touching(built, degrees);
        return String.format(Locale.ENGLISH, "Leaned %.1f° to its %s; touching the ground: %s", Math.abs(degrees),
                degrees < 0 ? "left" : "right", touching.isEmpty() ? "nothing but its tyres"
                : String.join(", ", touching));
    }

    /** How far {@code built} leans either way before anything but its tyres touches the ground, and what does. */
    String limits(final Assembled built) {
        final double left = first(built, -1);
        final double right = first(built, 1);
        return String.format(Locale.ENGLISH, "It leans %.1f° to its left before its %s touches, %.1f° to its right"
                + " before its %s", left, String.join(", ", touching(built, -(left + STEP))), right,
                String.join(", ", touching(built, right + STEP)));
    }

    /** Its parts but its tyres that reach under the ground, leaned {@code degrees}. */
    List<String> touching(final Assembled built, final double degrees) {
        final Mat4 m = leaned(degrees);
        return built.placed().entrySet().stream().filter(e -> !e.getKey().part().name().contains("tyre"))
                .filter(e -> e.getKey().shape().form().points().stream()
                        .anyMatch(p -> high(m, p.plus(e.getValue())) < 0))
                .map(e -> e.getKey().part().name()).distinct().toList();
    }

    /** How far it leans toward {@code side}, -1 its left, before anything but its tyres touches the ground. */
    private double first(final Assembled built, final int side) {
        double lean = 0;
        while (lean < MOST && touching(built, side * (lean + STEP)).isEmpty()) {
            lean += STEP;
        }
        return lean;
    }

    /** How high {@code p} comes to over the ground, moved by {@code m}. */
    private double high(final Mat4 m, final Vec3 p) {
        return m.at(1, 0) * p.x() + m.at(1, 1) * p.y() + m.at(1, 2) * p.z() + m.at(1, 3);
    }

    private BikeFigures figures() {
        return new Motorcycle().figures();
    }
}
