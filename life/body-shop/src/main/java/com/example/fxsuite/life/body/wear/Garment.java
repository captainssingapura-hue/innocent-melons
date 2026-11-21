package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.life.body.flesh.Flesh;

/** Something worn: what it is called, its colour, and its cloth bound to the body, to move with it ({@link Flesh}). */
public record Garment(String name, String colour, Flesh cloth) {
}
