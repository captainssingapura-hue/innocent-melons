package melons.innocent.physics.existences;

import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * What exists in physics, as far as the worlds need it: the shapes a thing that keeps its shape may have
 * ({@link Shapes}); whatever goes over a surface, by how it meets it ({@link Movers}); the forms a surface that is
 * moved over may have ({@link Surfaces}); and the state a surface is in at a spot ({@link Conditions}). The concepts
 * are beyond any one engine, which types what it reckons with to them.
 */
public record Existences() {

    /** The trees. */
    public List<Taxonomy> trees() {
        return List.of(new Shapes().tree(), new Movers().tree(), new Surfaces().tree(), new Conditions().tree());
    }

    /** Everything wrong with any of them, each said with its tree's name; none if nothing is. */
    public List<String> problems() {
        return trees().stream().flatMap(tree -> tree.problems().stream().map(said -> tree.name() + ": " + said))
                .toList();
    }
}
