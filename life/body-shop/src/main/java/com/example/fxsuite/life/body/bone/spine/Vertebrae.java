package com.example.fxsuite.life.body.bone.spine;

import com.example.fxsuite.life.body.Sketch;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

/**
 * How a vertebra is drawn, from the axis down, in the body's frame, each part as big as its level has it
 * ({@link Level}): its body, a short oval column, waisted; its pedicles and laminae round its canal; its spinous
 * process back and down, split at its tip in the neck, a broad hatchet low in the back; its transverse processes out,
 * in the neck holed for its arteries, in the chest back to the ribs; its articular processes above and below.
 */
public record Vertebrae() implements StatelessFunctionalObject {

    public static final Vertebrae INSTANCE = new Vertebrae();

    /** The vertebra at {@code level}, as light as {@code light}. */
    public void draw(final Sketch s, final Level level, final double light) {
        final V3[] f = level.frame();
        final double w = level.wide() * light;
        s.blend(0.3).disc(f[0], f[1], w / 2, level.high() / 2, 0.28, f[2], level.deep() * light / w);
        s.blend(0.25).carve().ring(f[0], f[1], w / 2 + 0.38, 0.42);
        final V3 root = arch(s, level, light);
        spinous(s, level, root);
        for (final int side : new int[] {1, -1}) {
            transverse(s, level, side);
            s.blend(0.6);
            final double back = -level.deep() / 2 - level.pedicle();
            final double cw = level.canalWide();
            s.egg(level.at(side * (cw / 2 + 0.25), 0.55 * level.high(), back - 0.15), 0.38, 0.45, 0.38);
            s.egg(level.at(side * (cw / 2 + 0.15), -0.55 * level.high(), back - 0.45), 0.36, 0.45, 0.36);
        }
    }

    /** The pedicles back from the body, the laminae meeting behind the canal; where they meet. */
    private V3 arch(final Sketch s, final Level level, final double light) {
        final V3[] f = level.frame();
        final double h = level.high();
        final double cw = level.canalWide();
        final double back = -level.deep() * light / 2;
        final double pl = level.pedicle();
        final double pr = (level.lumbar() ? 0.52 : level.cervical() ? 0.33 : 0.42) * light;
        final double w = level.wide() * light;
        final V3 root = level.at(0, 0, back - pl - level.canalDeep());
        s.blend(0.4);
        for (final int side : new int[] {1, -1}) {
            final V3 pedicle = level.at(side * (cw / 2 + 0.2), 0.1 * h, back - pl);
            s.rod(level.at(side * w * 0.3, 0.15 * h, back + 0.3), pedicle, pr, pr);
            s.flat(pedicle, root, pr * 0.95, pr * 1.05, f[1], 1.0);
            s.flat(level.at(side * (cw / 2 + 0.15), 0.25 * h, back - pl - 0.2),
                    level.at(0, 0.3 * h, back - pl - level.canalDeep() + 0.15), pr * 0.75, pr * 0.8, f[2], 0.55);
        }
        return root;
    }

    /** The spinous process, back and down from where the laminae meet. */
    private void spinous(final Sketch s, final Level level, final V3 root) {
        final V3[] f = level.frame();
        final double slope = Math.toRadians(level.slope());
        final V3 tip = root.plus(f[1].times(-level.spinous() * Math.sin(slope)))
                .plus(f[2].times(-level.spinous() * Math.cos(slope)));
        if (level.lumbar()) {
            s.flat(root, tip, 0.85, 0.8, V3.X, 0.42);
        } else if (level.cervical() && level != Level.C7) {
            s.rod(root, tip, 0.38, 0.3);
            s.ball(tip.plus(V3.X.times(0.3)), 0.3);
            s.ball(tip.minus(V3.X.times(0.3)), 0.3);
        } else {
            s.rod(root, tip, 0.45, 0.33);
            s.ball(tip, 0.42);
        }
    }

    /** A transverse process: in the neck forward and holed; in the chest back to its rib; low in the back long. */
    private void transverse(final Sketch s, final Level level, final int side) {
        final V3[] f = level.frame();
        final double w = level.wide();
        if (level.cervical()) {
            final V3 hole = level.at(side * (w / 2 + 0.55), 0, -0.1);
            s.blend(0.35).rod(level.at(side * w * 0.4, 0, 0), level.at(side * level.transverse(), -0.1, -0.25),
                    0.36, 0.28);
            s.ring(hole, f[1], 0.36, 0.2);
            s.blend(0.2).carve().rod(hole.plus(f[1].times(0.6)), hole.minus(f[1].times(0.6)), 0.2, 0.2);
            s.blend(0.35);
            return;
        }
        final double back = -level.deep() / 2 - level.pedicle();
        final V3 out = level.at(side * level.transverse(), 0.15 * level.high(), back - level.transverseBack());
        final boolean lumbar = level.lumbar();
        s.rod(level.at(side * (level.canalWide() / 2 + 0.2), 0.12 * level.high(), back), out, lumbar ? 0.38 : 0.42,
                lumbar ? 0.3 : 0.4);
        if (!lumbar) {
            s.ball(out, 0.46);
        }
    }

    /** The atlas: no body, a ring of bone round the axis's dens and the cord, its lateral masses under the skull. */
    public void atlas(final Sketch s, final double light) {
        final Level l = Level.C1;
        final V3 up = l.frame()[1];
        s.blend(0.4);
        for (final int side : new int[] {1, -1}) {
            s.egg(l.at(side * 1.55, 0, -0.2), 0.75 * light, 0.65, 1.05);
            s.tube(0.36, 0.34, l.at(side * 1.4, 0, 0.55), l.at(side * 0.8, 0, 0.95), l.at(0, 0, 1.08));
            s.tube(0.36, 0.3, l.at(side * 1.6, 0, -1.1), l.at(side * 1.4, 0.05, -2.2), l.at(0, 0.05, -2.65));
            final V3 hole = l.at(side * 2.55, 0, -0.35);
            s.rod(l.at(side * 2.0, 0, -0.3), l.at(side * 3.7, -0.1, -0.5), 0.4, 0.45);
            s.ring(hole, up, 0.35, 0.18);
            s.blend(0.2).carve().rod(hole.plus(up.times(0.6)), hole.minus(up.times(0.6)), 0.2, 0.2);
            s.blend(0.4);
        }
        s.ball(l.at(0, 0, 1.15), 0.38);
        s.ball(l.at(0, 0.05, -2.7), 0.35);
    }

    /** The axis's dens, rising from its body up through the atlas's ring. */
    public void dens(final Sketch s) {
        final Level l = Level.C2;
        final V3[] f = l.frame();
        s.blend(0.5).rod(l.at(0, l.high() * 0.4, 0.1), l.at(0, l.high() / 2 + 1.45, 0.2), 0.55, 0.42);
        s.blend(0.3).ball(f[0].plus(f[1].times(l.high() / 2 + 1.5)).plus(f[2].times(0.2)), 0.42);
    }
}
