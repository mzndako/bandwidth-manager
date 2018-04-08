package screen;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

class LoginClient {
  public LoginClient() {
    try {
      SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
      SSLSocket socket = (SSLSocket) socketFactory.createSocket("localhost", 7070);
      PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
      String userName = "MyName";
      output.println(userName);
      String password = "MyPass";
      output.println(password);
      output.flush();
      BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String response = input.readLine();
      System.out.println(response);

      output.close();
      input.close();
      socket.close();
    } catch (IOException ioException) {
      ioException.printStackTrace();
    } finally {
      System.exit(0);
    }
  }

  public static void main(String args[]) {
    new LoginClient();
  }
}
//////////////////////////////////////////////////////////////////////////////////////////////
class LoginServer {

  private static final String CORRECT_USER_NAME = "Java";

  private static final String CORRECT_PASSWORD = "HowToProgram";

  private SSLServerSocket serverSocket;

  public LoginServer() throws Exception {
    SSLServerSocketFactory socketFactory = (SSLServerSocketFactory) SSLServerSocketFactory
        .getDefault();
    serverSocket = (SSLServerSocket) socketFactory.createServerSocket(7070);

  }

  private void runServer() {
    while (true) {
      try {
        System.err.println("Waiting for connection...");
        SSLSocket socket = (SSLSocket) serverSocket.accept();
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        String userName = input.readLine();
        String password = input.readLine();
    System.err.println("more...");
        if (userName.equals(CORRECT_USER_NAME) && password.equals(CORRECT_PASSWORD)) {
          output.println("Welcome, " + userName);
        } else {
          output.println("Login Failed.");
          System.err.println("more... failed");
        }
        output.close();
        input.close();
        socket.close();

      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    }
  }

  public static void main(String args[]) throws Exception {
    LoginServer server = new LoginServer();
    System.out.println("waiting");
    server.runServer();
  }
}