package com.example.fxsuite.life.body;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A body drawn: its pieces ({@link Body.Piece}) as meshes, one for each part's layer's colour, each knowing the part
 * it is of ({@code userData}), to be taken hold of by; a mark knows none, and is not picked. A smooth surface shaded
 * round, a flat-faced one face by face. Its skin drawn last, so that, seen through, what is under it shows.
 *
 * <p>In the scene's frame, as the engine's world is drawn: up is {@code -y}; a body facing south, toward who looks at it
 * from the bench's front, so its frame is turned half round ({@code x} and {@code z} the other way).
 */
public final class FigureModel {

    /** Whether a triangle's corners, as a surface goes round them, must be given the other way round to the screen. */
    static final boolean REVERSED = true;
    /** The skin of the part chosen: warmer. */
    static final String CHOSEN = "#e6c49c";
    /** How much of what is behind its skin shows through it, seen through. */
    static final double SEEN_THROUGH = 0.3;

    private FigureModel() {
    }

    /**
     * {@code pieces} drawn, standing at the scene's origin facing south; the skin seen through, if {@code through};
     * {@code chosen}'s skin warmer, if any part is.
     */
    public static Group of(final List<Body.Piece> pieces, final boolean through, final Part chosen) {
        record Key(Part part, Body.Layer layer, Paint paint) {
        }
        final Map<Key, TriangleMesh> meshes = new LinkedHashMap<>();
        for (final Body.Piece p : pieces) {
            final TriangleMesh mesh = meshes.computeIfAbsent(new Key(p.part(), p.layer(), p.surface().paint()), k -> {
                final TriangleMesh m = new TriangleMesh();
                m.getTexCoords().addAll(0, 0);
                return m;
            });
            add(mesh, p.surface());
        }
        final Group drawn = new Group();
        final Group skin = new Group();
        meshes.forEach((key, mesh) -> {
            final MeshView view = new MeshView(mesh);
            final boolean isSkin = key.layer() == Body.Layer.SKIN;
            view.setMaterial(matte(isSkin && key.part() == chosen && chosen != null ? CHOSEN : key.paint().colour(),
                    isSkin && through ? 1 - SEEN_THROUGH : 1));
            view.setCullFace(CullFace.BACK);
            view.setUserData(key.part());
            view.setMouseTransparent(key.part() == null);
            (isSkin ? skin : drawn).getChildren().add(view);
        });
        drawn.getChildren().add(skin);
        return drawn;
    }

    /** The part {@code picked} (a node of a drawn body) is of; none if it is of none. */
    public static Part partOf(final javafx.scene.Node picked) {
        return picked != null && picked.getUserData() instanceof Part p ? p : null;
    }

    /** A point of the scene where a drawn body stands, in the body's frame; and back. */
    public static V3 body(final javafx.geometry.Point3D scene) {
        return new V3(-scene.getX(), -scene.getY(), -scene.getZ());
    }

    public static javafx.geometry.Point3D scene(final V3 body) {
        return new javafx.geometry.Point3D(-body.x(), -body.y(), -body.z());
    }

    /** How many triangles {@code pieces} are drawn in. */
    public static int triangles(final List<Body.Piece> pieces) {
        return pieces.stream().mapToInt(p -> p.surface().triangles().size()).sum();
    }

    private static void add(final TriangleMesh mesh, final Surface s) {
        final int base = mesh.getPoints().size() / 3;
        for (final V3 v : s.points()) {
            final javafx.geometry.Point3D at = scene(v);
            mesh.getPoints().addAll((float) at.getX(), (float) at.getY(), (float) at.getZ());
        }
        final List<int[]> triangles = s.triangles();
        final List<Integer> faces = s.faces();
        for (int k = 0; k < triangles.size(); k++) {
            final int[] t = triangles.get(k);
            final int b = REVERSED ? t[2] : t[1];
            final int c = REVERSED ? t[1] : t[2];
            mesh.getFaces().addAll(base + t[0], 0, base + b, 0, base + c, 0);
            // smooth all round; or each face its own, a box's six
            mesh.getFaceSmoothingGroups().addAll(s.smooth() ? 1 : 1 << (1 + faces.get(k) % 30));
        }
    }

    static PhongMaterial matte(final String colour, final double opacity) {
        final PhongMaterial m = new PhongMaterial(Color.web(colour, opacity));
        m.setSpecularColor(Color.web("#3a3a3a"));
        return m;
    }
}
