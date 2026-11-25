package melons.innocent.glm.model;

import melons.innocent.glm.core.Connection;
import melons.innocent.glm.core.Joining;
import melons.innocent.glm.core.Part;

import java.util.List;

/**
 * The retro saloon on the GLM platform: the early-eighties notchback saloon kept as it was drawn, made larger evenly
 * to the platform's least track, on the platform's chassis; its body built as the original's was, a pressed steel
 * shell, its root its underbody, every chassis mount on it: the body sides welded along it, the bulkhead and the tail
 * panel across it, and the roof's two headers across between the body sides, a cage for the roof's glass; and its
 * four doors hung in it.
 */
public record Santana() implements Bodywork {

    private static final SantanaLines LINES = new SantanaLines();

    /** Its figures. */
    public Proportions proportions() {
        return LINES.proportions();
    }

    /** Its body's own figures. */
    public BodyFigures figures() {
        return LINES.figures();
    }

    /** It on the platform, {@code bay} its engine in its bay, or none, null. */
    public GlmPlatform platform(final EngineBay bay) {
        return new GlmPlatform(proportions(), figures(), bay, this);
    }

    @Override
    public Part root(final Proportions proportions) {
        return new SantanaShell(new ShellLines(LINES, proportions)).part();
    }

    @Override
    public List<Connection> joined(final Part root) {
        final ShellPanels panels = new ShellPanels(new ShellLines(LINES, proportions()));
        final Part side = new BodySide(LINES, root.point("body side, right").at(), panels).part();
        final Part bulkhead = panels.bulkhead();
        final Part tail = panels.tailPanel();
        final Part front = panels.header("roof header, front", LINES.frontHeader());
        final Part rear = panels.header("roof header, rear", LINES.rearHeader());
        final Part frontDoor = new Door("front door", LINES, LINES.frontDoor(), LINES.frontDivider()).part();
        final Part rearDoor = new Door("rear door", LINES, LINES.rearDoor(), LINES.rearDivider()).part();
        return List.of(
                new Connection("body side", root, "body side, right", side, "body", Joining.WELDED),
                new Connection("bulkhead", root, "bulkhead", bulkhead, "underbody", Joining.WELDED),
                new Connection("tail panel", root, "tail panel", tail, "underbody", Joining.WELDED),
                new Connection("roof header, front", side, "header, front", front, "body side, right",
                        Joining.WELDED),
                new Connection("roof header, rear", side, "header, rear", rear, "body side, right", Joining.WELDED),
                new Connection("front door", side, "front door, hinge", frontDoor, "hinge", Joining.HINGE),
                new Connection("rear door", side, "rear door, hinge", rearDoor, "hinge", Joining.HINGE));
    }
}
