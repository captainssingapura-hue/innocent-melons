package melons.innocent.glm.model;

import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * The retro saloon's lines, the 1981 notchback saloon's as its dimensioned side and front views draw them, read off
 * the drawing in millimetres, the side view as along from the front axle (ahead positive) and up from the ground, the
 * front view as how far out from the middle at each height; and the whole made larger evenly, by its front track's
 * 1,414 mm to the platform's least, 1,500 mm, so it looks as it did, a little bigger. Given in the platform's
 * centimetres. Where the drawing's length and wheelbase disagree, by some 1.6%, its overhangs are shared out as it
 * draws them and the maker's figures kept: 2,550 mm between the axles, 862 mm ahead and 1,133 mm behind. Where the
 * drawing's outline is the car's middle rather than its side, at the C pillar, whose rear window bulges back at the
 * middle, and along the boot, whose lid is crowned, or is the bumper rather than the tail, the side's edge, and the
 * quarter light's opening, are taken off the maker's square-on side photograph, scaled by its wheelbase and its
 * heights to the drawing's.
 */
record SantanaLines() {

    /** How much larger it is made: as the platform's least track is than its own. */
    private static final double SCALE = 1_500.0 / 1_414;
    /** Its rear wheel arch's middle, along and up, and how far round it is. */
    private static final double ARCH_ALONG = -2_550;
    private static final double ARCH_UP = 309;
    private static final double ARCH = 375;
    /** How far the rear door's opening stands clear of the arch; its door bottoms' height; its waist's; its rail's. */
    private static final double ARCH_CLEAR = 40;
    private static final double DOOR_FOOT = 353;
    private static final double WAIST = 920;
    private static final double RAIL = 1_290;
    /** Along where the rear door's back edge comes down to the wheel's arch. */
    private static final double DOOR_BACK = -2_355;
    /** How high the doors' glass begins, over the chrome along the waist; where each door's window is divided. */
    private static final double BELT = 935;
    private static final double FRONT_DIVIDER = -959;
    private static final double REAR_DIVIDER = -2_097;
    /** Where the tail panel stands along; how high the boot's floor is, the rear quarter carried down to it. */
    private static final double TAIL = -3_470;
    private static final double BOOT = 430;
    /** The roof's front and rear edges, where its headers run across: along and up. */
    private static final double[] FRONT_HEADER = {-1_060, 1_342};
    private static final double[] REAR_HEADER = {-2_390, 1_337};

    /** The cross-section, half-widths at heights, as the front view draws it; up, out, in millimetres. */
    private static final double[][] SECTION = {
        {292, 815}, {353, 832}, {450, 845}, {540, 848}, {650, 847}, {706, 833}, {761, 813}, {816, 802},
        {871, 796}, {920, 790}, {940, 760}, {990, 735}, {1_036, 721}, {1_091, 704}, {1_146, 681},
        {1_202, 653}, {1_257, 630}, {1_312, 601}, {1_340, 590}, {1_400, 520}};

    /** How much larger it is made than the original. */
    double scale() {
        return SCALE;
    }

    /** Where its tail panel stands, along from the front axle. */
    double tail() {
        return TAIL * SCALE / 10;
    }

    /** How high its boot's floor is. */
    double boot() {
        return BOOT * SCALE / 10;
    }

    /** How high the doors' glass begins. */
    double belt() {
        return BELT * SCALE / 10;
    }

    /** Where the front door's window is divided, the small vent ahead of the divider. */
    double frontDivider() {
        return FRONT_DIVIDER * SCALE / 10;
    }

    /** Where the rear door's window is divided, the small fixed pane behind the divider. */
    double rearDivider() {
        return REAR_DIVIDER * SCALE / 10;
    }

    /** How high the doors' waist is. */
    double waist() {
        return WAIST * SCALE / 10;
    }

    /** Where its roof's front header runs across, along and up. */
    Spot frontHeader() {
        return at(FRONT_HEADER[0], FRONT_HEADER[1]);
    }

