package com.example.fxsuite.vulkan.bench.play;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Steps told by someone playing. Each frame they are told what was done since the last: the keys held now and
 * those that went down, how far the mouse turned the avatar and tilted the view, the wheel, F and C. The frame's
 * steps then share it out: the turn and the tilt evenly among them, so a frame's worth is never lost or told twice;
 * what was done once, in the first of them; what is held, in all. So the mouse is kept as what it did to each step,
 * and a run kept from it is the same at any rate of frames.
 */
public final class Played implements Source {

    private Set<String> held = Set.of();
    private final List<String> pressed = new ArrayList<>();
    private double turning;
    private double tilting;
    private double scrolling;
    private int circling;
    private boolean flies;
    private boolean behind;

    /**
     * Told what was done since the last frame: {@code held} the keys held now and {@code pressed} those that went
     * down, by the engine's names; the mouse's turn and tilt, in degrees; and the wheel, in notches. As in a script,
     * F takes off or lands, Q and E held swing the view, and C puts it back.
     */
    public void told(final Set<String> held, final List<String> pressed, final double turn, final double tilt,
                     final double scrolled) {
        this.held = new TreeSet<>(held);
        pressed.stream().filter(key -> !key.equals("F")).forEach(this.pressed::add);
        this.turning += turn;
        this.tilting += tilt;
        this.scrolling += scrolled;
        this.circling = (held.contains("E") ? 1 : 0) - (held.contains("Q") ? 1 : 0);
        this.flies |= pressed.contains("F");
        this.behind |= pressed.contains("C");
    }

    @Override
    public long length() {
        return Long.MAX_VALUE;
    }

    @Override
    public Step step(final long step, final int left) {
        final int share = Math.max(1, left);
        final double turn = turning / share;
        final double tilt = tilting / share;
        turning -= turn;
        tilting -= tilt;
        final Step told = new Step(held, List.copyOf(pressed), turn, tilt, flies, circling, scrolling, behind);
        pressed.clear();
        scrolling = 0;
        flies = false;
        behind = false;
        return told;
    }

    @Override
    public boolean over(final long steps) {
        return false;
    }

    @Override
    public boolean ends() {
        return false;
    }
}
