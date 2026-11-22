package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.avatar.sim.Controls;
import com.example.fxsuite.avatar.sim.Keys;
import com.example.fxsuite.avatar.sim.Script;
import com.example.fxsuite.vulkan.device.Key;
import com.example.fxsuite.vulkan.device.Window;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * What the avatar is told to do, a step of the simulation at a time: by the keys held in the card's window, which
 * mean exactly what they mean in the engine's game, because the engine's own {@code Keys} says what they mean (W
 * and S ahead and back, A and D sideways, full speed unless Shift is held, Space to jump); or, where a script is
 * given, by the script, in the engine's own script language, so a run with nobody at the keys is the same run every
 * time. The keys are heard once a frame; a key pressed is told to the first step that comes, and to that step only.
 */
public final class Driven {

    private final Optional<Script> script;
    private final Set<Key> held = EnumSet.noneOf(Key.class);
    private final List<String> pressed = new ArrayList<>();

    /** Driven by {@code written}, a script as the engine writes them; or by the keys, where it is empty. */
    public Driven(final String written) {
        this.script = written.isEmpty() ? Optional.empty() : Optional.of(Script.parse(written));
    }

    /** The keys heard: which are held in {@code window} now, and which have gone down since they were last heard. */
    public void heard(final Window window) {
        for (final Key key : Key.values()) {
            final boolean down = window.held(key);
            if (down && !held.contains(key)) {
                pressed.add(key.name());
            }
            if (down) {
                held.add(key);
            } else {
                held.remove(key);
            }
        }
    }

    /** Whether {@code key} is held, as last heard. */
    public boolean held(final Key key) {
        return held.contains(key);
    }

    /** Whether {@code key} has gone down and not yet been taken; taken here, it is told to no step. */
    public boolean taken(final Key key) {
        return pressed.remove(key.name());
    }

    /**
     * What the avatar is told for the step numbered {@code step}, the first step 1: turned {@code turn} degrees by
     * the mouse during it, and aiming {@code aim} degrees up.
     */
    public Controls controls(final long step, final double turn, final double aim) {
        if (script.isPresent()) {
            // as the script's own controls, but for F: taking off and landing are done here, on its jets
            final Script written = script.get();
            return Keys.controls(written.held(step),
                    written.pressed(step).stream().filter(key -> !key.equals(Key.F.name())).toList(),
                    written.turn(step), written.trigger(step), written.aim(step), written.bomb(step));
        }
        final Set<String> names = new HashSet<>();
        held.forEach(key -> names.add(key.name()));
        final Controls controls = Keys.controls(names, List.copyOf(pressed), turn, false, aim);
        pressed.clear();
        return controls;
    }

    /**
     * Whether the avatar is asked, at the step numbered {@code step}, to take off or to land: F pressed in the
     * card's window, or tapped in the script. Asked of the keys, it is told once and not again.
     */
    public boolean flies(final long step) {
        return script.map(written -> written.pressed(step).contains(Key.F.name())).orElseGet(() -> taken(Key.F));
    }

    /** How far the script tilts the view up during the step numbered {@code step}, in degrees; nothing, by the keys. */
    public double tilt(final long step) {
        return script.map(written -> written.tilt(step)).orElse(0.0);
    }

    /** Whether a script was given. */
    public boolean scripted() {
        return script.isPresent();
    }

    /** Whether a script was given and {@code steps} steps have run it to its end. */
    public boolean over(final long steps) {
        return script.isPresent() && steps >= script.get().length();
    }
}
