package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The guards over the limbs' joints, the thighs and the feet: on both sides, each over what it guards and open where
 * its joint bends, said in its part's frame as the papers' figure has it.
 */
class GuardsTest {

    /** For each part a guard moves with, as on the right: a point it covers, and a point it is open over. */
    private static final Map<Part, List<V3>> GUARDED = Map.of(
            Part.RIGHT_FOREARM, List.of(new V3(-1.2, 1.5, -4.5), new V3(0, -1, 3.5)),
            Part.RIGHT_THIGH, List.of(new V3(0, -20, 8), new V3(0, -20, -7)),
            Part.RIGHT_SHIN, List.of(new V3(0, 2, 5), new V3(0, 0, -5)),
            Part.RIGHT_FOOT, List.of(new V3(0, -1.5, 7), new V3(0, -6, 5)),
            Part.RIGHT_TOES, List.of(new V3(0, 1.5, 3), new V3(0, -0.5, 3)));

    private final Rig bind = new Rig(Proportions.MODEL, BoundPose.INSTANCE.pose(), false);

    @Test
    void eachGuardCoversItsJointAndIsOpenWhereItBends() {
        final List<Plating.Cut> cuts = Guards.INSTANCE.of(bind);
        for (final Plating.Cut cut : cuts) {
            final Part right = cut.part().side() < 0 ? cut.part().other() : cut.part();
            final Rig.Frame f = bind.frame(cut.part());
            final List<V3> at = GUARDED.get(right);
            assertTrue(kept(cut, f.place(at.get(0))), cut.kind().title() + " over " + cut.part().said());
            assertFalse(kept(cut, f.place(at.get(1))), cut.kind().title() + " open on " + cut.part().said());
        }
        final Set<Part> guarded = cuts.stream().map(Plating.Cut::part).collect(Collectors.toSet());
        assertEquals(GUARDED.size() * 2, guarded.size(), "each on both sides");
    }

    /** Whether {@code p} is within {@code cut}'s box and on the side of each of its edges it keeps. */
    private boolean kept(final Plating.Cut cut, final V3 p) {
        final double[] b = cut.box();
        final boolean in = p.x() >= b[0] && p.y() >= b[1] && p.z() >= b[2] && p.x() <= b[3] && p.y() <= b[4]
                && p.z() <= b[5];
        return in && cut.edges().stream().filter(e -> e.op() == Field.Op.CLIP)
                .allMatch(e -> e.shape().at(p.x(), p.y(), p.z()) <= 0);
    }
}
