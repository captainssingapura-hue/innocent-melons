package com.example.fxsuite.life.body.muscle.head;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.Optional;

/**
 * How big a neck's muscles are, as measured: each so much of the whole neck's, its share as Zheng and others measured
 * it by MRI in seven young men, each muscle from its upper end to its lower or to the top of the second thoracic
 * vertebra (Zheng et al. 2013, J Biomech 46:899, table 3); the whole neck's, both sides, by their line for men on a
 * body's mass for its height, 482 + 13.0 times it, kg/m2, cm3 (their equation 3). What they measured together shared
 * among its muscles as Borst found them by mass in a man's neck dissected (his thesis, TU Delft 2009, appendix C): the
 * splenius, 27.1 g its head's part to 10.7 its neck's; the scalenes, 4.0 g the front one, 11.7 the middle, 5.5 the
 * back; the infrahyoids, 3.9 g the sternohyoid, 4.3 the sternothyroid, 5.1 the omohyoid, 2.4 the thyrohyoid; of
 * the semispinalis of the neck and the multifidus, 20.0 g to 16.9, the multifidus, drawn the spine's length, left as
 * drawn. Their trapezius, down to the second thoracic vertebra, the trapezius's upper part. One side's.
 */
public record NeckVolumes() implements StatelessFunctionalObject {

    public static final NeckVolumes INSTANCE = new NeckVolumes();

    /** The whole of one side of a neck's muscle, cm3, for a body {@code height} m tall and {@code mass} kg. */
    public double whole(final double height, final double mass) {
        return (482 + 13.0 * mass / (height * height)) / 2;
    }

    /** Muscle {@code name}'s volume, cm3, in a neck of {@code whole} cm3 of muscle; none if it was not measured. */
    public Optional<Double> volume(final String name, final double whole) {
        final double share = share(name);
        return share == 0 ? Optional.empty() : Optional.of(share / 100 * whole);
    }

    /** Muscle {@code name}'s share of the neck's muscle, per cent; none, 0. */
    private double share(final String name) {
        return switch (name) {
            case "trapezius, upper" -> 28.7;
            case "sternocleidomastoid" -> 14.0;
            case "semispinalis capitis" -> 10.7;
            case "splenius capitis" -> 9.9 * 27.1 / 37.8;
            case "splenius cervicis" -> 9.9 * 10.7 / 37.8;
            case "levator scapulae" -> 10.0;
            case "semispinalis cervicis" -> 7.0 * 20.0 / 36.9;
            case "scalenus anterior" -> 6.3 * 4.0 / 21.2;
            case "scalenus medius" -> 6.3 * 11.7 / 21.2;
            case "scalenus posterior" -> 6.3 * 5.5 / 21.2;
            default -> small(name);
        };
    }

    /** The smaller muscles' shares, per cent. */
    private double small(final String name) {
        return switch (name) {
            case "sternohyoid" -> 2.7 * 3.9 / 15.7;
            case "sternothyroid" -> 2.7 * 4.3 / 15.7;
            case "omohyoid" -> 2.7 * 5.1 / 15.7;
            case "thyrohyoid" -> 2.7 * 2.4 / 15.7;
            case "obliquus capitis inferior" -> 1.9;
            case "obliquus capitis superior" -> 0.6;
            case "longus capitis" -> 1.6;
            case "longus colli" -> 1.7;
            case "longissimus capitis" -> 1.8;
            case "rectus capitis posterior minor" -> 1.1;
            case "rectus capitis posterior major" -> 0.8;
            default -> 0;
        };
    }
}
