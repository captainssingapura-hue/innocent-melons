package melons.innocent.glm.enginebench;

import melons.innocent.glm.core.Material;

/**
 * How the engine on the bench is run, as its window said it last: which of the range it is; whether it revs free on
 * its throttle or is held at the revs said; the throttle, the revs held and the load; how much slower than it runs
 * it is shown turning, so the eye can follow; whether it is turbocharged; how loud it is; how its block is shown;
 * and what its turning parts are made of.
 *
 * @param engine   which of the range, from nought
 * @param free     whether it revs free on its throttle, or is held at {@code rpm}
 * @param throttle how far its throttle is open, nought to one
 * @param rpm      the revs it is held at
 * @param load     how hard it is working, nought to one
 * @param slow     how much slower than it runs it is shown turning, a share of its revs
 * @param turbo    whether it is turbocharged
 * @param volume   how loud it is, nought to one
 * @param block    how its block is shown
 * @param made     what its turning parts are made of, designed in titanium
 */
public record EngineRun(int engine, boolean free, double throttle, double rpm, double load, double slow,
                        boolean turbo, double volume, Block block, Material made) {

    /** How the block is shown: as glass, so what turns inside is seen; finished; or not at all. */
    public enum Block {
        GLASS("The block as glass"),
        FINISHED("The block finished"),
        HIDDEN("The block hidden");

        private final String title;

        Block(final String title) {
            this.title = title;
        }

        /** What it is called. */
        public String title() {
            return title;
        }
    }

    /**
     * The V6 at idle, turbocharged, shown a hundred times slower than it runs, its block as glass, its turning parts
     * titanium.
     */
    public EngineRun() {
        this(2, true, 0, 2000, 0, 0.01, true, 0.8, Block.GLASS, Material.TITANIUM);
    }
}
