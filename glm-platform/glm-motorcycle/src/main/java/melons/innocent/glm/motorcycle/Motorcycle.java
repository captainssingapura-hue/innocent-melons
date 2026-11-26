package melons.innocent.glm.motorcycle;

import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Platform;
import melons.innocent.glm.engine.EngineFigures;
import melons.innocent.glm.engine.EngineInternals;
import melons.innocent.glm.engine.EngineRange;

import java.util.ArrayList;
import java.util.List;

/**
 * A motorcycle round its V twin, put together from the crankcase out as the engine carries its loads: the engine,
 * covered, and its belt drive; the trellis frame bolted to the front head and the steering head welded in it; the
 * yokes turning in the steering head and the fork's legs clamped in them, the front axle, wheel, tyre, brake disc and
 * caliper on them; the mid-structure bolted to the crankcase behind the gearbox, the subframe on it and the swingarm
 * pivoting in it; the back axle, wheel, tyre, brake disc and caliper on the swingarm, and a shock either side from it
 * up to the subframe ({@link BikeFigures}).
 *
 * @param twin the engine's figures
 */
public record Motorcycle(EngineFigures twin) {

    /** The motorcycle round the range's twin. */
    public Motorcycle() {
        this(new EngineRange().v2());
    }

    /** Its figures. */
    public BikeFigures figures() {
        return new BikeFigures(twin);
    }

    /** It, its crankcase its root. */
    public Platform platform() {
        final Platform engine = new EngineInternals(twin).platform(true);
        final Part crankcase = engine.root();
        final List<Connection> joined = new ArrayList<>(engine.connections());
        final Mounts mounts = new Mounts(figures(), carried(joined, "head, bank 2"), carried(joined, "head, bank 1"),
                crankcase, carried(joined, "timing cover"));
        final Frames frames = new Frames(figures(), mounts, beneath(engine));
        final List<Connection> front = front(mounts, frames);
        final List<Connection> back = back(crankcase, frames);
        joined.addAll(front);
        joined.addAll(back);
        joined.addAll(again(mounts, front, back, carried(joined, "rear pulley")));
        joined.addAll(bodywork(frames, front, back));
        joined.addAll(controls(carried(front, "top yoke")));
        joined.addAll(exhaust(mounts));
        return new Platform("the motorcycle", crankcase, joined);
    }

    /** What it is ridden with, at its bars, on the top yoke ({@link Controls}). */
    private List<Connection> controls(final Part yoke) {
        final Controls made = new Controls(figures());
        final Part bar = made.handlebar();
        final Part cylinder = made.masterCylinder();
        final List<Connection> joined = new ArrayList<>(List.of(
                new Connection("handlebar on its risers", yoke, "bars", bar, "yoke", Joining.BOLTED),
                new Connection("gauge on the top yoke", yoke, "bars", made.gauge(), "yoke", Joining.BOLTED),
                new Connection("front master cylinder on the bar", bar, "grip, right", cylinder, "bar",
                        Joining.BOLTED),
                new Connection("brake lever on its master cylinder", cylinder, "bar", made.lever(1), "bar",
                        Joining.HINGE),
                new Connection("clutch lever on the bar", bar, "grip, left", made.lever(-1), "bar", Joining.HINGE)));
        for (final int side : List.of(-1, 1)) {
            final String grip = "grip, " + (side < 0 ? "left" : "right");
            joined.add(new Connection(grip + " on the bar", bar, grip, made.grip(side), "bar", Joining.SEATED));
            joined.add(new Connection(grip + "'s mirror", bar, grip, made.mirror(side), "bar", Joining.BOLTED));
        }
        return joined;
    }

