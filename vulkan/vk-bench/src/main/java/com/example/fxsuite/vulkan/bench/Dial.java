package com.example.fxsuite.vulkan.bench;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A figure of a place's that whoever walks it may set while walking: its name, the least and the most it may be,
 * and what it is now. A place says its dials and reads each as it readies a frame; the walk's window shows a slider
 * for each and sets it as the slider is moved, from another thread, so a figure can be tried by eye without
 * starting again. It is not part of a kept run: a run played again takes the dials as they then stand.
 *
 * <p>A dial may have named steps in place of a run of figures: it is then at one of them, its value the number of
 * the step it is at, the first being nothing, and the window shows a list to choose from and no slider.
 */
public final class Dial {

    private final String name;
    private final double least;
    private final double most;
    private final AtomicLong now;
    private final List<String> steps;

    /** A dial called {@code name}, from {@code least} to {@code most}, at {@code first} to begin with. */
    public Dial(final String name, final double least, final double most, final double first) {
        if (!(most > least)) {
            throw new IllegalArgumentException(name + " goes from some figure up to a greater: " + least + ", " + most);
        }
        this.name = name;
        this.least = least;
        this.most = most;
        this.steps = List.of();
        this.now = new AtomicLong(Double.doubleToLongBits(Math.clamp(first, least, most)));
    }

    /** A dial called {@code name} that is at one of {@code steps}, each named, at the one numbered {@code first}. */
    public Dial(final String name, final List<String> steps, final int first) {
        if (steps.size() < 2) {
            throw new IllegalArgumentException(name + " is at one of two steps or more: " + steps);
        }
        this.name = name;
        this.least = 0;
        this.most = steps.size() - 1;
        this.steps = List.copyOf(steps);
        this.now = new AtomicLong(Double.doubleToLongBits(Math.clamp(first, 0, steps.size() - 1)));
    }

    /** Its steps by name, where it has named steps; none, where it runs from its least to its most. */
    public List<String> steps() {
        return steps;
    }

    /** The number of the step it is at, where it has named steps: its value, to the nearest whole. */
    public int step() {
        return (int) Math.round(value());
    }

    /** What it is called. */
    public String name() {
        return name;
    }

    /** The least it may be. */
    public double least() {
        return least;
    }

    /** The most it may be. */
    public double most() {
        return most;
    }

    /** What it is now. From any thread. */
    public double value() {
        return Double.longBitsToDouble(now.get());
    }

    /** Set to {@code value}, or to its least or its most where that is beyond them. From any thread. */
    public void set(final double value) {
        now.set(Double.doubleToLongBits(Math.clamp(value, least, most)));
    }
}
