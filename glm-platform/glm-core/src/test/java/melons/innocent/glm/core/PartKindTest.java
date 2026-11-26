package melons.innocent.glm.core;

import com.example.fxsuite.taxonomy.L0;
import com.example.fxsuite.taxonomy.L1;
import com.example.fxsuite.taxonomy.Leaf;
import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Mat4;
import com.example.fxsuite.vulkan.space.Vec3;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A part's kind: none until a tree of kinds says one; once said, it is of that kind and of every kind over it, and
 * not of another; its mirror and the part carried into another frame of the same kind.
 */
class PartKindTest {

    private final Part arm = new Part("arm", Group.LINKS, true, List.of(new Point("inner", new Vec3(1, 0, 0))),
            new Mesh(List.of(), List.of(), List.of()), new Made(Material.STEEL, false));

    @Test
    void noneUntilSaidThenOfItAndEveryKindOverIt() {
        assertEquals(Optional.empty(), arm.kind());
        assertFalse(arm.is(new Link()));
        final Part kinded = arm.as(new Arm());
        assertEquals(Optional.of(new Arm()), kinded.kind());
        assertTrue(kinded.is(new Arm()) && kinded.is(new Link()) && kinded.is(new Bit()));
        assertFalse(kinded.is(new Wheel()));
        assertEquals(arm.name(), kinded.name());
        assertEquals(arm.points(), kinded.points());
    }

    @Test
    void itsMirrorAndItCarriedOfTheSameKind() {
        final Part kinded = arm.as(new Arm());
        assertEquals(Optional.of(new Arm()), kinded.mirrored().kind());
        assertEquals(Optional.of(new Arm()), new Carrying(new Mat4()).carried(kinded).kind());
    }

    private record Bit() implements L0 {
    }

    private record Link() implements L1<Bit> { public Bit parent() { return new Bit(); } }

    private record Arm() implements Leaf<Link> { public Link parent() { return new Link(); } }

    private record Wheel() implements Leaf<Bit> { public Bit parent() { return new Bit(); } }
}
