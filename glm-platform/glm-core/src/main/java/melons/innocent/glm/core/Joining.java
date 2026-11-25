package melons.innocent.glm.core;

/**
 * What kind of joint a connection is: kept so that how the parts may move on one another can be worked out later; at
 * rest, every kind holds its two parts where they are.
 */
public enum Joining {
    /** Welded: one piece with the other. */
    WELDED,
    /** Bolted hard to the other. */
    BOLTED,
    /** Bolted through a rubber bush, so it gives a little every way. */
    BUSHED,
    /** On a ball joint, so it turns every way about the point. */
    BALL_JOINT,
    /** On a hinge or a bearing, so it turns about one line. */
    HINGE,
    /** On splines, so it turns with the other and slides along it. */
    SPLINED,
    /** Seated on the other, as a tyre is on its rim. */
    SEATED,
    /** Sliding along a line in the other, as a piston does in its bore, its point on that line. */
    SLIDING
}
