package com.example.fxsuite.life.body;

import java.util.Map;

/**
 * Something worn, declared: what it is, how it is cut and in what colours; nothing of whom it is on. Of a kind, in a
 * family of kinds, each kind its own numbers to set: {@link Clothing} (its {@link Underwear}, for men and for women;
 * {@link Robes}; {@link Tops}; {@link Legwear}), {@link Footwear}, {@link Accessory} and {@link Hair}. The families are
 * the tree the wardrobe bench shows, read from the kinds themselves.
 * Put on a body ({@link Dressed}), it is made to that body: fitted to its contour, laid on what is under it, or hung
 * from it; moving with it. Any on any body; someone wears a few, inner first ({@link Outfit}).
 */
public sealed interface Wear permits Wear.Clothing, Wear.Footwear, Wear.Accessory, Wear.Hair {

    /** What it is called. */
    String name();

    /** What is worn on the body: fitted to it, close on it, or hanging from it. */
    sealed interface Clothing extends Wear permits Underwear, Robes, Tops, Legwear {
    }

    /** What is worn next to the skin, under the rest. */
    sealed interface Underwear extends Clothing permits ForMen, ForWomen {
    }

    /** Men's underwear. */
    sealed interface ForMen extends Underwear permits Briefs, BoxerBriefs, Undershirt {
    }

    /** Women's underwear. */
    sealed interface ForWomen extends Underwear permits Bra, Panties {
    }

    /** Long garments, from the shoulders to the ankles. */
    sealed interface Robes extends Clothing permits Robe {
    }

    /** What is worn on the upper body. */
    sealed interface Tops extends Clothing permits Shirt, Jacket, Hoodie, Jersey {
    }

    /** What is worn on the legs. */
    sealed interface Legwear extends Clothing permits Leggings, Trousers, Shorts, Socks {
    }

    /** Hair, worn as clothes are: cut and coloured, on any head. */
    sealed interface Hair extends Wear permits ShortHair, LongHair, Bun {
    }

    /** What is worn on the feet. */
    sealed interface Footwear extends Wear permits Shoes, Boots, Trainers {
    }

    /** What is worn over the clothes: laid on whatever is under it. */
    sealed interface Accessory extends Wear permits Sash, Belt, Apron, Cap, Helmet, Badge {
    }

    /**
     * A long robe: its body from the waist up round the neck, fitted to the body's contour with {@code ease} to spare;
     * a crossed collar, its left lapel over its right, in {@code trim}; sleeves to the elbow and on, either wide bells
     * from the elbow ({@code wideSleeves}) or fitted to the wrist; its skirt hanging from the waist to {@code hem} over the
     * ground, flaring out by {@code flare}, clear of the legs.
     *
     * @param colour      its cloth
     * @param trim        its collar and its cuffs
     * @param ease        how loose, cm
     * @param hem         how high its hem over the ground, cm
     * @param flare       how far its skirt flares by the hem, as a share of the waist
     * @param wideSleeves whether its sleeves are wide bells from the elbow
     */
    record Robe(String colour, String trim, double ease, double hem, double flare, boolean wideSleeves)
            implements Robes {

        @Override
        public String name() {
            return "Robe";
        }
    }

    /**
     * A shirt: from the waist up round the neck, fitted to the body's contour with {@code ease} to spare; its sleeves to
     * the elbow, or on to the wrist ({@code longSleeves}).
     *
     * @param colour      its cloth
     * @param ease        how loose, cm
     * @param longSleeves whether its sleeves go on to the wrist
     */
    record Shirt(String colour, double ease, boolean longSleeves) implements Tops {

        @Override
        public String name() {
            return "Shirt";
        }
    }

    /** Leggings, close on the legs, from the hips to the ankles. */
    record Leggings(String colour) implements Legwear {

        @Override
        public String name() {
            return "Leggings";
        }
    }

    /** Cloth shoes, close on the feet. */
    record Shoes(String colour) implements Footwear {

        @Override
        public String name() {
            return "Shoes";
        }
    }

    /**
     * Boots: on the feet and up the shins, {@code height} of the way to the knee.
     *
     * @param colour their leather
     * @param height how far up the shin, as a share of it
     */
    record Boots(String colour, double height) implements Footwear {

        @Override
        public String name() {
            return "Boots";
        }
    }

    /**
     * A sash worn across the body, as a kasaya is: over the left shoulder, down across the chest to the right hip,
     * round and up the back; laid on whatever is under it.
     *
     * @param colour its cloth
     * @param wide   how wide, cm
     */
    record Sash(String colour, double wide) implements Accessory {

