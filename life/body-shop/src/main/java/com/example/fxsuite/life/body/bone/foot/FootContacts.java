package com.example.fxsuite.life.body.bone.foot;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.joint.Axes;
import com.example.fxsuite.life.body.joint.Place;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * Where the foot's bones meet, as on the right, in the foot's frame (from the ankle), or the toes' (from their joint):
 * the talus on the heel bone, turning about the subtalar axis, forward, up and in; the midfoot's bones each where it
 * meets its neighbour, midway between their middles; each metatarsal on its tarsal; the metatarsals' bases side by
 * side; each toe's joints, flexing down.
 */
public record FootContacts() implements StatelessFunctionalObject {

    public static final FootContacts INSTANCE = new FootContacts();

    /** The middles the midfoot's bones meet between. */
    public static final V3 TALUS_HEAD = new V3(-1.0, -2.3, 3.35);
    public static final V3 NAVICULAR = new V3(-1.1, -2.4, 5.05);
    public static final V3 CUBOID = new V3(1.7, -3.65, 4.85);
    public static final V3 MEDIAL = new V3(-1.9, -3.3, 6.45);
    public static final V3 INTERMEDIATE = new V3(-0.5, -2.65, 6.35);
    public static final V3 LATERAL = new V3(0.7, -2.85, 6.4);

    /** The talus on the heel bone: inverting and everting about the subtalar axis. */
    public Place subtalar() {
        return new Place(new V3(0.1, -1.95, 0.0), Axes.INSTANCE.along(new V3(-0.25, 0.45, 0.86), V3.Y));
    }

    /** The heel bone's front on the cuboid's back. */
    public Place calcaneocuboid() {
        return new Place(new V3(1.5, -3.7, 3.5), Axes.UPRIGHT);
    }

    /** Where two bones meet, midway between their middles {@code a} and {@code b}. */
    public Place between(final V3 a, final V3 b) {
        return new Place(a.plus(b).times(0.5), Axes.UPRIGHT);
    }

    /** {@code toe}'s metatarsal's base on its tarsal: a cuneiform, or the cuboid for the outer two. */
    public Place tarsometatarsal(final Toe toe) {
        final V3 tarsal = switch (toe) {
            case GREAT -> MEDIAL;
            case SECOND -> INTERMEDIATE;
            case THIRD -> LATERAL;
            case FOURTH, LITTLE -> CUBOID;
        };
        return between(tarsal, toe.base());
    }

    /** {@code toe}'s metatarsal's base beside the next one's out. */
    public Place intermetatarsal(final Toe toe) {
        return between(toe.base(), Toe.values()[toe.ordinal() + 1].base());
    }

    /** {@code toe} on its metatarsal, the middle of its head: in the foot's frame. */
    public Place metatarsophalangeal(final Toe toe) {
        return new Place(toe.head(), Axes.FORWARD);
    }

    /** The same, in the toes' frame. */
    public Place metatarsophalangealInToes(final Toe toe) {
        return new Place(toe.head().minus(Toes.JOINT), Axes.FORWARD);
    }

    /** The joint before {@code toe}'s phalanx {@code q}, in the toes' frame. */
    public Place interphalangeal(final Toe toe, final int q) {
        return new Place(Toes.INSTANCE.interphalangeal(toe, q), Axes.FORWARD);
    }
}
