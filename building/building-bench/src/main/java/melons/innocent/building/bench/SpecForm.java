package melons.innocent.building.bench;

import com.example.fxsuite.building.Spec;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A specification set field by field, made from the specification itself: a specification is a record, so each of its
 * components has a control, a whole number or a measure as a field to type in, a choice among an enumeration's as a
 * list to pick from. Changed, the specification is made again from what the controls say, and told to whoever asked;
 * what its own checks refuse is shown under it instead. Made and used on JavaFX's thread.
 */
public final class SpecForm {

    private final Consumer<Spec<?, ?, ?>> changed;
    private final GridPane grid = new GridPane(8, 4);
    private final Label refused = new Label();
    private final List<Node> controls = new ArrayList<>();
    private Spec<?, ?, ?> shown;

    /** A form telling {@code changed} each specification it is set to. */
    public SpecForm(final Consumer<Spec<?, ?, ?>> changed) {
        this.changed = changed;
        refused.setWrapText(true);
        refused.setStyle("-fx-text-fill: #a03020;");
    }

    /** The fields, and what is refused under them. */
    public VBox node() {
        return new VBox(6, grid, refused);
    }

    /** {@code spec}'s fields shown, each its control, set as it is. */
    public void show(final Spec<?, ?, ?> spec) {
        shown = spec;
        grid.getChildren().clear();
        controls.clear();
        refused.setText("");
        final RecordComponent[] components = spec.getClass().getRecordComponents();
        for (int i = 0; i < components.length; i++) {
            final Node control = control(components[i], value(components[i], spec));
            controls.add(control);
            grid.add(new Label(components[i].getName()), 0, i);
            grid.add(control, 1, i);
        }
    }

    /** A control for {@code component}, set to {@code value}, the specification made again when it is changed. */
    private Node control(final RecordComponent component, final Object value) {
        final Class<?> type = component.getType();
        if (type.isEnum()) {
            final ComboBox<Object> choice = new ComboBox<>();
            choice.getItems().setAll(type.getEnumConstants());
            choice.setValue(value);
            choice.setOnAction(event -> made());
            return choice;
        }
        final TextField field = new TextField(type == int.class ? String.valueOf(value)
                : String.valueOf(((Number) value).doubleValue()).replaceAll("\\.0$", ""));
        field.setPrefColumnCount(8);
        field.setOnAction(event -> made());
        field.focusedProperty().addListener((property, was, now) -> {
            if (!now) {
                made();
            }
        });
        return field;
    }

    /** The specification made again from the controls, and told; or what refused it, shown. */
    private void made() {
        final RecordComponent[] components = shown.getClass().getRecordComponents();
        final Class<?>[] types = new Class<?>[components.length];
        final Object[] values = new Object[components.length];
        try {
            for (int i = 0; i < components.length; i++) {
                types[i] = components[i].getType();
                values[i] = read(types[i], controls.get(i));
            }
            final Spec<?, ?, ?> spec = (Spec<?, ?, ?>) shown.getClass().getDeclaredConstructor(types)
                    .newInstance(values);
            refused.setText("");
            shown = spec;
            changed.accept(spec);
        } catch (final InvocationTargetException e) {
            refused.setText(e.getCause().getMessage());
        } catch (final NumberFormatException e) {
            refused.setText("not a number: " + e.getMessage());
        } catch (final ReflectiveOperationException | RuntimeException e) {
            refused.setText(e.toString());
        }
    }

    /** What {@code control} says, as a {@code type}. */
    private Object read(final Class<?> type, final Node control) {
        if (control instanceof ComboBox<?> choice) {
            return choice.getValue();
        }
        final String text = ((TextField) control).getText().strip();
        return type == int.class ? (Object) Integer.parseInt(text) : (Object) Double.parseDouble(text);
    }

    /** What {@code spec} has for {@code component}. */
    private Object value(final RecordComponent component, final Spec<?, ?, ?> spec) {
        try {
            return component.getAccessor().invoke(spec);
        } catch (final ReflectiveOperationException e) {
            throw new IllegalStateException("a specification's " + component.getName() + " unread", e);
        }
    }
}
