package melons.innocent.core.rest;

import melons.innocent.core.step.Explanation;
import melons.innocent.core.tick.Tick;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * What happened in one tick of a world, stamped with the tick it happened in: what a journal keeps, a recorder
 * writes and another player is sent. With it, for whoever asks why, each thing in motion's explanation as its
 * engine gave it; these are not what happened, and nothing in the world reads them back.
 *
 * @param tick     the tick that was taken
 * @param happened what happened in it, in the order it happened
 * @param why      what the engine worked out for each thing it stepped in the tick, by the thing's name
 */
public record Stamped(Tick tick, List<Happened> happened, Map<String, Explanation<Rigid>> why) {

    public Stamped {
        happened = List.copyOf(happened);
        why = Collections.unmodifiableSortedMap(new TreeMap<>(why));
    }
}
