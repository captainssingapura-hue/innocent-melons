package melons.innocent.glm.existences;

import com.example.fxsuite.taxonomy.Taxonomy;

import java.util.List;

/**
 * What exists in the GLM platform, as far as it needs it so far: the kinds of part an engine needs
 * ({@link EngineParts}), the GLM's own and the V2 motorcycle twin's.
 */
public record Existences() {

    /** The trees. */
    public List<Taxonomy> trees() {
        return List.of(new EngineParts().tree());
    }

    /** Everything wrong with any of them, each said with its tree's name; none if nothing is. */
    public List<String> problems() {
        return trees().stream().flatMap(tree -> tree.problems().stream().map(said -> tree.name() + ": " + said))
                .toList();
    }
}
