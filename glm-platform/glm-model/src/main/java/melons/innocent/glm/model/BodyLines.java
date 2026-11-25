package melons.innocent.glm.model;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.vehiclelab.forms.Section;

import java.util.List;

/**
 * The cast body's lines, laid out from the platform's figures, the body's own and the chassis it carries: where each
 * of its members runs, on the right, the left its mirror, and each of those that run across from side to side; and
 * how each is cast in section. Ahead, the front rails over the front subframe's mounts, the bumper's beam across
 * their tips, the upper rails over the wheel arches to the windscreen's foot, the cowl across it; along each side, the
 * sill, the hoop round the rear wheel from the sill to the rear rail, the A, B, C and D pillars and the roof's rail;
 * down the middle, the tunnel; across the floor, the seats' members and the rear seat's; behind, the rear rails over
 * the rear subframe's mounts, falling to the tail, and the tail's member across; over it all, the roof's front and
 * rear headers and its bows.
 *
 * @param proportions the platform's figures
 * @param figures     the body's own
 * @param front       the front axle's points
 * @param rear        the rear axle's points
 */
record BodyLines(Proportions proportions, BodyFigures figures, FrontPoints front, RearPoints rear) {

    /** A member: its line, and how it is cast in section. */
    record Member(List<Vec3> through, Section section) {
    }

    private static final Section SILL = new Section(12, 16, 4);
    private static final Section RAIL = new Section(8, 12, 4);
    private static final Section PILLAR = new Section(7, 10, 3);
    private static final Section ROOF_RAIL = new Section(7, 8, 3);
    private static final Section BOW = new Section(5, 6, 3);
    private static final Section CROSS = new Section(7, 9, 3);
    private static final Section TUNNEL = new Section(26, 18, 3);

    double rearAxle() {
        return -proportions.wheelbase();
    }

    double noseZ() {
        return figures.nose();
    }

    double tailZ() {
        return rearAxle() - figures.tail();
    }

    double cowlZ() {
        return -figures.cowl() * proportions.wheelbase();
    }

    double pillarZ() {
        return -figures.pillar() * proportions.wheelbase();
    }

    double sillX() {
        return figures.wide() / 2 - 20;
    }

    double floorY() {
        return 24;
    }

    double roofY() {
        return figures.roof() - 4;
    }

    double roofX() {
        return figures.wide() / 2 - 34;
    }

    /** The members on the right, each mirrored on the left. */
    List<Member> sides() {
        final double sx = sillX();
        final double sy = 29;
        final double rx = roofX();
        final double ry = roofY();
        final double cz = cowlZ();
        final double rz = rearAxle();
        final double tz = tailZ();
        return List.of(
                new Member(List.of(v(sx, sy, cz + 15), v(sx, sy, rz + 45)), SILL),
                new Member(frontRail(), RAIL),
                new Member(rearRail(), RAIL),
                new Member(List.of(v(sx - 30, 60, noseZ() - 15), v(sx - 8, 75, 40),
                        v(sx - 10, front.strutTop().y() + 2, front.strutTop().z() + 18), v(sx - 2, 95, cz + 2)),
                        new Section(6, 8, 3)),
                new Member(List.of(v(sx, sy, cz + 15), v(sx - 2, 95, cz), v(rx + 6, ry - 28, cz - 45),
                        v(rx, ry - 2, cz - 75)), PILLAR),
                new Member(List.of(v(rx, ry - 2, cz - 75), v(rx + 2, ry, cz - 140), v(rx + 1, ry, rz + 10),
                        v(rx - 3, ry - 3, tz + 45)), ROOF_RAIL),
                new Member(List.of(v(sx, sy + 6, pillarZ()), v(sx - 6, 110, pillarZ() - 2),
                        v(rx + 1, ry - 1, pillarZ() - 4)), new Section(6, 14, 3)),
                new Member(List.of(v(sx - 8, 80, rz + 30), v(rx + 3, 130, rz + 5), v(rx + 1, ry - 1, rz - 10)),
                        PILLAR),
                new Member(List.of(v(rear.mountBack().x() + 2, rear.mountBack().y() + 10, tz + 5),
                        v(rx + 2, 120, tz + 25),
                        v(rx - 3, ry - 3, tz + 45)), PILLAR),
                new Member(List.of(v(sx, sy, rz + 45), v(sx - 10, 70, rz + 30), v(sx - 13, 82, rz),
                        v(sx - 15, 70, rz - 30), v(rear.mountBack().x() + 2, rear.mountBack().y() + 10, rz - 40)),
                        RAIL));
    }

