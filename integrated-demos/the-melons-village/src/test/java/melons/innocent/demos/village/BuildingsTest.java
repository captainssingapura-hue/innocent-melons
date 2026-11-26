package melons.innocent.demos.village;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.avatar.sim.Prism;
import com.example.fxsuite.avatar.sim.Vec2;
import com.example.fxsuite.avatar.sim.Volume;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The village's buildings: four houses and five blocks of flats, each nothing wrong with it as a thing built; each
 * in the way as its own shape is, set down where it stands; each clear of the others and of everything else of the
 * village, its yards, its houses and its towers; those east of the lane on its far side from the yards; and each come
 * into from the lane's side.
 */
class BuildingsTest {

    private final Village village = new Village(8);

    @Test
    void fourBuildingsEachSoundAndInTheWay() {
        final List<Built> buildings = village.buildings();
        assertEquals(4 + 5, buildings.size(), "four houses and five blocks of flats");
        buildings.forEach(b -> assertEquals(List.of(), b.structure().problems()));
        final Obstacles inTheWay = village.inTheWay();
        final int built = buildings.stream().mapToInt(b -> b.inTheWay().size()).sum();
        assertEquals(built, inTheWay.volumes().size(), "every building's shape in the way");
    }

    @Test
    void eachClearOfTheOthersAndOfTheVillage() {
        final List<double[]> boxes = buildings();
        for (int i = 0; i < boxes.size(); i++) {
            for (int j = i + 1; j < boxes.size(); j++) {
                assertFalse(overlap(boxes.get(i), boxes.get(j)), "building " + i + " clear of " + j);
            }
            for (final Block block : village.inTheWay().blocks()) {
                final double r = Math.hypot(block.halfWidth(), block.halfDepth());
                assertFalse(overlap(boxes.get(i), new double[] {block.x() - r, block.x() + r, block.z() - r,
                        block.z() + r}), "building " + i + " clear of the village at " + block.x() + ", "
                        + block.z());
            }
        }
        assertTrue(boxes.get(0)[0] > 2_000 && boxes.get(1)[0] > 2_000, "east of the lane, past its yards");
    }

    @Test
    void noFloorLaidWhereTheGroundIsSoNoneFlickersOnIt() {
        for (final Built b : village.buildings()) {
            final List<Volume> shape = b.inTheWay();
            for (final Volume v : shape) {
                if (Math.abs(v.high()) < 1) {
                    assertTrue(shape.stream().anyMatch(on -> Math.abs(on.low() - v.high()) < 1e-6
                            && covers(((Prism) on).bounds(), ((Prism) v).bounds())),
                            "a top where the ground is only under what stands on it");
                }
            }
            assertEquals(Built.LIFT, b.placed().clip(new Vec3(0, 0, 0)).shown().y(), 1e-9, "set down over it");
        }
    }

    @Test
    void eachFacingTheLane() {
        for (final Built b : village.buildings()) {
            final Structure.WayIn in = b.structure().waysIn().getFirst();
            final Vec3 at = b.placed().clip(new Vec3(in.across(), 0, in.along())).shown();
            final Vec3 s = b.stands();
            final double toward = -s.x() * (at.x() - s.x()) + (lane(s.z()) - s.z()) * (at.z() - s.z());
            assertTrue(toward > 0, "come into from the lane's side: " + at);
        }
    }

    /** How far along the nearest point of the lane is to {@code z}: the lane runs from the start to the last yards. */
    private double lane(final double z) {
        return Math.clamp(z, 0, 8_000);
    }

    /** Each building's box seen from above: its least and most across, its least and most along. */
    private List<double[]> buildings() {
        final List<double[]> boxes = new ArrayList<>();
        for (final Built b : village.buildings()) {
            final double[] box = {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
                    Double.NEGATIVE_INFINITY};
            for (final Volume v : b.inTheWay()) {
                for (final Vec2 p : ((Prism) v).outline()) {
                    box[0] = Math.min(box[0], p.x());
                    box[1] = Math.max(box[1], p.x());
                    box[2] = Math.min(box[2], p.z());
                    box[3] = Math.max(box[3], p.z());
                }
            }
            boxes.add(box);
        }
        return boxes;
    }

    /** Whether {@code over} covers all of {@code under}, seen from above. */
    private boolean covers(final Volume.Bounds over, final Volume.Bounds under) {
        return over.minX() <= under.minX() + 1e-6 && over.maxX() >= under.maxX() - 1e-6
                && over.minZ() <= under.minZ() + 1e-6 && over.maxZ() >= under.maxZ() - 1e-6;
    }

    private boolean overlap(final double[] a, final double[] b) {
        return a[0] < b[1] && b[0] < a[1] && a[2] < b[3] && b[2] < a[3];
    }
}
