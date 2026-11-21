package com.example.fxsuite.life.body.joint;

import com.example.fxsuite.life.body.joint.arm.Elbow;
import com.example.fxsuite.life.body.joint.arm.Glenohumeral;
import com.example.fxsuite.life.body.joint.arm.Sternoclavicular;
import com.example.fxsuite.life.body.joint.foot.FootJoints;
import com.example.fxsuite.life.body.joint.hand.HandJoints;
import com.example.fxsuite.life.body.joint.hand.Wrist;
import com.example.fxsuite.life.body.joint.skull.AtlantoOccipital;
import com.example.fxsuite.life.body.joint.skull.Temporomandibular;
import com.example.fxsuite.life.body.joint.foot.Subtalar;
import com.example.fxsuite.life.body.joint.leg.Ankle;
import com.example.fxsuite.life.body.joint.leg.Hip;
import com.example.fxsuite.life.body.joint.leg.Knee;
import com.example.fxsuite.life.body.joint.spine.SpineJoints;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * Postures to start from, by name: at rest, standing straight, the arms at the sides, the palms forward; a step, the
 * right leg forward on its heel, the left behind on its toes; a squat; on tiptoe; bent forward at the hips and down the
 * spine; reaching forward with the right hand; the arms out to the sides; fists, the forearms up; the mouth open, the
 * head back.
 */
public record Postures() implements StatelessFunctionalObject {

    public static final Postures INSTANCE = new Postures();

    /** A posture, and what it is called. */
    public record Named(String name, Posture posture) {
    }

    /** Every one. */
    public List<Named> all() {
        return List.of(new Named("At rest", Posture.REST), new Named("A step", step()), new Named("A squat", squat()),
                new Named("On tiptoe", tiptoe()), new Named("Bent forward", bent()), new Named("Reaching", reaching()),
                new Named("Arms out", armsOut()), new Named("Fists", fists()), new Named("Mouth open", mouthOpen()));
    }

    private Posture reaching() {
        final Posture p = Posture.REST.with(Glenohumeral.INSTANCE, Side.RIGHT, Movement.FLEXION, 90)
                .with(Sternoclavicular.INSTANCE, Side.RIGHT, Movement.SIDE_BEND, -10)
                .with(Elbow.INSTANCE, Side.RIGHT, Movement.FLEXION, 10)
                .with(Elbow.INSTANCE, Side.RIGHT, Movement.TWIST, 80)
                .with(Wrist.INSTANCE, Side.RIGHT, Movement.FLEXION, -15);
        return hand(p, Side.RIGHT, "metacarpophalangeal joint of the", 15);
    }

    private Posture armsOut() {
        Posture p = Posture.REST;
        for (final Side s : new Side[] {Side.RIGHT, Side.LEFT}) {
            p = p.with(Glenohumeral.INSTANCE, s, Movement.SIDE_BEND, 90)
                    .with(Sternoclavicular.INSTANCE, s, Movement.FLEXION, 10);
        }
        return p;
    }

    private Posture fists() {
        Posture p = Posture.REST;
        for (final Side s : new Side[] {Side.RIGHT, Side.LEFT}) {
            p = p.with(Elbow.INSTANCE, s, Movement.FLEXION, 90).with(Elbow.INSTANCE, s, Movement.TWIST, 85);
            p = hand(p, s, "metacarpophalangeal joint of the", 85);
            p = hand(p, s, "proximal interphalangeal", 95);
            p = hand(p, s, "distal interphalangeal", 65);
            p = hand(p, s, "carpometacarpal joint of the thumb", 35);
            p = hand(p, s, "metacarpophalangeal joint of the thumb", 45);
            p = hand(p, s, "interphalangeal joint of the thumb", 50);
        }
        return p;
    }

    private Posture mouthOpen() {
        return Posture.REST.with(Temporomandibular.INSTANCE, Side.MIDDLE, Movement.FLEXION, 28)
                .with(AtlantoOccipital.INSTANCE, Side.MIDDLE, Movement.FLEXION, -15);
    }

    /**
     * {@code p}, every joint of the hand on {@code side} whose name begins {@code named} flexed {@code degrees}: the
     * thumb's only if named.
     */
    private Posture hand(final Posture p, final Side side, final String named, final double degrees) {
        Posture out = p;
        for (final Complex j : HandJoints.INSTANCE.joints()) {
            if (j.name().startsWith(named) && (named.contains("thumb") || !j.name().contains("thumb"))) {
                out = out.with(j, side, Movement.FLEXION, degrees);
            }
        }
        return out;
    }

    private Posture step() {
        Posture p = Posture.REST.with(Hip.INSTANCE, Side.RIGHT, Movement.FLEXION, 25)
                .with(Knee.INSTANCE, Side.RIGHT, Movement.FLEXION, 5)
                .with(Ankle.INSTANCE, Side.RIGHT, Movement.FLEXION, -10)
                .with(Hip.INSTANCE, Side.LEFT, Movement.FLEXION, -12)
                .with(Knee.INSTANCE, Side.LEFT, Movement.FLEXION, 35)
                .with(Ankle.INSTANCE, Side.LEFT, Movement.FLEXION, 20);
        return toes(p, Side.LEFT, -35);
    }

    private Posture squat() {
        Posture p = Posture.REST;
        for (final Side s : new Side[] {Side.RIGHT, Side.LEFT}) {
            p = p.with(Hip.INSTANCE, s, Movement.FLEXION, 110).with(Hip.INSTANCE, s, Movement.SIDE_BEND, 15)
                    .with(Knee.INSTANCE, s, Movement.FLEXION, 125).with(Ankle.INSTANCE, s, Movement.FLEXION, -20);
        }
        return spine(p, 0.4);
    }

    private Posture tiptoe() {
        Posture p = Posture.REST;
        for (final Side s : new Side[] {Side.RIGHT, Side.LEFT}) {
            p = toes(p.with(Ankle.INSTANCE, s, Movement.FLEXION, 45).with(Subtalar.INSTANCE, s, Movement.FLEXION, 0),
                    s, -50);
        }
        return p;
    }

    private Posture bent() {
        Posture p = Posture.REST;
        for (final Side s : new Side[] {Side.RIGHT, Side.LEFT}) {
            p = p.with(Hip.INSTANCE, s, Movement.FLEXION, 60);
        }
        return spine(p, 0.7);
    }

    /** {@code p}, every toe on {@code side} turned at its metatarsal {@code degrees}. */
    private Posture toes(final Posture p, final Side side, final double degrees) {
        Posture out = p;
        for (final Complex j : FootJoints.INSTANCE.joints()) {
            if (j.name().contains("metatarsophalangeal")) {
                out = out.with(j, side, Movement.FLEXION, degrees);
            }
        }
        return out;
    }

    /** {@code p}, every joint of the spine flexed {@code share} of the way to its end. */
    private Posture spine(final Posture p, final double share) {
        Posture out = p;
        for (final Complex j : SpineJoints.INSTANCE.joints()) {
            if (j.movements().contains(Movement.FLEXION)) {
                out = out.with(j, Side.MIDDLE, Movement.FLEXION, j.range(Movement.FLEXION).at(share));
            }
        }
        return out;
    }
}
