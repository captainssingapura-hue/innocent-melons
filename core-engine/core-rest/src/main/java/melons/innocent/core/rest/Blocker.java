package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;

/**
 * Something that may stand in the path a thing took in a tick: an outsider, another thing in motion, a thing at
 * rest. It is where it was at the tick's start and where it is at its end, so a thing that is itself moving is
 * met where the two truly were at the same moment.
 *
 * @param shape its shape
 * @param from  where it was when the tick began
 * @param to    where it is at the tick's end, as its own engine has just put it; the same, where it stands
 * @param going how what meets it is to go against it: its own going, where nothing moves it; where it can be moved,
 *              how the two would go on together, which is what both are brought to along the line they meet on
 */
record Blocker(ShapeImpl<?> shape, Placed from, Placed to, Spot going) {
}
