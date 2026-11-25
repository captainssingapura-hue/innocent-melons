package melons.innocent.glm.bench;

import melons.innocent.glm.core.Group;
import melons.innocent.glm.model.Finishes;
import melons.innocent.vehiclelab.forms.Coat;

import java.util.Map;
import java.util.Set;

/**
 * What the bench shows, as its window said it last: each group of the platform's parts finished as {@code coats}
 * says, and those in {@code hidden} not shown. To begin with every group shown, each as such parts come.
 *
 * @param coats  what each group is finished in
 * @param hidden the groups not shown
 */
public record OnShow(Map<Group, Coat> coats, Set<Group> hidden) {

    public OnShow {
        coats = Map.copyOf(coats);
        hidden = Set.copyOf(hidden);
        for (final Group group : Group.values()) {
            if (!coats.containsKey(group)) {
                throw new IllegalArgumentException("a finish for every group, and none for " + group);
            }
        }
    }

    /** Every group shown, each in its finish to begin with ({@link Finishes}). */
    public OnShow() {
        this(new Finishes().coats(), Set.of());
    }
}
