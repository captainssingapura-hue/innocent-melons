package com.example.fxsuite.vulkan.flight;

import com.example.fxsuite.vulkan.bench.play.Scripted;
import com.example.fxsuite.vulkan.bench.play.Source;
import com.example.fxsuite.vulkan.bench.play.Step;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Clicked;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A tiltrotor flown from the keyboard, with no card. Its nacelles up and its power raised, it lifts off and climbs;
 * idle, it settles back on the ground and no lower. Sent ahead at full power, it comes to about its top speed, its
 * nacelles turning down at their own pace. Rolled and let go, it comes back level. Flying ahead low, its rotors'
 * tips are kept over the ground. 1, 2 and 3 send its nacelles up, half way and down; T flies it four times as fast.
 * And a click on its panel's buttons is the button's name.
 */
class TiltrotorTest {

    private static final double STEP = 1.0 / 120;
    private final Below level = (x, z) -> 0;
    private final Airframe frame = new Frame();

    @Test
    void itsPowerRaisedItLiftsOffAndIdleItSettlesBackOnTheGround() {
        Tiltrotor craft = flown(new Tiltrotor(new Vec3(0, 50, 0), 0), "W:2.3;SHIFT:4");
        assertTrue(craft.at().y() > 500, "it climbed: " + craft.at());
        craft = flown(craft, "S:3;SHIFT:12");
        assertEquals(50, craft.at().y(), 1e-6, "back on the ground, half a metre over it");
    }

    @Test
    void sentAheadAtFullPowerItComesToAboutItsTopSpeed() {
        Tiltrotor craft = flown(new Tiltrotor(new Vec3(0, 50, 0), 0), "W:2.5;THREE;SHIFT:60");
        assertEquals(0, craft.tilt(), 1e-9, "its nacelles down");
        final double ahead = craft.going().dot(craft.attitude().ahead());
        assertEquals(frame.top(), ahead, 0.1 * frame.top(), "about its top speed: " + ahead);
        assertTrue(craft.at().y() - 50 >= frame.belowWheels(0) - 1e-6, "its rotors' tips over the ground");
    }

    @Test
    void rolledAndLetGoItComesBackLevel() {
        final Tiltrotor rolled = flown(new Tiltrotor(new Vec3(0, 5000, 0), 0), "RIGHT:0.6");
        assertTrue(rolled.attitude().roll() > 20, "rolled: " + rolled.attitude());
        assertEquals(0, flown(rolled, "SHIFT:3").attitude().roll(), 1e-9, "level again");
    }

    @Test
    void presetsSendItsNacellesAndTFliesItFourTimesAsFast() {
        assertEquals(60, flown(new Tiltrotor(new Vec3(0, 5000, 0), 0), "TWO;SHIFT:4").aim(), 1e-9);
        assertEquals(0, flown(new Tiltrotor(new Vec3(0, 5000, 0), 0), "THREE;SHIFT:8").tilt(), 1e-9);
        assertEquals(4, flown(new Tiltrotor(new Vec3(0, 5000, 0), 0), "T").faster());
        assertEquals(1, flown(new Tiltrotor(new Vec3(0, 5000, 0), 0), "T;T;T").faster(), "round again");
    }

    @Test
    void aClickOnItsPanelsButtonsIsTheButtonsName() {
        final Panel panel = new Panel();
        final int wide = 1600;
        final int high = 900;
        for (final Panel.Button button : panel.buttons((double) wide / high)) {
            final double x = (button.x0() + button.x1()) / 2 * high / 2 + wide / 2.0;
            final double y = high / 2.0 - (button.y0() + button.y1()) / 2 * high / 2;
            assertEquals(List.of(button.name()), panel.clicked(new Clicked(x, y), wide, high));
        }
        assertEquals(List.of(), panel.clicked(new Clicked(wide / 2.0, high / 2.0), wide, high), "the middle");
        assertEquals(Set.of("HOVER", "CONVERT", "PLANE", "VIEW", "TIME"), Set.copyOf(panel.buttons(1.6).stream()
                .map(Panel.Button::name).toList()));
    }

    /** {@code craft} flown as {@code script} says, step by step, till it ends. */
    private Tiltrotor flown(final Tiltrotor craft, final String script) {
        final Source source = new Scripted(script);
        Tiltrotor now = craft;
        for (long step = 1; !source.over(step - 1); step++) {
            final Step told = source.step(step, 1);
            now = now.stepped(told, STEP, frame, level);
        }
        return now;
    }

    /** An airframe with the Osprey's speeds, its rotors as low as its, and no drawing. */
    private record Frame() implements Airframe {

        @Override
        public double top() {
            return 14_147;
        }

        @Override
        public double cruise() {
            return 12_347;
        }

        @Override
        public double tilted() {
            return 97.5;
        }

        @Override
        public double belowWheels(final double tilt) {
            return Math.max(0, 580 * Math.cos(Math.toRadians(tilt)) - 412 - 200 * Math.sin(Math.toRadians(tilt)));
        }

        @Override
        public Vec3 cockpit() {
            return new Vec3(0, 255, 600);
        }

        @Override
        public void carded(final Card card, final Laid laid) {
        }

        @Override
        public void drawn(final Pass pass, final Camera camera, final Mat4 placed, final double tilt,
                          final double turned) {
        }

        @Override
        public void close() {
        }
    }
}
