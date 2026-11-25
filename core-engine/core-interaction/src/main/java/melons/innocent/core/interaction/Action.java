package melons.innocent.core.interaction;

import java.util.Optional;

/**
 * The actions there are: what can be done with a thing, each with a name that never changes, by which a use of it is
 * told and kept, and with what it is called to one near the thing and to one holding it; not offered, where it is
 * called nothing. Carrying is one action seen two ways: near, a thing is taken up; held, it is put down.
 *
 * <p>Which of them can be done with a kind of thing is not said here, nor by the kind: it is bound to the kind apart
 * ({@link Bindings}).
 */
public enum Action {

    /** Carried: taken up into the hands, and put down from them. */
    CARRY("carry", "Carry", "Put down"),
    /** Cut into pieces, lying where it is. */
    CUT("cut", "Cut", ""),
    /** Crushed: broken where it lies or in the hands, and gone. */
    CRUSH("crush", "Crush", "Crush"),
    /** Thrown from the hands, the way one faces. */
    THROW("throw", "", "Throw"),
    /** Eaten, and gone. */
    EAT("eat", "Eat", "Eat"),
    /** Opened, or shut again. */
    OPEN("open", "Open or shut", "");

    private final String id;
    private final String near;
    private final String held;

    Action(final String id, final String near, final String held) {
        this.id = id;
        this.near = near;
        this.held = held;
    }

    /** Its name, which never changes: what a use of it is told and kept by. */
    public String id() {
        return id;
    }

    /** What it is called to one who stands so to the thing; nothing, where it is not offered to them. */
    public Optional<String> offered(final Relation relation) {
        final String label = relation == Relation.NEAR ? near : relation == Relation.HELD ? held : "";
        return label.isEmpty() ? Optional.empty() : Optional.of(label);
    }
}
