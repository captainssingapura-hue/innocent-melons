package melons.innocent.core.sheet;

import melons.innocent.core.motion.Spot;

import java.util.List;

/**
 * A sheet that curves, in a frame of its own: across, up and along, in centimetres. It says how high it is over any
 * spot of its floor and how it rises from there, worked out from what it is and never cut into facets, so that two
 * spots however near each other differ by as little as they truly do: which is what a wheel, a foot or a keel going
 * over it needs. Whatever is made as a curved surface is one of these: a road's deck, a spiral ramp, a roof.
 */
public interface Sheet {

    /**
     * It over the spot of its floor so far {@code across} and {@code along}: once for each time it passes over the
     * spot, the lowest pass first; none where it is not over the spot at all.
     */
    List<Lift> over(double across, double along);

    /**
     * A spot of it by how far it is over the sheet each of its two ways, each from nothing to one: for drawing it,
     * and for whatever else goes over the whole of it. Every spot of it is given by one such pair.
     */
    Spot at(double far, double over);

    /** How far the furthest of it is from its frame's middle, in centimetres. */
    double reaches();
}
