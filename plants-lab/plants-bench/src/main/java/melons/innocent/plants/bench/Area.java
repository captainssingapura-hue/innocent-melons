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
 * An area of forest with an outline of its own: trees {@value #APART} centimetres apart in rows, each a little off
 * its place, turned any way and a little bigger or smaller, wherever the outline holds; the same every time.
 *
 * @param outline its outline
 * @param across  how far across it is at its widest, in centimetres
 */
public record Area(Outline outline, double across) {

    private static final double APART = 700;
    private static final double ASTRAY = 0.3;
    private static final double SMALLEST = 0.85;
    private static final double BIGGEST = 1.1;
    private static final double FULL = 360;
    private static final long CHANCE = 20261124L;

    /** Its trees, of {@code kinds}, round {@code middle} on the ground, each held at {@code held} if any. */
    public List<WoodDrawn.Planted> planted(final Kinds kinds, final Vec3 middle, final Optional<Stage> held) {
        final Random chance = new Random(CHANCE);
        final int rows = Math.max(1, (int) Math.round(across / APART));
        final List<WoodDrawn.Planted> planted = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            for (int along = 0; along < rows; along++) {
                // every number drawn whether the tree stands or not, so a tree is the same under any outline
                final double x = ((along + 0.5 + (chance.nextDouble() * 2 - 1) * ASTRAY) / rows) * 2 - 1;
                final double z = ((row + 0.5 + (chance.nextDouble() * 2 - 1) * ASTRAY) / rows) * 2 - 1;
                final double times = SMALLEST + chance.nextDouble() * (BIGGEST - SMALLEST);
                final double turned = chance.nextDouble() * FULL;
                final int grown = kinds.any(chance);
                if (outline.holds(x, z)) {
                    final Vec3 at = middle.plus(new Vec3(x * across / 2, 0, z * across / 2));
                    planted.add(new WoodDrawn.Planted(grown, new Mat4(at).times(new Turned(new Vec3(0, 1, 0), turned)
                            .matrix()).times(new Sized(times).matrix()), times, held));
                }
            }
        }
        return planted;
    }
}
