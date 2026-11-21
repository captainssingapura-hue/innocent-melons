package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.vulkan.bench.subject.Courier;
import com.example.fxsuite.vulkan.bench.subject.Tone;
import com.example.fxsuite.vulkan.device.Card;
import com.example.fxsuite.vulkan.device.Pass;
import com.example.fxsuite.vulkan.form.Cuboid;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.render.Formed;
import com.example.fxsuite.vulkan.shade.Laid;
import com.example.fxsuite.vulkan.shade.Look;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Sized;
import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.view.Camera;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * What is drawn of boxes, on the card: one box a unit each way for each of the courier's tones, and one for the
 * ground, each built once and then drawn at whatever size and place it is asked for. Closed, all are gone from the
 * card.
 */
public final class Cast implements AutoCloseable {

    /** How far the ground reaches each way, and how thick it is, in centimetres. */
    private static final double REACH = 400_000;
    private static final double THICK = 100;
    private static final String GRASS = "#8fa66f";

    private final List<Formed> made = new ArrayList<>();
    private final Map<Tone, Formed> boxes = new EnumMap<>(Tone.class);
    private final Formed ground;

    /** The boxes, on {@code card}, their layouts as {@code laid} says. */
    public Cast(final Card card, final Laid laid) {
        final Mesh box = new Cuboid(1, 1, 1).formed();
        try {
            for (final Tone tone : Tone.values()) {
                boxes.put(tone, kept(new Formed(card, new Look(laid, tone.surface()), box)));
            }
            this.ground = kept(new Formed(card, new Look(laid,
                    new Surface(new Colour(GRASS), List.of(), new Colour(0, 0, 0), 1)), box));
        } catch (final RuntimeException refused) {
            close();
            throw refused;
        }
    }

    private Formed kept(final Formed formed) {
        made.add(formed);
        return formed;
    }

    /** The ground drawn by {@code pass}: flat, its top where height is nothing. */
    public void ground(final Pass pass, final Camera camera) {
        ground.drawn(pass, new Mat4(new Vec3(0, -THICK / 2, 0)).times(new Sized(REACH, THICK, REACH).matrix()),
                camera, 1);
    }

    /** The courier drawn by {@code pass}, each of its boxes where {@code placed} has it. */
    public void figure(final Pass pass, final List<Courier.Placed> placed, final Camera camera) {
        for (final Courier.Placed one : placed) {
            boxes.get(one.tone()).drawn(pass, one.stands(), camera, 1);
        }
    }

    @Override
    public void close() {
        made.reversed().forEach(Formed::close);
        made.clear();
    }
}
