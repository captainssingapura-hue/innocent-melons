package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.plants.tree.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * A forest without end, made of a set of samples: the ground is squared off, as far as it goes every way, each
 * square as big as a sample, and each square is one of the samples, turned a quarter turn or two or three or not at
 * all. Which sample and how turned are worked out from where the square is and from nothing else, so the forest is
 * the same whoever comes to a place and from whichever side, and nothing of it need be kept.
 *
 * @param samples the set it is made of
 * @param chance  the number its squares are chosen by
 */
public record Endless(Samples samples, long chance) {

    private static final int QUARTERS = 4;
    private static final double QUARTER = 90;

    /**
     * A square as it is seen: which it is, across and along; the sample it is and how many quarter turns it is
     * turned; how far the nearest of it is from the eye, in centimetres; and the level that calls for.
     */
    public record Seen(long across, long along, int sample, int quarters, double away, Stage level) {
    }

    /** Which sample the square {@code across}, {@code along} is. */
    public int sample(final long across, final long along) {
        return (int) Long.remainderUnsigned(mixed(across, along), samples.count());
    }

    /** How many quarter turns the square {@code across}, {@code along} is turned. */
    public int quarters(final long across, final long along) {
        return (int) Long.remainderUnsigned(mixed(across, along) >>> 40, QUARTERS);
    }

    /** The middle of the square {@code across}, {@code along}, on the ground. */
    public Vec3 middle(final long across, final long along) {
        return new Vec3((across + 0.5) * samples.side(), 0, (along + 0.5) * samples.side());
    }

    /** What takes the square's sample from its own frame to where the square is: turned, then moved. */
    public Mat4 placed(final long across, final long along) {
        return new Mat4(middle(across, along)).times(turned(quarters(across, along)));
    }

    /** A sample turned so many quarter turns about the upright through its middle. */
    public Mat4 turned(final int quarters) {
        return new Turned(new Vec3(0, 1, 0), quarters * QUARTER).matrix();
    }

    /** The trees of the square {@code across}, {@code along}, where they stand in the world. */
    public List<WoodDrawn.Planted> trees(final long across, final long along) {
        final Mat4 placed = placed(across, along);
        return samples.planted(sample(across, along)).stream().map(tree -> new WoodDrawn.Planted(tree.grown(),
                placed.times(tree.placed()), tree.times())).toList();
    }

    /**
     * Every square some of which is within {@code reaches}' furthest of {@code eye}, each with the level its
     * distance calls for: the distance from the eye to the nearest of the square on the ground, so the square stood
     * in is at nothing and from high above every square is far.
     */
    public List<Seen> seen(final Vec3 eye, final Reaches reaches) {
        final double side = samples.side();
        final double far = reaches.far() * 100;
        final List<Seen> seen = new ArrayList<>();
        for (long along = (long) Math.floor((eye.z() - far) / side); along * side <= eye.z() + far; along++) {
            for (long across = (long) Math.floor((eye.x() - far) / side); across * side <= eye.x() + far; across++) {
                final double x = Math.max(0, Math.max(across * side - eye.x(), eye.x() - (across + 1) * side));
                final double z = Math.max(0, Math.max(along * side - eye.z(), eye.z() - (along + 1) * side));
                final double away = Math.sqrt(x * x + z * z + eye.y() * eye.y());
                final long a = across;
                final long b = along;
                reaches.level(away).ifPresent(level -> seen.add(new Seen(a, b, sample(a, b), quarters(a, b), away,
                        level)));
            }
        }
        return seen;
    }

    /** The square's two numbers and the forest's own mixed into one, every bit of it depending on all of them. */
    private long mixed(final long across, final long along) {
        long mixed = chance + across * 0x9E3779B97F4A7C15L + along * 0xC2B2AE3D27D4EB4FL;
        mixed = (mixed ^ (mixed >>> 30)) * 0xBF58476D1CE4E5B9L;
        mixed = (mixed ^ (mixed >>> 27)) * 0x94D049BB133111EBL;
        return mixed ^ (mixed >>> 31);
    }
}
