package gui;

import email.EmailSender;
import javafx.animation.KeyFrame;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class MainController {

    private long startTime;
    private boolean isRecording = false;
    private Timeline timeUpdateTimeline;

    @FXML
    private Label statusLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private ListView<String> emailListView;

    @FXML
    private TextField emailTextField;

    final private List<File> generatedFiles = new ArrayList<>();
    final private List<String> emailAddresses = new ArrayList<>();

    @FXML
    public void initialize() {
        updateStatus("Idle");
    }

    @FXML
    public void addEmail() {
        String email = emailTextField.getText().trim();
        if (!email.isEmpty() && !emailAddresses.contains(email)) {

            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            Pattern pattern = Pattern.compile(emailRegex);
            Matcher matcher = pattern.matcher(email);
            regexMatch(email, matcher);
        }
    }

    @FXML
    public void removeEmail() {
        String selectedEmail = emailListView.getSelectionModel().getSelectedItem();
        if (selectedEmail != null) {
            emailAddresses.remove(selectedEmail);
            emailListView.getItems().remove(selectedEmail);
        }
    }

    @FXML
    public void startRecording() {


        updateStatus("Recording...");


        startTime = System.currentTimeMillis();
        isRecording = true;
        startTimeUpdate();

        // Symulacja nagrywania
        generatedFiles.clear(); // Czyszczenie poprzednich plików
        generatedFiles.add(new File("example_report.txt")); // Przykładowy plik
    }

    @FXML
    public void stopRecording() {
        updateStatus("Stopped");
        isRecording = false;
        stopTimeUpdate();
    }
    private void startTimeUpdate() {
        // Update the time label every second
        timeUpdateTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (isRecording) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                long seconds = (elapsedTime / 1000) % 60;
                long minutes = (elapsedTime / 60000) % 60;
                long hours = (elapsedTime / 3600000) % 24;  // Calculate hours
                timeLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));  // Update time format
            }
        }));
        timeUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
        timeUpdateTimeline.play();
    }

    private void stopTimeUpdate() {
        // Stop the time update, effectively stopping the timer
        if (timeUpdateTimeline != null) {
            timeUpdateTimeline.stop();
        }
    }

    @FXML
    public void sendEmails() {

        EmailSender emailSender = EmailSender.getInstance();
        if(!emailSender.areCredentialsSet()){
            configureEmailSender();
        }

        if(!emailSender.areCredentialsSet()){
            updateStatus("Email sender not set");
            return;
        }
        if (emailAddresses.isEmpty()) {
            updateStatus("No email addresses provided!");
            return;
        }


        try {
            emailSender.sendEmail(
                    emailAddresses,
                    "Meeting Report",
                    "Attached are the files from the meeting.",
                    generatedFiles
            );
            updateStatus("Emails sent successfully!");
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            updateStatus("Failed to send emails.");
        }
    }

    private void updateStatus(String status) {
        statusLabel.setText("Status: " + status);
    }

    @FXML
    public void configureEmailSender() {
        TextInputDialog emailDialog = new TextInputDialog();
        emailDialog.setTitle("Configure Email Sender");
        emailDialog.setHeaderText(null);
        emailDialog.setContentText("Enter Email Address");

        // Oczekiwanie na odpowiedź użytkownika
        Optional<String> result = emailDialog.showAndWait();
        if (result.isPresent()) {
            String email = result.get().trim();
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            Pattern pattern = Pattern.compile(emailRegex);
            Matcher matcher = pattern.matcher(emailDialog.getEditor().getText());

            regexMatch(email, matcher);
        }
        String senderEmail = emailDialog.showAndWait().orElse(null);

        if (senderEmail != null || senderEmail.isEmpty())  {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Email configuration cancelled.");
        }

        TextInputDialog passwordDialog = new TextInputDialog();
        passwordDialog.setTitle("Configure Password Sender");
        passwordDialog.setHeaderText(null);
        passwordDialog.setContentText("Enter Password");
        String senderPassword = passwordDialog.showAndWait().orElse(null);

        if (senderPassword != null || senderPassword.isEmpty())  {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Password configuration cancelled.");
        }

        EmailSender emailSender = EmailSender.getInstance();
        emailSender.setCredentials(senderEmail, senderPassword);

    }

    private void regexMatch(String email, Matcher matcher) {
        if (matcher.matches()) {
            emailAddresses.add(email);
            emailListView.getItems().add(email);
            emailTextField.clear();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Invalid Email");
            alert.showAndWait();
        }
    }
}
