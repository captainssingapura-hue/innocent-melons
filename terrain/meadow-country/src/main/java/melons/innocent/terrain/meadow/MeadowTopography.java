package melons.innocent.terrain.meadow;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import melons.innocent.terrain.Topography;
import melons.innocent.terrain.TopographyText;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * The meadow's ground as a topographic map, read from its text, {@code meadow.topo}, beside this: twenty kilometres
 * along its river's valley, fifteen west of the village and five east, and ten across it. The valley's floor level at
 * the river; the land rising gently northward, past a knoll and a hollow to a beacon hill, and a small hill north-west
 * of the village, its top level for a church; southward more steeply, past a pair of hills; and up to the moors at
 * 100 m round the edge. The river's channel cut into the floor: its banks' tops level with it 8.5 m either side of
 * the river's middle, its bed level 1.5 m down. Its loops and spot heights as avatar-demo's meadow draws them, its
 * fine roughness left out. Read afresh each time it is asked for.
 */
public record MeadowTopography() implements StatelessFunctionalObject {

    public static final MeadowTopography INSTANCE = new MeadowTopography();

    /** The map, read. */
    public Topography topography() {
        try (InputStream in = MeadowTopography.class.getResourceAsStream("meadow.topo")) {
            if (in == null) {
                throw new IllegalStateException("the meadow's map, meadow.topo, is missing");
            }
            return TopographyText.INSTANCE.parse(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