    /** Its exhaust, from the heads' exhaust ports back to the muffler ({@link Exhaust}). */
    private List<Connection> exhaust(final Mounts mounts) {
        final Exhaust made = new Exhaust(figures(), mounts.front(), mounts.rear());
        final Part front = made.frontHeader();
        final Part rear = made.rearHeader();
        final Part collector = made.collector();
        final Part muffler = made.muffler();
        return List.of(
                new Connection("front header on its port", mounts.front(), "exhaust port 2.1", front, "port",
                        Joining.BOLTED),
                new Connection("collector on the front header", front, "collector", collector, "headers",
                        Joining.WELDED),
                new Connection("back header on its port", mounts.rear(), "exhaust port 1.1", rear, "port",
                        Joining.BOLTED),
                new Connection("muffler on the collector", collector, "muffler", muffler, "collector",
                        Joining.SEATED),
                new Connection("heat shield on the muffler", muffler, "collector", made.heatShield(), "muffler",
                        Joining.BOLTED),
                new Connection("collector on the back header", rear, "collector", collector, "headers",
                        Joining.WELDED),
                new Connection("back header into the front", front, "collector", rear, "collector", Joining.WELDED));
    }

    /** How high {@code engine} reaches under where the seat goes, as far across as the seat is wide. */
    private double beneath(final Platform engine) {
        final double[] along = new Bodywork(figures(), null).seatRun();
        return engine.connections().stream().map(Connection::carried).distinct()
                .flatMap(p -> p.form().points().stream())
                .filter(v -> v.x() < along[0] && v.x() > along[1] && Math.abs(v.z() - figures().centre()) < 16)
                .mapToDouble(v -> v.y()).max().orElse(figures().ground());
    }

    /** The front: the trellis on the front head, the steering head, yokes, legs and the front wheel on them. */
    private List<Connection> front(final Mounts mounts, final Frames frames) {
        final FrontEnd end = new FrontEnd(figures());
        final BikeWheels wheels = new BikeWheels(figures());
        final Part trellis = frames.trellis();
        final Part head = end.head();
        final Part low = end.yoke(false);
        final Part high = end.yoke(true);
        final Part left = end.leg(-1);
        final Part right = end.leg(1);
        final Part axle = wheels.axle(true, BikeFigures.LEGS);
        final Part wheel = wheels.wheel(true);
        return List.of(
                new Connection("trellis on the front head", mounts.front(), "top", trellis, "front head",
                        Joining.BOLTED),
                new Connection("steering head in the trellis", trellis, "steering head", head, "middle",
                        Joining.WELDED),
                new Connection("bottom yoke in the steering head", head, "bottom", low, "head", Joining.HINGE),
                new Connection("top yoke on the steering head", head, "top", high, "head", Joining.HINGE),
                new Connection("left fork leg in the yokes", low, "leg, left", left, "yoke", Joining.BOLTED),
                new Connection("right fork leg in the yokes", low, "leg, right", right, "yoke", Joining.BOLTED),
                new Connection("front axle in the fork", left, "axle", axle, "left", Joining.BOLTED),
                new Connection("front wheel on its axle", axle, "hub", wheel, "hub", Joining.HINGE),
                new Connection("front tyre on its rim", wheel, "hub", wheels.tyre(true), "rim", Joining.SEATED),
                new Connection("front brake disc on its wheel", wheel, "hub", wheels.disc(true), "hub",
                        Joining.BOLTED),
                new Connection("front caliper on the fork", left, "axle", wheels.caliper(true), "mount",
                        Joining.BOLTED));
    }

    /** The back: the mid-structure on the crankcase, the subframe, the swingarm, its shocks and the back wheel. */
    private List<Connection> back(final Part crankcase, final Frames frames) {
        final RearEnd end = new RearEnd(figures(), frames);
        final BikeWheels wheels = new BikeWheels(figures());
        final Part mid = frames.midStructure();
        final Part swingarm = end.swingarm();
        final Part axle = wheels.axle(false, 2 * Frames.ARMS);
        final Part wheel = wheels.wheel(false);
        return List.of(
                new Connection("mid-structure on the crankcase", crankcase, "mount, right", mid, "crankcase",
                        Joining.BOLTED),
                new Connection("subframe on the mid-structure", mid, "subframe", frames.subframe().part(), "front",
                        Joining.BOLTED),
                new Connection("swingarm on its pivot", mid, "pivot", swingarm, "pivot", Joining.HINGE),
                new Connection("left shock on the swingarm", swingarm, "shock, left", end.shock(-1), "lug",
                        Joining.BUSHED),
                new Connection("right shock on the swingarm", swingarm, "shock, right", end.shock(1), "lug",
                        Joining.BUSHED),
                new Connection("rear axle in the swingarm", swingarm, "axle", axle, "hub", Joining.BOLTED),
                new Connection("rear wheel on its axle", axle, "hub", wheel, "hub", Joining.HINGE),
                new Connection("rear tyre on its rim", wheel, "hub", wheels.tyre(false), "rim", Joining.SEATED),
                new Connection("rear brake disc on its wheel", wheel, "hub", wheels.disc(false), "hub",
                        Joining.BOLTED),
                new Connection("rear caliper on the swingarm", swingarm, "axle", wheels.caliper(false), "mount",
                        Joining.BOLTED));
    }

