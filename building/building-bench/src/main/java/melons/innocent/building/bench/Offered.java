package melons.innocent.building.bench;

import com.example.fxsuite.building.Spec;
import com.example.fxsuite.building.Structure;
import com.example.fxsuite.building.ThingImpl;
import com.example.fxsuite.taxonomy.Leaf;

/**
 * A thing the bench offers to build: how it is built, and a specification to build it from at first.
 *
 * @param impl   how it is built
 * @param sample what it is built from at first
 */
public record Offered(ThingImpl<?, ?, ?, ?> impl, Spec<?, ?, ?> sample) {

    /** The thing it builds, a leaf of the taxonomy of what is made. */
    public Leaf<?> thing() {
        return impl.thing();
    }

    /** It built from {@code spec}, a specification of the kind its sample is. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Structure built(final Spec<?, ?, ?> spec) {
        return ((ThingImpl) impl).build(spec);
    }

    /** What it is called: the thing, and whose way and when it is built. */
    public String title() {
        return impl.thing().title() + ", " + impl.civilization().getClass().getSimpleName() + " "
                + impl.era().getClass().getSimpleName();
    }
}
