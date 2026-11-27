package melons.innocent.glm.engine;

import com.example.fxsuite.vulkan.form.Mesh;
import com.example.fxsuite.vulkan.space.Vec3;
import melons.innocent.glm.core.Group;
import melons.innocent.glm.core.Made;
import melons.innocent.glm.core.Material;
import melons.innocent.glm.core.Part;
import melons.innocent.glm.core.Point;
import melons.innocent.glm.existences.EngineParts;
import melons.innocent.vehiclelab.forms.Outlines;
import melons.innocent.vehiclelab.forms.Plate;
import melons.innocent.vehiclelab.forms.Shapes;
import melons.innocent.vehiclelab.forms.Sleeve;
import melons.innocent.vehiclelab.forms.Spot;

import java.util.ArrayList;
import java.util.List;

/**
 * A bank's cylinder head, cast in aluminium, laid out as {@link HeadFigures} says: its fire deck on the block's deck,
 * bored where each cylinder meets it; over each bore its chamber, a pent roof; in each chamber the spark plug's tube up
 * through the head from the ridge's middle, and a guide's boss up each valve's stem; from each inlet valve's seat a
 * port curving out to the inlet side, from each exhaust valve's an exhaust port out to the other; its side walls
 * standing to its camshafts' axes, opened where the ports come out; and a tower across between each pair of cylinders
 * and at each end, its camshafts' bearings in its top and their caps bolted over them ({@link CamTower}). Open on
 * top, for its camshafts and its cover. Its points: the middle of its deck, bolted to the block's; the middle of its
 * top, where its cover is bolted; each chamber at its deck; each cylinder's exhaust port, between its two exhaust
 * ports where they come out of its side; each camshaft's axis at its front; and each valve's seat and each valve
 * spring's.
 *
 * @param figures the engine's figures
 * @param bank    which bank it is, from nought
 */
record CylinderHead(EngineFigures figures, int bank) {

    private static final int ROUND = 20;
    /** How long the plug's tube and a guide's boss are round, outside and in. */
    private static final double PLUG = 1.4;
    private static final double PLUG_BORE = 0.9;
    private static final double GUIDE = 0.75;
    private static final double GUIDE_BORE = 0.35;
    /** How high over the deck the ports come out of the head's sides. */
    private static final double PORT = 3.8;

    /** It, called as its engine and its bank are. */
    Part part() {
        final HeadFigures head = new HeadFigures(figures);
        final List<Point> points = new ArrayList<>();
        final Vec3 deck = new BlockForm(figures).up(bank).times(figures.deck());
        points.add(new Point("deck", deck.plus(new Vec3(0, 0, figures.length() / 2))));
        points.add(new Point("top", head.top(bank)));
        for (int k = 0; k < figures.perBank(); k++) {
            points.add(new Point("chamber " + (bank + 1) + "." + (k + 1), head.at(bank, k, new Vec3(0, 0, 0))));
            points.add(new Point("exhaust port " + (bank + 1) + "." + (k + 1), head.at(bank, k,
                    new Vec3(-head.side(false), PORT, 0))));
        }
        for (final boolean inlet : List.of(true, false)) {
            final Vec3 cam = head.cam(inlet);
            points.add(new Point((inlet ? "inlet" : "exhaust") + " camshaft, front", head.at(bank, 0,
                    new Vec3(cam.x(), cam.y(), head.front(bank) - figures.along(bank, 0)))));
            for (int k = 0; k < figures.perBank(); k++) {
                for (final int side : List.of(-1, 1)) {
                    final Valve valve = new Valve(figures, bank, k, inlet, side);
                    points.add(new Point(valve.called(), valve.seat()));
                    points.add(new Point(valve.called().replace("valve", "spring seat"), valve.springAt()));
                }
            }
        }
        return new Part(called(), Group.ENGINE, false, points, mesh(head), new Made(Material.ALUMINIUM, true))
                .as(figures.twin() ? new EngineParts.TwinCamHead() : new EngineParts.CylinderHead());
    }

    /** What it is called. */
    String called() {
        return figures.name() + " cylinder head" + (figures.banks() == 2 ? ", bank " + (bank + 1) : "");
    }

    private Mesh mesh(final HeadFigures head) {
        final List<Mesh> made = new ArrayList<>();
        made.add(deck(head));
        for (final boolean inlet : List.of(true, false)) {
            made.add(side(head, inlet));
        }
        final CamTower tower = new CamTower(head, bank);
        for (final double z : towers(head)) {
            made.addAll(tower.at(z));
        }
        final Chamber chamber = new Chamber(head);
        for (int k = 0; k < figures.perBank(); k++) {
            final int at = k;
            made.add(chamber.roofed(p -> head.at(bank, at, p)));
            made.addAll(inside(head, k));
        }
        return new Shapes(ROUND).joined(made);
    }

