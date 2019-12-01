import com.jcraft.jsch.*;

import javax.swing.*;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SSL {

    private static Session session;
    private static ChannelShell channel;


    private static Session getSession(){
        if(session == null || !session.isConnected()){
            JOptionPane.showMessageDialog(null,"you are not connected to your cluster");
        }
        return session;
    }

    private static Channel getChannel(){
        if(channel == null || !channel.isConnected()){
            try{
                channel = (ChannelShell)getSession().openChannel("shell");
                channel.connect();

            }catch(Exception e){
                System.out.println("Error while opening channel: "+ e);
            }
        }
        return channel;
    }

    public static Session connect(){

        JSch jsch=new JSch();
        //jsch.setKnownHosts("/home/foo/.ssh/known_hosts");

        String host=null;
        host= JOptionPane.showInputDialog("Enter username@cluster",
                ("mua31")+ "@ric-edge-01.sci.pitt.edu");
        String user=host.substring(0, host.indexOf('@'));
        host=host.substring(host.indexOf('@')+1);
        try {
            session=jsch.getSession(user, host, 22);
        } catch (JSchException e) {
            e.printStackTrace();
        }

        JPasswordField pf = new JPasswordField();
        int okCxl = JOptionPane.showConfirmDialog(null, pf, "Enter Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (okCxl == JOptionPane.OK_OPTION) {
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword(new String(pf.getPassword()));
        }

        UserInfo ui = new MyUserInfo(){
            public void showMessage(String message){
                JOptionPane.showMessageDialog(null, message);
            }

            public boolean promptYesNo(String message){
                Object[] options={ "yes", "no" };
                int foo=JOptionPane.showOptionDialog(null,
                        message,
                        "Warning",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null, options, options[0]);
                return foo==0;
            }

        };

        return session;

    }
    public static abstract class MyUserInfo implements UserInfo, UIKeyboardInteractive{
        public String getPassword(){ return null; }
        public boolean promptYesNo(String str){ return false; }
        public String getPassphrase(){ return null; }
        public boolean promptPassphrase(String message){ return false; }
        public boolean promptPassword(String message){ return false; }
        public void showMessage(String message){ }
        public String[] promptKeyboardInteractive(String destination,
                                                  String name,
                                                  String instruction,
                                                  String[] prompt,
                                                  boolean[] echo){
            return null;
        }
    }

    public static List executeCommands(List<String> commands){
        List<String> toReturn = null;
        try{
            Channel channel=getChannel();

            System.out.println("Sending commands...");
            sendCommands(channel, commands);

             toReturn = listChannelOutput(channel);
            System.out.println("Finished sending commands!");

        }catch(Exception e){
            System.out.println("An error ocurred during executeCommands: "+e);
        }
        return toReturn;
    }

    private static void sendCommands(Channel channel, List<String> commands){

        try{
            PrintStream out = new PrintStream(channel.getOutputStream());

            out.println("#!/bin/bash");
            for(String command : commands){
                out.println(command);
            }
            out.println("exit");

            out.flush();
        }catch(Exception e){
            System.out.println("Error while sending commands: "+ e);
        }

    }

    private static List<String> listChannelOutput(Channel channel){
        List<String>lines = new ArrayList<>();

        byte[] buffer = new byte[1024];

        try{
            InputStream in = channel.getInputStream();
            String line = "";
            while (true){
                while (in.available() > 0) {
                    int i = in.read(buffer, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    line = new String(buffer, 0, i);
                    System.out.println(line);
                    lines.add(line);
                }

                if(line.contains("logout")){
                    break;
                }

                if (channel.isClosed()){
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ignored){}
            }
        }catch(Exception e){
            System.out.println("Error while reading channel output: "+ e);
        }

        return lines;
    }

    public static void close(){
        channel.disconnect();
        session.disconnect();
        System.out.println("Disconnected channel and session");
    }


    public static void main(String[] args){
        List<String> commands = new ArrayList<String>();
        commands.add("ls -l");

        executeCommands(commands);
        close();
    }
}