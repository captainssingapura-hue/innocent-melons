package com.example.fxsuite.life.body.head;

import com.example.fxsuite.life.body.Mesher;
import com.example.fxsuite.life.body.V3;
import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * A reference head read from a body's mesh in a Wavefront OBJ file, as MakeHuman's base mesh is made: its points; its
 * skin, the faces of the group {@code body}; its joints, each a small group named {@code joint-...}, where the head
 * sits on the neck ({@code joint-head}), the neck ({@code joint-neck}), each eye ({@code joint-r-eye}, {@code
 * joint-l-eye}), the mouth ({@code joint-mouth}). Of its skin, what is above its neck's joint, at its own size, its
 * decimetres our centimetres, its head's joint our head's middle; its right, at its x's less, made ours, at our x's
 * more, its faces turned to go round the other way with it.
 */
public record ReferenceReading() implements StatelessFunctionalObject {

    public static final ReferenceReading INSTANCE = new ReferenceReading();

    /** How many centimetres to its unit, a decimetre. */
    private static final double UNIT = 10;

    /** The OBJ's points, and each group's faces, three or more points each, by number from nothing. */
    private record Obj(List<V3> points, Map<String, List<int[]>> groups) {
    }

    /** The head of the body in the file at {@code path}, in our head's frame. */
    public ReferenceHead of(final Path path) throws IOException {
        final Obj obj = read(path);
        final V3 head = middle(obj, "joint-head");
        final double neck = middle(obj, "joint-neck").y();
        final UnaryOperator<V3> ours = p -> new V3(-(p.x() - head.x()) * UNIT, (p.y() - head.y()) * UNIT,
                (p.z() - head.z()) * UNIT);
        final Map<Integer, Integer> kept = new HashMap<>();
        final List<Float> at = new ArrayList<>();
        final List<Integer> t = new ArrayList<>();
        for (final int[] f : obj.groups().getOrDefault("body", List.of())) {
            boolean above = true;
            for (final int i : f) {
                above &= obj.points().get(i).y() >= neck;
            }
            if (!above) {
                continue;
            }
            final int[] c = new int[f.length];
            for (int k = 0; k < f.length; k++) {
                c[k] = kept.computeIfAbsent(f[k], i -> {
                    final V3 p = ours.apply(obj.points().get(i));
                    at.addAll(List.of((float) p.x(), (float) p.y(), (float) p.z()));
                    return at.size() / 3 - 1;
                });
            }
            for (int k = 1; k + 1 < c.length; k++) {
                t.addAll(List.of(c[0], c[k + 1], c[k]));
            }
        }
        final float[] points = new float[at.size()];
        for (int i = 0; i < points.length; i++) {
            points[i] = at.get(i);
        }
        return new ReferenceHead(new Mesher.Mesh(points, t.stream().mapToInt(Integer::intValue).toArray()),
                path.toString(), ours.apply(middle(obj, "joint-r-eye")), ours.apply(middle(obj, "joint-l-eye")),
                ours.apply(middle(obj, "joint-mouth")));
    }

    /** The middle of the points the faces of group {@code name} go through. */
    private V3 middle(final Obj obj, final String name) {
        final List<int[]> faces = obj.groups().get(name);
        if (faces == null) {
            throw new IllegalArgumentException("no group " + name + " in the reference");
        }
        V3 sum = V3.ZERO;
        int n = 0;
        for (final int[] f : faces) {
            for (final int i : f) {
                sum = sum.plus(obj.points().get(i));
                n++;
            }
        }
        return sum.times(1.0 / n);
    }

    /** The file's points and its groups' faces; its texture's and its ways' numbers left. */
    private Obj read(final Path path) throws IOException {
        final List<V3> points = new ArrayList<>();
        final Map<String, List<int[]>> groups = new HashMap<>();
        String group = "";
        for (final String line : Files.readAllLines(path)) {
            final String[] w = line.trim().split("\\s+");
            if (w[0].equals("v")) {
                points.add(new V3(Double.parseDouble(w[1]), Double.parseDouble(w[2]), Double.parseDouble(w[3])));
            } else if (w[0].equals("g") && w.length > 1) {
                group = w[1];
            } else if (w[0].equals("f")) {
                final int[] f = new int[w.length - 1];
                for (int k = 1; k < w.length; k++) {
                    f[k - 1] = Integer.parseInt(w[k].split("/")[0]) - 1;
                }
                groups.computeIfAbsent(group, x -> new ArrayList<>()).add(f);
            }
        }
        return new Obj(points, groups);
    }
}
