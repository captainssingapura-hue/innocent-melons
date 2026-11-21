package com.example.fxsuite.vulkan.term;

import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * One expression, written out as the shader's code and worked out by Java: the code a line for each thing done, the
 * same thing written once however often it is used; what is of constants alone worked out before anything is
 * written; and a thing done to the wrong shapes refused where it is written.
 */
class WritingTest {

    private final Seen seen = new Seen();

    @Test
    void eachThingDoneIsALineAndTheSameThingDoneTwiceIsWrittenOnce() {
        final Real out = seen.at().up().times(seen.at().up()).plus(seen.at().along().times(seen.at().along())).root();
        final Writing writing = new Writing();
        final String name = writing.named(out.plus(out).node());
        assertEquals(List.of(
                "float t0 = at.y;",
                "float t1 = (t0 * t0);",
                "float t2 = at.z;",
                "float t3 = (t2 * t2);",
                "float t4 = (t1 + t3);",
                "float t5 = sqrt(t4);",
                "float t6 = (t5 + t5);"), writing.lines());
        assertEquals("t6", name);
    }

    @Test
    void javaWorksOutWhatTheCodeSays() {
        final Real out = seen.at().up().times(seen.at().up()).plus(seen.at().along().times(seen.at().along())).root();
        assertEquals(5, out.node().worked(seen.sample(new Vec3(9, 3, 4), 0))[0], 1e-12);
    }

    @Test
    void aConstantIsWrittenAsASingleHasItAndAColourAsThreeNumbers() {
        final Writing writing = new Writing();
        assertEquals("0.33", writing.named(new Real(0.33).node()));
        assertEquals("1.0E-4", writing.named(new Real(1e-4).node()));
        assertEquals("vec3(1.0, 0.5, 0.0)", writing.named(new Colour(1, 0.5, 0).node()));
        assertEquals(List.of(), writing.lines(), "nothing done, so no lines");
    }

    @Test
    void whatIsOfConstantsAloneIsWorkedOutBeforeAnythingIsWritten() {
        final Colour mixed = new Colour("#000000").mixed(new Colour("#ffffff"), new Real(0.25));
        final Constant folded = assertInstanceOf(Constant.class, mixed.node().folded());
        assertEquals(List.of(0.25, 0.25, 0.25), folded.numbers());
        final Node kept = seen.covers().times(new Real(2).times(3)).node().folded();
        assertEquals("(covers * 6.0)", kept.written(new Writing()), "the constants worked out, what is given kept");
    }

    @Test
    void aThingDoneToTheWrongShapesIsRefusedWhereItIsWritten() {
        assertThrows(IllegalArgumentException.class,
                () -> new Applied(Pairwise.ANGLE, List.of(seen.at().node(), seen.covers().node())));
        assertThrows(IllegalArgumentException.class, () -> new Real(seen.at().node()));
        assertThrows(IllegalArgumentException.class, () -> new Colour("green"));
    }

    @Test
    void askingForWhatWasNotGivenSaysWhat() {
        final IllegalStateException refused = assertThrows(IllegalStateException.class,
                () -> seen.covers().node().worked(new Sample()));
        assertEquals("nothing was given as covers", refused.getMessage());
    }
}
