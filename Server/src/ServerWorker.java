import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.IIOException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ServerWorker extends Thread {

    private final Socket clientSocket;
    private int maxtopic;
    private final Server server;
    private String login = null;
    private OutputStream outputStream;
    private ArrayList<String> topicset = new ArrayList<>();
    private ArrayList<String> topic = new ArrayList<>();
    private ArrayList<ArrayList<String>> database = new ArrayList<>();
    private int passwordn;
    private boolean loggato;

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

    public ServerWorker(Server server, Socket Socketclient){
        this.server = server;
        this.clientSocket = Socketclient;
        topicset = server.getTopics();
        maxtopic = 1;
        topic.add(server.generalTopic);
        database = server.getDatabase();
        loggato = false;
    }



    @Override
    public void run() {
        try{
            HandleClientSocket();
        }catch (IOException e){
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private void HandleClientSocket() throws IOException,InterruptedException{
        InputStream inputStream = clientSocket.getInputStream();
        this.outputStream = clientSocket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line = reader.readLine())!= null){
            String[] tokens = StringUtils.split(line);
            if(tokens != null && tokens.length>0){
                String cmd = tokens[0];
                if(cmd.charAt(0) == '/'){
                    if(cmd.substring(1)!= null){
                    cmd = cmd.substring(1);}
                    if(("logout".equalsIgnoreCase(cmd) || "quit".equalsIgnoreCase(cmd)) && loggato){
                        HandleLogout();
                    }else if("login".equalsIgnoreCase(cmd)){
                        HandleLogin(outputStream,tokens);
                    }else if("join".equalsIgnoreCase(cmd)&& loggato){
                        HandleJoin(tokens);
                    }else if("leave".equalsIgnoreCase(cmd)&& loggato){
                        HandleLeave(tokens);
                    }else if("topic".equalsIgnoreCase(cmd)&& loggato){
                        HandleTopic();
                    }else if("mytopic".equalsIgnoreCase(cmd)&& loggato){
                        HandleMyTopic();
                    } else if(loggato){
                        String msg = cmd + " << Comando non riconosciuto\n\r";
                        outputStream.write(msg.getBytes());
                    }else{
                        String msg2 = "Per eseguire i comandi devi prima essere loggato.\n\r";
                        outputStream.write(msg2.getBytes());
                    }
                }else if(cmd.charAt(0) == '@'){
                    line = line.substring(1);
                    String[] tokensmsg = StringUtils.split(line,null,2);
                    HandleMessagePrivate(tokensmsg);
                }else if(loggato){
                    String tokensmsg = line;
                    HandleMessage(tokensmsg);
                }
            }
        }
        clientSocket.close();
    }

    private void HandleMyTopic() throws IOException {
        outputStream.write("##MyTopic##\n\r".getBytes());
        for (String s:topic) {

            s += "\n\r";
            outputStream.write(s.getBytes());

        }
    }

    public String GetTopic(int n){
        return topic.get(n);
    }

    private void HandleTopic() throws IOException {
        outputStream.write("@@\n\r".getBytes());
        for( String s : topicset){
            String sus = "!"+s+"\n\r";
            outputStream.write(sus.getBytes());
            List<ServerWorker> workers = server.getWorkerList();
            for(ServerWorker worker:workers){
                if(worker.isMemberOfTopic(s)){
                    String r = "["+worker.getlogin()+"\n\r";
                    outputStream.write(r.getBytes());
                }
            }
        }
    }


    private void checktopic(String s) throws IOException {
        System.out.println(s);
        List<ServerWorker> workers = server.getWorkerList();
        for(ServerWorker worker:workers){
            if(worker.topic.contains(s)){
                String n = " -"+ANSI_CYAN + worker.getlogin() + ANSI_RESET+"\n\r";
                outputStream.write(n.getBytes());
            }
        }
    }

    private void HandleMessagePrivate(String[] tokensmsg) throws IOException {
        if(tokensmsg.length == 2) {
            String sendto = tokensmsg[0];
            String msg = tokensmsg[1];
            List<ServerWorker> workerList = server.getWorkerList();
            for (ServerWorker worker : workerList) {
                if (sendto.equalsIgnoreCase(worker.getlogin())) {
                    String msme =ANSI_RED+ "(privato) "+ ANSI_GREEN+"Io"+ANSI_RESET +" >> "+ worker.getlogin() + " >> "+ msg+"\n\r";
                    msg = ANSI_RED+ "(privato) "+ ANSI_GREEN+ login +ANSI_RESET+ " >> " + " " + msg+"\n\r";
                    outputStream.write(msme.getBytes());
                    worker.send(msg);
                }
            }
        }
    }

    private void HandleMessage(String tokensmsg) throws IOException {
        String msg = tokensmsg;
        List<ServerWorker> workerList = server.getWorkerList();
        for(ServerWorker worker: workerList){
            if(worker.isMemberOfTopic(this.GetTopic(0))){
                String outmsg = login+" >> "+ msg+"\n\r";
                worker.send(outmsg);
            }
        }
    }

    private void send(String outmsg) throws IOException {
        if(login != null){
            outputStream.write(outmsg.getBytes());
        }
    }

    private void HandleLeave(String[] tokens) {
        if(tokens.length>1){
            if(topic.contains(tokens[1])){
                topic.remove(tokens[1]);
            }
        }
    }

    public boolean isMemberOfTopic(String topics){return topic.contains(topics);}

    public void Flush() throws IOException {
        outputStream.flush();
    }

    private void HandleJoin(String[] tokens) throws IOException {
        if(tokens.length>1){
            if(topicset.contains(tokens[1]) && !topic.contains(tokens[1])){
                topic.clear();
                topic.add(tokens[1]);
                HandleTopic();
                String s = "Stai scrivendo in " + tokens[1] + "\n\r";
                outputStream.write(s.getBytes());
                List<ServerWorker> workerlist = server.getWorkerList();
                for (ServerWorker worker: workerlist) {
                    if(worker.getlogin() != login){
                        if(worker.getlogin()!= null){
                            worker.HandleTopic();
                        }
                    }
                }
            }else{
                outputStream.write("il topic potrebbe non esistere, oppure fai già parte del topic".getBytes());
            }
        }
    }

    private void HandleLogin(OutputStream outputStream, String[] tokens) throws IOException, InterruptedException {
        if(tokens.length==3){
            String login = tokens[1];
            String pass = tokens[2];
            if(Check(login,pass)){
                loggato = true;
                String msg = "Hai effettuato l'accesso correttamente\n\r";
                outputStream.write(msg.getBytes());
                this.login = login;
                EnterScreen();
                System.out.println(login + " loggato con successo");
                List<ServerWorker> workerlist = server.getWorkerList();
                for (ServerWorker worker: workerlist) {
                        if(worker.getlogin()!= null){
                            worker.HandleTopic();
                        }
                }

            }else{
                String msg = "Il login non e' andato a buon fine, riprova.\r\n";
                outputStream.write(msg.getBytes());
                System.err.println(login+" ha fallito il login.");
            }
        }else{
            String msg = "Sintassi del login errato: /login <Username> <password>";
        }

    }

    public void EnterScreen() throws IOException {
        //clearScreen();
        outputStream.write("######################################\n".getBytes());
        outputStream.write(" ".getBytes());
        String s = "Benvenuto " + login + " Su WeGetBoosted Chat\n";
        outputStream.write(s.getBytes());
        outputStream.write("######################################\n".getBytes());
    }

    private String getlogin() {
        return login;
    }

    public boolean Check(String login, String pass){
        List<ServerWorker> workers = server.getWorkerList();
        for(ServerWorker worker:workers){
            if(login.equalsIgnoreCase(worker.getlogin())){
                return false;
            }
        }
        for (int i = 0; i < database.size()-1; i++) {
            for (int j = 0; j < database.get(i).size(); j++) {
                if(login.equals(database.get(i).get(j)) &&  pass.equals(database.get(i+1).get(j))){
                    passwordn = j;
                    return true;
                }
            }
        }
        return false;
    }

    private void HandleLogout() throws IOException {
        server.removeWorker(this);
        String onlinemsg = ANSI_CYAN+login+ANSI_RESET+" e' ora offline\n\r";
        System.out.println(login + "e' offline");
        List<ServerWorker> workers = server.getWorkerList();
        for(ServerWorker worker : workers){
            worker.HandleTopic();
            worker.send(onlinemsg);
        }
        clientSocket.close();
    }

    public void clearScreen() throws IOException {
        outputStream.write("\033[H\033[2J".getBytes());
        outputStream.flush();
    }
}