    /** Where its roof's rear header runs across, along and up. */
    Spot rearHeader() {
        return at(REAR_HEADER[0], REAR_HEADER[1]);
    }

    /** The platform's figures for it: its wheelbase and rear track larger as it is, on 225/50 R17 tyres. */
    Proportions proportions() {
        return new Proportions(255 * SCALE, 142.2 * SCALE, 225, 50, 17);
    }

    /** The cast body's own figures inside it: its overhangs, under its roof and within its sills. */
    BodyFigures figures() {
        return new BodyFigures(86.2 * SCALE, -TAIL / 10 * SCALE - 255 * SCALE, 146, 172, 0.155, 0.6);
    }

    /**
     * A body side's outline, in the side view, along and up: its front edge where the wing meets it, up the
     * windscreen's edge, along the roof's edge, down the rear window's edge, along the boot's edge to the tail, down the
     * tail behind the bumper to the boot's floor, along to the rear wheel, round over it, and along the sill's foot.
     */
    List<Spot> side() {
        final List<Spot> out = new ArrayList<>(spots(new double[][] {
            {-395, 292}, {-395, 954}, {-496, 1_047}, {-607, 1_119}, {-718, 1_185}, {-830, 1_251}, {-941, 1_301},
            {-1_030, 1_340}, {-1_164, 1_345}, {-1_430, 1_350}, {-1_700, 1_352}, {-2_000, 1_350}, {-2_250, 1_345},
            {-2_420, 1_335}, {-2_537, 1_258}, {-2_650, 1_183}, {-2_749, 1_117}, {-2_830, 1_065}, {-2_876, 1_040},
            {-2_920, 1_024}, {-2_961, 1_012}, {-3_070, 993}, {-3_173, 977}, {-3_343, 965}, {-3_420, 957},
            {-3_441, 953}, {-3_457, 938}, {-3_463, 880}, {-3_468, 700}, {TAIL, BOOT}, {-3_000, BOOT}}));
        out.addAll(arch(ARCH, 180 - rise(ARCH, BOOT), rise(ARCH, 292)));
        return out;
    }

    /**
     * The front door's opening: its hinge line, up behind the windscreen's pillar, along under the roof's rail to the
     * B pillar, down the B pillar, along over the sill.
     */
    List<Spot> frontDoor() {
        return spots(new double[][] {
            {-451, DOOR_FOOT}, {-451, 960}, {-616, 1_047}, {-727, 1_119}, {-838, 1_185}, {-950, 1_251},
            {-1_010, RAIL}, {-1_537, RAIL}, {-1_498, WAIST}, {-1_498, DOOR_FOOT}});
    }

    /**
     * The rear door's opening: up the B pillar, along under the roof's rail, down its window frame's back edge, leaning
     * back along the C pillar, to the waist, then straight down, the door's shut line, round clear of the rear wheel's
     * arch, and along over the sill.
     */
    List<Spot> rearDoor() {
        final List<Spot> out = new ArrayList<>(spots(new double[][] {
            {-1_565, DOOR_FOOT}, {-1_565, WAIST}, {-1_598, RAIL}, {-2_205, RAIL}, {DOOR_BACK, WAIST}}));
        final double reach = ARCH + ARCH_CLEAR;
        out.addAll(arch(reach, Math.toDegrees(Math.acos((DOOR_BACK - ARCH_ALONG) / reach)), rise(reach, DOOR_FOOT)));
        return out;
    }

    /**
     * The quarter light's opening, the small fixed window in the C pillar behind the rear door, as the side photograph
     * shows it: up its front edge just behind the door's shut line, to its top under the roof's rail, down its long
     * edge to its sharp back corner, and along the waist.
     */
    List<Spot> quarterLight() {
        return spots(new double[][] {
            {-2_380, 932}, {-2_382, 1_278}, {-2_404, 1_286}, {-2_762, 950}, {-2_748, 940}});
    }

