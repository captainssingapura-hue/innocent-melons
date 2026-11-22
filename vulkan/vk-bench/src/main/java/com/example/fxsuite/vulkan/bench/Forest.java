package com.example.fxsuite.vulkan.bench;

import java.io.IOException;

/**
 * The avatar running through a wood of giant watermelons, drawn by the card: a walk ({@link Walk}) in the wood
 * ({@link Melons}).
 */
public final class Forest {

    public static void main(final String[] args) throws IOException {
        new Walk("The courier among giant watermelons", Melons::new).run();
    }
}
