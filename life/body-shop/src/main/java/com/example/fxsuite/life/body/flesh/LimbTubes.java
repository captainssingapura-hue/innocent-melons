package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.bone.arm.Humerus;
import com.example.fxsuite.life.body.bone.arm.Ulna;
import com.example.fxsuite.life.body.bone.leg.Femur;
import com.example.fxsuite.life.body.bone.leg.Tibia;
import com.example.fxsuite.life.body.joint.Point;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * The limbs' flesh, any build's: the thigh, the shin, the upper arm and the forearm, each a tube of rings hung on its
 * bones, each ring {@code along} the way between two of its bone's points ({@link Rings#limb}). Where bone lies under
 * the skin (the kneecap, the knee's sides, the shin's crest, the ankle's knobs, the shoulder, the elbow's points, the
 * ulna's edge, the wrist's knobs) a spoke reaches so far beyond the bone; elsewhere so far from the ring's middle,
 * times the girth a build gives that ring. In the drawn figure's centimetres; representative figures, set from memory.
 */
public record LimbTubes() implements StatelessFunctionalObject {

    public static final LimbTubes INSTANCE = new LimbTubes();

    /** The limbs, each ring's free spokes as thick as the girths say: the thigh's five, the shin's, the arm's... */
    public List<Tube> tubes(final double[] thigh, final double[] shin, final double[] arm, final double[] forearm) {
        return List.of(thigh(thigh), shin(shin), upperArm(arm), forearm(forearm));
    }

    private Tube thigh(final double[] g) {
        final Rings r = Rings.INSTANCE;
        final Point<Femur> hip = Femur.Points.HEAD;
        final Point<Femur> knee = Femur.Points.KNEE;
        return new Tube("thigh", Part.RIGHT_THIGH, List.of(
                r.limb("below the groin", hip, knee, 0.143, 0.8, -0.5, 0, g[0],
                        r.f(7.5), r.f(7.5), r.b(2.5), r.f(8.0), r.f(8.5), r.f(7.5), r.f(7.0), r.f(7.2)),
                r.limb("mid-thigh", hip, knee, 0.381, 1.2, 0.3, 0, g[1],
                        r.f(7.2), r.f(7.0), r.f(6.8), r.f(7.2), r.f(7.4), r.f(7.0), r.f(6.6), r.f(6.8)),
                r.limb("low on the thigh", hip, knee, 0.667, 0.9, 0.2, 0, g[2],
                        r.f(6.2), r.f(6.0), r.f(5.6), r.f(6.0), r.f(6.2), r.f(6.0), r.f(5.8), r.f(6.0)),
                r.limb("above the knee", hip, knee, 0.857, 0.5, 0, 0, g[3],
                        r.f(5.2), r.f(5.0), r.f(4.8), r.f(5.0), r.f(5.2), r.f(5.3), r.f(5.4), r.f(5.2)),
                r.limb("the knee", hip, knee, 0.976, 0.2, 0, 0, g[4],
                        r.b(0.7), r.b(1.0), r.b(1.0), r.f(4.3), r.f(4.6), r.f(4.3), r.b(1.2), r.b(1.0))), List.of());
    }

    private Tube shin(final double[] g) {
        final Rings r = Rings.INSTANCE;
        final Point<Tibia> knee = Tibia.Points.KNEE;
        final Point<Tibia> ankle = Tibia.Points.ANKLE;
        return new Tube("shin", Part.RIGHT_SHIN, List.of(
                r.limb("below the knee", knee, ankle, 0.024, 0, 0, 0, g[0],
                        r.b(0.8), r.b(1.2), r.b(1.5), r.f(4.6), r.f(5.0), r.f(4.6), r.b(1.4), r.b(1.0)),
                r.limb("the calf", knee, ankle, 0.238, 0.2, -1.2, 0, g[1],
                        r.b(0.5), r.f(4.0), r.f(5.0), r.f(5.8), r.f(6.4), r.f(5.8), r.f(4.8), r.b(0.7)),
                r.limb("mid-shin", knee, ankle, 0.524, 0.3, -0.8, 0, g[2],
                        r.b(0.5), r.f(3.4), r.f(4.0), r.f(4.4), r.f(4.6), r.f(4.2), r.f(3.6), r.b(0.6)),
                r.limb("low on the shin", knee, ankle, 0.786, 0.3, -0.6, 0, g[3],
                        r.b(0.6), r.f(2.8), r.f(3.0), r.f(3.1), r.f(3.2), r.f(3.0), r.f(2.8), r.b(0.6)),
                r.limb("the ankle", knee, ankle, 0.976, 0.3, -0.4, 0, g[4],
                        r.f(2.6), r.f(2.6), r.b(0.5), r.f(2.8), r.f(3.0), r.f(2.8), r.b(0.5), r.f(2.5))), List.of());
    }

