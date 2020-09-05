import com.sun.jdi.ArrayReference;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;
import java.sql.SQLRecoverableException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Server extends Thread{
    private int serverport;
    private ArrayList<String> topics = new ArrayList<>();
    private ArrayList<ServerWorker> workers = new ArrayList<>();
    public String generalTopic;
    private ArrayList<ArrayList<String>> database = new ArrayList<>();
    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<String> password = new ArrayList<>();

    private boolean hasgeneral;
    //colori
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    //colori

    Server(int port) throws FileNotFoundException {
        this.serverport = port;
        hasgeneral = false;
        GenerateTopic();
        GenerateDatabase();
    }
    public void GetTopic(){
        System.out.println("##Topic##");
        for( String s : topics){
            if(s.equalsIgnoreCase(generalTopic)){
                System.out.print(ANSI_RED+" (generale)"+ANSI_RESET);
            }else{
                System.out.println(s);
            }

        }
    }

    public void Getdatabase() {
        for (int i = 0; i < database.size()-1; i++) {
            for (int j = 0; j < database.get(i).size(); j++) {
                System.out.print(database.get(i).get(j) + " ");
                System.out.print(database.get(i+1).get(j) + " ");
                System.out.println("");
            }
        }

    }

    public ArrayList<ArrayList<String>> getDatabase() {
        return database;
    }

    private void GenerateTopic() throws FileNotFoundException {
        File f = new File("Server-setting");
        if(!f.exists()){
            System.out.println("Impostazione del sever in creazione...");
            PrintWriter out = new PrintWriter(f);
            out.println("Topic:");
            out.println("# Generale");
            topics.add("Generale");
            out.close();
        }else{
            topics.clear();
            FileReader r = new FileReader(f);
            Scanner reader = new Scanner(r);
            while(reader.hasNextLine()){
                if(reader.nextLine().equals("Topic:")){
                    while(reader.hasNext("#")){
                        String s = reader.nextLine();
                        s = s.substring(2);
                        if(s.contains("@")){
                            s = s.substring(0,s.length()-2);
                            generalTopic = s;
                            hasgeneral = true;
                        }
                        topics.add(s);
                    }
                    if(!hasgeneral){
                        generalTopic = topics.get(0);
                        hasgeneral = true;
                    }
                }
            }
            reader.close();
        }
    }

    private void GenerateDatabase() throws  FileNotFoundException{
        File f = new File("Server-Database");
        if(!f.exists()){
            System.out.println("Creazione del databse in corso...");
            PrintWriter out = new PrintWriter(f);
            out.write("guest guest");
            out.close();
        }else{
            FileReader r = new FileReader(f);
            Scanner reader = new Scanner(r);
            while(reader.hasNextLine()){
                int i = 0;
                name.add(i,reader.next());
                password.add(i,reader.next().trim());
                i++;
            }
            database.add(name);
            database.add(password);
        }
    }

    public ArrayList<ServerWorker> getWorkerList(){
        return workers;
    }

    public ArrayList<String> getTopics() {
        return topics;
    }

    public void removeWorker(ServerWorker worker){
        workers.remove(worker);
    }

    @Override
    public void run() {
        try{
            ServerSocket serverSocket = new ServerSocket(serverport);
            while (true){
                System.out.println("In attesa di accettare connessioni...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("connessione accettate da: "+ clientSocket);
                ServerWorker worker = new ServerWorker(this,clientSocket);
                workers.add(worker);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
