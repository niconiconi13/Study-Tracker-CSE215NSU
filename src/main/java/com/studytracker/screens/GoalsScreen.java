package com.studytracker.screens;

import com.studytracker.controller.DataController;
import com.studytracker.model.InvalidStudyDataException;
import com.studytracker.model.StudyGoal;
import com.studytracker.model.Subject;
import com.studytracker.model.TrackerItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.function.Consumer;

public class GoalsScreen extends VBox {

    private final Consumer<String> navigator;
    private ObservableList<StudyGoal> goalList;
    private ComboBox<Subject> subjectCombo;
    private TextField hoursField;
    private Label statusLabel;

    public GoalsScreen(Consumer<String> navigator) {
        this.navigator = navigator;
        buildUI();
    }

    private void buildUI() {
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(50, 40, 40, 40));
        setSpacing(28);
        getStyleClass().add("screen");
        this.setStyle("-fx-background-image: url('/com/studytracker/css/goal.gif'); " +
              "-fx-background-size: cover; " +
              "-fx-background-position: center;");


        Button back = new Button("← Back");
        back.getStyleClass().add("back-btn");
        back.setOnAction(e -> navigator.accept("home"));
        HBox backBar = new HBox(back);
        backBar.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("🎯  Goals");
        title.getStyleClass().add("screen-title");

        // ── Add goal form ──────────────────────────────────────────────────────
        subjectCombo = new ComboBox<>();
        subjectCombo.getItems().addAll(DataController.getInstance().getSubjects());
        subjectCombo.setPromptText("Choose subject...");
        subjectCombo.getStyleClass().add("combo-box-dark");
        subjectCombo.setPrefWidth(200);

        hoursField = new TextField();
        hoursField.setPromptText("Target hours e.g. 10");
        hoursField.getStyleClass().add("dark-field");
        hoursField.setPrefWidth(150);

        Button addBtn = new Button("+ Add Goal");
        addBtn.getStyleClass().addAll("action-btn", "btn-start");
        addBtn.setOnAction(e -> handleAdd());

        HBox inputRow = new HBox(12, subjectCombo, hoursField, addBtn);
        inputRow.setAlignment(Pos.CENTER);

        statusLabel = new Label("");
        statusLabel.getStyleClass().add("status-label");

        VBox formCard = new VBox(12, inputRow, statusLabel);
        formCard.setAlignment(Pos.CENTER);
        formCard.setPadding(new Insets(24));
        formCard.getStyleClass().add("content-card");
        formCard.setMaxWidth(600);

        // ── Goals list ─────────────────────────────────────────────────────────
        Label listTitle = new Label("Your Goals");
        listTitle.getStyleClass().add("section-heading");

        goalList = FXCollections.observableArrayList();
        for (StudyGoal goal : DataController.getInstance().getGoals()) {
            DataController.getInstance().updateGoalProgress(goal);
            goalList.add(goal);
        }

        ListView<StudyGoal> listView = new ListView<>(goalList);
        listView.getStyleClass().add("dark-list");
        listView.setPrefHeight(300);
        listView.setMaxWidth(600);
        listView.setPlaceholder(new Label("No goals yet. Add one above!"));

        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(StudyGoal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    // ── Polymorphism via TrackerItem superclass reference ──────
                    TrackerItem goalItem = item;
                    String summary = goalItem.getSummary();       // StudyGoal's override
                    double score = goalItem.calculateScore();     // StudyGoal's override

                    Label summaryLabel = new Label(summary);
                    summaryLabel.getStyleClass().add("field-label");
                    summaryLabel.setWrapText(true);
                    HBox.setHgrow(summaryLabel, Priority.ALWAYS);

                    // Also call Subject's calculateScore() polymorphically
                    Subject matchedSubject = DataController.getInstance().getSubjects()
                        .stream()
                        .filter(s -> s.getName().equalsIgnoreCase(item.getSubjectName()))
                        .findFirst().orElse(null);

                    String dailyHint = "";
                    if (matchedSubject != null) {
                        TrackerItem subItem = matchedSubject;
                        double hoursPerDay = subItem.calculateScore(); // Subject's override
                        dailyHint = String.format("%.1fh/day needed to meet weekly goal", hoursPerDay);
                    }

                    Label hintLabel = new Label(dailyHint);
                    hintLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");

                    Button del = new Button("✕");
                    del.getStyleClass().add("delete-btn");
                    del.setOnAction(e -> {
                        DataController.getInstance().removeGoal(item);
                        goalList.remove(item);
                    });

                    VBox textBox = new VBox(4, summaryLabel, hintLabel);
                    HBox.setHgrow(textBox, Priority.ALWAYS);

                    HBox row = new HBox(12, textBox, del);
                    row.setAlignment(Pos.CENTER_LEFT);

                    setGraphic(row);
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                    
                }
            }
        });

        VBox listSection = new VBox(12, listTitle, listView);
        listSection.setMaxWidth(600);

        getChildren().addAll(backBar, title, formCard, listSection);
    }

    private void handleAdd() {
        try {
            if (subjectCombo.getValue() == null) {
                throw new InvalidStudyDataException("Please select a subject.");
            }
            String hoursText = hoursField.getText().trim();
            if (hoursText.isEmpty()) {
                throw new InvalidStudyDataException("Please enter target hours.");
            }
            double targetHours;
            try {
                targetHours = Double.parseDouble(hoursText);
            } catch (NumberFormatException e) {
                throw new InvalidStudyDataException("Target hours must be a number.");
            }

            StudyGoal goal = new StudyGoal(subjectCombo.getValue().getName(), targetHours);
            DataController.getInstance().updateGoalProgress(goal);
            DataController.getInstance().addGoal(goal);
            goalList.add(goal);

            subjectCombo.setValue(null);
            hoursField.clear();
            statusLabel.setText("✓  Goal added!");
            statusLabel.setStyle("-fx-text-fill: #4ade80;");

        } catch (InvalidStudyDataException ex) {
            statusLabel.setText("⚠  " + ex.getMessage());
            statusLabel.setStyle("-fx-text-fill: #f59e0b;");
        }
    }
}