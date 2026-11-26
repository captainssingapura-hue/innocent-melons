package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Sized;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.plants.tree.Kind;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A wood of trees with no leaves, of the kinds {@code kinds}, on level ground {@code across} by {@code deep}
 * centimetres, its middle at {@code middle}: {@code trees} of them, standing unevenly about as far apart as the
 * ground gives each, in rows each shifted half a place from the last, every one any of its kinds, the first or
 * second tree grown of that kind, turned any way and a little bigger or smaller. Where each stands and what it is
 * are the same every time, from {@code chance}.
 */
public record Woodland(List<Kind> kinds, Vec3 middle, double across, double deep, int trees, long chance) {

    /** How many different trees of each kind a wood has, grown from the numbers one and on. */
    private static final int EACH = 2;
    /** How far a tree may stand from its place in the rows, of the distance between rows. */
    private static final double ASTRAY = 0.36;
    private static final double SMALLEST = 0.8;
    private static final double BIGGEST = 1.15;
    private static final double FULL = 360;

    public Woodland {
        kinds = List.copyOf(kinds);
        if (kinds.isEmpty() || !(across > 0 && deep > 0 && trees > 0)) {
            throw new IllegalArgumentException("a wood has some kind of tree, some ground and some trees: " + kinds
                    + ", " + across + " by " + deep + ", " + trees);
        }
    }

    /** One tree standing in a wood: where its foot is, its kind, which of that kind, how far turned, how big. */
    public record Planted(Vec3 stands, Kind kind, int number, double turned, double times) {

        /** What takes the tree, in its own frame, to where it stands: made as big, turned, and set down. */
        public Mat4 placed() {
            return new Mat4(stands).times(new Turned(new Vec3(0, 1, 0), turned).matrix())
                    .times(new Sized(times).matrix());
        }
    }

    /** How many different trees of each kind any wood has, grown from the numbers one and on. */
    public int each() {
        return EACH;
    }

    /** Every tree standing in it, row by row. */
    public List<Planted> standing() {
        final Random dice = new Random(chance);
        // rows about as far apart each way as gives every tree its share of the ground
        final double apart = Math.sqrt(across * deep / trees);
        final int along = (int) Math.ceil(across / apart);
        final int rows = (int) Math.ceil((double) trees / along);
        final List<Planted> standing = new ArrayList<>();
        for (int tree = 0; tree < trees; tree++) {
            // every other row shifted half a place, so the trees stand in no straight lines from front to back
            final double shifted = tree / along % 2 == 0 ? 0.25 : 0.75;
            final double x = (tree % along + shifted) * across / along - across / 2
                    + (dice.nextDouble() * 2 - 1) * ASTRAY * apart;
            final double z = (tree / along + 0.5) * deep / rows - deep / 2
                    + (dice.nextDouble() * 2 - 1) * ASTRAY * apart;
            standing.add(new Planted(middle.plus(new Vec3(x, 0, z)), kinds.get(dice.nextInt(kinds.size())),
                    1 + dice.nextInt(EACH), dice.nextDouble() * FULL,
                    SMALLEST + dice.nextDouble() * (BIGGEST - SMALLEST)));
        }
        return standing;
    }
}
