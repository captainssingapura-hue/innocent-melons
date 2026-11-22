package com.example.fxsuite.texture.qc;

import com.example.fxsuite.texture.bench.Swatch;
import com.example.fxsuite.texture.brick.BrickWall;
import com.example.fxsuite.texture.stone.RubbleWall;
import com.example.fxsuite.texture.lab.Stand;
import com.example.fxsuite.texture.materials.Shades;
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
 * The lab's code against CQ's basic rules, in the build that breaks them: a class within 250 lines and a method
 * within 50, no public fields, no static methods but {@code main}, no static state that changes. Every module's main
 * code, read from its classes, with its sources from beside this module. Tests are not checked.
 */
class TheCodeKeepsCqsBasicRulesTest {

    /** What is checked: the lab's packages, and where each module keeps its sources. */
    private record Checked() implements Declaration, StatelessFunctionalObject {

        @Override
        public List<String> packages() {
            return List.of("com.example.fxsuite.texture");
        }

        @Override
        public List<Path> sources() {
            return List.of(Path.of("..", "tx-brick", "src", "main", "java").toAbsolutePath().normalize(),
                    Path.of("..", "tx-stone", "src", "main", "java").toAbsolutePath().normalize(),
                    Path.of("..", "tx-bench", "src", "main", "java").toAbsolutePath().normalize(),
                    Path.of("..", "tx-materials", "src", "main", "java").toAbsolutePath().normalize(),
                    Path.of("..", "tx-lab", "src", "main", "java").toAbsolutePath().normalize());
        }
    }

    /** One class of each module the scan must read. */
    private final List<Class<?>> modules = List.of(BrickWall.class, RubbleWall.class, Swatch.class, Shades.class,
            Stand.class);

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
