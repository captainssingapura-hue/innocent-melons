package melons.innocent.doors.swing;

import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import com.example.fxsuite.vulkan.space.Vec3;

/**
 * The pieces of a leaf, put where the leaf's own measures say: a piece from so far out from the hinge edge to so
 * far, from so far up to so far, so thick about the middle of the leaf's thickness, or standing off one of its
 * faces. Every piece turns with the leaf.
 *
 * @param boxes   where the pieces are gathered
 * @param hanging how the leaf hangs
 */
record Joinery(Boxes boxes, Hanging hanging) {

    /** How thick a pane of glass set in a wooden leaf is, in centimetres. */
    private static final double PANE = 0.6;

    /** A piece of {@code wood} from {@code out} to {@code far} and {@code low} to {@code high}, so {@code thick}. */
    void timber(final Wood wood, final double out, final double far, final double low, final double high,
                final double thick) {
        piece(wood.leaf(), wood.tint(), out, far, low, high, 0, thick);
    }

    /** A pane of glass from {@code out} to {@code far} and {@code low} to {@code high}, set in a wooden leaf. */
    void pane(final double out, final double far, final double low, final double high) {
        glass(out, far, low, high, PANE);
    }

    /** A sheet of glass from {@code out} to {@code far} and {@code low} to {@code high}, so {@code thick}. */
    void glass(final double out, final double far, final double low, final double high, final double thick) {
        piece(new Materials.Glass(), new Vec3(1, 1, 1), out, far, low, high, 0, thick);
    }

    /**
     * A piece of metal to each face of the leaf: from {@code out} to {@code far} and {@code low} to {@code high},
     * from {@code off} clear of the face to {@code proud} of it.
     */
    void furniture(final Fittings fittings, final double out, final double far, final double low,
                   final double high, final double off, final double proud) {
        final double face = hanging.size().thick() / 2;
        for (final int side : new int[] {-1, 1}) {
            piece(fittings.leaf(), new Vec3(1, 1, 1), out, far, low, high, side * (face + (off + proud) / 2),
                    proud - off);
        }
    }

    /** A piece of metal through the leaf's thickness and so far {@code proud} of each face. */
    void patch(final Fittings fittings, final double out, final double far, final double low, final double high,
               final double proud) {
        piece(fittings.leaf(), new Vec3(1, 1, 1), out, far, low, high, 0, hanging.size().thick() + 2 * proud);
    }

    private void piece(final Material of, final Vec3 tint, final double out, final double far, final double low,
                       final double high, final double back, final double thick) {
        boxes.box(of, tint, Moves.LEAF, hanging.at((out + far) / 2, (low + high) / 2, back), far - out, high - low,
                thick);
    }
}
