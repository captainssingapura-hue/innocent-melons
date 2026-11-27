package melons.innocent.glm.motorcycle;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Point;
import melons.innocent.glm.engine.Hull;
import melons.innocent.glm.existences.MotorcycleParts;
import melons.innocent.vehiclelab.forms.Outlines;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * What a motorcycle's rider rests their feet on and works with them, at its places ({@link FootPlaces}): a hanger
 * each side, an aluminium plate on two standoffs out from the mid-structure's plate, outside the exhaust on the left;
 * a peg on each, folding up on its pivot; the gear lever on the left hanger, its toe peg ahead of the left peg, turned
 * by the rider's toe; the shift linkage from the shift shaft out of the crankcase's left end, behind the clutch cover,
 * out under the exhaust's front header to the gear lever's arm, between the header and the hanger; the brake pedal
 * on the right hanger, its pad ahead of the right peg; and the back brake's master cylinder inside the right hanger,
 * over the pedal's arm, its pushrod down to it.
 *
 * @param figures the bike's figures
 * @param shaft   where the shift shaft comes out of the crankcase's left end
 */
record Footrests(BikeFigures figures, Vec3 shaft) {

    private static final int ROUND = 16;
    /** How far out from the middle each hanger's plate is, and how thick; how far round its standoffs are. */
    private static final double HANGER = 28;
    private static final double PLATE = 0.6;
    private static final double STANDOFF = 1.1;
    /** How far round a peg is and how long. */
    private static final double PEG = 1.1;
    private static final double PEG_LONG = 9;
    /**
     * How far out from the middle the master cylinder is, inside the right hanger; and the shift linkage, inside the
     * left one and outside the exhaust's headers.
     */
    private static final double INSIDE = 25.5;
    private static final double LINKAGE = 26.9;
    /** How long the shift shaft's arm is; how far round a lever is and its toe peg. */
    private static final double ARM = 5;
    private static final double LEVER = 0.6;
    private static final double TOE = 1.1;

    /** The hanger on side {@code side}, -1 the left. */
    Part hanger(final int side) {
        final Shapes shapes = new Shapes(ROUND);
        final Outlines outlines = new Outlines(ROUND);
        final FootPlaces at = places();
        final List<Spot> round = new ArrayList<>(outlines.circle(spot(at.bolt(true)), 2.2));
        round.addAll(outlines.circle(spot(at.bolt(false)), 2.2));
        round.addAll(outlines.circle(spot(at.peg()), 2));
        round.addAll(outlines.circle(spot(at.pivot()), 1.8));
        if (side > 0) {
            round.addAll(outlines.circle(spot(cylinderFoot()), 1.5));
        }
        final List<Mesh> made = new ArrayList<>(List.of(new Plate(new Hull(round).outline(), List.of(),
                at.out(new Vec3(0, 0, 0), side * (HANGER + PLATE / 2)), new Vec3(1, 0, 0), new Vec3(0, 1, 0),
                PLATE).mesh()));
        for (final boolean upper : List.of(true, false)) {
            made.add(shapes.drum(at.out(at.bolt(upper), side * Frames.PLATES), at.out(at.bolt(upper),
                    side * HANGER), STANDOFF));
        }
        final List<Point> points = new ArrayList<>(List.of(new Point("mid-structure", bolt(side)),
                new Point("peg", at.out(at.peg(), side * (HANGER + PLATE))),
                new Point("lever", at.out(at.pivot(), side * (HANGER + PLATE)))));
        if (side > 0) {
            points.add(new Point("master cylinder", at.out(cylinderFoot(), HANGER)));
        }
        return new Part("footrest hanger, " + called(side), Group.FRAME, false, points, shapes.joined(made),
                new Made(Material.ALUMINIUM, false)).as(new MotorcycleParts.FootrestHanger());
    }

    /** The peg on side {@code side}, -1 the left; where the ball of the rider's foot rests on it its foot. */
    Part peg(final int side) {
        final FootPlaces at = places();
        final Vec3 foot = at.out(at.peg().plus(new Vec3(0, PEG, 0)), side * (HANGER + PLATE + PEG_LONG / 2));
        return new Part("footpeg, " + called(side), Group.TRIM, false, List.of(new Point("hanger",
                at.out(at.peg(), side * (HANGER + PLATE))), new Point("foot", foot)),
                new Shapes(ROUND).drum(at.out(at.peg(), side * (HANGER + PLATE)), at.out(at.peg(),
                        side * (HANGER + PLATE + PEG_LONG)), PEG), new Made(Material.STEEL, false))
                .as(new MotorcycleParts.FootPeg());
    }

