package com.example.fxsuite.vulkan.bench.play;

import java.util.Optional;

/**
 * Where a walk's steps come from: a script, someone playing, or a run kept and read back. Whatever follows from the
 * steps asks only this, and cannot tell which it has.
 */
public interface Source {

    /**
     * What the step numbered {@code step} is told, the first step 1; {@code left} is how many steps, this one among
     * them, the frame it is taken in still has to take, for a source that shares a frame's doings out among them.
     */
    Step step(long step, int left);

    /** Whether it has nothing to tell once {@code steps} steps have been taken: the run it tells is over. */
    boolean over(long steps);

    /** How many steps it tells before it is over: of someone playing, no end. */
    long length();

    /** Whether it ends of itself: a script or a kept run does; someone playing does not. */
    boolean ends();

    /** Where the avatar stood after the step numbered {@code step}, where the source knows: a kept run does. */
    default Optional<Where> check(final long step) {
        return Optional.empty();
    }
}
