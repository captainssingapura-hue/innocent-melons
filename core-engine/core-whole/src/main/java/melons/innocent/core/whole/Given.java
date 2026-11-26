package melons.innocent.core.whole;

import melons.innocent.core.rest.Stamped;
import melons.innocent.core.walk.Stride;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * What a tick of a whole world gives: the whole a tick on, what happened in it stamped with the tick taken, and
 * what the walking engine worked out for each walker.
 *
 * @param whole   the whole state a tick on
 * @param stamped what happened in the tick, to things and to walkers, with which tick it was
 * @param strides what was worked out for each walker, by its name
 */
public record Given(Whole whole, Stamped stamped, Map<String, Stride> strides) {

    public Given {
        strides = Collections.unmodifiableSortedMap(new TreeMap<>(strides));
    }
}
