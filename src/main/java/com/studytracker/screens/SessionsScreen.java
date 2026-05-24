package com.studytracker.screens;

import com.studytracker.controller.DataController;
import com.studytracker.model.StudySession;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.function.Consumer;

public class SessionsScreen extends VBox {

    private final Consumer<String> navigator;

    public SessionsScreen(Consumer<String> navigator) {
        this.navigator = navigator;
        buildUI();
    }

    private void buildUI() {
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(50, 40, 40, 40));
        setSpacing(24);
        getStyleClass().add("screen");
         getStyleClass().add("screen"); this.setStyle("-fx-background-image: url('/com/studytracker/css/sessions.gif'); "+ 
        "-fx-background-size: cover;" + "-fx-background-position: center;");


        // ── Back button ───────────────────────────────────────────────────────
        Button back = new Button("← Back");
        back.getStyleClass().add("back-btn");
        back.setOnAction(e -> navigator.accept("home"));
        HBox backBar = new HBox(back);
        backBar.setAlignment(Pos.CENTER_LEFT);

        // ── Title ─────────────────────────────────────────────────────────────
        Label title = new Label("📖  Sessions");
        title.getStyleClass().add("screen-title");

        // ── Summary ───────────────────────────────────────────────────────────
        int count = DataController.getInstance().getTotalSessionCount();
        String totalTime = formatTime(DataController.getInstance().getTotalStudySeconds());
        Label summary = new Label("Total: " + count + " sessions  •  " + totalTime + " studied");
        summary.getStyleClass().add("summary-text");

        // ── Table ─────────────────────────────────────────────────────────────
        TableView<StudySession> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("dark-table");
        table.setPlaceholder(new Label("No sessions recorded yet."));

        TableColumn<StudySession, String> subjectCol = new TableColumn<>("Subject");
        subjectCol.setCellValueFactory(new PropertyValueFactory<>("subject"));
        subjectCol.setMinWidth(160);

        TableColumn<StudySession, String> durationCol = new TableColumn<>("Duration");
        durationCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getFormattedDuration()
            )
        );
        durationCol.setMinWidth(120);

        TableColumn<StudySession, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getDateOnly()
            )
        );
        dateCol.setMinWidth(120);

        TableColumn<StudySession, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(data -> {
            String ts = data.getValue().getTimestamp();
            String t = (ts != null && ts.length() >= 19) ? ts.substring(11, 19) : "";
            return new javafx.beans.property.SimpleStringProperty(t);
        });
        timeCol.setMinWidth(100);

        table.getColumns().addAll(subjectCol, durationCol, dateCol, timeCol);

        // Load in reverse chronological order
        var sessions = FXCollections.observableArrayList(DataController.getInstance().getSessions());
        FXCollections.reverse(sessions);
        table.setItems(sessions);
        table.setPrefHeight(360);

        VBox tableCard = new VBox(12, summary, table);
        tableCard.setPadding(new Insets(24));
        tableCard.getStyleClass().add("content-card");
        tableCard.setMaxWidth(660);

        getChildren().addAll(backBar, title, tableCard);
    }

    private String formatTime(long seconds) {
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        if (h > 0) return String.format("%dh %02dm %02ds", h, m, s);
        if (m > 0) return String.format("%dm %02ds", m, s);
        return String.format("%ds", s);
    }
}
