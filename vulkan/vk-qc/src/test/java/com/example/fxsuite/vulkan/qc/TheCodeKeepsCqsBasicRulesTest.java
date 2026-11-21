package com.example.fxsuite.vulkan.qc;

import com.example.fxsuite.vulkan.bench.subject.Watermelon;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.render.Formed;
import com.example.fxsuite.vulkan.space.Vec3;
import com.homing_labs.cq.rules.basic.BasicRules;
import com.homing_labs.cq.scan.Declaration;
import com.homing_labs.cq.scan.Gate;
import com.homing_labs.cq.scan.ScanResult;
import com.homing_labs.cq.scan.ScannedUnit;
import com.homing_labs.cq.scan.Scanner;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The renderer's code against CQ's basic rules, in the build that breaks them: a class within 250 lines and a method
 * within 50, no public fields, no static methods but {@code main}, no static state that changes. Every module's main
 * code, read from its classes, with its sources from beside this module. Tests are not checked.
 */
class TheCodeKeepsCqsBasicRulesTest {

    /** What is checked: the renderer's packages, and where each module keeps its sources. */
    private record Checked() implements Declaration, StatelessFunctionalObject {

        @Override
        public List<String> packages() {
            return List.of("com.example.fxsuite.vulkan");
        }

        @Override
        public List<Path> sources() {
            return List.of(Path.of("..", "vk-drawing", "src", "main", "java").toAbsolutePath().normalize(),
                    Path.of("..", "vk-device", "src", "main", "java").toAbsolutePath().normalize(),
                    Path.of("..", "vk-render", "src", "main", "java").toAbsolutePath().normalize(),
                    Path.of("..", "vk-bench", "src", "main", "java").toAbsolutePath().normalize());
        }
    }

    /** One class of each module the scan must read. */
    private final List<Class<?>> modules = List.of(Vec3.class, Card.class, Formed.class, Watermelon.class);

    private final ScanResult result = Scanner.INSTANCE.scan(new Checked().and(BasicRules.INSTANCE),
            getClass().getClassLoader());

    @Test
    void everyModuleIsRead() {
        final List<String> read = result.units().items().stream().map(scanned -> scanned.unit().type().getName())
                .toList();
        assertEquals(List.of(), modules.stream().map(Class::getName).filter(name -> !read.contains(name)).toList(),
                "a module the scan did not read");
    }

    @Test
    void everyClassIsReadWithItsSource() {
        assertEquals(List.of(), result.units().items().stream()
                .filter(scanned -> scanned.unit().source().isEmpty())
                .map(ScannedUnit::unit).map(unit -> unit.type().getName())
                .toList(), "classes whose source was not found, so their lines went unchecked");
    }

    @Test
    void theCodePassesTheGate() {
        assertEquals(List.of(), Gate.INSTANCE.failures(result));
    }
}
