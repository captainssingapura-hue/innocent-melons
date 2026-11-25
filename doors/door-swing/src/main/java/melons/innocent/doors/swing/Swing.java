package melons.innocent.doors.swing;

/**
 * How a door swings: which side it is hung on, which way it opens, and how wide, all as one standing at its front
 * has it.
 *
 * @param hung   the side its hinges are on
 * @param opens  whether it is pushed away or pulled toward
 * @param widest how far it opens, in degrees from shut
 */
public record Swing(Hung hung, Opens opens, double widest) {

    public Swing {
        if (!(widest >= 30 && widest <= 180)) {
            throw new IllegalArgumentException("a door that opens wide enough to go through: " + widest);
        }
    }

    /** Hung so and opening so, a little past square to the wall. */
    public Swing(final Hung hung, final Opens opens) {
        this(hung, opens, 95);
    }

    /** One way or the other across the door: 1 where the leaf runs rightward from its hinges, -1 leftward. */
    public int runs() {
        return hung == Hung.LEFT ? 1 : -1;
    }

    /** One way or the other through the door: 1 where it opens away from its front, -1 toward it. */
    public int side() {
        return opens == Opens.AWAY ? 1 : -1;
    }
}
