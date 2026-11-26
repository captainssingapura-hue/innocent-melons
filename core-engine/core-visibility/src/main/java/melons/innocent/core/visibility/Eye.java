package melons.innocent.core.visibility;

/**
 * What an observer's eye can do: the finest angle it tells apart when nothing moves; for how long it gathers what
 * it sees, over which whatever sweeps across its view is smeared; and how fast it must be going for it to make out,
 * of everything together, half of what it makes out standing still.
 *
 * <p>A person's eye tells apart about a sixtieth of a degree. The other two are not figures of nature here but the
 * ones to set by eye against a picture in motion: a hundred-and-twentieth of a second, and a hundred and fifty
 * kilometres an hour, unless said.
 *
 * @param finest  the finest angle told apart with everything still, in degrees
 * @param gathers how long it gathers what it sees, in seconds: nothing, for an eye that no sweep ever blurs
 * @param halved  the speed at which it makes out half as much of everything, in centimetres a second: no speed at
 *                all, for an eye that going fast costs nothing
 */
public record Eye(double finest, double gathers, double halved) {

    /** A hundred and fifty kilometres an hour, in centimetres a second. */
    private static final double FAST = 150 * 100_000.0 / 3_600;

    public Eye {
        if (!(finest > 0) || !(gathers >= 0) || !(halved > 0)) {
            throw new IllegalArgumentException("an eye telling apart some angle, gathering for some while or none, "
                    + "making out half as much at some speed: " + finest + ", " + gathers + ", " + halved);
        }
    }

    /** A person's eye, gathering for a hundred-and-twentieth of a second, halved at 150 kilometres an hour. */
    public Eye() {
        this(1.0 / 60, 1.0 / 120, FAST);
    }
}
