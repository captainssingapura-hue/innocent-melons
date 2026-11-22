package com.example.fxsuite.vulkan.bench.subject;

import com.example.fxsuite.avatar.sim.Fruit;
import com.example.fxsuite.vulkan.form.Dome;
import com.example.fxsuite.vulkan.form.Ellipsoid;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.term.Colour;
import com.example.fxsuite.vulkan.term.Point;
import com.example.fxsuite.vulkan.term.Real;
import com.example.fxsuite.vulkan.term.Seen;
import com.example.fxsuite.vulkan.term.Surface;
import com.example.fxsuite.vulkan.term.Term;
import com.example.fxsuite.vulkan.term.Turns;

import java.util.List;

/**
 * The engine's watermelon ({@link Fruit#WATERMELON}), lying on its side, as the bench's first subject: a round body
 * as long, wide and tall as the engine has it, dark green, striped lighter from end to end. Its colours, its nine
 * stripes, each a third light, and their wander are those the engine paints into a picture for JavaFX; here they are
 * a term, a function of where a point is on the fruit, so they are as fine as they are looked at and fade to their
 * average far off.
 */
public record Watermelon() {

    private static final double STRIPES = 9;
    private static final double LIGHT = 0.33;
    /** How far the stripes wander either way, in stripes, and how often from end to end, in half turns. */
    private static final double WANDER = 2.5 / 128 * STRIPES;
    private static final double WANDERS = 3;
    /** The narrowest a light stripe is across, in centimetres: a third of a ninth of the way round its middle. */
    private static final double NARROWEST = 3;
    /** Nearer its line across than this, in centimetres, the stripes are taken to run as they do this far out. */
    private static final double CORE = 0.01;

    /** How long it is across, how tall and how wide, lying on its side, in centimetres. */
    public Ellipsoid body() {
        final Fruit fruit = Fruit.WATERMELON;
        return new Ellipsoid(fruit.length(), fruit.height(), fruit.width());
    }

    /** Its body as triangles, {@code round} points round it. */
    public Mesh formed(final int round) {
        return body().formed(round, round / 2);
    }

    /**
     * Half of it, cut along its length and set down on the cut: a dome as long and as wide as it is and half as
     * high, {@code round} points over it. Its rind is the whole fruit's, so its stripes run on as they did.
     */
    public Mesh halved(final int round) {
        final Fruit fruit = Fruit.WATERMELON;
        return new Dome(fruit.length(), fruit.height(), fruit.width()).formed(round, round);
    }

    /** How high its middle is when it lies on the ground, in centimetres. */
    public double rests() {
        return body().up() / 2;
    }

    /** Its rind: dark green, the lighter stripes over it, and a dull green shine. */
    public Surface rind() {
        final Seen seen = new Seen();
        return new Surface(new Colour("#1e4a1c"),
                List.of(new Term("stripes", new Colour("#5b9a3c"), stripes(seen.at()).pulse(LIGHT, seen.covers()),
                        NARROWEST)),
                new Colour("#3a4a3a"), 32);
    }

    /**
     * How many stripes round the fruit a point is, from its top: nine all the way round its line across, each
     * pushed a little one way and then the other as it runs from end to end.
     */
    private Turns stripes(final Point at) {
        final Real round = at.along().angle(at.up()).over(2 * Math.PI);
        final Real fromEnd = at.across().over(body().across() / 2).held(-1, 1).arc().over(Math.PI);
        final Real wander = fromEnd.times(WANDERS * Math.PI).sine().times(WANDER);
        final Real out = at.up().times(at.up()).plus(at.along().times(at.along())).root().most(new Real(CORE));
        return new Turns(round.times(STRIPES).plus(wander), new Real(STRIPES / (2 * Math.PI)).over(out));
    }
}
