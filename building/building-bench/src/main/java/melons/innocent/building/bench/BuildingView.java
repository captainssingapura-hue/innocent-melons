package melons.innocent.building.bench;

import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.DirectionalLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import melons.innocent.forms.Facets;
import melons.innocent.forms.fx.FxForms;

/**
 * A building drawn by JavaFX 3D, as one object: its faces a mesh to each material ({@link FxForms}), turned over from
 * its own frame, y up, into JavaFX's, y down, its middle where the eye looks; lit by the sky all round and a sun from
 * above to one side. Dragged, the eye goes round it and up and down over it; the wheel brings it nearer or takes it
 * further off. Made and used on JavaFX's thread.
 */
public final class BuildingView {

    private static final double FIELD = 40;
    private static final double TURNED = 0.4;
    private static final double SCROLLED = 1.0015;

    private final Group building = new Group();
    /** How far round it is first seen from: {@code -Dbench.turn=} degrees, 30 if not said. */
    private final Rotate round = new Rotate(Double.parseDouble(System.getProperty("bench.turn", "30")), Rotate.Y_AXIS);
    private final Rotate down = new Rotate(-20, Rotate.X_AXIS);
    private final Translate back = new Translate(0, 0, -3_000);
    private final SubScene scene;
    private double lastX;
    private double lastY;

    /** A view {@code width} by {@code height} pixels. */
    public BuildingView(final double width, final double height) {
        final AmbientLight sky = new AmbientLight(Color.gray(0.42));
        final DirectionalLight sun = new DirectionalLight(Color.gray(0.75));
        sun.setDirection(new Point3D(0.45, 1, 0.35));
        final Group world = new Group(building, sky, sun);
        final PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setFieldOfView(FIELD);
        camera.setNearClip(10);
        camera.setFarClip(1_000_000);
        camera.getTransforms().addAll(round, down, back);
        scene = new SubScene(world, width, height, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.web("#cdd9e3"));
        scene.setCamera(camera);
        scene.setOnMousePressed(event -> {
            lastX = event.getSceneX();
            lastY = event.getSceneY();
        });
        scene.setOnMouseDragged(event -> {
            round.setAngle(round.getAngle() + (event.getSceneX() - lastX) * TURNED);
            down.setAngle(Math.clamp(down.getAngle() - (event.getSceneY() - lastY) * TURNED, -89, 89));
            lastX = event.getSceneX();
            lastY = event.getSceneY();
        });
        scene.setOnScroll(event -> back.setZ(back.getZ() * Math.pow(SCROLLED, event.getDeltaY())));
    }

    /** The view, to be laid out. */
    public SubScene node() {
        return scene;
    }

    /** {@code facets} drawn, their middle where the eye looks, the eye far enough off to see them whole. */
    public void show(final Facets facets) {
        final double[] low = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
        final double[] high = {-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};
        facets.sheets().values().forEach(sheet -> {
            for (int i = 0; i < sheet.points().length; i++) {
                low[i % 3] = Math.min(low[i % 3], sheet.points()[i]);
                high[i % 3] = Math.max(high[i % 3], sheet.points()[i]);
            }
        });
        final Group form = new FxForms().of(facets);
        if (low[0] <= high[0]) {
            form.getTransforms().setAll(new Rotate(180, Rotate.X_AXIS), new Translate(-(low[0] + high[0]) / 2,
                    -(low[1] + high[1]) / 2, -(low[2] + high[2]) / 2));
            final double size = Math.max(high[0] - low[0], Math.max(high[1] - low[1], high[2] - low[2]));
            back.setZ(-Math.max(500, size * 2.2));
        }
        building.getChildren().setAll(form);
    }
}
