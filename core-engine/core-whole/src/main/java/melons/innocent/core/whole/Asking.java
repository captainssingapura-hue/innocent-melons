package melons.innocent.core.whole;

import melons.innocent.core.rest.Asked;
import melons.innocent.core.walk.Wish;

import java.util.Map;

/**
 * What is asked of a whole world for one tick, from outside it: what is asked of its things, and what each walker
 * is asked, by its name. A walker asked nothing is asked nothing: it comes to a stand.
 *
 * @param things what is asked of the things
 * @param wishes what each walker is asked
 */
public record Asking(Asked things, Map<String, Wish> wishes) {

    public Asking {
        wishes = Map.copyOf(wishes);
    }

    /** Nothing asked of anything. */
    public Asking() {
        this(new Asked(), Map.of());
    }
}
