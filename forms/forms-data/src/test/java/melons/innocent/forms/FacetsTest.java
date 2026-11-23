package melons.innocent.forms;

import com.example.fxsuite.avatar.build.Material;
import com.example.fxsuite.avatar.build.Point;
import com.example.fxsuite.avatar.build.Role;
import com.example.fxsuite.avatar.build.Solid;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Solids as faces: a box six faces of two triangles each, every triangle wound to face out of it and every point facing
 * out with it, however its corners are given; a sheet to a material, each triangle knowing its solid; solids too small
 * left out.
 */
class FacetsTest {

    /** A box from ({@code x0}, {@code z0}, {@code y0}) so big each way, across, along and up, of {@code material}. */
    private Solid box(final double x0, final double z0, final double y0, final double size, final Material material) {
        final List<Point> corners = new ArrayList<>();
        for (int k = 0; k < 8; k++) {
            corners.add(new Point(x0 + (k & 1) * size, z0 + (k >> 1 & 1) * size, y0 + (k >> 2 & 1) * size));
        }
        return new Solid("box", Role.WALL, material, corners);
    }

    @Test
    void sixFacesWoundOutASheetToAMaterialSmallOnesLeftOut() {
        final Facets facets = new Facets(List.of(box(0, 0, 0, 100, Material.STONE), box(500, 0, 0, 100,
                Material.TIMBER), box(900, 0, 0, 2, Material.STONE)), 5);
        assertEquals(24, facets.triangles());
        final Facets.Sheet stone = facets.sheets().get(Material.STONE);
        assertEquals(12, stone.triangles());
        for (int t = 0; t < stone.triangles(); t++) {
            final double[][] p = new double[3][];
            for (int k = 0; k < 3; k++) {
                final int c = stone.corners()[3 * t + k];
                p[k] = new double[] {stone.points()[3 * c], stone.points()[3 * c + 1], stone.points()[3 * c + 2]};
            }
            final double[] a = {p[1][0] - p[0][0], p[1][1] - p[0][1], p[1][2] - p[0][2]};
            final double[] b = {p[2][0] - p[0][0], p[2][1] - p[0][1], p[2][2] - p[0][2]};
            final double[] n = {a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0]};
            final double[] out = {(p[0][0] + p[1][0] + p[2][0]) / 3 - 50, (p[0][1] + p[1][1] + p[2][1]) / 3 - 50,
                (p[0][2] + p[1][2] + p[2][2]) / 3 - 50};
            assertTrue(n[0] * out[0] + n[1] * out[1] + n[2] * out[2] > 0, "triangle " + t + " faces out");
            final int c = stone.corners()[3 * t];
            assertTrue(stone.facings()[3 * c] * out[0] + stone.facings()[3 * c + 1] * out[1]
                    + stone.facings()[3 * c + 2] * out[2] > 0, "its points facing out");
            assertEquals(0, stone.solids()[t], "from the first solid");
        }
        assertEquals(1, facets.sheets().get(Material.TIMBER).solids()[0]);
    }
}
