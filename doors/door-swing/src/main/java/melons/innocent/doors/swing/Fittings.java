package melons.innocent.doors.swing;

import com.example.fxsuite.texture.materials.Material;
import com.example.fxsuite.texture.materials.Materials;

import java.util.function.Supplier;

/** What a door's metal is: its hinges, its handle, and a glass door's patches. Each is one of the lab's metals. */
public enum Fittings {

    /** Bright steel. */
    STEEL(Materials.Steel::new),
    /** Bronze, as old door furniture is. */
    BRONZE(Materials.Bronze::new),
    /** Dark gunmetal. */
    GUNMETAL(Materials.Gunmetal::new);

    private final Supplier<Material> leaf;

    Fittings(final Supplier<Material> leaf) {
        this.leaf = leaf;
    }

    /** The lab's material it is drawn as. */
    public Material leaf() {
        return leaf.get();
    }
}