    /**
     * Where its parts meet again, by a second way from the crankcase: the trellis on the mid-structure behind the
     * back head, the mid-structure on the back head, the front axle through the fork's right leg, each shock's top on
     * the subframe, and the rear pulley on the back axle.
     */
    private List<Connection> again(final Mounts mounts, final List<Connection> front, final List<Connection> back,
                                   final Part pulley) {
        final Part trellis = carried(front, "trellis frame");
        final Part mid = carried(back, "mid-structure");
        final Part subframe = carried(back, "subframe");
        final Part axle = carried(back, "rear axle");
        return List.of(
                new Connection("trellis on the mid-structure", mid, "trellis", trellis, "rear node", Joining.BOLTED),
                new Connection("mid-structure on the back head", mounts.rear(), "top", mid, "back head",
                        Joining.BOLTED),
                new Connection("front axle in the right fork leg", carried(front, "fork leg, right"), "axle",
                        carried(front, "front axle"), "right", Joining.BOLTED),
                new Connection("left shock on the subframe", subframe, "shock, left", carried(back, "shock, left"),
                        "subframe", Joining.BUSHED),
                new Connection("right shock on the subframe", subframe, "shock, right", carried(back, "shock, right"),
                        "subframe", Joining.BUSHED),
                new Connection("rear pulley on its axle", axle, "pulley", pulley, "axle", Joining.BOLTED));
    }

    /**
     * Its bodywork: the airbox cover on the trellis's top rails; the seat, the fuel tank and the side covers on the
     * subframe; the front fender on the fork's legs and the back one on the subframe's tail ({@link Bodywork},
     * {@link Fenders}).
     */
    private List<Connection> bodywork(final Frames frames, final List<Connection> front,
                                      final List<Connection> back) {
        final Bodywork body = new Bodywork(figures(), frames);
        final Fenders fenders = new Fenders(figures(), frames);
        final Part subframe = carried(back, "subframe");
        final Part fender = fenders.front();
        return List.of(
                new Connection("airbox cover on the trellis", carried(front, "trellis frame"), "top rail",
                        body.airboxCover(), "trellis", Joining.BOLTED),
                new Connection("seat on the subframe", subframe, "front", body.seat(), "subframe", Joining.BOLTED),
                new Connection("fuel tank in the subframe", subframe, "front", body.tank(), "subframe",
                        Joining.BOLTED),
                new Connection("left side cover", subframe, "front", body.sideCover(-1), "subframe", Joining.BOLTED),
                new Connection("right side cover", subframe, "front", body.sideCover(1), "subframe", Joining.BOLTED),
                new Connection("front fender on the fork", carried(front, "fork leg, left"), "axle", fender, "fork",
                        Joining.BOLTED),
                new Connection("front fender on the right leg", carried(front, "fork leg, right"), "axle", fender,
                        "fork, right", Joining.BOLTED),
                new Connection("rear fender on the subframe", subframe, "tail", fenders.rear(), "subframe",
                        Joining.BOLTED));
    }

    /** The part {@code joined} carries whose name ends {@code called}. */
    private Part carried(final List<Connection> joined, final String called) {
        return joined.stream().map(Connection::carried).filter(p -> p.name().endsWith(called)).findFirst()
                .orElseThrow();
    }
}
