package com.studytracker.ui;

import com.studytracker.service.DailyQuoteService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * A self-contained card that fetches and displays a daily motivational quote.
 * The fetch happens on a background thread so the UI stays responsive.
 */
public class QuoteCard extends VBox {

    public QuoteCard() {
        getStyleClass().add("quote-card");
        setAlignment(Pos.CENTER);
        setPadding(new Insets(18, 24, 18, 24));
        setMaxWidth(560);

        Label icon = new Label("💡");
        icon.getStyleClass().add("quote-icon");

        Label quoteLabel = new Label("Loading quote…");
        quoteLabel.getStyleClass().add("quote-text");
        quoteLabel.setWrapText(true);
        quoteLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Label authorLabel = new Label("");
        authorLabel.getStyleClass().add("quote-author");

        getChildren().addAll(icon, quoteLabel, authorLabel);

        // Fetch on background thread
        Thread fetchThread = new Thread(() -> {
            String[] result = new DailyQuoteService().fetchQuote();
            Platform.runLater(() -> {
                quoteLabel.setText("\"" + result[0] + "\"");
                authorLabel.setText("— " + result[1]);
            });
        });
        fetchThread.setDaemon(true);
        fetchThread.start();
    }
}
