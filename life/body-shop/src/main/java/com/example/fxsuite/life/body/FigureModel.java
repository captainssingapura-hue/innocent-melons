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
 * it is of ({@code userData}), to be taken hold of by; a mark knows none, and is not picked, but those of the centre
 * of gravity and where it is kept, which are, to move where it is kept. A smooth surface shaded
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
    public static Group of(final List<Body.Piece> pieces, final boolean through, final Part chosen,
                           final Palette palette) {
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
            final boolean warm = isSkin && key.part() == chosen && chosen != null;
            view.setMaterial(material(warm ? CHOSEN : palette.colour(key.paint()), key.paint().finish(),
                    isSkin && through ? 1 - SEEN_THROUGH : 1));
            view.setCullFace(CullFace.BACK);
            final boolean centre = key.paint() == Paint.MARK_MASS || key.paint() == Paint.MARK_TARGET;
            view.setUserData(key.part() != null ? key.part() : centre ? CENTRE : null);
            view.setMouseTransparent(key.part() == null && !centre);
            (isSkin ? skin : drawn).getChildren().add(view);
        });
        drawn.getChildren().add(skin);
        return drawn;
    }

    /** What a drawn mark of its centre of gravity is known by, to be taken hold of by. */
    static final String CENTRE = "centre of gravity";

    /** Whether {@code picked} is a mark of the centre of gravity, or of where it is kept. */
    public static boolean isCentre(final javafx.scene.Node picked) {
        return picked != null && CENTRE.equals(picked.getUserData());
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
        add(mesh, s, FigureModel::scene);
    }

    /**
     * {@code surfaces} drawn as they are mapped by {@code map} (which, with whatever transform the drawing is given,
     * must turn the body's frame inside out once, as the scene's frame does): one mesh a colour, each its finish.
     */
    public static Group meshes(final List<Surface> surfaces, final Palette palette,
                               final java.util.function.Function<V3, javafx.geometry.Point3D> map) {
        final Map<Paint, TriangleMesh> byPaint = new java.util.EnumMap<>(Paint.class);
        for (final Surface s : surfaces) {
            add(byPaint.computeIfAbsent(s.paint(), p -> {
                final TriangleMesh m = new TriangleMesh();
                m.getTexCoords().addAll(0, 0);
                return m;
            }), s, map);
        }
        final Group drawn = new Group();
        byPaint.forEach((paint, mesh) -> {
            final MeshView view = new MeshView(mesh);
            view.setMaterial(material(palette.colour(paint), paint.finish(), 1));
            view.setCullFace(CullFace.BACK);
            drawn.getChildren().add(view);
        });
        return drawn;
    }

    private static void add(final TriangleMesh mesh, final Surface s,
                            final java.util.function.Function<V3, javafx.geometry.Point3D> map) {
        final int base = mesh.getPoints().size() / 3;
        for (final V3 v : s.points()) {
            final javafx.geometry.Point3D at = map.apply(v);
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

    /**
     * {@code colour}, so far opaque, taking the light as {@code finish} says: flat, with a soft sheen, as polished metal
     * (a bright, tight highlight; JavaFX reflects nothing round it), or lit from within.
     */
    static PhongMaterial material(final String colour, final Paint.Finish finish, final double opacity) {
        final Color c = Color.web(colour, opacity);
        final PhongMaterial m = new PhongMaterial(c);
        switch (finish) {
            case MATTE -> m.setSpecularColor(Color.web("#3a3a3a"));
            case GLOSS -> {
                m.setSpecularColor(Color.web("#8c8c8c"));
                m.setSpecularPower(28);
            }
            case METAL -> {
                m.setSpecularColor(Color.web("#f4f6f8"));
                m.setSpecularPower(72);
            }
            case GLOW -> {
                final javafx.scene.image.WritableImage lit = new javafx.scene.image.WritableImage(1, 1);
                lit.getPixelWriter().setColor(0, 0, c);
                m.setSelfIlluminationMap(lit);
            }
        }
        return m;
    }
}
