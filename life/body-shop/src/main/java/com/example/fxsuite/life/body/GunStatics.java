package com.example.fxsuite.life.body;

import java.util.ArrayList;
import java.util.List;

/**
 * The slung Gatling gun at rest, its forces balanced: its weight, at its centre of mass; the strap pulling its swivel
 * both ways along the strap, as hard one way as the other (it runs free over the shoulder); the hip pushing out on its
 * side where it bears, square to the hip, or along it no more than friction lets; and whatever the hands must add to
 * hold it still. Of every way to balance it, the one asking least of the hands: so a gun slung well rests on its strap
 * and the hip, the hands only steadying it. Still: what it takes to hold it so, not what it takes to move it. And the
 * body and the gun together over their feet ({@link #balance}).
 *
 * <p>Forces in newtons, at points in the body's frame (cm).
 */
public final class GunStatics {

    /** How fast things fall, m/s²; how far a push along the hip may go, as a share of the push into it. */
    static final double G = 9.81;
    static final double FRICTION = 0.5;

    /** A force on the gun: what it is, where, how hard and which way. */
    public record Force(String name, V3 at, V3 force) {
    }

    /**
     * The gun held still: how heavy and where its centre of mass is; the strap's pull; what the hip pushes and each hand
     * must; every force, to show; whether it would slide on the hip all the same.
     */
    public record Solved(double mass, V3 centre, double strap, V3 hip, V3 right, V3 left, List<Force> forces,
                         boolean sliding) {

        /** All the hands must hold, newtons. */
        public double hands() {
            return right.length() + left.length();
        }
    }

    /** How heavy, kg, each piece of the gun ({@link GatlingHold#model}) is, by its name: the box loaded. */
    static double mass(final String name) {
        if (name.startsWith("receiver")) {
            return 7;
        }
        if (name.startsWith("motor")) {
            return 2.5;
        }
        if (name.startsWith("barrel")) {
            return 1.0;
        }
        if (name.startsWith("ammunition")) {
            return 5;
        }
        if (name.startsWith("spindle")) {
            return 0.6;
        }
        if (name.startsWith("clamp")) {
            return 0.5;
        }
        if (name.startsWith("butt")) {
            return 0.4;
        }
        if (name.startsWith("side handle")) {
            return 0.3;
        }
        if (name.startsWith("rear grip arm") || name.startsWith("side arm") || name.startsWith("feed")) {
            return 0.15;
        }
        if (name.startsWith("rear grip")) {
            return 0.2;
        }
        return 0.05;
    }

    /** The gun's mass, kg, and its centre of mass in its own frame: each piece's by its weight. */
    public record Mass(double kg, V3 centre) {
    }

    public static Mass of(final List<Surface> model) {
        V3 sum = V3.ZERO;
        double kg = 0;
        for (final Surface s : model) {
            final List<V3> points = s.points();
            V3 c = V3.ZERO;
            for (final V3 p : points) {
                c = c.plus(p);
            }
            c = c.times(1.0 / points.size());
            final double m = mass(s.name());
            sum = sum.plus(c.times(m));
            kg += m;
        }
        return new Mass(kg, sum.times(1 / kg));
    }

    private static final Mass GUN = of(GatlingHold.model(GatlingHold.Sling.DEFAULT));

    private GunStatics() {
    }

