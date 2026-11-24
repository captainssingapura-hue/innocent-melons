package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.avatar.hear.Hearing;
import com.example.fxsuite.avatar.look.Gait;
import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.bench.subject.Bot;
import com.example.fxsuite.vulkan.bench.subject.Courier;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Overlay;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Who is in a walk on the engine's own avatar: its lead, drawn as {@code bot}, whom the keys or the lead's script
 * move, the camera follows and the sound is heard from; and, where there is one, a companion, another bot moved by a
 * run of its own (its own script, or a kept run), which goes step for step with the lead's, stands where its own
 * start puts it, is in the way of nothing but what the place puts there, and is drawn and casts its shadow as the
 * lead does. Only the lead's run moves the sky's clock. The walk is over when the lead's run is.
 */
public record Company(Run lead, Bot bot, Optional<Companion> companion) implements Roaming {

    /** Where its figures are written over the picture, how large and how far apart, in the picture's own units. */
    private static final double MARGIN = 0.06;
    private static final double FOOT = -0.92;
    private static final double LETTER = 0.045;
    private static final double LINE = 0.08;
    private static final double METRE = 100;

    /** A companion: its run, and the bot it is drawn as. */
    public record Companion(Run run, Bot bot) {
    }

    /** A lead alone, drawn as the man bot. */
    public Company(final Run lead) {
        this(lead, new Bot(), Optional.empty());
    }

    @Override
    public void inTheWay(final Obstacles blocks) {
        lead.inTheWay(blocks);
        companion.ifPresent(other -> other.run().inTheWay(blocks));
    }

    @Override
    public double stepped(final double seconds) {
        final double blend = lead.stepped(seconds);
        companion.ifPresent(other -> other.run().stepped(seconds));
        return blend;
    }

    @Override
    public boolean over() {
        return lead.over();
    }

    @Override
    public boolean ends() {
        return lead.ends();
    }

    @Override
    public double seconds() {
        return lead.seconds();
    }

    @Override
    public Vec3 head(final double blend) {
        return lead.head(lead.shown(blend));
    }

    @Override
    public double yaw(final double blend) {
        return lead.shown(blend).body().yaw();
    }

    @Override
    public Camera camera(final double blend) {
        return lead.following().camera(lead.inTheWay(), blend);
    }

    @Override
    public Vec3 foot(final double blend) {
        final com.example.fxsuite.avatar.sim.Vec3 at = lead.shown(blend).body().position();
        return new Vec3(at.x(), 0, at.z());
    }

    @Override
    public List<Courier.Placed> figures(final double blend) {
        final Avatar shown = lead.shown(blend);
        final List<Courier.Placed> all = new ArrayList<>(bot.placed(shown.body(), shown.body().thrust(),
                Gait.of(shown, lead.following().yaw(), lead.following().lookUp()), lead.otherLeg()));
        all.addAll(others(blend));
        return all;
    }

    /**
     * The lead's speed over the picture, low on its left, and, flying, its height and the gear its jets are in with
     * the fastest that gear goes: for flying by, since the fastest gear is twice the engine's own top speed.
     */
    @Override
    public void overlaid(final Pass pass, final Overlay.Over over, final double blend) {
        final Body body = lead.shown(blend).body();
        final Vec3 green = new Vec3(0.35, 1, 0.45);
        final Gear gear = lead.gear();
        final double left = -over.aspect() + MARGIN;
        over.text(pass, String.format(Locale.ROOT, "KMH %.0f", gear.hourly(Math.hypot(body.speed(), body.drift()))),
                left, FOOT, LETTER, green);
        if (body.flying()) {
            over.text(pass, String.format(Locale.ROOT, "GEAR %d OF %d  TOP %.0f", gear.ordinal() + 1,
                    Gear.values().length, gear.fastest()), left, FOOT + LINE, LETTER, green);
            over.text(pass, String.format(Locale.ROOT, "ALT %.0f M", -body.position().y() / METRE), left,
                    FOOT + 2 * LINE, LETTER, green);
        }
    }

    @Override
    public void heard(final Hearing hearing, final double blend) {
        // heard as the engine's game hears it: what happened in the steps just taken, from where the view is
        hearing.hear(lead.shown(blend), lead.happened(), lead.following().chase());
    }

    /**
     * The companion's parts, {@code blend} of the way between its last two steps, as they are drawn: nothing, where
     * there is no companion. It looks where its own run has its view look.
     */
    public List<Courier.Placed> others(final double blend) {
        return companion.map(other -> {
            final Avatar shown = other.run().shown(blend);
            return other.bot().placed(shown.body(), shown.body().thrust(),
                    Gait.of(shown, other.run().following().yaw(), other.run().following().lookUp()),
                    other.run().otherLeg());
        }).orElse(List.of());
    }
}
