package melons.innocent.core.rest;

/**
 * What the world found in the path of a thing it had just stepped: the thing moved, as its engine said it would,
 * from where it was to where it would be, and something stood in that path. It is found after the regular tick,
 * by the world and not by the thing's engine, and is handed back to that engine to correct what it worked out
 * ({@link Simulator#correctionS1}).
 *
 * @param thing what was moving
 * @param with  what stood in its path
 * @param share how far along its path it had come when it met it: nothing at its start, one at its end
 * @param there where the thing was then, as near its path's start as the meeting was found to be
 * @param touch where the two met: the spot, which way the thing is pushed there, and how what it met is going
 */
public record Collision(String thing, String with, double share, Placed there, Touch touch) {
}
