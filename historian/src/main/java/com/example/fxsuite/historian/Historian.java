package com.example.fxsuite.historian;

import hue.captains.singapura.tao.ontology.StatelessFunctionalObject;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * The historian: every civilization the worlds draw on and every era of theirs, listed once, read once; what is wrong
 * with them all found at once ({@link #problems}); and the questions asked of them: a civilization's lineage, the eras
 * open to it, what ran in a year, what ran at the same time as an era elsewhere. Nothing is found by scanning: what is
 * here is what is listed.
 */
public record Historian() implements StatelessFunctionalObject {

    public static final Historian INSTANCE = new Historian();

    /** Every civilization, the root first. */
    public List<Civilization> civilizations() {
        return List.of(Civilization.ROOT, Chinese.IT, StraitsChinese.IT, Japanese.IT, Korean.IT, Malay.IT, Indian.IT,
                Islamic.IT, European.IT, British.IT, Egyptian.IT, Mesoamerican.IT);
    }

    /** Every era, by civilization, each civilization's oldest first. */
    public List<Era<?>> eras() {
        return Stream.of(CommonEras.INSTANCE.all(), ChineseEras.INSTANCE.all(), StraitsChineseEras.INSTANCE.all(),
                JapaneseEras.INSTANCE.all(), KoreanEras.INSTANCE.all(), MalayEras.INSTANCE.all(),
                IndianEras.INSTANCE.all(), IslamicEras.INSTANCE.all(), EuropeanEras.INSTANCE.all(),
                BritishEras.INSTANCE.all(), EgyptianEras.INSTANCE.all(), MesoamericanEras.INSTANCE.all())
                .flatMap(List::stream).toList();
    }

    /**
     * Everything wrong with them, all of it, none if nothing is: a name used twice; a civilization whose parent is not
     * listed, or whose lineage does not reach the root, or which its parent does not include (its declared parent and
     * Java's disagreeing), or which begins before its parent did; an era that ends before it begins, whose
     * civilization is not listed, that runs outside its civilization's years, or that is part of an era not listed,
     * of another civilization, or whose years it does not lie within.
     */
    public List<String> problems() {
        final List<String> problems = new ArrayList<>();
        final Set<String> names = new HashSet<>();
        for (final Civilization c : civilizations()) {
            if (!names.add("civilization " + c.name())) {
                problems.add("two civilizations called " + c.name());
            }
            if (c == Civilization.ROOT) {
                continue;
            }
            if (!civilizations().contains(c.parent())) {
                problems.add(c.name() + ": its parent " + c.parent().name() + " is not listed");
            } else if (lineage(c).getLast() != Civilization.ROOT) {
                problems.add(c.name() + ": its lineage does not reach the root");
            }
            if (!c.parent().includes(c) || c.includes(c.parent())) {
                problems.add(c.name() + ": its parent, " + c.parent().name() + ", is not the one its type extends");
            }
            if (c.from().isBefore(c.parent().from())) {
                problems.add(c.name() + ": begins before " + c.parent().name() + " did");
            }
        }
        for (final Era<?> e : eras()) {
            final Civilization c = e.civilization();
            if (!names.add("era " + c.name() + " " + e.name())) {
                problems.add("two eras of " + c.name() + " called " + e.name());
            }
            if (e.to().isBefore(e.from())) {
                problems.add(e.name() + ": ends before it begins");
            }
            if (!civilizations().contains(c)) {
                problems.add(e.name() + ": its civilization " + c.name() + " is not listed");
            } else if (e.from().isBefore(c.from()) || e.to().isAfter(c.to())) {
                problems.add(e.name() + ": runs outside " + c.name() + "'s years");
            }
            final Era<?> within = e.within();
            if (within != null) {
                if (!eras().contains(within)) {
                    problems.add(e.name() + ": part of " + within.name() + ", which is not listed");
                } else if (!within.civilization().equals(c)) {
                    problems.add(e.name() + ": part of " + within.name() + ", another civilization's");
                } else if (e.from().isBefore(within.from()) || e.to().isAfter(within.to())) {
                    problems.add(e.name() + ": runs outside " + within.name() + "'s years");
                }
            }
        }
        return problems;
    }

    /** {@code c} and what it grew from, in turn, to the root. */
    public List<Civilization> lineage(final Civilization c) {
        final List<Civilization> lineage = new ArrayList<>(List.of(c));
        Civilization at = c;
        while (at != Civilization.ROOT && lineage.size() <= civilizations().size()) {
            at = at.parent();
            lineage.add(at);
        }
        return lineage;
    }

    /** The eras open to {@code c}: its own, then each of its ancestors' in turn, the root's (everyone's) last. */
    public List<Era<?>> erasOf(final Civilization c) {
        final List<Era<?>> eras = new ArrayList<>();
        for (final Civilization at : lineage(c)) {
            eras().stream().filter(e -> e.civilization().equals(at)).forEach(eras::add);
        }
        return eras;
    }

    /** The eras running in {@code year}, every civilization's. */
    public List<Era<?>> at(final Year year) {
        return eras().stream().filter(e -> e.spans(year)).toList();
    }

    /** The eras of other civilizations running at some time in {@code era}'s years: its contemporaries. */
    public List<Era<?>> contemporaries(final Era<?> era) {
        return eras().stream().filter(e -> !e.civilization().equals(era.civilization()) && e.overlaps(era)).toList();
    }
}
