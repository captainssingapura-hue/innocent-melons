package com.example.fxsuite.life.body.cage;

import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.PartBones;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.BoneView;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import javafx.scene.Group;

/**
 * A still body drawn on a bench, in the scene's frame, where up is {@code -y} and the body faces the viewer from the
 * bench's front: a point of it taken there; its bones, as it is held.
 */
public record CageView() implements StatelessFunctionalObject {

    public static final CageView INSTANCE = new CageView();

    /** {@code v}, in the body's frame, in the scene's. */
    public V3 scene(final V3 v) {
        return new V3(-v.x(), -v.y(), -v.z());
    }

    /**
     * The bones of the body {@code bound} holds, of the parts {@code which} says, as far a woman's as {@code female},
     * as finely as {@code detail}.
     */
    public Group bones(final Rig bound, final double female, final Detail detail,
                       final java.util.function.Predicate<Part> which) {
        final Group g = new Group();
        for (final Part p : Part.values()) {
            if (!which.test(p)) {
                continue;
            }
            final Part right = p.side() < 0 ? p.other() : p;
            final Rig.Frame f = bound.frame(p);
            for (final PartBones.Piece piece : PartBones.INSTANCE.pieces(right, detail, female)) {
                g.getChildren().add(BoneView.INSTANCE.mapped(piece.mesh(), piece.paint(), v -> scene(f.place(v)),
                        p.side() >= 0));
            }
        }
        g.setMouseTransparent(true);
        return g;
    }
}
