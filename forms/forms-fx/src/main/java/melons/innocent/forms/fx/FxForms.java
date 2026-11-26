package melons.innocent.forms.fx;

import javafx.scene.Group;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;
import melons.innocent.forms.Facets;

import java.util.Map;

/**
 * Solids' faces ({@link Facets}) as JavaFX 3D draws them: a mesh to each coat, each in the colour {@link Hues}
 * gives it, both sides drawn, every point facing as its face does so the light falls flat on each face. In their own
 * frame, y up; whoever shows them in JavaFX's, y down, turns them over.
 */
public record FxForms(Hues hues) {

    /** In the materials' own colours. */
    public FxForms() {
        this(new Hues());
    }

    /** {@code facets} as a group of meshes, one to each coat. */
    public Group of(final Facets facets) {
        final Group group = new Group();
        for (final Map.Entry<Facets.Coat, Facets.Sheet> sheet : facets.sheets().entrySet()) {
            final MeshView view = new MeshView(mesh(sheet.getValue()));
            final PhongMaterial look = new PhongMaterial(hues.of(sheet.getKey()));
            view.setMaterial(look);
            view.setCullFace(CullFace.NONE);
            group.getChildren().add(view);
        }
        return group;
    }

    /** {@code sheet} as a mesh: its points, their facings, one place on a texture, and its triangles. */
    private TriangleMesh mesh(final Facets.Sheet sheet) {
        final TriangleMesh mesh = new TriangleMesh(VertexFormat.POINT_NORMAL_TEXCOORD);
        final float[] points = new float[sheet.points().length];
        final float[] facings = new float[sheet.facings().length];
        for (int i = 0; i < points.length; i++) {
            points[i] = (float) sheet.points()[i];
            facings[i] = (float) sheet.facings()[i];
        }
        final int[] faces = new int[sheet.corners().length * 3];
        for (int c = 0; c < sheet.corners().length; c++) {
            faces[3 * c] = sheet.corners()[c];
            faces[3 * c + 1] = sheet.corners()[c];
            faces[3 * c + 2] = 0;
        }
        mesh.getPoints().setAll(points);
        mesh.getNormals().setAll(facings);
        mesh.getTexCoords().setAll(0, 0);
        mesh.getFaces().setAll(faces);
        return mesh;
    }
}
