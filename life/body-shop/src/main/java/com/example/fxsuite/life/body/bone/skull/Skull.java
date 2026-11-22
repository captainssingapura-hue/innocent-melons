package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.bone.Bone;
import com.example.fxsuite.life.body.bone.Region;
import com.example.fxsuite.life.body.joint.Place;
import com.example.fxsuite.life.body.joint.Point;

import java.util.function.Supplier;

/**
 * The skull, its lower jaw apart: its other twenty-one bones knit fast along their sutures into one, which moves as
 * one, the cranium ({@link Cranium}). Where it meets what moves on it: its condyles on the atlas, the hollows the jaw
 * turns in, the upper teeth's sockets, the inner ears' windows the stirrups sit in.
 */
public record Skull() implements Bone {

    public static final Skull INSTANCE = new Skull();

    /** How many of the body's bones it is: the skull's twenty-two but the jaw, knit into one. */
    private static final int BONES = 21;

    /** Where what moves on it meets it, as on the right, each a frame in the frame it is drawn in. */
    public enum Points implements Point<Skull> {
        ATLAS("its condyles, on the atlas", () -> SkullContacts.INSTANCE.atlas()),
        MANDIBLE("the hollows the jaw turns in", () -> SkullContacts.INSTANCE.jaw()),
        TEETH("the upper jaw's arch, round the upper teeth",
                () -> SkullContacts.INSTANCE.between(SkullContacts.MAXILLA, SkullContacts.UPPER_TEETH)),
        STAPES("the inner ear's window, the stirrup in it",
                () -> SkullContacts.INSTANCE.between(SkullContacts.STAPES, SkullContacts.OVAL_WINDOW));

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
        return "skull";
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
    public int count() {
        return BONES;
    }

    @Override
    public double coarsest() {
        return 0.12;
    }

    @Override
    public void draw(final Sketch s, final double female) {
        Cranium.INSTANCE.draw(s, female);
    }
}
