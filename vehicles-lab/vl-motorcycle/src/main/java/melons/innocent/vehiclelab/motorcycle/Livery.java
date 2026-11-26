package melons.innocent.vehiclelab.motorcycle;

import melons.innocent.vehiclelab.forms.Coat;

import java.util.EnumMap;
import java.util.Map;

/**
 * How a motorcycle is finished, as its owner chose: a finish for every one of its slots ({@link Part.Slot}). The
 * boxer's own, to begin with, when nothing else is said: its frame gloss black; its engine titanium; its tank and
 * fenders a deep metallic blue; its seat black rubber; its forks, bars and pipes steel; its wheels gun metal; its
 * tyres rubber; its headlamp lit a warm white and its tail lamp red.
 *
 * @param coats each slot's finish
 */
public record Livery(Map<Part.Slot, Coat> coats) {

    public Livery {
        coats = Map.copyOf(coats);
        for (final Part.Slot slot : Part.Slot.values()) {
            if (!coats.containsKey(slot)) {
                throw new IllegalArgumentException("a finish for every slot, and none for " + slot);
            }
        }
    }

    /** The boxer's own. */
    public Livery() {
        this(Map.of(
                Part.Slot.FRAME, new Coat(Coat.Kind.GLOSS_PAINT, "#1c1e21"),
                Part.Slot.ENGINE, new Coat(Coat.Kind.TITANIUM),
                Part.Slot.TANK, new Coat(Coat.Kind.METALLIC_PAINT, "#1f4e8c"),
                Part.Slot.FENDERS, new Coat(Coat.Kind.METALLIC_PAINT, "#1f4e8c"),
                Part.Slot.SEAT, new Coat(Coat.Kind.RUBBER, "#2a2a2c"),
                Part.Slot.BRIGHT, new Coat(Coat.Kind.STEEL),
                Part.Slot.WHEELS, new Coat(Coat.Kind.GUN_METAL),
                Part.Slot.TYRES, new Coat(Coat.Kind.RUBBER, "#1b1d20"),
                Part.Slot.HEADLAMP, new Coat(Coat.Kind.LAMP, "#fff4cf"),
                Part.Slot.TAIL_LAMP, new Coat(Coat.Kind.LAMP, "#e0342b")));
    }

    /** What {@code slot} is finished in. */
    public Coat coat(final Part.Slot slot) {
        return coats.get(slot);
    }

    /** The same, {@code slot} finished in {@code coat} instead. */
    public Livery with(final Part.Slot slot, final Coat coat) {
        final Map<Part.Slot, Coat> changed = new EnumMap<>(coats);
        changed.put(slot, coat);
        return new Livery(changed);
    }
}
