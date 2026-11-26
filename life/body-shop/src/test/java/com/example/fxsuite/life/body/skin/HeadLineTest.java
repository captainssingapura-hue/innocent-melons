package com.example.fxsuite.life.body.skin;

import com.example.fxsuite.life.body.BoundPose;
import com.example.fxsuite.life.body.Part;
import com.example.fxsuite.life.body.Proportions;
import com.example.fxsuite.life.body.Rig;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.wear.Sheet;
import com.example.fxsuite.life.body.wear.Weights;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The head's skin parted from the neck's as a sculptor draws it, not level: at the same height, a little above the
 * chin's lowest, the chin the head's and the nape behind it the neck's; the plane under the chin and the jaw, down to
 * the hyoid, the head's; the throat under it the neck's; the trunk's left alone; a piece of either apart from the rest
 * of it given to the other.
 */
class HeadLineTest {

    private final Rig bind = new Rig(Proportions.DRAWN, BoundPose.INSTANCE.pose(), false);

    @Test
    void theChinIsTheHeadsAndTheNapeAtItsHeightTheNecks() {
        assertEquals(Part.HEAD, split(Part.NECK, new V3(0, -5.5, 8.5))[0], "the chin");
        assertEquals(Part.NECK, split(Part.HEAD, new V3(0, -5.5, -6))[0], "the nape, as high");
    }

    @Test
    void underTheChinAndTheJawToTheHyoidIsTheHeadsTheThroatTheNecks() {
        assertEquals(Part.HEAD, split(Part.NECK, new V3(0, -6.8, 5))[0], "under the chin");
        assertEquals(Part.HEAD, split(Part.NECK, new V3(5, -6.5, 1.6))[0], "under the jaw's angle");
        assertEquals(Part.NECK, split(Part.HEAD, new V3(0, -9.5, 4))[0], "the throat");
    }

    @Test
    void theOcciputIsTheHeadsAndTheTrunkIsLeftAlone() {
        assertEquals(Part.HEAD, split(Part.NECK, new V3(0, 6, -9))[0], "the back of the skull, above the nuchal line");
        assertEquals(Part.CHEST, split(Part.CHEST, new V3(0, 6, -9))[0], "a part not the head's nor the neck's");
    }

    @Test
    void aPieceOfTheNeckApartFromTheRestIsTheHeads() {
        final Part[] parts = split(Part.NECK, new V3(0, -8.5, 6), new V3(0, -8.5, -6), new V3(0, -9, -6));
        assertEquals(Part.HEAD, parts[0], "the piece apart");
        assertEquals(Part.NECK, parts[1], "the largest piece, two triangles joined");
        assertEquals(Part.NECK, parts[2], "the largest piece, two triangles joined");
    }

    /**
     * A small triangle round each of {@code at}, in the head's frame, each first of {@code was}, split; each sharing
     * an edge with the one before it if they are within a centimetre.
     */
    private Part[] split(final Part was, final V3... at) {
        final Rig.Frame head = bind.frame(Part.HEAD);
        final Sheet sheet = new Sheet();
        final int[] triangles = new int[3 * at.length];
        final Part[] patches = new Part[at.length];
        final Weights none = new Weights(new float[Part.values().length]);
        for (int i = 0; i < at.length; i++) {
            final boolean joined = i > 0 && at[i].minus(at[i - 1]).length() < 1;
            triangles[3 * i] = joined ? triangles[3 * i - 3]
                    : sheet.add(head.place(at[i].plus(new V3(0.3, 0, 0))), none);
            triangles[3 * i + 1] = joined ? triangles[3 * i - 2]
                    : sheet.add(head.place(at[i].plus(new V3(-0.3, 0, 0))), none);
            triangles[3 * i + 2] = sheet.add(head.place(at[i].plus(new V3(0, 0.3, 0))), none);
            patches[i] = was;
        }
        return HeadLine.INSTANCE.split(patches, sheet, triangles, bind);
    }
}
