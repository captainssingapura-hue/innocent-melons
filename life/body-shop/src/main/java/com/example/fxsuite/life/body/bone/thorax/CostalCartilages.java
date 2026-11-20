package com.example.fxsuite.life.body.bone.thorax;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.bone.Kind;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * One side's costal cartilages, drawn in the body's frame at rest and moving with the chest: from each rib's end to the
 * sternum, to the cartilage above, or a free tip. Not bone, so not counted among the bones.
 */
public record CostalCartilages() implements Bone {

    public static final CostalCartilages INSTANCE = new CostalCartilages();

    @Override
    public String name() {
        return "costal cartilages";
    }

    @Override
    public Region region() {
        return Region.THORAX;
    }

    @Override
    public Part part() {
        return Part.CHEST;
    }

    @Override
    public double coarsest() {
        return 0.15;
    }

    @Override
    public boolean paired() {
        return true;
    }

    @Override
    public boolean inBody() {
        return true;
    }

    @Override
    public Kind kind() {
        return Kind.CARTILAGE;
    }

    @Override
    public Paint paint() {
        return Paint.CARTILAGE;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        for (final Rib rib : Rib.values()) {
            Ribs.INSTANCE.cartilage(s, rib, 1 - 0.05 * female);
        }
    }
}
