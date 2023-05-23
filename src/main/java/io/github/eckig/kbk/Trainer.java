package io.github.eckig.kbk;

import java.util.ArrayList;
import java.util.List;

import io.github.eckig.kbk.layout.KeyboardLayout;
import io.github.eckig.kbk.result.IKeyResult;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
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

    private static final double FAILURE_THRESHOLD = 0.8;

    private final KeyByKey mTrainer = new KeyByKey();

    @Override
    public void start(Stage pStage)
    {
        final var display = new Label();
        display.setStyle("-fx-font-family: monospace; -fx-font-size: 400%;");
        display.textProperty().bind(mTrainer.nextProperty().map(Key::key));

        final var gui = new BorderPane(display);
        final var bottom = getBottomView();

        bottom.setPadding(new Insets(0, 15, 30, 15));
        bottom.setMaxWidth(Region.USE_PREF_SIZE);
        BorderPane.setAlignment(bottom, Pos.CENTER);
        gui.setBottom(bottom);

        pStage.addEventFilter(KeyEvent.KEY_TYPED, this::keyPressed);
        pStage.setScene(new Scene(gui, 400, 400));
        pStage.setTitle("Key by Key");
        pStage.show();
    }

    private Pane getBottomView()
    {
        final var options = new HBox(16);
        for (final var group : KeyList.DEFAULTS)
        {
            final var cbx = new CheckBox(group.getLabel());
            cbx.setMaxHeight(Double.MAX_VALUE);
            cbx.selectedProperty().bindBidirectional(group.activeProperty());
            options.getChildren().add(cbx);
        }

        final var grid = new BorderPane();
        final var cbxLayout = new ChoiceBox<>(FXCollections.observableArrayList(KeyboardLayout.Layout.values()));
        options.getChildren().add(cbxLayout);
        grid.centerProperty().bind(cbxLayout.valueProperty().map(this::createGrid));
        cbxLayout.getSelectionModel().selectFirst();

        return new VBox(16, options, grid);
    }

    private Node createGrid(final KeyboardLayout.Layout pLayout)
    {
        final var allKeys = new ArrayList<>(KeyList.DEFAULTS.stream().flatMap(l -> l.getKeys().stream()).toList());
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
                    final var group = getGroup(key);
                    final IKeyResult _result = mTrainer.getResult(key);
                    final IKeyResult result;
                    if (group == KeyList.CHARS_LOWER)
                    {
                        result = _result.join(mTrainer.getResult(key.toLowerCase()));
                    }
                    else
                    {
                        result = _result;
                    }
                    allKeys.removeIf(result::isValidFor);
                    addToGrid(key, group, result, grid, finalColumn, finalRow);
                });
            }
        }
        for (int column = 0; column < allKeys.size(); column++)
        {
            final var remaining = allKeys.get(column);
            final var group = getGroup(remaining);
            final IKeyResult result = mTrainer.getResult(remaining);
            addToGrid(remaining, group, result, grid, column, rows);
        }
        return grid;
    }

    private void addToGrid(final Key key, final KeyList group, final IKeyResult result, final GridPane grid, final int finalColumn,
            final int finalRow)
    {
        final var keyLabel = new Label(key.key());
        grid.add(keyLabel, finalColumn, finalRow);
        keyLabel.styleProperty()
                .bind(Bindings.createStringBinding(() -> getStyleFor(result), result.rateProperty(), mTrainer.nextProperty()));
        keyLabel.opacityProperty().bind(Bindings.when(group.activeProperty()).then(1.0).otherwise(0.5));
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

    private String getStyleFor(final IKeyResult pKey)
    {
        final double hitRate = pKey.rateProperty().get();
        final Key next = mTrainer.nextProperty().get();

        final List<String> styles = new ArrayList<>();
        styles.add("-fx-font-weight: bold");
        styles.add("-fx-font-family: monospace");
        styles.add("-fx-padding: 5 7 5 7");
        styles.add("-fx-background-radius: 5");
        if (pKey.isValidFor(next))
        {
            final var text = "white";
            final var background = hitRate < FAILURE_THRESHOLD ? "red" : "green";
            styles.add("-fx-text-background-color: " + text);
            styles.add("-fx-background-color: " + background);
        }
        else
        {
            final var text = hitRate < FAILURE_THRESHOLD ? "red" : "green";
            styles.add("-fx-text-background-color: " + text);
            styles.add("-fx-background-color: transparent");
        }

        styles.add("-fx-font-size: 150%");

        return String.join(";", styles);
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
