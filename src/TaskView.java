import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;


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

        root.setTop(createTopSection());
        root.setLeft(createTaskList());
        root.setRight(createTaskForm());

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Todo List - " + currentUser);
        stage.setScene(scene);
    }



    private VBox createTopSection() {

        VBox topSection = new VBox(10);
        topSection.setPadding(new Insets(10));


        searchField = new TextField();
        searchField.setPromptText("Search tasks...");


        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredTasks.setPredicate(task -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                return task.getTitle().toLowerCase().contains(newValue.toLowerCase()) ||
                        task.getDescription().toLowerCase().contains(newValue.toLowerCase());
            });
        });


        topSection.getChildren().addAll(new Label("Search:"), searchField);
        return topSection;
    }

    private VBox createTaskList() {

        VBox listSection = new VBox(10);
        listSection.setPadding(new Insets(10));
        listSection.setPrefWidth(400);


        taskListView = new ListView<>(filteredTasks);


        taskListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                } else {
                    setText(task.getTitle() + " (" + task.getPriority() + ")");
                }
            }
        });

        taskListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        titleField.setText(newValue.getTitle());
                        descriptionArea.setText(newValue.getDescription());
                        priorityComboBox.setValue(newValue.getPriority());
                    }
                }
        );

        listSection.getChildren().addAll(new Label("Tasks:"), taskListView);
        return listSection;
    }

    private VBox createTaskForm() {
        VBox formSection = new VBox(10);
        formSection.setPadding(new Insets(10));
        formSection.setPrefWidth(300);

        titleField = new TextField();
        titleField.setPromptText("Task Title");

        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Task Description");
        descriptionArea.setPrefRowCount(3);

        priorityComboBox = new ComboBox<>(
                FXCollections.observableArrayList(Task.Priority.values())
        );
        priorityComboBox.setPromptText("Select Priority");

        Button addButton = new Button("Add Task");
        addButton.setOnAction(e -> handleAddTask());

        Button deleteButton = new Button("Delete Task");
        deleteButton.setOnAction(e -> handleDeleteTask());

        Button updateButton = new Button("Update Task");
        updateButton.setOnAction(e -> handleUpdateTask());

        formSection.getChildren().addAll(
                new Label("Title:"),
                titleField,
                new Label("Description:"),
                descriptionArea,
                new Label("Priority:"),
                priorityComboBox,
                new HBox(10, addButton, updateButton, deleteButton)
        );

        return formSection;
    }

    private void handleDeleteTask() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            tasks.remove(selectedTask);
            clearForm();
        } else {
            showAlert("Error", "Please select a task to delete");
        }
    }

    private void handleUpdateTask() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            try {
                String title = titleField.getText().trim();
                String description = descriptionArea.getText().trim();
                Task.Priority priority = priorityComboBox.getValue();

                if (title.isEmpty()) {
                    throw new IllegalArgumentException("Title is required");
                }
                if (priority == null) {
                    throw new IllegalArgumentException("Please select a priority");
                }

                selectedTask.setTitle(title);
                selectedTask.setDescription(description);
                selectedTask.setPriority(priority);

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

            if (title.isEmpty()) {
                throw new IllegalArgumentException("Title is required");
            }
            if (priority == null) {
                throw new IllegalArgumentException("Please select a priority");
            }

            Task newTask = new Task(title, description, priority);
            tasks.add(newTask);
            clearForm();

        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }


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