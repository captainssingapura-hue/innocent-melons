package com.example.fxsuite.life.body;

import java.util.Locale;

/**
 * A humanoid robot's own parts, as designed: its spine, its chest and its head, each a few numbers, the frame drawn
 * from them ({@link RobotFrame}); every other part of it the skeleton's bones in metal. Refined on the robot parts
 * bench ({@link RobotPartsBench}), which reads a design out as code to be set here as the default. Nothing stored:
 * the numbers are the design.
 *
 * @param spine its spine, from the pelvis up to the head
 * @param chest its chest's cage round the spine
 * @param head  its head, a covered helmet
 */
public record RobotDesign(Spine spine, Chest chest, Head head) {

    /**
     * A spine of machined vertebrae, each a block on a dark disc, a fin behind it and arms out to its sides; so many
     * in the waist, the chest and the neck; a pair of struts up the waist beside it, if {@code struts}.
     *
     * @param waist  how many vertebrae up the waist
     * @param chest  how many up the chest
     * @param neck   how many up the neck
     * @param width  how wide a vertebra is, cm
     * @param depth  how deep
     * @param disc   how much of each vertebra's height its disc takes
     * @param fin    how far its fin stands out behind, cm
     * @param arms   how far its arms reach out to each side, as a share of its width
     * @param struts whether a strut runs up the waist on either side
     * @param strut  how thick a strut is, cm
     */
    public record Spine(int waist, int chest, int neck, double width, double depth, double disc, double fin,
                        double arms, boolean struts, double strut) {

        public Spine {
            check(waist >= 2 && waist <= 8 && chest >= 4 && chest <= 14 && neck >= 2 && neck <= 7,
                    "two to eight vertebrae up the waist, four to fourteen up the chest, two to seven up the neck");
            check(width >= 2 && width <= 10 && depth >= 2 && depth <= 10, "vertebrae from 2 to 10 cm across and deep");
            check(disc >= 0.05 && disc <= 0.5, "discs from 0.05 to 0.5 of a vertebra");
        }
    }

    /**
     * A chest's cage: so many ribs round from the spine to a plate down its front, each a flat bar so wide and so
     * thick, as broad and deep as said; a core in its middle behind the plate, glowing, if {@code core}; a yoke across
     * its back between the shoulders, if {@code yoke}.
     *
     * @param ribs   how many pairs of ribs
     * @param rib    how wide a rib is, cm, up and down
     * @param thick  how thick, cm
     * @param broad  how broad the cage is, as a share of a person's ribs
     * @param deep   how deep, front to back, cm from the spine's line
     * @param plate  whether a plate runs down its front, its ribs meeting it
     * @param core   whether a core glows in its middle
     * @param radius how big the core is round, cm
     * @param yoke   whether a yoke runs across its back between the shoulders
     */
    public record Chest(int ribs, double rib, double thick, double broad, double deep, boolean plate, boolean core,
                        double radius, boolean yoke) {

        public Chest {
            check(ribs >= 2 && ribs <= 10, "two to ten pairs of ribs");
            check(rib >= 0.4 && rib <= 4 && thick >= 0.3 && thick <= 3, "ribs from 0.4 to 4 cm wide, 0.3 to 3 thick");
            check(broad >= 0.6 && broad <= 1.4 && deep >= 5 && deep <= 14, "a cage of some breadth and depth");
        }
    }

    /**
     * A covered head: a helmet, broad, tall and deep as said, in so many flat panels round; a visor across where the
     * eyes would be, so wide and tall; a pod at either side; a guard under the chin, if {@code chin}; a crest along
     * its top, if {@code crest}. No face: nothing in it to look out of but the visor's glow.
     *
     * @param broad    how broad, as a share of a person's head
     * @param tall     how tall
     * @param deep     how deep
     * @param panels   how many flat panels round
     * @param visor    how wide its visor is, cm
     * @param band     how tall
     * @param pods     how big its pods are round, cm
     * @param chin     whether a guard runs under its chin
     * @param crest    whether a crest runs along its top
     */
    public record Head(double broad, double tall, double deep, int panels, double visor, double band, double pods,
                       boolean chin, boolean crest) {

        public Head {
            check(broad >= 0.7 && broad <= 1.4 && tall >= 0.7 && tall <= 1.4 && deep >= 0.7 && deep <= 1.4,
                    "a helmet from 0.7 to 1.4 times a head");
            check(panels >= 5 && panels <= 32, "five to thirty-two panels round");
            check(visor >= 4 && visor <= 16 && band >= 0.6 && band <= 6, "a visor of some width and height");
        }
    }

    /** The robot as first designed: after the exposed frames of the genre, its face covered. */
    public static final RobotDesign DEFAULT = new RobotDesign(
            new Spine(5, 10, 4, 5.2, 4.2, 0.22, 2.6, 0.7, true, 0.9),
            new Chest(6, 1.6, 0.8, 1.0, 9.0, true, true, 2.4, true),
            new Head(1.0, 1.0, 1.0, 12, 10.5, 2.4, 2.2, true, true));

    private static void check(final boolean ok, final String what) {
        if (!ok) {
            throw new IllegalArgumentException("a robot's " + what);
        }
    }

    public RobotDesign with(final Spine s) {
        return new RobotDesign(s, chest, head);
    }

    public RobotDesign with(final Chest c) {
        return new RobotDesign(spine, c, head);
    }

    public RobotDesign with(final Head h) {
        return new RobotDesign(spine, chest, h);
    }

    /** This design as code, to be set as the default. */
    public String code() {
        return String.format(Locale.ROOT, "new RobotDesign(%n"
                        + "        new Spine(%d, %d, %d, %.1f, %.1f, %.2f, %.1f, %.2f, %b, %.1f),%n"
                        + "        new Chest(%d, %.1f, %.1f, %.2f, %.1f, %b, %b, %.1f, %b),%n"
                        + "        new Head(%.2f, %.2f, %.2f, %d, %.1f, %.1f, %.1f, %b, %b))",
                spine.waist(), spine.chest(), spine.neck(), spine.width(), spine.depth(), spine.disc(), spine.fin(),
                spine.arms(), spine.struts(), spine.strut(),
                chest.ribs(), chest.rib(), chest.thick(), chest.broad(), chest.deep(), chest.plate(), chest.core(),
                chest.radius(), chest.yoke(),
                head.broad(), head.tall(), head.deep(), head.panels(), head.visor(), head.band(), head.pods(),
                head.chin(), head.crest());
    }
}