    /** The rear wheel's arch, its lip as the body side's outline runs round it. */
    Arch rearArch() {
        return new Arch(ARCH_ALONG, ARCH_UP, ARCH, 180 - rise(ARCH, BOOT), rise(ARCH, 292));
    }

    /**
     * How far out from the middle its side is, at {@code up} over the ground and {@code along} from the front axle,
     * in centimetres: as the front view's section has it, drawn in a little round the tail's corners and the nose's.
     */
    double half(final double along, final double up) {
        final double z = along * 10 / SCALE;
        final double tail = Math.clamp((-z - 3_300) / 290, 0, 1);
        final double nose = Math.clamp((z - 650) / 150, 0, 1);
        return (section(up * 10 / SCALE) - 30 * tail * tail - 30 * nose * nose) * SCALE / 10;
    }

    /** The section's half-width at {@code h} millimetres up, smoothly between the heights the drawing gives. */
    private double section(final double h) {
        final int last = SECTION.length - 1;
        if (h <= SECTION[0][0] || h >= SECTION[last][0]) {
            return SECTION[h <= SECTION[0][0] ? 0 : last][1];
        }
        int i = 1;
        while (SECTION[i][0] < h) {
            i++;
        }
        final double h0 = SECTION[i - 1][0];
        final double span = SECTION[i][0] - h0;
        final double t = (h - h0) / span;
        final double m0 = slope(i - 1) * span;
        final double m1 = slope(i) * span;
        final double t2 = t * t;
        final double t3 = t2 * t;
        return (2 * t3 - 3 * t2 + 1) * SECTION[i - 1][1] + (t3 - 2 * t2 + t) * m0 + (-2 * t3 + 3 * t2) * SECTION[i][1]
                + (t3 - t2) * m1;
    }

    /**
     * How steeply the section's half-width changes at its {@code k}th height, from its neighbours': level where it
     * turns, and never so steep the curve overshoots the drawing's heights.
     */
    private double slope(final int k) {
        final int a = Math.max(k - 1, 0);
        final int b = Math.min(k + 1, SECTION.length - 1);
        final double before = k > 0 ? secant(k - 1) : secant(k);
        final double after = k < SECTION.length - 1 ? secant(k) : secant(k - 1);
        if (before * after <= 0) {
            return 0;
        }
        final double m = (SECTION[b][1] - SECTION[a][1]) / (SECTION[b][0] - SECTION[a][0]);
        final double most = 3 * Math.min(Math.abs(before), Math.abs(after));
        return Math.clamp(m, -most, most);
    }

    /** How steeply the section's half-width changes from its {@code k}th height to the next. */
    private double secant(final int k) {
        return (SECTION[k + 1][1] - SECTION[k][1]) / (SECTION[k + 1][0] - SECTION[k][0]);
    }

    /**
     * Round the rear wheel: points on a circle {@code reach} millimetres from the arch's middle, from {@code from}
     * degrees round to {@code to}, nought ahead and ninety straight up.
     */
    private List<Spot> arch(final double reach, final double from, final double to) {
        final List<Spot> points = new ArrayList<>();
        final int steps = 24;
        for (int k = 0; k <= steps; k++) {
            final double a = Math.toRadians(from + (to - from) * k / steps);
            points.add(at(ARCH_ALONG + reach * Math.cos(a), ARCH_UP + reach * Math.sin(a)));
        }
        return points;
    }

    /** How far round, in degrees from ahead, a circle {@code reach} from the arch's middle is at {@code up}. */
    private double rise(final double reach, final double up) {
        return Math.toDegrees(Math.asin(Math.clamp((up - ARCH_UP) / reach, -1, 1)));
    }

    private List<Spot> spots(final double[][] millimetres) {
        final List<Spot> out = new ArrayList<>();
        for (final double[] p : millimetres) {
            out.add(at(p[0], p[1]));
        }
        return out;
    }

    /** A point of the drawing's, along and up in millimetres, in the platform's centimetres, made larger. */
    private Spot at(final double along, final double up) {
        return new Spot(along * SCALE / 10, up * SCALE / 10);
    }
}
