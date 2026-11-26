package melons.innocent.glm.testdrive;

import com.example.fxsuite.avatar.sim.Place;
import com.example.fxsuite.vulkan.bench.play.Step;
import com.example.fxsuite.vulkan.flight.Chasing;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;

/**
 * The views the car is driven in, as the avatar engine's own driving has them, as racing games do: C goes to the next,
 * in turn, chase far, chase near, the hood, the cockpit, the bumper; V to the driver's seat, and back to the view it was
 * in; R held looks back, from the hood and the bumper over the car's back. Behind it, the chase camera swings round
 * with Q and E and looks up and down with the mouse; from on it, the view is the car's alone, ahead as it heads and
 * tipped as it is. Told what each step pressed and held, so a run kept and played again is seen the same.
 */
final class DriveViews {

    /** The views, in turn. */
    enum View {
        CHASE_FAR("CHASE FAR"), CHASE_NEAR("CHASE NEAR"), HOOD("HOOD"), COCKPIT("COCKPIT"), BUMPER("BUMPER");

        private final String title;

        View(final String title) {
            this.title = title;
        }

        /** What it is called, as written over the picture. */
        String title() {
            return title;
        }

        View next() {
            return values()[(ordinal() + 1) % values().length];
        }

        /** Whether it is from a point on the car, not from behind it. */
        boolean onIt() {
            return this == HOOD || this == COCKPIT || this == BUMPER;
        }
    }

    /** The near chase: so much of the far one's distance, looking down so far. */
    private static final double NEAR = 0.75;
    private static final double NEAR_DOWN = 10;
    /** From on it: looking this far down, as wide a lens, from this near. */
    private static final double DOWN = 3;
    private static final double WIDE = 65;
    private static final double CLOSE = 5;
    /**
     * Where each view on it is, in the car's frame from its front axle's middle on the ground: the hood's, over the
     * engine's trim, short of the windscreen; the driver's eyes, on the left, behind the front axle; the bumper's, low
     * at its nose; and, looking back, the hood's over its tail.
     */
    private static final Vec3 HOOD = new Vec3(0, 125, -45);
    private static final Vec3 EYES = new Vec3(-40, 130, -115);
    private static final Vec3 BUMPER = new Vec3(0, 45, 105);
    private static final Vec3 TAIL = new Vec3(0, 125, -395);

    private final GlmChassis chassis;
    private View view;
    private View beforeCockpit;
    private boolean back;

    /** {@code chassis}'s views, beginning in {@code first}. */
    DriveViews(final GlmChassis chassis, final View first) {
        this.chassis = chassis;
        this.view = first;
        this.beforeCockpit = first == View.COCKPIT ? View.CHASE_FAR : first;
    }

    /** Told what a step pressed and held: C the next view, V the driver's seat and back, R held looking back. */
    void told(final Step step) {
        if (step.pressed().contains("C")) {
            view = view.next();
        }
        if (step.pressed().contains("V")) {
            if (view == View.COCKPIT) {
                view = beforeCockpit;
            } else {
                beforeCockpit = view;
                view = View.COCKPIT;
            }
        }
        back = step.held().contains("R");
    }

    /** The view it is in. */
    View view() {
        return view;
    }

    /**
     * The camera, the car at {@code at}, tipped {@code tilt} and rolled {@code roll}, the chase camera as
     * {@code chasing} leaves it looking at {@code head}.
     */
    Camera camera(final Chasing chasing, final Vec3 head, final Place at, final double tilt, final double roll) {
        return switch (view) {
            case CHASE_FAR -> behind(chasing).camera(head, at.yaw(), (x, z) -> 0);
            case CHASE_NEAR -> behind(new Chasing(chasing.round(), NEAR_DOWN, chasing.distance() * NEAR))
                    .camera(head, at.yaw(), (x, z) -> 0);
            case HOOD -> onIt(back ? TAIL : HOOD, at, tilt, roll);
            case COCKPIT -> onIt(EYES, at, tilt, roll);
            case BUMPER -> onIt(back ? new Vec3(0, BUMPER.y(), TAIL.z() - 20) : BUMPER, at, tilt, roll);
        };
    }

    /** The chase camera as it is, or swung right round, looking back. */
    private Chasing behind(final Chasing chasing) {
        return back ? new Chasing(chasing.round() + 180, chasing.down(), chasing.distance()) : chasing;
    }

    /** From {@code eye} on the car, looking ahead as it heads, a little down; or back. */
    private Camera onIt(final Vec3 eye, final Place at, final double tilt, final double roll) {
        final Mat4 car = chassis.carried(at, tilt, roll);
        final double down = Math.toRadians(DOWN);
        final Vec3 ahead = way(car, new Vec3(0, -Math.sin(down), (back ? -1 : 1) * Math.cos(down))).unit();
        final Vec3 right = way(car, new Vec3(0, 1, 0)).cross(ahead).unit();
        return new Camera(point(car, eye), right, ahead.cross(right), ahead, WIDE, CLOSE);
    }

    private Vec3 point(final Mat4 m, final Vec3 p) {
        return way(m, p).plus(new Vec3(m.at(0, 3), m.at(1, 3), m.at(2, 3)));
    }

    private Vec3 way(final Mat4 m, final Vec3 v) {
        return new Vec3(m.at(0, 0) * v.x() + m.at(0, 1) * v.y() + m.at(0, 2) * v.z(),
                m.at(1, 0) * v.x() + m.at(1, 1) * v.y() + m.at(1, 2) * v.z(),
                m.at(2, 0) * v.x() + m.at(2, 1) * v.y() + m.at(2, 2) * v.z());
    }
}
