package melons.innocent.terrain.melon;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import melons.innocent.terrain.Topography;
import melons.innocent.terrain.TopographyText;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * The Melon country's natural ground as a topographic map: its contours, spot heights and break lines, read from the
 * map's text, {@code melon-country.topo}, beside this. The plain level at 12 m; Melon Bay shelving to 28 m and more;
 * the Melon Downs to the west, 97 m; the Melon Range across the north-east, Mount Melon its highest peak, 760 m, the
 * west ridge over the river's valley 640 m; the Melon River's thalweg down its valley, over the plain and out into
 * the bay. No harbour: Melon Harbor's deep is dredged, a made work laid over this, not part of it. Read afresh each
 * time it is asked for.
 */
public record MelonTopography() implements StatelessFunctionalObject {

    public static final MelonTopography INSTANCE = new MelonTopography();

    /** The map, read. */
    public Topography topography() {
        try (InputStream in = MelonTopography.class.getResourceAsStream("melon-country.topo")) {
            if (in == null) {
                throw new IllegalStateException("the Melon country's map, melon-country.topo, is missing");
            }
            return TopographyText.INSTANCE.parse(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
