package melons.innocent.bay.terrain;

import melons.innocent.terrain.LandText;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Marina Bay's land, as the terrain has it ({@link BayCountry}), written out as a country's text ({@link LandText}),
 * for a terrain bench to draw with {@code -Dbench.map=} the file: to the file {@code args[0]} names, or to
 * {@code marina-bay/bay-terrain/target/marina-bay.land}; credited to OpenStreetMap, as what it is derived from is.
 */
public final class BayExport {

    private BayExport() {
    }

    public static void main(final String[] args) throws IOException {
        final Path to = Path.of(args.length > 0 ? args[0] : "marina-bay/bay-terrain/target/marina-bay.land");
        final String text = "# Marina Bay's land, as the terrain has it: map data (c) OpenStreetMap contributors,"
                + " ODbL.\n# Written by BayExport from the bay's own lines. Metres.\n"
                + LandText.INSTANCE.text(new BayCountry().features());
        Files.createDirectories(to.toAbsolutePath().getParent());
        Files.writeString(to, text.replace("\n", "\r\n"), StandardCharsets.UTF_8);
        System.out.println("Marina Bay's land written to " + to.toAbsolutePath() + ", " + text.length() / 1024
                + " KiB");
    }
}
