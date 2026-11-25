package melons.innocent.glm.core;

/**
 * Where two parts are joined: the part it is carried by, nearer the platform's root, and its point there; the part it
 * carries and its point; and what kind of joint it is. Said as on the right: where either part is one of a pair, the
 * connection is one of a pair too, its left the right's mirror, a part down the middle met on the left at its point of
 * the same name on that side ({@link Side#named}). A point a part does not have is refused.
 *
 * @param name    what it is called
 * @param carrier the part it is carried by
 * @param at      its point on that part
 * @param carried the part it carries
 * @param to      its point on that part
 * @param joining what kind of joint it is
 */
public record Connection(String name, Part carrier, String at, Part carried, String to, Joining joining) {

    public Connection {
        if (name == null || carrier == null || carried == null || joining == null) {
            throw new IllegalArgumentException("a connection is named, between two parts, of some kind: " + name);
        }
        carrier.point(at);
        carried.point(to);
        if (carrier == carried) {
            throw new IllegalArgumentException(name + ": " + carrier.name() + " joined to itself");
        }
        final boolean pair = carrier.paired() || carried.paired();
        if (pair && !carrier.paired()) {
            carrier.point(Side.LEFT.named(at));
        }
        if (pair && !carried.paired()) {
            carried.point(Side.LEFT.named(to));
        }
    }

    /** Whether it is one of a pair, either of its parts being one of a pair. */
    public boolean paired() {
        return carrier.paired() || carried.paired();
    }
}
