package melons.innocent.glm.core;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A platform as it is made: its root, the part it is put together from, down its middle; and its connections, in the
 * order they are walked when it is put together ({@link Assembly}). Its parts are its root and every part a connection
 * joins.
 *
 * @param name        what it is
 * @param root        the part it is put together from
 * @param connections how its parts are joined
 */
public record Platform(String name, Part root, List<Connection> connections) {

    public Platform {
        connections = List.copyOf(connections);
        if (root == null || root.paired()) {
            throw new IllegalArgumentException(name + ": a root, down the middle");
        }
    }

    /** Its parts, each once: its root first, then each as its connections first name it. */
    public List<Part> parts() {
        final Set<Part> parts = new LinkedHashSet<>();
        parts.add(root);
        for (final Connection c : connections) {
            parts.add(c.carrier());
            parts.add(c.carried());
        }
        return new ArrayList<>(parts);
    }

    /** What is wrong with it as said, before it is put together: two parts of one name, or one joined twice alike. */
    public List<String> problems() {
        final List<String> wrong = new ArrayList<>();
        final List<Part> parts = parts();
        for (int i = 0; i < parts.size(); i++) {
            for (int j = i + 1; j < parts.size(); j++) {
                if (parts.get(i).name().equals(parts.get(j).name())) {
                    wrong.add("two parts called " + parts.get(i).name());
                }
            }
        }
        for (int i = 0; i < connections.size(); i++) {
            for (int j = i + 1; j < connections.size(); j++) {
                final Connection a = connections.get(i);
                final Connection b = connections.get(j);
                if (a.carrier() == b.carrier() && a.carried() == b.carried() && a.at().equals(b.at())
                        && a.to().equals(b.to())) {
                    wrong.add(a.carried().name() + " joined twice to " + a.carrier().name() + " at " + a.at());
                }
            }
        }
        return wrong;
    }
}
