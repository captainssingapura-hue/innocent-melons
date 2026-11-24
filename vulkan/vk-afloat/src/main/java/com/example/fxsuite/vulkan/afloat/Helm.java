package com.example.fxsuite.vulkan.afloat;

import com.example.fxsuite.vulkan.device.Clicked;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Overlay;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A boat's display over the picture: its heading above; its speed in knots and its throttle on the left; the water's
 * depth under its keel on the right; across the foot in the middle a rudder gauge, a mark as far left or right as the
 * wheel is over; and a row of buttons along the foot on the right to be clicked: AHEAD, STOP and ASTERN set the
 * throttle, VIEW goes to the helm or behind, TIME drives it one, four or sixteen times as fast. The one that holds is
 * lit amber. A click is the button's name, told to the next step as a key is.
 */
public final class Helm {

    private static final double KNOT = 51.444;
    private static final double LETTER = 0.045;
    private static final double THIN = 0.004;
    private static final double FOOT = -0.95;
    private static final double BUTTON = 0.09;
    private final Vec3 green = new Vec3(0.35, 1, 0.45);
    private final Vec3 amber = new Vec3(1, 0.72, 0.2);

    /** A button: its name, and where it is, in the screen's units. */
    public record Button(String name, double x0, double y0, double x1, double y1) {

        /** Whether ({@code x}, {@code y}) is on it. */
        public boolean holds(final double x, final double y) {
            return x >= x0 && x <= x1 && y >= y0 && y <= y1;
        }
    }

    /** The buttons, along the foot of a picture {@code aspect} times as wide as it is high, from the right. */
    public List<Button> buttons(final double aspect) {
        final List<Button> buttons = new ArrayList<>();
        double right = aspect - 0.04;
        for (final String name : List.of("TIME", "VIEW", "ASTERN", "STOP", "AHEAD")) {
            final double wide = name.length() * LETTER * 5.0 / 6 + 0.05;
            buttons.add(new Button(name, right - wide, FOOT, right, FOOT + BUTTON));
            right -= wide + 0.03;
        }
        return buttons;
    }

    /** What a click at {@code click}, on a picture {@code width} by {@code height}, presses: a button, or none. */
    public List<String> clicked(final Clicked click, final int width, final int height) {
        final double x = (click.across() - width / 2.0) / (height / 2.0);
        final double y = (height / 2.0 - click.down()) / (height / 2.0);
        return buttons((double) width / height).stream().filter(button -> button.holds(x, y)).map(Button::name)
                .toList();
    }

    /** The display by {@code pass} over {@code over}, for {@code boat}, {@code deep} of water under its keel. */
    public void drawn(final Pass pass, final Overlay.Over over, final Afloat boat, final double deep) {
        final double edge = over.aspect();
        final double heading = ((boat.heading() % 360) + 360) % 360;
        final String top = String.format(Locale.ROOT, "HDG %03.0f", heading);
        over.text(pass, top, -over.wide(top, LETTER) / 2, 0.88, LETTER, green);
        over.text(pass, String.format(Locale.ROOT, "KT %.1f", boat.speed() / KNOT), -edge + 0.06, 0.05, LETTER, green);
        over.text(pass, String.format(Locale.ROOT, "THR %.0f%%", boat.throttle() * 100), -edge + 0.06, FOOT + 0.1,
                LETTER, boat.throttle() < 0 ? amber : green);
        over.text(pass, String.format(Locale.ROOT, "DEPTH %.1f M", deep / 100), edge - 0.55, 0.05, LETTER, green);
        over.text(pass, "X" + boat.faster(), -edge + 0.5, FOOT + 0.02, LETTER, boat.faster() > 1 ? amber : green);
        // the rudder gauge: a line across, and a mark as far over as the wheel is
        final double gauge = 0.3;
        final double y = FOOT + 0.2;
        over.line(pass, -gauge, y, gauge, y, THIN, green);
        over.line(pass, 0, y - 0.02, 0, y + 0.02, THIN, green);
        over.line(pass, boat.wheel() * gauge, y - 0.035, boat.wheel() * gauge, y + 0.035, THIN * 2, amber);
        over.text(pass, "RUDDER", -over.wide("RUDDER", 0.03) / 2, y - 0.08, 0.03, green);
        for (final Button button : buttons(edge)) {
            final boolean lit = switch (button.name()) {
                case "AHEAD" -> boat.throttle() > 0.01;
                case "STOP" -> Math.abs(boat.throttle()) <= 0.01;
                case "ASTERN" -> boat.throttle() < -0.01;
                case "VIEW" -> boat.helm();
                default -> boat.faster() > 1;
            };
            final Vec3 colour = lit ? amber : green;
            over.box(pass, button.x0(), button.y0(), button.x1(), button.y1(), THIN, colour);
            over.text(pass, button.name(), button.x0() + 0.025, button.y0() + 0.022, LETTER, colour);
        }
    }
}
