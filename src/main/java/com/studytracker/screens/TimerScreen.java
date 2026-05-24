package com.studytracker.screens;

import com.studytracker.controller.DataController;
import com.studytracker.model.InvalidStudyDataException;
import com.studytracker.model.StudySession;
import com.studytracker.model.Subject;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import javafx.scene.media.Media;

public class TimerScreen extends VBox {

    private final Consumer<String> navigator;
    private MediaPlayer mediaPlayer;

    // The variables for the free timer
    private long elapsedSeconds = 0;
    private boolean running = false;
    private Timeline timeline;

    // ── Pomodoro state ────────────────────────────────────────────────────────
    private static final long WORK_SECONDS  = 25 * 60;
    private static final long BREAK_SECONDS =  5 * 60;
    private boolean pomodoroMode      = false;
    private boolean onBreak           = false;
    private long    pomodoroRemaining = WORK_SECONDS;
    private int     pomodoroRound     = 1;

    // ── UI refs ───────────────────────────────────────────────────────────────
    private Label        timerDisplay;
    private Button       startBtn, pauseBtn, stopBtn;
    private ComboBox<Subject> subjectCombo;
    private Label        statusLabel;
    private Label        phaseLabel;
    private ToggleButton modeToggle;

    // Music field
    private MediaPlayer musicPlayer;

    public TimerScreen(Consumer<String> navigator) {
        this.navigator = navigator;
        buildUI();
    }

    private void buildUI() {
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(50, 40, 40, 40));
        setSpacing(28);
        getStyleClass().add("screen");
        this.setStyle("-fx-background-image: url('/com/studytracker/css/bg.gif'); " +
              "-fx-background-size: cover; " +
              "-fx-background-position: center;");

        

        // Back button
        Button back = new Button("← Back");
        back.getStyleClass().add("back-btn");
        back.setOnAction(e -> {
            if (mediaPlayer != null) mediaPlayer.stop();
            navigator.accept("home");
        });
        HBox backBar = new HBox(back);
        backBar.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("⏱  Timer");
        title.getStyleClass().add("screen-title");

        // Subject picker
        Label subLabel = new Label("Select Subject:");
        subLabel.getStyleClass().add("field-label");
        subjectCombo = new ComboBox<>();
        subjectCombo.getItems().addAll(DataController.getInstance().getSubjects());
        subjectCombo.setPromptText("Choose a subject...");
        subjectCombo.getStyleClass().add("combo-box-dark");
        subjectCombo.setPrefWidth(320);
        VBox subjectBox = new VBox(8, subLabel, subjectCombo);
        subjectBox.setAlignment(Pos.CENTER);

        // Mode toggle
        modeToggle = new ToggleButton("🍅  Pomodoro Mode");
        modeToggle.getStyleClass().add("back-btn");
        modeToggle.setOnAction(e -> switchMode());

        phaseLabel = new Label("");
        phaseLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");

        VBox modeBox = new VBox(6, modeToggle, phaseLabel);
        modeBox.setAlignment(Pos.CENTER);

        // Timer display
        timerDisplay = new Label("00:00:00");
        timerDisplay.getStyleClass().add("timer-display");

        // Buttons
        startBtn = new Button("▶  Start");
        startBtn.getStyleClass().addAll("timer-btn", "btn-start");

        pauseBtn = new Button("⏸  Pause");
        pauseBtn.getStyleClass().addAll("timer-btn", "btn-pause");
        pauseBtn.setDisable(true);

        stopBtn = new Button("⏹  Stop");
        stopBtn.getStyleClass().addAll("timer-btn", "btn-stop");
        stopBtn.setDisable(true);

        HBox btnRow = new HBox(16, startBtn, pauseBtn, stopBtn);
        btnRow.setAlignment(Pos.CENTER);

