package com.example.fxsuite.life.body.vulkan.lineup;

import com.example.fxsuite.existence.Figures;
import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Detail;
import com.example.fxsuite.life.body.Move;
import com.example.fxsuite.life.body.Paint;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.RobotDesign;
import com.example.fxsuite.life.body.RobotParts;
import com.example.fxsuite.life.body.vulkan.Motion;
import com.example.fxsuite.life.body.vulkan.Moving;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.render.Shadowing;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;
import com.example.fxsuite.vulkan.worked.Daylight;
import com.example.fxsuite.vulkan.worked.Picture;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The line-up's view on the card: two framed robots standing on their feet, by day with shadows, nothing said by the
 * validation layer; the sky in a corner; the robots covering some of the picture, and more of it when one is made
 * taller; drawn too with no shadows. Pictures of each are kept in {@code target/card}.
 */
class LineupViewOnTheCardTest {

    private static final int WIDTH = 480;
    private static final int HEIGHT = 360;

    private final Daylight day = new Daylight();
    private final Path folder = Path.of("target", "card");

    @Test
    void twoFramedRobotsAreDrawnWithNothingSaidAndOneMadeTallerCoversMore() throws IOException {
        Files.createDirectories(folder);
        final Rig bound = new Rig(Proportions.MODEL, BoundPose.INSTANCE.pose(), false);
        final AvatarMaker maker = new AvatarMaker();
        final Avatar robot = new Avatar(new Figures.FramedRobot(), bound, maker.pieces(
                RobotParts.INSTANCE.of(bound, RobotDesign.DEFAULT, Paint.FRAME, new Detail(0.8))));
        final Moving standing = new Moving();
        standing.moved(new Motion(Move.WALK, 0, false, 0, 1, true), 0);
        final Rig rest = standing.rig();
        final double lift = standing.lift();
        final Camera camera = new Orbit(new Vec3(0, 110, 0), 15, 6, 520).camera(40, 5);
        try (Card card = new Card();
             Canvas canvas = card.canvas(WIDTH, HEIGHT);
             LineupView view = new LineupView(card, new Laid().asking(card.rays()))) {
            final Shadowing by = view.offered().getFirst();
            view.drawn(canvas, new LineupShown(2), rest, lift, camera, day, true, by);
            final Picture empty = new Picture(canvas.width(), canvas.pixels());
            view.add(robot);
            view.add(robot);
            view.drawn(canvas, new LineupShown(2), rest, lift, camera, day, true, by);
            final Picture two = kept(canvas, "lineup-two-framed-robots.png");
            view.drawn(canvas, new LineupShown(2).picking(1).standing(230), rest, lift, camera, day, true, by);
            final Picture taller = kept(canvas, "lineup-one-taller.png");
            view.drawn(canvas, new LineupShown(2), rest, lift, camera, day, false, by);
            kept(canvas, "lineup-no-shadows.png");
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
            assertEquals(empty.at(4, 4), two.at(4, 4), "the sky in a corner");
            final int covered = changed(empty, two);
            assertTrue(covered > WIDTH * HEIGHT / 100, "the robots cover some of the picture: " + covered);
            assertTrue(changed(empty, taller) > covered, "and more of it with one taller");
        }
    }

    /** What {@code canvas} shows, written to {@code name} in the pictures' folder. */
    private Picture kept(final Canvas canvas, final String name) throws IOException {
        final Picture p = new Picture(canvas.width(), canvas.pixels());
        ImageIO.write(p.image(), "png", folder.resolve(name).toFile());
        return p;
    }

    /** How many pixels of {@code b} are not as they are in {@code a}. */
    private int changed(final Picture a, final Picture b) {
        int n = 0;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                n += a.at(x, y) != b.at(x, y) ? 1 : 0;
            }
        }
        return n;
    }
}
