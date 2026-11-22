package com.example.fxsuite.texture.lab;

import com.example.fxsuite.avatar.build.Sheet;
import com.example.fxsuite.avatar.sim.Form;
import com.example.fxsuite.avatar.sim.Vec3;
import com.example.fxsuite.texture.lab.CarBody.Side;

import java.util.ArrayList;
import java.util.List;

/**
 * One flank of a car's body: the sheet from its belt down to its sill, from the nose to the tail, with an arch cut
 * up into it over each wheel. It is made a column at a time: under each point of the belt, a column of points down
 * to the sill beneath, or to the arch where a wheel is beneath; and a strip of triangles between each column and
 * the next. So it follows its sill round the arches and cannot fold on itself, as a patch filled in between four
 * edges does when one of them doubles back.
 *
 * <p>It is bowed out between the windscreen's foot and the rear window's, as much as the skeleton's post at the
 * windscreen's foot is, and less and less toward the nose and the tail, where it meets them flat.
 */
public record CarFlank(Form skeleton, Side side) {

    private static final int HIGH = 6;
    /** A curved edge of the sill is followed in pieces no longer than this, in centimetres. */
    private static final double FINE = 2;

    /**
     * The flank under {@code belt}, which runs from the nose's corner to the tail's along the foot of the bonnet,
     * the cabin and the boot: the points the sheets above it have along their own edges, so that the flank meets
     * each of them point for point.
     */
    public Sheet sheet(final String called, final List<Vec3> belt) {
        final List<Vec3> points = columns(belt);
        final List<Vec3> normals = new ArrayList<>();
        final List<int[]> triangles = new ArrayList<>();
        for (int column = 0; column < belt.size(); column++) {
            for (int row = 0; row <= HIGH; row++) {
                normals.add(out(points, column, row, belt.size()));
                if (column + 1 < belt.size() && row < HIGH) {
                    final int here = column * (HIGH + 1) + row;
                    final int next = here + HIGH + 1;
                    triangles.add(new int[] {here, next, next + 1});
                    triangles.add(new int[] {here, next + 1, here + 1});
                }
            }
        }
        return new Sheet(called, "body", points, triangles, normals);
    }

    /**
     * The wells over this flank's wheels, under {@code belt}: from each arch's edge in the flank, a band running in
     * toward the car's middle as far as {@code within} of it, the shape of the arch all the way, with a face each
     * way. With a plate across its inner end it shuts the wheel off from the car's inside, so nothing is seen
     * through an arch but the wheel.
     */
    public List<Sheet> wells(final List<Vec3> belt, final double within) {
        final List<Vec3> points = columns(belt);
        final double level = skeleton.at(vertex("scuttle sill")).z();
        final List<Sheet> wells = new ArrayList<>();
        List<Vec3> band = new ArrayList<>();
        for (int column = 0; column < belt.size(); column++) {
            final boolean over = at(points, column, 0).z() > level + 1e-6
                    || column + 1 < belt.size() && at(points, column + 1, 0).z() > level + 1e-6
                    || column > 0 && at(points, column - 1, 0).z() > level + 1e-6;
            if (over) {
                band.add(at(points, column, 0));
            } else if (!band.isEmpty()) {
                wells.addAll(banded(band, within));
                band = new ArrayList<>();
            }
        }
        if (!band.isEmpty()) {
            wells.addAll(banded(band, within));
        }
        return wells;
    }

    /** The band from {@code edge}, the points of one arch's edge, in to {@code within} of the car's middle. */
    private List<Sheet> banded(final List<Vec3> edge, final double within) {
        final Vec3 middle = edge.get(edge.size() / 2);
        final Form.Vertex axle = skeleton.vertices().get(vertex(
                Math.abs(middle.y() - skeleton.at(vertex("front axle")).y()) < Math.abs(middle.y()
                        - skeleton.at(vertex("rear axle")).y()) ? "front axle" : "rear axle"));
        final List<Sheet> faces = new ArrayList<>();
        for (final double way : new double[] {1, -1}) {
            final List<Vec3> points = new ArrayList<>();
            final List<Vec3> normals = new ArrayList<>();
            final List<int[]> triangles = new ArrayList<>();
            for (int piece = 0; piece < edge.size(); piece++) {
                final Vec3 rim = edge.get(piece);
                // toward the wheel's middle, for the face the wheel sees; away from it, for the one the cabin sees
                final Vec3 toWheel = new Vec3(0, axle.along() - rim.y(), axle.up() - rim.z()).normalize();
                points.add(rim);
                points.add(new Vec3(side.out() * within, rim.y(), rim.z()));
                normals.add(toWheel.multiply(way));
                normals.add(toWheel.multiply(way));
                if (piece + 1 < edge.size()) {
                    triangles.add(new int[] {2 * piece, 2 * piece + 1, 2 * piece + 3});
                    triangles.add(new int[] {2 * piece, 2 * piece + 3, 2 * piece + 2});
                }
            }
            faces.add(new Sheet(axle.name() + ", well, " + (way > 0 ? "under" : "over"), "well", points, triangles,
                    normals));
        }
        return faces;
    }

