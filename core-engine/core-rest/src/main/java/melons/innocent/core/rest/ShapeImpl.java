package melons.innocent.core.rest;

import com.example.fxsuite.taxonomy.Leaf;

/**
 * A shape as the core engine reckons with it, of one kind of shape that physics knows: a leaf of the shapes' tree
 * (the physics aggregator's {@code Shapes}). The kind is in its type, so what is true of the kind across that tree,
 * that it rests on a face, tips or rolls, can be asked of any shape the engine has; and which kinds the engine
 * reckons with is told by which have a shape here.
 *
 * @param <K> the kind of shape it is
 */
public interface ShapeImpl<K extends Leaf<?>> {

    /** The kind of shape it is. */
    K kind();

    /** How much room it takes, in cubic metres. */
    double room();
}
