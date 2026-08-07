///usr/bin/env jbang "$0" "$@" ; exit $?
//JBANG >=0.141.0
//JAVA 25+
//DEPS org.openjfx:javafx-controls:25:${os.detected.jfxname}
//DEPS org.openjfx:javafx-graphics:25:${os.detected.jfxname}
//DEPS org.jspecify:jspecify:1.0.0
//SOURCES ../../validation/src/main/java/**/*
//JAVA_OPTIONS --module-path %deps --add-modules javafx.controls,javafx.graphics

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import atlantafx.validation.Check.Strings;
import atlantafx.validation.Rule;
import atlantafx.validation.RuleSet;
import atlantafx.validation.actions.StyleClassAction;
import atlantafx.validation.actions.TextAction;
import atlantafx.validation.actions.TooltipAction;

import java.util.Base64;

public class SimpleLoginForm extends Application {

    static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        var headerLabel = new Label("Sign In");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        var usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.getStyleClass().add("Username");

        var passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.getStyleClass().add("Password");

        var usernameErrorLabel = errorLabel();
        var passwordErrorLabel = errorLabel();

        var statusLabel = new Label();
        statusLabel.setStyle("-fx-font-weight: bold;");

        var loginBtn = new Button("Log In");
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        var tooltipAction = new TooltipAction(usernameField, passwordField);

        var usernameRule = Rule.on(usernameField.textProperty(), "Username")
            .must(Strings.isNotBlank())
            .failMessage("Username is required.")
            .must(Strings.lengthGreaterOrEqual(3))
            .failMessage("Username must be at least 3 characters.")
            .onValidated(
                new TextAction(usernameErrorLabel),
                tooltipAction,
                new StyleClassAction("has-tooltip", usernameField)
            );

        var passwordRule = Rule.on(passwordField.textProperty(), "Password")
            .must(Strings.isNotBlank())
            .failMessage("Password is required.")
            .must(Strings.lengthGreaterOrEqual(6))
            .failMessage("Password must be at least 6 characters.")
            .onValidated(
                new TextAction(passwordErrorLabel),
                tooltipAction,
                new StyleClassAction("has-tooltip", passwordField)
            );

        var loginFormSet = RuleSet.of("LoginForm", usernameRule, passwordRule).immediate();

        loginBtn.disableProperty().bind(loginFormSet.observeInvalid());
        loginBtn.setOnAction(_ -> {
            if (loginFormSet.result().valid()) {
                statusLabel.setTextFill(Color.DARKGREEN);
                statusLabel.setText("Login successful!");
            }
        });

        var root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER_LEFT);

        root.getChildren().addAll(
            headerLabel,
            new Label("Username:"), usernameField, usernameErrorLabel,
            new Label("Password:"), passwordField, passwordErrorLabel,
            new Separator(),
            loginBtn,
            statusLabel
        );

        var scene = new Scene(root, 400, 400);
        scene.getStylesheets().addAll(
            "data:base64," + new String(Base64.getEncoder().encode(CSS.getBytes()))
        );
        stage.setScene(scene);
        stage.setTitle("Simple Login Form");
        stage.show();
    }

    Label errorLabel() {
        var label = new Label();
        label.setTextFill(Color.RED);
        return label;
    }

    private static final String CSS = """
        .text-field.has-tooltip,
        .password-field.has-tooltip {
            -fx-border-color: #d32f2f;
            -fx-focus-color: #d32f2f;
        }
        """;
}