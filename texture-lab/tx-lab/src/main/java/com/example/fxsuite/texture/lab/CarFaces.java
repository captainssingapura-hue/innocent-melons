package com.example.fxsuite.texture.lab;

import com.example.fxsuite.avatar.build.Sheet;
import com.example.fxsuite.avatar.sim.Form;
import com.example.fxsuite.avatar.sim.Vec3;
import com.example.fxsuite.texture.lab.CarBody.Side;

import java.util.ArrayList;
import java.util.List;

/**
 * The faces of a car's body, filled in over its skeleton as a building's are over its own: each a sheet of the
 * engine's ({@code build.Sheet}), made from the skeleton's edges and named for what it is. Two sorts:
 *
 * <ul>
 *   <li>its sheets of metal, of the kind {@code body}: the bonnet, the roof and the boot lid, the nose and the tail,
 *       each a curved patch between the four edges round it; a flank each side, from its belt down to its sill
 *       and its wheels' arches ({@link CarFlank}); and the frames round
 *       the glass, each a flat face with its windows cut out of it as holes;</li>
 *   <li>its windows, of the kind {@code glass}: each a flat face that fills one of those holes exactly.</li>
 * </ul>
 *
 * <p>Sheets that meet share the points along the edge between them, so there is no crack to see through.
 */
public record CarFaces(Form skeleton) {

    private static final String BODY = "body";
    private static final String GLASS = "glass";
    /** How many triangles' pairs a patch has along the car or across it, and up a flank. */
    private static final int LONG = 16;
    private static final int HIGH = 6;
    /** How many pieces the belt is in under the cabin, from the windscreen's foot to the rear window's. */
    private static final int WAIST = 30;
    /** Somewhere inside the car: every face's outside is the side away from it. */
    private static final double INSIDE = 40;
    /** How far above its sills its floor lies: a hair, so the two are told apart where they meet. */
    private static final double LIFTED = 0.5;

    /** Its sheets of metal. */
    public List<Sheet> body() {
        final List<Sheet> sheets = new ArrayList<>();
        final Patch bonnet = across("bonnet", "nose belt", "scuttle", LONG);
        final Patch roof = across("roof", "screen top", "rear top", LONG);
        final Patch boot = across("boot lid", "deck", "tail belt", LONG);
        sheets.addAll(List.of(bonnet.sheet(), roof.sheet(), boot.sheet(),
                across("nose", "nose sill", "nose belt", HIGH).sheet(),
                across("tail", "tail sill", "tail belt", HIGH).sheet()));
        sheets.add(framed("windscreen", bonnet.top(), roof.bottom()));
        sheets.add(framed("rear window", boot.bottom(), roof.top()));
        for (final Side side : Side.values()) {
            final String of = ", " + side.called();
            final List<Vec3> waist = waist(side, bonnet, boot);
            sheets.add(new CarFlank(skeleton, side).sheet("flank" + of, belt(side, bonnet, waist, boot)));
            sheets.add(Sheet.flat("cabin side" + of, BODY, round(waist, roof.edge(side)),
                    List.of(corners("door window" + of), corners("quarter window" + of)),
                    outward(waist, roof.edge(side))));
        }
        return sheets;
    }

    /**
     * What its inside has that its outside has not: its floor, and over each wheel a well running in from the
     * wheel's arch as far as {@code within} of the car's middle. A plate across each well's inner end, which the
     * wheels bring ({@link CarWheels#wells}), shuts the wheel off from the inside.
     */
    public List<Sheet> inside(final double within) {
        final Patch bonnet = across("bonnet", "nose belt", "scuttle", LONG);
        final Patch boot = across("boot lid", "deck", "tail belt", LONG);
        final List<Sheet> inside = new ArrayList<>(List.of(floor()));
        for (final Side side : Side.values()) {
            inside.addAll(new CarFlank(skeleton, side)
                    .wells(belt(side, bonnet, waist(side, bonnet, boot), boot), within));
        }
        return inside;
    }

    /** The belt under the cabin on one side: so many points, evenly, from the bonnet's corner to the boot's. */
    private List<Vec3> waist(final Side side, final Patch bonnet, final Patch boot) {
        final List<Vec3> waist = new ArrayList<>();
        for (int station = 0; station <= WAIST; station++) {
            waist.add(bonnet.edge(side).getLast().add(boot.edge(side).getFirst()
                    .subtract(bonnet.edge(side).getLast()).multiply((double) station / WAIST)));
        }
        waist.set(WAIST, boot.edge(side).getFirst());
        return waist;
    }

    /** The belt on one side from the nose's corner to the tail's: the bonnet's edge, the cabin's foot, the boot's. */
    private List<Vec3> belt(final Side side, final Patch bonnet, final List<Vec3> waist, final Patch boot) {
        final List<Vec3> belt = new ArrayList<>(bonnet.edge(side));
        belt.addAll(waist.subList(1, waist.size()));
        belt.addAll(boot.edge(side).subList(1, boot.edge(side).size()));
        return belt;
    }

    /** Its windows. */
    public List<Sheet> glass() {
        final List<Sheet> panes = new ArrayList<>();
        final List<String> all = new ArrayList<>(List.of("windscreen", "rear window"));
        for (final Side side : Side.values()) {
            all.add("door window, " + side.called());
            all.add("quarter window, " + side.called());
        }
        for (final String window : all) {
            final List<Vec3> corners = corners(window);
            panes.add(Sheet.flat(window, GLASS, corners, List.of(),
                    outward(corners.subList(0, 2), corners.subList(2, 4))));
        }
        return panes;
    }

