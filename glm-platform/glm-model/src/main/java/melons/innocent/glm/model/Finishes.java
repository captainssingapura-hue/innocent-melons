package melons.innocent.glm.model;

import melons.innocent.glm.core.Group;
import melons.innocent.vehiclelab.forms.Coat;

import java.util.EnumMap;
import java.util.Map;

/**
 * What each group of the platform's parts is finished in. To begin with, when nothing else is said, as such parts
 * come: the underbody and the subframes painted dark, the links and the carriers cast light alloy, the air springs
 * rubber, the dampers gun metal, the discs steel, the calipers painted red, the wheels gun metal and the tyres rubber.
 *
 * @param coats each group's finish
 */
public record Finishes(Map<Group, Coat> coats) {

    public Finishes {
        coats = Map.copyOf(coats);
        for (final Group group : Group.values()) {
            if (!coats.containsKey(group)) {
                throw new IllegalArgumentException("a finish for every group, and none for " + group);
            }
        }
    }

    /** As such parts come. */
    public Finishes() {
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
                Group.TYRES, new Coat(Coat.Kind.RUBBER, "#1b1d20")));
    }

    /** What {@code group} is finished in. */
    public Coat of(final Group group) {
        return coats.get(group);
    }

    /** The same, {@code group} finished in {@code coat} instead. */
    public Finishes with(final Group group, final Coat coat) {
        final Map<Group, Coat> changed = new EnumMap<>(coats);
        changed.put(group, coat);
        return new Finishes(changed);
    }
}