    /** The fire deck, on the block's deck, bored where each cylinder meets it. */
    private Mesh deck(final HeadFigures head) {
        final Outlines outlines = new Outlines(ROUND);
        final List<List<Spot>> bores = new ArrayList<>();
        for (int k = 0; k < figures.perBank(); k++) {
            bores.add(outlines.circle(new Spot(0, figures.along(bank, k)), head.radius()));
        }
        final double front = head.front(bank);
        final double rear = head.rear(bank);
        final List<Spot> outline = List.of(new Spot(-head.side(false), front), new Spot(head.side(true), front),
                new Spot(head.side(true), rear), new Spot(-head.side(false), rear));
        return new Plate(outline, bores, head.at(bank, 0, new Vec3(0, HeadFigures.DECK / 2, -figures.along(bank, 0))),
                head.way(bank, new Vec3(1, 0, 0)), head.way(bank, new Vec3(0, 0, 1)), HeadFigures.DECK).mesh();
    }

    /** Its inlet or its exhaust side, standing from its deck to its camshafts' axes, opened where its ports come out. */
    private Mesh side(final HeadFigures head, final boolean inlet) {
        final double u = (inlet ? 1 : -1) * head.side(inlet);
        final Outlines outlines = new Outlines(ROUND);
        final List<List<Spot>> ports = new ArrayList<>();
        for (int k = 0; k < figures.perBank(); k++) {
            for (final int s : List.of(-1, 1)) {
                ports.add(outlines.circle(new Spot(figures.along(bank, k) + s * head.apart(), PORT), port(head,
                        inlet)));
            }
        }
        final List<Spot> outline = List.of(new Spot(head.front(bank), 0), new Spot(head.rear(bank), 0),
                new Spot(head.rear(bank), head.high()), new Spot(head.front(bank), head.high()));
        return new Plate(outline, ports, head.at(bank, 0, new Vec3(u, 0, -figures.along(bank, 0))),
                head.way(bank, new Vec3(0, 0, 1)), head.way(bank, new Vec3(0, 1, 0)), HeadFigures.WALL).mesh();
    }

    /** Where along the crank its towers stand: at each end, and between each pair of cylinders. */
    private List<Double> towers(final HeadFigures head) {
        final List<Double> at = new ArrayList<>(List.of(head.front(bank) + 1, head.rear(bank) - 1));
        for (int k = 0; k + 1 < figures.perBank(); k++) {
            at.add(figures.along(bank, k) + figures.spacing() / 2);
        }
        return at;
    }

    /** Over cylinder {@code k}: its plug's tube, its valves' guides and its ports. */
    private List<Mesh> inside(final HeadFigures head, final int k) {
        final List<Mesh> made = new ArrayList<>();
        final Vec3 up = head.way(bank, new Vec3(0, 1, 0));
        final double plug = head.high() - head.ridge();
        made.add(new Sleeve(head.at(bank, k, new Vec3(0, head.ridge() + plug / 2, 0)), up, plug, PLUG, PLUG_BORE,
                ROUND).mesh());
        for (final boolean inlet : List.of(true, false)) {
            final Vec3 axis = head.axis(inlet);
            for (final int s : List.of(-1, 1)) {
                final Vec3 seat = head.seat(inlet, s);
                final double from = 3.2;
                final double to = head.stem() - 2.2;
                made.add(new Sleeve(head.at(bank, k, seat.plus(axis.times((from + to) / 2))), head.way(bank, axis),
                        to - from, GUIDE, GUIDE_BORE, ROUND).mesh());
                made.addAll(housing(head, new Valve(figures, bank, k, inlet, s)));
                final Vec3 bend = seat.plus(axis.times(1.6));
                final double out = (inlet ? 1 : -1) * head.side(inlet);
                final Vec3 elbow = new Vec3((bend.x() + out) / 2, PORT, seat.z());
                final Vec3 exit = new Vec3(out, PORT, seat.z());
                made.add(new Shapes(ROUND).pipe(List.of(head.at(bank, k, seat), head.at(bank, k, bend),
                        head.at(bank, k, elbow), head.at(bank, k, exit)), port(head, inlet)));
            }
        }
        return made;
    }

    /**
     * Where {@code valve} is worked: the pad its spring sits on, round its guide; and the bore its bucket slides in,
     * as deep as the bucket goes with the valve open the most.
     */
    private List<Mesh> housing(final HeadFigures head, final Valve valve) {
        final Vec3 way = valve.way();
        final Vec3 pad = valve.springAt();
        final double top = head.cam(valve.inlet()).minus(head.seat(valve.inlet(), 0)).length() - head.journal() * 1.1;
        final double from = head.stem() - 0.3 - new ValveTiming(figures).most() - 0.2;
        final Vec3 seat = valve.seat();
        final double bore = valve.bucket() + 0.05;
        return List.of(new Sleeve(pad.minus(way.times(0.1)), way, 0.2, 1.3, GUIDE_BORE, ROUND).mesh(),
                new Sleeve(seat.plus(way.times((from + top) / 2)), way, top - from, bore + 0.35, bore, ROUND).mesh());
    }

    /** How far round a port is: an inlet's, or an exhaust's, a little narrower than its valve. */
    private double port(final HeadFigures head, final boolean inlet) {
        return head.valve(inlet) / 2 * 0.8;
    }
}
