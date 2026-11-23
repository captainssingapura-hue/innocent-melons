package com.example.fxsuite.vulkan.afloat;

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
 * A boat driven from the keyboard on a straight canal 20 m wide and 3 m deep, with no card. Its throttle opened, it
 * comes to the speed asked, slowly; it rides at the water's top. Its wheel over, it turns the way the wheel is put,
 * faster the faster it goes, and not at all standing still. Steered at the bank, it stops where its bow would run
 * aground, and backs off astern. The panel's AHEAD, STOP and ASTERN set the throttle, and a click on a button is its
 * name. Where it starts is searched for outward from where it is asked to.
 */
class AfloatTest {

    private static final double STEP = 1.0 / 120;
    private final Waters canal = new Canal();
    private final Vessel boat = new Boat();

    @Test
    void itsThrottleOpenedItComesSlowlyToTheSpeedAskedRidingAtTheWatersTop() {
        final Afloat early = driven(new Afloat(new Vec3(0, 0, 0), 0), "AHEAD;SHIFT:1");
        assertTrue(early.speed() < 0.3 * 0.6 * boat.top(), "slow to begin with: " + early.speed());
        final Afloat later = driven(early, "SHIFT:30");
        assertEquals(0.6 * boat.top(), later.speed(), 0.01 * boat.top());
        assertEquals(0, later.at().y(), 1e-9, "at the water's top");
        assertTrue(later.at().z() > 10_000, "gone ahead along the canal: " + later.at());
    }

    @Test
    void itsWheelOverItTurnsThatWayFasterTheFasterItGoesAndNotAtAllStandingStill() {
        assertEquals(0, driven(new Afloat(new Vec3(0, 0, 0), 0), "D:1;SHIFT:4").heading(), 1e-9, "still");
        final Afloat slow = driven(new Afloat(new Vec3(0, 0, 0), 0), "ASTERN;STOP;W:0.5;D:1;SHIFT:2");
        final Afloat fast = driven(new Afloat(new Vec3(0, 0, 0), 0), "W:2.5;SHIFT:20;D:1;SHIFT:2");
        assertTrue(slow.heading() > 0, "to the right: " + slow.heading());
        assertTrue(fast.heading() > 2 * slow.heading(), "faster when faster: " + fast.heading());
        assertTrue(driven(new Afloat(new Vec3(0, 0, 0), 0), "W:2.5;SHIFT:20;A:1;SHIFT:2").heading() < 0, "left");
    }

    @Test
    void steeredAtTheBankItStopsWhereItsBowWouldRunAgroundAndBacksOffAstern() {
        final Afloat aground = driven(new Afloat(new Vec3(0, 0, 0), 90), "AHEAD;SHIFT:20");
        assertEquals(0, aground.speed(), 1e-9, "stopped");
        assertTrue(aground.at().x() + boat.length() / 2 <= 1_000 + 1e-6, "its bow short of the bank: " + aground.at());
        assertTrue(aground.at().x() > 1_000 - boat.length() / 2 - 100, "close by it: " + aground.at());
        final Afloat backed = driven(aground, "ASTERN;SHIFT:10");
        assertTrue(backed.at().x() < aground.at().x() - 500, "backed off: " + backed.at());
    }

    @Test
    void thePanelsButtonsSetTheThrottleAndAClickOnOneIsItsName() {
        assertEquals(0.6, driven(new Afloat(new Vec3(0, 0, 0), 0), "AHEAD").throttle(), 1e-9);
        assertEquals(-0.3, driven(new Afloat(new Vec3(0, 0, 0), 0), "ASTERN").throttle(), 1e-9);
        assertEquals(0, driven(new Afloat(new Vec3(0, 0, 0), 0), "W:1;STOP").throttle(), 1e-9);
        assertEquals(16, driven(new Afloat(new Vec3(0, 0, 0), 0), "TIME;T").faster());
        assertTrue(driven(new Afloat(new Vec3(0, 0, 0), 0), "VIEW").helm());
        final Helm helm = new Helm();
        final int wide = 1600;
        final int high = 900;
        for (final Helm.Button button : helm.buttons((double) wide / high)) {
            final double x = (button.x0() + button.x1()) / 2 * high / 2 + wide / 2.0;
            final double y = high / 2.0 - (button.y0() + button.y1()) / 2 * high / 2;
            assertEquals(List.of(button.name()), helm.clicked(new Clicked(x, y), wide, high));
        }
        assertEquals(Set.of("AHEAD", "STOP", "ASTERN", "VIEW", "TIME"),
                Set.copyOf(helm.buttons(1.6).stream().map(Helm.Button::name).toList()));
    }

    @Test
    void whereItStartsIsTheNearestWaterDeepEnoughToWhereItIsAsked() {
        final Vec3 found = new Cruising(canal, "0,0,0", Boat::new).water(new Vec3(5_000, 0, 700), 150);
        assertTrue(canal.deep(found.x(), found.z()) >= 150, "in the canal: " + found);
        assertEquals(700, found.z(), 1e-9, "straight across to it");
    }

    /** {@code boat} driven as {@code script} says, step by step, till it ends. */
    private Afloat driven(final Afloat start, final String script) {
        final Source source = new Scripted(script);
        Afloat now = start;
        for (long step = 1; !source.over(step - 1); step++) {
            final Step told = source.step(step, 1);
            now = now.stepped(told, STEP, boat, canal);
        }
        return now;
    }

    /** A canal along the world, 20 m wide and 3 m deep, its top at nothing; land a metre up either side. */
    private record Canal() implements Waters {

        @Override
        public double top(final double x, final double z) {
            return Math.abs(x) < 1_000 ? 0 : Double.NaN;
        }

        @Override
        public double bed(final double x, final double z) {
            return Math.abs(x) < 1_000 ? -300 : 100;
        }
    }

    /** A boat 13 m long drawing 62 cm, going 7 m a second at full throttle, with no drawing. */
    private record Boat() implements Vessel {

        @Override
        public double top() {
            return 700;
        }

        @Override
        public double draught() {
            return 62;
        }

        @Override
        public double length() {
            return 1_300;
        }

        @Override
        public Vec3 helm() {
            return new Vec3(0, 260, 25);
        }

        @Override
        public void carded(final Card card, final Laid laid) {
        }

        @Override
        public void drawn(final Pass pass, final Camera camera, final Mat4 placed, final double wheel) {
        }

        @Override
        public void close() {
        }
    }
}
