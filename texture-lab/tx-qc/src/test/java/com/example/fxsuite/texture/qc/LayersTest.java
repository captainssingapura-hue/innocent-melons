package com.example.fxsuite.texture.qc;

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
 * Keeps the lab's layers apart, by what each file imports. A texture is a description: it touches no card, no JavaFX,
 * no engine, and nothing of the Vulkan renderer but what is worked out with no card (its terms, forms and Java's
 * working of a picture). A texture does not know the bench it is looked at on. And every package is in a layer: one
 * added without a place here fails.
 */
class LayersTest {

    private static final Path CODE = Path.of("src", "main", "java", "com", "example", "fxsuite", "texture");

    /** Each module's packages. */
    private final Map<String, List<String>> layers = Map.of(
            "tx-brick", List.of("brick"),
            "tx-stone", List.of("stone"),
            "tx-bench", List.of("bench"));

    @Test
    void aTextureTouchesNoCardNoJavaFxNoEngineAndNoBench() throws IOException {
        final List<String> wrong = new ArrayList<>();
        for (final String[] texture : new String[][] {{"tx-brick", "brick"}, {"tx-stone", "stone"}}) {
            forbid(wrong, texture[0], texture[1], "org.lwjgl", "javafx.", "java.awt", "com.example.fxsuite.avatar",
                    "com.example.fxsuite.vulkan.bench", "com.example.fxsuite.vulkan.device",
                    "com.example.fxsuite.vulkan.worked", "com.example.fxsuite.texture.bench");
        }
        // bricks know nothing of the stone they may stand on
        forbid(wrong, "tx-brick", "brick", "com.example.fxsuite.texture.stone");
        assertEquals(List.of(), wrong);
    }

    @Test
    void theBenchTouchesNoCardAndNoEngine() throws IOException {
        final List<String> wrong = new ArrayList<>();
        forbid(wrong, "tx-bench", "bench", "org.lwjgl", "com.example.fxsuite.avatar",
                "com.example.fxsuite.vulkan.bench", "com.example.fxsuite.vulkan.device");
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
