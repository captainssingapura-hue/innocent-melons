package com.example.fxsuite.life.body.muscle.arm;

import com.example.fxsuite.life.body.muscle.leg.LegVolumes;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.Optional;

/**
 * How big an arm's muscles are, as measured: each muscle crossing the shoulder, the elbow or the wrist so much of the
 * whole arm's, its share as Holzbaur and others measured it by MRI in ten adults, 157 to 188 cm, the shares much the
 * same in every one whatever their size or sex (Holzbaur et al. 2007, table 2). The whole arm's muscle, which they did
 * not find a body's height and weight to tell, taken as the leg's in the same body ({@link LegVolumes}) times as much as
 * their mean subject's was of the leg's Handsfield's line gives one of that height and weight: 2554 cm3 at 1.715 m and
 * 69.2 kg, of 6868, 0.372 of it. The muscles drawn in parts shared among them as is usual: the deltoid three tenths its
 * front, four its middle and three its back; the triceps four tenths its long head, seven twentieths its lateral, a
 * quarter its medial; the biceps eleven twentieths its long head; the pectoralis major three tenths its clavicular head.
 * One arm's.
 */
public record ArmVolumes() implements StatelessFunctionalObject {

    public static final ArmVolumes INSTANCE = new ArmVolumes();

    /** The whole of one arm's muscle, cm3, for a body {@code height} m tall and {@code mass} kg. */
    public double whole(final double height, final double mass) {
        return 0.372 * LegVolumes.INSTANCE.whole(height, mass);
    }

    /** Muscle {@code name}'s volume, cm3, in an arm of {@code whole} cm3 of muscle; none if it was not measured. */
    public Optional<Double> volume(final String name, final double whole) {
        final double share = share(name);
        return share == 0 ? Optional.empty() : Optional.of(share / 100 * whole);
    }

    /** Muscle {@code name}'s share of the arm's muscle, per cent; none, 0. */
    private double share(final String name) {
        return switch (name) {
            case "latissimus dorsi" -> 9.8;
            case "pectoralis major, clavicular head" -> 10.7 * 0.3;
            case "pectoralis major, sternal head" -> 10.7 * 0.7;
            case "deltoid, front" -> 15.2 * 0.3;
            case "deltoid, middle" -> 15.2 * 0.4;
            case "deltoid, back" -> 15.2 * 0.3;
            case "supraspinatus" -> 2.0;
            case "infraspinatus" -> 4.8;
            case "subscapularis" -> 6.6;
            case "teres minor" -> 1.1;
            case "teres major" -> 1.3;
            case "coracobrachialis" -> 0.9;
            case "triceps brachii, long head" -> 14.5 * 0.4;
            case "triceps brachii, lateral head" -> 14.5 * 0.35;
            case "triceps brachii, medial head" -> 14.5 * 0.25;
            case "biceps brachii, long head" -> 5.6 * 0.55;
            case "biceps brachii, short head" -> 5.6 * 0.45;
            case "brachialis" -> 5.7;
            default -> forearm(name);
        };
    }

    /** The forearm's shares, per cent. */
    private double forearm(final String name) {
        return switch (name) {
            case "brachioradialis" -> 2.5;
            case "anconeus" -> 0.4;
            case "supinator" -> 0.8;
            case "pronator teres" -> 1.5;
            case "pronator quadratus" -> 0.4;
            case "extensor carpi radialis brevis" -> 0.9;
            case "extensor carpi radialis longus" -> 1.5;
            case "extensor carpi ulnaris" -> 0.7;
            case "flexor carpi radialis" -> 1.3;
            case "flexor carpi ulnaris" -> 1.5;
            case "palmaris longus" -> 0.4;
            case "extensor digitorum" -> 1.1;
            case "extensor digiti minimi" -> 0.3;
            case "extensor indicis" -> 0.2;
            case "extensor pollicis longus" -> 0.3;
            case "extensor pollicis brevis" -> 0.2;
            case "flexor digitorum superficialis" -> 3.0;
            case "flexor digitorum profundus" -> 3.7;
            case "flexor pollicis longus" -> 0.7;
            case "abductor pollicis longus" -> 0.5;
            default -> 0;
        };
    }
}
