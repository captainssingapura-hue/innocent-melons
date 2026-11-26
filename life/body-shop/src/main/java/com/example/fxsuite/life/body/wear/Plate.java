package com.example.fxsuite.life.body.wear;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.taxonomy.Leaf;

/**
 * A hard piece worn, keeping its shape: what kind of thing worn it is
 * ({@link com.example.fxsuite.existence.Wearables}); the part of the body it is held to and moves with; its surface,
 * in the body's frame as it is bound.
 *
 * @param kind what it is
 * @param part what it moves with
 * @param mesh its surface, as the body is bound
 */
public record Plate(Leaf<?> kind, Part part, Mesher.Mesh mesh) {
}
