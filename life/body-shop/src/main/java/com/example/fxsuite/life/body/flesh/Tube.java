package com.example.fxsuite.life.body.flesh;

import com.example.fxsuite.life.body.Part;
import hue.captains.singapura.tao.ontology.Scalar;

import java.util.List;

/**
 * A limb's or the trunk's flesh on one of the rig's parts, as on the right: a tube through its rings, top to bottom,
 * smooth round each and from each to the next, rounded shut at either end. Laid over its part's bones, and over those
 * of the parts it {@code covers} as they stand at rest (the chest's flesh over the shoulder blades and the collarbones,
 * the hips' over the tops of the thigh bones), both sides of those; and those of the parts it meets, so that a ring two
 * parts share is found the same in both.
 *
 * @param said   what it is, in words
 * @param part   the part it moves with
 * @param rings  its rings, top to bottom
 * @param covers the parts on the right whose bones it is laid over too, and their fellows on the left
 */
public record Tube(String said, Part part, List<Ring> rings, List<Part> covers) implements Scalar {

    public Tube {
        rings = List.copyOf(rings);
        covers = List.copyOf(covers);
    }
}
