package com.example.fxsuite.vulkan.term;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An expression being written out as the shader's code: a line for each thing done, in the order it has to be done,
 * each given a name the lines after it use. The same thing done to the same things is written once, however often
 * it is used and however often it was said, so the code is as long as what it works out and no longer. What the
 * shader has to declare before any of it (a function of its own, for what the language has no word for) is gathered
 * alongside, each once.
 */
public final class Writing {

    /** A thing done, told from another by what is done and the names of what it is done to: not by going down it. */
    private record Done(Op op, List<String> of) {
    }

    private final Map<Node, String> met = new IdentityHashMap<>();
    private final Map<Done, String> names = new HashMap<>();
    private final List<String> lines = new ArrayList<>();
    /** Each thing declared, whole: its lines together, so two that share a line are still each whole. */
    private final Set<List<String>> declared = new LinkedHashSet<>();

    /** What {@code node} goes by in the code: a constant or what is given, itself; anything done, its line's name. */
    public String named(final Node node) {
        if (!(node instanceof Applied applied)) {
            return node.written(this);
        }
        final String known = met.get(node);
        if (known != null) {
            return known;
        }
        final List<String> of = applied.of().stream().map(this::named).toList();
        final String name = names.computeIfAbsent(new Done(applied.op(), of), done -> written(applied, of));
        met.put(node, name);
        return name;
    }

    /** The lines written so far, in order. */
    public List<String> lines() {
        return List.copyOf(lines);
    }

    /** What the shader has to declare before those lines: each thing once, whole, in the order it was first needed. */
    public List<String> declared() {
        return declared.stream().flatMap(List::stream).toList();
    }

    /** A line for {@code applied}, done to what goes by the names {@code of}: the name the line gives it. */
    private String written(final Applied applied, final List<String> of) {
        final String name = "t" + lines.size();
        lines.add((applied.size() == 1 ? "float " : "vec3 ") + name + " = " + applied.op().written(of) + ";");
        if (!applied.op().declared().isEmpty()) {
            declared.add(applied.op().declared());
        }
        return name;
    }
}
