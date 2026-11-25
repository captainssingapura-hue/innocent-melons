package melons.innocent.glm.bench;

import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.model.Finishes;
import melons.innocent.glm.model.MadeOf;
import melons.innocent.vehiclelab.forms.Coat;

import java.util.Map;
import java.util.Set;

/**
 * What the bench shows, as its window said it last: each group's coated parts coated as {@code coats} says, those in
 * {@code hidden} not shown, and every part made of what {@code madeOf} says, its bare parts seen so. To begin with
 * every group shown, each as such parts come, made as designed.
 *
 * @param coats  what each group's coated parts are coated in
 * @param hidden the groups not shown
 * @param madeOf what the parts are made of
 */
public record OnShow(Map<Group, Coat> coats, Set<Group> hidden, MadeOf madeOf) {

    public OnShow {
        coats = Map.copyOf(coats);
        hidden = Set.copyOf(hidden);
        for (final Group group : Group.values()) {
            if (!coats.containsKey(group)) {
                throw new IllegalArgumentException("a finish for every group, and none for " + group);
            }
        }
        if (madeOf == null) {
            throw new IllegalArgumentException("the parts made of something");
        }
    }

    /** Every group shown, each in its finish to begin with ({@link Finishes}), made as designed. */
    public OnShow() {
        this(new Finishes().coats(), Set.of(), new MadeOf());
    }

    /** What {@code part} is seen in. */
    public Coat coat(final Part part) {
        return new Finishes(coats).of(part, madeOf);
    }
}
