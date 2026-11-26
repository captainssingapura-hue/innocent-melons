package melons.innocent.plants.walk;

import com.example.fxsuite.vulkan.bench.Dial;
import melons.innocent.core.motion.Motion;

import java.util.List;

/**
 * The dials a forest without end is drawn by, set while walking: the finest level any of its plants is drawn at,
 * a cap over every other rule ({@link Cap}); how far its finest level reaches, and its level with twigs gone, in
 * metres, as its reaches have them first; how many seconds ahead the eye looks, a square being drawn as it will be
 * needed at the nearest the eye comes to it in that while; and the speed above which nothing is drawn at its finest,
 * every twig. The last two to begin with as the run says: {@code -Dforest.ahead=} (3) and {@code
 * -Dforest.coarser=} kilometres an hour (50).
 */
final class ForestDials {

    private final Cap cap = new Cap();
    private final Dial fine = new Dial("Finest out to, metres", 5, 100, new Reaches().fine());
    private final Dial twigs = new Dial("Twigs gone out to, metres", 20, 250, new Reaches().twigs());
    private final Dial ahead = new Dial("Looked ahead, seconds", 0, 60,
            Double.parseDouble(System.getProperty("forest.ahead", "3")));
    private final Dial coarser = new Dial("Finest level off above, km an hour", 10, 200,
            Double.parseDouble(System.getProperty("forest.coarser", "50")));

    /** The dials, as a walk's window shows them. */
    List<Dial> dials() {
        return List.of(cap.dial(), fine, twigs, ahead, coarser);
    }

    /** The finest level any plant is drawn at, as its cap now has it. */
    Cap cap() {
        return cap;
    }

    /** How whoever moves as {@code moving} looks, by the dials and under the cap. */
    Looking looking(final Motion moving) {
        return cap.over(new Going(moving, ahead.value(), coarser.value()));
    }

    /** {@code reaches} as the dials now have them: the two nearest as set, the rest as they were. */
    Reaches reached(final Reaches reaches) {
        final double finest = fine.value();
        final double gone = Math.max(finest, twigs.value());
        return new Reaches(finest, gone, Math.max(gone, reaches.boughs()), Math.max(gone, reaches.limbs()),
                Math.max(gone, reaches.far()));
    }
}
