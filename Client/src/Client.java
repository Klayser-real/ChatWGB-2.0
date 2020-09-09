import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
public class Client extends WindowAdapter {
    JFrame frame = new JFrame("WeGetBoosted");
    final JTextPane textwrite = new JTextPane();
    JScrollPane textwritesp = new JScrollPane(textwrite);
    JTextPane message = new JTextPane();
    final JTextPane textroom = new JTextPane();
    JScrollPane textroomsp = new JScrollPane(textroom);
    final JTextPane textinput = new JTextPane();
    JScrollPane textinputsp = new JScrollPane(textinput);
    //colori esadecimale
    String arancione_p = "#f1935c";
    String verdelime_p = "#81b214";
    String neroc_p = "#1b262c";
    //colori esadecimale
    final JTextField host = new JTextField("Host");
    final JTextField port = new JTextField("Porta");
    final JButton connect = new JButton("Connettiti");
    final JTextField name = new JTextField("Username");
    final JTextField pass = new JTextField("Password");
    final JButton login = new JButton("Login");
    final JButton send = new JButton("Manda");
    boolean loggato= false;
    private String ServerName;
    private int Port;
    Socket server;
    BufferedReader input;
    PrintWriter output;
    Read read;
    public Client(){
        Start();
    }
    public static void main(String[] args) {
        Client c = new Client();
    }
    public void appendToPane(JTextPane tp, String msg, Color c) throws BadLocationException {
        if(tp != null) {
            StyleContext sc = StyleContext.getDefaultStyleContext();
            AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
            System.out.println("pane: " + tp.getName());
            StyledDocument document = (StyledDocument) tp.getDocument();
            document.insertString(document.getLength(), msg, aset);
        }else{
            System.err.println("Pane non trovato");
        }
    }
    class Read extends Thread{
        JFrame f;
        Boolean exit = false;
        public void setExit(Boolean exit) {
            this.exit = exit;
        }
        public Boolean getExit() {
            return exit;
        }
        @Override
        public void run() {
            exit = false;
            String message;
            while(!Thread.currentThread().isInterrupted() && !exit){
                try{
                    message = input.readLine();
                    System.out.println(message);
                    if(message != null && !message.isEmpty()){
                        if(message.charAt(0) == '['){
                            message = message.substring(1);
                            message = " -"+ message;
                            message += "\n\r";
                            appendToPane(textroom,message,Color.decode("#ea5455"));
                        }else if(message.charAt(0) == '!'){
                            message = message.substring(1);

                            System.out.println(message);
                            String topic = message+": \n\r";
                            appendToPane(textroom,topic,Color.decode("#4e89ae"));
                        }else if(message.charAt(0)=='@' && message.charAt(1)== '@'){
                            textroom.setText(null);
                            appendToPane(textroom,"#stanze#\r\n",Color.decode(arancione_p));
                        } else{
                            System.out.println(message.length());
                            if(message.charAt(message.length()-2) == '@'){
                                if(message.charAt(message.length()-1) == '0'){
                                    message = message.substring(0,message.length()-2);
                                    String s = message;
                                    if(message.charAt(message.length()-1 )== 'y') {
                                        s = s.substring(0,s.length()-1);
                                        s = s + "\n\r";
                                    }
                                    appendToPane(textwrite,s,Color.decode(verdelime_p));
                                }else if(message.charAt(message.length()-1) == '1'){
                                    message = message.substring(0,message.length()-2);
                                    String s = message;
                                    if(message.charAt(message.length()-1 )== 'y') {
                                        s = s + "\n\r";
                                    }
                                    appendToPane(textwrite,s,Color.RED);
                                }else if(message.charAt(message.length()-1) == '2'){
                                    message = message.substring(0,message.length()-2);
                                    String s = message;
                                    if(message.charAt(message.length()-1 )== 'y') {
                                        s = s + "\n\r";
                                    }
                                    appendToPane(textwrite,s,Color.GRAY);
                                }else if(message.charAt(message.length()-1) == '3'){
                                    message = message.substring(0,message.length()-2);
                                    String s = message;
                                    if(message.charAt(message.length()-1 )== 'y') {
                                        s = s + "\n\r";
                                    }
                                    appendToPane(textwrite,s,Color.decode(neroc_p));
                                }else if(message.charAt(message.length()-1) == '4'){
                                    message = message.substring(0,message.length()-2);
                                    String s = message;
                                    if(message.charAt(message.length()-1 )== 'y') {
                                        s = s + "\n\r";
                                    }
                                    appendToPane(textwrite,s,Color.decode("#00b7c2"));
                                }
                            }else {
                                String s = message + "\n\r";
                                System.out.println(message);
                                appendToPane(textwrite, s, Color.GRAY);
                            }
                        }
                    }
                } catch (IOException | BadLocationException e) {
                    try {
                        HomeScreen();
                    } catch (IOException | BadLocationException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
            System.out.println("fuori");
        }
    }
    public void Start(){
        frame.setSize(1000,900);
        frame.setResizable(false);
        frame.setLayout(null);
        host.setBounds(225,350,250,50);
        host.setMargin(new Insets(6,6,6,6));
        host.setForeground(Color.gray);
        host.setVisible(true);
        port.setBounds(525,350,250,50);
        port.setMargin(new Insets(6,6,6,6));
        port.setForeground(Color.gray);
        port.setVisible(true);
        connect.setBounds(450,600,100,35);
        port.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (port.getText().equals("Porta")) {
                    port.setText("");
                    port.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (port.getText().isEmpty()) {
                    port.setForeground(Color.GRAY);
                    port.setText("Porta");
                }
            }
        });
        host.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (host.getText().equals("Host")) {
                    host.setText("");
                    host.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (host.getText().isEmpty()) {
                    host.setForeground(Color.GRAY);
                    host.setText("Host");
                }
            }
        });
        name.setBounds(225,350,250,50);
        name.setMargin(new Insets(6,6,6,6));
        name.setForeground(Color.gray);
        name.setVisible(true);
        pass.setBounds(525,350,250,50);
        pass.setMargin(new Insets(6,6,6,6));
        pass.setForeground(Color.gray);
        pass.setVisible(true);
        login.setBounds(450,600,100,35);
        pass.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (pass.getText().equals("Password")) {
                    pass.setText("");
                    pass.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (pass.getText().isEmpty()) {
                    pass.setForeground(Color.GRAY);
                    pass.setText("Password");
                }
            }
        });
        name.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (name.getText().equals("Username")) {
                    name.setText("");
                    name.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (name.getText().isEmpty()) {
                    name.setForeground(Color.GRAY);
                    name.setText("Username");
                }
            }
        });
        textroom.setBounds(2,2,250,1000);
        textroom.setVisible(true);
        textroom.setMargin(new Insets(10,10,10,10));
        textroom.setEditable(false);
        textroom.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,true);
        textroomsp.setBounds(0,0,250,1000);
        textinput.setBounds(250,800,500,60);
        textinput.setVisible(true);
        textinput.setMargin(new Insets(10,10,10,10));
        textinputsp.setBounds(250,800,500,60);
        textinputsp.setAutoscrolls(true);
        textinputsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        textwrite.setBounds(250,0,750,800);
        textwrite.setVisible(true);
        textwrite.setMargin(new Insets(10,10,10,10));
        textwrite.setEditable(false);
        textwrite.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,true);
        textwrite.setAutoscrolls(true);
        SimpleAttributeSet aligncentrale = new SimpleAttributeSet();
        StyleConstants.setAlignment(aligncentrale, StyleConstants.ALIGN_CENTER);
        StyleConstants.setFontSize(aligncentrale,22);
        message.setBackground(new Color(0,0,0,0));
        message.setOpaque(false);
        message.setBounds(300,100,400,70);
        message.setMargin(new Insets(6,6,6,6));
        message.setEditable(false);
        message.setFont(new Font("Arial", Font.BOLD, 22));
        message.setParagraphAttributes(aligncentrale,true);
        //textwritesp.setViewportView(textwrite);
        textwritesp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        textwritesp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        //textwrite.setLineWrap(true);
        textinputsp.setViewportView(textinput);
        textwritesp.setBounds(250,0,750,800);
        send.setBounds(750,800,100,60);
        send.setMargin(new Insets(10,10,10,10));
        send.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(loggato) {
                    try {
                        output.write("/logout");
                        output.close();
                        input.close();
                        System.out.println("fatto");
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });
        frame.add(connect);
        frame.add(port);
        frame.add(host);
        frame.setVisible(true);
        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = "Connessione in corso...";
                try {
                    appendToPane(message,s,Color.GRAY);
                } catch (BadLocationException badLocationException) {
                    badLocationException.printStackTrace();
                }
                try {
                    String porta = port.getText().trim();
                    ServerName = host.getText().trim();
                    int PORT = Integer.parseInt(porta);
                    server = new Socket(ServerName, PORT);
                    input = new BufferedReader(new InputStreamReader(server.getInputStream()));
                    output = new PrintWriter(server.getOutputStream(),true);
                    message.setText(null);
                    message.setText("Connesso");
                    frame.remove(host);
                    frame.remove(port);
                    frame.remove(connect);
                    frame.add(message);
                    frame.add(name);
                    frame.add(pass);
                    frame.add(login);
                    frame.revalidate();
                    frame.repaint();
                }catch (Exception ex){
                    frame.add(message);
                    message.setText(null);
                    String su = "Connessione fallita";
                    try {
                        appendToPane(message,su,Color.decode("#ea5455"));
                    } catch (BadLocationException badLocationException) {
                        badLocationException.printStackTrace();
                    }
                    ex.printStackTrace();
                }
            }
        });
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = name.getText().trim();
                String password = pass.getText().trim();
                String s = "/login "+user+" "+password;
                output.println(s);
                try {
                    String s2 = input.readLine();
                    System.out.println(s2);
                    if (s2.equalsIgnoreCase("hai effettuato l'accesso correttamente")) {
                        loggato = true;
                        read = new Read();
                        System.out.println(read.getState());
                        frame.remove(name);
                        frame.remove(pass);
                        frame.remove(login);
                        frame.add(textinputsp);
                        frame.add(textroomsp);
                        frame.add(textwritesp);
                        frame.add(send);
                        frame.revalidate();
                        frame.repaint();
                        read.start();
                    } else {
                        String su = "Login fallito";
                        message.setText(null);
                        appendToPane(message,su,Color.decode("#ea5455"));
                    }
                } catch (IOException | BadLocationException ioException) {
                    System.err.println("errore, torno home");
                    try {
                        HomeScreen();
                    } catch (IOException | BadLocationException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    String message = textinput.getText().trim();
                    if(message.equals("")){
                        return;
                    }else{
                        output.println(message);
                        textinput.requestFocus();
                        textinput.setText(null);
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
    }
    public void HomeScreen() throws IOException, BadLocationException {

        if(loggato) {
            System.out.println("sus");
            frame.remove(textroomsp);
            frame.remove(textwritesp);
            frame.remove(textinput);
            frame.remove(textinputsp);
            textwrite.setText(null);
            frame.remove(send);
            frame.add(host);
            frame.add(port);
            frame.add(connect);
            server.close();
            output.close();
            input.close();
            frame.revalidate();
            frame.repaint();
            read.setExit(true);
            System.out.println(read.getState());
            loggato = false;
        }else{
            frame.remove(name);
            frame.remove(pass);
            frame.remove(login);
            frame.add(host);
            frame.add(port);
            frame.add(connect);
            frame.revalidate();
            frame.repaint();
        }
        frame.add(message);
        message.setText(null);
        String s = "Disconnesso";
        appendToPane(message,s,Color.decode("#ea5455"));
    }
}
