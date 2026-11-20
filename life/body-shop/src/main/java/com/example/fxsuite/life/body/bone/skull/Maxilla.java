package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;

/**
 * A maxilla, one a side, the upper jaw: the face under the orbit beside the nose, the upper teeth's arch, the front of
 * the hard palate; all of the face there but the zygomatic, the nasal, the ethmoid, the lacrimal and the palatine.
 */
public record Maxilla() implements Bone {

    public static final Maxilla INSTANCE = new Maxilla();

    @Override
    public String name() {
        return "maxilla";
    }

    @Override
    public Region region() {
        return Region.SKULL;
    }

    @Override
    public Part part() {
        return Part.HEAD;
    }

    @Override
    public double coarsest() {
        return 0.12;
    }

    @Override
    public boolean paired() {
        return true;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        final Cuts c = Cuts.INSTANCE;
        c.cut(s, female, c.but(c.all(c.right(s), c.below(s, 5.2), c.above(s, -1.5), c.ahead(s, 3.0)),
                c.all(c.outside(s, 4.0), c.above(s, 1.6), c.ahead(s, 3.0)),
                c.all(c.within(s, 1.2), c.above(s, 3.9), c.ahead(s, 9.0)), c.all(c.within(s, 1.0), c.above(s, 3.4)),
                c.all(c.within(s, 2.0), c.above(s, 3.6), c.ahead(s, 6.6), c.behind(s, 8.2)),
                c.all(c.within(s, 2.6), c.below(s, 1.2), c.behind(s, 4.6))));
    }
}
