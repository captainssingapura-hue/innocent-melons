package com.example.fxsuite.vulkan.qc;

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
 * Keeps the renderer's layers apart, by what each file imports. What is worked out with no card (vk-drawing) touches
 * no card, no JavaFX, no engine and nothing built on it; within it, each package reads only those below it. Only the
 * device kit (vk-device) calls the card, and it knows nothing of terms, solids, benches or JavaFX. A renderer
 * (vk-render) asks the kit and knows no bench. And every package is in a layer: one added without a place here
 * fails, so nothing goes unkept.
 */
class LayersTest {

    private static final String BASE = "com.example.fxsuite.vulkan.";
    private static final Path CODE = Path.of("src", "main", "java", "com", "example", "fxsuite", "vulkan");

    /** Each module's packages, lowest first: a package may read those before it in its module, and none after. */
    private final Map<String, List<String>> layers = Map.of(
            "vk-drawing", List.of("space", "layout", "form", "view", "term", "shade", "worked"),
            "vk-device", List.of("device"),
            "vk-render", List.of("render"),
            "vk-bench", List.of("bench"));

    @Test
    void whatIsWorkedOutWithNoCardTouchesNoCardNoJavaFxAndNoEngine() throws IOException {
        final List<String> wrong = new ArrayList<>();
        for (final String layer : layers.get("vk-drawing")) {
            forbid(wrong, "vk-drawing", layer, "org.lwjgl", "javafx.", "com.example.fxsuite.avatar",
                    BASE + "bench", BASE + "device", BASE + "render");
            // a picture worked out is handed over as an image; nothing below it has any use for one
            if (!layer.equals("worked")) {
                forbid(wrong, "vk-drawing", layer, "java.awt");
            }
        }
        assertEquals(List.of(), wrong);
    }

    @Test
    void withinAModuleEachPackageReadsOnlyThoseBelowIt() throws IOException {
        final List<String> wrong = new ArrayList<>();
        for (final Map.Entry<String, List<String>> module : layers.entrySet()) {
            final List<String> packages = module.getValue();
            for (int at = 0; at < packages.size(); at++) {
                for (final String above : packages.subList(at + 1, packages.size())) {
                    forbid(wrong, module.getKey(), packages.get(at), BASE + above + ".");
                }
            }
        }
        assertEquals(List.of(), wrong);
    }

    @Test
    void onlyTheDeviceKitCallsTheCard() throws IOException {
        final List<String> wrong = new ArrayList<>();
        forbid(wrong, "vk-render", "render", "org.lwjgl");
        forbid(wrong, "vk-bench", "bench", "org.lwjgl");
        assertEquals(List.of(), wrong);
    }

    @Test
    void theDeviceKitKnowsNothingOfTermsSolidsBenchesOrJavaFx() throws IOException {
        final List<String> wrong = new ArrayList<>();
        forbid(wrong, "vk-device", "device", "javafx.", "java.awt", "com.example.fxsuite.avatar", BASE + "term",
                BASE + "worked", BASE + "view", BASE + "render", BASE + "bench");
        assertEquals(List.of(), wrong);
    }

    @Test
    void aRendererKnowsNoBenchAndNoJavaFx() throws IOException {
        final List<String> wrong = new ArrayList<>();
        forbid(wrong, "vk-render", "render", "javafx.", "java.awt", "com.example.fxsuite.avatar", BASE + "bench");
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
