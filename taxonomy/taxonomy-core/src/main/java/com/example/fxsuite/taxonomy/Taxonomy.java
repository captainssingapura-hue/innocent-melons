package com.example.fxsuite.taxonomy;

import hue.captains.singapura.tao.ontology.FunctionalObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A taxonomy, read: a root, the leaves listed under it, the branches between them found by following each leaf's
 * parents, and what each node means. Read once; everything wrong with it that the compiler could not refuse is found
 * all at once ({@link #problems}). Nothing is found by scanning: what is in it is what is listed.
 */
public final class Taxonomy implements FunctionalObject {

    private final String name;
    private final L0 root;
    private final List<Node> nodes;
    private final List<Leaf<?>> leaves;
    private final Map<String, String> meanings;
    private final List<String> problems;

    /**
     * The tree called {@code name} under {@code root}: its {@code leaves} as listed, its branches those their parents
     * lead through, its nodes' {@code meanings} by their titles ({@link Meanings}).
     */
    public Taxonomy(final String name, final L0 root, final List<? extends Leaf<?>> leaves,
                    final Map<String, String> meanings) {
        final List<String> problems = new ArrayList<>();
        final Set<Node> branches = new LinkedHashSet<>(List.of(root));
        final Set<Leaf<?>> listed = new LinkedHashSet<>();
        for (final Leaf<?> leaf : leaves) {
            if (!listed.add(leaf)) {
                problems.add(leaf.title() + ": listed twice");
                continue;
            }
            final List<Node> lineage = leaf.lineage();
            final Node top = lineage.getLast();
            if (!top.equals(root)) {
                problems.add(leaf.title() + ": its lineage ends at " + (top instanceof L0 ? top.title() + ", another"
                        + " tree's root" : top.title() + ", which has no parent"));
            }
            for (final Node branch : lineage.subList(1, lineage.size()).reversed()) {
                branches.add(branch);
            }
        }
        final List<Node> nodes = new ArrayList<>(branches);
        nodes.addAll(listed);
        problems.addAll(unsaid(nodes, meanings));
        this.name = name;
        this.root = root;
        this.nodes = List.copyOf(nodes);
        this.leaves = List.copyOf(listed);
        this.meanings = Map.copyOf(meanings);
        this.problems = List.copyOf(problems);
    }

    /** What is wrong with {@code nodes}' names and meanings: a name used twice, a node or a meaning on its own. */
    private List<String> unsaid(final List<Node> nodes, final Map<String, String> meanings) {
        final List<String> problems = new ArrayList<>();
        final Map<String, Node> named = new LinkedHashMap<>();
        for (final Node node : nodes) {
            final Node other = named.putIfAbsent(node.name(), node);
            if (other != null) {
                problems.add("two nodes named " + node.name() + ": " + other.getClass().getName() + " and "
                        + node.getClass().getName());
            }
            if (meanings.getOrDefault(node.title(), "").isBlank()) {
                problems.add(node.title() + ": says nothing of what it means");
            }
        }
        final Set<String> titles = new LinkedHashSet<>(nodes.stream().map(Node::title).toList());
        meanings.keySet().stream().filter(title -> !titles.contains(title))
                .forEach(title -> problems.add("a meaning for " + title + ", which is no node of this tree"));
        return problems;
    }

    /** What it is called. */
    public String name() {
        return name;
    }

    public L0 root() {
        return root;
    }

    /** Every node: its root, its branches as they are come to, its leaves as listed. */
    public List<Node> nodes() {
        return nodes;
    }

    /** Its leaves, as listed: the concrete kinds. */
    public List<Leaf<?>> leaves() {
        return leaves;
    }

    /** Its branches, its root among them. */
    public List<Branch> branches() {
        return nodes.stream().filter(Branch.class::isInstance).map(Branch.class::cast).toList();
    }

    /** The leaves under {@code branch}, however far under. */
    public List<Leaf<?>> under(final Branch branch) {
        return leaves.stream().filter(leaf -> leaf.lineage().contains(branch)).toList();
    }

    /** Whether {@code node} is one of its nodes. */
    public boolean has(final Node node) {
        return nodes.contains(node);
    }

    /** Its node called {@code name} ({@link Node#name}), if it has one. */
    public Optional<Node> named(final String name) {
        return nodes.stream().filter(node -> node.name().equals(name)).findFirst();
    }

    /** What {@code node} means, as written beside the tree; nothing if nothing is. */
    public String meaning(final Node node) {
        return meanings.getOrDefault(node.title(), "");
    }


    /** Everything wrong with it, all of it; none if nothing is. */
    public List<String> problems() {
        return problems;
    }
}
