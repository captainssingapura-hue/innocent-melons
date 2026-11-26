package com.example.fxsuite.life.body.vulkan;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Looked;
import com.example.fxsuite.vulkan.render.Shaped;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.view.Camera;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * What the body wears as the card has it ({@link BodyView}): its hard pieces, sent once as the body is bound and each
 * drawn moved as its part has, so that they go as the body does; its cloth, sent posed with its skin, a stopped body
 * at a time, those posed before let go, and drawn only while the skin is. Each drawn if it is worn, in what it is shown
 * made of.
 */
final class WornDrawn {

    /** A hard piece sent to the card. */
    private record Hard(BodyPieces.Worn worn, Shaped shaped) {
    }

    /** A piece of cloth sent to the card, posed. */
    private record Soft(Leaf<?> kind, Shaped shaped) {
    }

    private final List<Hard> hard = new ArrayList<>();
    private final List<Soft> soft = new ArrayList<>();

    /** {@code worn}'s hard pieces sent to the card by {@code sent}. */
    void bound(final List<BodyPieces.Worn> worn, final Function<BodyPieces.Piece, Shaped> sent) {
        for (final BodyPieces.Worn w : worn) {
            hard.add(new Hard(w, sent.apply(w.bone().piece())));
        }
    }

    /** {@code clothes}, posed, sent by {@code sent} in place of those posed before, which {@code let} lets go. */
    void posed(final List<BodyPieces.Cloth> clothes, final Function<BodyPieces.Piece, Shaped> sent,
               final Consumer<Shaped> let) {
        soft.forEach(s -> let.accept(s.shaped()));
        soft.clear();
        for (final BodyPieces.Cloth c : clothes) {
            soft.add(new Soft(c.kind(), sent.apply(c.piece())));
        }
    }

    /**
     * What it wears, as {@code w} says, drawn by {@code pass} in {@code looks}, seen by {@code camera}: each hard piece
     * moved as its part has, {@code moved}, a visor in glass; its cloth, if {@code fleshed}, stood by {@code stands}.
     */
    void drawn(final Pass pass, final WornShown w, final Map<Material, Looked> looks, final Map<Part, Mat4> moved,
               final Mat4 stands, final boolean fleshed, final Camera camera) {
        for (final Hard on : hard) {
            final Mat4 at = moved.get(on.worn().bone().part());
            if (at != null && w.wears(on.worn().kind())) {
                looks.get(on.worn().glass() ? w.glass() : w.material()).drawn(pass, on.shaped(), at, camera, 1,
                        on.worn().glass() ? w.visor() : w.colour());
            }
        }
        for (final Soft on : soft) {
            if (fleshed && w.wears(on.kind())) {
                looks.get(w.fabric()).drawn(pass, on.shaped(), stands, camera, 1, w.cloth());
            }
        }
    }
}
