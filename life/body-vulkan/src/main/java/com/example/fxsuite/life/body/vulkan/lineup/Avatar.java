package com.example.fxsuite.life.body.vulkan.lineup;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

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
     * A piece of {@code part}, drawn in {@code material}, of the colour {@code colour} if the material takes one, its
     * surface {@code mesh}, in centimetres, up the way {@code y} grows.
     */
    public record Piece(Part part, Material material, Vec3 colour, Mesh mesh) {
    }

    /** How many triangles it is drawn with. */
    public long triangles() {
        return pieces.stream().mapToLong(p -> p.mesh().corners().size() / 3).sum();
    }
}
