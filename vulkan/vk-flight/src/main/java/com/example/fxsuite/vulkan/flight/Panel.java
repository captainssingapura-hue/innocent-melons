package com.example.fxsuite.vulkan.flight;

import com.example.fxsuite.vulkan.device.Clicked;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Overlay;
import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A tiltrotor's display, over the picture, in the green of an instrument's light: in the middle the craft's mark and
 * its pitch ladder, a line every ten degrees, turned with its roll; above, its heading; on the left its speed in
 * knots, on the right its height over the sea and over what is below, in metres; below on the left its power, its
 * nacelles' angle and how much faster than time it flies. And a row of buttons along the foot on the right, to be
 * clicked: HOVER, CONVERT and PLANE send the nacelles up, half way and down, VIEW goes to the cockpit or behind, TIME
 * flies one, four or sixteen times as fast. The one that holds is lit amber. A click is the button's name, told to
 * the next step as a key is.
 */
public final class Panel {

    /** How many degrees of the view a unit of the screen's height is: half the walks' view of 48. */
    private static final double DEGREES = 24;
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
        final List<String> names = List.of("TIME", "VIEW", "PLANE", "CONVERT", "HOVER");
        for (final String name : names) {
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

    /** The display by {@code pass} over {@code over}, for {@code craft} over {@code below}. */
    public void drawn(final Pass pass, final Overlay.Over over, final Tiltrotor craft, final Below below) {
        if (!craft.cockpit()) {
            ladder(pass, over, craft.attitude());
        }
        final double edge = over.aspect();
        over.line(pass, -0.1, 0, -0.03, 0, THIN, green);
        over.line(pass, 0.03, 0, 0.1, 0, THIN, green);
        over.line(pass, -0.03, 0, 0, -0.025, THIN, green);
        over.line(pass, 0, -0.025, 0.03, 0, THIN, green);
        final double heading = ((craft.attitude().heading() % 360) + 360) % 360;
        centred(pass, over, String.format(Locale.ROOT, "HDG %03.0f", heading), 0.88);
        over.text(pass, String.format(Locale.ROOT, "KT %.0f", craft.speed() / KNOT), -edge + 0.06, 0.05, LETTER,
                green);
        final double sea = craft.at().y() / 100;
        final double over0 = (craft.at().y() - below.under(craft.at().x(), craft.at().z())) / 100;
        over.text(pass, String.format(Locale.ROOT, "ALT %.0f M", sea), edge - 0.5, 0.05, LETTER, green);
        over.text(pass, String.format(Locale.ROOT, "AGL %.0f M", over0), edge - 0.5, -0.03, LETTER, green);
        over.text(pass, String.format(Locale.ROOT, "PWR %.0f%%", craft.power() * 100), -edge + 0.06, FOOT + 0.1,
                LETTER, green);
        over.text(pass, String.format(Locale.ROOT, "NAC %.0f", craft.tilt()), -edge + 0.06, FOOT + 0.02, LETTER,
                green);
        over.text(pass, "X" + craft.faster(), -edge + 0.5, FOOT + 0.02, LETTER, craft.faster() > 1 ? amber : green);
        buttons(pass, over, craft);
    }

    private void centred(final Pass pass, final Overlay.Over over, final String text, final double y) {
        over.text(pass, text, -over.wide(text, LETTER) / 2, y, LETTER, green);
    }

    /** The buttons, the one that holds lit amber. */
    private void buttons(final Pass pass, final Overlay.Over over, final Tiltrotor craft) {
        for (final Button button : buttons(over.aspect())) {
            final boolean lit = switch (button.name()) {
                case "HOVER" -> craft.aim() >= 85;
                case "CONVERT" -> craft.aim() > 5 && craft.aim() < 85;
                case "PLANE" -> craft.aim() <= 5;
                case "VIEW" -> craft.cockpit();
                default -> craft.faster() > 1;
            };
            final Vec3 colour = lit ? amber : green;
            over.box(pass, button.x0(), button.y0(), button.x1(), button.y1(), THIN, colour);
            over.text(pass, button.name(), button.x0() + 0.025, button.y0() + 0.022, LETTER, colour);
        }
    }

    /** The pitch ladder: a line every ten degrees, the horizon longest, turned with the roll about the middle. */
    private void ladder(final Pass pass, final Overlay.Over over, final Attitude attitude) {
        final double c = Math.cos(Math.toRadians(-attitude.roll()));
        final double s = Math.sin(Math.toRadians(-attitude.roll()));
        for (int degrees = -60; degrees <= 60; degrees += 10) {
            final double y = (degrees - attitude.pitch()) / DEGREES;
            if (Math.abs(y) > 0.75) {
                continue;
            }
            final double half = degrees == 0 ? 0.45 : 0.16;
            final double gap = degrees == 0 ? 0.12 : 0.06;
            for (final double side : new double[] {-1, 1}) {
                final double x0 = side * gap;
                final double x1 = side * half;
                over.line(pass, x0 * c - y * s, x0 * s + y * c, x1 * c - y * s, x1 * s + y * c, THIN, green);
            }
            if (degrees != 0) {
                final double lx = half + 0.02;
                over.text(pass, String.valueOf(Math.abs(degrees)), lx * c - y * s, lx * s + y * c - 0.015, 0.03,
                        green);
            }
        }
    }
}
