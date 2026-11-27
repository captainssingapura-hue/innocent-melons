package com.example.fxsuite.life.body.vulkan.lineup;

import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.vulkan.form.Mesh;

import java.util.List;

/**
 * A figure on the line-up as the card draws it: what kind of figure it is
 * ({@link com.example.fxsuite.existence.Figures}), the body it is built as, as that body is bound, and its pieces,
 * every one hard, each in the body's frame as bound and moving with its part, so that it goes as the body does.
 *
 * @param kind   what kind of figure it is
 * @param bound  its body as bound, which its pieces are made on
 * @param pieces its pieces
 */
public record Avatar(Leaf<?> kind, Rig bound, List<Piece> pieces) {

    public Avatar {
        pieces = List.copyOf(pieces);
    }

    /**
     * A piece of {@code part}, of what {@code paint} says it is of, its shell, its frame or another, drawn as the
     * figure's paintwork has that ({@link Paintwork}); its surface {@code mesh}, in centimetres, up the way {@code y}
     * grows.
     */
    public record Piece(Part part, Paint paint, Mesh mesh) {
    }

    /** How many triangles it is drawn with. */
    public long triangles() {
        return pieces.stream().mapToLong(p -> p.mesh().corners().size() / 3).sum();
    }
}
