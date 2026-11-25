package melons.innocent.core.interaction;

/**
 * How the one acting stands to a thing: what can be done with a thing depends first on that. A watermelon lying on a
 * table offers one set of things to do, the same watermelon held another.
 */
public enum Relation {

    /** The thing is at hand, on its own in the world. */
    NEAR,
    /** The thing is in the hands of the one acting. */
    HELD,
    /** The one acting is in the thing. */
    INSIDE,
    /** The one acting has the thing's controls. */
    OPERATING
}
