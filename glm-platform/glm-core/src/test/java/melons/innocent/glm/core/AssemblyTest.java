package melons.innocent.glm.core;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A platform put together from its connections: a beam down the middle, an arm each side joined to it and a wheel on
 * each arm; each part placed so its point lies on its neighbour's, the left the right's mirror; a part met again by a
 * second connection told how far its points are apart; a part not reached told; and a point a part has not refused.
 */
class AssemblyTest {

    private final Mesh nothing = new Mesh(List.of(), List.of(), List.of());

    private final Part beam = new Part("beam", Group.SUBFRAMES, false, List.of(
            new Point("arm, right", new Vec3(50, 0, 0)), new Point("arm, left", new Vec3(-50, 0, 0)),
            new Point("stay, right", new Vec3(50, 30, 0)), new Point("stay, left", new Vec3(-50, 30, 0))), nothing);
    private final Part arm = new Part("arm", Group.LINKS, true, List.of(new Point("inner", new Vec3(0, 0, 0)),
            new Point("outer", new Vec3(40, 0, 0)), new Point("stay", new Vec3(0, 30, 0))), nothing);
    private final Part wheel = new Part("wheel", Group.WHEELS, true, List.of(new Point("hub", new Vec3(0, 0, 0))),
            nothing);

    @Test
    void eachPartPlacedOnItsNeighboursPointTheLeftTheRightsMirror() {
        final Assembled built = new Assembly(new Platform("test", beam, List.of(
                new Connection("arm", beam, "arm, right", arm, "inner", Joining.BUSHED),
                new Connection("wheel", arm, "outer", wheel, "hub", Joining.HINGE)))).assembled();
        assertEquals(List.of(), built.problems(1e-9));
        assertEquals(new Vec3(90, 0, 0), built.at(new PartAt(wheel, Side.RIGHT)));
        assertEquals(new Vec3(-90, 0, 0), built.at(new PartAt(wheel, Side.LEFT)));
        assertEquals(5, built.placed().size(), "the beam once, the arm and the wheel each side");
    }

    @Test
    void aPartMetAgainToldHowFarApart() {
        final Assembled built = new Assembly(new Platform("test", beam, List.of(
                new Connection("arm", beam, "arm, right", arm, "inner", Joining.BUSHED),
                new Connection("stay", beam, "stay, right", arm, "stay", Joining.BOLTED)))).assembled();
        assertEquals(2, built.gaps().size(), "each side met again once");
        assertTrue(built.gaps().stream().allMatch(g -> g.apart() < 1e-9), "met where it is");
    }

    @Test
    void whatIsNotReachedToldAndAPointItHasNotRefused() {
        final Part loose = new Part("loose", Group.TYRES, true, List.of(new Point("rim", new Vec3(0, 0, 0))),
                nothing);
        final Assembled built = new Assembly(new Platform("test", beam, List.of(
                new Connection("arm", beam, "arm, right", arm, "inner", Joining.BUSHED),
                new Connection("loose", wheel, "hub", loose, "rim", Joining.SEATED)))).assembled();
        assertEquals(4, built.unreached().size(), "the wheel and its tyre, each side");
        assertThrows(IllegalArgumentException.class, () -> new Connection("bad", beam, "nowhere", arm, "inner",
                Joining.BOLTED));
        final Part lopsided = new Part("lopsided", Group.UNDERBODY, false, List.of(new Point("mount, right",
                new Vec3(10, 0, 0))), nothing);
        assertThrows(IllegalArgumentException.class, () -> new Connection("one side", lopsided, "mount, right",
                wheel, "hub", Joining.BOLTED), "a pair joined to a part down the middle on its right, and its left");
    }
}
