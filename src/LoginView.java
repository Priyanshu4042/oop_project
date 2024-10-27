import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;


public class LoginView {
    private final Stage stage;
    private final UserManager userManager;
    private TextField usernameField;
    private PasswordField passwordField;
    private Label messageLabel;

    public LoginView(Stage stage) {                     //Constructor PrimaryStage Parameter lega
        this.stage = stage;
        this.userManager = UserManager.getInstance();
        createLoginScene();
    }

    private void createLoginScene() {

        VBox root = new VBox(10);           //Vertical Box create hoga
        root.setAlignment(Pos.CENTER);


        setupComponents(root);                 // function that adds all the components in the VBox


        Scene scene = new Scene(root, 400, 300);       //creates a scene jisme VBlock hoga with sll components
        stage.setTitle("Todo List - Login");
        stage.setScene(scene);
    }

    private void setupComponents(VBox root) {

        Label titleLabel = new Label("Todo List Login");
        titleLabel.setStyle("-fx-font-size: 24px");

        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(200);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(200);

        Button loginButton = new Button("Login");
        Button signupButton = new Button("Sign Up");

        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red");

        HBox buttonBox = new HBox(10, loginButton, signupButton) ;
        buttonBox.setAlignment(Pos.CENTER);


        root.getChildren().addAll(              //adds all the UI Component in the Column
                titleLabel,
                usernameField,
                passwordField,
                buttonBox,
                messageLabel
        );

        //button Clicks

        loginButton.setOnAction(e -> handleLogin());
        signupButton.setOnAction(e -> showSignupDialog());
    }

    private void handleLogin() {
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please fill in all fields");
                return;
            }

            if (userManager.authenticate(username, password)) {             //authenticates if creds are correct
                new TaskView(stage, username).show();
            } else {
                messageLabel.setText("Invalid username or password");
            }
        } catch (Exception e) {
            messageLabel.setText("Login error: " + e.getMessage());
        }
    }

    private void showSignupDialog() {

        // Create signup dialog

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sign Up");
        dialog.setHeaderText("Create new account");


        TextField newUsername = new TextField();
        newUsername.setPromptText("Username");
        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("Password");


        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Username:"), newUsername,
                new Label("Password:"), newPassword
        );
        dialog.getDialogPane().setContent(content);


        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    if (userManager.registerUser(newUsername.getText(), newPassword.getText())) {
                        messageLabel.setText("Account created successfully!");
                        messageLabel.setStyle("-fx-text-fill: green");
                    } else {
                        messageLabel.setText("Username already exists");
                    }
                } catch (IllegalArgumentException e) {
                    messageLabel.setText(e.getMessage());
                }
            }
            return dialogButton;
        });

        dialog.showAndWait();
    }

    public void show() {
        stage.show();
    }
}