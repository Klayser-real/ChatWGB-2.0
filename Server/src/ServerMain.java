import java.io.FileNotFoundException;

public class ServerMain {
    public static void main(String[] args) throws FileNotFoundException {
        Server server = new Server(8989);
        server.GetTopic();
        server.start();
    }
}
