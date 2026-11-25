package melons.innocent.doors.walk;

import com.example.fxsuite.avatar.sim.Block;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.core.mechanism.Swung;
import melons.innocent.doors.swing.Stood;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Doors standing somewhere, each by its name, as they are from tick to tick: how far each has swung, and whether it
 * is wanted open or shut. Worked, a door is wanted the other way from now on. Each tick every door's hinge is a
 * step on; a leaf that would swing into whoever walks there waits for them. No card is in it: what is drawn and
 * what is in the way are asked of it.
 */
public final class Doors {

    private static final double TICK = 1.0 / 120;
    /** How much room whoever walks takes up to every side, in centimetres. */
    private static final double ROOM = 32;

    /** A door by its name, where it stands. */
    public record Doorway(String name, Stood stood) {
    }

    private final List<Doorway> doorways;
    private final List<Swung> swung = new ArrayList<>();
    private final List<Boolean> open = new ArrayList<>();
    /** How many times what is in the way has changed: a leaf moved. */
    private long changes;

    /** These doors, all shut. */
    public Doors(final List<Doorway> doorways) {
        this.doorways = List.copyOf(doorways);
        for (int door = 0; door < this.doorways.size(); door++) {
            swung.add(new Swung());
            open.add(false);
        }
    }

    /** The doors, in the order they were given. */
    public List<Doorway> doorways() {
        return doorways;
    }

    /** The door called {@code name} worked: wanted open if it was wanted shut, shut if open. */
    public void worked(final String name) {
        final int door = numbered(name).orElseThrow(() -> new IllegalArgumentException("no door is called " + name));
        open.set(door, !open.get(door));
    }

    /** A tick: every door a step on, a leaf waiting where someone at {@code walker} stands in its way. */
    public void ticked(final Vec3 walker) {
        for (int door = 0; door < doorways.size(); door++) {
            final Stood stood = doorways.get(door).stood();
            final Swung now = swung.get(door);
            final Swung next = stood.door().hinged().stepped(now, open.get(door),
                    stood.sweeps(now, open.get(door), walker, ROOM), TICK);
            if (!next.equals(now)) {
                swung.set(door, next);
                changes += next.degrees() == now.degrees() ? 0 : 1;
            }
        }
    }

    /** How far the door numbered {@code door} has swung. */
    public Swung swung(final int door) {
        return swung.get(door);
    }

    /** Whether the door numbered {@code door} is wanted open. */
    public boolean open(final int door) {
        return open.get(door);
    }

    /** Which door is called {@code name}; none, where none is. */
    public Optional<Integer> numbered(final String name) {
        for (int door = 0; door < doorways.size(); door++) {
            if (doorways.get(door).name().equals(name)) {
                return Optional.of(door);
            }
        }
        return Optional.empty();
    }

    /** What of every door is in the way, as each stands now. */
    public List<Block> inTheWay() {
        final List<Block> blocks = new ArrayList<>();
        for (int door = 0; door < doorways.size(); door++) {
            blocks.addAll(doorways.get(door).stood().inTheWay(swung.get(door)));
        }
        return blocks;
    }

    /** How many times what is in the way has changed since they were made. */
    public long changes() {
        return changes;
    }

    /** How the door numbered {@code door} stands, in words. */
    public String said(final int door) {
        final Swung now = swung.get(door);
        final String how = now.moving() ? (open.get(door) ? "opening" : "shutting")
                : now.degrees() == 0 ? "shut" : open.get(door) == (now.degrees() > 0)
                && now.degrees() == doorways.get(door).stood().door().swing().widest() ? "open" : "waiting";
        return String.format(Locale.ENGLISH, "%s: %s, %.0f degrees", doorways.get(door).name(), how,
                now.degrees());
    }
}
