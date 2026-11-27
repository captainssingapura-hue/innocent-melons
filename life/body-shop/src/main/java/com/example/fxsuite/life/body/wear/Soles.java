package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.existence.Wearables;
import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * A boot's sole, and its heel: a slab under the foot as wide as the boot's outline and a little wider, its welt; flat
 * underneath, in the part's own frame; and under its back a heel, a slab deeper again, as far forward as told. Its
 * outline the boot's own: how far out of what is beneath the boot each point is ({@link Plating#shaped}), taken where
 * the point would be raised square to the sole into the foot, so the slab stands straight down from it.
 */
public record Soles() implements StatelessFunctionalObject {

    public static final Soles INSTANCE = new Soles();

    /**
     * Where a sole lies under {@code part}, said in its own frame as the papers' figure has it: the level its outline
     * is taken at, inside the foot; its top, under the boot's; its tread's underside; how far out its welt stands past
     * the boot's outside; how far forward its heel comes, and the heel's underside, if it has one (the same as the
     * tread's if not); the box it lies in, in the part's frame; how finely it is found and meshed, cm.
     *
     * @param part    what it moves with
     * @param level   where its outline is taken, up the part
     * @param top     its top, up the part
     * @param tread   its tread's underside, up the part
     * @param welt    how far out its outline stands from what is beneath the boot
     * @param heel    how far forward its heel comes, along the part
     * @param under   the heel's underside, up the part
     * @param box     where it lies, in the part's frame: lowest x, y, z, then highest
     * @param sample  how finely its outline is found
     * @param cell    how finely it is meshed
     */
    public record Sole(Part part, double level, double top, double tread, double welt, double heel, double under,
                       double[] box, double sample, double cell) {
    }

    /** {@code sole}, under a boot shaped over {@code d}, in the frame {@code f} its part has as the body is bound. */
    public Plate of(final Sole sole, final Rig.Frame f, final Field.Sampled d) {
        final Bounds b = Bounds.INSTANCE;
        final Field.Sampled outline = outline(sole, f, d, b.box(f, sole.box()));
        final List<Field.Element> made = new ArrayList<>(List.of(new Field.Element("tread", Field.Compound.of(List.of(
                new Field.Element("outline", outline, Field.Op.ADD, 0),
                b.cut("top", f, new V3(0, sole.top(), 0), V3.Y, 0),
                b.cut("underside", f, new V3(0, sole.tread(), 0), new V3(0, -1, 0), 0))), Field.Op.ADD, 0)));
        if (sole.under() < sole.tread()) {
            made.add(new Field.Element("heel", Field.Compound.of(List.of(
                    new Field.Element("outline", outline, Field.Op.ADD, 0),
                    b.cut("top", f, new V3(0, sole.tread() + sole.cell(), 0), V3.Y, 0),
                    b.cut("underside", f, new V3(0, sole.under(), 0), new V3(0, -1, 0), 0),
                    b.cut("front", f, new V3(0, 0, sole.heel()), V3.Z, 0))), Field.Op.ADD, 0));
        }
        return new Plate(new Wearables.Boot(), sole.part(), Mesher.of(new Field(made), sole.cell()));
    }

    /**
     * How far outside the boot's outline, and its welt, each point of {@code box} is, {@code sole.sample()} apart:
     * how far outside what is beneath the boot ({@code d}) the point is, raised square to the sole to its level, less
     * the welt.
     */
    private Field.Sampled outline(final Sole sole, final Rig.Frame f, final Field.Sampled d, final double[] box) {
        final double h = sole.sample();
        final int[] n = {(int) Math.ceil((box[3] - box[0]) / h) + 1, (int) Math.ceil((box[4] - box[1]) / h) + 1,
            (int) Math.ceil((box[5] - box[2]) / h) + 1};
        final float[] v = new float[n[0] * n[1] * n[2]];
        IntStream.range(0, v.length).parallel().forEach(at -> {
            final V3 p = new V3(box[0] + at % n[0] * h, box[1] + at / n[0] % n[1] * h, box[2] + at / (n[0] * n[1]) * h);
            final V3 local = f.local(p);
            final V3 raised = f.place(new V3(local.x(), sole.level(), local.z()));
            v[at] = (float) (d.at(raised.x(), raised.y(), raised.z()) - sole.welt());
        });
        return new Field.Sampled(new double[] {box[0], box[1], box[2]}, h, n, v, box);
    }
}
