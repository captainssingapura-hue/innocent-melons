package melons.innocent.physics.existences;

import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * What exists in physics, as far as the worlds need it: the one tree so far, the shapes a thing that keeps its shape
 * may have ({@link Shapes}). The concepts are beyond any one engine, which types what it reckons with to them.
 */
public record Existences() {

    /** The trees. */
    public List<Taxonomy> trees() {
        return List.of(new Shapes().tree());
    }

    /** Everything wrong with any of them, each said with its tree's name; none if nothing is. */
    public List<String> problems() {
        return trees().stream().flatMap(tree -> tree.problems().stream().map(said -> tree.name() + ": " + said))
                .toList();
    }
}
