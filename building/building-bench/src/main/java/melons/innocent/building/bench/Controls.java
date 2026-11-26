package melons.innocent.building.bench;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import melons.innocent.forms.Tint;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * A control for each kind of value a specification has, set to it, and read back: a whole number or a measure a field
 * to type in; a choice among an enumeration's a list to pick from; yes or no a box to tick; a colour a picker; and
 * something there or not a box to tick beside the control for what is there, that control idle while it is not, a
 * colour starting white. Each telling {@code changed} when it is changed. Made and used on JavaFX's thread.
 *
 * @param changed what is told when any of them is changed
 */
record Controls(Runnable changed) {

    /** What a control says, read back as the value it stands for. */
    interface Reading {

        /** What it says. */
        Object read() throws ReflectiveOperationException;
    }

    /**
     * A control, and how what it says is read.
     *
     * @param node    the control
     * @param reading how it is read
     */
    record Field(Node node, Reading reading) {
    }

    /** Whether a record of {@code type} has a control of its own here, rather than its fields one each. */
    boolean knows(final Class<?> type) {
        return type == Tint.class;
    }

    /** A control for a value of {@code type}, set to {@code value}. */
    Field of(final Type type, final Object value) {
        final Class<?> raw = (Class<?>) (type instanceof ParameterizedType p ? p.getRawType() : type);
        if (raw == Optional.class) {
            return optional(((ParameterizedType) type).getActualTypeArguments()[0], (Optional<?>) value);
        }
        if (raw == Tint.class) {
            return colour((Tint) value);
        }
        if (raw.isEnum()) {
            final ComboBox<Object> choice = new ComboBox<>();
            choice.getItems().setAll(raw.getEnumConstants());
            choice.setValue(value);
            choice.setOnAction(event -> changed.run());
            return new Field(choice, choice::getValue);
        }
        if (raw == boolean.class) {
            final CheckBox box = new CheckBox();
            box.setSelected((Boolean) value);
            box.setOnAction(event -> changed.run());
            return new Field(box, box::isSelected);
        }
        return number(raw, (Number) value);
    }

    /** A field to type {@code value} into, a whole number if {@code type} is one, else a measure. */
    private Field number(final Class<?> type, final Number value) {
        final boolean whole = type == int.class;
        final TextField field = new TextField(whole ? String.valueOf(value)
                : String.valueOf(value.doubleValue()).replaceAll("\\.0$", ""));
        field.setPrefColumnCount(8);
        field.setOnAction(event -> changed.run());
        field.focusedProperty().addListener((property, was, now) -> {
            if (!now) {
                changed.run();
            }
        });
        return new Field(field, () -> whole ? (Object) Integer.parseInt(field.getText().strip())
                : (Object) Double.parseDouble(field.getText().strip()));
    }

    /** A picker set to {@code tint}. */
    private Field colour(final Tint tint) {
        final ColorPicker picker = new ColorPicker(Color.color(tint.red(), tint.green(), tint.blue()));
        picker.setOnAction(event -> changed.run());
        return new Field(picker, () -> {
            final Color c = picker.getValue();
            return new Tint(c.getRed(), c.getGreen(), c.getBlue());
        });
    }

    /** A box ticked if {@code value} is there, beside a control for a {@code type}, idle while it is not. */
    private Field optional(final Type type, final Optional<?> value) {
        final Field inner = of(type, value.isPresent() ? value.get() : blank(type));
        final CheckBox there = new CheckBox();
        there.setSelected(value.isPresent());
        inner.node().setDisable(value.isEmpty());
        there.setOnAction(event -> {
            inner.node().setDisable(!there.isSelected());
            changed.run();
        });
        return new Field(new HBox(6, there, inner.node()), () -> there.isSelected()
                ? Optional.of(inner.reading().read()) : Optional.empty());
    }

    /** What a control for a {@code type} not there starts at. */
    private Object blank(final Type type) {
        if (type == Tint.class) {
            return new Tint(1, 1, 1);
        }
        throw new IllegalArgumentException("nothing to start a " + type.getTypeName() + " at");
    }
}
