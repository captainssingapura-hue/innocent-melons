package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.List;

/**
 * What drives a twin's back wheel from its gearbox, on the bike's right ({@link BeltParts}): the output pulley turning
 * in its bearing in the crankcase's back end, the belt seated on it and on the rear pulley, which turns on the back
 * axle. Made once, its parts kept, so it can say how far each has turned and where the belt's teeth have run to when
 * the bike has rolled so far.
 */
final class BeltDrive {

    private static final Vec3 ALONG = new Vec3(0, 0, 1);

    private final BeltFigures figures;
    private final BeltParts made;
    private final Part output;
    private final Part belt;
    private final Part rear;

    /** Its parts, made to {@code figures}. */
    BeltDrive(final EngineFigures figures) {
        this.figures = new BeltFigures(figures);
        this.made = new BeltParts(figures);
        this.output = made.output();
        this.belt = made.belt();
        this.rear = made.rear();
    }

    /** How its parts are joined to {@code block}, in the order they are walked. */
    List<Connection> joined(final Part block) {
        return List.of(
                new Connection(output.name() + " in its bearing", block, "countershaft, right", output,
                        "countershaft", Joining.HINGE),
                new Connection(belt.name() + " on the output pulley", output, "belt", belt, "front", Joining.SEATED),
                new Connection(rear.name() + " in the belt", belt, "rear", rear, "axle", Joining.SEATED));
    }

    /** Whether {@code part} is its belt, which runs round its pulleys rather than moving whole. */
    boolean belt(final Part part) {
        return part == belt;
    }

    /** Whether {@code part} is one of its parts. */
    boolean has(final Part part) {
        return part == output || part == belt || part == rear;
    }

    /** Where its belt's teeth are, the bike having rolled {@code rolled}. */
    List<Mat4> teeth(final double rolled) {
        return made.places(rolled);
    }

    /** One of its belt's teeth, in its own frame. */
    Mesh tooth() {
        return made.tooth();
    }

    /**
     * How far {@code part} has turned, the bike having rolled {@code rolled} forward: the rear pulley with the back
     * wheel, the output pulley as many times as fast as its teeth are fewer, the same way; none if not a pulley.
     */
    Mat4 moved(final Part part, final double rolled) {
        final double wheel = Math.toDegrees(figures.wheel(rolled));
        if (part == rear) {
            return about(figures.axle(), wheel);
        }
        if (part == output) {
            return about(figures.countershaft(), wheel * BeltFigures.REAR / BeltFigures.FRONT);
        }
        return new Mat4();
    }

    /** Turned {@code degrees} forward about the axis through {@code middle}, along the crank. */
    private Mat4 about(final Spot middle, final double degrees) {
        final Vec3 c = new Vec3(middle.u(), middle.v(), 0);
        return new Mat4(c).times(new Turned(ALONG, -degrees).matrix()).times(new Mat4(c.times(-1)));
    }

    /** How many parts it has. */
    int count() {
        return 3;
    }
}
