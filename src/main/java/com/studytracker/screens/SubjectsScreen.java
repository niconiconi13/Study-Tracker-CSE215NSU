package com.studytracker.screens;

import com.studytracker.controller.DataController;
import com.studytracker.model.InvalidStudyDataException;
import com.studytracker.model.Subject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.function.Consumer;

public class SubjectsScreen extends VBox {

    private final Consumer<String> navigator;
    private ObservableList<Subject> subjectList;
    private ListView<Subject> listView;
    private TextField nameField;
    private Label statusLabel;

    public SubjectsScreen(Consumer<String> navigator) {
        this.navigator = navigator;
        buildUI();
    }

    private void buildUI() {
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(50, 40, 40, 40));
        setSpacing(24);
        getStyleClass().add("screen");
         this.setStyle("-fx-background-image: url('/com/studytracker/css/subjects.gif'); "+ 
        "-fx-background-size: cover;" + "-fx-background-position: center;");


        Button back = new Button("← Back");
        back.getStyleClass().add("back-btn");
        back.setOnAction(e -> navigator.accept("home"));
        HBox backBar = new HBox(back);
        backBar.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("📚  Subjects");
        title.getStyleClass().add("screen-title");

        nameField = new TextField();
        nameField.setPromptText("Enter subject name...");
        nameField.getStyleClass().add("dark-field");
        nameField.setPrefWidth(280);

        Button addBtn = new Button("+ Add");
        addBtn.getStyleClass().addAll("action-btn", "btn-start");
        addBtn.setOnAction(e -> handleAdd());
        nameField.setOnAction(e -> handleAdd());

        HBox inputRow = new HBox(12, nameField, addBtn);
        inputRow.setAlignment(Pos.CENTER);

        statusLabel = new Label("");
        statusLabel.getStyleClass().add("status-label");

        VBox addSection = new VBox(10, inputRow, statusLabel);
        addSection.setAlignment(Pos.CENTER);
        addSection.setPadding(new Insets(24));
        addSection.getStyleClass().add("content-card");
        addSection.setMaxWidth(500);

        subjectList = FXCollections.observableArrayList(DataController.getInstance().getSubjects());
        listView = new ListView<>(subjectList);
        listView.getStyleClass().add("dark-list");
        listView.setPrefHeight(280);
        listView.setMaxWidth(500);
        listView.setPlaceholder(new Label("No subjects yet. Add one above!"));

        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Subject item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    HBox row = new HBox();
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setSpacing(8);

                    Label name = new Label("📌  " + item.getName());
                    name.getStyleClass().add("list-item-label");
                    HBox.setHgrow(name, Priority.ALWAYS);

                    Button del = new Button("✕");
                    del.getStyleClass().add("delete-btn");
                    del.setOnAction(e -> {
                        DataController.getInstance().removeSubject(item);
                        subjectList.remove(item);
                    });

                    row.getChildren().addAll(name, del);
                    setGraphic(row);
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                }
            }
        });

        Label listTitle = new Label("Your Subjects");
        listTitle.getStyleClass().add("section-heading");

        VBox listSection = new VBox(12, listTitle, listView);
        listSection.setAlignment(Pos.CENTER);
        listSection.setMaxWidth(500);

        getChildren().addAll(backBar, title, addSection, listSection);
    }

    private void handleAdd() {
        String name = nameField.getText().trim();
        try {
            // Check duplicate in UI list before calling controller
            boolean exists = subjectList.stream()
                .anyMatch(s -> s.getName().equalsIgnoreCase(name));
            if (exists) {
                throw new InvalidStudyDataException("Subject '" + name + "' already exists.");
            }

            DataController.getInstance().addSubject(name);  // throws InvalidStudyDataException
            Subject s = new Subject(name);
            subjectList.add(s);
            nameField.clear();
            statusLabel.setText("✓  Subject '" + name + "' added!");
            statusLabel.setStyle("-fx-text-fill: #4ade80;");

        } catch (InvalidStudyDataException ex) {
            statusLabel.setText("⚠  " + ex.getMessage());
            statusLabel.setStyle("-fx-text-fill: #f59e0b;");
        }
    }
}
