package com.example.fxsuite.texture.materials;

import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Patches;
import com.example.fxsuite.vulkan.term.Point;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Term;
import com.example.fxsuite.vulkan.term.Tint;
import com.example.fxsuite.vulkan.term.Turns;

import java.util.List;

/**
 * What a human body is made of, each with its surface: what is seen of a person from outside, and what the
 * anatomy under the skin is drawn in. Each is a colour, marked in one of three ways with a darker shade of
 * itself, and specked with a few grains of another shade:
 *
 * <ul>
 *   <li>mottled: one place going over into the next, as skin does, or bone;</li>
 *   <li>fibred: in strands side by side that wander a little, as hair lies, and muscle, and tendon;</li>
 *   <li>lobed: in small rounded lumps with edges, as fat is.</li>
 * </ul>
 *
 * <p>Skin, hair and the iris take their colour from the person: it is told with each thing drawn, so one skin
 * serves everybody. The others are the colour they are in anyone.
 *
 * <p>These are first surfaces, and plain. Skin does not glow where light passes through it, hair does not shine
 * along its strands, and the fibres run one way in the thing's own frame, not round a limb: the lighting has none
 * of that yet. The clear skin over the eye is not here: it is a surface of its own ({@link ClearCornea}). The
 * colours and sizes are their author's, from no atlas.
 */
public enum Tissues {
    SKIN("", Marked.MOTTLED, 3, 0.78, 0.3, 0.08, 0.12, 0.85, 0.12, 30),
    LIP("#b5605d", Marked.FIBRED, 0.15, 0.8, 0.3, 0.3, 0.08, 1.15, 0.35, 60),
    NAIL("#e3c4b8", Marked.FIBRED, 0.08, 0.92, 0.3, 0.2, 0.05, 1.05, 0.5, 120),
    HAIR("", Marked.FIBRED, 0.25, 0.55, 0.6, 0.1, 0.1, 1.6, 0.3, 20),
    SCLERA("#f1ece4", Marked.MOTTLED, 1, 0.9, 0.3, 0.05, 0.04, 0.8, 0.5, 200),
    IRIS("", Marked.FIBRED, 0.1, 0.5, 0.6, 0.06, 0.1, 1.5, 0.4, 150),
    TOOTH_ENAMEL("#f3efe3", Marked.MOTTLED, 0.8, 0.9, 0.3, 0.1, 0.03, 0.9, 0.6, 150),
    BONE("#e6dcc6", Marked.MOTTLED, 2, 0.84, 0.4, 0.1, 0.08, 0.7, 0.08, 20),
    CARTILAGE("#c9d6d2", Marked.MOTTLED, 1.5, 0.88, 0.3, 0.1, 0.04, 1.08, 0.45, 80),
    MUSCLE("#9c3b34", Marked.FIBRED, 0.4, 0.68, 0.55, 0.12, 0.06, 1.3, 0.25, 40),
    TENDON("#e4dcc8", Marked.FIBRED, 0.15, 0.85, 0.5, 0.1, 0.05, 1.06, 0.4, 60),
    FAT("#e9d58a", Marked.LOBED, 0.8, 0.84, 0.5, 0.1, 0.05, 1.08, 0.3, 50);

    /** How a tissue is marked with its darker shade. */
    private enum Marked { MOTTLED, FIBRED, LOBED }

    private static final int MIDDLES = 41;
    private static final int SHADE = 43;
    private static final int GRAIN = 44;
    /** How far a strand wanders from straight, in strands' widths, and over how many widths it does so. */
    private static final double WANDER = 0.8;
    private static final double WAVE = 9;

    private final String colour;
    private final Marked marked;
    private final double size;
    private final double darker;
    private final double darkest;
    private final double grain;
    private final double few;
    private final double specked;
    private final double shine;
    private final double tight;

    Tissues(final String colour, final Marked marked, final double size, final double darker, final double darkest,
            final double grain, final double few, final double specked, final double shine, final double tight) {
        this.colour = colour;
        this.marked = marked;
        this.size = size;
        this.darker = darker;
        this.darkest = darkest;
        this.grain = grain;
        this.few = few;
        this.specked = specked;
        this.shine = shine;
        this.tight = tight;
    }

    /** Whether it takes its colour from the person, and has none of its own. */
    public boolean told() {
        return colour.isEmpty();
    }

    /** Its surface, worked out from where on the body a point is. */
    public Surface surface() {
        final Seen seen = new Seen();
        final Point at = seen.at();
        final Colour own = told() ? new Tint().colour() : new Colour(colour);
        final Real marks = switch (marked) {
            case MOTTLED -> new Mottle(size, SHADE).soft(at, seen.covers());
            case FIBRED -> new Turns(at.across().over(size)
                    .plus(at.up().over(size * WAVE).sine().times(WANDER))
                    .plus(at.along().over(size * WAVE * 1.3).sine().times(WANDER)), new Real(1 / size))
                    .pulse(0.5, seen.covers());
            case LOBED -> new Patches(at.across(), at.up().plus(at.along()), size, MIDDLES).scatter(SHADE)
                    .settling(seen.covers());
        };
        final Real grains = new Mottle(grain, GRAIN).cell(at, 0, 0, 0).some(few, seen.covers());
        return new Surface(own, List.of(
                new Term("its darker shade", own.scaled(new Real(darker)), marks.times(darkest), size / 2),
                new Term("specks", own.scaled(new Real(specked)), grains, grain)),
                new Colour(shine, shine, shine), tight);
    }
}
