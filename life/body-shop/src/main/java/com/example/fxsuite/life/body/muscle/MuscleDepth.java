package com.example.fxsuite.life.body.muscle;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * How deep each muscle lies among those about it ({@link Muscle#depth}), the outermost 1: where two lie over each
 * other, the deeper holds and the shallower lies over it. The back's in the layers anatomy gives them: the trapezius
 * and the latissimus; under them the levator scapulae and the rhomboids; the serratus posterior; the splenius; the
 * erector spinae; the semispinalis of the head, over that of the neck and the chest, over the multifidus; the short
 * muscles from one vertebra to the next, and those under the skull. The neck's, from the front: the platysma; the
 * sternocleidomastoid; the strap muscles, the sternohyoid, the omohyoid and the digastric, over the sternothyroid, the
 * thyrohyoid, the stylohyoid and the mylohyoid, over the geniohyoid; the scalenes; those on the front of the spine.
 * The rest as their layer says, superficial or deep.
 */
public record MuscleDepth() implements StatelessFunctionalObject {

    public static final MuscleDepth INSTANCE = new MuscleDepth();

    /** How deep {@code m} lies, the outermost 1. */
    public int of(final Muscle m) {
        final String n = m.name();
        if (n.startsWith("rotator") || n.startsWith("interspinalis") || n.startsWith("intertransversarius")
                || n.startsWith("levator costarum") || n.startsWith("rectus capitis posterior")
                || n.startsWith("obliquus capitis") || n.startsWith("longus") || n.startsWith("rectus capitis")) {
            return 9;
        }
        return switch (n) {
            case "platysma", "trapezius, upper", "trapezius, middle", "trapezius, lower", "latissimus dorsi" -> 1;
            case "sternocleidomastoid", "levator scapulae", "rhomboid minor", "rhomboid major" -> 2;
            case "serratus posterior superior", "serratus posterior inferior", "sternohyoid", "omohyoid",
                 "digastric, anterior belly", "digastric, posterior belly" -> 3;
            case "splenius capitis", "splenius cervicis", "sternothyroid", "thyrohyoid", "stylohyoid", "mylohyoid" -> 4;
            case "iliocostalis", "longissimus", "longissimus capitis", "spinalis", "geniohyoid", "scalenus anterior",
                 "scalenus medius", "scalenus posterior" -> 5;
            case "semispinalis capitis" -> 6;
            case "semispinalis cervicis", "semispinalis thoracis" -> 7;
            case "multifidus" -> 8;
            default -> m.layer() == Layer.DEEP ? 4 : 1;
        };
    }
}
