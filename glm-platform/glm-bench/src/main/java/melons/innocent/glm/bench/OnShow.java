package melons.innocent.glm.bench;

import melons.innocent.glm.core.Group;
import melons.innocent.vehiclelab.forms.Coat;

import java.util.Map;
import java.util.Set;

/**
 * What the bench shows, as its window said it last: each group of the platform's parts finished as {@code coats}
 * says, and those in {@code hidden} not shown. To begin with every group shown: the underbody and the subframe painted
 * dark, the links and the carriers cast light alloy, the air springs rubber, the dampers gun metal, the discs steel,
 * the calipers painted red, the wheels gun metal and the tyres rubber.
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

    /** Every group shown, each in its finish to begin with. */
    public OnShow() {
        this(Map.of(
                Group.UNDERBODY, new Coat(Coat.Kind.GLOSS_PAINT, "#3b3f44"),
                Group.SUBFRAMES, new Coat(Coat.Kind.GLOSS_PAINT, "#1e2023"),
                Group.LINKS, new Coat(Coat.Kind.TITANIUM),
                Group.CARRIERS, new Coat(Coat.Kind.TITANIUM),
                Group.SPRINGS, new Coat(Coat.Kind.RUBBER, "#2a2a2c"),
                Group.DAMPERS, new Coat(Coat.Kind.GUN_METAL),
                Group.BRAKES, new Coat(Coat.Kind.STEEL),
                Group.CALIPERS, new Coat(Coat.Kind.GLOSS_PAINT, "#b3261e"),
                Group.WHEELS, new Coat(Coat.Kind.GUN_METAL),
                Group.TYRES, new Coat(Coat.Kind.RUBBER, "#1b1d20")), Set.of());
    }
}
