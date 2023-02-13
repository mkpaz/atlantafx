/* SPDX-License-Identifier: MIT */

package atlantafx.sampler;

import static java.lang.Double.MAX_VALUE;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Stage stage;

    public DefaultExceptionHandler(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();

        var dialog = createExceptionDialog(e);
        if (dialog != null) {
            dialog.showAndWait();
        }
    }

    private Alert createExceptionDialog(Throwable throwable) {
        Objects.requireNonNull(throwable);

        var alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(throwable.getMessage());

        try (var sw = new StringWriter(); var printWriter = new PrintWriter(sw)) {
            throwable.printStackTrace(printWriter);

            var label = new Label("Full stacktrace:");

            var textArea = new TextArea(sw.toString());
            textArea.setEditable(false);
            textArea.setWrapText(false);
            textArea.setMaxWidth(MAX_VALUE);
            textArea.setMaxHeight(MAX_VALUE);

            var content = new VBox(5, label, textArea);
            content.setMaxWidth(MAX_VALUE);

            alert.getDialogPane().setExpandableContent(content);
            alert.initOwner(stage);

            return alert;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
