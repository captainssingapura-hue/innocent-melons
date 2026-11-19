package com.example.fxsuite.bay.world;

import com.example.fxsuite.avatar.buildings.OfficeTower;
import com.example.fxsuite.avatar.sim.Demos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The bay a project of its own: told to the engine by itself, found by its name and offered, once; and its office, a few
 * office floors of the financial centre's first tower, held for it.
 */
class ProjectOfficeTest {

    @Test
    void theBayToldByItselfFoundByItsNameAndOfferedOnce() {
        Bay.tell();
        Bay.tell();
        assertSame(Bay.DEMO, Demos.named("MARINA_BAY"));
        assertEquals(1, Demos.playable().stream().filter(d -> d == Bay.DEMO).count(), "offered once");
    }

    @Test
    void itsOfficeOnOfficeFloorsOfTheFinancialCentresFirstTower() {
        final ProjectOffice office = ProjectOffice.BAY;
        assertEquals("MBFC Tower 1", office.tower().name());
        assertEquals(3, office.storeys().size(), "three floors");
        for (final int storey : office.storeys()) {
            assertTrue(storey > 0 && storey < office.tower().storeys(), "within the tower: " + storey);
            assertEquals(OfficeTower.OFFICE, office.tower().uses().get(storey), "an office floor: " + storey);
        }
        assertTrue(office.said().contains("reserved"));
    }
}
