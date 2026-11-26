package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Field;
import com.example.fxsuite.life.body.V3;
import com.example.fxsuite.life.body.bone.skull.Landmark;
import com.example.fxsuite.life.body.bone.skull.SkullForm;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A helmet over the head: the head's skin over the vertex, the brow, the back of the head, the cheekbones, the jaw's
 * angles and the chin inside it, clear of its shell; the band across the eyes open in front of them, the visor in it
 * behind the shell's outside; the neck passing out under its rim.
 */
class HelmetTest {

    private final Helmet h = Helmet.INSTANCE;
    private final Field.Shape outer = h.outer();
    private final Field.Shape shell = h.shell(outer);
    private final Field.Shape visor = h.visor(outer);

    @Test
    void theHeadsSkinIsInsideItClearOfTheShell() {
        final SkullForm s = SkullForm.INSTANCE;
        final List<V3> skin = List.of(at(s, Landmark.BREGMA, 0, 0.5, 0), at(s, Landmark.GLABELLA, 0, 0, 0.55),
                at(s, Landmark.OPISTHOCRANION, 0, 0, -0.65), at(s, Landmark.ZYGION, 0.75, 0, 0),
                at(s, Landmark.GONION, 1.3, 0, 0), at(s, Landmark.GNATHION, 0, -0.8, 0.4));
        for (final V3 p : skin) {
            assertTrue(outer.at(p.x(), p.y(), p.z()) < -0.5, p + " well inside");
            assertTrue(shell.at(p.x(), p.y(), p.z()) > 0, p + " not in the shell");
        }
    }

    @Test
    void theBandIsOpenBeforeTheEyesTheVisorInIt() {
        final V3 eye = SkullUnder.INSTANCE.eyeOnSkull();
        double z = eye.z();
        while (outer.at(0, eye.y(), z) < 0) {
            z += 0.02;
        }
        assertTrue(shell.at(0, eye.y(), z - 0.2) > 0, "no shell where the band is cut");
        assertTrue(visor.at(0, eye.y(), z - 0.25) < 0, "the visor there, behind the outside");
        double brow = eye.z();
        while (outer.at(0, eye.y() + 4, brow) < 0) {
            brow += 0.02;
        }
        assertTrue(shell.at(0, eye.y() + 4, brow - 0.2) < 0, "the shell above the band");
    }

    @Test
    void theNeckPassesOutUnderTheRim() {
        assertTrue(shell.at(0, -14, 0) > 0, "open under it");
        double y = -14;
        while (shell.at(0, y, 2) > 0 && y < 20) {
            y += 0.05;
        }
        assertTrue(y > 13, "nothing of it across the neck's middle, up to the top of the vault: " + y);
    }

    /** Landmark {@code l} as the skull has it, moved out by {@code dx}, {@code dy} and {@code dz}, to its skin. */
    private V3 at(final SkullForm s, final Landmark l, final double dx, final double dy, final double dz) {
        return s.apply(l.drawn()).plus(new V3(dx, dy, dz));
    }
}
