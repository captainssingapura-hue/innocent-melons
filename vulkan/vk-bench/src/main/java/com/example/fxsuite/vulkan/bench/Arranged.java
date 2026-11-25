package com.example.fxsuite.vulkan.bench;

import com.example.fxsuite.vulkan.device.Desk;
import com.example.fxsuite.vulkan.device.Monitor;
import com.example.fxsuite.vulkan.device.Stands;
import com.example.fxsuite.vulkan.device.Window;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A bench's two windows arranged over the desk's screens, for one run of the bench: the card's window, the
 * picture, and JavaFX's, the console. Each is put where the spread kept says ({@link Spreads}); with nothing kept,
 * the usual way ({@link Spread}): the picture as large as the first screen lets it be and the console on a second
 * screen, where there is one. A run nobody attends, a script or a timed one, is always arranged as the bench itself
 * has always put its windows, so its pictures are the size they were.
 *
 * <p>Two buttons for the console keep the arrangement as the windows now stand, or forget what is kept. The two
 * windows belong to two threads: the console's seat is taken on JavaFX's when the button is pressed, and the
 * picture's on the card's the next time it hears, which then writes both.
 */
public final class Arranged {

    /** How wide the gap between the console's two buttons is. */
    private static final double GAP = 8;

    private final Spreads spreads = new Spreads();
    private final Optional<Spread> kept;
    private final Spread before;
    /** The console's seat, taken when it was asked to be kept, until the card's thread has kept it. */
    private final AtomicReference<Optional<Seat>> asked = new AtomicReference<>(Optional.empty());

    /**
     * An arrangement for a bench that has always put its console {@code x} across the first screen and {@code y}
     * down it and its picture {@code beside} further across: {@code attended} or not.
     */
    public Arranged(final boolean attended, final int x, final int y, final int beside) {
        this.before = new Spread(new Seat(Seat.Which.FIRST, x + beside, y), new Seat(Seat.Which.FIRST, x, y));
        this.kept = attended ? spreads.kept() : Optional.of(before);
    }

    /** The spread this run is arranged by, on a desk with {@code two} screens or with one. */
    private Spread spread(final boolean two) {
        return kept.orElseGet(() -> new Spread(two, before));
    }

    /** The picture shown where it goes. To be called on the card's thread. */
    public void shown(final Window window, final Desk desk) {
        final List<Monitor> monitors = desk.monitors();
        final Seat seat = spread(monitors.size() > 1).picture();
        final Monitor on = monitors.get(seat.on() == Seat.Which.OTHER && monitors.size() > 1 ? 1 : 0);
        if (seat.width() > 0 && seat.height() > 0) {
            window.sized(seat.width(), seat.height());
        }
        if (seat.maximized()) {
            window.maximized(on);
        } else {
            window.shown(on, seat.x(), seat.y());
        }
    }

    /** What the console has asked since this was last called, done. To be called on the card's thread each frame. */
    public void heard(final Window window, final Desk desk) {
        asked.getAndSet(Optional.empty()).ifPresent(console -> {
            final Stands stands = window.stands();
            final List<Monitor> monitors = desk.monitors();
            // the screen its corner is on, a little in from the corner; the first, where it is on none
            final int on = Math.max(0, monitors.indexOf(monitors.stream()
                    .filter(monitor -> monitor.holds(stands.left() + 40, stands.top() + 40)).findFirst()
                    .orElse(monitors.getFirst())));
            spreads.keep(new Spread(new Seat(on == 0 ? Seat.Which.FIRST : Seat.Which.OTHER, stands.maximized(),
                    (int) Math.round((stands.left() - monitors.get(on).left()) / stands.larger()),
                    (int) Math.round((stands.top() - monitors.get(on).top()) / stands.larger()),
                    stands.width(), stands.height()), console));
        });
    }

    /** The console put where it goes, before it is shown. To be called on JavaFX's thread. */
    public void placed(final Stage stage) {
        final List<Screen> screens = screens();
        final Seat seat = spread(screens.size() > 1).console();
        final Rectangle2D on = screens.get(seat.on() == Seat.Which.OTHER && screens.size() > 1 ? 1 : 0)
                .getVisualBounds();
        if (seat.width() > 0 && seat.height() > 0) {
            stage.setWidth(seat.width());
            stage.setHeight(seat.height());
        }
        // JavaFX lays its screens out in one measure of its own, each at its own size, so this holds across them
        stage.setX(on.getMinX() + seat.x());
        stage.setY(on.getMinY() + seat.y());
        if (seat.maximized()) {
            // made large only once it is shown, so that it is the screen it stands on that it fills
            stage.addEventHandler(WindowEvent.WINDOW_SHOWN, shown -> Platform.runLater(() -> stage.setMaximized(true)));
        }
    }

    /** The arrangement asked to be kept, the console standing at {@code console}: kept when the card next hears. */
    void asked(final Seat console) {
        asked.set(Optional.of(console));
    }

    /** JavaFX's screens, the first screen first. */
    private List<Screen> screens() {
        final List<Screen> screens = new ArrayList<>(List.of(Screen.getPrimary()));
        Screen.getScreens().stream().filter(screen -> !screen.equals(Screen.getPrimary())).forEach(screens::add);
        return screens;
    }

    /**
     * Two buttons for a console: one keeps the arrangement as both windows now stand, for every bench from now on;
     * the other forgets what is kept. The console is whichever window they are put in. To be made on JavaFX's thread.
     */
    public Node bar() {
        final Label said = new Label(spreads.kept().isPresent() ? "Windows as kept" : "Windows the usual way");
        final Button keep = new Button("Keep this layout");
        keep.setOnAction(pressed -> {
            final Stage stage = (Stage) keep.getScene().getWindow();
            final List<Screen> screens = screens();
            final Screen holds = screens.stream().filter(screen -> screen.getVisualBounds().contains(
                    stage.getX() + stage.getWidth() / 2, stage.getY() + stage.getHeight() / 2)).findFirst()
                    .orElse(screens.getFirst());
            final Rectangle2D on = holds.getVisualBounds();
            asked(new Seat(screens.indexOf(holds) == 0 ? Seat.Which.FIRST : Seat.Which.OTHER,
                    stage.isMaximized(), (int) Math.round(stage.getX() - on.getMinX()),
                    (int) Math.round(stage.getY() - on.getMinY()), (int) Math.round(stage.getWidth()),
                    (int) Math.round(stage.getHeight())));
            said.setText("Kept: every bench opens so");
        });
        final Button forget = new Button("Forget it");
        forget.setOnAction(pressed -> {
            spreads.forget();
            said.setText("Forgotten: the usual way next time");
        });
        return new HBox(GAP, keep, forget, said);
    }
}
