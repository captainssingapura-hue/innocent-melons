package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.life.body.flesh.Flesh;
import com.example.fxsuite.taxonomy.Leaf;

/**
 * Something worn of cloth: what it is called, its colour, its cloth bound to the body, to move with it ({@link Flesh}),
 * and what kind of thing worn it is ({@link com.example.fxsuite.existence.Wearables}).
 */
public record Garment(String name, String colour, Flesh cloth, Leaf<?> kind) {
}
