package melons.innocent.glm.model;

import melons.innocent.glm.core.Material;

import java.util.EnumMap;
import java.util.Map;

/**
 * What the platform's parts are made of, where another material is put in place of the one they are designed in:
 * titanium wherever aluminium is, say. Every part designed in a material is then made of the other, so weighs and
 * bears as that does, and where its skin is bare, looks it. Only a metal is put in a metal's place. To begin with,
 * each as designed.
 *
 * @param instead the material put in each one's place
 */
public record MadeOf(Map<Material, Material> instead) {

    public MadeOf {
        instead = Map.copyOf(instead);
        instead.forEach((designed, made) -> {
            if (designed.metal() != made.metal()) {
                throw new IllegalArgumentException("only a metal in a metal's place: " + made.title() + " for "
                        + designed.title());
            }
        });
    }

    /** Each as designed. */
    public MadeOf() {
        this(Map.of());
    }

    /** What is made of where {@code designed} is designed. */
    public Material of(final Material designed) {
        return instead.getOrDefault(designed, designed);
    }

    /** The same, {@code made} put where {@code designed} is. */
    public MadeOf with(final Material designed, final Material made) {
        final Map<Material, Material> changed = new EnumMap<>(Material.class);
        changed.putAll(instead);
        if (designed == made) {
            changed.remove(designed);
        } else {
            changed.put(designed, made);
        }
        return new MadeOf(changed);
    }
}
