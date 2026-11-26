package melons.innocent.core.surface;

import com.example.fxsuite.taxonomy.Node;
import melons.innocent.core.motion.Spot;
import melons.innocent.core.motion.Turn;
import melons.innocent.core.rest.Ground;
import melons.innocent.core.rest.Placed;
import melons.innocent.core.rest.Rigid;
import melons.innocent.core.rest.Shell;
import melons.innocent.core.rest.Simulator;
import melons.innocent.core.rest.World;
import melons.innocent.core.sheet.Arched;
import melons.innocent.core.sheet.Banked;
import melons.innocent.core.sheet.Round;
import melons.innocent.core.sheet.Straight;
import melons.innocent.core.sheet.Swept;
import melons.innocent.physics.existences.Conditions;
import melons.innocent.physics.existences.Shapes;
import melons.innocent.physics.existences.Surfaces;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A surface that curves, asked what is under a spot of it: a spiral ramp standing turned in the world, and a vault.
 * The answers at two spots however near each other differ by as much as the surface truly does between them, with
 * no facet anywhere; a ramp that passes over itself answers with the turn nearest under the spot.
 */
class CurvedTest {

    /** Where the ramp's middle stands, and how far it is turned about the upright, in degrees. */
    private static final Spot STANDS = new Spot(3_000, 0, -1_000);
    private static final double TURNED = 40;
    private static final double RADIUS = 900;
    private static final double CLIMBS = 360;

    private final Ground ground = new Ground();
    private final Simulator still = (rigid, now, around, seconds) -> new Simulator.Stepped(now, Optional.empty());
    private final World world = new World(ground, still)
            .stood(new Rigid("the spiral ramp", new Shell(new Swept(new Round(new Spot(0, 0, 0), RADIUS, 0, 2,
                    CLIMBS), new Banked(200)), 30), 2_400), new Placed(STANDS, new Turn(new Spot(0, 1, 0),
                    Math.toRadians(TURNED))))
            .stood(new Rigid("the vault", new Shell(new Swept(new Straight(new Spot(0, 0, -700), 0, 1_400),
                    new Arched(500, 220)), 25), 600), new Placed(new Spot(-2_000, 0, 0)));
    private final Facts facts = new Facts() {
        @Override
        public Node material(final String provider, final Spot spot) {
            return new Shapes.Shell();
        }

        @Override
        public Node condition(final String provider, final Spot spot) {
            return new Conditions.Dry();
        }
    };
    private final Underfoot underfoot = new Underfoot(world, ground, facts);

    /**
     * A spot of the world over the ramp: so far round it from where it starts, in radians, so far out from its
     * middle, and so high.
     */
    private Spot over(final double round, final double out, final double high) {
        final Spot own = new Spot(out * Math.sin(round), high, out * Math.cos(round));
        return STANDS.plus(new Turn(new Spot(0, 1, 0), Math.toRadians(TURNED)).turned(own));
    }

    @Test
    void aSpiralRampAnswersAsASlopeAsHighAndAsSteepAsItIsThere() {
        final Underfoot.Found found = underfoot.under(over(1.2, RADIUS, 200), 400).orElseThrow();
        final Patch patch = found.patch();
        assertEquals("the spiral ramp", patch.provider());
        assertEquals(new Surfaces.Slope(), patch.form());
        assertEquals(CLIMBS * 1.2 / (2 * Math.PI), found.at().up(), 1e-9);
        assertEquals(Math.toDegrees(Math.atan(CLIMBS / (2 * Math.PI * RADIUS))), patch.steep(), 1e-9);
        assertEquals(200, patch.within(), 1e-9);
        // it falls back the way it came up, square to the line out from its middle
        final Spot out = over(1.2, RADIUS + 1, 0).minus(over(1.2, RADIUS, 0));
        assertEquals(0, found.frame().turned().turned(patch.falls()).dot(out), 1e-9);
    }

