package com.example.fxsuite.vulkan.bench.play;

import com.example.fxsuite.avatar.sim.Script;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Steps told by a script, as the engine writes them ({@code "W:2;SPACE;W+@60:1"}). F takes off or lands, Q and E
 * held swing the view and C puts it back, as the keys would; a tilt the script asks for tilts the view.
 */
public record Scripted(Script script) implements Source {

    private static final String FLY = "F";
    private static final String SWING_LEFT = "Q";
    private static final String SWING_RIGHT = "E";
    private static final String BEHIND = "C";

    /** The steps {@code written} tells, a script as the engine writes them. */
    public Scripted(final String written) {
        this(Script.parse(written));
    }

    @Override
    public long length() {
        return script.length();
    }

    @Override
    public Step step(final long step, final int left) {
        final Set<String> held = new TreeSet<>(script.held(step));
        if (script.trigger(step)) {
            held.add(new Step().trigger());
        }
        final List<String> pressed = script.pressed(step);
        final int circling = (held.contains(SWING_RIGHT) ? 1 : 0) - (held.contains(SWING_LEFT) ? 1 : 0);
        return new Step(held, pressed.stream().filter(key -> !key.equals(FLY)).toList(), script.turn(step),
                script.tilt(step), pressed.contains(FLY), circling, 0, pressed.contains(BEHIND));
    }

    @Override
    public boolean over(final long steps) {
        return steps >= script.length();
    }

    @Override
    public boolean ends() {
        return true;
    }
}
