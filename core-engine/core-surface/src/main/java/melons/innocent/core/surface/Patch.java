package melons.innocent.core.surface;

import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.taxonomy.Node;
import melons.innocent.core.motion.Spot;

/**
 * The answer to what a mover is on: a surface at a spot, as whatever provides it has it. Everything in it is in the
 * provider's own frame, which goes as the provider goes: where the surface is under the spot asked about, which way
 * it faces, and which way is down there. It says nothing of how the provider is itself moving: a mover's engine
 * works out only how the mover goes on the surface, and the world puts that together with how the provider goes.
 *
 * <p>Its form is a leaf of the tree of surfaces, its material a leaf of the materials' tree, and its condition a leaf
 * of the tree of conditions: three things told apart, any of which may change without the others.
 *
 * @param provider  what provides it, by name: for the world, which keeps what stands on what
 * @param at        where the surface is under the spot asked about, in the provider's frame, in centimetres
 * @param faces     which way the surface faces there, one long, in the provider's frame
 * @param down      which way is down there, one long, in the provider's frame
 * @param form      what form it has: level, a slope, stepped, land, a liquid or upright
 * @param material  what it is made of
 * @param condition what state it is in at that spot
 * @param within    how far it goes on from that spot before its nearest edge, in centimetres
 * @param below     how far under the spot asked about it is, the way down goes; less than nothing, a little over it
 */
public record Patch(String provider, Spot at, Spot faces, Spot down, Leaf<?> form, Node material, Node condition,
                    double within, double below) {

    /** How steep it is: how far from square to down it faces, in degrees; nothing, where it is level. */
    public double steep() {
        return Math.toDegrees(Math.acos(Math.max(-1, Math.min(1, -faces.dot(down)))));
    }

    /** The way it falls: the way down goes along the surface, one long; no way, where it is level. */
    public Spot falls() {
        final Spot along = down.minus(faces.times(down.dot(faces)));
        return along.length() < 1e-9 ? new Spot(0, 0, 0) : along.unit();
    }
}
