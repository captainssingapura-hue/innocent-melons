package com.example.fxsuite.texture.lab;

import com.example.fxsuite.avatar.build.Sheet;
import com.example.fxsuite.avatar.sim.Form;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The car body the bench shows paint and glass on. Its skeleton: nothing wrong with it as a graph, as long as it
 * says and the same on its left as on its right, the body one piece and each of its six windows a piece apart.
 * Its faces: fifteen sheets of metal and six of glass, each named. Body and glass together close it in: no crack
 * between two sheets and no hole a window does not fill, its only open edge the one it stands on. And every
 * window faces out of the car.
 */
class CarBodyTest {

    private final Form skeleton = new CarBody().skeleton();
    private final CarFaces faces = new CarFaces(skeleton);

    @Test
    void theSkeletonIsASoundGraphAsLongAsItSaysTheSameBothSidesAndItsWindowsStandApart() {
        assertEquals(List.of(), skeleton.problems());
        final Form.Bounds bounds = skeleton.bounds();
        assertEquals(440, bounds.depth(), 1e-6, "from its tail to its nose");
        assertEquals(-bounds.leftmost(), bounds.rightmost(), 1e-9, "as wide to its left as to its right");
        assertEquals(0, bounds.low(), 1e-9, "it stands on its sills");
        assertTrue(bounds.width() > 160 && bounds.width() < 172 && bounds.high() > 120 && bounds.high() < 130,
                "a saloon's width and height: " + bounds);
        assertEquals(7, skeleton.pieces(), "the body, and six windows standing apart in it");
        for (final Form.Vertex vertex : skeleton.vertices()) {
            if (vertex.name().endsWith(", left")) {
                final Form.Vertex other = skeleton.vertices().get(
                        skeleton.vertex(vertex.name().replace(", left", ", right")));
                assertEquals(-vertex.across(), other.across(), 1e-9, vertex.name());
                assertEquals(vertex.up(), other.up(), 1e-9, vertex.name());
            }
        }
    }

    @Test
    void itsFacesAreFifteenSheetsOfMetalAndSixOfGlassEachNamed() {
        final List<Sheet> body = faces.body();
        final List<Sheet> glass = faces.glass();
        assertEquals(15, body.size());
        assertEquals(6, glass.size());
        assertTrue(body.stream().allMatch(sheet -> sheet.kind().equals("body") && sheet.area() > 1000));
        assertTrue(glass.stream().allMatch(sheet -> sheet.kind().equals("glass") && sheet.area() > 1000));
        final List<String> names = new ArrayList<>();
        body.forEach(sheet -> names.add(sheet.name()));
        glass.forEach(sheet -> names.add(sheet.name()));
        assertEquals(21, names.stream().distinct().count(), "no two of one name: " + names);
        assertTrue(names.containsAll(List.of("bonnet", "roof", "boot lid", "door panel, left", "windscreen frame",
                "windscreen", "quarter window, right")), names.toString());
    }

    @Test
    void bodyAndGlassTogetherCloseItInWithNoCrackAndItsOnlyOpenEdgeIsTheOneItStandsOn() {
        final List<Sheet> all = new ArrayList<>(faces.body());
        all.addAll(faces.glass());
        final Mesh whole = new Sheeted(all).formed();
        final Map<String, Integer> sides = new HashMap<>();
        final Map<String, Double> highest = new HashMap<>();
        for (int triangle = 0; triangle < whole.triangles(); triangle++) {
            for (int corner = 0; corner < 3; corner++) {
                final Vec3 one = whole.corner(triangle, corner);
                final Vec3 other = whole.corner(triangle, (corner + 1) % 3);
                final String edge = key(one).compareTo(key(other)) < 0 ? key(one) + key(other) : key(other) + key(one);
                sides.merge(edge, 1, Integer::sum);
                highest.put(edge, Math.max(one.y(), other.y()));
            }
        }
        int open = 0;
        for (final Map.Entry<String, Integer> edge : sides.entrySet()) {
            if (edge.getValue() != 2) {
                assertEquals(1, edge.getValue(), "an edge has a triangle to each side of it, or one: " + edge.getKey());
                assertEquals(0, highest.get(edge.getKey()), 1e-6, "an open edge is one it stands on: " + edge.getKey());
                open++;
            }
        }
        assertTrue(open >= 4 * 12, "open all round its foot: " + open);
    }

    @Test
    void everyWindowFacesOutOfTheCar() {
        final Mesh glass = Shape.CAR.glazed().orElseThrow();
        final Vec3 inside = new Vec3(0, 40, 0);
        assertTrue(glass.triangles() >= 12);
        for (int triangle = 0; triangle < glass.triangles(); triangle++) {
            final Vec3 centre = glass.corner(triangle, 0).plus(glass.corner(triangle, 1))
                    .plus(glass.corner(triangle, 2)).times(1.0 / 3);
            assertTrue(glass.crossed(triangle).dot(centre.minus(inside)) > 0, "triangle " + triangle);
        }
        assertTrue(Shape.WALL.glazed().isEmpty(), "a wall has no windows");
    }

    @Test
    void itsInsideIsItsSheetsAgainFacingInAndAFloorJustAboveTheGroundSoNoGroundIsSeenThroughItsWindows() {
        final Mesh inside = Shape.CAR.lined().orElseThrow();
        final Sheet floor = faces.floor();
        assertEquals(Shape.CAR.formed().triangles() + floor.triangles().size(), inside.triangles(),
                "every sheet of the body again, and the floor");
        final Vec3 within = new Vec3(0, 40, 0);
        for (int triangle = 0; triangle < inside.triangles(); triangle++) {
            final Vec3 centre = inside.corner(triangle, 0).plus(inside.corner(triangle, 1))
                    .plus(inside.corner(triangle, 2)).times(1.0 / 3);
            assertTrue(inside.crossed(triangle).dot(centre.minus(within)) < 0, "faces in: triangle " + triangle);
        }
        assertTrue(floor.points().stream().allMatch(point -> Math.abs(point.z() - 0.5) < 1e-9), "half a centimetre up");
        assertTrue(floor.area() > 0.9 * 430 * 148 && floor.area() < 440 * 164, "as big as the car's foot: "
                + floor.area());
        assertTrue(Shape.BALL.lined().isEmpty(), "a ball is not looked into");
    }

    /** A place, to a hundredth of a millimetre: what two sheets' points along one edge have alike. */
    private String key(final Vec3 at) {
        return String.format(Locale.ENGLISH, "(%.3f %.3f %.3f)", at.x() + 0.0, at.y() + 0.0, at.z() + 0.0);
    }
}
