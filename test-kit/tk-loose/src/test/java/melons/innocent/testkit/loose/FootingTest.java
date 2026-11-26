package melons.innocent.testkit.loose;

import com.example.fxsuite.texture.materials.Materials;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.surface.Patch;
import melons.innocent.physics.existences.Conditions;
import melons.innocent.physics.existences.Surfaces;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What is underfoot in the trial, asked as any mover asks it: the ground at the walk's beginning, the ramp where
 * one stands on it, in the state it has been put in, and the wagon's deck, which answers the same once it has gone.
 */
class FootingTest {

    private final Trial trial = new Trial(-1, 12);
    private final Footing footing = new Footing(trial);

    private void ticked(final int ticks) {
        for (int tick = 0; tick < ticks; tick++) {
            trial.ticked();
        }
    }

    @Test
    void atTheWalksBeginningTheGroundIsUnderfootLevelTurfAndDry() {
        ticked(60);
        final Patch patch = footing.under(new Spot(0, 0, 0)).orElseThrow().patch();
        assertEquals("the ground", patch.provider());
        assertEquals(new Surfaces.Level(), patch.form());
        assertEquals(new Materials.Turf(), patch.material());
        assertEquals("Underfoot: the ground; level, turf, dry; no edge; a walker goes on it: yes",
                footing.said(Optional.of(new Spot(0, 0, 0))));
        assertEquals("Underfoot: nothing within reach", footing.said(Optional.of(new Spot(0, 500, 0))));
        assertEquals("Underfoot: nothing within reach", footing.said(Optional.empty()));
    }

    @Test
    void onTheRampItIsASlopeOfConcreteInWhateverStateItHasBeenPutIn() {
        ticked(60);
        // over the ramp's middle, on its top: half way up its five metres at twelve degrees
        final Spot feet = new Spot(1_600 + 100, 250 * Math.sin(Math.toRadians(12)) + 1, 1_200
                + 250 * Math.cos(Math.toRadians(12)));
        final Patch patch = footing.under(feet).orElseThrow().patch();
        assertEquals("the ramp", patch.provider());
        assertEquals(new Surfaces.Slope(), patch.form());
        assertEquals(12, patch.steep(), 1e-6);
        assertEquals(new Materials.Concrete(), patch.material());
        assertEquals(new Conditions.Dry(), patch.condition());
        assertEquals(50, patch.within(), 0.5, "half a metre in from its side");
        footing.conditioned(new Conditions.Icy());
        assertEquals("Underfoot: the ramp; a slope of 12 degrees, concrete, icy; 50 cm to its edge; "
                + "a walker goes on it: to a grade", footing.said(Optional.of(feet)));
        assertTrue(footing.said(Optional.of(new Spot(0, 0, 0))).contains("turf, dry"), "the ground is as it was");
    }

    @Test
    void theWagonsDeckAnswersTheSameOnceItHasGoneAndOnlyItsFrameHasMoved() {
        ticked(60);
        final Spot onDeck = new Spot(500, 51, 100);
        final Spot was = trial.world().placed("the wagon").orElseThrow().at();
        final Patch before = footing.under(was.plus(onDeck)).orElseThrow().patch();
        assertEquals("the wagon", before.provider());
        assertEquals(new Surfaces.Level(), before.form());
        assertEquals(50, before.within(), 1e-6, "half a metre in from the deck's side");
        trial.driven(36);
        ticked(240);
        final Spot now = trial.world().placed("the wagon").orElseThrow().at();
        assertTrue(now.across() - was.across() > 1_900, "two seconds on, some twenty metres along");
        assertEquals(before, footing.under(now.plus(onDeck)).orElseThrow().patch(), "the same, to one who went with it");
    }
}
