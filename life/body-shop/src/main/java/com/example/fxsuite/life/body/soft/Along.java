package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.V3;

/** Which way something runs at a point: a muscle's fibres, say ({@link Fibres}). One long. */
public interface Along {

    /** The way it runs at {@code p}, one long. */
    V3 at(V3 p);
}
