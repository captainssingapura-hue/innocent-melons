package com.example.fxsuite.life.body.fat;

import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.cage.Building;

/**
 * The fat cage's torso ({@link FatCage}): its rings, level, each point on the fat's outside along a line in to the
 * torso's middle at that height, between its front and its back, at its angle round from straight ahead by the right;
 * the right side found, the left its mirror; none where an arm goes out. Where a line meets nothing, between the legs,
 * the point is set across from its neighbour. The point under the crotch, found from below.
 */
final class Torso {

    private static final int COLUMNS = FatCage.COLUMNS;
    /** How far out the lines start, cm. */
    private static final double OUT = 40;

    private final FatTarget target;
    private final V3 crotch;

    Torso(final FatTarget target) {
        this.target = target;
        final double hip = target.bind().frame(Part.RIGHT_THIGH).at().y();
        final double middle = middle(hip);
        crotch = target.onto(new V3(0, hip - 30, middle), V3.Y, FatTarget.Region.TRUNK, 1, 30)
                .map(FatTarget.Hit::at).orElse(new V3(0, hip - 9, middle));
    }

    /** The point under the crotch. */
    V3 crotch() {
        return crotch;
    }

    /** The torso's rings at {@code heights} added to {@code b}, every point numbered by ring and column. */
    int[][] rings(final Building b, final double[] heights) {
        final int[][] grid = new int[heights.length][COLUMNS];
        double last = middle(heights[0] + 3);
        for (int r = 0; r < heights.length; r++) {
            final double y = heights[r];
            final double mid = middleOr(y, last);
            last = mid;
            final V3[] right = new V3[COLUMNS / 2 + 1];
            for (int j = 0; j <= COLUMNS / 2; j++) {
                final double a = 2 * Math.PI * j / COLUMNS;
                final V3 way = new V3(Math.sin(a), 0, Math.cos(a));
                final V3 c = new V3(0, y, mid);
                right[j] = target.onto(c.plus(way.times(OUT)), way.times(-1), FatTarget.Region.TRUNK, 1, OUT)
                        .map(FatTarget.Hit::at).orElse(null);
            }
            across(right, y);
            for (int j = 0; j < COLUMNS; j++) {
                grid[r][j] = -1;
                if (r == FatCage.ARMPITS + 1 && (j == FatCage.ARM || j == COLUMNS - FatCage.ARM)) {
                    continue;
                }
                final V3 p = j <= COLUMNS / 2 ? right[j] : mirror(right[COLUMNS - j]);
                grid[r][j] = b.add(p, "torso " + r + ", column " + j);
            }
            for (int j = 0; j <= COLUMNS / 2; j++) {
                if (grid[r][j] >= 0) {
                    b.fellows(grid[r][j], grid[r][(COLUMNS - j) % COLUMNS]);
                }
            }
        }
        faces(b, grid);
        return grid;
    }

    /** Where a line met nothing, the point set across from its neighbour: on the middle, or as far out. */
    private void across(final V3[] right, final double y) {
        for (int j = 0; j < right.length; j++) {
            if (right[j] != null) {
                continue;
            }
            final V3 n = j == 0 ? right[1] : j == right.length - 1 ? right[j - 1] : right[j - 1] != null
                    ? right[j - 1] : right[j + 1];
            right[j] = j == 0 || j == right.length - 1 ? new V3(0, y, n.z()) : n;
        }
        right[0] = new V3(0, right[0].y(), right[0].z());
        right[right.length - 1] = new V3(0, right[right.length - 1].y(), right[right.length - 1].z());
    }

    private void faces(final Building b, final int[][] grid) {
        final int a = FatCage.ARMPITS;
        for (int r = 0; r + 1 < grid.length; r++) {
            for (int j = 0; j < COLUMNS; j++) {
                final int n = (j + 1) % COLUMNS;
                final boolean opening = (r == a || r == a + 1) && (j == FatCage.ARM - 1 || j == FatCage.ARM
                        || j == COLUMNS - FatCage.ARM - 1 || j == COLUMNS - FatCage.ARM);
                if (!opening) {
                    b.face(grid[r][j], grid[r][n], grid[r + 1][n], grid[r + 1][j]);
                }
            }
        }
        b.face(grid[grid.length - 1]);
    }

    /** The torso's middle, front to back, at height {@code y}: between its front and its back on the body's middle. */
    private double middle(final double y) {
        return middleOr(y, 0);
    }

    private double middleOr(final double y, final double otherwise) {
        final var front = target.onto(new V3(0, y, OUT), V3.Z.times(-1), FatTarget.Region.TRUNK, 1, 2 * OUT);
        final var back = target.onto(new V3(0, y, -OUT), V3.Z, FatTarget.Region.TRUNK, 1, 2 * OUT);
        return front.isPresent() && back.isPresent() ? (front.get().at().z() + back.get().at().z()) / 2 : otherwise;
    }

    private V3 mirror(final V3 v) {
        return new V3(-v.x(), v.y(), v.z());
    }
}
