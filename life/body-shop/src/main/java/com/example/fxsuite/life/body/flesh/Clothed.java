package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

/**
 * A body's flesh drawn, and what it wears cut from it ({@link Tailor}): its skin where it is bare, and its tights, if
 * any, each its own surface posed afresh each frame ({@link FleshView}). The skin's material to colour as a skin tone
 * says; the cloth coloured as it is, a little sheen on it.
 */
public final class Clothed {

    private final FleshView bare;
    private final FleshView worn;
    private final Group node = new Group();

    /**
     * {@code skin}, a body {@code scale} times the drawn figure, in {@code tights} (or none), drawn with each way's
     * sign as {@code signs} has it, posed at first as {@code rig} holds it.
     */
    public Clothed(final Flesh skin, final Tights tights, final double scale, final V3 signs, final Rig rig) {
        if (tights == null) {
            bare = new FleshView(skin, signs, rig);
            worn = null;
            node.getChildren().add(bare.node());
        } else {
            final Tailor.Clad clad = Tailor.INSTANCE.clad(skin, tights, scale);
            bare = new FleshView(clad.bare(), signs, rig);
            worn = new FleshView(clad.worn(), signs, rig);
            worn.material().setDiffuseColor(Color.web(tights.colour()));
            worn.material().setSpecularColor(Color.web("#6a6a78"));
            worn.material().setSpecularPower(24);
            node.getChildren().addAll(bare.node(), worn.node());
        }
    }

    /** What to put in the scene. */
    public Node node() {
        return node;
    }

    /** The skin's material, to colour. */
    public PhongMaterial skin() {
        return bare.material();
    }

    /** Posed as {@code rig} holds the body. */
    public void show(final Rig rig) {
        bare.show(rig);
        if (worn != null) {
            worn.show(rig);
        }
    }
}