    /**
     * The flank's points under {@code belt}: a column under each of the belt's points, from the sill or the arch
     * beneath it up to the belt, each column's points one after another from its foot.
     */
    private List<Vec3> columns(final List<Vec3> belt) {
        final List<Vec3> sill = sill();
        final List<Vec3> level = List.of(skeleton.at(vertex("nose sill")), skeleton.at(vertex("scuttle sill")),
                skeleton.at(vertex("tail sill")));
        final List<Vec3> points = new ArrayList<>();
        for (final Vec3 head : belt) {
            // the flank as it would be with no wheel under it, from the level sill up to the belt; over a wheel
            // the column begins higher up that same sheet, at the arch, so the arch's edge lies in the flank
            final Vec3 foot = foot(level, head.y());
            final double begins = (foot(sill, head.y()).z() - foot.z()) / (head.z() - foot.z());
            final double full = side.out() * bow() * fullness(head.y(), belt);
            for (int row = 0; row <= HIGH; row++) {
                final double up = begins + (1 - begins) * row / HIGH;
                points.add(foot.add(head.subtract(foot).multiply(up)).add(full * (1 - Math.pow(2 * up - 1, 2)), 0, 0));
            }
        }
        return points;
    }

    /** The way out of the flank at one of its points: square to the points beside it, away from the car's middle. */
    private Vec3 out(final List<Vec3> points, final int column, final int row, final int columns) {
        final Vec3 along = at(points, Math.min(column + 1, columns - 1), row)
                .subtract(at(points, Math.max(column - 1, 0), row));
        final Vec3 up = at(points, column, Math.min(row + 1, HIGH)).subtract(at(points, column, Math.max(row - 1, 0)));
        final Vec3 square = new Vec3(along.y() * up.z() - along.z() * up.y(), along.z() * up.x() - along.x() * up.z(),
                along.x() * up.y() - along.y() * up.x()).normalize();
        return square.x() * side.out() < 0 ? square.multiply(-1) : square;
    }

    private Vec3 at(final List<Vec3> points, final int column, final int row) {
        return points.get(column * (HIGH + 1) + row);
    }

    /**
     * How much of its full bow the flank has, {@code along} the car: all of it from the windscreen's foot to the
     * rear window's, and none at the two ends of {@code belt}, going smoothly from the one to the other.
     */
    private double fullness(final double along, final List<Vec3> belt) {
        final double scuttle = skeleton.at(vertex("scuttle")).y();
        final double deck = skeleton.at(vertex("deck")).y();
        final double share = along > scuttle ? (belt.getFirst().y() - along) / (belt.getFirst().y() - scuttle)
                : along < deck ? (along - belt.getLast().y()) / (deck - belt.getLast().y()) : 1;
        return share * share * (3 - 2 * share);
    }

    /** How far the flank is bowed out at its fullest: as far as the post at the windscreen's foot is. */
    private double bow() {
        final int low = vertex("scuttle sill");
        final int high = vertex("scuttle");
        for (final Form.Edge edge : skeleton.edges()) {
            if (edge.curved() && (edge.from() == low && edge.to() == high || edge.from() == high && edge.to() == low)) {
                return Math.abs(edge.curve().control().x());
            }
        }
        return 0;
    }

    /** The sill from the nose's corner to the tail's, round over each wheel's arch, as points along it. */
    public List<Vec3> sill() {
        final List<Vec3> sill = new ArrayList<>(List.of(skeleton.at(vertex("nose sill"))));
        final List<String> through = new ArrayList<>(List.of("nose sill"));
        for (final String wheel : List.of("front", "rear")) {
            through.addAll(List.of(wheel + " arch fore", wheel + " arch fore spring", wheel + " arch aft spring",
                    wheel + " arch aft", wheel.equals("front") ? "scuttle sill" : "tail sill"));
        }
        for (int leg = 0; leg + 1 < through.size(); leg++) {
            final int from = vertex(through.get(leg));
            final int to = vertex(through.get(leg + 1));
            final Form.Edge edge = skeleton.edges().stream()
                    .filter(one -> one.from() == from && one.to() == to || one.from() == to && one.to() == from)
                    .findFirst().orElseThrow(() -> new IllegalArgumentException("the sill is broken at " + from));
            final List<Vec3> next = edge.from() == from ? skeleton.points(edge, FINE)
                    : skeleton.points(edge, FINE).reversed();
            sill.addAll(next.subList(1, next.size()));
        }
        return sill;
    }

    /** The sill's point {@code along} the car: where the sill doubles back over a wheel, the highest there. */
    private Vec3 foot(final List<Vec3> sill, final double along) {
        Vec3 foot = null;
        for (int piece = 0; piece + 1 < sill.size(); piece++) {
            final Vec3 one = sill.get(piece);
            final Vec3 other = sill.get(piece + 1);
            if ((along - one.y()) * (along - other.y()) <= 0) {
                final double span = other.y() - one.y();
                final Vec3 here = Math.abs(span) < 1e-9 ? (one.z() > other.z() ? one : other)
                        : one.add(other.subtract(one).multiply((along - one.y()) / span));
                foot = foot == null || here.z() > foot.z() ? here : foot;
            }
        }
        if (foot == null) {
            throw new IllegalArgumentException("the sill does not reach " + along + " along the car");
        }
        return foot;
    }

    /** The number of the vertex called {@code called} on this side. */
    private int vertex(final String called) {
        final int vertex = skeleton.vertex(called + ", " + side.called());
        if (vertex < 0) {
            throw new IllegalArgumentException("the skeleton has no vertex called " + called + ", " + side.called());
        }
        return vertex;
    }
}
