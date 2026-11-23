package com.example.fxsuite.vulkan.bench.film;

import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;
import com.example.fxsuite.vulkan.view.Orbit;

import java.util.List;
import java.util.Map;

/**
 * One shot of a film: from {@code start} seconds of the run, the camera placed as its {@code kind} says, by what
 * {@code said} tells it, in metres and degrees; a path through {@code path}, for a shot that moves along one. It is
 * come into from the shot before by a cut, or, where {@code blend} is more than nothing, by moving from where that
 * one has the camera to where this one does over so many seconds.
 */
public record Shot(double start, Kind kind, Map<String, String> said, List<Vec3> path, double blend) {

    /** The kinds of shot there are. */
    public enum Kind {
        /** The chase camera exactly as it was played. */
        AS_PLAYED,
        /** Standing at a point, watching the avatar. */
        FIXED,
        /** Circling the avatar at a set distance, height and speed. */
        ORBIT,
        /** Travelling with the avatar at a set offset from it: to its right, above it, ahead of it. */
        BESIDE,
        /** Moving through a few set places, eased, watching the avatar or looking the way it moves. */
        PATH
    }

    private static final double METRE = 100;
    private static final double FIELD = 48;
    private static final double NEAR = 10;
    private static final double STEEPEST = 89;

    public Shot {
        said = Map.copyOf(said);
        path = List.copyOf(path);
        if (kind == Kind.PATH && path.size() < 2) {
            throw new IllegalArgumentException("a path goes through two places or more, not " + path.size());
        }
    }

    /**
     * Where it has the camera at {@code at} seconds of the run, the shot after it beginning at {@code until}: the
     * avatar's head at {@code head}, the avatar facing {@code yaw} degrees round, and the chase camera as played
     * at {@code asPlayed}.
     */
    public Camera seen(final double at, final double until, final Vec3 head, final double yaw,
                       final Camera asPlayed) {
        final double into = at - start;
        return switch (kind) {
            case AS_PLAYED -> asPlayed;
            case FIXED -> looking(place("at", "0,2,0"), head, number("fov", FIELD));
            case ORBIT -> {
                final double round = Math.toRadians(number("from", yaw + 180) + number("speed", 20) * into);
                final double away = number("distance", 8) * METRE;
                yield looking(head.plus(new Vec3(Math.sin(round) * away, number("height", 2) * METRE,
                        Math.cos(round) * away)), head, number("fov", FIELD));
            }
            case BESIDE -> {
                final Vec3 off = place("offset", "4,1,0");
                final double round = Math.toRadians(yaw);
                final Vec3 ahead = new Vec3(Math.sin(round), 0, Math.cos(round));
                final Vec3 right = new Vec3(Math.cos(round), 0, -Math.sin(round));
                yield looking(head.plus(right.times(off.x())).plus(new Vec3(0, off.y(), 0)).plus(ahead.times(off.z())),
                        head, number("fov", FIELD));
            }
            case PATH -> along(into, until - start, head);
        };
    }

    /** Where a path shot has the camera {@code into} seconds after it began, its whole length {@code over}. */
    private Camera along(final double into, final double over, final Vec3 head) {
        final double length = said.containsKey("over") ? number("over", 1) : over;
        final double through = new Eased().of(Double.isFinite(length) && length > 0 ? into / length : 1);
        final double reached = through * (path.size() - 1);
        final int from = Math.min(path.size() - 2, (int) Math.floor(reached));
        final Vec3 here = path.get(from).plus(path.get(from + 1).minus(path.get(from)).times(reached - from));
        final Vec3 looked = said.getOrDefault("look", "avatar").equals("ahead")
                ? here.plus(path.get(from + 1).minus(path.get(from))) : head;
        return looking(here, looked, number("fov", FIELD));
    }

    /** The camera at {@code eye} looking at {@code at}, seeing {@code field} degrees from top to bottom. */
    Camera looking(final Vec3 eye, final Vec3 at, final double field) {
        final Vec3 way = at.minus(eye);
        final double away = Math.max(way.length(), 1);
        final double pitch = Math.max(-STEEPEST, Math.min(STEEPEST, Math.toDegrees(Math.asin(-way.y() / away))));
        return new Orbit(eye.plus(way.unit().times(away)), Math.toDegrees(Math.atan2(way.x(), way.z())), pitch, away)
                .camera(field, NEAR);
    }

    /** The number said as {@code name}, or {@code otherwise} where none is. */
    private double number(final String name, final double otherwise) {
        return said.containsKey(name) ? Double.parseDouble(said.get(name)) : otherwise;
    }

    /** The place said as {@code name}, three numbers in metres, or {@code otherwise}; in centimetres. */
    private Vec3 place(final String name, final String otherwise) {
        return new Placed(said.getOrDefault(name, otherwise)).place();
    }

    /** A place written as three numbers in metres, {@code x,y,z}, read into centimetres. */
    record Placed(String written) {

        Vec3 place() {
            final String[] numbers = written.split(",");
            if (numbers.length != 3) {
                throw new IllegalArgumentException("a place is three numbers, in metres: " + written);
            }
            return new Vec3(Double.parseDouble(numbers[0]), Double.parseDouble(numbers[1]),
                    Double.parseDouble(numbers[2])).times(METRE);
        }
    }

    /** Eased: slow to begin, and slow to end, from nothing at nothing to one at one; held there either side. */
    record Eased() {

        double of(final double share) {
            final double held = Math.max(0, Math.min(1, share));
            return held * held * (3 - 2 * held);
        }
    }
}
