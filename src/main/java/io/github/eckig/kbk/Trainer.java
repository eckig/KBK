package io.github.eckig.kbk;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import io.github.eckig.kbk.impl.KeyByKey;
import io.github.eckig.kbk.layout.KeyboardLayout;
import io.github.eckig.kbk.result.IKeyResult;
import io.github.eckig.kbk.result.KeyResult;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Trainer extends Application
{

    private static final DecimalFormat FORMAT_PERCENT = new DecimalFormat("#%");

    private static final PseudoClass PS_FAILURE = PseudoClass.getPseudoClass("failure");
    private static final PseudoClass PS_ACTIVE = PseudoClass.getPseudoClass("active");

    private static final double FAILURE_THRESHOLD = 0.8;

    private final KeyByKey mTrainer = new KeyByKey();

    @Override
    public void start(Stage pStage)
    {
        final var display = new Label();
        display.getStyleClass().add("display");
        display.textProperty().bind(mTrainer.nextProperty().map(Key::key));

        final var gui = new BorderPane(display);
        final var bottom = getBottomView();

        bottom.setPadding(new Insets(0, 15, 30, 15));
        bottom.setMaxWidth(Region.USE_PREF_SIZE);
        BorderPane.setAlignment(bottom, Pos.CENTER);
        gui.setBottom(bottom);

        final var css = getClass().getResource(Trainer.class.getSimpleName() + ".css");
        if (css != null)
        {
            gui.getStylesheets().add(css.toExternalForm());
        }

        pStage.addEventFilter(KeyEvent.KEY_TYPED, this::keyPressed);
        pStage.setScene(new Scene(gui, 550, 400));
        pStage.setTitle("Key by Key");
        pStage.show();
    }

    private Pane getBottomView()
    {
        final var options = new HBox(16);
        for (final var group : KeyList.DEFAULTS)
        {
            final var cbx = new CheckBox(group.getLabel());
            cbx.selectedProperty().bindBidirectional(group.activeProperty());
            options.getChildren().add(cbx);
        }
        final CheckBox cbxUppercase = new CheckBox("Uppercase");
        cbxUppercase.selectedProperty().bindBidirectional(mTrainer.upperCaseProperty());
        options.getChildren().add(0, cbxUppercase);

        options.getChildren().forEach(n -> ((Region) n).setMaxHeight(Double.MAX_VALUE));

        final var grid = new BorderPane();
        final var cbxLayout = new ChoiceBox<>(FXCollections.observableArrayList(KeyboardLayout.Layout.values()));
        grid.centerProperty().bind(cbxLayout.valueProperty().map(this::createGrid));
        cbxLayout.getSelectionModel().selectFirst();

        return new VBox(16, options, cbxLayout, grid);
    }

    private Node createGrid(final KeyboardLayout.Layout pLayout)
    {
        final var allKeys = new ArrayList<>(KeyList.DEFAULTS.stream().flatMap(KeyList::stream).toList());
        final var layout = KeyboardLayout.get(pLayout);
        final var rows = layout.getRows();
        final var columns = layout.getColumns();
        final var grid = new GridPane();

        for (int row = 0; row < rows; row++)
        {
            for (int column = 0; column < columns; column++)
            {
                final var finalColumn = column;
                final var finalRow = row;
                layout.getKey(row, column).ifPresent(key -> {
                    final var result = addToGrid(key, grid, finalColumn + 1, finalRow);
                    allKeys.removeIf(result::matchesIgnoreCase);
                });
            }
        }

        final Label lbShift = new Label("Shift");
        lbShift.getStyleClass().addAll("key", "shift");
        GridPane.setMargin(lbShift, new Insets(0, 2, 0, 0));
        mTrainer.nextProperty().map(Key::upperCase).addListener((w, o, n) -> lbShift.pseudoClassStateChanged(PS_ACTIVE, n));
        lbShift.disableProperty().bind(mTrainer.nextProperty().map(pKey -> !pKey.upperCase()));
        grid.add(lbShift, 0, rows - 1);

        for (int column = 0; column < allKeys.size(); column++)
        {
            final var remaining = allKeys.get(column);
            addToGrid(remaining, grid, column + 1, rows);
        }
        return grid;
    }

    private KeyResult addToGrid(final Key key, final GridPane grid, final int column, final int row)
    {
        final var result = mTrainer.getResult(key.key());
        final var group = getGroup(key);
        final var keyLabel = new Label(key.key().toUpperCase(Locale.ENGLISH));
        grid.add(keyLabel, column, row);
        keyLabel.getStyleClass().add("key");
        keyLabel.disableProperty().bind(group.activeProperty().not());

        final InvalidationListener listener = obs -> updateStyle(keyLabel, result);
        result.rateProperty().addListener(listener);
        mTrainer.nextProperty().addListener(listener);
        listener.invalidated(null);

        return result;
    }

    private KeyList getGroup(final Key pKey)
    {
        for (final var group : KeyList.DEFAULTS)
        {
            if (group.contains(pKey))
            {
                return group;
            }
        }
        throw new IllegalStateException(pKey.toString());
    }

    private void updateStyle(final Label pNode, final IKeyResult pKey)
    {
        final double hitRate = pKey.rateProperty().get();
        final Key next = mTrainer.nextProperty().get();

        pNode.pseudoClassStateChanged(PS_ACTIVE, pKey.matchesIgnoreCase(next));
        pNode.pseudoClassStateChanged(PS_FAILURE, hitRate < FAILURE_THRESHOLD);
        if (pNode.getTooltip() == null)
        {
            pNode.setTooltip(new Tooltip());
        }
        pNode.getTooltip().setText("Hit Rate: " + FORMAT_PERCENT.format(hitRate));
    }

    private void keyPressed(final KeyEvent pEvent)
    {
        mTrainer.tryNext(pEvent.getCharacter());
    }

    public static void main(String[] pArgs)
    {
        Application.launch(Trainer.class, pArgs);
    }
}
