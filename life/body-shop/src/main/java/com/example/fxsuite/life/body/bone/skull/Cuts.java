package com.example.fxsuite.life.body.bone.skull;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.fxsuite.life.body.Sketch.v;

/**
 * Where a bone of the skull is, to cut the whole skull down to ({@link Cranium}): sides of planes, each set in a little
 * so that a suture opens between neighbours, as drawn on a sketch (so mirrored with it), and where all, any, or one but
 * none of others are. The vault's sutures by name: the coronal between the frontal bone and the parietals, the lambdoid
 * between them and the occipital; the middle between the right and the left.
 */
public record Cuts() implements StatelessFunctionalObject {

    public static final Cuts INSTANCE = new Cuts();

    /** How wide a suture is, cm. */
    public static final double SUTURE = 0.035;

    /** The side of the plane through {@code at} away from {@code out}. */
    public Field.Shape half(final Sketch s, final V3 at, final V3 out) {
        return s.half(at, out, SUTURE);
    }

    /** Above {@code y}, below it; ahead of {@code z}, behind it. */
    public Field.Shape above(final Sketch s, final double y) {
        return half(s, v(0, y, 0), v(0, -1, 0));
    }

    public Field.Shape below(final Sketch s, final double y) {
        return half(s, v(0, y, 0), v(0, 1, 0));
    }

    public Field.Shape ahead(final Sketch s, final double z) {
        return half(s, v(0, 0, z), v(0, 0, -1));
    }

    public Field.Shape behind(final Sketch s, final double z) {
        return half(s, v(0, 0, z), v(0, 0, 1));
    }

    /** Further out to the right than {@code x}. */
    public Field.Shape outside(final Sketch s, final double x) {
        return half(s, v(x, 0, 0), v(-1, 0, 0));
    }

    /** Within {@code x} of the middle, either side. */
    public Field.Shape within(final Sketch s, final double x) {
        return all(half(s, v(x, 0, 0), v(1, 0, 0)), half(s, v(-x, 0, 0), v(-1, 0, 0)));
    }

    /** To the right of the middle. */
    public Field.Shape right(final Sketch s) {
        return outside(s, 0);
    }

    /** Ahead of the coronal suture, from the top of the head down to the temples. */
    public Field.Shape aheadOfCoronal(final Sketch s) {
        return half(s, v(0, 14.6, 1.2), v(0, -0.29, -0.957));
    }

    public Field.Shape behindCoronal(final Sketch s) {
        return half(s, v(0, 14.6, 1.2), v(0, 0.29, 0.957));
    }

    /** Behind the lambdoid suture, from the back of the top of the head down to behind the ears. */
    public Field.Shape behindLambdoid(final Sketch s) {
        return half(s, v(0, 12.6, -7.0), v(0, 0.104, 0.995));
    }

    public Field.Shape aheadOfLambdoid(final Sketch s) {
        return half(s, v(0, 12.6, -7.0), v(0, -0.104, -0.995));
    }

    /** Where every one of {@code shapes} is. */
    public Field.Shape all(final Field.Shape... shapes) {
        final List<Field.Element> e = new ArrayList<>();
        for (int k = 0; k < shapes.length; k++) {
            e.add(new Field.Element("cut", shapes[k], k == 0 ? Field.Op.ADD : Field.Op.CLIP, 0));
        }
        return Field.Compound.of(e);
    }

    /** Where any of {@code shapes} is. */
    public Field.Shape any(final Field.Shape... shapes) {
        final List<Field.Element> e = new ArrayList<>();
        for (final Field.Shape shape : shapes) {
            e.add(new Field.Element("cut", shape, Field.Op.ADD, 0));
        }
        return Field.Compound.of(e);
    }

    /** Where {@code shape} is but none of {@code not}. */
    public Field.Shape but(final Field.Shape shape, final Field.Shape... not) {
        final List<Field.Element> e = new ArrayList<>();
        e.add(new Field.Element("cut", shape, Field.Op.ADD, 0));
        for (final Field.Shape n : not) {
            e.add(new Field.Element("cut", n, Field.Op.CARVE, 0));
        }
        return Field.Compound.of(e);
    }

    /** The whole skull, a woman's as far as said, cut down to {@code where}. */
    public void cut(final Sketch s, final double female, final Field.Shape where) {
        Cranium.INSTANCE.draw(s, female);
        s.blend(0).clip().shape("where it is", where);
    }
}
