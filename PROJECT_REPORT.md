# Study Time Tracker Project Report

## Introduction

The Study Time Tracker is a desktop application developed using Java and JavaFX framework to help students and learners monitor their study habits, track time spent on different subjects, and maintain study streaks. The application provides a gamified experience with XP (experience points) system, Pomodoro timer functionality, and motivational features like daily quotes and study tips.

The project is built as a Maven-based Java application targeting Java 25 with JavaFX 23.0.1 for the graphical user interface. It follows object-oriented design principles with a clear separation of concerns across model, view (screens), and controller layers.

## Methodology

### Technology Stack
- **Language**: Java 25
- **Framework**: JavaFX 23.0.1 for GUI
- **Build Tool**: Maven 3.11.0
- **Persistence**: Plain text file storage (no database)
- **Styling**: CSS with custom themes and background images

### Architecture
The application follows a Model-View-Controller (MVC) pattern:

- **Model Layer**: Contains data classes like `StudySession`, `Subject`, `StudyGoal`, and `AppData`
- **Controller Layer**: `DataController` handles all data operations and persistence
- **View Layer**: Various screen classes extending JavaFX containers for UI
- **Service Layer**: `DailyQuoteService` for motivational content

### Key Features Implementation

1. **Timer Functionality**: Supports both free-form timing and Pomodoro technique (25-minute work sessions with 5-minute breaks)
2. **Subject Management**: Users can add/remove study subjects with weekly hour goals
3. **Session Tracking**: Records study sessions with timestamps and durations
4. **Statistics Dashboard**: Displays total study time, session count, XP earned, and current streaks
5. **Goal Setting**: Allows setting specific hour targets for subjects
6. **Gamification**: XP system with bonuses for longer sessions
7. **Motivational Features**: Daily quotes and study tips

### Data Persistence
Data is stored in a plain text file (`study_tracker_data.txt`) in the user's home directory using a custom delimited format:
```
SUBJECT|name|weeklyGoalHours|createdDate
SESSION|subject|durationSeconds|timestamp|createdDate
GOAL|subjectName|targetHours|createdDate
```

## Uses

The Study Time Tracker application serves several important purposes:

1. **Study Habit Tracking**: Helps users monitor how much time they spend studying each subject
2. **Productivity Enhancement**: Pomodoro timer encourages focused work sessions with regular breaks
3. **Goal Achievement**: Weekly goals and streak tracking motivate consistent study habits
4. **Progress Visualization**: Dashboard provides visual feedback on study progress and achievements
5. **Motivation**: Daily quotes and XP system gamify the learning process
6. **Time Management**: Helps students balance time across different subjects

The application is particularly useful for:
- Students preparing for exams
- Self-learners following structured study plans
- Individuals trying to build consistent study habits
- Educators tracking their preparation time

## Limitations

While the Study Time Tracker provides valuable functionality, it has several limitations:

1. **Data Storage**: Uses plain text files instead of a robust database, limiting scalability and concurrent access
2. **No User Accounts**: Single-user application with no authentication or multi-user support
3. **No Cloud Sync**: Data is stored locally only, with no backup or synchronization features
4. **Limited Analytics**: Basic statistics without advanced reporting or trend analysis
5. **No Mobile Version**: Desktop-only application, not accessible on mobile devices
6. **Fixed UI Layout**: Limited responsiveness for different screen sizes
7. **No Export Features**: No way to export data to other formats (CSV, PDF, etc.)
8. **Memory Usage**: All data loaded into memory at startup, not suitable for very large datasets

## Code Snippets

### Main Application Entry Point
```java
public class Main extends Application {
    @Override
    public void start(Stage stage) {
        root = new StackPane();
        root.setStyle("-fx-background-color: #0f172a;");
        
        mainScene = new Scene(root, 860, 680);
        
        // Load CSS
        String css = Objects.requireNonNull(
            getClass().getResource("/com/studytracker/css/style.css")
        ).toExternalForm();
        mainScene.getStylesheets().add(css);
        
        navigateTo("home");
        
        stage.setTitle("Study Time Tracker");
        stage.setScene(mainScene);
        stage.show();
    }
}
```

