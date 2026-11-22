package com.example.fxsuite.vulkan.qc;

import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.vulkan.EXTDebugUtils;
import org.lwjgl.vulkan.KHRAccelerationStructure;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.VK13;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The device kit's own rules, kept by reading its source and its classes. No answer of the card's is dropped: every
 * call to Vulkan that answers is made inside the one place answers are checked, under its own name. And no bare
 * handle leaves the kit: nothing the kit shows to others takes or gives back a bare number where a handle would be,
 * or anything of LWJGL's.
 */
class TheDeviceKitKeepsItsRulesTest {

    private static final String KIT = "com.example.fxsuite.vulkan.device";
    private static final Path SOURCE = Path.of("..", "vk-device", "src", "main", "java", "com", "example", "fxsuite",
            "vulkan", "device");
    /** A call checked: the name it is checked under, and the call itself. */
    private static final String CHECKED = "new Checked\\(\"(\\w+)\"\\)\\s*\\.(?:of|allowing)\\((\\w+)\\(";
    /** Any call to Vulkan, or to what makes a window's surface for it. */
    private static final String CALL = "\\b((?:vk|glfw)[A-Z]\\w*)\\(";

    /** Where Vulkan's calls are declared, as LWJGL has them. */
    private final List<Class<?>> vulkan = List.of(VK13.class, KHRSurface.class, KHRSwapchain.class,
            EXTDebugUtils.class, GLFWVulkan.class, KHRAccelerationStructure.class);

    @Test
    void noAnswerOfTheCardsIsDropped() throws IOException {
        final List<String> wrong = new ArrayList<>();
        int checked = 0;
        for (final Path file : sources()) {
            final String code = Files.readString(file);
            final Set<Integer> kept = new HashSet<>();
            final Matcher inside = Pattern.compile(CHECKED).matcher(code);
            while (inside.find()) {
                kept.add(inside.start(2));
                checked++;
                if (!inside.group(1).equals(inside.group(2))) {
                    wrong.add(file.getFileName() + ": " + inside.group(2) + " is checked as " + inside.group(1));
                }
            }
            final Matcher call = Pattern.compile(CALL).matcher(code);
            while (call.find()) {
                if (answers(call.group(1)) && !kept.contains(call.start(1))) {
                    wrong.add(file.getFileName() + ": " + call.group(1) + " answers, and its answer is not checked");
                }
            }
        }
        assertEquals(List.of(), wrong);
        assertTrue(checked > 20, "the kit's checked calls were found: " + checked);
    }

    @Test
    void noBareHandleAndNothingOfLwjglsLeavesTheKit() throws IOException, ClassNotFoundException {
        final List<String> wrong = new ArrayList<>();
        int shown = 0;
        for (final Path file : sources()) {
            final Class<?> type = Class.forName(KIT + "." + file.getFileName().toString().replace(".java", ""));
            if (!Modifier.isPublic(type.getModifiers())) {
                continue;
            }
            shown++;
            final List<Executable> offered = new ArrayList<>(List.of(type.getDeclaredConstructors()));
            offered.addAll(List.of(type.getDeclaredMethods()));
            for (final Executable one : offered) {
                final int access = one.getModifiers();
                if (one.isSynthetic() || !(Modifier.isPublic(access) || Modifier.isProtected(access))) {
                    continue;
                }
                final List<Class<?>> types = new ArrayList<>(Arrays.asList(one.getParameterTypes()));
                if (one instanceof Method method) {
                    types.add(method.getReturnType());
                } else if (one instanceof Constructor<?>) {
                    types.add(void.class);
                }
                for (final Class<?> used : types) {
                    if (used == long.class || used == long[].class || used.getName().startsWith("org.lwjgl")) {
                        wrong.add(type.getSimpleName() + "." + one.getName() + " shows " + used.getName());
                    }
                }
            }
        }
        assertEquals(List.of(), wrong);
        assertTrue(shown >= 5, "the kit's public classes were found: " + shown);
    }

    /** Whether any call of that name gives back an answer to be checked: a whole number, as Vulkan's answers are. */
    private boolean answers(final String name) {
        for (final Class<?> where : vulkan) {
            for (final Method method : where.getMethods()) {
                if (method.getName().equals(name) && method.getReturnType() == int.class) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Path> sources() throws IOException {
        try (Stream<Path> files = Files.list(SOURCE)) {
            return files.filter(file -> file.toString().endsWith(".java")).sorted().toList();
        }
    }
}
