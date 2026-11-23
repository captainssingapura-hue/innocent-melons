package com.example.fxsuite.vulkan.bench.card;

import com.example.fxsuite.avatar.look.Gait;
import com.example.fxsuite.avatar.look.Pose;
import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Clock;
import com.example.fxsuite.avatar.sim.Controls;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.bench.Cast;
import com.example.fxsuite.vulkan.bench.subject.Bot;
import com.example.fxsuite.vulkan.bench.subject.Build;
import com.example.fxsuite.vulkan.bench.subject.Courier;
import com.example.fxsuite.vulkan.bench.subject.Finish;
import com.example.fxsuite.vulkan.device.Canvas;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.render.Framed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;
import com.example.fxsuite.vulkan.worked.Daylight;
import com.example.fxsuite.vulkan.worked.Picture;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The bot on the card, by day, for the eye to judge: standing, from the front and from its right; walking and
 * running, caught mid-stride, from its right; and standing beside the engine's courier at its own size, for the two
 * to be compared; and the woman, 1.70 m in metallic pink, from the front, turned, walking, and beside the man. The
 * validation layer has nothing to say of any of it. The pictures are kept in {@code target/card}.
 */
class BotOnTheCardTest {

    private static final int WIDE = 480;
    private static final int TALL = 600;

    private final Daylight day = new Daylight();
    private final Laid laid = new Laid();
    private final Bot bot = new Bot();
    private final Pose still = new Pose(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

    @Test
    void itIsDrawnStandingWalkingAndRunningAndBesideTheCourier() throws IOException {
        final Path folder = Path.of("target", "card");
        Files.createDirectories(folder);
        final Body standing = Body.standing(new com.example.fxsuite.avatar.sim.Vec3(0, 0, 0), 0);
        try (Card card = new Card(); Canvas canvas = card.canvas(WIDE, TALL); Cast cast = new Cast(card, laid)) {
            pictured(canvas, cast, bot.placed(standing, 0, still), 180, folder.resolve("bot-front.png"));
            pictured(canvas, cast, bot.placed(standing, 0, still), 270, folder.resolve("bot-side.png"));
            pictured(canvas, cast, strode(bot, false, 0.32), 270, folder.resolve("bot-walking.png"));
            pictured(canvas, cast, strode(bot, true, 0.4), 270, folder.resolve("bot-running.png"));
            final Bot woman = new Bot(Build.WOMAN);
            pictured(canvas, cast, woman.placed(standing, 0, still), 180, folder.resolve("bot-woman-front.png"));
            pictured(canvas, cast, woman.placed(standing, 0, still), 225, folder.resolve("bot-woman-turned.png"));
            pictured(canvas, cast, strode(woman, false, 0.32), 270, folder.resolve("bot-woman-walking.png"));
            final List<Courier.Placed> pair = new ArrayList<>(bot.placed(Body.standing(
                    new com.example.fxsuite.avatar.sim.Vec3(-40, 0, 0), 0), 0, still));
            pair.addAll(woman.placed(Body.standing(new com.example.fxsuite.avatar.sim.Vec3(40, 0, 0), 0), 0, still));
            pictured(canvas, cast, pair, 180, folder.resolve("bots-together.png"));
            // turned to face the bench's sun, low in the south-west, and seen from its side
            final List<Courier.Placed> sunlit = new ArrayList<>(bot.placed(Body.standing(
                    new com.example.fxsuite.avatar.sim.Vec3(-40, 0, 0), 232), 0, still));
            sunlit.addAll(woman.placed(Body.standing(new com.example.fxsuite.avatar.sim.Vec3(40, 0, 0), 232), 0,
                    still));
            pictured(canvas, cast, sunlit, 52, folder.resolve("bots-in-the-sun.png"));
            final List<Courier.Placed> both = new ArrayList<>(bot.placed(Body.standing(
                    new com.example.fxsuite.avatar.sim.Vec3(-45, 0, 0), 0), 0, still));
            both.addAll(new Courier(190, Finish.GILT).placed(Body.standing(
                    new com.example.fxsuite.avatar.sim.Vec3(45, 0, 0), 0), still));
            pictured(canvas, cast, both, 180, folder.resolve("bot-beside-the-courier.png"));
            assertEquals(List.of(), card.heard(), "the validation layer has nothing to say");
        }
    }

    /** The bot walking, or running, ahead from a standstill, after so many seconds, as it is then. */
    private List<Courier.Placed> strode(final Bot of, final boolean running, final double seconds) {
        Avatar avatar = new Avatar(Body.standing(new com.example.fxsuite.avatar.sim.Vec3(0, 0, 0), 0), false);
        for (int step = 0; step < (2 + seconds) * 120; step++) {
            avatar = avatar.step(new Controls(1, 0, 0, 0, running), Clock.STEP, Obstacles.NONE).avatar();
        }
        final Body body = avatar.body();
        final Body there = Body.standing(new com.example.fxsuite.avatar.sim.Vec3(0, 0, 0), 0);
        // drawn where it began, so the camera need not follow it
        return of.placed(new Body(there.position(), body.yaw(), body.travel(), body.speed(), body.drift(),
                body.turnRate(), body.climb(), body.flying(), body.onGround(), body.phase(), body.time()), 0,
                Gait.of(avatar, body.yaw(), 0));
    }

    /** {@code boxes} on the ground, by day, seen from {@code round} degrees round, written to {@code file}. */
    private void pictured(final Canvas canvas, final Cast cast, final List<Courier.Placed> boxes, final double round,
                          final Path file) throws IOException {
        final Camera camera = new Orbit(new com.example.fxsuite.vulkan.space.Vec3(0, 100, 0), round, 3, 420)
                .camera(36, 5);
        canvas.drawn(day.sky(), new Framed(laid, camera, day.light()).filling((double) WIDE / TALL), pass -> {
            cast.ground(pass, camera);
            cast.figure(pass, boxes, camera);
        });
        ImageIO.write(new Picture(canvas.width(), canvas.pixels()).image(), "png", file.toFile());
    }
}
