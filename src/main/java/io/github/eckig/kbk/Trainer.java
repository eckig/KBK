package io.github.eckig.kbk;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Trainer extends Application
{

    private final KeyByKey mTrainer = new KeyByKey();

    @Override
    public void start(Stage pStage) throws Exception
    {
        final var cbxNumbers = new CheckBox("Include Numbers?");
        cbxNumbers.selectedProperty().bindBidirectional(mTrainer.includeNumbersProperty());

        final var cbxUpper = new CheckBox("Include Uppercase?");
        cbxNumbers.selectedProperty().bindBidirectional(mTrainer.includeUppercaseProperty());

        final var display = new Label();
        display.setStyle("-fx-font-family: monospace; -fx-font-size: 300%;");
        display.textProperty().bind(mTrainer.nextProperty());

        final var gui = new BorderPane(display);

        final var top = new VBox(15, cbxNumbers, cbxUpper);
        top.setPadding(new Insets(15));
        gui.setTop(top);

        pStage.addEventFilter(KeyEvent.KEY_TYPED, this::keyPressed);
        pStage.setScene(new Scene(gui, 300, 300));
        pStage.show();
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
