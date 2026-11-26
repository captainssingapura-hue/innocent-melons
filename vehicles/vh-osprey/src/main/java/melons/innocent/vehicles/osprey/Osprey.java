package melons.innocent.vehicles.osprey;

import com.example.fxsuite.vulkan.form.Forming;
import com.example.fxsuite.vulkan.form.Lofting;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * The Bell Boeing V-22 Osprey as it is made, in its own frame: across to its right, up, along to its nose, in
 * centimetres, the ground under its wheels at nothing high and its wing's pivot line at nothing along. Its fuselage
 * a skin of rings, deep and squared in section, its nose rounded down, its tail swept up to the ramp; a sponson
 * low on each side, where its main wheels are; a high wing across its top, swept a little forward and rising a
 * little to its tips; at each tip a nacelle that turns about a pivot across the wing, from flying ahead to standing
 * up and a little past; on each nacelle's front a rotor of three blades; a tail of a stabiliser with a fin at each
 * end. How big each is goes by its published figures ({@link Figures}); how each is shaped is the builder's.
 *
 * <p>A nacelle's form is in its own frame, its pivot at its middle and its length along its third way, flying
 * ahead; a rotor's is about its hub, turning about its third way. Whoever draws it turns each as it is told.
 */
public record Osprey(Figures figures) {

    /** How high the wing's middle is at its root; how much its tips rise and come forward, of how far out they are. */
    private static final double WING_Y = 370;
    private static final double DIHEDRAL = 0.06;
    private static final double SWEEP = 0.105;
    private static final double CHORD = 250;
    private static final double WING_DEEP = 30;
    /** A nacelle: how far it reaches behind its pivot and ahead of it, to its hub; how thick; its spinner. */
    private static final double BEHIND = 200;
    private static final double HUB = 200;
    private static final double NACELLE = 70;
    private static final double SPINNER = 60;
    /** A blade: how far from the hub it begins, how wide it is there and at its tip, how thin. */
    private static final double ROOT = 60;
    private static final double ROOT_CHORD = 75;
    private static final double TIP_CHORD = 40;
    private static final double BLADE_THIN = 8;
    private static final int ROUND = 16;

    /** The Osprey at its published size. */
    public Osprey() {
        this(new Figures());
    }

    /** Every part of it. */
    public List<Part> parts() {
        final List<Part> parts = new ArrayList<>();
        parts.add(new Part("body", Part.Finish.PAINT, Part.Moves.BODY, body()));
        parts.add(new Part("windows", Part.Finish.GLASS, Part.Moves.BODY, windows()));
        parts.add(new Part("wheels", Part.Finish.RUBBER, Part.Moves.BODY, wheels()));
        parts.add(new Part("left nacelle", Part.Finish.PAINT, Part.Moves.LEFT_NACELLE, nacelle()));
        parts.add(new Part("right nacelle", Part.Finish.PAINT, Part.Moves.RIGHT_NACELLE, nacelle()));
        parts.add(new Part("left blades", Part.Finish.PAINT, Part.Moves.LEFT_ROTOR, blades()));
        parts.add(new Part("right blades", Part.Finish.PAINT, Part.Moves.RIGHT_ROTOR, blades()));
        parts.add(new Part("left hub", Part.Finish.METAL, Part.Moves.LEFT_ROTOR, spinner()));
        parts.add(new Part("right hub", Part.Finish.METAL, Part.Moves.RIGHT_ROTOR, spinner()));
        return parts;
    }

    /** Where a nacelle turns, at the wing's tip: the left's ({@code side} -1) or the right's (1). */
    public Vec3 pivot(final int side) {
        final double out = figures.rotorsApart();
        return new Vec3(side * out, WING_Y + DIHEDRAL * out, SWEEP * out);
    }

    /** How far ahead of its pivot, along a nacelle, its rotor's hub is. */
    public double hub() {
        return HUB;
    }

    /**
     * How far below its wheels the lowest of its rotors' tips comes, with its nacelles {@code tilt} degrees up from
     * flying ahead: nothing, where they are up far enough to clear the ground.
     */
    public double belowWheels(final double tilt) {
        final double up = Math.toRadians(tilt);
        // the hub's height, and the disc's lowest point: it reaches the blade's length down as the disc stands up
        final double hubY = pivot(1).y() + HUB * Math.sin(up);
        final double lowest = hubY - figures.rotorReach() * Math.cos(up);
        return Math.max(0, -lowest);
    }

    /** The fuselage, its sponsons, the wing and its root's fairing, and the tail. */
    private Mesh body() {
        final Forming forming = new Forming(new Vec3(0, 200, 0));
        final Lofting lofting = new Lofting(forming);
        final double nose = figures.length() * 0.46;
        final double tail = nose - figures.length();
        // each station: along, its floor, its roof, how wide either side
        final double[][] stations = {{nose, 150, 172, 8}, {nose - 60, 112, 250, 72}, {nose - 150, 82, 300, 112},
            {nose - 280, 66, 320, 130}, {300, 60, 330, 135}, {-200, 60, 330, 135}, {-450, 120, 330, 125},
            {-700, 220, 330, 95}, {tail, 282, 330, 50}};
        final List<List<Vec3>> rings = new ArrayList<>();
        for (final double[] s : stations) {
            rings.add(lofting.ring(new Vec3(0, (s[1] + s[2]) / 2, s[0]), new Vec3(1, 0, 0), new Vec3(0, 1, 0), s[3],
                    (s[2] - s[1]) / 2, 4, ROUND));
        }
        lofting.lofted(rings);
        for (final int side : new int[] {-1, 1}) {
            lofting.lofted(List.of(sponson(lofting, side, -160, 0.6), sponson(lofting, side, 40, 1),
                    sponson(lofting, side, 260, 0.6)));
        }
        lofting.box(new Vec3(0, 345, 0), new Vec3(150, 40, CHORD * 0.9));
        wing(lofting);
        tail(lofting, tail);
        return forming.formed();
    }

