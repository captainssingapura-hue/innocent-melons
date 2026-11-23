package com.example.fxsuite.life.body.soft;

import com.example.fxsuite.life.body.V3;

/** What a region's muscles are kept out of: how far outside it a point is, and the way out of it there. */
public interface Walls {

    /** How far outside {@code x}, {@code y}, {@code z} is, cm; less than nothing inside. */
    double distance(double x, double y, double z);

    /** The way out at {@code x}, {@code y}, {@code z}, one long; none where it is level. */
    V3 out(double x, double y, double z);
}
