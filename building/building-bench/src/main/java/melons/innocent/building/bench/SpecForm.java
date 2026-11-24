package melons.innocent.building.bench;

import com.example.fxsuite.building.Spec;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.function.Consumer;

/**
 * A specification set field by field, made from the specification itself: a specification is a record, so each of its
 * components has a control ({@link Controls}), a record within it its own fields under its name. Changed, the
 * specification is made again from what the controls say, and told to whoever asked; what its own checks refuse is
 * shown under it instead. Made and used on JavaFX's thread.
 */
public final class SpecForm {

    private final Consumer<Spec<?, ?, ?>> changed;
    private final VBox fields = new VBox();
    private final Label refused = new Label();
    private Controls.Reading reading;

    /** A form telling {@code changed} each specification it is set to. */
    public SpecForm(final Consumer<Spec<?, ?, ?>> changed) {
        this.changed = changed;
        refused.setWrapText(true);
        refused.setStyle("-fx-text-fill: #a03020;");
    }

    /** The fields, and what is refused under them. */
    public VBox node() {
        return new VBox(6, fields, refused);
    }

    /** {@code spec}'s fields shown, each its control, set as it is. */
    public void show(final Spec<?, ?, ?> spec) {
        refused.setText("");
        final Controls.Field field = record(spec.getClass(), spec, new Controls(this::made));
        fields.getChildren().setAll(field.node());
        reading = field.reading();
    }

    /** The fields of a record of {@code type}, set as {@code value} is, read back as one of it. */
    private Controls.Field record(final Class<?> type, final Object value, final Controls controls) {
        final GridPane grid = new GridPane(8, 4);
        final RecordComponent[] components = type.getRecordComponents();
        final Class<?>[] types = new Class<?>[components.length];
        final Controls.Reading[] readings = new Controls.Reading[components.length];
        for (int i = 0; i < components.length; i++) {
            final Object part = value(components[i], value);
            types[i] = components[i].getType();
            final Controls.Field field = types[i].isRecord() && !controls.knows(types[i])
                    ? record(types[i], part, controls) : controls.of(components[i].getGenericType(), part);
            readings[i] = field.reading();
            grid.add(new Label(components[i].getName()), 0, i);
            grid.add(field.node(), 1, i);
        }
        return new Controls.Field(grid, () -> {
            final Object[] values = new Object[readings.length];
            for (int i = 0; i < readings.length; i++) {
                values[i] = readings[i].read();
            }
            return type.getDeclaredConstructor(types).newInstance(values);
        });
    }

    /** The specification made again from the controls, and told; or what refused it, shown. */
    private void made() {
        try {
            final Spec<?, ?, ?> spec = (Spec<?, ?, ?>) reading.read();
            refused.setText("");
            changed.accept(spec);
        } catch (final InvocationTargetException e) {
            refused.setText(e.getCause().getMessage());
        } catch (final NumberFormatException e) {
            refused.setText("not a number: " + e.getMessage());
        } catch (final ReflectiveOperationException | RuntimeException e) {
            refused.setText(e.toString());
        }
    }

    /** What {@code of} has for {@code component}. */
    private Object value(final RecordComponent component, final Object of) {
        try {
            return component.getAccessor().invoke(of);
        } catch (final ReflectiveOperationException e) {
            throw new IllegalStateException("a specification's " + component.getName() + " unread", e);
        }
    }
}
