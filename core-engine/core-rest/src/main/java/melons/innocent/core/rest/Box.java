package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;
import melons.innocent.physics.existences.Shapes;

import java.util.ArrayList;
import java.util.List;

/**
 * A box, the first shape a thing in the world at rest may have, and the first the core engine reckons with of the
 * shapes physics knows: so far across, so far up and so far along, in centimetres, about its own middle.
 *
 * @param across how far across it is, side to side
 * @param up     how high it is
 * @param along  how far along it is
 */
public record Box(double across, double up, double along) implements ShapeImpl<Shapes.Box> {

    public Box {
        if (!(across > 0) || !(up > 0) || !(along > 0)) {
            throw new IllegalArgumentException("a box of some size each way: " + across + ", " + up + ", " + along);
        }
    }

    @Override
    public Shapes.Box kind() {
        return new Shapes.Box();
    }

    /** How far its faces are from its middle, each way. */
    public Spot half() {
        return new Spot(across / 2, up / 2, along / 2);
    }

    /** Its eight corners from its middle, as it was made. */
    public List<Spot> corners() {
        final List<Spot> corners = new ArrayList<>();
        for (int corner = 0; corner < 8; corner++) {
            corners.add(new Spot((corner & 1) == 0 ? -across / 2 : across / 2,
                    (corner & 2) == 0 ? -up / 2 : up / 2, (corner & 4) == 0 ? -along / 2 : along / 2));
        }
        return corners;
    }

    @Override
    public double room() {
        return across * up * along / 1_000_000;
    }
}
