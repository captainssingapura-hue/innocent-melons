package com.example.fxsuite.life.body;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.List;

/**
 * A smooth bending of space through given points to where they are to go ({@link Warp}), as a thin plate is bent:
 * the least bent of all that take each of them there, an affine map and, from each point, a pull falling off with the
 * distance from it, the distance itself in three dimensions; found by solving for the pulls and the map at once, the
 * pulls adding to nothing and turning nothing, so that far off it is the affine map alone.
 */
public record ThinPlate() implements StatelessFunctionalObject {

    public static final ThinPlate INSTANCE = new ThinPlate();

    /** The warp taking each of {@code from} to the one of {@code to} at the same place in the list. */
    public Warp through(final List<V3> from, final List<V3> to) {
        final int n = from.size();
        final int size = n + 4;
        final double[][] a = new double[size][size + 3];
        for (int i = 0; i < n; i++) {
            final V3 p = from.get(i);
            for (int j = 0; j < n; j++) {
                a[i][j] = p.minus(from.get(j)).length();
            }
            final double[] row = {1, p.x(), p.y(), p.z()};
            for (int k = 0; k < 4; k++) {
                a[i][n + k] = row[k];
                a[n + k][i] = row[k];
            }
            a[i][size] = to.get(i).x();
            a[i][size + 1] = to.get(i).y();
            a[i][size + 2] = to.get(i).z();
        }
        solve(a, size);
        final V3[] weight = new V3[n];
        for (int i = 0; i < n; i++) {
            weight[i] = new V3(a[i][size], a[i][size + 1], a[i][size + 2]);
        }
        final double[] affine = new double[12];
        for (int c = 0; c < 3; c++) {
            for (int k = 0; k < 4; k++) {
                affine[4 * c + k] = a[n + k][size + c];
            }
        }
        return new Warp(from.toArray(new V3[0]), weight, affine);
    }

    /** {@code a}'s first {@code size} columns made the unit matrix, its last three the answers, by elimination. */
    private void solve(final double[][] a, final int size) {
        for (int col = 0; col < size; col++) {
            int pivot = col;
            for (int r = col + 1; r < size; r++) {
                pivot = Math.abs(a[r][col]) > Math.abs(a[pivot][col]) ? r : pivot;
            }
            final double[] swap = a[col];
            a[col] = a[pivot];
            a[pivot] = swap;
            final double d = a[col][col];
            for (int c = col; c < size + 3; c++) {
                a[col][c] /= d;
            }
            for (int r = 0; r < size; r++) {
                if (r != col && a[r][col] != 0) {
                    final double f = a[r][col];
                    for (int c = col; c < size + 3; c++) {
                        a[r][c] -= f * a[col][c];
                    }
                }
            }
        }
    }
}