    /** The members across, from the left to the right. */
    List<Member> across() {
        final double sx = sillX();
        final double rx = roofX();
        final double ry = roofY();
        final double y = floorY();
        final double tailY = rear.mountBack().y() + 9;
        return List.of(
                new Member(List.of(v(-55, front.mountFront().y() + 1, noseZ() + 2), v(0, front.mountFront().y() + 1,
                        noseZ() + 7), v(55, front.mountFront().y() + 1, noseZ() + 2)), new Section(6, 11, 4)),
                new Member(List.of(v(-(sx - 2), 95, cowlZ()), v(sx - 2, 95, cowlZ())), new Section(9, 9, 3)),
                new Member(List.of(v(-sx, y + 4, pillarZ() + 40), v(sx, y + 4, pillarZ() + 40)), CROSS),
                new Member(List.of(v(-sx, y + 4, pillarZ() - 25), v(sx, y + 4, pillarZ() - 25)), CROSS),
                new Member(List.of(v(-sx, y + 12, rearAxle() + 62), v(sx, y + 12, rearAxle() + 62)),
                        new Section(8, 12, 3)),
                new Member(List.of(v(-50, tailY, tailZ() + 2), v(50, tailY, tailZ() + 2)), CROSS),
                new Member(List.of(v(-rx, ry - 2, cowlZ() - 75), v(0, ry + 1, cowlZ() - 78), v(rx, ry - 2,
                        cowlZ() - 75)), CROSS),
                new Member(List.of(v(-(rx - 3), ry - 3, tailZ() + 45), v(0, ry - 1, tailZ() + 45), v(rx - 3, ry - 3,
                        tailZ() + 45)), CROSS),
                bow(pillarZ() - 4), bow(rearAxle() - 10));
    }

    /** The tunnel, down the middle of the floor. */
    Member tunnel() {
        return new Member(List.of(v(0, floorY() + 9, cowlZ()), v(0, floorY() + 9, rearAxle() + 70)), TUNNEL);
    }

    /** A front rail: from the nose over the subframe's front mount, falling over its back mount into the floor. */
    private List<Vec3> frontRail() {
        final double x = front.mountFront().x() + 1.4 + RAIL.wide() / 2;
        final double high = front.mountFront().y() + 1;
        final double low = front.mountBack().y() + 9.5;
        final double back = front.mountBack().x() + 1;
        return List.of(v(x, high, noseZ() - 2), v(x, high, 30), v(back, low, -14), v(back, low,
                front.mountBack().z()), v(back, floorY() + 6, cowlZ() - 5));
    }

    /** A rear rail: from the floor up over the subframe's front mount, down over its back mount, to the tail. */
    private List<Vec3> rearRail() {
        final Vec3 ahead = rear.mountFront();
        final Vec3 behind = rear.mountBack();
        final double over = 4 + RAIL.high() / 2;
        return List.of(v(sillX() - 30, 31, rearAxle() + 70), v(ahead.x(), ahead.y() + over, ahead.z()),
                v(ahead.x(), ahead.y() + over, ahead.z() - 22), v(behind.x(), behind.y() + over, behind.z()),
                v(behind.x(), behind.y() + over, tailZ() + 4));
    }

    private Member bow(final double z) {
        return new Member(List.of(v(-roofX() - 1, roofY(), z), v(0, roofY() + 3, z), v(roofX() + 1, roofY(), z)),
                BOW);
    }

    /** A point {@code x} across, {@code y} over the ground and {@code z} along. */
    private Vec3 v(final double x, final double y, final double z) {
        return new Vec3(x, y, z);
    }
}