        statusLabel = new Label("");
        statusLabel.getStyleClass().add("status-label");
        statusLabel.setWrapText(true);
        statusLabel.setMaxWidth(400);
        statusLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        VBox card = new VBox(24, subjectBox, modeBox, timerDisplay, btnRow, statusLabel);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(40));
        card.getStyleClass().add("content-card");
        card.setMaxWidth(500);

        getChildren().addAll(backBar, title, card);

        // Timeline
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> onTick()));
        timeline.setCycleCount(Animation.INDEFINITE);

        startBtn.setOnAction(e -> handleStart());
        pauseBtn.setOnAction(e -> handlePause());
        stopBtn.setOnAction(e -> handleStop());
    }

    // ── Switch between Free and Pomodoro ──────────────────────────────────────

    private void switchMode() {
        if (running) {
            modeToggle.setSelected(pomodoroMode);
            return;
        }
        pomodoroMode = modeToggle.isSelected();
        resetState();
    }

    // ── Tick handler ──────────────────────────────────────────────────────────

    private void onTick() {
        if (pomodoroMode) {
            pomodoroRemaining--;
            updatePomodoroDisplay();

            if (pomodoroRemaining <= 0) {
                if (!onBreak) {
                    
                    autoSavePomodoroSession();
                    playBreakMusic();
                    onBreak = true;
                    pomodoroRemaining = BREAK_SECONDS;
                    phaseLabel.setText("Round " + pomodoroRound + "  |  ☕ Break time! (5 min)");
                    phaseLabel.setStyle("-fx-text-fill: #fb923c; -fx-font-size: 12px;");
                    statusLabel.setText("✓  25 min done! Session saved. Enjoy your break.");
                    statusLabel.setStyle("-fx-text-fill: #4ade80;");
                } else {
                    stopBreakMusic();
                    pomodoroRound++;
                    onBreak = false;
                    pomodoroRemaining = WORK_SECONDS;
                    phaseLabel.setText("Round " + pomodoroRound + "  |  🟢 Work (25 min)");
                    phaseLabel.setStyle("-fx-text-fill: #4ade80; -fx-font-size: 12px;");
                    statusLabel.setText("Break over! Round " + pomodoroRound + " started. Stay focused!");
                    statusLabel.setStyle("-fx-text-fill: #22d3ee;");
                }
            }
        } else {
            elapsedSeconds++;
            timerDisplay.setText(formatTime(elapsedSeconds));
        }
    }

    private void updatePomodoroDisplay() {
        long m = pomodoroRemaining / 60;
        long s = pomodoroRemaining % 60;
        timerDisplay.setText(String.format("00:%02d:%02d", m, s));
    }

    // ── Button handlers ───────────────────────────────────────────────────────

    private void handleStart() {
        if (subjectCombo.getValue() == null) {
            statusLabel.setText("⚠  Please select a subject first.");
            statusLabel.setStyle("-fx-text-fill: #0bf5f5;");
            return;
        }
        running = true;
        timeline.play();
        startBtn.setDisable(true);
        pauseBtn.setDisable(false);
        stopBtn.setDisable(false);
        modeToggle.setDisable(true);

        if (pomodoroMode) {
            phaseLabel.setText("Round 1  |  🟢 Work (25 min)");
            phaseLabel.setStyle("-fx-text-fill: #4ade80; -fx-font-size: 12px;");
            statusLabel.setText("🍅  Pomodoro started! Focus for 25 minutes.");
            statusLabel.setStyle("-fx-text-fill: #4ade80;");
        } else {
            statusLabel.setText("Session in progress...");
            statusLabel.setStyle("-fx-text-fill: #4ade80;");
        }
    }

    private void handlePause() {
        if (running) {
            timeline.pause();
            running = false;
            pauseBtn.setText("▶  Resume");
            pauseBtn.getStyleClass().remove("btn-pause");
            pauseBtn.getStyleClass().add("btn-start");
            statusLabel.setText("⏸  Paused");
            statusLabel.setStyle("-fx-text-fill: #94a3b8;");
        } else {
            timeline.play();
            running = true;
            pauseBtn.setText("⏸  Pause");
            pauseBtn.getStyleClass().remove("btn-start");
            pauseBtn.getStyleClass().add("btn-pause");
            statusLabel.setText(pomodoroMode ? "🍅  Resumed." : "Session in progress...");
            statusLabel.setStyle("-fx-text-fill: #4ade80;");
        }
    }

    private void handleStop() {
        timeline.stop();
        running = false;

        if (!pomodoroMode && elapsedSeconds > 0 && subjectCombo.getValue() != null) {
            StudySession session = new StudySession(
                subjectCombo.getValue().getName(), elapsedSeconds, LocalDateTime.now()
            );
            try {
                DataController.getInstance().addSession(session);
                int xp = (int) session.calculateScore();
                statusLabel.setText("✓  Session saved! Duration: " + formatTime(elapsedSeconds) + "  |  +" + xp + " XP");
                statusLabel.setStyle("-fx-text-fill: #4ade80;");
            } catch (InvalidStudyDataException ex) {
                statusLabel.setText("⚠  " + ex.getMessage());
                statusLabel.setStyle("-fx-text-fill: #f59e0b;");
            }
        }

        resetState();
        startBtn.setDisable(false);
        pauseBtn.setDisable(true);
        pauseBtn.setText("⏸  Pause");
        pauseBtn.getStyleClass().remove("btn-start");
        pauseBtn.getStyleClass().add("btn-pause");
        stopBtn.setDisable(true);
        modeToggle.setDisable(false);
    }

    

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void autoSavePomodoroSession() {
        if (subjectCombo.getValue() == null) return;
        StudySession session = new StudySession(
            subjectCombo.getValue().getName(), WORK_SECONDS, LocalDateTime.now()
        );
        try {
            DataController.getInstance().addSession(session);
        } catch (InvalidStudyDataException ex) {
            statusLabel.setText("⚠  Could not save session: " + ex.getMessage());
            statusLabel.setStyle("-fx-text-fill: #f59e0b;");
        }
    }

    private void resetState() {
        elapsedSeconds = 0;
        pomodoroRemaining = WORK_SECONDS;
        onBreak = false;
        pomodoroRound = 1;
        timerDisplay.setText(pomodoroMode ? "00:25:00" : "00:00:00");
        if (!pomodoroMode) phaseLabel.setText("");
        else phaseLabel.setText("Round 1  |  🟢 Work (25 min)");
    }

    private String formatTime(long seconds) {
        long h = seconds / 3600;
        long m = (seconds % 3600) / 60;
        long s = seconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }


    private void playBreakMusic() {
    try {
        String musicUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3";
        Media media = new Media(musicUrl);
        musicPlayer = new MediaPlayer(media);
        musicPlayer.setVolume(0.8);
        musicPlayer.play();
    } catch (Exception e) {
        System.err.println("Music error: " + e.getMessage());
    }
}

private void stopBreakMusic() {
    if (musicPlayer != null) {
        musicPlayer.stop();
        musicPlayer = null;
    }
}
}