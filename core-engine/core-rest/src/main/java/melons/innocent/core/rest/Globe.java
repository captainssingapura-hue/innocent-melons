package melons.innocent.core.rest;

/**
 * A globe a world may be on, by how hard it pulls at its surface.
 */
public enum Globe {

    /** The Earth: 9.81 metres a second each second. */
    EARTH(981),
    /** The Moon: a sixth of the Earth's. */
    MOON(162),
    /** Mars: three eighths of the Earth's. */
    MARS(372);

    private final double pull;

    Globe(final double pull) {
        this.pull = pull;
    }

    /** How hard it pulls a thing down at its surface, in centimetres a second each second. */
    public double pull() {
        return pull;
    }
}
