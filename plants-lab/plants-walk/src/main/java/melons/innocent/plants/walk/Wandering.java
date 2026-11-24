package melons.innocent.plants.walk;

import java.util.Random;

/**
 * How a road strays sideways as it goes on, a step at a time: a random walk whose volatility is itself random, as
 * the models of prices have it. The way is taken in sections of so many steps. Each section is first given a drift,
 * a lean to one side or the other that it keeps for its length; each step then goes sideways by its section's
 * drift and by a chance amount, and how large the chance amounts are is the road's volatility, one for the whole
 * road, which wanders too: its logarithm is carried on from each step to the next, a little nearer its usual value
 * and off again by chance, so the road has calm stretches and twisting ones, and comes from one to the other by
 * degrees. A step never goes further sideways than {@link #STEEPEST} of its length, so the road always goes on.
 *
 * <p>It is not the same road twice unless it is given the same chances: it is made as it is travelled and not
 * before. Its figures are their author's.
 */
public final class Wandering {

    /** How long a step is along the road's way, in centimetres, and how many steps a section is. */
    static final double STEP = 2_000;
    static final int SECTION = 5;
    /** The most a step goes sideways, of its length. */
    static final double STEEPEST = 0.8;
    /** How far a section's drift is usually from none, as so much sideways for each step's length along. */
    private static final double DRIFT = 0.2;
    /** How far a step usually strays by chance when the road's volatility is its usual, in centimetres. */
    private static final double USUAL = 150;
    /** How much of its distance from its usual the volatility's logarithm keeps each step, and how far it is moved. */
    private static final double KEPT = 0.95;
    private static final double MOVED = 0.25;

    private final Random chance;
    private double drift;
    private double moodiness;
    private long steps;

    /** A road straying by {@code chance}. */
    public Wandering(final Random chance) {
        this.chance = chance;
    }

    /** A road straying by chance nobody chose: no two alike. */
    public Wandering() {
        this(new Random());
    }

    /** How far sideways the next step goes, in centimetres: its section given its drift first, if it begins one. */
    public double stepped() {
        if (steps % SECTION == 0) {
            drift = chance.nextGaussian() * DRIFT;
        }
        steps++;
        moodiness = KEPT * moodiness + MOVED * chance.nextGaussian();
        final double strayed = drift * STEP + volatility() * chance.nextGaussian();
        return Math.clamp(strayed, -STEEPEST * STEP, STEEPEST * STEP);
    }

    /** The drift of the section it is in: so much sideways for each step's length along. */
    public double drift() {
        return drift;
    }

    /** Its volatility now: how far a step usually strays by chance, in centimetres. */
    public double volatility() {
        return USUAL * Math.exp(moodiness);
    }
}
