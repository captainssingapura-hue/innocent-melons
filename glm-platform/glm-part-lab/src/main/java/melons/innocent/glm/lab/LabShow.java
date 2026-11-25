package melons.innocent.glm.lab;

import melons.innocent.glm.core.PartAt;
import melons.innocent.glm.model.MadeOf;

/**
 * What the lab shows, as its window said it last: which part is looked at, and whether alone or put together with
 * the rest; the rest, put together, as glass, finished, hidden, or all hidden but the part's own group; and whether
 * the grid, the ground and the part's points are shown, and how finely the grid is ruled; and what the parts are
 * made of.
 *
 * @param part    the part looked at
 * @param level   alone, or put together
 * @param others  how the rest are shown, put together
 * @param grid    whether the grid is shown
 * @param ground  whether the ground is shown
 * @param points  whether the part's points are marked
 * @param spacing how finely the grid is ruled
 * @param madeOf  what the parts are made of
 */
public record LabShow(PartAt part, Level level, Others others, boolean grid, boolean ground, boolean points,
                      Spacing spacing, MadeOf madeOf) {

    /** Whether the part is looked at alone, in its own frame, or put together with the rest. */
    public enum Level {
        PART("The part alone, in its own frame"),
        ASSEMBLY("Put together with the rest");

        private final String title;

        Level(final String title) {
            this.title = title;
        }

        /** What it is called. */
        public String title() {
            return title;
        }
    }

    /** How the rest are shown, put together. */
    public enum Others {
        GHOST("The rest as glass"),
        FINISHED("The rest finished"),
        GROUP("Its group finished, the rest hidden"),
        HIDDEN("The rest hidden");

        private final String title;

        Others(final String title) {
            this.title = title;
        }

        /** What it is called. */
        public String title() {
            return title;
        }
    }

    public LabShow {
        if (part == null || level == null || others == null || spacing == null || madeOf == null) {
            throw new IllegalArgumentException("a part looked at, somehow, its rest shown somehow, a grid ruled");
        }
    }

    /** {@code part} alone, in the grid ruled every 10 cm, its points marked, on the ground, made as designed. */
    public LabShow(final PartAt part) {
        this(part, Level.PART, Others.GHOST, true, true, true, Spacing.NORMAL, new MadeOf());
    }
}
