package melons.innocent.glm.model;

import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Platform;
import melons.innocent.vehiclelab.forms.Shapes;

import java.util.ArrayList;
import java.util.List;

/**
 * The GLM platform's rear axle, a five-link axle, made to a car's figures and joined part to part from the underbody
 * out: the subframe bushed to the underbody at its four mounts; on each side, the spring's link bushed to the subframe
 * and the wheel's carrier on its outer end; the other four links bushed to the subframe and each on its ball joint at
 * the carrier; the hub turning in the carrier, the disc bolted to the hub and the caliper to the carrier; the wheel
 * bolted to the hub and the tyre seated on it; the air spring on its seat on the spring's link up to the underbody;
 * and the damper from the carrier up to the underbody. Said as on the right, the left its mirror.
 *
 * @param proportions the car's figures
 */
public record RearAxle(Proportions proportions) {

    /** How many flat sides its round parts are round in. */
    private static final int ROUND = 24;

    /** A large SUV's. */
    public RearAxle() {
        this(new Proportions());
    }

    /** It, as a platform to be put together. */
    public Platform platform() {
        final RearPoints points = new RearPoints(proportions);
        final Shapes shapes = new Shapes(ROUND);
        final Part body = new RearBody(points, shapes).part();
        final Part subframe = new RearSubframe(points).part();
        final RearLinks links = new RearLinks(points, shapes);
        final Part spring = links.spring();
        final List<Part> others = List.of(links.upperFront(), links.upperRear(), links.lowerFront(), links.toe());
        final List<String> at = List.of("upper front", "upper rear", "lower front", "toe link");
        final RearCorner corner = new RearCorner(points, shapes);
        final Part carrier = corner.carrier();
        final Part hub = corner.hub();
        final RearWheel wheel = new RearWheel(proportions, points, shapes);
        final Part rim = wheel.wheel();
        final RearSprings springs = new RearSprings(points, shapes);
        final Part air = springs.spring();
        final Part damper = springs.damper();
        final List<Connection> joined = new ArrayList<>();
        for (final String side : List.of("right", "left")) {
            for (final String end : List.of("front", "back")) {
                joined.add(new Connection("subframe mount, " + end + ", " + side, body, "subframe mount, " + end + ", "
                        + side, subframe, "body mount, " + end + ", " + side, Joining.BUSHED));
            }
        }
        joined.add(new Connection("spring link, inner", subframe, "spring link, right", spring, "inner",
                Joining.BUSHED));
        joined.add(new Connection("spring link, outer", spring, "outer", carrier, "spring link", Joining.BUSHED));
        for (int k = 0; k < others.size(); k++) {
            final Part link = others.get(k);
            joined.add(new Connection(link.name() + ", inner", subframe, link.name() + ", right", link, "inner",
                    Joining.BUSHED));
            joined.add(new Connection(link.name() + ", outer", link, "outer", carrier, at.get(k), Joining.BALL_JOINT));
        }
        joined.add(new Connection("hub bearing", carrier, "hub", hub, "carrier", Joining.HINGE));
        joined.add(new Connection("brake disc", hub, "disc", corner.disc(), "hub", Joining.BOLTED));
        joined.add(new Connection("brake caliper", carrier, "caliper", corner.caliper(), "carrier", Joining.BOLTED));
        joined.add(new Connection("wheel", hub, "wheel", rim, "hub", Joining.BOLTED));
        joined.add(new Connection("tyre", rim, "tyre", wheel.tyre(), "rim", Joining.SEATED));
        joined.add(new Connection("air spring, seat", spring, "spring seat", air, "seat", Joining.BOLTED));
        joined.add(new Connection("air spring, top", body, "spring top, right", air, "top", Joining.BOLTED));
        joined.add(new Connection("damper, foot", carrier, "damper", damper, "foot", Joining.BUSHED));
        joined.add(new Connection("damper, top", body, "damper top, right", damper, "top", Joining.BUSHED));
        return new Platform("the GLM platform's rear axle", body, joined);
    }
}
