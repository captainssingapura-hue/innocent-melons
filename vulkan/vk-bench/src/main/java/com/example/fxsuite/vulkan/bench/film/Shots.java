package com.example.fxsuite.vulkan.bench.film;

import com.example.fxsuite.vulkan.space.Vec3;
import com.example.fxsuite.vulkan.view.Camera;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Where a film's camera is, shot by shot: a list of shots, each beginning at a moment of the run, in seconds of the
 * run's own time. With none, the film is the run as played. Written as a film is told them, a shot a line:
 * <pre>
 * shots 1
 * 0     as played
 * 6.5   orbit distance=8 height=2.5 speed=20 cut
 * 14    fixed at=12,1.6,30 fov=35 blend 1.5
 * 18    fixed at=70,5,-34 look=-97,6,-34 fov=60 cut
 * 22    path 0,40,-20 > 30,25,10 > 60,8,40 look=avatar blend 2
 * </pre>
 * In metres, degrees and seconds. A shot is come into by a cut unless it says {@code blend} and over how many
 * seconds. Lines that are blank, or begin with {@code #}, say nothing. The shots are in the order they begin; a
 * list that is not, or a line that cannot be read, is refused, saying which line.
 */
public record Shots(List<Shot> shots, double end) {

    /** The first line of every list of shots, and the format's number. */
    private static final String FORMAT = "shots 1";
    private static final double FAR = 1000;

    public Shots {
        shots = List.copyOf(shots);
        for (int at = 1; at < shots.size(); at++) {
            if (shots.get(at).start() < shots.get(at - 1).start()) {
                throw new IllegalArgumentException("shots are in the order they begin: " + shots.get(at));
            }
        }
    }

    /** No shots: the run as played, all through. */
    public Shots() {
        this(List.of(new Shot(0, Shot.Kind.AS_PLAYED, Map.of(), List.of(), 0)), Double.POSITIVE_INFINITY);
    }

    /** The shots {@code written} says, as a list of shots is written. */
    public Shots(final String written) {
        this(new Reading(written).shots(), Double.POSITIVE_INFINITY);
    }

    /**
     * The same shots, in a film that ends at {@code seconds} of the run: so its last shot knows how long it lasts,
     * as a path, which goes from its first place to its last over the whole of its shot, needs to.
     */
    public Shots endingAt(final double seconds) {
        return new Shots(shots, seconds);
    }

    /**
     * Where the camera is at {@code at} seconds of the run: the avatar's head at {@code head}, the avatar facing
     * {@code yaw} degrees round, the chase camera as played at {@code asPlayed}. Within a shot's blend, part of the
     * way from where the shot before has it to where this one does.
     */
    public Camera camera(final double at, final Vec3 head, final double yaw, final Camera asPlayed) {
        int now = 0;
        while (now + 1 < shots.size() && shots.get(now + 1).start() <= at) {
            now++;
        }
        final Shot shot = shots.get(now);
        final Camera seen = shot.seen(at, until(now), head, yaw, asPlayed);
        if (now == 0 || !(shot.blend() > 0) || at - shot.start() >= shot.blend()) {
            return seen;
        }
        final Camera was = shots.get(now - 1).seen(at, shot.start(), head, yaw, asPlayed);
        final double share = new Shot.Eased().of((at - shot.start()) / shot.blend());
        final Vec3 eye = was.eye().plus(seen.eye().minus(was.eye()).times(share));
        final Vec3 aimWas = was.eye().plus(was.ahead().times(FAR));
        final Vec3 aim = aimWas.plus(seen.eye().plus(seen.ahead().times(FAR)).minus(aimWas).times(share));
        return shot.looking(eye, aim, was.fieldOfView() + (seen.fieldOfView() - was.fieldOfView()) * share);
    }

    /** When the shot after the {@code shot}th begins; for the last, when the film ends. */
    private double until(final int shot) {
        return shot + 1 < shots.size() ? shots.get(shot + 1).start() : end;
    }

    /** The shots said by the lines of {@code written}. */
    record Reading(String written) {

        List<Shot> shots() {
            final List<String> lines = written.lines().map(String::strip).toList();
            if (lines.isEmpty() || !lines.getFirst().equals(FORMAT)) {
                throw new IllegalArgumentException("a list of shots begins " + FORMAT);
            }
            final List<Shot> shots = new ArrayList<>();
            for (int at = 1; at < lines.size(); at++) {
                final String line = lines.get(at);
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                try {
                    shots.add(shot(line));
                } catch (final RuntimeException unread) {
                    throw new IllegalArgumentException("line " + (at + 1) + " of the shots cannot be read: " + line,
                            unread);
                }
            }
            return shots;
        }

        /** One line, one shot. */
        private Shot shot(final String line) {
            final List<String> words = new ArrayList<>(List.of(line.split("\\s+")));
            final double start = Double.parseDouble(words.removeFirst());
            final String first = words.removeFirst();
            if (first.equals("as")) {
                words.removeFirst();
            }
            final Shot.Kind kind = first.equals("as") ? Shot.Kind.AS_PLAYED
                    : Shot.Kind.valueOf(first.toUpperCase(Locale.ROOT));
            double blend = 0;
            final Map<String, String> said = new HashMap<>();
            final List<Vec3> path = new ArrayList<>();
            for (int at = 0; at < words.size(); at++) {
                final String word = words.get(at);
                if (word.equals("blend")) {
                    blend = Double.parseDouble(words.get(++at));
                } else if (word.contains("=")) {
                    said.put(word.substring(0, word.indexOf('=')), word.substring(word.indexOf('=') + 1));
                } else if (!word.equals("cut") && !word.equals(">")) {
                    path.add(new Shot.Placed(word).place());
                }
            }
            return new Shot(start, kind, said, path, blend);
        }
    }
}
