package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.skull.Landmark;
import com.example.fxsuite.life.body.bone.skull.SkullLandmarks;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The skull under a head: a round head ten centimetres about the skull's middle asks each landmark its skin tells of
 * to lie under it as far in as the soft over it is thick, along a line from the skull's middle or, the jaws', from
 * the mouth; of those it does not tell of, nothing.
 */
class SkullUnderTest {

    private final V3 middle = new V3(0, 6.5, 0.5);
    private final Field.Shape round = Field.Ellipsoid.of(middle, new V3(10, 0, 0), new V3(0, 10, 0), new V3(0, 0, 10));
    private final Map<Landmark, V3> asked = SkullUnder.INSTANCE.under(round, V3.ZERO,
            SkullLandmarks.INSTANCE.targets());

    @Test
    void eachLandmarkLiesUnderTheSkinAsFarAsTheSoftOverItIsThick() {
        for (final Map.Entry<Landmark, V3> e : asked.entrySet()) {
            final double depth = SkullUnder.INSTANCE.depth(e.getKey()).orElseThrow();
            final V3 p = e.getValue();
            final double under = -round.at(p.x(), p.y(), p.z());
            assertTrue(under <= depth + 0.03 && under >= 0.5 * depth, e.getKey() + ": " + under + " under, " + depth);
        }
        assertEquals(10 - 0.55, asked.get(Landmark.GLABELLA).minus(middle).length(), 0.03,
                "seen from the skull's middle, square to the skin, as far in as the soft is thick");
    }

    @Test
    void whatTheSkinDoesNotTellOfIsNotAsked() {
        assertFalse(asked.containsKey(Landmark.EURYON), "the vault's side, not measured");
        assertFalse(asked.containsKey(Landmark.DACRYON), "the orbit, under no skin");
    }
}
