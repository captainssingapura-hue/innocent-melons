package com.example.fxsuite.texture.brick;

import com.example.fxsuite.vulkan.term.Point;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Turns;

/**
 * A wall's bricks as they are laid, as terms: at a point of the wall's face (how far across it, and how far up from
 * its foot), which course the point is in, which brick of the course, and how much of what a pixel covers there is
 * mortar. Courses rise from the foot, each a brick and a joint high, the first even; the even and the odd are laid
 * as the bond says.
 */
public record Laying(Brick brick, Bond bond, double joint) {

    public Laying {
        if (!(joint > 0)) {
            throw new IllegalArgumentException("bricks are laid with some mortar between: " + joint);
        }
    }

    /** How high a course is with the joint over it, in centimetres. */
    public double rise() {
        return brick.height() + joint;
    }

    /** Which course the point is in, counted from the foot: a whole number. */
    public Real course(final Point at) {
        return at.up().over(rise()).floored();
    }

    /** Which brick of its course the point is in, counted along the course: a whole number. */
    public Real brick(final Point at) {
        // 1 in an odd course, 0 in an even: exactly, a course being a whole number
        final Real odd = course(at).over(2).part().times(2);
        return even().brick(along(at, bond.even(brick, joint)))
                .mixed(odd().brick(along(at, bond.odd(brick, joint))), odd);
    }

    /**
     * How much of what a pixel covers at the point is mortar: all of it in a joint and none of it on a brick, looked
     * at closely; from far off, the joints' share of the whole wall, everywhere.
     */
    public Real mortar(final Point at, final Real covers) {
        final Real brickwork = new Real(1).less(beds(at, covers)).times(new Real(1).less(uprights(at, covers)));
        return new Real(1).less(brickwork);
    }

    /** How much of what a pixel covers is the joint a course is bedded in: the top of each rise. */
    private Real beds(final Point at, final Real covers) {
        return new Turns(at.up().less(new Real(brick.height())).over(rise()), new Real(1 / rise()))
                .pulse(joint / rise(), covers);
    }

    /** How much of what a pixel covers is upright joint: the even course's or the odd's, as the point's course is. */
    private Real uprights(final Point at, final Real covers) {
        // how much of what the pixel covers is odd course: the upper half of every two rises
        final Real odd = new Turns(at.up().over(2 * rise()).less(new Real(0.5)), new Real(1 / (2 * rise())))
                .pulse(0.5, covers);
        return even().joints(along(at, bond.even(brick, joint)), covers)
                .mixed(odd().joints(along(at, bond.odd(brick, joint)), covers), odd);
    }

    /** How far along {@code course} the point is, from where its faces begin to come round. */
    private Real along(final Point at, final Course course) {
        return at.across().less(new Real(course.shifted()));
    }

    private Run even() {
        return new Run(bond.even(brick, joint), joint);
    }

    private Run odd() {
        return new Run(bond.odd(brick, joint), joint);
    }
}
