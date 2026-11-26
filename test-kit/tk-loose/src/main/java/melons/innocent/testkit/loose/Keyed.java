package melons.innocent.testkit.loose;

import melons.innocent.core.walk.Wish;

import java.util.List;
import java.util.Set;

/**
 * Keys turned into what a walker is asked: the one place it is done, outside the core, which never sees a key. W
 * and S are ahead and back, A and D aside; it runs unless Shift is held, as the walks' avatar always has; and Space
 * pressed is a jump.
 *
 * @param held    the keys held at a step
 * @param pressed the keys pressed at it
 */
record Keyed(Set<String> held, List<String> pressed) {

    /** What a walker is asked at this step, turning so many degrees toward the right. */
    Wish wish(final double turn) {
        return new Wish((held.contains("W") ? 1 : 0) - (held.contains("S") ? 1 : 0),
                (held.contains("D") ? 1 : 0) - (held.contains("A") ? 1 : 0), turn, !held.contains("SHIFT"),
                pressed.contains("SPACE"));
    }
}
