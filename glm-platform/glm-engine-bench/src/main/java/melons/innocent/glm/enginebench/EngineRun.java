package melons.innocent.glm.enginebench;

import melons.innocent.glm.core.Material;

/**
 * How the engine on the bench is set up, as its window said it last: which of the range it is; how much slower than
 * it runs it is shown turning, so the eye can follow, or at its actual speed; whether it is turbocharged; how loud it
 * is; how its block is shown; and what its turning parts are made of. How it is driven is the driver's, at the card's
 * window, not the window's.
 *
 * @param engine which of the range, from nought
 * @param slow   how much slower than it runs it is shown turning, a share of its revs; one, at its actual speed
 * @param turbo  whether it is turbocharged
 * @param volume how loud it is, nought to one
 * @param block  how its block is shown
 * @param made   what its turning parts are made of, designed in titanium
 */
public record EngineRun(int engine, double slow, boolean turbo, double volume, Block block, Material made) {

    /** How the block is shown: as glass, so what turns inside is seen; finished; or not at all. */
    public enum Block {
        GLASS("The block and heads as glass"),
        FINISHED("The block and heads finished"),
        HIDDEN("The block and heads hidden");

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
     * The V6, turbocharged, shown a hundred times slower than it runs, its block as glass, its turning parts
     * titanium.
     */
    public EngineRun() {
        this(2, 0.01, true, 0.8, Block.GLASS, Material.TITANIUM);
    }
}
