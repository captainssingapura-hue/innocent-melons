package com.example.fxsuite.avatar.bench.kit;

import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.DirectionalLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * A bench's 3D view: what it shows, lit by a light over all and a sun, the sky behind; seen by a camera that turns
 * round a point. Dragged, it turns round the point; right-dragged (or Shift-dragged), it moves across; scrolled, nearer
 * or further. In the game's units, centimetres, y down as JavaFX has it.
 *
 * <p>Where it is first seen from is as the bench's properties say ({@link #from}): {@code <prefix>.yaw},
 * {@code <prefix>.pitch} (degrees, looking down when positive) and {@code <prefix>.distance} (metres).
 */
public final class Orbit {

    /** What share of the way to what it turns round it draws from, at least; and from how near, at most, cm. */
    static final double NEAR_SHARE = 200;
    static final double NEAREST_MOST = 500;

    private final PerspectiveCamera eye = new PerspectiveCamera(true);
    private final Rotate yaw = new Rotate(-35, Rotate.Y_AXIS);
    private final Rotate pitch = new Rotate(-22, Rotate.X_AXIS);
    private final Translate back = new Translate(0, 0, -9_000);
    private final Translate target = new Translate(0, 0, 0);
    private final Group world = new Group();
    private final Pane holder;
    private double lastX;
    private double lastY;
    /** How near and how far off it may be scrolled, cm. */
    private double nearest = 300;
    private double furthest = 200_000;

    /** A view of a sky so coloured, lit so, the camera seeing from {@code near} to {@code far} (cm). */
    public Orbit(final Color sky, final Color ambient, final Color sun, final double near, final double far) {
        this(sky, ambient, sun, new javafx.geometry.Point3D(0.45, 1, 0.35), near, far);
    }

    /** The same, the sun shining {@code towards} that way (y down). */
    public Orbit(final Color sky, final Color ambient, final Color sun, final javafx.geometry.Point3D towards,
                 final double near, final double far) {
        this(sky, near, far);
        final DirectionalLight light = new DirectionalLight(sun);
        light.setDirection(towards);
        world.getChildren().addAll(new AmbientLight(ambient), light);
    }

    /** A view of a sky so coloured, lit by nothing of its own: what it shows brings its lights (the game's world). */
    public Orbit(final Color sky, final double near, final double far) {
        eye.setNearClip(near);
        eye.setFarClip(far);
        eye.setFieldOfView(40);
        eye.getTransforms().addAll(target, yaw, pitch, back);
        // drawn from no nearer than a share of the way to what it turns round: the depth it tells near from far by is
        // finest near the camera, so from far off, surfaces a few centimetres apart (a slab and a rail on its edge)
        // are told apart rather than flickering through each other
        back.zProperty().addListener((o, was, now) -> eye.setNearClip(Math.clamp(-now.doubleValue() / NEAR_SHARE,
                near, NEAREST_MOST)));
        world.getChildren().add(eye);
        final SubScene view = new SubScene(world, 900, 520, true, SceneAntialiasing.BALANCED);
        view.setFill(sky);
        view.setCamera(eye);
        holder = new Pane(view);
        view.widthProperty().bind(holder.widthProperty());
        view.heightProperty().bind(holder.heightProperty());
        handle(view);
    }

    /** A view as the roads bench has had it: a pale sky, a soft light, from 10 cm to 20 km. */
    public static Orbit standard() {
        return new Orbit(Color.web("#c9dbe8"), Color.web("#8e959c"), Color.web("#9a978f"), 10, 2_000_000);
    }

    /** What to put in the window: the view, filling what holds it. */
    public Pane node() {
        return holder;
    }

    /** {@code shown} added to what is shown, under the lights. */
    public void add(final Node... shown) {
        world.getChildren().addAll(shown);
    }

    /** Seen from as {@code prefix}'s properties say, or else as given: degrees, degrees down, metres. */
    public void from(final String prefix, final double yawDegrees, final double pitchDegrees, final double metres) {
        yaw.setAngle(Double.parseDouble(System.getProperty(prefix + ".yaw", String.valueOf(yawDegrees))));
        pitch.setAngle(-Double.parseDouble(System.getProperty(prefix + ".pitch", String.valueOf(pitchDegrees))));
        back.setZ(-100 * Double.parseDouble(System.getProperty(prefix + ".distance", String.valueOf(metres))));
    }

