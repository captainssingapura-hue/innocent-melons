package melons.innocent.vehiclelab.motorcycle;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * The boxer's pipes and footrests: from under each cylinder's head a pipe, down ahead of it, back low along its side
 * clear of the frame, and up into a muffler, long and round-ended, either side of the back wheel and clear of it;
 * the pipes bright. And a footrest each side under the rider's feet, rubber, as the grips are.
 *
 * @param shapes what it is shaped with
 */
record Pipes(Shapes shapes) {

    /** How round a pipe is, and a muffler. */
    private static final double PIPE = 2.2;
    private static final double MUFFLER = 5.5;

    /** Its parts: the pipes with their mufflers, and the footrests. */
    List<Part> parts() {
        final List<Mesh> pipes = new ArrayList<>();
        final List<Mesh> rests = new ArrayList<>();
        for (final int side : List.of(-1, 1)) {
            pipes.add(shapes.pipe(List.of(new Vec3(side * 36, 30, 20), new Vec3(side * 30, 18, 30),
                    new Vec3(side * 20, 14, 8), new Vec3(side * 20, 16, -30), new Vec3(side * 21, 20, -40)), PIPE));
            pipes.add(shapes.capsule(new Vec3(side * 21, 20, -40), new Vec3(side * 22, 25, -92), MUFFLER));
            rests.add(shapes.capsule(new Vec3(side * 14, 26, -12), new Vec3(side * 24, 26, -12), 1.6));
        }
        return List.of(new Part("pipes", Part.Slot.BRIGHT, Part.Moves.BODY, shapes.joined(pipes)),
                new Part("footrests", Part.Slot.TYRES, Part.Moves.BODY, shapes.joined(rests)));
    }
}
