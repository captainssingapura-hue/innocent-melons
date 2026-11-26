package melons.innocent.building.walk;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.forms.Facets;

import java.util.ArrayList;
import java.util.List;

/**
 * A coat's sheet of triangles as the card's mesh: its points, which way each faces and its triangles' corners, as
 * they are.
 *
 * @param sheet the sheet
 */
public record Meshed(Facets.Sheet sheet) {

    /** The sheet as a mesh. */
    public Mesh mesh() {
        final List<Vec3> points = new ArrayList<>();
        final List<Vec3> facings = new ArrayList<>();
        for (int i = 0; i < sheet.points().length; i += 3) {
            points.add(new Vec3(sheet.points()[i], sheet.points()[i + 1], sheet.points()[i + 2]));
            facings.add(new Vec3(sheet.facings()[i], sheet.facings()[i + 1], sheet.facings()[i + 2]));
        }
        final List<Integer> corners = new ArrayList<>();
        for (final int corner : sheet.corners()) {
            corners.add(corner);
        }
        return new Mesh(points, facings, corners);
    }
}
