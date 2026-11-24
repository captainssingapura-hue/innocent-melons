package com.example.fxsuite.life.body.bone.form;

import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.math.form.Face;
import com.example.fxsuite.math.form.Form;
import com.example.fxsuite.math.form.Patch;
import com.example.fxsuite.math.form.Run;
import com.example.fxsuite.math.graph.Point.Spatial;
import melons.innocent.math.curves.CurveImpl;

import java.util.List;

/**
 * What a bone says about a part of its form, by its names: a ridge along a path of its edges, a sheet through one of
 * its faces, a knob about one of its landmarks, a pit carved there. Each drawn as the body's sketches draw bone, where
 * the form is placed.
 */
public sealed interface BonePiece permits BonePiece.Ridge, BonePiece.Sheet, BonePiece.Knob, BonePiece.Pit {

    /** How far apart, in centimetres, a ridge's sections are taken along it. */
    double SECTION = 0.4;

    /** It drawn on {@code s}: the part of {@code form} it names, the form placed as {@code placed} says. */
    void drawn(Form form, Placed placed, Sketch s);

    /**
     * A ridge along the landmarks named in {@code path}: at each share of its length along, as wide as {@code width}
     * says and as deep as {@code depth} says, its depth toward {@code deep} (a way in the form's frame, taken square
     * to the path), blended {@code blend} into what is drawn before it. A border, a crest, a process.
     */
    record Ridge(String name, List<String> path, CurveImpl<?> width, CurveImpl<?> depth, Spatial deep,
                 double blend) implements BonePiece {

        @Override
        public void drawn(final Form form, final Placed placed, final Sketch s) {
            final Run run = form.run(path, SECTION / 2);
            final int n = Math.max(2, (int) Math.ceil(run.length() / SECTION));
            final Sketch inner = s.fresh();
            inner.blend(0.15);
            for (int i = 0; i < n; i++) {
                final double t0 = (double) i / n;
                final double t1 = (double) (i + 1) / n;
                final Spatial a = run.at(t0);
                final Spatial b = run.at(t1);
                final Spatial along = b.minus(a).unit();
                final Spatial square = deep.minus(along.times(deep.dot(along)));
                final double w0 = width.at(t0);
                final double w1 = width.at(t1);
                final double d = (depth.at(t0) + depth.at(t1)) / 2;
                inner.flat(placed.body(a), placed.body(b), w0 / 2, w1 / 2, placed.bodyWay(square.unit()),
                        d / ((w0 + w1) / 2));
            }
            s.blend(blend).group(inner);
        }
    }

    /**
     * A sheet through the face named {@code face}: {@code thick} through, bowed {@code bow} toward {@code toward} (a
     * way in the form's frame) in its middle, in {@code across} by {@code down} pieces, blended {@code blend}. A
     * blade, a fossa's floor, a shelf.
     */
    record Sheet(String name, String face, double thick, double bow, Spatial toward, int across, int down,
                 double blend) implements BonePiece {

        @Override
        public void drawn(final Form form, final Placed placed, final Sketch s) {
            final Face f = form.faces().stream().filter(x -> x.name().equals(face)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(form.name() + " has no face " + face));
            final Patch patch = new Patch(form, f, SECTION / 2);
            final Spatial[][] grid = new Spatial[across + 1][down + 1];
            for (int i = 0; i <= across; i++) {
                for (int j = 0; j <= down; j++) {
                    final double u = (double) i / across;
                    final double v = (double) j / down;
                    grid[i][j] = patch.at(u, v).plus(toward.times(bow * Math.sin(Math.PI * u) * Math.sin(Math.PI * v)));
                }
            }
            // its plates joined without blending, which would ridge it where they meet; the sheet blended as one
            final Sketch inner = s.fresh();
            inner.blend(0);
            for (int i = 0; i < across; i++) {
                for (int j = 0; j < down; j++) {
                    plate(inner, placed, grid[i][j], grid[i + 1][j], grid[i + 1][j + 1]);
                    plate(inner, placed, grid[i][j], grid[i + 1][j + 1], grid[i][j + 1]);
                }
            }
            s.blend(blend).group(inner);
        }

        /** The triangle {@code a b c} laid, unless it has drawn to a line or a point, as a side drawn to one does. */
        private void plate(final Sketch inner, final Placed placed, final Spatial a, final Spatial b, final Spatial c) {
            if (b.minus(a).cross(c.minus(a)).length() > 1e-6) {
                inner.plate(placed.body(a), placed.body(b), placed.body(c), thick);
            }
        }
    }

    /**
     * A knob about the landmark named {@code at}, moved {@code shift} off it: an egg lying along {@code along},
     * {@code ra} each way along it, {@code rb} toward {@code facing} and {@code rc} square to both, blended
     * {@code blend}. A tubercle, an angle, a rim.
     */
    record Knob(String name, String at, Spatial shift, Spatial along, Spatial facing, double ra, double rb, double rc,
                double blend) implements BonePiece {

        @Override
        public void drawn(final Form form, final Placed placed, final Sketch s) {
            s.blend(blend).egg(placed.body(form.at(at).plus(shift)), placed.bodyWay(along), placed.bodyWay(facing),
                    ra, rb, rc);
        }
    }

    /** A pit carved about the landmark named {@code at}, moved {@code shift} off it, {@code radius} round. A socket. */
    record Pit(String name, String at, Spatial shift, double radius, double blend) implements BonePiece {

        @Override
        public void drawn(final Form form, final Placed placed, final Sketch s) {
            s.blend(blend).carve().ball(placed.body(form.at(at).plus(shift)), radius);
        }
    }
}
