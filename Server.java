package chapter33;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server extends Application {
  @Override
  public void start(Stage primaryStage) {
    TextArea ta = new TextArea();

    // Create a scene and place it in the stage
    Scene scene = new Scene(new ScrollPane(ta), 450, 200);
    primaryStage.setTitle("Server");
    primaryStage.setScene(scene);
    primaryStage.show();

    new Thread(() -> {
      try {
        // Create a server socket
        ServerSocket serverSocket = new ServerSocket(8080);
        Platform.runLater(() ->
                ta.appendText("Server started at " + new Date() + '\n'));

        // Listen for a connection request
        Socket socket = serverSocket.accept();

        // Create data input and output streams
        DataInputStream inputFromClient = new DataInputStream(
                socket.getInputStream());
        DataOutputStream outputToClient = new DataOutputStream(
                socket.getOutputStream());

        while (true) {
          // Receive the number from the client
          int number = inputFromClient.readInt();

          // Check if the received number is a termination signal
          if (number == -1) {
            Platform.runLater(() ->
                    ta.appendText("Server is shutting down...\n"));
            break;
          }

          // Check if the number is prime
          boolean isPrime = isPrime(number);

          // Send the result back to the client
          outputToClient.writeBoolean(isPrime);
          outputToClient.flush();

          Platform.runLater(() ->
                  ta.appendText("Number received from client: " + number + '\n' +
                          "Is prime? " + isPrime + '\n'));
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }).start();
  }

  //To calculate if a number is prime, check if it is divisible by any numbers other than 1 and the number itself
  private static boolean isPrime(int number) {
    if (number <= 1) {
      return false;
    }
    for (int i = 2; i <= Math.sqrt(number); i++) {
      if (number % i == 0) {
        return false;
      }
    }
    return true;
  }

  public static void main(String[] args) {
    launch(args);
  }
}
