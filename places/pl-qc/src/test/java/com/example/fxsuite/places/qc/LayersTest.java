package com.example.fxsuite.places.qc;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Keeps the places in their place, by what each file imports. A place is drawn by the card through the renderer
 * and never by calling the card's own library; it has no window of JavaFX's of its own; and it takes its materials
 * from the lab, not from the shared trees of what exists, where there are none. And every package is in a layer:
 * one added without a place here fails.
 */
class LayersTest {

    private static final Path CODE = Path.of("src", "main", "java", "com", "example", "fxsuite", "places");

    /** Each module's packages. */
    private final Map<String, List<String>> layers = Map.of("pl-yard", List.of("yard"));

    @Test
    void aPlaceCallsNoCardOfItsOwnOpensNoWindowOfJavaFxsAndTakesItsMaterialsFromTheLab() throws IOException {
        final List<String> wrong = new ArrayList<>();
        forbid(wrong, "pl-yard", "yard", "org.lwjgl", "javafx.", "java.awt", "com.example.fxsuite.existence");
        assertEquals(List.of(), wrong);
    }

    @Test
    void everyPackageIsInALayer() throws IOException {
        for (final Map.Entry<String, List<String>> module : layers.entrySet()) {
            final Set<String> found = new TreeSet<>();
            try (Stream<Path> packages = Files.list(Path.of("..", module.getKey()).resolve(CODE))) {
                packages.filter(Files::isDirectory).forEach(folder -> found.add(folder.getFileName().toString()));
            }
            assertEquals(new TreeSet<>(module.getValue()), found, module.getKey() + "'s packages");
        }
    }

    /** Every import in {@code module}'s {@code layer}, or under it, that begins with one of {@code prefixes}. */
    private void forbid(final List<String> wrong, final String module, final String layer, final String... prefixes)
            throws IOException {
        try (Stream<Path> files = Files.walk(Path.of("..", module).resolve(CODE).resolve(layer))) {
            for (final Path file : files.filter(each -> each.toString().endsWith(".java")).toList()) {
                for (final String line : Files.readAllLines(file)) {
                    for (final String prefix : prefixes) {
                        if (line.startsWith("import " + prefix) || line.startsWith("import static " + prefix)) {
                            wrong.add(module + "/" + layer + "/" + file.getFileName() + ": " + line.trim());
                        }
                    }
                }
            }
        }
    }
}
