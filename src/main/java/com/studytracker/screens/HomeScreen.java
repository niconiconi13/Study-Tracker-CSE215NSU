package com.studytracker.screens;

import com.studytracker.controller.DataController;
import com.studytracker.ui.QuoteCard;
import com.studytracker.ui.StudyTipWidget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.function.Consumer;

public class HomeScreen extends VBox {

    private final Consumer<String> navigator;

    public HomeScreen(Consumer<String> navigator) {
        this.navigator = navigator;
        buildUI();
    }

    private void buildUI() {
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(60, 40, 40, 40));
        setSpacing(8);
        getStyleClass().add("home-screen");
        int hour = java.time.LocalTime.now().getHour();

        String bgImage;
        if (hour >= 5 && hour < 12) {
        bgImage = "/com/studytracker/css/bg_morning.png";  // morning
        } else if (hour >= 12 && hour < 17) {
        bgImage = "/com/studytracker/css/bg_afternoon.png"; // afternoon
        } else if (hour >= 17 && hour < 21) {
        bgImage = "/com/studytracker/css/bg_evening.png";   // evening
        } else {
        bgImage = "/com/studytracker/css/bg_night.png";     // night
        }

        this.setStyle("-fx-background-image: url('" + bgImage + "'); " +
                      "-fx-background-size: cover; " +
                      "-fx-background-position: center;");

        // ── Header ────────────────────────────────────────────────────────────
        Label title = new Label("Study Time Tracker");
        title.getStyleClass().add("home-title");

        Label subtitle = new Label("Stay focused. Build your streak.");
        subtitle.getStyleClass().add("home-subtitle");

        // ── Streak text ───────────────────────────────────────────────────────
        int streak = DataController.getInstance().getCurrentStreak();
        String streakText = streak == 0
            ? "No streak yet — study today to start! 🔥"
            : (streak == 1
                ? "🔥 1 day streak — keep it going!"
                : "🔥 " + streak + " day streak — you're on fire!");

        Label streakLabel = new Label(streakText);
        streakLabel.getStyleClass().add("streak-label");

        VBox.setMargin(streakLabel, new Insets(12, 0, 36, 0));

        // ── Cards Grid ────────────────────────────────────────────────────────
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER);

        ColumnConstraints col = new ColumnConstraints();
        col.setPrefWidth(200);
        col.setMinWidth(180);
        grid.getColumnConstraints().addAll(col, new ColumnConstraints(col.getPrefWidth()));

        grid.add(makeCard("⏱", "Timer", "Start a study session", "timer"), 0, 0);
        grid.add(makeCard("📚", "Subjects", "Manage your subjects", "subjects"), 1, 0);
        grid.add(makeCard("📖", "Sessions", "View your study history", "sessions"), 0, 1);
        grid.add(makeCard("📊", "Dashboard", "Stats & streaks", "dashboard"), 1, 1);
        grid.add(makeCard("🎯", "Goals", "Finish your goals", "goals"), 2, 0);


        // ── Daily Quote ───────────────────────────────────────────────────────
        QuoteCard quoteCard = new QuoteCard();

        // ── Study Tip ─────────────────────────────────────────────────────────
        StudyTipWidget tipWidget = new StudyTipWidget();

        getChildren().addAll(title, subtitle, streakLabel, quoteCard, tipWidget, grid);
    }

    private VBox makeCard(String icon, String titleText, String subtitleText, String screen) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(28, 20, 28, 20));
        card.setPrefSize(200, 170);
        card.getStyleClass().add("nav-card");
        card.setCursor(Cursor.HAND);

        // Shadow
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.4));
        shadow.setRadius(16);
        shadow.setOffsetY(4);
        card.setEffect(shadow);

        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("card-icon");

        Label cardTitle = new Label(titleText);
        cardTitle.getStyleClass().add("card-title");

        Label cardSubtitle = new Label(subtitleText);
        cardSubtitle.getStyleClass().add("card-subtitle");
        cardSubtitle.setWrapText(true);
        cardSubtitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        card.getChildren().addAll(iconLabel, cardTitle, cardSubtitle);

        // Hover effects
        card.setOnMouseEntered(e -> {
            card.setScaleX(1.05);
            card.setScaleY(1.05);
            card.getStyleClass().add("nav-card-hover");
        });
        card.setOnMouseExited(e -> {
            card.setScaleX(1.0);
            card.setScaleY(1.0);
            card.getStyleClass().remove("nav-card-hover");
        });

        card.setOnMouseClicked(e -> navigator.accept(screen));

        return card;
    }
}
