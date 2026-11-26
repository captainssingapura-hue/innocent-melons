package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.vulkan.bench.play.Keeping;
import com.example.fxsuite.vulkan.bench.play.Source;

import java.util.List;
import java.util.Optional;

/**
 * What a walk moves, made for each run: told its steps by {@code source}, kept by {@code keeping} where there is
 * one, its steps moving {@code skies}' clock on; and the keys it answers to, as the walk's JavaFX window says them.
 */
public interface Roamer {

    /** What moves, for a run told by {@code source}. */
    Roaming roaming(Source source, Optional<Keeping> keeping, Skies skies);

    /** The keys and the mouse, line by line, as the window beside the card's says them. */
    List<String> keys();
}