    /**
     * Its floor, which only its inside has: a flat face within its sills, a hair above them,
     * facing up. Without it, whoever looks in at a window sees the ground under the car.
     */
    public Sheet floor() {
        // round its sills, straight past each wheel's arch: the floor is flat, and does not climb over a wheel
        final List<Vec3> outline = new ArrayList<>(run("nose sill, left", "nose sill, right"));
        for (final String station : List.of("scuttle")) {
            outline.add(skeleton.at(vertex(station + " sill, right")));
        }
        outline.addAll(run("tail sill, right", "tail sill, left"));
        for (final String station : List.of("scuttle")) {
            outline.add(skeleton.at(vertex(station + " sill, left")));
        }
        outline.add(outline.getFirst());
        final List<Vec3> lifted = new ArrayList<>();
        for (final Vec3 point : outline) {
            // where one edge ends the next begins: the point they share is taken once
            if (lifted.isEmpty() || point.add(0, 0, LIFTED).distance(lifted.getLast()) > Form.TOLERANCE) {
                lifted.add(point.add(0, 0, LIFTED));
            }
        }
        lifted.removeLast();
        return Sheet.flat("floor", "floor", lifted, List.of(), new Vec3(0, 0, 1));
    }

    /** A patch lying across the car, between the edge across it called {@code foot} and the one called {@code head}. */
    private Patch across(final String called, final String foot, final String head, final int deep) {
        return new Patch(Sheet.patch(called, BODY, run(foot + ", left", foot + ", right"),
                run(foot + ", right", head + ", right"), run(head + ", left", head + ", right"),
                run(foot + ", left", head + ", left"), LONG, deep, new Vec3(0, 0, INSIDE)), LONG, deep);
    }

    /** The frame round the window called {@code window}: the flat face between a foot and a head, less the window. */
    private Sheet framed(final String window, final List<Vec3> foot, final List<Vec3> head) {
        return Sheet.flat(window + " frame", BODY, round(foot, head), List.of(corners(window)), outward(foot, head));
    }

    /** The outline that goes along {@code foot}, up to the far end of {@code head} and back along it. */
    private List<Vec3> round(final List<Vec3> foot, final List<Vec3> head) {
        final List<Vec3> outline = new ArrayList<>(foot);
        outline.addAll(head.reversed());
        return outline;
    }

    /** The way out of the flat face between {@code foot} and {@code head}: square to it, away from the car's inside. */
    private Vec3 outward(final List<Vec3> foot, final List<Vec3> head) {
        final Vec3 along = foot.getLast().subtract(foot.getFirst());
        final Vec3 up = head.getFirst().subtract(foot.getFirst());
        final Vec3 square = new Vec3(along.y() * up.z() - along.z() * up.y(), along.z() * up.x() - along.x() * up.z(),
                along.x() * up.y() - along.y() * up.x()).normalize();
        return square.dotProduct(foot.getFirst().subtract(new Vec3(0, 0, INSIDE))) < 0 ? square.multiply(-1) : square;
    }

    /** The four corners of the window called {@code window}, going round. */
    private List<Vec3> corners(final String window) {
        final List<Vec3> corners = new ArrayList<>();
        for (final String corner : List.of("low first", "low second", "high second", "high first")) {
            corners.add(skeleton.at(vertex(window + ", " + corner)));
        }
        return corners;
    }

    /** The points along the skeleton's edge between the vertices called {@code from} and {@code to}, in that order. */
    private List<Vec3> run(final String from, final String to) {
        final int first = vertex(from);
        final int last = vertex(to);
        for (final Form.Edge edge : skeleton.edges()) {
            if (edge.from() == first && edge.to() == last) {
                return skeleton.points(edge, 5);
            }
            if (edge.from() == last && edge.to() == first) {
                return skeleton.points(edge, 5).reversed();
            }
        }
        throw new IllegalArgumentException("the skeleton has no edge between " + from + " and " + to);
    }

    private int vertex(final String called) {
        final int vertex = skeleton.vertex(called);
        if (vertex < 0) {
            throw new IllegalArgumentException("the skeleton has no vertex called " + called);
        }
        return vertex;
    }

    /**
     * A curved patch, and the points along each of its four edges as it was cut: what a flat face beside it takes
     * for its own edge, so the two meet point for point. {@code across} and {@code up} are how many pairs of
     * triangles it has each way.
     */
    private record Patch(Sheet sheet, int across, int up) {

        List<Vec3> bottom() {
            return row(0);
        }

        List<Vec3> top() {
            return row(up);
        }

        /** Its edge on {@code side}, from its foot to its head. */
        List<Vec3> edge(final Side side) {
            final List<Vec3> edge = new ArrayList<>();
            for (int high = 0; high <= up; high++) {
                edge.add(sheet.points().get((side == Side.LEFT ? 0 : across) * (up + 1) + high));
            }
            return edge;
        }

        private List<Vec3> row(final int high) {
            final List<Vec3> row = new ArrayList<>();
            for (int along = 0; along <= across; along++) {
                row.add(sheet.points().get(along * (up + 1) + high));
            }
            return row;
        }
    }
}
