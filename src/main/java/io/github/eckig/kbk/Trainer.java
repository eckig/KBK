package io.github.eckig.kbk;

import java.text.DecimalFormat;

import javafx.application.Application;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Trainer extends Application
{

    private final KeyByKey mTrainer = new KeyByKey();

    @Override
    public void start(Stage pStage)
    {
        final var settings = new HBox(15);
        for (final var list : mTrainer.getKeys())
        {
            final var cbxNumbers = new CheckBox("Include " + list.getLabel() + "?");
            cbxNumbers.selectedProperty().bindBidirectional(list.activeProperty());
            settings.getChildren().add(cbxNumbers);
        }

        final var display = new Label();
        display.setStyle("-fx-font-family: monospace; -fx-font-size: 400%;");
        display.textProperty().bind(mTrainer.nextProperty().asString());

        final var gui = new BorderPane(display);
        final var report = createReportsView();

        final var bottom = new VBox(15, report, settings);
        bottom.setPadding(new Insets(15));
        gui.setBottom(bottom);

        pStage.addEventFilter(KeyEvent.KEY_TYPED, this::keyPressed);
        pStage.setScene(new Scene(gui, 550, 550));
        pStage.show();
    }

    private TableView<KeyResult> createReportsView()
    {
        final var decFormat = new DecimalFormat("#%");
        final var table = new TableView<KeyResult>();
        VBox.setVgrow(table, Priority.SOMETIMES);

        final var colKey = new TableColumn<KeyResult, Key>("Key");
        colKey.setCellValueFactory(v -> v.getValue().keyProperty());

        final var colRate = new TableColumn<KeyResult, Number>("Hit Rate");
        colRate.setCellValueFactory(v -> v.getValue().rateProperty());
        colRate.setCellFactory(v -> new TableCell<>()
        {
            @Override
            protected void updateItem(final Number number, final boolean b)
            {
                super.updateItem(number, b);

                if (!b && number != null)
                {
                    setText(decFormat.format(number.doubleValue()));
                }
            }
        });

        //noinspection unchecked
        table.getColumns().addAll(colKey, colRate);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSortOrder().add(colRate);
        colRate.setSortType(TableColumn.SortType.ASCENDING);

        final var data = new SortedList<>(mTrainer.getResults());
        data.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(data);
        return table;
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
