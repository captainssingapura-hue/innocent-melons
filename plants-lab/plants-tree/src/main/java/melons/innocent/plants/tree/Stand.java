package melons.innocent.plants.tree;

import com.example.fxsuite.vulkan.space.Vec3;

import java.util.List;

/**
 * A patch of a wood as it is drawn far off: its trees taken together, drawn alike, and turned to their nearer
 * stages together, so that what is far is chosen patch by patch and not tree by tree. Square, exactly its patch,
 * from its trees' feet up to as high as they stand on the whole. Centimetres.
 *
 * @param corner where it begins: its least corner across and along, at its trees' feet's height
 * @param side   how far across it is, each way
 * @param tall   how high its trees stand, on the whole
 * @param trees  which of the wood's trees it stands for, by their numbers
 */
public record Stand(Vec3 corner, double side, double tall, List<Integer> trees) {

    public Stand {
        trees = List.copyOf(trees);
    }

    /** Where its middle is. */
    public Vec3 middle() {
        return corner.plus(new Vec3(side / 2, tall / 2, side / 2));
    }

    /** How far it reaches from its middle, at most, its trees' crowns spreading half a patch beyond it. */
    public double reach() {
        return Math.sqrt(2 * side * side + tall * tall / 4);
    }
}
