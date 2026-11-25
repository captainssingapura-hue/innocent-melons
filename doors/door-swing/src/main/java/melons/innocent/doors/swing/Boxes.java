package melons.innocent.doors.swing;

import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.vulkan.form.Cuboid;
import com.example.fxsuite.vulkan.form.Merged;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A door's pieces as they are gathered: each a box, of one material, told one colour, fixed or turning with the
 * leaf. All the boxes alike in those three become one part, one mesh. A box is laid out along its longest way, as
 * a piece of timber is sawn, and then stood where it belongs, so that a surface with a grain runs it along every
 * stile, rail and bar.
 */
final class Boxes {

    /** What boxes are gathered by. */
    private record Kind(Material of, Vec3 tint, Moves moves) {
    }

    private final Map<Kind, List<Merged.Placed>> gathered = new LinkedHashMap<>();

    /**
     * A box of {@code of}, told {@code tint}, moving as {@code moves} says: its middle at {@code middle} in the
     * door's frame, so far {@code across}, {@code up} and {@code through}.
     */
    void box(final Material of, final Vec3 tint, final Moves moves, final Vec3 middle, final double across,
             final double up, final double through) {
        final Cuboid laid;
        final Mat4 stood;
        if (up >= across && up >= through) {
            // its length stood upright
            laid = new Cuboid(up, across, through);
            stood = new Mat4(new double[] {0, -1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1});
        } else if (through > across) {
            // its length run through the wall
            laid = new Cuboid(through, up, across);
            stood = new Mat4(new double[] {0, 0, -1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1});
        } else {
            laid = new Cuboid(across, up, through);
            stood = new Mat4();
        }
        gathered.computeIfAbsent(new Kind(of, tint, moves), kind -> new ArrayList<>())
                .add(new Merged.Placed(laid.formed(), new Mat4(middle).times(stood)));
    }

    /** The parts: all the boxes of one material, colour and way of moving as one mesh, in the order first met. */
    List<DoorPart> parts() {
        final List<DoorPart> parts = new ArrayList<>();
        gathered.forEach((kind, boxes) -> parts.add(new DoorPart(
                (kind.moves() == Moves.LEAF ? "the leaf's " : "the frame's ") + kind.of().name().replace('-', ' '),
                kind.of(), kind.tint(), kind.moves(), new Merged(boxes).mesh())));
        return parts;
    }
}
