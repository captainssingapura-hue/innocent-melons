package com.example.fxsuite.life.body.robot;

import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.wear.Plating;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A robot's shell: a plate to every part but its head, whose helmet is its frame's, and its shoulder girdles, which
 * the shoulders' caps cover; on both sides. Each part's plates over its middle, and parted at its joints, so that the
 * frame shows there and the body bends: the hips open for the legs, the waist below the chest, the elbow, the wrist,
 * the knee and the ankle bare, the sole open. Said in each part's frame as the papers' figure has it.
 */
class ShellTest {

    /** For each part, as on the right: a point its plates cover, and points none of them do. */
    private static final Map<Part, List<V3>> COVERED = Map.ofEntries(
            Map.entry(Part.PELVIS, List.of(new V3(0, 0, 8), new V3(12, -20, 0), new V3(0, 13, 0))),
            Map.entry(Part.ABDOMEN, List.of(new V3(0, 5, 9), new V3(0, 13, 9))),
            Map.entry(Part.CHEST, List.of(new V3(0, 10, 10), new V3(0, -3, 10))),
            Map.entry(Part.NECK, List.of(new V3(0, 3, -9), new V3(0, 9, 5))),
            Map.entry(Part.RIGHT_UPPER_ARM, List.of(new V3(0, -18, 0), new V3(0, -30.5, 0))),
            Map.entry(Part.RIGHT_FOREARM, List.of(new V3(0, -12, 0), new V3(0, 0, 0), new V3(0, -24.5, 0))),
            Map.entry(Part.RIGHT_HAND, List.of(new V3(0, -10, 2), new V3(0, 1.5, 0))),
            Map.entry(Part.RIGHT_THIGH, List.of(new V3(0, -20, 0), new V3(0, -40.5, 0))),
            Map.entry(Part.RIGHT_SHIN, List.of(new V3(0, -20, 0), new V3(0, 0, 0), new V3(0, -40.5, 0))),
            Map.entry(Part.RIGHT_FOOT, List.of(new V3(0, -3, 6), new V3(0, -6, 6))),
            Map.entry(Part.RIGHT_TOES, List.of(new V3(0, 1.5, 3), new V3(0, -0.5, 3))));

    private final Rig bind = new Rig(Proportions.MODEL, BoundPose.INSTANCE.pose(), false);
    private final List<Plating.Cut> cuts = Shell.INSTANCE.cuts(bind);

    @Test
    void everyPartButTheHeadAndTheGirdlesHasAPlateOnBothSides() {
        final Set<Part> plated = cuts.stream().map(Plating.Cut::part).collect(Collectors.toSet());
        final Set<Part> all = EnumSet.allOf(Part.class);
        all.removeAll(List.of(Part.HEAD, Part.RIGHT_GIRDLE, Part.LEFT_GIRDLE));
        assertEquals(all, plated);
    }

    @Test
    void eachPartsPlatesCoverItsMiddleAndLeaveItsJointsBareOnBothSides() {
        for (final Part p : Part.values()) {
            final List<V3> at = COVERED.get(p.side() < 0 ? p.other() : p);
            if (at == null) {
                continue;
            }
            final Rig.Frame f = bind.frame(p);
            final List<Plating.Cut> mine = cuts.stream().filter(c -> c.part() == p).toList();
            assertTrue(mine.stream().anyMatch(c -> kept(c, f.place(at.getFirst()))), p.said() + " covered");
            for (final V3 bare : at.subList(1, at.size())) {
                assertFalse(mine.stream().anyMatch(c -> kept(c, f.place(bare))), p.said() + " bare at " + bare);
            }
        }
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
