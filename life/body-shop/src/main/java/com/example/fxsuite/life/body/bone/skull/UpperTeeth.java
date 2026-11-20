package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.bone.Kind;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

/**
 * The sixteen upper teeth, set in the maxillae's arch, meeting the lower in a bite. Not bones, so not counted.
 */
public record UpperTeeth() implements Bone {

    public static final UpperTeeth INSTANCE = new UpperTeeth();

    /** Where it meets its neighbours, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<UpperTeeth> {
        MAXILLA("their roots, in the maxilla",
                () -> SkullContacts.INSTANCE.between(SkullContacts.MAXILLA, SkullContacts.UPPER_TEETH));

        private final String said;
        private final Supplier<Place> frame;

        Points(final String said, final Supplier<Place> frame) {
            this.said = said;
            this.frame = frame;
        }

        @Override
        public String said() {
            return said;
        }

        @Override
        public Place frame() {
            return frame.get();
        }
    }

    @Override
    public String name() {
        return "upper teeth";
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
        return 0.05;
    }

    @Override
    public Kind kind() {
        return Kind.TEETH;
    }

    @Override
    public Paint paint() {
        return Paint.TEETH;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        Teeth.INSTANCE.draw(s, true);
    }
}
