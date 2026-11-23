package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.avatar.look.Gait;
import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.bench.subject.Bot;
import com.example.fxsuite.vulkan.bench.subject.Courier;

import java.util.List;
import java.util.Optional;

/**
 * Who is in a walk: its lead, whom the keys or the lead's script move, the camera follows and the sound is heard
 * from; and, where there is one, a companion, another bot moved by a run of its own (its own script, or a kept run),
 * which goes step for step with the lead's, stands where its own start puts it, is in the way of nothing but what
 * the place puts there, and is drawn and casts its shadow as the lead does. Only the lead's run moves the sky's
 * clock. The walk is over when the lead's run is.
 */
public record Company(Run lead, Optional<Companion> companion) {

    /** A companion: its run, and the bot it is drawn as. */
    public record Companion(Run run, Bot bot) {
    }

    /** A lead alone. */
    public Company(final Run lead) {
        this(lead, Optional.empty());
    }

    /** What is in the way of everyone in it, from now on. */
    public void inTheWay(final Obstacles blocks) {
        lead.inTheWay(blocks);
        companion.ifPresent(other -> other.run().inTheWay(blocks));
    }

    /** Everyone taken on by the steps {@code seconds} earn; how far on the lead's next step is, for drawing. */
    public double stepped(final double seconds) {
        final double blend = lead.stepped(seconds);
        companion.ifPresent(other -> other.run().stepped(seconds));
        return blend;
    }

    /** Whether the walk is over: its lead's run is. */
    public boolean over() {
        return lead.over();
    }

    /**
     * The companion's parts, {@code blend} of the way between its last two steps, as they are drawn: nothing, where
     * there is no companion. It looks where its own run has its view look.
     */
    public List<Courier.Placed> others(final double blend) {
        return companion.map(other -> {
            final Avatar shown = other.run().shown(blend);
            return other.bot().placed(shown.body(), shown.body().thrust(),
                    Gait.of(shown, other.run().following().yaw(), other.run().following().lookUp()));
        }).orElse(List.of());
    }
}
