package com.example.fxsuite.vulkan.term;

import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * An expression made ready and worked out step by step comes to exactly what it comes to worked out as it is
 * written, at every sample: stripes, a scatter and a colour mixed by both, each used more than once.
 */
class ReckoningTest {

    private final Seen seen = new Seen();

    @Test
    void madeReadyItComesToExactlyWhatItComesToAsWritten() {
        final Real stripes = new Turns(seen.at().across().over(7.5), new Real(1 / 7.5)).pulse(0.2, seen.covers());
        final Real fired = new Scatter(seen.at().across().over(7.5).floored(), seen.at().up().over(3).floored(), 1, 3)
                .settling(seen.covers());
        final Colour colour = new Colour("#ac5438").mixed(new Colour("#8a3c2a"), fired)
                .mixed(new Colour("#b8b0a2"), stripes).scaled(stripes.plus(fired));
        final Reckoning ready = new Reckoning(colour.node());
        for (final double[] at : new double[][] {{0.3, 0.1, 0.0}, {-41.2, 17.9, 0.4}, {7.5, 3, 2}, {123.4, -8, 30}}) {
            final Sample sample = seen.sample(new Vec3(at[0], at[1], 0), at[2]);
            assertArrayEquals(colour.node().worked(sample), ready.at(sample), 0, "at " + at[0] + ", " + at[1]);
        }
    }

    @Test
    void aConstantAndWhatIsGivenAreMadeReadyAsTheyAre() {
        assertArrayEquals(new double[] {0.25}, new Reckoning(new Real(0.25).node()).at(new Sample()), 0);
        assertArrayEquals(new double[] {6}, new Reckoning(seen.covers().node()).at(seen.sample(new Vec3(0, 0, 0), 6)),
                0);
    }
}
