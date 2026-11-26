package melons.innocent.glm.model;

import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.vehiclelab.forms.Coat;

import java.util.EnumMap;
import java.util.Map;

/**
 * What the platform's parts are seen in: a part whose skin is coated in its group's coat, a part whose skin is bare in
 * what it is made of. To begin with, when nothing else is said, as such parts come: the underbody and the subframes
 * painted dark, the coated links black, the calipers painted red, the wheels gun metal, the dampers gun metal, the
 * windows clear glass, the lamps' lenses white, the tail lamps red, the rear indicators amber and the glass roof
 * gloss black, as dark glass looks from outside; and bare, steel as steel, cast iron dark, aluminium and titanium as
 * themselves, rubber black, plastic a dull dark grey, glass clear.
 *
 * @param coats each group's coat, for its coated parts
 * @param bare  how each material looks, bare
 */
public record Finishes(Map<Group, Coat> coats, Map<Material, Coat> bare) {

    public Finishes {
        coats = Map.copyOf(coats);
        bare = Map.copyOf(bare);
        for (final Group group : Group.values()) {
            if (!coats.containsKey(group)) {
                throw new IllegalArgumentException("a coat for every group, and none for " + group);
            }
        }
        for (final Material material : Material.values()) {
            if (!bare.containsKey(material)) {
                throw new IllegalArgumentException("a look for every material, and none for " + material);
            }
        }
    }

    /** Each group coated as {@code coats} says, each material bare as it comes. */
    public Finishes(final Map<Group, Coat> coats) {
        this(coats, Map.of(
                Material.STEEL, new Coat(Coat.Kind.STEEL),
                Material.IRON, new Coat(Coat.Kind.GUN_METAL),
                Material.ALUMINIUM, new Coat(Coat.Kind.ALUMINIUM),
                Material.TITANIUM, new Coat(Coat.Kind.TITANIUM),
                Material.RUBBER, new Coat(Coat.Kind.RUBBER, "#1b1d20"),
                Material.PLASTIC, new Coat(Coat.Kind.RUBBER, "#2a2d31"),
                Material.GLASS, new Coat(Coat.Kind.GLASS)));
    }

    /** As such parts come. */
    public Finishes() {
        this(Map.ofEntries(
                Map.entry(Group.UNDERBODY, new Coat(Coat.Kind.GLOSS_PAINT, "#3b3f44")),
                Map.entry(Group.SUBFRAMES, new Coat(Coat.Kind.GLOSS_PAINT, "#1e2023")),
                Map.entry(Group.LINKS, new Coat(Coat.Kind.GLOSS_PAINT, "#1e2023")),
                Map.entry(Group.CARRIERS, new Coat(Coat.Kind.GLOSS_PAINT, "#1e2023")),
                Map.entry(Group.SPRINGS, new Coat(Coat.Kind.RUBBER, "#2a2a2c")),
                Map.entry(Group.DAMPERS, new Coat(Coat.Kind.GUN_METAL)),
                Map.entry(Group.BRAKES, new Coat(Coat.Kind.STEEL)),
                Map.entry(Group.CALIPERS, new Coat(Coat.Kind.GLOSS_PAINT, "#b3261e")),
                Map.entry(Group.STEERING, new Coat(Coat.Kind.GLOSS_PAINT, "#1e2023")),
                Map.entry(Group.ENGINE, new Coat(Coat.Kind.GLOSS_PAINT, "#2b2d30")),
                Map.entry(Group.COVERS, new Coat(Coat.Kind.GLOSS_PAINT, "#2b2d30")),
                Map.entry(Group.BODYWORK, new Coat(Coat.Kind.METALLIC_PAINT, "#1d2a4c")),
                Map.entry(Group.GLASS, new Coat(Coat.Kind.GLASS, "#d6e2e6")),
                Map.entry(Group.ROOF, new Coat(Coat.Kind.GLOSS_PAINT, "#0f1215")),
                Map.entry(Group.TRIM, new Coat(Coat.Kind.RUBBER, "#1b1d20")),
                Map.entry(Group.LAMPS, new Coat(Coat.Kind.GLOSS_PAINT, "#ecebe4")),
                Map.entry(Group.TAIL_LAMPS, new Coat(Coat.Kind.GLOSS_PAINT, "#9e1218")),
                Map.entry(Group.SIGNALS, new Coat(Coat.Kind.GLOSS_PAINT, "#d97f1a")),
                Map.entry(Group.PLATES, new Coat(Coat.Kind.ENAMEL, "#f1f1ec")),
                Map.entry(Group.WHEELS, new Coat(Coat.Kind.GUN_METAL)),
                Map.entry(Group.TYRES, new Coat(Coat.Kind.RUBBER, "#1b1d20"))));
    }

    /** What {@code group}'s coated parts are coated in. */
    public Coat of(final Group group) {
        return coats.get(group);
    }

    /** What {@code part} is seen in, made of what {@code madeOf} says: its group's coat, or bare, its material. */
    public Coat of(final Part part, final MadeOf madeOf) {
        return part.made().bare() ? bare.get(madeOf.of(part.made().material())) : coats.get(part.group());
    }

    /** The same, {@code group}'s coated parts coated in {@code coat} instead. */
    public Finishes with(final Group group, final Coat coat) {
        final Map<Group, Coat> changed = new EnumMap<>(coats);
        changed.put(group, coat);
        return new Finishes(changed, bare);
    }
}
