package melons.innocent.building.bench;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.existence.Made;
import melons.innocent.forms.Facets;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.RecordComponent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * What the bench offers: each thing a leaf of the taxonomy of what is made, built from its sample with nothing wrong,
 * drawn as faces; each sample made again from its own fields, as the bench's form makes it; and the bench reaching
 * into the game engine for nothing but the plain data its buildings are made of.
 */
class CatalogueTest {

    private final Catalogue catalogue = new Catalogue();

    @Test
    void eachALeafOfWhatIsMadeBuiltWithNothingWrongDrawn() {
        assertEquals(11, catalogue.offered().size());
        for (final Offered offered : catalogue.offered()) {
            assertTrue(Made.TREE.has(offered.thing()), offered.title() + " in the taxonomy of what is made");
            final Structure built = offered.built(offered.sample());
            assertEquals(List.of(), built.problems(), offered.title());
            assertTrue(new Facets(built.solids(), 10).triangles() > 0, offered.title() + " drawn");
            assertEquals(offered, catalogue.of(offered.thing()).orElseThrow());
        }
    }

    @Test
    void eachSampleMadeAgainFromItsOwnFields() throws ReflectiveOperationException {
        for (final Offered offered : catalogue.offered()) {
            final Spec<?, ?, ?> sample = offered.sample();
            final RecordComponent[] components = sample.getClass().getRecordComponents();
            final Class<?>[] types = new Class<?>[components.length];
            final Object[] values = new Object[components.length];
            for (int i = 0; i < components.length; i++) {
                types[i] = components[i].getType();
                values[i] = components[i].getAccessor().invoke(sample);
            }
            assertEquals(sample, sample.getClass().getDeclaredConstructor(types).newInstance(values));
        }
    }

    @Test
    void nothingOfTheGameEngineButThePlainData() throws IOException {
        final List<String> allowed = List.of("Solid", "Point", "Role", "Material");
        try (Stream<Path> sources = Files.walk(Path.of("src/main/java"))) {
            for (final Path source : sources.filter(p -> p.toString().endsWith(".java")).toList()) {
                for (final String line : Files.readAllLines(source)) {
                    if (line.startsWith("import com.example.fxsuite.avatar.")) {
                        final String name = line.substring(line.lastIndexOf('.') + 1, line.length() - 1);
                        assertTrue(line.startsWith("import com.example.fxsuite.avatar.build.")
                                && allowed.contains(name), source.getFileName() + ": " + line);
                    }
                }
            }
        }
    }
}
