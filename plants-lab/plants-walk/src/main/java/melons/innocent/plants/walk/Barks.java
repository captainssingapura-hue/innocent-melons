package melons.innocent.plants.walk;

import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;
import melons.innocent.plants.tree.Kind;

/**
 * Which of the material lab's barks each kind of tree wears. A plant makes no material of its own: its bark is one
 * of the lab's leaves, asked for here, the one place a kind of tree and its bark are put together.
 */
public record Barks() {

    /** The bark a tree of {@code kind} wears. */
    public Material of(final Kind kind) {
        return switch (kind) {
            case OAK -> new Materials.OakBark();
            case WALNUT -> new Materials.WalnutBark();
            case BIRCH -> new Materials.BirchBark();
        };
    }
}
