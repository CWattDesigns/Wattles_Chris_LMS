package chapter33;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client extends Application {
  // IO streams
  DataOutputStream toServer = null;
  DataInputStream fromServer = null;

  @Override
  public void start(Stage primaryStage) {
    BorderPane paneForTextField = new BorderPane();
    paneForTextField.setPadding(new Insets(5, 5, 5, 5));
    paneForTextField.setStyle("-fx-border-color: green");
    paneForTextField.setLeft(new Label("Enter a number: "));

    TextField tf = new TextField();
    tf.setAlignment(Pos.BOTTOM_RIGHT);
    paneForTextField.setCenter(tf);

    BorderPane mainPane = new BorderPane();
    TextArea ta = new TextArea();
    mainPane.setCenter(new ScrollPane(ta));
    mainPane.setTop(paneForTextField);

    // Create a scene and place it in the stage
    Scene scene = new Scene(mainPane, 450, 200);
    primaryStage.setTitle("Client");
    primaryStage.setScene(scene);
    primaryStage.show();

    tf.setOnAction(e -> {
      try {
        // Get the number from the text field
        int number = Integer.parseInt(tf.getText().trim());

        // Send the number to the server
        toServer.writeInt(number);
        toServer.flush();

        // Check if the number is a termination signal
        if (number == -1) {
          toServer.close();
          fromServer.close();
          System.exit(0);
        }

        // Receive the result from the server
        boolean isPrime = fromServer.readBoolean();

        ta.appendText("Is " + number + " prime? " + (isPrime ? "Yes" : "No") + "\n");

      } catch (IOException ex) {
        System.err.println(ex);
      }
    });

    try {
      // Create a socket to connect to the server
      Socket socket = new Socket("localhost", 8080);

      // Create an input stream to receive data from the server
      fromServer = new DataInputStream(socket.getInputStream());

      // Create an output stream to send data to the server
      toServer = new DataOutputStream(socket.getOutputStream());
    } catch (IOException ex) {
      ta.appendText(ex.toString() + '\n');
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
