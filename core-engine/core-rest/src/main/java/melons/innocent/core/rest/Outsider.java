package melons.innocent.core.rest;

import melons.innocent.core.motion.Spot;

/**
 * A thing that is in the world for a tick and is not the world's to step: the game engine's avatar, until walking
 * is the core's own. Each tick the world is told where it is and how it is going, and it is there as a box. The
 * world moves it nowhere and nothing moves it; whatever is in motion may find it in its path.
 *
 * @param box    how big it is
 * @param placed where its middle is this tick, and how it is turned
 * @param going  how it is going over the ground, in centimetres a second
 */
public record Outsider(Box box, Placed placed, Spot going) {
}