    private Tube upperArm(final double[] g) {
        final Rings r = Rings.INSTANCE;
        final Point<Humerus> shoulder = Humerus.Points.HEAD;
        final Point<Humerus> elbow = Humerus.Points.CAPITULUM;
        return new Tube("upper arm", Part.RIGHT_UPPER_ARM, List.of(
                r.limb("the shoulder", shoulder, elbow, 0.032, 0.26, -0.02, 0, g[0],
                        r.b(2.2), r.b(2.4), r.b(2.6), r.b(2.4), r.b(2.4), r.f(3.6), r.f(3.4), r.f(3.6)),
                r.limb("the deltoid", shoulder, elbow, 0.258, 0.15, -0.18, 0, g[1],
                        r.f(4.6), r.f(4.9), r.f(5.0), r.f(4.8), r.f(4.8), r.f(4.2), r.f(4.0), r.f(4.3)),
                r.limb("the biceps", shoulder, elbow, 0.548, -0.34, -0.18, 0, g[2],
                        r.f(4.8), r.f(4.4), r.f(4.1), r.f(4.3), r.f(4.8), r.f(4.3), r.f(4.0), r.f(4.4)),
                r.limb("low on the arm", shoulder, elbow, 0.839, -0.93, -0.39, 0, g[3],
                        r.f(4.2), r.f(3.8), r.f(3.6), r.f(3.6), r.f(3.9), r.f(3.6), r.f(3.6), r.f(3.9)),
                r.limb("the elbow", shoulder, elbow, 0.984, -1.13, -0.49, 0, g[4],
                        r.f(3.6), r.f(3.4), r.b(0.8), r.f(3.2), r.f(3.4), r.f(3.2), r.b(0.8), r.f(3.5))), List.of());
    }

    private Tube forearm(final double[] g) {
        final Rings r = Rings.INSTANCE;
        final Point<Ulna> elbow = Ulna.Points.TROCHLEA;
        final Point<Ulna> wrist = Ulna.Points.HEAD;
        return new Tube("forearm", Part.RIGHT_FOREARM, List.of(
                r.limb("the elbow", elbow, wrist, 0, 0.8, -0.36, 0, g[0],
                        r.f(3.8), r.f(3.9), r.f(4.0), r.f(3.6), r.b(0.6), r.f(3.6), r.f(4.0), r.f(3.9)),
                r.limb("high on the forearm", elbow, wrist, 0.217, 1.25, -0.08, 0.217, g[1],
                        r.f(4.2), r.f(4.4), r.f(4.4), r.f(3.9), r.f(3.6), r.f(3.9), r.f(4.2), r.f(4.2)),
                r.limb("mid-forearm", elbow, wrist, 0.543, 1.6, 0.08, 0.543, g[2],
                        r.f(3.3), r.f(3.5), r.f(3.6), r.f(3.1), r.f(2.8), r.b(0.6), r.f(3.3), r.f(3.3)),
                r.limb("low on the forearm", elbow, wrist, 0.869, 1.86, 0.34, 0.869, g[3],
                        r.f(2.3), r.f(2.6), r.f(2.9), r.f(2.4), r.f(2.0), r.f(2.4), r.f(2.8), r.f(2.4)),
                r.limb("the wrist", elbow, wrist, 1.0, 1.9, 0.45, 1.0, g[4],
                        r.f(1.9), r.f(2.3), r.b(0.4), r.f(2.1), r.f(1.8), r.f(2.2), r.b(0.4), r.f(2.2))), List.of());
    }
}
