package com.example.fxsuite.vulkan.form;

import com.example.fxsuite.vulkan.space.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Letters drawn as strokes, the way an instrument's display draws them: each letter a few straight strokes on a grid
 * four wide and six high, written out here as the points each stroke runs through, two figures a point (across, then
 * up), the strokes parted by a space. Nothing stored: the letters are these lines of code. A letter's form is its
 * strokes as thin flat bars, a unit high, lying on the plane its points are on, facing back toward whoever reads
 * it, which is the way a camera looks from (less along its third way). Letters a form cannot be made of are refused.
 */
public record Lettering() {

    /** How wide the grid is, and how high: a letter is a unit high, so a sixth of a unit a step. */
    private static final double HIGH = 6;
    /** How far apart letters are set, of a letter's height; and how thick a stroke is. */
    private static final double SET = 5.0 / 6;
    private static final double THICK = 0.11;

    /** Each letter's strokes. */
    private Map<Character, String> strokes() {
        return Map.ofEntries(
                Map.entry('0', "0040460600 0046"), Map.entry('1', "1526 2620 1030"),
                Map.entry('2', "05163645440040"), Map.entry('3', "05163645443313 334241301001"),
                Map.entry('4', "3036 360242"), Map.entry('5', "460604344341301001"),
                Map.entry('6', "453616050110304142331302"), Map.entry('7', "064610"),
                Map.entry('8', "13040516364544331302011030414233"), Map.entry('9', "011030414536160504133344"),
                Map.entry('A', "0004264440 0343"), Map.entry('B', "00063645443303 3342413000"),
                Map.entry('C', "4536160501103041"), Map.entry('D', "00062644422000"),
                Map.entry('E', "46060040 0333"), Map.entry('F', "460600 0333"),
                Map.entry('G', "45361605011030414323"), Map.entry('H', "0006 4046 0343"),
                Map.entry('I', "1636 2620 1030"), Map.entry('J', "4641301001"),
                Map.entry('K', "0006 460340"), Map.entry('L', "060040"),
                Map.entry('M', "0006234640"), Map.entry('N', "00064046"),
                Map.entry('O', "010516364541301001"), Map.entry('P', "00063645443303"),
                Map.entry('Q', "010516364541301001 2240"), Map.entry('R', "00063645443303 3340"),
                Map.entry('S', "453616050413334241301001"), Map.entry('T', "0646 2620"),
                Map.entry('U', "060110304146"), Map.entry('V', "062046"),
                Map.entry('W', "0610233046"), Map.entry('X', "0046 0640"),
                Map.entry('Y', "062346 2320"), Map.entry('Z', "06460040"),
                Map.entry('-', "0333"), Map.entry('+', "0333 2125"), Map.entry('.', "2021"),
                Map.entry(':', "2122 2425"), Map.entry('/', "0046"), Map.entry('|', "2026"),
                Map.entry('<', "400346"), Map.entry('>', "004306"), Map.entry('[', "30101636"),
                Map.entry(']', "10303616"), Map.entry('%', "0046 0516 3040"), Map.entry('*', "2636352526"));
    }

    /** Whether there is a form for {@code letter}; a space is none, and needs none. */
    public boolean has(final char letter) {
        return strokes().containsKey(Character.toUpperCase(letter));
    }

    /** How far along the next letter is set, of a letter's height. */
    public double set() {
        return SET;
    }

    /** {@code letter}'s form: its strokes as bars, from its lower left corner, a unit high. */
    public Mesh form(final char letter) {
        final String said = strokes().get(Character.toUpperCase(letter));
        if (said == null) {
            throw new IllegalArgumentException("no letter is drawn for " + letter);
        }
        final Forming forming = new Forming(new Vec3(0, 0, -1));
        for (final String stroke : said.split(" ")) {
            final List<Vec3> points = new ArrayList<>();
            for (int at = 0; at + 1 < stroke.length(); at += 2) {
                points.add(new Vec3((stroke.charAt(at) - '0') / HIGH, (stroke.charAt(at + 1) - '0') / HIGH, 0));
            }
            for (int piece = 0; piece + 1 < points.size(); piece++) {
                barred(forming, points.get(piece), points.get(piece + 1), THICK);
            }
            if (points.size() == 1) {
                barred(forming, points.getFirst(), points.getFirst().plus(new Vec3(0, THICK / 2, 0)), THICK);
            }
        }
        return forming.formed();
    }

    /** A bar from {@code from} to {@code to}, {@code thick} across, its ends carried on by half that. */
    public void barred(final Forming forming, final Vec3 from, final Vec3 to, final double thick) {
        final Vec3 along = to.minus(from);
        final Vec3 way = along.length() < 1e-12 ? new Vec3(1, 0, 0) : along.unit();
        final Vec3 across = new Vec3(-way.y(), way.x(), 0).times(thick / 2);
        final Vec3 a = from.minus(way.times(thick / 2));
        final Vec3 b = to.plus(way.times(thick / 2));
        final Vec3 facing = new Vec3(0, 0, -1);
        final int p = forming.point(a.minus(across), facing);
        final int q = forming.point(b.minus(across), facing);
        final int r = forming.point(b.plus(across), facing);
        final int s = forming.point(a.plus(across), facing);
        forming.faced(p, q, r);
        forming.faced(p, r, s);
    }
}
