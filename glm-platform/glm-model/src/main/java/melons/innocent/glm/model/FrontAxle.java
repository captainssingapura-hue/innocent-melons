package melons.innocent.glm.model;

import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Platform;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.ArrayList;
import java.util.List;

/**
 * The GLM platform's front axle, a five-link axle after the current large SUV's, joined part to part from the
 * underbody's front out as its workshop manual has it: the front subframe bolted rigid to the body; on each side,
 * the control arm bushed to its ear on the node and to its ball joint, the ball joint in the wheel bearing housing;
 * the shock absorber's fork bolted round the control arm's boss, the damper clamped in the fork, the air spring on the
 * damper up to the strut tower; the guide link bushed in the subframe's front mount and on its ball pin in the housing;
 * the two upper links bushed to the strut tower and on their ball pins in the housing; the steering gear bolted to the
 * nodes and the tie rods from its rack to the housing's arms; the hub turning in the housing, the disc and caliper,
 * the wheel and the tyre. Said as on the right, the left its mirror.
 *
 * @param proportions the car's figures
 */
public record FrontAxle(Proportions proportions) {

    private static final int ROUND = 24;

    /** A large SUV's. */
    public FrontAxle() {
        this(new Proportions());
    }

    /** It, as a platform to be put together, the underbody's front its root. */
    public Platform platform() {
        final Part body = body();
        return new Platform("the GLM platform's front axle", body, joined(body));
    }

    /** The underbody's front. */
    Part body() {
        return new FrontBody(new FrontPoints(proportions)).part();
    }

    /** How its parts are joined to {@code body}, in the order they are walked. */
    List<Connection> joined(final Part body) {
        final FrontPoints points = new FrontPoints(proportions);
        final Shapes shapes = new Shapes(ROUND);
        final FrontSubframe subframe = new FrontSubframe(points);
        final FrontLinks links = new FrontLinks(points, shapes);
        final FrontStrut strut = new FrontStrut(points, shapes);
        final Corner corner = new Corner(points, shapes);
        final Wheel wheel = new Wheel(proportions, points, shapes);
        final Part arm = links.controlArm();
        final Part ball = links.controlArmBallJoint();
        final Part carrier = corner.carrier();
        final Part fork = strut.fork();
        final Part damper = strut.damper();
        final Part air = strut.airSpring();
        final Part gear = new SteeringGear(points, shapes).part();
        final Part hub = corner.hub();
        final Part rim = wheel.wheel();
        final List<Connection> joined = new ArrayList<>(subframe.joined(body));
        joined.add(new Connection("front control arm, inner", subframe.node(), "control arm", arm, "inner",
                Joining.BUSHED));
        joined.add(new Connection("front control arm, outer", arm, "outer", ball, "ball", Joining.BUSHED));
        joined.add(new Connection("front control arm, ball joint", ball, "ball", carrier, "control arm",
                Joining.BALL_JOINT));
        joined.add(new Connection("shock absorber fork", arm, "fork", fork, "arm", Joining.BUSHED));
        joined.add(new Connection("front damper, foot", fork, "strut", damper, "foot", Joining.BOLTED));
        joined.add(new Connection("front air spring, seat", damper, "spring seat", air, "seat", Joining.SEATED));
        joined.add(new Connection("front air spring, top", body, "strut top, right", air, "top", Joining.BOLTED));
        final Part guide = links.guideLink();
        joined.add(new Connection("front guide link, inner", subframe.hook(), "guide link", guide, "inner",
                Joining.BUSHED));
        joined.add(new Connection("front guide link, outer", guide, "outer", carrier, "guide link",
                Joining.BALL_JOINT));
        for (final Part upper : List.of(links.upperFront(), links.upperRear())) {
            final String which = upper.name().replaceFirst("front ", "").replace(" link", "");
            final String bush = upper.name().replaceFirst("front ", "") + ", right";
            joined.add(new Connection(upper.name() + ", inner", body, bush, upper, "inner", Joining.BUSHED));
            joined.add(new Connection(upper.name() + ", outer", upper, "outer", carrier, which, Joining.BALL_JOINT));
        }
        joined.add(new Connection("steering gear", subframe.node(), "steering gear", gear, "node mount, right",
                Joining.BOLTED));
        final Part tie = links.tieRod();
        joined.add(new Connection("tie rod, inner", gear, "rack end, right", tie, "inner", Joining.BALL_JOINT));
        joined.add(new Connection("tie rod, outer", tie, "outer", carrier, "tie rod", Joining.BALL_JOINT));
        joined.add(new Connection("front hub bearing", carrier, "hub", hub, "carrier", Joining.HINGE));
        joined.add(new Connection("front brake disc", hub, "disc", corner.disc(), "hub", Joining.BOLTED));
        joined.add(new Connection("front brake caliper", carrier, "caliper", corner.caliper(), "carrier",
                Joining.BOLTED));
        joined.add(new Connection("front wheel", hub, "wheel", rim, "hub", Joining.BOLTED));
        joined.add(new Connection("front tyre", rim, "tyre", wheel.tyre(), "rim", Joining.SEATED));
        return joined;
    }
}
