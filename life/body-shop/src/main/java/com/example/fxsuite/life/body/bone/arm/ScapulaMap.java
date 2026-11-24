package com.example.fxsuite.life.body.bone.arm;

import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.math.form.Form;
import com.example.fxsuite.math.graph.Point.Spatial;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Where a point on the scapula as first sketched is on it as a form ({@link ScapulaForm}), and back: the straight-line
 * map (an affine one) that takes the sketch's landmarks nearest the form's, by least squares, both in the body's frame
 * at rest. So what was held at a point on the sketch, a muscle's origin on its blade, say, is held at the same place
 * on the form, as near as the two are alike; and a landmark of the form named, taken back to the sketch, comes to it
 * again exactly.
 */
public enum ScapulaMap {
    INSTANCE;

    /** The linear part and the offset, each row an axis of what it is taken to: x, y, z; a, b, c, offset. */
    private final double[][] forward;
    private final double[][] back;

    ScapulaMap() {
        final Map<String, V3> sketch = new LinkedHashMap<>();
        sketch.put("superior angle", new V3(7.2, 47.8, -10.0));
        sketch.put("inferior angle", new V3(9.3, 34.0, -9.2));
        sketch.put("root of the spine", new V3(6.6, 43.8, -10.3));
        sketch.put("lateral border", new V3(12.2, 38.3, -5.5));
        sketch.put("neck", new V3(14.9, 44.1, -2.4));
        sketch.put("suprascapular notch", new V3(12.2, 46.6, -5.2));
        sketch.put("coracoid knee", new V3(14.2, 46.5, -0.8));
        sketch.put("coracoid tip", new V3(14.6, 44.9, 3.6));
        sketch.put("crest's root", new V3(6.9, 44.1, -11.3));
        sketch.put("crest", new V3(10.45, 45.35, -9.0));
        sketch.put("acromion's base", new V3(14.0, 46.8, -6.6));
        sketch.put("acromial angle", new V3(18.1, 47.2, -3.7));
        sketch.put("acromion's tip", new V3(19.0, 47.3, -0.3));
        sketch.put("clavicular facet", new V3(16.6, 47.55, 0.9));
        sketch.put("glenoid", Scapula.JOINT.minus(Scapula.FACING.times(2.5)));
        final ScapulaForm f = new ScapulaForm();
        final Form form = f.form();
        final Map<V3, V3> pairs = new LinkedHashMap<>();
        sketch.forEach((name, at) -> {
            final Spatial p = f.placed().point(form.at(name));
            pairs.put(at, new V3(p.x(), p.y(), p.z()));
        });
        forward = fitted(pairs);
        back = inverse(forward);
    }

    /** {@code sketched}, a point on the scapula as first sketched, on the form. */
    public V3 toForm(final V3 sketched) {
        return applied(forward, sketched);
    }

    /** {@code formed}, a point on the form, on the scapula as first sketched. */
    public V3 toSketch(final V3 formed) {
        return applied(back, formed);
    }

    private V3 applied(final double[][] m, final V3 p) {
        final double[] q = new double[3];
        for (int r = 0; r < 3; r++) {
            q[r] = m[r][0] * p.x() + m[r][1] * p.y() + m[r][2] * p.z() + m[r][3];
        }
        return new V3(q[0], q[1], q[2]);
    }

    /** The affine map taking each key of {@code pairs} nearest its value, by least squares. */
    private double[][] fitted(final Map<V3, V3> pairs) {
        final double[][] normal = new double[4][4];
        final double[][] right = new double[4][3];
        for (final Map.Entry<V3, V3> e : pairs.entrySet()) {
            final double[] x = {e.getKey().x(), e.getKey().y(), e.getKey().z(), 1};
            final double[] y = {e.getValue().x(), e.getValue().y(), e.getValue().z()};
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    normal[i][j] += x[i] * x[j];
                }
                for (int k = 0; k < 3; k++) {
                    right[i][k] += x[i] * y[k];
                }
            }
        }
        final double[][] solved = solved(normal, right);
        final double[][] m = new double[3][4];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 4; c++) {
                m[r][c] = solved[c][r];
            }
        }
        return m;
    }

    /** {@code a} x = {@code b}, for each of {@code b}'s columns, by elimination with the largest pivot. */
    private double[][] solved(final double[][] a, final double[][] b) {
        final int n = a.length;
        final int w = b[0].length;
        final double[][] m = new double[n][n + w];
        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], 0, m[i], 0, n);
            System.arraycopy(b[i], 0, m[i], n, w);
        }
        for (int c = 0; c < n; c++) {
            int pivot = c;
            for (int r = c + 1; r < n; r++) {
                if (Math.abs(m[r][c]) > Math.abs(m[pivot][c])) {
                    pivot = r;
                }
            }
            final double[] swap = m[c];
            m[c] = m[pivot];
            m[pivot] = swap;
            for (int r = 0; r < n; r++) {
                if (r != c) {
                    final double k = m[r][c] / m[c][c];
                    for (int j = c; j < n + w; j++) {
                        m[r][j] -= k * m[c][j];
                    }
                }
            }
        }
        final double[][] x = new double[n][w];
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < w; k++) {
                x[i][k] = m[i][n + k] / m[i][i];
            }
        }
        return x;
    }

    /** The map undoing {@code m}. */
    private double[][] inverse(final double[][] m) {
        final double[][] linear = new double[3][3];
        final double[][] identity = new double[3][3];
        for (int r = 0; r < 3; r++) {
            System.arraycopy(m[r], 0, linear[r], 0, 3);
            identity[r][r] = 1;
        }
        final double[][] inv = solved(linear, identity);
        final double[][] back = new double[3][4];
        for (int r = 0; r < 3; r++) {
            System.arraycopy(inv[r], 0, back[r], 0, 3);
            for (int c = 0; c < 3; c++) {
                back[r][3] -= inv[r][c] * m[c][3];
            }
        }
        return back;
    }
}