    /** The gun as {@code held} holds it, still: its forces balanced, asking least of the hands. */
    public static Solved solve(final Proportions proportions, final GatlingHold.Held held) {
        final GatlingHold.Frame gun = held.gun();
        final Rig rig = new Rig(proportions, held.pose());
        final V3 c = gun.place(GUN.centre());
        final V3 weight = new V3(0, -GUN.kg() * G, 0);
        final List<V3> way = held.way();
        final V3 rearAt = way.getFirst();
        final V3 rearWay = way.get(1).minus(rearAt).unit();
        final V3 frontAt = way.getLast();
        final V3 frontWay = way.get(way.size() - 2).minus(frontAt).unit();
        final V3 bears = gun.place(held.sling().bears());
        final V3 into = rig.frame(Part.PELVIS).way(V3.X).unit();
        final V3 along = into.cross(V3.Y).unit();
        final V3 slide = along.cross(into).unit();
        final V3 rightAt = gun.place(GatlingHold.REAR_GRIP);
        final V3 leftAt = rig.held(Part.LEFT_HAND, GatlingHold.PALM);

        // the unknowns: the strap's pull; the hip's push in, and along it two ways; each hand's three ways
        final List<double[]> columns = new ArrayList<>();
        columns.add(wrench(rearAt, rearWay, frontAt, frontWay, c));
        columns.add(wrench(bears, into, c));
        columns.add(wrench(bears, along, c));
        columns.add(wrench(bears, slide, c));
        for (final V3 at : new V3[] {rightAt, leftAt}) {
            for (final V3 way3 : new V3[] {V3.X, V3.Y, V3.Z}) {
                columns.add(wrench(at, way3, c));
            }
        }
        final double[] b = {-weight.x(), -weight.y(), -weight.z(), 0, 0, 0};
        // the strap and the hip cost little; friction more, so it is no more than it must be; the hands most
        final double[] cost = {1e-4, 1e-4, 3e-2, 3e-2, 1, 1, 1, 1, 1, 1};
        final boolean[] free = new boolean[cost.length];
        java.util.Arrays.fill(free, true);
        double[] x = new double[cost.length];
        boolean sliding = false;
        for (int round = 0; round < 8; round++) {
            x = leastCost(columns, b, cost, free);
            if (free[0] && x[0] < 0) {
                free[0] = false;
                continue;
            }
            if (free[1] && x[1] < 0) {
                free[1] = false;
                free[2] = false;
                free[3] = false;
                continue;
            }
            final double rub = Math.hypot(x[2], x[3]);
            if (free[2] && rub > FRICTION * Math.max(0, x[1]) + 1e-6) {
                if (cost[2] < 30) {
                    cost[2] *= 30;
                    cost[3] *= 30;
                    continue;
                }
                sliding = true;
            }
            break;
        }
        final V3 right = new V3(x[4], x[5], x[6]);
        final V3 left = new V3(x[7], x[8], x[9]);
        final V3 hip = into.times(x[1]).plus(along.times(x[2])).plus(slide.times(x[3]));
        final List<Force> forces = List.of(new Force("weight", c, weight),
                new Force("strap, up the chest", rearAt, rearWay.times(x[0])),
                new Force("strap, round the back", frontAt, frontWay.times(x[0])),
                new Force("hip", bears, hip), new Force("right hand", rightAt, right),
                new Force("left hand", leftAt, left));
        return new Solved(GUN.kg(), c, x[0], hip, right, left, forces, sliding);
    }

    /**
     * How far inside the area its feet hold it up over the body and the gun together have their centre of mass, cm, as
     * the rig's own margin ({@link Rig#margin}): the gun's weight at its centre with the body's.
     */
    public static double balance(final Rig rig, final Solved gun) {
        final double body = rig.proportions().mass();
        final V3 both = rig.centreOfMass().times(body).plus(gun.centre().times(gun.mass())).times(1 / (body
                + gun.mass()));
        return Rig.margin(new double[] {both.x(), both.z()}, rig.support());
    }

    /** A pull {@code way} at {@code at}, as a force and its turn about {@code about}. */
    private static double[] wrench(final V3 at, final V3 way, final V3 about) {
        final V3 turn = at.minus(about).cross(way);
        return new double[] {way.x(), way.y(), way.z(), turn.x(), turn.y(), turn.z()};
    }

    /** The strap's pull, at both ends at once. */
    private static double[] wrench(final V3 a, final V3 aWay, final V3 b, final V3 bWay, final V3 about) {
        final double[] one = wrench(a, aWay, about);
        final double[] two = wrench(b, bWay, about);
        final double[] both = new double[6];
        for (int k = 0; k < 6; k++) {
            both[k] = one[k] + two[k];
        }
        return both;
    }

    /**
     * The unknowns that balance the forces ({@code columns} times them make {@code b}), costing least, each its
     * {@code cost} times its square; those not {@code free} held at nothing.
     */
    static double[] leastCost(final List<double[]> columns, final double[] b, final double[] cost,
                              final boolean[] free) {
        final int n = columns.size();
        final double[][] m = new double[6][6];
        for (int k = 0; k < n; k++) {
            if (!free[k]) {
                continue;
            }
            final double[] a = columns.get(k);
            for (int r = 0; r < 6; r++) {
                for (int s = 0; s < 6; s++) {
                    m[r][s] += a[r] * a[s] / cost[k];
                }
            }
        }
        final double[] lambda = Drag.solve(m, b);
        final double[] x = new double[n];
        for (int k = 0; k < n; k++) {
            if (free[k]) {
                final double[] a = columns.get(k);
                double dot = 0;
                for (int r = 0; r < 6; r++) {
                    dot += a[r] * lambda[r];
                }
                x[k] = dot / cost[k];
            }
        }
        return x;
    }
}
