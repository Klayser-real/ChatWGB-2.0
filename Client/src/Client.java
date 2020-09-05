import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;

public class Client extends WindowAdapter {
    final JTextArea textwrite = new JTextArea();
    final JTextArea textroom = new JTextArea();
    final JTextPane textinput = new JTextPane();
    private String ServerName;
    private int Port;
    Socket server;
    BufferedReader input;
    PrintWriter output;



    public Client(){
        JFrame frame = new JFrame("WeGetBoosted");
        frame.setSize(1000,900);
        frame.setResizable(false);
        frame.setLayout(null);
        final JTextField host = new JTextField("Host");
        final JTextField port = new JTextField("Porta");
        final JButton connect = new JButton("Connettiti");

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

        final JTextField name = new JTextField("Username");
        final JTextField pass = new JTextField("Password");
        final JButton login = new JButton("Login");

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
        JScrollPane textroomsp = new JScrollPane(textroom);
        textroom.append("#Stanze#\n\r");
        textroomsp.setBounds(0,0,250,1000);

        textinput.setBounds(250,800,500,100);
        textinput.setVisible(true);
        textinput.setMargin(new Insets(6,6,6,6));
        JScrollPane textinputsp = new JScrollPane(textinput);
        textinputsp.setBounds(250,800,700,100);

        textwrite.setBounds(250,0,750,800);
        textwrite.setVisible(true);
        textwrite.setMargin(new Insets(6,6,6,6));
        textwrite.setEditable(false);
        textwrite.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,true);
        textwrite.setAutoscrolls(true);
        textwrite.setLineWrap(true);
        JScrollPane textwritesp = new JScrollPane(textwrite);
        textwritesp.setBounds(250,0,750,800);

        JButton send = new JButton("Manda");
        send.setBounds(850,800,100,100);
        send.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {

            @Override

            public void windowClosing(WindowEvent e) {
                try {
                    output.write("/logout");
                    output.close();
                    input.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                    System.out.println("fatto");
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
                try {
                    String porta = port.getText().trim();
                    ServerName = host.getText().trim();
                    int PORT = Integer.parseInt(porta);
                    server = new Socket(ServerName, PORT);
                    input = new BufferedReader(new InputStreamReader(server.getInputStream()));
                    output = new PrintWriter(server.getOutputStream(),true);
                    frame.remove(host);
                    frame.remove(port);
                    frame.remove(connect);
                    frame.add(name);
                    frame.add(pass);
                    frame.add(login);
                    frame.revalidate();
                    frame.repaint();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = name.getText();
                String password = pass.getText();
                String s = "/login "+user+" "+password;
                output.println(s);
                try {
                    String s2 = input.readLine();
                    System.out.println(s2);
                    if(s2.equalsIgnoreCase("hai effettuato l'accesso correttamente")){
                        Read read = new Read();
                        read.start();
                        frame.remove(name);
                        frame.remove(pass);
                        frame.remove(login);
                        frame.add(textinput);
                        frame.add(textroomsp);
                        frame.add(textwritesp);
                        frame.add(send);
                        frame.revalidate();
                        frame.repaint();
                    }
                } catch (IOException ioException) {
                    System.err.println("errore");;
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



    public static void main(String[] args) {
        Client c = new Client();
    }
    class Read extends Thread{
        @Override
        public void run() {
            String message;
            while(!Thread.currentThread().isInterrupted()){
                try{
                    message = input.readLine();
                    System.out.println(message);
                    if(message != null && !message.isEmpty()){
                        if(message.charAt(0) == '['){
                            message = message.substring(1);
                                textroom.append(" -"+message+"\n\r");
                        }else if(message.charAt(0) == '!'){
                            message = message.substring(1);

                            System.out.println(message);
                            textroom.append(message+": \n\r");
                        }else if(message.charAt(0)=='@' && message.charAt(1)== '@'){
                            textroom.setText(null);
                        } else{
                            textwrite.append(message+"\n\r");
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }





        }
    }

    private void AppendToPane(JTextPane textroom, String s) {
        HTMLDocument doc = (HTMLDocument)textroom.getDocument();
        HTMLEditorKit editorKit = (HTMLEditorKit)textroom.getEditorKit();
        try{
            editorKit.insertHTML(doc,doc.getLength(),s,0,0,null);
            textroom.setCaretPosition(doc.getLength());
        } catch (IOException | BadLocationException e) {
            e.printStackTrace();
        }
    }


}
