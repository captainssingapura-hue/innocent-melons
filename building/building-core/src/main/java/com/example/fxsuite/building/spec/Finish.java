package com.example.fxsuite.building.spec;

import melons.innocent.forms.Tint;

import java.util.Optional;

/**
 * How a building is finished outside, as a specification says it: what its walls are filled in with between its
 * structure; whether it is painted over, and what colour, the paint hiding what is under it; the colour of its
 * windows' frames, and of their glass.
 *
 * @param walling what its walls are filled in with
 * @param paint   what it is painted, if it is
 * @param frames  what colour its windows' frames are
 * @param glass   what colour its windows' glass is tinted
 */
public record Finish(Walling walling, Optional<Tint> paint, Tint frames, Tint glass) {

    public Finish {
        if (walling == null || paint == null || frames == null || glass == null) {
            throw new IllegalArgumentException("its walls filled in with something, painted or not, its frames and"
                    + " its glass some colour");
        }
    }
}
