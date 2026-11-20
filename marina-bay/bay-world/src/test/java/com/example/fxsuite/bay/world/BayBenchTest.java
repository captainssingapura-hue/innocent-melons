package com.example.fxsuite.bay.world;

import com.example.fxsuite.avatar.buildings.OfficeTower;
import com.example.fxsuite.avatar.build.Solid;
import com.example.fxsuite.avatar.lifts.Built;
import com.example.fxsuite.bay.buildings.Orq;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A tower shown on its own on the bay's bench: whole, with every landing's doors standing shut; cut away over a
 * storey, nothing of its landings' doors over the floor it is cut under, what it draws as it was built so.
 */
class BayBenchTest {

    @Test
    void wholeItsEveryLandingsDoorsStandShut() {
        final OfficeTower tower = Orq.south();
        final Built built = tower.built(tower.storeys(), true);
        final List<Solid> still = BayBench.still(tower, built, tower.storeys());
        assertEquals(built.drawn().size() + tower.landingsShut().size(), still.size());
    }

    @Test
    void cutAwayOverAStoreyNothingOfItsLandingsDoorsOverTheFloorItIsCutUnder() {
        final OfficeTower tower = Orq.south();
        final int upTo = 12;
        final Built built = tower.built(upTo, false);
        final double cut = tower.levels().get(upTo + 1);
        final List<Solid> still = BayBench.still(tower, built, upTo);
        final List<Solid> doors = still.subList(built.drawn().size(), still.size());
        assertTrue(!doors.isEmpty() && doors.size() < tower.landingsShut().size(),
                "some of its landings' doors, not all: " + doors.size() + " of " + tower.landingsShut().size());
        assertTrue(doors.stream().allMatch(s -> s.corners().stream().allMatch(p -> p.up() <= cut + 1)),
                "none over the floor it is cut under, " + cut / 100 + " m");
        assertTrue(built.drawn().stream().allMatch(s -> s.corners().stream().allMatch(p -> p.up() <= cut + 1)),
                "nothing it draws over that floor");
    }
}