    @Test
    void whereItPassesOverItselfTheTurnNearestUnderTheSpotAnswers() {
        final double first = CLIMBS * 1.2 / (2 * Math.PI);
        assertEquals(first, underfoot.under(over(1.2, RADIUS, first + 150), 400).orElseThrow().at().up(), 1e-9);
        assertEquals(first + CLIMBS, underfoot.under(over(1.2, RADIUS, first + CLIMBS + 150), 400).orElseThrow()
                .at().up(), 1e-9);
        // under its first turn there is only the ground
        assertEquals("the ground", underfoot.under(over(1.2, RADIUS, first - 40), 400).orElseThrow().patch()
                .provider());
    }

    @Test
    void twoSpotsAHairApartDifferByJustWhatTheSurfaceDoesBetweenThem() {
        // a hundredth of a millimetre on round the ramp, on its middle line
        final double hair = 1e-3 / RADIUS;
        final Span span = underfoot.between(over(2, RADIUS, 300), over(2 + hair, RADIUS, 300), 400).orElseThrow();
        assertTrue(span.same());
        assertEquals(1e-3, span.run(), 1e-9);
        assertEquals(1e-3 * CLIMBS / (2 * Math.PI * RADIUS), span.rise(), 1e-12);
        assertEquals(CLIMBS / (2 * Math.PI * RADIUS), span.grade(), 1e-6);
    }

    @Test
    void equalStepsRoundTheRampRiseEquallyWithNoFacetAnywhere() {
        // nine thousand steps of about a sixth of a centimetre: on a ramp cut into pieces most would rise nothing
        final double step = 1.0 / 9_000;
        double least = Double.POSITIVE_INFINITY;
        double most = Double.NEGATIVE_INFINITY;
        double climbed = 0;
        for (int k = 0; k < 9_000; k++) {
            final double rise = underfoot.between(over(0.5 + k * step, RADIUS - 120, 200),
                    over(0.5 + (k + 1) * step, RADIUS - 120, 200), 400).orElseThrow().rise();
            least = Math.min(least, rise);
            most = Math.max(most, rise);
            climbed += rise;
        }
        assertEquals(CLIMBS / (2 * Math.PI), climbed, 1e-9);
        assertEquals(least, most, 1e-10);
        assertTrue(least > 0);
    }

    @Test
    void theWayItFacesTurnsEvenlyAlongIt() {
        final double one = underfoot.between(over(1, RADIUS, 300), over(1 + 1.0 / RADIUS, RADIUS, 300), 400)
                .orElseThrow().turned();
        final double half = underfoot.between(over(1, RADIUS, 300), over(1 + 0.5 / RADIUS, RADIUS, 300), 400)
                .orElseThrow().turned();
        assertTrue(one > 0, "it faces another way a centimetre on");
        assertEquals(2, one / half, 1e-3);
    }

    @Test
    void acrossAVaultTheTwoSidesOfAnAxleStandAtDifferentHeights() {
        // an axle 160 cm long lying across the vault, its middle a metre and a half from the crown
        final Span axle = underfoot.between(new Spot(-2_000 + 70, 400, 100), new Spot(-2_000 + 230, 400, 100), 600)
                .orElseThrow();
        final Arched arch = new Arched(500, 220);
        assertEquals(arch.high(230) - arch.high(70), axle.rise(), 1e-9);
        assertEquals(160, axle.run(), 1e-9);
        assertTrue(axle.pitched() < 0 && axle.turned() > 5);
    }

    @Test
    void fromTheGroundOntoTheRampTheDifferenceIsReckonedInTheWorld() {
        final Span span = underfoot.between(over(0.3, RADIUS + 400, 100), over(0.3, RADIUS, 100), 400)
                .orElseThrow();
        assertFalse(span.same());
        assertEquals(CLIMBS * 0.3 / (2 * Math.PI), span.rise(), 1e-9);
        assertEquals(400, span.run(), 1e-9);
    }
}
