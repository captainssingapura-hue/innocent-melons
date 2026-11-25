package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Vec3;
import com.example.fxsuite.vulkan.bench.play.Scripted;
import com.example.fxsuite.vulkan.bench.subject.Bot;
import com.example.fxsuite.vulkan.bench.subject.Build;
import com.example.fxsuite.vulkan.bench.subject.Tone;
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

    @Test
    void pressedVTheViewIsFromTheLeadsOwnEyesTheWayItLooksAndPressedAgainFromBehindIt() {
        final Company company = company(":0.3;V;:0.6;@90:1;:0.4;V;:0.6", ":4");
        assertFalse(company.lead().following().own(), "from behind, to begin with");
        assertEquals(48, company.camera(1).fieldOfView(), 0);
        while (!company.over() && !company.lead().following().own()) {
            company.stepped(1.0 / 60);
        }
        final com.example.fxsuite.vulkan.view.Camera own = company.camera(1);
        assertTrue(company.lead().following().own(), "V pressed, the view is its own");
        assertEquals(62, own.fieldOfView(), 0, "seen wider from its own eyes");
        assertTrue(own.eye().y() > 160 && own.eye().y() < 185, "at its eyes, in its head: " + own.eye().y());
        assertTrue(Math.hypot(own.eye().x(), own.eye().z()) < 15, "over where it stands");
        assertTrue(own.ahead().z() > 0.99, "looking the way it faces, level: " + own.ahead());
        assertEquals(company.figures(1).size() - 5, company.seen(1).size(), "it sees itself without its head");
        assertEquals(company.figures(1).stream().filter(part -> part.tone() == Tone.VISOR).count() - 2,
                company.seen(1).stream().filter(part -> part.tone() == Tone.VISOR).count(),
                "its own two eyes among what it does not see, its companion's still seen");
        // turned a quarter round to its right, the view has turned with it
        for (int frame = 0; frame < 110 && company.lead().following().own(); frame++) {
            company.stepped(1.0 / 60);
        }
        assertTrue(company.lead().following().own(), "still its own, the turn made");
        assertTrue(company.camera(1).ahead().x() > 0.99, "turned with it: " + company.camera(1).ahead());
        while (!company.over()) {
            company.stepped(1.0 / 60);
        }
        assertFalse(company.lead().following().own(), "V again, from behind once more");
        final com.example.fxsuite.vulkan.view.Camera behind = company.camera(1);
        assertTrue(Math.hypot(behind.eye().x(), behind.eye().z()) > 200, "well behind it");
        assertEquals(48, behind.fieldOfView(), 0);
        assertEquals(company.figures(1).size(), company.seen(1).size(), "and from behind it is seen whole");
    }

    @Test
    void itsOwnViewIsSteadiedUnlessToldNotAndThenMovesAsItsHeadDoes() {
        final Company company = company("V;W:3", ":4");
        assertTrue(company.lead().following().steady(), "steadied, to begin with");
        final double[] steady = heights(company, 90);
        assertTrue(steady[1] - steady[0] < 1e-6, "walking, the steadied view keeps its height: " + steady[1]);
        assertTrue(steady[0] > 160 && steady[0] < 185, "at the height of its eyes: " + steady[0]);
        company.steadied(false);
        final double[] moving = heights(company, 90);
        assertTrue(moving[1] - moving[0] > 0.5, "not steadied, it dips and rises with the stride: "
                + (moving[1] - moving[0]));
        assertTrue(moving[1] <= steady[1] + 0.5, "and never rises over where it stands still");
    }

    /** The lowest and the highest the lead's own view is, over so many frames of its walking on. */
    private double[] heights(final Company company, final int frames) {
        double low = Double.POSITIVE_INFINITY;
        double high = Double.NEGATIVE_INFINITY;
        for (int frame = 0; frame < frames; frame++) {
            company.stepped(1.0 / 60);
            if (company.lead().following().own()) {
                low = Math.min(low, company.camera(1).eye().y());
                high = Math.max(high, company.camera(1).eye().y());
            }
        }
        return new double[] {low, high};
    }

    /** A lead walking {@code led} from the world's middle and a companion walking {@code with} 5 m to its right. */
    private Company company(final String led, final String with) {
        final Run lead = new Run(new Scripted(led), Optional.empty(), Optional.of(new Skies(new Asked(1))),
                new Start("0,0,0").body());
        final Run other = new Run(new Scripted(with), Optional.empty(), Optional.empty(), new Start("5,0,0").body());
        return new Company(lead, new Bot(), Optional.of(new Company.Companion(other, new Bot(Build.WOMAN))));
    }
}
