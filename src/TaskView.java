import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Manages the main task interface.
 * Handles task creation, editing, deletion, and display.
 */
public class TaskView {
    private final Stage stage;
    private final String currentUser;
    private final ObservableList<Task> tasks;
    private final FilteredList<Task> filteredTasks;

    // UI Components
    private ListView<Task> taskListView;
    private TextField titleField;
    private TextArea descriptionArea;
    private ComboBox<Task.Priority> priorityComboBox;
    private TextField searchField;

    public TaskView(Stage stage, String username) {
        this.stage = stage;
        this.currentUser = username;
        this.tasks = FXCollections.observableArrayList();
        this.filteredTasks = new FilteredList<>(tasks, p -> true);
        createTaskScene();
    }

    private void createTaskScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Create main sections
        root.setTop(createTopSection());
        root.setLeft(createTaskList());
        root.setRight(createTaskForm());

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Todo List - " + currentUser);
        stage.setScene(scene);
    }

    // UI Creation Methods...
    // (Previous TaskView methods remain largely the same, 
    // but with added exception handling and simplified layout)

    /**
     * Handles task addition with input validation
     */

    private VBox createTopSection() {
        // Create a container for the top section with 10px spacing
        VBox topSection = new VBox(10);
        topSection.setPadding(new Insets(10));

        // Initialize search functionality
        searchField = new TextField();
        searchField.setPromptText("Search tasks...");

        // Add listener to filter tasks based on search input
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredTasks.setPredicate(task -> {
                // If search is empty, show all tasks
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Filter tasks based on title or description containing search text
                return task.getTitle().toLowerCase().contains(newValue.toLowerCase()) ||
                        task.getDescription().toLowerCase().contains(newValue.toLowerCase());
            });
        });

        // Add components to top section
        topSection.getChildren().addAll(new Label("Search:"), searchField);
        return topSection;
    }

    private VBox createTaskList() {
        // Create a container for the task list with 10px spacing
        VBox listSection = new VBox(10);
        listSection.setPadding(new Insets(10));
        listSection.setPrefWidth(400);

        // Initialize ListView with filtered tasks
        taskListView = new ListView<>(filteredTasks);

        // Custom cell factory to display task information
        taskListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                } else {
                    // Display task title and priority in the list
                    setText(task.getTitle() + " (" + task.getPriority() + ")");
                }
            }
        });

        // Add selection listener to populate form fields when a task is selected
        taskListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        // Fill form fields with selected task details
                        titleField.setText(newValue.getTitle());
                        descriptionArea.setText(newValue.getDescription());
                        priorityComboBox.setValue(newValue.getPriority());
                    }
                }
        );

        // Add components to list section
        listSection.getChildren().addAll(new Label("Tasks:"), taskListView);
        return listSection;
    }

    private VBox createTaskForm() {
        // Create a container for the form with 10px spacing
        VBox formSection = new VBox(10);
        formSection.setPadding(new Insets(10));
        formSection.setPrefWidth(300);

        // Initialize form input fields
        titleField = new TextField();
        titleField.setPromptText("Task Title");

        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Task Description");
        descriptionArea.setPrefRowCount(3);

        // Initialize priority dropdown with enum values
        priorityComboBox = new ComboBox<>(
                FXCollections.observableArrayList(Task.Priority.values())
        );
        priorityComboBox.setPromptText("Select Priority");

        // Create action buttons
        Button addButton = new Button("Add Task");
        addButton.setOnAction(e -> handleAddTask());

        Button deleteButton = new Button("Delete Task");
        deleteButton.setOnAction(e -> handleDeleteTask());

        Button updateButton = new Button("Update Task");
        updateButton.setOnAction(e -> handleUpdateTask());

        // Arrange all form components vertically
        formSection.getChildren().addAll(
                new Label("Title:"),
                titleField,
                new Label("Description:"),
                descriptionArea,
                new Label("Priority:"),
                priorityComboBox,
                new HBox(10, addButton, updateButton, deleteButton) // Buttons in horizontal layout
        );

        return formSection;
    }

    private void handleDeleteTask() {
        // Get the currently selected task
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            // Remove the task and clear the form
            tasks.remove(selectedTask);
            clearForm();
        } else {
            showAlert("Error", "Please select a task to delete");
        }
    }

    private void handleUpdateTask() {
        // Get the currently selected task
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            try {
                // Get values from form fields
                String title = titleField.getText().trim();
                String description = descriptionArea.getText().trim();
                Task.Priority priority = priorityComboBox.getValue();

                // Validate input
                if (title.isEmpty()) {
                    throw new IllegalArgumentException("Title is required");
                }
                if (priority == null) {
                    throw new IllegalArgumentException("Please select a priority");
                }

                // Update task with new values
                selectedTask.setTitle(title);
                selectedTask.setDescription(description);
                selectedTask.setPriority(priority);

                // Refresh the view and clear form
                taskListView.refresh();
                clearForm();

            } catch (Exception e) {
                showAlert("Error", e.getMessage());
            }
        } else {
            showAlert("Error", "Please select a task to update");
        }
    }

    private void clearForm() {
        // Reset all form fields to their default state
        titleField.clear();
        descriptionArea.clear();
        priorityComboBox.setValue(null);
        taskListView.getSelectionModel().clearSelection();
    }

    private void handleAddTask() {
        try {
            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();
            Task.Priority priority = priorityComboBox.getValue();

            // Validate input
            if (title.isEmpty()) {
                throw new IllegalArgumentException("Title is required");
            }
            if (priority == null) {
                throw new IllegalArgumentException("Please select a priority");
            }

            // Create and add task
            Task newTask = new Task(title, description, priority);
            tasks.add(newTask);
            clearForm();

        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    /**
     * Shows an error dialog with the specified message
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void show() {
        stage.show();
    }
}