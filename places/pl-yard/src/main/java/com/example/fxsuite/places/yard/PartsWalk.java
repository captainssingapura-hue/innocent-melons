package com.example.fxsuite.places.yard;

import com.example.fxsuite.vulkan.bench.Walk;

import java.io.IOException;

/**
 * A car's parts, laid out apart and opened: the avatar walking round the frame and along the bench the smaller
 * parts lie on, drawn by the card ({@link PartsYard}). The walk, its keys and its mouse are the renderer's bench's
 * ({@code Walk}).
 */
public final class PartsWalk {

    public static void main(final String[] args) throws IOException {
        new Walk("A car's parts, laid out apart", PartsYard::new).run();
    }
}
