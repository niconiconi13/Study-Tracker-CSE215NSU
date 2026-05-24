package com.studytracker.main;

import com.studytracker.screens.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    private Stage primaryStage;
    private Scene mainScene;
    private StackPane root;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        root = new StackPane();
        root.setStyle("-fx-background-color: #0f172a;");

        mainScene = new Scene(root, 860, 680);

        // Load CSS
        try {
            String css = Objects.requireNonNull(
                getClass().getResource("/com/studytracker/css/style.css")
            ).toExternalForm();
            mainScene.getStylesheets().add(css);
        } catch (Exception e) {
            System.err.println("CSS not found: " + e.getMessage());
        }

        navigateTo("home");

        stage.setTitle("Study Time Tracker");
        stage.setScene(mainScene);
        stage.setMinWidth(720);
        stage.setMinHeight(560);
        stage.show();
    }

    private void navigateTo(String screen) {
        root.getChildren().clear();
        switch (screen) {
            case "home"      -> root.getChildren().add(new HomeScreen(this::navigateTo));
            case "timer"     -> root.getChildren().add(new TimerScreen(this::navigateTo));
            case "subjects"  -> root.getChildren().add(new SubjectsScreen(this::navigateTo));
            case "sessions"  -> root.getChildren().add(new SessionsScreen(this::navigateTo));
            case "dashboard" -> root.getChildren().add(new DashboardScreen(this::navigateTo));
            
            case "goals"     -> root.getChildren().add(new GoalsScreen(this::navigateTo));
            default          -> root.getChildren().add(new HomeScreen(this::navigateTo));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
