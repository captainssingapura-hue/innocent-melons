package com.example.fxsuite.avatar.look;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The figures the avatar may be drawn as, to choose from: the courier ({@link BoxRig}) always, first; any other a
 * library offers ({@link #offer}), as the body shop offers its robots, by name. Which is chosen ({@link #choose}),
 * or {@code -Davatar.figure} at the start; each game makes its own ({@link #make}).
 */
public final class Figures {

    /** The courier: the figure the avatar has always been. */
    public static final String COURIER = "Courier";

    private static final Map<String, Supplier<Figure>> OFFERED = new LinkedHashMap<>();
    private static String chosen = System.getProperty("avatar.figure", COURIER);

    static {
        OFFERED.put(COURIER, BoxRig::new);
    }

    private Figures() {
    }

    /** {@code made} offered as a figure for the avatar, called {@code name}; in place of any so called before. */
    public static synchronized void offer(final String name, final Supplier<Figure> made) {
        OFFERED.put(name, made);
    }

    /** Every figure offered, the courier first. */
    public static synchronized List<String> names() {
        return new ArrayList<>(OFFERED.keySet());
    }

    /** The figure chosen, by name. */
    public static synchronized String chosen() {
        return OFFERED.containsKey(chosen) ? chosen : COURIER;
    }

    /** {@code name} chosen, if it is offered. */
    public static synchronized void choose(final String name) {
        if (OFFERED.containsKey(name)) {
            chosen = name;
        }
    }

    /** A figure for the avatar, as chosen: the courier, if what was chosen is not offered. */
    public static synchronized Figure make() {
        return OFFERED.get(chosen()).get();
    }
}
