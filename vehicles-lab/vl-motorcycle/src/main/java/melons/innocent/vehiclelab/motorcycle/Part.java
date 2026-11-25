package melons.innocent.vehiclelab.motorcycle;

import com.example.fxsuite.vulkan.form.Mesh;

/**
 * One part of a motorcycle: what it is called, which of the bike's finishes it is in ({@link Slot}), what it turns
 * with ({@link Moves}), and its form, in the bike's own frame: across as x, up from the ground as y, along as z.
 *
 * @param name  what it is
 * @param slot  which of the bike's finishes it is in
 * @param moves what it turns with
 * @param form  its form
 */
public record Part(String name, Slot slot, Moves moves, Mesh form) {

    /** Which of the bike's finishes a part is in: each chosen by its owner, every part in it alike. */
    public enum Slot {
        FRAME("Frame"),
        ENGINE("Engine"),
        TANK("Tank and side cover"),
        FENDERS("Fenders"),
        SEAT("Seat"),
        BRIGHT("Bright work: forks, bars, pipes"),
        WHEELS("Wheels"),
        TYRES("Tyres and grips"),
        HEADLAMP("Headlamp"),
        TAIL_LAMP("Tail lamp");

        private final String title;

        Slot(final String title) {
            this.title = title;
        }

        /** What it is called. */
        public String title() {
            return title;
        }
    }

    /** What a part turns with. */
    public enum Moves {
        /** The bike itself. */
        BODY,
        /** The forks, turned about the head tube as the bars are turned. */
        STEERED,
        /** The front wheel: steered with the forks, and rolling about its axle. */
        FRONT_WHEEL,
        /** The back wheel, rolling about its axle. */
        BACK_WHEEL
    }

    public Part {
        if (name == null || slot == null || moves == null || form == null) {
            throw new IllegalArgumentException("a part is named, finished, moves some way and has a form: " + name);
        }
    }
}