    /** Turned round ({@code x}, {@code y}, {@code z}), in JavaFX's frame (y down). */
    public void lookAt(final double x, final double y, final double z) {
        target.setX(x);
        target.setY(y);
        target.setZ(z);
    }

    /** Seen from {@code yawDegrees} round, {@code pitchDegrees} down. */
    public void turned(final double yawDegrees, final double pitchDegrees) {
        yaw.setAngle(yawDegrees);
        pitch.setAngle(-pitchDegrees);
    }

    /** How far round it is seen from, degrees. */
    public double yawDegrees() {
        return yaw.getAngle();
    }

    /** How far down it is seen from, degrees. */
    public double pitchDegrees() {
        return -pitch.getAngle();
    }

    /** Seen from {@code cm} off. */
    public void distance(final double cm) {
        back.setZ(-cm);
    }

    /** Scrolled no nearer than {@code nearestCm}, no further than {@code furthestCm}. */
    public void zoom(final double nearestCm, final double furthestCm) {
        nearest = nearestCm;
        furthest = furthestCm;
    }

    /** What takes hold of what is shown, if anything does; whether it holds something now. */
    private Grip grip;
    private boolean holding;

    /**
     * {@code grip} given what is pressed on with the left button, to take hold of and move, instead of the view
     * turning ({@link Grip}).
     */
    public void grip(final Grip grip) {
        this.grip = grip;
    }

    /**
     * The line from the eye through ({@code x}, {@code y}) of the view as it is {@code width} by {@code height}, in
     * the view's frame: the camera's field of view upright, its eye at its own origin looking along its z.
     */
    Grip.Ray ray(final double x, final double y, final double width, final double height) {
        final double t = Math.tan(Math.toRadians(eye.getFieldOfView()) / 2);
        final Point3D way = new Point3D((x - width / 2) / (height / 2) * t, (y - height / 2) / (height / 2) * t, 1);
        final javafx.scene.transform.Transform toView = eye.getLocalToParentTransform();
        return new Grip.Ray(toView.transform(Point3D.ZERO), toView.deltaTransform(way).normalize());
    }

    private void handle(final SubScene view) {
        view.setOnMousePressed(e -> {
            lastX = e.getSceneX();
            lastY = e.getSceneY();
            holding = grip != null && e.getButton() == MouseButton.PRIMARY && !e.isShiftDown()
                    && e.getPickResult().getIntersectedNode() != null
                    && grip.take(e.getPickResult(), ray(e.getX(), e.getY(), view.getWidth(), view.getHeight()),
                            e.isControlDown());
        });
        view.setOnMouseReleased(e -> {
            if (holding) {
                holding = false;
                grip.letGo();
            }
        });
        view.setOnMouseDragged(e -> {
            if (holding) {
                grip.move(ray(e.getX(), e.getY(), view.getWidth(), view.getHeight()), e.isControlDown());
                return;
            }
            final double dx = e.getSceneX() - lastX;
            final double dy = e.getSceneY() - lastY;
            lastX = e.getSceneX();
            lastY = e.getSceneY();
            if (e.getButton() == MouseButton.SECONDARY || e.isShiftDown()) {
                final double scale = -back.getZ() / 900;
                final double turn = Math.toRadians(yaw.getAngle());
                target.setX(target.getX() - (dx * Math.cos(turn)) * scale);
                target.setZ(target.getZ() + (dx * Math.sin(turn)) * scale);
                target.setY(target.getY() - dy * scale);
            } else {
                yaw.setAngle(yaw.getAngle() + dx * 0.4);
                pitch.setAngle(Math.clamp(pitch.getAngle() - dy * 0.4, -89, 89));
            }
        });
        view.setOnScroll(e -> back.setZ(Math.clamp(back.getZ() * Math.pow(1.0015, -e.getDeltaY()), -furthest,
                -nearest)));
    }
}
