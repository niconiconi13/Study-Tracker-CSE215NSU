package com.studytracker.screens;

import com.studytracker.controller.DataController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Map;
import java.util.function.Consumer;

public class DashboardScreen extends VBox {

    private final Consumer<String> navigator;

    public DashboardScreen(Consumer<String> navigator) {
        this.navigator = navigator;
        buildUI();
    }

    private void buildUI() {
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(50, 40, 40, 40));
        setSpacing(24);
        getStyleClass().add("screen"); this.setStyle("-fx-background-image: url('/com/studytracker/css/dashboard.gif'); "+ 
        "-fx-background-size: cover;" + "-fx-background-position: center;");


        Button back = new Button("← Back");
        back.getStyleClass().add("back-btn");
        back.setOnAction(e -> navigator.accept("home"));
        HBox backBar = new HBox(back);
        backBar.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("📊  Dashboard");
        title.getStyleClass().add("screen-title");

        // ── Stat cards ────────────────────────────────────────────────────────
        long totalSecs = DataController.getInstance().getTotalStudySeconds();
        int sessions = DataController.getInstance().getTotalSessionCount();
        int streak = DataController.getInstance().getCurrentStreak();
        int totalXP = (int) DataController.getInstance().getTotalXP();

        HBox statRow = new HBox(16,
            makeStatCard("⏱", "Total Study Time", formatTime(totalSecs)),
            makeStatCard("📖", "Sessions", String.valueOf(sessions)),
            makeStatCard("🔥", "Streak", streak + (streak == 1 ? " day" : " days")),
            makeStatCard("⭐", "Total XP", String.valueOf(totalXP))
        );
        statRow.setAlignment(Pos.CENTER);

        // ── XP Info card ──────────────────────────────────────────────────────
        VBox xpCard = new VBox(8);
        xpCard.setPadding(new Insets(16, 20, 16, 20));
        xpCard.getStyleClass().add("content-card");
        xpCard.setMaxWidth(620);

        Label xpTitle = new Label("⭐ XP Calculation");
        xpTitle.getStyleClass().add("section-heading");

        Label xpInfo = new Label(
            "XP Formula: 1 XP per minute studied.\n" +
            "Bonus: +50% XP for sessions ≥ 1 hour (long session bonus).\n" +
            "Your total XP: " + totalXP + " pts"
        );
        xpInfo.getStyleClass().add("field-label");
        xpInfo.setWrapText(true);

        xpCard.getChildren().addAll(xpTitle, xpInfo);

        // ── Bar chart ─────────────────────────────────────────────────────────
        Map<String, Long> subjectTimes = DataController.getInstance().getTimePerSubject();

        VBox chartCard = new VBox(16);
        chartCard.setPadding(new Insets(24));
        chartCard.getStyleClass().add("content-card");
        chartCard.setMaxWidth(620);
        chartCard.setAlignment(Pos.CENTER_LEFT);

        Label chartTitle = new Label("Time per Subject");
        chartTitle.getStyleClass().add("section-heading");

        if (subjectTimes.isEmpty()) {
            Label noData = new Label("No sessions recorded yet. Start studying! 🚀");
            noData.getStyleClass().add("field-label");
            chartCard.getChildren().addAll(chartTitle, noData);
        } else {
            Canvas canvas = buildBarChart(subjectTimes);
            chartCard.getChildren().addAll(chartTitle, canvas);
        }

        getChildren().addAll(backBar, title, statRow, xpCard, chartCard);
    }

    private VBox makeStatCard(String icon, String label, String value) {
        VBox card = new VBox(6);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.getStyleClass().add("stat-card");
        card.setPrefWidth(145);

        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("stat-icon");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");
        valueLabel.setWrapText(true);
        valueLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label nameLabel = new Label(label);
        nameLabel.getStyleClass().add("stat-name");
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        card.getChildren().addAll(iconLabel, valueLabel, nameLabel);
        return card;
    }

    private Canvas buildBarChart(Map<String, Long> data) {
        int width = 560;
        int height = 220;
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.TRANSPARENT);
        gc.fillRect(0, 0, width, height);

        if (data.isEmpty()) return canvas;

        long maxVal = data.values().stream().mapToLong(Long::longValue).max().orElse(1);
        int barCount = data.size();
        int barWidth = Math.min(80, (width - 60) / barCount - 14);
        int chartH = height - 60;
        int startX = 40;

        gc.setStroke(Color.web("#334155"));
        gc.setLineWidth(1);
        gc.strokeLine(35, 10, 35, height - 40);
        gc.strokeLine(35, height - 40, width - 10, height - 40);

        gc.setStroke(Color.web("#1e293b"));
        for (int i = 1; i <= 3; i++) {
            double y = (height - 40) - (chartH * i / 3.0);
            gc.strokeLine(36, y, width - 10, y);
            long labelVal = (maxVal * i) / 3;
            gc.setFill(Color.web("#64748b"));
            gc.setFont(Font.font("System", 10));
            gc.fillText(formatTimeShort(labelVal), 2, y + 4);
        }

        Color[] palette = {
            Color.web("#6366f1"), Color.web("#22d3ee"), Color.web("#4ade80"),
            Color.web("#f472b6"), Color.web("#fb923c"), Color.web("#a78bfa"),
        };

        int i = 0;
        int spacing = (width - 60) / barCount;
        for (Map.Entry<String, Long> entry : data.entrySet()) {
            double ratio = (double) entry.getValue() / maxVal;
            int barH = (int) (ratio * chartH);
            int x = startX + i * spacing;
            int y = height - 40 - barH;

            Color c = palette[i % palette.length];

            LinearGradient grad = new LinearGradient(
                x, y, x, height - 40, false, CycleMethod.NO_CYCLE,
                new Stop(0, c.brighter()), new Stop(1, c.darker())
            );
            gc.setFill(grad);
            gc.fillRoundRect(x, y, barWidth, barH, 6, 6);

            gc.setFill(Color.web("#e2e8f0"));
            gc.setFont(Font.font("System", FontWeight.BOLD, 11));
            gc.fillText(formatTimeShort(entry.getValue()), x + barWidth / 2.0 - 14, y - 6);

            gc.setFill(Color.web("#94a3b8"));
            gc.setFont(Font.font("System", 11));
            String lbl = entry.getKey();
            if (lbl.length() > 8) lbl = lbl.substring(0, 7) + "…";
            gc.fillText(lbl, x, height - 20);

            i++;
        }

        return canvas;
    }

    private String formatTime(long seconds) {
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        if (h > 0) return h + "h " + m + "m";
        if (m > 0) return m + "m";
        return seconds + "s";
    }

    private String formatTimeShort(long seconds) {
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        if (h > 0) return h + "h" + (m > 0 ? m + "m" : "");
        return m + "m";
    }
}