    /** The gear lever, on the left; where the rider's toe lifts it, under its toe peg, its toe. */
    Part gearLever() {
        final Shapes shapes = new Shapes(ROUND);
        final FootPlaces at = places();
        final double outer = -(HANGER + PLATE + 0.8);
        final List<Mesh> made = List.of(shapes.drum(at.out(at.pivot(), -(LINKAGE - 0.5)), at.out(at.pivot(),
                outer - 0.4), 1.0), shapes.capsule(at.out(at.pivot(), outer), at.out(at.toe(), outer), LEVER),
                shapes.capsule(at.out(at.pivot(), -LINKAGE), rodEnd(), LEVER),
                shapes.drum(at.out(at.toe(), outer + 0.5), at.out(at.toe(), outer - 5.2), TOE));
        final Vec3 toe = at.out(at.toe().minus(new Vec3(0, TOE, 0)), outer - 2.5);
        return new Part("gear lever", Group.STEERING, false, List.of(new Point("hanger", at.out(at.pivot(),
                -(HANGER + PLATE))), new Point("linkage", rodEnd()), new Point("toe", toe)), shapes.joined(made),
                new Made(Material.STEEL, false)).as(new MotorcycleParts.FootPedal());
    }

    /** The shift linkage: the arm on the shift shaft, out past the exhaust, and its rod on to the gear lever. */
    Part linkage() {
        final Shapes shapes = new Shapes(ROUND);
        final FootPlaces at = places();
        final Vec3 arm = at.out(shaft, -LINKAGE);
        final Vec3 end = arm.plus(new Vec3(0, ARM, 0));
        return new Part("shift linkage", Group.STEERING, false, List.of(new Point("shaft", shaft),
                new Point("lever", rodEnd())), shapes.joined(List.of(shapes.drum(shaft, at.out(shaft,
                        -(LINKAGE + 0.5)), 0.7), shapes.capsule(arm, end, LEVER), shapes.capsule(end, rodEnd(), 0.4))),
                new Made(Material.STEEL, false)).as(new MotorcycleParts.ShiftLinkage());
    }

    /** The brake pedal, on the right; where the rider's toe presses it, on its pad, its toe. */
    Part brakePedal() {
        final Shapes shapes = new Shapes(ROUND);
        final FootPlaces at = places();
        final double outer = HANGER + PLATE + 0.8;
        final List<Mesh> made = List.of(shapes.drum(at.out(at.pivot(), INSIDE - 0.5), at.out(at.pivot(),
                outer + 0.4), 1.0), shapes.capsule(at.out(at.pivot(), outer), at.out(at.pad(), outer), 0.7),
                shapes.capsule(at.out(at.pivot(), INSIDE), at.out(at.pushrod(), INSIDE), LEVER),
                shapes.drum(at.out(at.pad(), outer - 0.5), at.out(at.pad(), outer + 5), 1.2));
        return new Part("brake pedal", Group.STEERING, false, List.of(new Point("hanger", at.out(at.pivot(),
                HANGER + PLATE)), new Point("master cylinder", at.out(at.pushrod(), INSIDE)),
                new Point("toe", at.out(at.pad().plus(new Vec3(0, 1.2, 0)), outer + 2.5))), shapes.joined(made),
                new Made(Material.STEEL, false)).as(new MotorcycleParts.FootPedal());
    }

    /** The back brake's master cylinder, inside the right hanger, its pushrod down to the pedal's arm. */
    Part masterCylinder() {
        final Shapes shapes = new Shapes(ROUND);
        final FootPlaces at = places();
        final Vec3 rod = at.out(at.pushrod(), INSIDE);
        final Vec3 low = rod.plus(new Vec3(-0.5, 2, 0));
        final Vec3 high = rod.plus(new Vec3(-2, 8, 0));
        return new Part("rear master cylinder", Group.STEERING, false, List.of(new Point("hanger",
                at.out(cylinderFoot(), HANGER)), new Point("pedal", rod)), shapes.joined(List.of(
                        shapes.drum(low, high, 1.2), shapes.capsule(rod, low, 0.35),
                        shapes.drum(at.out(cylinderFoot(), INSIDE), at.out(cylinderFoot(), HANGER), 0.8))),
                new Made(Material.ALUMINIUM, false)).as(new MotorcycleParts.MasterCylinder());
    }

    /** Where the hanger on side {@code side} is bolted to the mid-structure, at its plate. */
    Vec3 bolt(final int side) {
        return places().out(places().bolt(true), side * Frames.PLATES);
    }

    /** Where the master cylinder is bolted to the right hanger, halfway up it. */
    private Vec3 cylinderFoot() {
        return places().pushrod().plus(new Vec3(-1.25, 5, 0));
    }

    /** Where the shift linkage's rod meets the gear lever's arm, inside the left hanger, under the lever's pivot. */
    private Vec3 rodEnd() {
        return places().out(places().pivot().minus(new Vec3(0, 4, 0)), -LINKAGE);
    }

    private FootPlaces places() {
        return new FootPlaces(figures);
    }

    private Spot spot(final Vec3 p) {
        return new Spot(p.x(), p.y());
    }

    private String called(final int side) {
        return side < 0 ? "left" : "right";
    }
}
