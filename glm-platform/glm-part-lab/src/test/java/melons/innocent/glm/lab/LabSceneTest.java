package melons.innocent.glm.lab;

import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Assembled;
import melons.innocent.glm.core.Assembly;
import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.core.Side;
import melons.innocent.glm.model.Finishes;
import melons.innocent.glm.model.GlmPlatform;
import melons.innocent.glm.model.MadeOf;
import melons.innocent.vehiclelab.forms.Coat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The part lab's scene: a part alone, in its own frame, lifted so the grid's floor is on the ground, its points where
 * its own frame has them; put together, every part where it stands, the rest as glass, finished or left out, the grid's
 * floor the ground; ruled fine, the grid round the part alone; and a grid too wide to rule fine ruled no finer than it
 * can be.
 */
class LabSceneTest {

    private final Assembled built = new Assembly(new GlmPlatform().platform()).assembled();
    private final PartAt link = built.placed().keySet().stream()
            .filter(p -> p.part().name().equals("toe link") && p.side() == Side.RIGHT).findFirst().orElseThrow();
    private final Finishes finishes = new Finishes();

    @Test
    void aPartAloneLiftedSoTheGridsFloorIsOnTheGround() {
        final LabScene.Settled alone = new LabScene(built, new LabShow(link), finishes).settled();
        assertEquals(1, alone.placements().size());
        assertEquals(new Vec3(0, 0, 0), alone.placements().getFirst().at(), "in its own frame");
        assertEquals(0, alone.grid().corner(true).y() + alone.lift().y(), 1e-9, "the grid's floor on the ground");
        assertEquals(link.part().points().getFirst().at(), alone.points().getFirst().at());
    }

    @Test
    void putTogetherTheRestAsGlassFinishedOrLeftOut() {
        final LabShow ghost = new LabShow(link, LabShow.Level.ASSEMBLY, LabShow.Others.GHOST, true, true, true,
                Spacing.NORMAL, new MadeOf());
        final LabScene.Settled scene = new LabScene(built, ghost, finishes).settled();
        assertEquals(built.placed().size(), scene.placements().size());
        assertTrue(scene.placements().stream().filter(p -> !p.part().equals(link))
                .allMatch(p -> p.coat().kind() == Coat.Kind.GLASS));
        assertEquals(new Vec3(0, 0, 0), scene.lift());
        assertEquals(0, scene.grid().corner(true).y(), 1e-9, "the grid's floor the ground");
        final LabShow hidden = new LabShow(link, LabShow.Level.ASSEMBLY, LabShow.Others.HIDDEN, true, true, true,
                Spacing.NORMAL, new MadeOf());
        assertEquals(1, new LabScene(built, hidden, finishes).settled().placements().size());
    }

    @Test
    void ruledFineRoundThePartAloneAndNoFinerThanItCanBe() {
        final LabShow fine = new LabShow(link, LabShow.Level.ASSEMBLY, LabShow.Others.GHOST, true, true, true,
                Spacing.FINE, new MadeOf());
        final LabShow whole = new LabShow(link, LabShow.Level.ASSEMBLY, LabShow.Others.GHOST, true, true, true,
                Spacing.NORMAL, new MadeOf());
        assertTrue(new LabScene(built, fine, finishes).size() < new LabScene(built, whole, finishes).size());
        assertEquals(1, new LabScene(built, fine, finishes).grid().step(), 1e-9, "a link ruled every centimetre");
        final Grid wide = new Grid(new Vec3(0, 0, 0), new Vec3(1_000, 100, 100), new Vec3(0, 0, 0), Spacing.FINE);
        assertEquals(10, wide.step(), 1e-9, "ten metres ruled no finer than every 10 cm");
    }
}
