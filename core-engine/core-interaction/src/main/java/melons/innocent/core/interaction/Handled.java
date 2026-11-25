package melons.innocent.core.interaction;

import com.example.fxsuite.existence.Things;

/**
 * What is bound to the kinds of the tree of things, unless a world says otherwise: the one table every world begins
 * from. Fruit is carried and eaten, so an apple and a pineapple are, having nothing bound of their own. A melon is a
 * fruit too big to be eaten as it is: it has only what is bound to it, carried, cut, crushed and thrown, and so has
 * a watermelon. A slice cut from one is again only carried and eaten. A plaything is carried and thrown. What holds
 * other things, and a loose piece of something built, is carried, if it is light enough. A door is opened and shut.
 * A wagon has nothing bound, and nothing can be done with it.
 */
public record Handled() {

    /** The table. */
    public Bindings bindings() {
        return new Bindings()
                .bound(new Things.Fruit(), new Binding.Adding(Action.CARRY, Action.EAT))
                .bound(new Things.Melon(), new Binding.Only(Action.CARRY, Action.CUT, Action.CRUSH, Action.THROW))
                .bound(new Things.MelonSlice(), new Binding.Only(Action.CARRY, Action.EAT))
                .bound(new Things.Plaything(), new Binding.Adding(Action.CARRY, Action.THROW))
                .bound(new Things.Container(), new Binding.Adding(Action.CARRY))
                .bound(new Things.Piece(), new Binding.Adding(Action.CARRY))
                .bound(new Things.Door(), new Binding.Adding(Action.OPEN));
    }
}