    private List<Vec3> sponson(final Lofting lofting, final int side, final double along, final double share) {
        return lofting.ring(new Vec3(side * 150, 115, along), new Vec3(1, 0, 0), new Vec3(0, 1, 0), 75 * share,
                55 * share, 3, 10);
    }

    /** The wing, from tip to tip, in rings along it, thinner toward its tips. */
    private void wing(final Lofting lofting) {
        final List<List<Vec3>> rings = new ArrayList<>();
        final double reach = figures.wingReach();
        for (int k = -4; k <= 4; k++) {
            final double x = reach * k / 4.0;
            final double out = Math.abs(x);
            rings.add(lofting.ring(new Vec3(x, WING_Y + DIHEDRAL * out, SWEEP * out), new Vec3(0, 0, 1),
                    new Vec3(0, 1, 0), CHORD / 2 * (1 - 0.1 * out / reach), WING_DEEP * (1 - 0.3 * out / reach), 2.6,
                    12));
        }
        lofting.lofted(rings);
    }

    /** The stabiliser across the tail, and a fin standing at each end of it. */
    private void tail(final Lofting lofting, final double end) {
        final double at = end + 110;
        lofting.box(new Vec3(0, 335, at), new Vec3(560, 16, 150));
        for (final int side : new int[] {-1, 1}) {
            lofting.box(new Vec3(side * 280, (230 + figures.high() * 0.8) / 2, at - 10),
                    new Vec3(18, figures.high() * 0.8 - 230, 170));
        }
    }

    /** The windscreen and the cockpit's side windows, standing a little proud of the skin. */
    private Mesh windows() {
        final Forming forming = new Forming(new Vec3(0, 200, 0));
        final Lofting lofting = new Lofting(forming);
        final double nose = figures.length() * 0.46;
        final List<List<Vec3>> rings = new ArrayList<>();
        for (final double[] s : new double[][] {{nose - 75, 225, 70}, {nose - 130, 262, 104}, {nose - 200, 275, 118}}) {
            rings.add(lofting.ring(new Vec3(0, s[1], s[0]), new Vec3(1, 0, 0), new Vec3(0, 1, 0), s[2] + 3, 34, 4,
                    ROUND));
        }
        lofting.lofted(rings);
        return forming.formed();
    }

    /** The nose wheel, and the main wheels under the sponsons. */
    private Mesh wheels() {
        final Forming forming = new Forming(new Vec3(0, 40, 0));
        final Lofting lofting = new Lofting(forming);
        final double nose = figures.length() * 0.46;
        lofting.bar(new Vec3(-14, 30, nose - 220), new Vec3(14, 30, nose - 220), 30, 14);
        for (final int side : new int[] {-1, 1}) {
            lofting.bar(new Vec3(side * 130, 38, 40), new Vec3(side * 170, 38, 40), 38, 14);
        }
        return forming.formed();
    }

    /** A nacelle in its own frame: about its pivot, reaching behind it and ahead to its hub. */
    private Mesh nacelle() {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        final Lofting lofting = new Lofting(forming);
        final List<List<Vec3>> rings = new ArrayList<>();
        for (final double[] s : new double[][] {{-BEHIND, 0.55}, {-BEHIND + 50, 0.9}, {0, 1}, {HUB - 60, 1},
            {HUB - 10, 0.85}}) {
            rings.add(lofting.ring(new Vec3(0, 0, s[0]), new Vec3(1, 0, 0), new Vec3(0, 1, 0), NACELLE * s[1],
                    NACELLE * s[1], 2, ROUND));
        }
        lofting.lofted(rings);
        return forming.formed();
    }

    /** A rotor's three blades about its hub, in the plane square to its turning. */
    private Mesh blades() {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        final Lofting lofting = new Lofting(forming);
        for (int blade = 0; blade < 3; blade++) {
            final double angle = 2 * Math.PI * blade / 3;
            final Vec3 out = new Vec3(Math.cos(angle), Math.sin(angle), 0);
            final Vec3 chord = new Vec3(-Math.sin(angle), Math.cos(angle), 0);
            final List<List<Vec3>> rings = new ArrayList<>();
            for (final double[] s : new double[][] {{ROOT, ROOT_CHORD}, {figures.rotorReach(), TIP_CHORD}}) {
                rings.add(lofting.ring(out.times(s[0]), chord, new Vec3(0, 0, 1), s[1] / 2, BLADE_THIN / 2, 2.4, 8));
            }
            lofting.lofted(rings);
        }
        return forming.formed();
    }

    /** A rotor's hub, its spinner rounded to a point ahead. */
    private Mesh spinner() {
        final Forming forming = new Forming(new Vec3(0, 0, 0));
        final Lofting lofting = new Lofting(forming);
        final List<List<Vec3>> rings = new ArrayList<>();
        for (final double[] s : new double[][] {{-15, 1}, {15, 0.95}, {45, 0.6}, {62, 0.15}}) {
            rings.add(lofting.ring(new Vec3(0, 0, s[0]), new Vec3(1, 0, 0), new Vec3(0, 1, 0), SPINNER * s[1],
                    SPINNER * s[1], 2, ROUND));
        }
        lofting.lofted(rings);
        return forming.formed();
    }
}
