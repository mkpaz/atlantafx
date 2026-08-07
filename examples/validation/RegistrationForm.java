///usr/bin/env jbang "$0" "$@" ; exit $?
//JBANG >=0.141.0
//JAVA 25+
//DEPS org.openjfx:javafx-controls:25:${os.detected.jfxname}
//DEPS org.openjfx:javafx-graphics:25:${os.detected.jfxname}
//DEPS org.jspecify:jspecify:1.0.0
//SOURCES ../../validation/src/main/java/**/*
//JAVA_OPTIONS --module-path %deps --add-modules javafx.controls,javafx.graphics

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import atlantafx.validation.Rule;
import atlantafx.validation.RuleSet;
import atlantafx.validation.actions.TextAction;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static atlantafx.validation.Check.*;

public class RegistrationForm extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Account Registration");

        var account = new Account();
        var validator = new AccountValidator(account);
        var root = new RegistrationPane(account, validator);

        var scene = new Scene(root, 600, 800);
        stage.setScene(scene);
        stage.show();
    }

    static void main(String[] args) {
        launch(args);
    }

    static class Account {

        static final LocalDate NOW = LocalDate.now();

        final StringProperty firstName = new SimpleStringProperty("");
        final StringProperty lastName = new SimpleStringProperty("");
        final ObjectProperty<LocalDate> birthDate = new SimpleObjectProperty<>();
        final StringProperty ageCategory = new SimpleStringProperty("Minor");
        final StringProperty postalCode = new SimpleStringProperty("");
        final BooleanProperty isInternationalCode = new SimpleBooleanProperty(false);
        final BooleanProperty hasPromoCode = new SimpleBooleanProperty(false);
        final StringProperty promoCode = new SimpleStringProperty("");
        final StringProperty password = new SimpleStringProperty("");
        final StringProperty confirmPassword = new SimpleStringProperty("");
        final BooleanProperty acceptTerms = new SimpleBooleanProperty(false);

        Account() {
            birthDate.subscribe(val ->
                ageCategory.set(val != null && Period.between(val, NOW).getYears() >= 18 ? "adult" : "minor")
            );
        }
    }

    static class AccountValidator {

        final StringProperty firstNameError = new SimpleStringProperty("");
        final StringProperty lastNameError = new SimpleStringProperty("");
        final StringProperty birthDateError = new SimpleStringProperty("");
        final StringProperty postalCodeError = new SimpleStringProperty("");
        final StringProperty promoCodeError = new SimpleStringProperty("");
        final StringProperty passwordError = new SimpleStringProperty("");
        final StringProperty confirmPasswordError = new SimpleStringProperty("");
        final StringProperty termsError = new SimpleStringProperty("");
        final RuleSet registrationForm;

        AccountValidator(Account account) {
            var firstNameRule = Rule.on(account.firstName, "First Name")
                .must(Strings.isNotBlank())
                .failMessage("First name is required.")
                .onValidated(new TextAction(firstNameError));

            var lastNameRule = Rule.on(account.lastName, "Last Name")
                .must(Strings.isNotBlank())
                .failMessage("Last name is required.")
                .onValidated(new TextAction(lastNameError));

            // child rule
            var ageCategoryRule = Rule.on(account.ageCategory, "Age Restriction")
                .must(Strings.isEqualAnyCase("adult"))
                .failMessage("Applicant must be at least 18 years old.")
                .onFailure(f -> birthDateError.set(join("\n", birthDateError.get(), f.message())))
                .onSuccess(_ -> birthDateError.set(null));

            var birthDateRule = Rule.on(account.birthDate, "Birth Date")
                .must(Temporals.isBefore(LocalDate.now()))
                .failMessage("Birth date must be in the past.")
                .childRules(ageCategoryRule)
                .onValidated(new TextAction(birthDateError));

            // individual precondition
            var postalCodeRule = Rule.on(account.postalCode, "Postal Code")
                .must(Strings.isNotBlank())
                .failMessage("Postal code is required.")
                .must(Strings.matches("\\d{5}"))
                .unless(_ -> account.isInternationalCode.get())
                .failMessage("Domestic postal code must consist of exactly 5 digits.")
                .onValidated(new TextAction(postalCodeError));

            // rule precondition
            var promoCodeRule = Rule.on(account.promoCode, "Promo Code")
                .must(Strings.startsWith("PROMO"))
                .failMessage("Promo code must start with 'PROMO'.")
                .given(_ -> account.hasPromoCode.get())
                .onValidated(new TextAction(promoCodeError));

            // dependency
            var passwordRule = Rule.on(account.password, "Password")
                .must(Strings.lengthGreaterOrEqual(6))
                .failMessage("Password must be at least 6 characters long.")
                .onValidated(new TextAction(passwordError));

            var confirmPasswordRule = Rule.on(account.confirmPassword, "Confirm Password")
                .must(confirm -> confirm != null && confirm.equals(account.password.get()))
                .failMessage("Passwords do not match.")
                .given(account.password, Strings.isNotBlank())
                .onValidated(new TextAction(confirmPasswordError));

            var termsRule = Rule.on(account.acceptTerms, "Terms")
                .must(Booleans.isTrue())
                .failMessage("You must accept the terms to proceed.")
                .onValidated(new TextAction(termsError));

            registrationForm = RuleSet.of(
                "RegistrationForm",
                firstNameRule,
                lastNameRule,
                birthDateRule,
                postalCodeRule,
                promoCodeRule,
                passwordRule,
                confirmPasswordRule,
                termsRule
            ).immediate();

            account.isInternationalCode.subscribe(_ -> postalCodeRule.revalidate());
        }

        public static String join(CharSequence delimiter, CharSequence... elements) {
            return Arrays.stream(elements)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(delimiter));
        }
    }

    static class RegistrationPane extends GridPane {

        private int row = 0;

        RegistrationPane(Account account, AccountValidator validator) {
            super(15, 10);
            setPadding(new Insets(20));

            var col0Constraints = new ColumnConstraints();
            col0Constraints.setMinWidth(140);

            var col1Constraints = new ColumnConstraints();
            col1Constraints.setHgrow(Priority.ALWAYS);

            getColumnConstraints().addAll(col0Constraints, col1Constraints);

            // header
            fullWidth(fx(new Label("Registration Form"), l -> l.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;")));
            fullWidth(new Separator());

            // first Name
            col0("First Name: *");
            col1(fx(new TextField(), tf -> tf.textProperty().bindBidirectional(account.firstName)));
            col1(errorLabel(validator.firstNameError));

            // last Name
            col0("Last Name: *");
            col1(fx(new TextField(), tf -> tf.textProperty().bindBidirectional(account.lastName)));
            col1(errorLabel(validator.lastNameError));

            // birthdate
            col0("Birth Date: *");
            col1(new VBox(4,
                fx(new DatePicker(), dp -> dp.valueProperty().bindBidirectional(account.birthDate)),
                fx(new Label(), l -> {
                    l.setStyle("-fx-text-fill: #555555;");
                    l.textProperty().bind(Bindings.concat("Age Category: ", account.ageCategory));
                })
            ));
            col1(errorLabel(validator.birthDateError));

            // postal code
            col0("Postal Code: *");
            col1(new VBox(5,
                fx(new TextField(), tf -> tf.textProperty().bindBidirectional(account.postalCode)),
                fx(new CheckBox("International Address (non-US ZIP)"), cb -> cb.selectedProperty().bindBidirectional(account.isInternationalCode))
            ));
            col1(errorLabel(validator.postalCodeError));

            // promo code
            col0("Promo Code:");
            col1(new VBox(5,
                fx(new TextField(), tf -> {
                    tf.textProperty().bindBidirectional(account.promoCode);
                    tf.disableProperty().bind(account.hasPromoCode.not());
                }),
                fx(new CheckBox("I have a promo code"), cb -> cb.selectedProperty().bindBidirectional(account.hasPromoCode))
            ));
            col1(errorLabel(validator.promoCodeError));
            account.hasPromoCode.subscribe(val -> {
                if (!val) {
                    account.promoCode.set(null);
                }
            });

            // password
            col0("Password:*");
            col1(fx(new PasswordField(), pf -> pf.textProperty().bindBidirectional(account.password)));
            col1(errorLabel(validator.passwordError));

            // confirm password
            col0("Confirm Password: *");
            col1(fx(new PasswordField(), pf -> pf.textProperty().bindBidirectional(account.confirmPassword)));
            col1(errorLabel(validator.confirmPasswordError));

            // terms
            col1(fx(new CheckBox("I accept the Terms and Conditions"), cb -> cb.selectedProperty().bindBidirectional(account.acceptTerms)));
            col1(errorLabel(validator.termsError));

            // submit
            var formStatusLabel = fx(new Label(), l -> l.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;"));

            fullWidth(new Separator());
            col1(fx(new Button("Register Account"), btn -> {
                btn.disableProperty().bind(validator.registrationForm.observeInvalid());
                btn.setOnAction(_ -> {
                    if (validator.registrationForm.revalidate().valid()) {
                        formStatusLabel.setTextFill(Color.DARKGREEN);
                        formStatusLabel.setText("Registration successful.");
                    } else {
                        formStatusLabel.setTextFill(Color.RED);
                        formStatusLabel.setText("Please correct errors in the form before submitting.");
                    }
                });
            }));
            col1(formStatusLabel);
        }

        void col0(String text) {
            var label = new Label(text);
            label.setPadding(new Insets(3, 0, 0, 0));
            label.setAlignment(Pos.TOP_LEFT);
            label.setMaxHeight(Double.MAX_VALUE);
            GridPane.setValignment(label, VPos.TOP);
            add(label, 0, row);
        }

        void col1(Node node) {
            add(node, 1, row++);
        }

        void fullWidth(Node node) {
            add(node, 0, row++, 2, 1);
        }

        static <T> T fx(T control, Consumer<T> functor) {
            functor.accept(control);
            return control;
        }

        static Label errorLabel(ReadOnlyStringProperty property) {
            var label = new Label();
            label.setTextFill(Color.RED);
            label.textProperty().bind(property);
            return label;
        }
    }
}