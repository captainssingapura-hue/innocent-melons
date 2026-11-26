package melons.innocent.core.rest;

import com.example.fxsuite.taxonomy.Leaf;
import melons.innocent.core.motion.Spot;

/**
 * A shape as the core engine reckons with it, of one kind of shape that physics knows: a leaf of the shapes' tree
 * (the physics aggregator's {@code Shapes}). The kind is in its type, so what is true of the kind across that tree,
 * that it rests on a face, tips, rolls or never moves, can be asked of any shape the engine has; and which kinds
 * the engine reckons with is told by which have a shape here: a box, an ellipsoid, a slab that never moves, and a
 * shell, a sheet that curves and never moves.
 *
 * @param <K> the kind of shape it is
 */
public sealed interface ShapeImpl<K extends Leaf<?>> permits Box, Ellipsoid, Slab, Shell {

    /** The kind of shape it is. */
    K kind();

    /** How much room it takes, in cubic metres. */
    double room();

    /** How far the furthest of it is from its middle, in centimetres. */
    double reaches();

    /**
     * How hard a thing of this shape weighing so many {@code kilograms} is to set turning about a line through its
     * middle, the line {@code along} lying as said in the shape's own frame and one long: in kilograms times
     * centimetres squared.
     */
    double turnsHard(Spot along, double kilograms);
}
