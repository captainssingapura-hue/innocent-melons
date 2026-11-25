package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.avatar.hear.Hearing;
import com.example.fxsuite.avatar.look.Gait;
import com.example.fxsuite.avatar.sim.Avatar;
import com.example.fxsuite.avatar.sim.Body;
import com.example.fxsuite.avatar.view.Chase;
import com.example.fxsuite.avatar.sim.Obstacles;
import com.example.fxsuite.vulkan.bench.subject.Bot;
import com.example.fxsuite.vulkan.bench.subject.Courier;
import com.example.fxsuite.vulkan.bench.subject.Tone;
import com.example.fxsuite.vulkan.bench.subject.Trodden;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.render.Overlay;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import melons.innocent.core.motion.Motion;

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

    /** How wide a foot's tread is taken to be when the ground under it is asked for, in centimetres. */
    private static final double TREAD = 5;

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
    public double tilt(final double blend) {
        return lead.following().lookUp();
    }

    @Override
    public void steadied(final boolean steady) {
        lead.following().steadied(steady);
    }

    @Override
    public Camera camera(final double blend) {
        return lead.following().own() ? lead.following().camera(eyes(blend), yaw(blend), blend)
                : lead.following().camera(lead.inTheWay(), blend);
    }

    /** From its own eyes the lead sees itself without its head, which is round the eye and not before it. */
    @Override
    public List<Courier.Placed> seen(final double blend) {
        if (!lead.following().own()) {
            return figures(blend);
        }
        final List<Courier.Placed> all = new ArrayList<>(bot.headless(own(blend)));
        all.addAll(others(blend));
        return all;
    }

    /**
     * Where the lead's own view is taken from, {@code blend} of the way between its last two steps. Steadied, it
     * is where its eyes would be if it stood still where it is, facing as it faces: it goes as the body goes,
     * smoothly, and not as the head does. Not steadied, it is where its eyes are as the figure is drawn, dipping
     * and rising with its stride.
     */
    private Vec3 eyes(final double blend) {
        if (!lead.following().steady()) {
            return glowing(own(blend)).orElseGet(() -> head(blend));
        }
        // the figure standing still at the world's middle, facing ahead: where its eyes are from its feet
        final Avatar still = new Avatar(new Start("0,0,0").body(), false);
        final Vec3 from = glowing(bot.placed(still.body(), 0, Gait.of(still, 0, 0), false))
                .orElse(new Vec3(0, Chase.EYE_HEIGHT, 0));
        final Body body = lead.shown(blend).body();
        final double sin = Math.sin(Math.toRadians(body.yaw()));
        final double cos = Math.cos(Math.toRadians(body.yaw()));
        return new Vec3(body.position().x() + cos * from.x() + sin * from.z(), -body.position().y() + from.y(),
                body.position().z() - sin * from.x() + cos * from.z());
    }

    /** Midway between the eyes that glow among {@code parts}; nowhere, where the figure has none. */
    private Optional<Vec3> glowing(final List<Courier.Placed> parts) {
        final List<Courier.Placed> glowing = parts.stream().filter(part -> part.tone() == Tone.VISOR).toList();
        if (glowing.isEmpty()) {
            return Optional.empty();
        }
        Vec3 sum = new Vec3(0, 0, 0);
        for (final Courier.Placed eye : glowing) {
            sum = sum.plus(new Vec3(eye.stands().at(0, 3), eye.stands().at(1, 3), eye.stands().at(2, 3)));
        }
        return Optional.of(sum.times(1.0 / glowing.size()));
    }

    /** The lead's own parts, as they are drawn {@code blend} of the way between its last two steps. */
    private List<Courier.Placed> own(final double blend) {
        final Avatar shown = lead.shown(blend);
        return bot.placed(shown.body(), shown.body().thrust(),
                Gait.of(shown, lead.following().yaw(), lead.following().lookUp()), lead.otherLeg(),
                trodden(lead, shown.body()));
    }

    /**
     * The ground under the feet of a figure of {@code run} whose body is {@code body}: what the engine itself would
     * stand a foot on at each spot, of what is in the run's way, against where it has the body stand. Nothing where
     * that is more than a step and a little from the body's own ground, as past an edge.
     */
    private Trodden trodden(final Run run, final Body body) {
        return (x, z) -> {
            final double higher = run.inTheWay().footing(x, z, TREAD, body.height() + Body.STEP).height()
                    - body.height();
            return Math.abs(higher) <= Body.STEP + TREAD ? higher : 0;
        };
    }

    @Override
    public Vec3 foot(final double blend) {
        final com.example.fxsuite.avatar.sim.Vec3 at = lead.shown(blend).body().position();
        return new Vec3(at.x(), 0, at.z());
    }

    @Override
    public List<Courier.Placed> figures(final double blend) {
        final List<Courier.Placed> all = new ArrayList<>(own(blend));
        all.addAll(others(blend));
        return all;
    }

    @Override
    public Optional<Motion> motion() {
        return Optional.of(lead.motion());
    }

    /**
     * The lead's speed over the picture, as the core engine detects it from the run's steps, low on its left, and,
     * flying, its height and the gear its jets are in with the fastest that gear goes: for flying by, since the
     * fastest gear is twice the engine's own top speed.
     */
    @Override
    public void overlaid(final Pass pass, final Overlay.Over over, final double blend) {
        final Body body = lead.shown(blend).body();
        final Vec3 green = new Vec3(0.35, 1, 0.45);
        final Gear gear = lead.gear();
        final double left = -over.aspect() + MARGIN;
        over.text(pass, String.format(Locale.ROOT, "KMH %.0f", lead.motion().hourly()), left, FOOT, LETTER, green);
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
                    other.run().otherLeg(), trodden(other.run(), shown.body()));
        }).orElse(List.of());
    }
}