### Study Session Model with XP Calculation
```java
public class StudySession extends TrackerItem {
    private String subject;
    private long durationSeconds;
    
    @Override
    public double calculateScore() {
        double minutes = durationSeconds / 60.0;
        double xp = minutes * 10;
        if (durationSeconds >= 3600) {
            xp *= 1.5;  // 50% bonus for sessions >= 1 hour
        }
        return Math.round(xp * 100.0) / 100.0;
    }
}
```

### Data Persistence Implementation
```java
public void saveData() {
    try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
        for (Subject s : appData.getSubjects()) {
            writer.println("SUBJECT|" + s.getName() + "|" + 
                         s.getWeeklyGoalHours() + "|" + s.getCreatedDate());
        }
        for (StudySession session : appData.getSessions()) {
            writer.println("SESSION|" + session.getSubject() + "|" + 
                         session.getDurationSeconds() + "|" + 
                         session.getTimestamp() + "|" + session.getCreatedDate());
        }
    } catch (IOException e) {
        System.err.println("Failed to save data: " + e.getMessage());
    }
}
```

## Console Output Snippet

When running the application with `mvn javafx:run`, the console shows:

```
[INFO] Scanning for projects...
[INFO] ----------------< com.studytracker:study-time-tracker >-----------------
[INFO] Building Study Time Tracker 1.0.0
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] >>> javafx:0.0.8:run (default-cli) > process-classes @ study-time-tracker >>>
[INFO] --- resources:3.4.0:resources (default-resources) ---
[INFO] Copying 11 resources from src\main\resources to target\classes
[INFO] --- compiler:3.11.0:compile (default-compile) ---
[INFO] Nothing to compile - all classes are up to date
[INFO] <<< javafx:0.0.8:run (default-cli) < process-classes @ study-time-tracker <<<
[INFO] --- javafx:0.0.8:run (default-cli) @ study-time-tracker ---
WARNING: A restricted method in java.lang.System has been called
WARNING: java.lang.System::load has been called by com.sun.glass.utils.NativeLibLoader in module javafx.graphics
WARNING: Use --enable-native-access=javafx.graphics to avoid a warning for callers in this module
WARNING: Restricted methods will be blocked in a future release unless native access is enabled
WARNING: A terminally deprecated method in sun.misc.Unsafe has been called
WARNING: sun.misc.Unsafe::allocateMemory has been called by com.sun.marlin.OffHeapArray
WARNING: sun.misc.Unsafe::allocateMemory will be removed in a future release
```

## Application Screenshots/Descriptions

### Home Screen
The home screen displays:
- Application title and motivational subtitle
- Current study streak indicator
- Navigation cards for different features (Timer, Subjects, Sessions, Dashboard, Goals)
- Daily motivational quote
- Study tip widget
- Dynamic background image based on time of day

### Timer Screen
Features:
- Subject selection dropdown
- Mode toggle between Free Timer and Pomodoro
- Large digital timer display (HH:MM:SS format)
- Start/Pause/Stop control buttons
- Status messages and phase indicators for Pomodoro mode
- Animated background

### Dashboard Screen
Shows statistics including:
- Total study time
- Number of sessions
- Total XP earned
- Current streak
- Recent sessions list
- Progress indicators

## Conclusion

The Study Time Tracker represents a well-designed desktop application that successfully combines productivity tools with gamification elements to encourage consistent study habits. Built with modern Java technologies, it demonstrates good software engineering practices through its clean architecture and separation of concerns.

The application's core functionality - time tracking, subject management, and progress visualization - effectively addresses the needs of students and learners. The addition of Pomodoro timing and motivational features enhances user engagement beyond basic tracking.

However, the current implementation has limitations in scalability, data management, and cross-platform availability that could be addressed in future versions. The use of plain text persistence, while simple, may not be suitable for users with extensive study data or those requiring data portability.

Overall, this project serves as an excellent example of applying object-oriented design and JavaFX for creating user-friendly desktop applications. The gamification approach and focus on user experience make it a practical tool for academic productivity, though it would benefit from expanded features like cloud synchronization and advanced analytics for broader adoption.</content>
<parameter name="filePath">c:\Users\DELL\OneDrive\Desktop\LATEST\study-time-tracker-enhanced\study-tracker-enhanced\PROJECT_REPORT.md