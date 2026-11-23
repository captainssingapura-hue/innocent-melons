package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.vulkan.device.Key;
import com.example.fxsuite.vulkan.device.Window;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * The keys held in the card's window, heard once a frame: which are held now, and which have gone down since they
 * were last heard, each by the engine's name for it, which is what the engine's own {@code Keys} says the meaning of
 * (W and S ahead and back, A and D sideways, full speed unless Shift is held, Space to jump). What the steps of a
 * walk are told from them is someone playing ({@code play.Played}); a script tells its own.
 */
public final class Driven {

    private final Set<Key> held = EnumSet.noneOf(Key.class);
    private final List<String> pressed = new ArrayList<>();

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

    /** Whether {@code key} has gone down and not yet been taken; taken here, it is told to no step. */
    public boolean taken(final Key key) {
        return pressed.remove(key.name());
    }

    /** The keys held, as last heard, by their names. */
    public Set<String> held() {
        final Set<String> names = new TreeSet<>();
        held.forEach(key -> names.add(key.name()));
        return names;
    }

    /** The keys that have gone down since this was last asked, by their names, in the order they went down. */
    public List<String> pressed() {
        final List<String> went = List.copyOf(pressed);
        pressed.clear();
        return went;
    }
}