        @Override
        public String name() {
            return "Sash";
        }
    }

    /**
     * A belt round the waist, laid on whatever is under it.
     *
     * @param colour its leather or cloth
     * @param wide   how wide, cm
     */
    record Belt(String colour, double wide) implements Accessory {

        @Override
        public String name() {
            return "Belt";
        }
    }

    /**
     * Briefs: close on the hips, from the crotch up to {@code rise} of the way to the waist, the legs cut close at the
     * tops of the thighs; a waistband round the top.
     *
     * @param colour     their cloth
     * @param waistband  their waistband
     * @param rise       how high they come up, as a share of the way from the crotch to the top of the hips
     */
    record Briefs(String colour, String waistband, double rise) implements ForMen {

        @Override
        public String name() {
            return "Briefs";
        }
    }

    /**
     * Boxer briefs: as briefs, and on down the thighs {@code length} of the way to the knee.
     *
     * @param colour    their cloth
     * @param waistband their waistband
     * @param rise      how high they come up, as briefs do
     * @param length    how far down the thigh, as a share of it
     */
    record BoxerBriefs(String colour, String waistband, double rise, double length) implements ForMen {

        @Override
        public String name() {
            return "Boxer briefs";
        }
    }

    /**
     * An undershirt, a vest: from the waist up round the neck, close on the body's contour, {@code ease} to spare; no
     * sleeves.
     *
     * @param colour its cloth
     * @param ease   how loose, cm
     */
    record Undershirt(String colour, double ease) implements ForMen {

        @Override
        public String name() {
            return "Undershirt";
        }
    }

    /**
     * A bra: a band round the ribs under the chest, {@code band} wide; two cups on the chest; a strap from each over its
     * shoulder to the band behind.
     *
     * @param colour its cloth
     * @param band   how wide its band, cm
     * @param cups   how deep its cups stand out from the chest, cm
     */
    record Bra(String colour, double band, double cups) implements ForWomen {

        @Override
        public String name() {
            return "Bra";
        }
    }

    /**
     * Panties: close on the hips, from the crotch up to {@code rise} of the way to the waist, cut high at the legs; a
     * waistband round the top.
     *
     * @param colour    their cloth
     * @param waistband their waistband
     * @param rise      how high they come up, as a share of the way from the crotch to the top of the hips
     */
    record Panties(String colour, String waistband, double rise) implements ForWomen {

        @Override
        public String name() {
            return "Panties";
        }
    }

    /**
     * A jacket: from below the hips up round the neck, fitted to the body's contour, loose; its sleeves to the wrist.
     *
     * @param colour its cloth
     * @param ease   how loose, cm
     * @param hem    how far below the hip joints it comes, cm
     */
    record Jacket(String colour, double ease, double hem) implements Tops {

        @Override
        public String name() {
            return "Jacket";
        }
    }

    /**
     * A hoodie: from the hips up round the neck, fitted to the body's contour, its sleeves to the wrist; its hood
     * lying down at the back of the neck; a pocket across its front.
     *
     * @param colour its cloth
     * @param ease   how loose, cm
     */
    record Hoodie(String colour, double ease) implements Tops {

        @Override
        public String name() {
            return "Hoodie";
        }
    }

    /**
     * A sports jersey: as a short-sleeved shirt, its collar in {@code trim}.
     *
     * @param colour its cloth
     * @param trim   its collar
     * @param ease   how loose, cm
     */
    record Jersey(String colour, String trim, double ease) implements Tops {

        @Override
        public String name() {
            return "Jersey";
        }
    }

    /**
     * Trousers: from the waist round the hips, down each leg to {@code length} of the way to the ankle, fitted to the
     * body's contour above the knee and hanging straight below it.
     *
     * @param colour their cloth
     * @param ease   how loose, cm
     * @param length how far down the leg, as a share of it, to the ankle
     */
    record Trousers(String colour, double ease, double length) implements Legwear {

        @Override
        public String name() {
            return "Trousers";
        }
    }

    /**
     * Shorts: from the waist round the hips, down each thigh {@code length} of the way to the knee.
     *
     * @param colour their cloth
     * @param ease   how loose, cm
     * @param length how far down the thigh, as a share of it
     */
    record Shorts(String colour, double ease, double length) implements Legwear {

        @Override
        public String name() {
            return "Shorts";
        }
    }

    /**
     * Socks: on the feet and up the shins {@code height} of the way to the knee.
     *
     * @param colour their knit
     * @param height how far up the shin, as a share of it
     */
    record Socks(String colour, double height) implements Legwear {

        @Override
        public String name() {
            return "Socks";
        }
    }

