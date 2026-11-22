package com.example.fxsuite.places.yard;

import com.example.fxsuite.vulkan.bench.Walk;

import java.io.IOException;

/**
 * The lab's yard, opened: the avatar walking round a house built of the lab's walls ({@link Homestead}), and in
 * through its doorway, drawn by the card. The walk, its keys and its mouse are the renderer's bench's
 * ({@code Walk}).
 */
public final class Yard {

    public static void main(final String[] args) throws IOException {
        new Walk("A house of grey brick on a stone footing", Homestead::new).run();
    }
}
