package melons.innocent.plants.bench;

import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Sized;
import com.example.fxsuite.vulkan.space.Turned;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.plants.tree.Stage;
import melons.innocent.plants.walk.WoodDrawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * What the bench has standing: one tree, upright and unturned, as grown; or a patch of trees as a wood has them, so
 * many along each side, {@value #APART} centimetres apart, each a little off its place in the rows, turned any way
 * and a little bigger or smaller, the same every time; or an area of forest with an outline of its own, which
 * {@link Area} plants. Centimetres.
 */
public enum Subject {
    TREE("One tree"),
    PATCH("A patch of trees"),
    AREA("An area of forest");

    /** How far apart a patch's trees stand, in its rows; and how much room is left between two side by side. */
    private static final double APART = 625;
    private static final double ROOM = 700;
    /** How far across one tree is taken to be. */
    private static final double ONE = 1_700;
    private static final double ASTRAY = 0.3;
    private static final double SMALLEST = 0.85;
    private static final double BIGGEST = 1.1;
    private static final double FULL = 360;
    private static final long CHANCE = 20261124L;

    private final String title;

    Subject(final String title) {
        this.title = title;
    }

    /** What it is called in the bench's window. */
    public String title() {
        return title;
    }

    /** How far apart two of it stand side by side, middle to middle, {@code rows} along a patch's side. */
    public double apart(final int rows) {
        return (this == TREE ? ONE : rows * APART) + ROOM;
    }

    /**
     * It, of {@code kinds}, standing round {@code middle} on the ground, {@code rows} trees along a patch's side,
     * each tree held at {@code held} if any.
     */
    public List<WoodDrawn.Planted> planted(final Kinds kinds, final Vec3 middle, final Optional<Stage> held,
                                           final int rows) {
        if (this == TREE) {
            return List.of(new WoodDrawn.Planted(kinds.first(), new Mat4(middle), 1, held));
        }
        final Random chance = new Random(CHANCE);
        final double square = rows * APART;
        final List<WoodDrawn.Planted> planted = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            for (int along = 0; along < rows; along++) {
                final Vec3 at = middle.plus(new Vec3((along + 0.5) * APART - square / 2
                        + (chance.nextDouble() * 2 - 1) * ASTRAY * APART, 0, (row + 0.5) * APART - square / 2
                        + (chance.nextDouble() * 2 - 1) * ASTRAY * APART));
                final double times = SMALLEST + chance.nextDouble() * (BIGGEST - SMALLEST);
                final Mat4 placed = new Mat4(at).times(new Turned(new Vec3(0, 1, 0), chance.nextDouble() * FULL)
                        .matrix()).times(new Sized(times).matrix());
                planted.add(new WoodDrawn.Planted(kinds.any(chance), placed, times, held));
            }
        }
        return planted;
    }
}
