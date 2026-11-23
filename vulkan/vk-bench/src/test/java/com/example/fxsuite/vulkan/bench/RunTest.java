package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.bench.play.Keeping;
import com.example.fxsuite.vulkan.bench.play.Played;
import com.example.fxsuite.vulkan.bench.play.Replayed;
import com.example.fxsuite.vulkan.bench.play.Scripted;
import com.example.fxsuite.vulkan.bench.play.Source;
import com.example.fxsuite.vulkan.bench.play.Step;
import com.example.fxsuite.vulkan.view.Camera;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A walk's run, with no card and no window. A script run, kept, and replayed is the same run, step for step, the
 * avatar and the chase camera alike, and keeping the replay keeps the same file; the replay drawn at another rate
 * of frames is the same run still. A kept run that has been tampered with stops, saying the step it went astray at.
 * Someone playing has each frame's turn shared evenly among its steps and what was done once told to the first. A
 * held key is one line of the kept run, not one a step. And a file that is not a kept run is refused.
 */
class RunTest {

    /** Walk ahead, turn while walking, take off, climb, fly on tilting the view down, swing the view, and back. */
    private static final String SCRIPT = "W:2;W+@60:1.5;F;SPACE:1;W+^-15:1;E:0.5;C";

    @TempDir
    Path folder;

    @Test
    void aScriptKeptAndReplayedIsTheSameRunAndKeepingTheReplayKeepsTheSameFile() throws IOException {
        final Path first = folder.resolve("first.walk");
        final List<String> scripted = run(new Scripted(SCRIPT), first, 1.0 / 60);
        final Path again = folder.resolve("again.walk");
        final List<String> replayed = run(new Replayed(first), again, 1.0 / 60);
        assertEquals(scripted, replayed, "the avatar and the camera, frame for frame");
        assertEquals(Files.readAllLines(first), Files.readAllLines(again));
        assertTrue(Files.readAllLines(first).stream().filter(line -> line.startsWith("check")).count() >= 6,
                "a check every second of the run");
        assertEquals(run(new Replayed(first), folder.resolve("slow.walk"), 1.0 / 60).getLast(),
                run(new Replayed(first), folder.resolve("fast.walk"), 1.0 / 144).getLast(),
                "drawn at another rate, the run ends where it ended");
    }

    @Test
    void aKeptRunThatHasBeenTamperedWithStopsSayingWhereItWentAstray() throws IOException {
        final Path kept = folder.resolve("kept.walk");
        run(new Scripted(SCRIPT), kept, 1.0 / 60);
        final List<String> lines = new ArrayList<>(Files.readAllLines(kept));
        final int turn = lines.indexOf(lines.stream().filter(line -> line.contains("turn=0.33")).findFirst()
                .orElseThrow());
        lines.set(turn, lines.get(turn).replace("turn=0.33", "turn=0.34"));
        Files.write(kept, lines);
        final IllegalStateException astray = assertThrows(IllegalStateException.class,
                () -> run(new Replayed(kept), folder.resolve("astray.walk"), 1.0 / 60));
        // the first check after the turn was tampered with: the next whole second of the run
        final long tampered = Long.parseLong(lines.get(turn).split(" ")[0]);
        final long checked = (tampered / 120 + 1) * 120;
        assertTrue(astray.getMessage().contains("gone astray at step " + checked + ":"), astray.getMessage());
    }

    @Test
    void someonePlayingHasAFramesTurnSharedAmongItsStepsAndWhatWasDoneOnceToldToTheFirst() {
        final Played played = new Played();
        played.told(Set.of("W", "E"), List.of("SPACE", "F", "C"), 3, -1.5, 2);
        final List<Step> steps = List.of(played.step(1, 3), played.step(2, 2), played.step(3, 1));
        assertEquals(3, steps.stream().mapToDouble(Step::turn).sum(), 1e-12, "the whole turn, and no more");
        assertEquals(1, steps.get(0).turn(), 1e-12);
        assertEquals(-0.5, steps.get(2).tilt(), 1e-12);
        assertEquals(List.of("SPACE", "C"), steps.get(0).pressed(), "F is taken by the jets, not by the engine");
        assertTrue(steps.get(0).flies() && steps.get(0).behind() && steps.get(0).scrolled() == 2);
        assertTrue(steps.get(1).pressed().isEmpty() && !steps.get(1).flies() && steps.get(1).scrolled() == 0);
        assertEquals(1, steps.get(2).circling(), "E held swings the view");
        assertEquals(Set.of("W", "E"), steps.get(2).held());
    }

    @Test
    void aHeldKeyIsOneLineAndAFileThatIsNotAKeptRunIsRefused() throws IOException {
        final Path kept = folder.resolve("held.walk");
        run(new Scripted("W:5"), kept, 1.0 / 30);
        assertEquals(1, Files.readAllLines(kept).stream().filter(line -> line.contains("held=W")).count());
        final Path other = folder.resolve("other.walk");
        Files.writeString(other, "avatar-inputs 1\n");
        assertThrows(IllegalArgumentException.class, () -> new Replayed(other));
        Files.writeString(other, "walk-inputs 1\nplace somewhere\n1 held= pressed= turn=x\nend 2\n");
        final IllegalArgumentException unread = assertThrows(IllegalArgumentException.class,
                () -> new Replayed(other));
        assertTrue(unread.getMessage().contains("line 3"), unread.getMessage());
        assertNotEquals(new Step(), new Step(Set.of("W"), List.of(), 0, 0, false, 0, 0, false));
    }

    /**
     * {@code source} run to its end, a frame every {@code frame} seconds, kept in {@code kept}, among two posts;
     * where the avatar and the chase camera's eye were at each frame, said in a line, and last where they ended.
     */
    private List<String> run(final Source source, final Path kept, final double frame) {
        final List<String> seen = new ArrayList<>();
        try (Keeping keeping = new Keeping(kept, "a test")) {
            final Run run = new Run(source, Optional.of(keeping), new Skies(new Asked(1)));
            run.inTheWay(new Obstacles(List.of(new Block(300, 900, 60, 60, 0, 0, 400),
                    new Block(-200, 1400, 80, 80, 0, 0, 300))));
            while (!run.over()) {
                final double blend = run.stepped(frame);
                final Camera camera = run.following().camera(run.inTheWay(), blend);
                seen.add(run.shown(blend).body().position() + " " + camera.eye());
            }
            if (!seen.isEmpty()) {
                seen.add("ended " + run.shown(1).body().position() + " "
                        + run.following().camera(run.inTheWay(), 1).eye());
            }
        }
        return seen;
    }
}
