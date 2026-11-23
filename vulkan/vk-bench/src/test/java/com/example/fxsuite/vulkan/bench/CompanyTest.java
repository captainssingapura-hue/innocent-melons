package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Vec3;
import com.example.fxsuite.vulkan.bench.play.Scripted;
import com.example.fxsuite.vulkan.bench.subject.Bot;
import com.example.fxsuite.vulkan.bench.subject.Build;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A walk's company, with no card and no window. A start is read as metres across and along and degrees round, and
 * what cannot be read is refused. A companion goes step for step with its lead and stands where its own start puts
 * it, so the same script walked from two starts ends as far apart as it began; one whose script ends first stands
 * still while the lead goes on, and the walk is over when the lead's run is. Only a companion is drawn as another.
 */
class CompanyTest {

    @Test
    void aStartIsMetresAcrossAndAlongAndDegreesRoundAndWhatCannotBeReadIsRefused() {
        final Body start = new Start("68, -10, 180").body();
        assertEquals(new Vec3(6800, 0, -1000), start.position());
        assertEquals(180, start.yaw(), 1e-12);
        assertEquals(new Vec3(0, 0, 0), new Start("").body().position(), "blank, the world's middle");
        assertThrows(IllegalArgumentException.class, () -> new Start("68,-10").body());
        assertThrows(NumberFormatException.class, () -> new Start("a,b,c").body());
    }

    @Test
    void aCompanionGoesStepForStepWithItsLeadFromItsOwnStart() {
        final Company company = company("W:2;@40:1;W:1", "W:2;@40:1;W:1");
        while (!company.over()) {
            company.stepped(1.0 / 60);
        }
        final Run lead = company.lead();
        final Run other = company.companion().orElseThrow().run();
        assertEquals(lead.steps(), other.steps(), "the same steps");
        final Vec3 here = lead.shown(1).body().position();
        final Vec3 there = other.shown(1).body().position();
        assertTrue(here.z() > 500, "the lead has walked: " + here);
        assertEquals(500, there.x() - here.x(), 1e-6, "as far across as they began");
        assertEquals(0, there.z() - here.z(), 1e-6, "and level along");
        assertEquals(lead.shown(1).body().yaw(), other.shown(1).body().yaw(), 1e-9, "turned alike");
    }

    @Test
    void aCompanionWhoseScriptEndsFirstStandsStillWhileTheLeadGoesOn() {
        final Company company = company("W:3", "W:1");
        while (!company.over()) {
            company.stepped(1.0 / 60);
        }
        final Run other = company.companion().orElseThrow().run();
        assertTrue(other.over(), "its script has ended");
        assertTrue(other.steps() < company.lead().steps(), "and it took no steps after");
        final double walked = other.shown(1).body().position().z();
        assertTrue(walked > 0 && walked < company.lead().shown(1).body().position().z() / 2, "walked: " + walked);
        assertFalse(company.others(0.5).isEmpty(), "a companion is drawn");
        assertTrue(new Company(company.lead()).others(0.5).isEmpty(), "a lead alone draws no other");
    }

    /** A lead walking {@code led} from the world's middle and a companion walking {@code with} 5 m to its right. */
    private Company company(final String led, final String with) {
        final Run lead = new Run(new Scripted(led), Optional.empty(), Optional.of(new Skies(new Asked(1))),
                new Start("0,0,0").body());
        final Run other = new Run(new Scripted(with), Optional.empty(), Optional.empty(), new Start("5,0,0").body());
        return new Company(lead, Optional.of(new Company.Companion(other, new Bot(Build.WOMAN))));
    }
}
