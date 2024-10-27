import javafx.beans.property.*;


public class Task {
    private final StringProperty title;
    private final StringProperty description;
    private final ObjectProperty<Priority> priority;
    private final BooleanProperty completed;

    public enum Priority {
        LOW("Low Priority"),
        MEDIUM("Medium Priority"),
        HIGH("High Priority");

        private final String display;

        Priority(String display) {
            this.display = display;
        }

        @Override
        public String toString() {
            return display;
        }
    }

    public Task(String title, String description, Priority priority) {
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.priority = new SimpleObjectProperty<>(priority);
        this.completed = new SimpleBooleanProperty(false);
    }

    public StringProperty titleProperty() { return title; }
    public StringProperty descriptionProperty() { return description; }
    public ObjectProperty<Priority> priorityProperty() { return priority; }
    public BooleanProperty completedProperty() { return completed; }

    public String getTitle() { return title.get(); }
    public String getDescription() { return description.get(); }
    public Priority getPriority() { return priority.get(); }
    public boolean isCompleted() { return completed.get(); }

    public void setTitle(String title) { this.title.set(title); }
    public void setDescription(String description) { this.description.set(description); }
    public void setPriority(Priority priority) { this.priority.set(priority); }
    public void setCompleted(boolean completed) { this.completed.set(completed); }
}