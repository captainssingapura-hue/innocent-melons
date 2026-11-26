package melons.innocent.plants.walk;

/**
 * How far a forest's rings are drawn in to keep what is held on the card within a budget: a quality, one for the
 * rings as they are set and less as they are drawn in, every ring by the same share, so that detail is lowered
 * evenly and only far off. Over the budget, it comes down a little each time it is asked; well under it, it comes
 * back up more slowly, so that it settles and does not swing. And a hard edge a little over the budget, past which
 * nothing more is sent to the card: so a card asked for too much is drawn coarser, and never fails. No card.
 *
 * <p>The budget is megabytes held on the card by everything the run has put there: {@code -Dforest.budget=}, 2,000
 * unless said.
 */
final class Budgeted {

    private static final double DOWN = 0.92;
    private static final double UP = 1.03;
    private static final double WELL_UNDER = 0.8;
    private static final double EDGE = 1.1;
    private static final double LEAST = 0.3;

    private final double budget;
    private double quality = 1;

    /** A budget as the run says, or 2,000 MB where it says none. */
    Budgeted() {
        this(Double.parseDouble(System.getProperty("forest.budget", "2000")));
    }

    /** A budget of {@code megabytes}. */
    Budgeted(final double megabytes) {
        this.budget = megabytes;
    }

    /** The quality, eased toward what {@code held} megabytes on the card call for. */
    double eased(final double held) {
        if (held > budget) {
            quality = Math.max(LEAST, quality * DOWN);
        } else if (held < WELL_UNDER * budget) {
            quality = Math.min(1, quality * UP);
        }
        return quality;
    }

    /** Whether, {@code held} megabytes being on the card, nothing more is to be sent to it. */
    boolean full(final double held) {
        return held > EDGE * budget;
    }

    /** The budget, in megabytes. */
    double budget() {
        return budget;
    }

    /** The quality as it stands. */
    double quality() {
        return quality;
    }
}
