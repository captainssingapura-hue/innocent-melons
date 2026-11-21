package com.example.fxsuite.life.body.bone.thorax;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.bone.spine.Discs;
import com.example.fxsuite.life.body.joint.Axes;
import com.example.fxsuite.life.body.joint.Place;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * Where the chest's bones meet, as on the right, in the chest's frame (its bones are drawn in the body's at rest, where
 * the chest stands): each rib on its vertebra, at its head, turning about its neck; each rib's bone on its cartilage,
 * where it ends in front; one side's cartilages on the sternum, where the fourth's reaches it.
 */
public record ThoraxContacts() implements StatelessFunctionalObject {

    public static final ThoraxContacts INSTANCE = new ThoraxContacts();

    /** {@code rib} on its vertebra. */
    public Place costovertebral(final Rib rib) {
        final Discs d = Discs.INSTANCE;
        return d.inPart(d.rib(rib.vertebra()), Part.CHEST);
    }

    /** {@code rib}'s bone on its cartilage. */
    public Place costochondral(final Rib rib) {
        return Discs.INSTANCE.inPart(new Place(rib.end(), Axes.UPRIGHT), Part.CHEST);
    }

    /** One side's cartilages on the sternum. */
    public Place sternocostal() {
        return Discs.INSTANCE.inPart(new Place(Rib.FOURTH.cartilage(), Axes.UPRIGHT), Part.CHEST);
    }
}
