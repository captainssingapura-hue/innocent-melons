package melons.innocent.core.interaction;

/**
 * An action resolved: this action, done with this thing. It is what reaches a world, never a key or a choice from a
 * list; so whatever a use came of, a key, a click or a script, the world is told the same.
 *
 * @param target the name of the thing
 * @param action the action done with it
 */
public record Use(String target, Action action) {
}
