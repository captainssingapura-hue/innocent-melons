package com.example.fxsuite.avatar;

import com.example.fxsuite.avatar.sim.Demos;
import com.example.fxsuite.avatar.worlds.Meadow;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The worlds designed outside the simulation told of it when asked, as the launcher asks before it looks any demo up
 * by name or offers them: the meadow village then found by its name, and offered, once. Marina Bay, a project of its
 * own, is not the engine's to tell: it tells of itself.
 */
class WorldsToldTest {

    @Test
    void theMeadowFoundByItsNameAndOfferedOnceTheWorldsAreTold() {
        Game.tellWorlds();
        Game.tellWorlds();
        assertSame(Meadow.DEMO, Demos.named("VILLAGE"));
        assertTrue(Demos.playable().contains(Meadow.DEMO));
        assertTrue(Demos.playable().stream().filter(d -> d == Meadow.DEMO).count() == 1, "once");
        assertNull(Demos.named("MARINA_BAY"), "the bay not the engine's to tell");
    }
}
