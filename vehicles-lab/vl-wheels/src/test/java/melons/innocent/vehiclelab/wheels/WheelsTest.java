package melons.innocent.vehiclelab.wheels;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Shapes;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Wheels at any size: a size any width, wall and rim more than nought, said as a sidewall says it; each design at a
 * small old saloon's size, a modern one's and a large one's, inside its tyre, nothing of it behind the hub's face but
 * its barrel, so a brake behind it has room; the classic rim's eight windows open through its face.
 */
class WheelsTest {

    private final Shapes shapes = new Shapes(32);
    private final List<WheelSize> sizes = List.of(new WheelSize(165, 80, 13), new WheelSize(225, 50, 17),
            new WheelSize(335, 25, 24), new WheelSize(120, 90, 10));
    private final double mount = 1;

    @Test
    void anySizeMoreThanNoughtSaidAsASidewallSaysIt() {
        final WheelSize santana = new WheelSize(225, 50, 17);
        assertEquals("225/50 R17", santana.said());
        assertEquals(17 * 2.54 / 2 + 11.25, santana.radius(), 1e-9);
        assertEquals("165/80 R13", new WheelSize(165, 80, 13).said());
        assertThrows(IllegalArgumentException.class, () -> new WheelSize(0, 50, 17));
        assertThrows(IllegalArgumentException.class, () -> new WheelSize(225, -5, 17));
    }

    @Test
    void eachDesignInsideItsTyreAndClearOfTheBrake() {
        for (final RimDesign design : RimDesign.values()) {
            final Rim rim = design.rim(shapes);
            for (final WheelSize size : sizes) {
                final Mesh mesh = rim.mesh(size, mount);
                for (final Vec3 p : mesh.points()) {
                    final double round = Math.hypot(p.y(), p.z());
                    assertTrue(round < size.radius(), design + " at " + size.said() + " outside its tyre: " + p);
                    assertTrue(Math.abs(p.x()) <= size.wide() / 2 + 1e-9, design + " wider than its tyre: " + p);
                    assertTrue(p.x() >= mount - 1e-9 || round >= rim.inside(size) - 1e-9,
                            design + " at " + size.said() + " in the brake's room: " + p);
                }
                assertTrue(new Tyre(size, shapes).mesh().points().stream()
                        .allMatch(p -> Math.hypot(p.y(), p.z()) <= size.radius() + 1e-9), "the tyre its size");
            }
        }
    }

    @Test
    void theClassicRimsEightWindowsOpen() {
        final WheelSize size = new WheelSize(225, 50, 17);
        final ClassicRim rim = new ClassicRim(shapes);
        final Mesh mesh = rim.mesh(size, mount);
        final double inside = rim.inside(size);
        final double reach = inside * 0.66;
        for (int k = 0; k < 8; k++) {
            final double a = 2 * Math.PI * k / 8;
            assertTrue(!covered(mesh, reach * Math.cos(a), reach * Math.sin(a), inside), "window " + k + " open");
            final double b = a + Math.PI / 8;
            assertTrue(covered(mesh, reach * Math.cos(b), reach * Math.sin(b), inside), "a spoke after window " + k);
        }
    }

    /**
     * Whether the rim's face, inside its barrel and out from its hub, covers the spot {@code y} up and {@code z} along,
     * seen square on.
     */
    private boolean covered(final Mesh mesh, final double y, final double z, final double inside) {
        for (int t = 0; t < mesh.triangles(); t++) {
            final Vec3 a = mesh.corner(t, 0);
            final Vec3 b = mesh.corner(t, 1);
            final Vec3 c = mesh.corner(t, 2);
            final boolean face = List.of(a, b, c).stream().allMatch(p -> p.x() > mount + 0.5
                    && Math.hypot(p.y(), p.z()) < inside);
            if (face && within(a, b, c, y, z)) {
                return true;
            }
        }
        return false;
    }

    /** Whether {@code y}, {@code z} lies within the triangle {@code a}, {@code b}, {@code c} seen square on. */
    private boolean within(final Vec3 a, final Vec3 b, final Vec3 c, final double y, final double z) {
        final double d1 = (y - b.y()) * (a.z() - b.z()) - (a.y() - b.y()) * (z - b.z());
        final double d2 = (y - c.y()) * (b.z() - c.z()) - (b.y() - c.y()) * (z - c.z());
        final double d3 = (y - a.y()) * (c.z() - a.z()) - (c.y() - a.y()) * (z - a.z());
        return !((d1 < 0 || d2 < 0 || d3 < 0) && (d1 > 0 || d2 > 0 || d3 > 0));
    }
}
