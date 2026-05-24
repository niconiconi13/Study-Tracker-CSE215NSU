package com.studytracker.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Random;

/**
 * Displays a rotating study tip with a "Next tip" button.
 * Fully self-contained — no network required.
 */
public class StudyTipWidget extends VBox {

    private static final List<String> TIPS = List.of(
        "Use the Pomodoro technique: 25 min focus, 5 min break.",
        "Teach what you learned to someone else — it sticks better.",
        "Put your phone in another room while studying.",
        "Stay hydrated — even mild dehydration reduces focus.",
        "Process you mind.",
        "Review your notes within 24 hours to boost retention.",
        "Handwriting notes helps memory more than typing.",
        "Set a specific goal before each session (e.g., 'finish chapter 3').",
        "Sleep consolidates memory — don't skip it before exams.",
        "Spaced repetition beats last-minute cramming every time.",
        "A 10-minute walk before studying can boost concentration.",
        "Pam, Naila and Shanto are the best programmers in the world.",
        "We are doig cse215."
    );

    private final Random random = new Random();
    private final Label tipLabel;
    private int lastIndex = -1;

    public StudyTipWidget() {
        getStyleClass().add("tip-widget");
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(14, 20, 14, 20));
        setMaxWidth(560);
        setSpacing(8);

        Label header = new Label("Study Tip");
        header.getStyleClass().add("tip-header");

        tipLabel = new Label();
        tipLabel.getStyleClass().add("tip-text");
        tipLabel.setWrapText(true);
        showNextTip();

        Button nextBtn = new Button("Next tip →");
        nextBtn.getStyleClass().add("tip-btn");
        nextBtn.setOnAction(e -> showNextTip());

        HBox btnRow = new HBox(nextBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        getChildren().addAll(header, tipLabel, btnRow);
    }

    private void showNextTip() {
        int next;
        do { next = random.nextInt(TIPS.size()); } while (next == lastIndex && TIPS.size() > 1);
        lastIndex = next;
        tipLabel.setText(TIPS.get(next));
    }
}