    /**
     * Trainers: close on the feet, on soles of their own colour.
     *
     * @param colour their uppers
     * @param sole   their soles
     */
    record Trainers(String colour, String sole) implements Footwear {

        @Override
        public String name() {
            return "Trainers";
        }
    }

    /**
     * An apron: tied round the waist, hanging before the legs {@code length} down; with a bib up the chest, if
     * {@code bib}.
     *
     * @param colour its cloth
     * @param length how far down from the waist, cm
     * @param bib    whether it has a bib up the chest
     */
    record Apron(String colour, double length, boolean bib) implements Accessory {

        @Override
        public String name() {
            return "Apron";
        }
    }

    /** A cap: its crown on the top of the head, its peak out over the brow. */
    record Cap(String colour) implements Accessory {

        @Override
        public String name() {
            return "Cap";
        }
    }

    /** A helmet: a hard shell over the head, down to the ears. */
    record Helmet(String colour) implements Accessory {

        @Override
        public String name() {
            return "Helmet";
        }
    }

    /**
     * A badge on a lanyard: a card at the chest, on a cord round the neck.
     *
     * @param card its card
     * @param cord its cord
     */
    record Badge(String card, String cord) implements Accessory {

        @Override
        public String name() {
            return "Badge";
        }
    }

    /**
     * Short hair: over the top and back of the head, down to its hairline, {@code thick} deep.
     *
     * @param colour its colour
     * @param thick  how deep, cm
     */
    record ShortHair(String colour, double thick) implements Hair {

        @Override
        public String name() {
            return "Short hair";
        }
    }

    /**
     * Long hair: as short hair, and falling down the back of the head and the neck {@code length} below it.
     *
     * @param colour its colour
     * @param length how far it falls below the head, cm
     */
    record LongHair(String colour, double length) implements Hair {

        @Override
        public String name() {
            return "Long hair";
        }
    }

    /** Hair drawn up into a bun at the back of the head. */
    record Bun(String colour) implements Hair {

        @Override
        public String name() {
            return "Bun";
        }
    }

    /** Each kind, as it is first put on. */
    Map<Class<? extends Wear>, Wear> FIRST = Map.ofEntries(
            Map.entry(Briefs.class, new Briefs("#f2efe9", "#3a3f46", 0.65)),
            Map.entry(BoxerBriefs.class, new BoxerBriefs("#2f3a4a", "#1c1f24", 0.68, 0.35)),
            Map.entry(Undershirt.class, new Undershirt("#f2efe9", 0.3)),
            Map.entry(Bra.class, new Bra("#e9d7d2", 3, 2.5)),
            Map.entry(Panties.class, new Panties("#e9d7d2", "#d6bfb8", 0.6)),
            Map.entry(Robe.class, new Robe("#8a5a2b", "#2a2420", 1.5, 11, 0.35, true)),
            Map.entry(Shirt.class, new Shirt("#e9e4d8", 0.8, true)),
            Map.entry(Jacket.class, new Jacket("#3a3f45", 2.2, 8)),
            Map.entry(Hoodie.class, new Hoodie("#4a5d7a", 1.8)),
            Map.entry(Jersey.class, new Jersey("#c0392b", "#f1f0ea", 1)),
            Map.entry(Leggings.class, new Leggings("#e8e4da")),
            Map.entry(Trousers.class, new Trousers("#2e3440", 1.4, 0.95)),
            Map.entry(Shorts.class, new Shorts("#d8cfb8", 1.4, 0.45)),
            Map.entry(Socks.class, new Socks("#f1f0ea", 0.35)),
            Map.entry(Shoes.class, new Shoes("#262321")),
            Map.entry(Boots.class, new Boots("#3b2a1f", 0.45)),
            Map.entry(Trainers.class, new Trainers("#e9e4d8", "#f7f7f5")),
            Map.entry(Sash.class, new Sash("#7a2a1c", 13)),
            Map.entry(Belt.class, new Belt("#3b2a1f", 4)),
            Map.entry(Apron.class, new Apron("#e8e4da", 45, false)),
            Map.entry(Cap.class, new Cap("#c8102e")),
            Map.entry(Helmet.class, new Helmet("#d0d4d8")),
            Map.entry(Badge.class, new Badge("#ffffff", "#c8102e")),
            Map.entry(ShortHair.class, new ShortHair("#2a1d14", 1)),
            Map.entry(LongHair.class, new LongHair("#6b4a2b", 18)),
            Map.entry(Bun.class, new Bun("#1c1c1c")));

    /** {@code kind} as it is first put on. */
    static Wear first(final Class<? extends Wear> kind) {
        return FIRST.get(kind);
    }
}
