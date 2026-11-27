package melons.innocent.glm.motorcycle;

import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Part;

import java.util.ArrayList;
import java.util.List;

/**
 * How what a motorcycle is ridden with and breathes out through is joined to it: its controls at the bars, on the top
 * yoke ({@link Controls}); its exhaust on the heads' ports ({@link Exhaust}); and its footrests on the mid-structure,
 * the shift linkage on the crankcase's shift shaft ({@link Footrests}).
 *
 * @param figures the bike's figures
 */
record Fittings(BikeFigures figures) {

    /** What it is ridden with at its bars, on the top yoke {@code yoke}. */
    List<Connection> controls(final Part yoke) {
        final Controls made = new Controls(figures);
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
            final String grip = "grip, " + called(side);
            joined.add(new Connection(grip + " on the bar", bar, grip, made.grip(side), "bar", Joining.SEATED));
            joined.add(new Connection(grip + "'s mirror", bar, grip, made.mirror(side), "bar", Joining.BOLTED));
        }
        return joined;
    }

    /** Its exhaust, from the heads' exhaust ports back to the muffler. */
    List<Connection> exhaust(final Mounts mounts) {
        final Exhaust made = new Exhaust(figures, mounts.front(), mounts.rear());
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

    /**
     * Its footrests: a hanger each side on the mid-structure {@code mid} and its peg on it; the gear lever on the left
     * one and the brake pedal and its master cylinder on the right; the shift linkage on the crankcase's shift shaft,
     * met again on the gear lever, and the pedal met again on the master cylinder's pushrod.
     */
    List<Connection> footrests(final Part crankcase, final Part mid) {
        final Footrests made = new Footrests(figures, crankcase.point("shift shaft, left").at());
        final Part lever = made.gearLever();
        final Part pedal = made.brakePedal();
        final Part cylinder = made.masterCylinder();
        final Part linkage = made.linkage();
        final List<Connection> joined = new ArrayList<>();
        for (final int side : List.of(-1, 1)) {
            final Part hanger = made.hanger(side);
            joined.add(new Connection(called(side) + " footrest hanger on the mid-structure", mid,
                    "footrest, " + called(side), hanger, "mid-structure", Joining.BOLTED));
            joined.add(new Connection(called(side) + " footpeg on its hanger", hanger, "peg", made.peg(side),
                    "hanger", Joining.HINGE));
            if (side < 0) {
                joined.add(new Connection("gear lever on its hanger", hanger, "lever", lever, "hanger",
                        Joining.HINGE));
            } else {
                joined.add(new Connection("brake pedal on its hanger", hanger, "lever", pedal, "hanger",
                        Joining.HINGE));
                joined.add(new Connection("rear master cylinder on its hanger", hanger, "master cylinder", cylinder,
                        "hanger", Joining.BOLTED));
            }
        }
        joined.add(new Connection("shift linkage on the shift shaft", crankcase, "shift shaft, left", linkage,
                "shaft", Joining.SPLINED));
        joined.add(new Connection("gear lever on its linkage", linkage, "lever", lever, "linkage",
                Joining.BALL_JOINT));
        joined.add(new Connection("brake pedal on the master cylinder's pushrod", cylinder, "pedal", pedal,
                "master cylinder", Joining.BALL_JOINT));
        return joined;
    }

    private String called(final int side) {
        return side < 0 ? "left" : "right";
    }
}
