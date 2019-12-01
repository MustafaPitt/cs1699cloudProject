import com.jcraft.jsch.*;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class MainGUI {
    private static JScrollPane scrollPane;
    private JPanel panel1;
    private JButton connectBtn;
    private JButton searchBtn;
    private JLabel connectLabel;
    private JButton runHadoopBtn;
    private JButton constInvertedBtn;
    private JButton topNbtn;
    private JButton getTopN;
    private JButton uploadBtn;

    //    private Channel channel; // use it to communicate with cluster via channel.inputStream(), channel.outputStream()
    private Session session;

    public MainGUI() {
        connectBtn.addActionListener(e -> {

            session = SSL.connect();
            //channel.connectBtn();
            try {
                session.connect(6000);
            } catch (JSchException e1) {
                e1.printStackTrace();
            }
            if(session.isConnected()) {
                JOptionPane.showMessageDialog(null, "connected successfully to your cluster");
                connectBtn.setEnabled(false);
                searchBtn.setEnabled(true);
//                searchBtn.setVisible(true);
                connectLabel.setVisible(true);
                constInvertedBtn.setEnabled(true);
                runHadoopBtn.setEnabled(true);
                topNbtn.setEnabled(true);
                getTopN.setEnabled(true);
                uploadBtn.setEnabled(true);
            }
            else JOptionPane.showMessageDialog(null, "error connecting to the cluster ");


        });




        searchBtn.addActionListener(e -> {
           String  term =JOptionPane.showInputDialog("Enter Your Search Term",
                   "");
           if(term.length() > 0 ) new SearchGUI(session,term.toLowerCase());

        });
        runHadoopBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int input = JOptionPane.showConfirmDialog(null, "Are you sure you want to run hadoop inverted indices on the current cluster!");
                // 0=yes, 1=no, 2=cancel
                if(input == 0) {
                    Instant start = Instant.now();
                    runHadoopBtn.setEnabled(false);
                    topNbtn.setEnabled(false);
                    constInvertedBtn.setEnabled(false);
                    searchBtn.setEnabled(false);
                    List<String> cmds = new ArrayList<>();
                    cmds.add("cd cloudProject");
                    cmds.add("export JAVA_HOME=/usr/local/jdk1.8.0_101");
                    cmds.add("export PATH=${JAVA_HOME}/bin:${PATH}");
                    cmds.add("export HADOOP_CLASSPATH=/opt/cloudera/parcels/CDH/lib/hadoop/hadoop-common.jar:/opt/cloudera/parcels/CDH/lib/hadoop-mapreduce/hadoop-mapreduce-client-core.jar");
                    cmds.add("hadoop fs -put myData/ .");
                    cmds.add("jar cvf invertedIndex.jar -C invertedIndex/ .");
                    cmds.add("hadoop fs -rm -r output");
                    cmds.add("hadoop jar invertedIndex.jar HadoopProject  'myData/*/*' output");
                    cmds.add("hadoop fs -getmerge output collectedResults");
                    List<String> result = SSL.executeCommands(cmds);
                    Instant finish = Instant.now();

                    long timeElapsed = Duration.between(start, finish).toMillis();  //in millis
                    JOptionPane.showMessageDialog(null, "Hadoop inverted index run completed in " + timeElapsed + " Millis");
                }
                runHadoopBtn.setEnabled(true);
                topNbtn.setEnabled(true);
                constInvertedBtn.setEnabled(true);
                searchBtn.setEnabled(true);
                getTopN.setEnabled(true);
            }
        });
        constInvertedBtn.addActionListener(e -> {
            List<String> cmds = new ArrayList<>();
            cmds.add("cd cloudProject");
            cmds.add("time java BuildInvertedIndices");
            List<String> result = SSL.executeCommands(cmds);
            JOptionPane.showMessageDialog(null, result);
        });
        topNbtn.addActionListener(e -> {
            int input = JOptionPane.showConfirmDialog(null, "Are you sure you want to run hadoop top N on the current cluster!");
            // 0=yes, 1=no, 2=cancel
            if(input == 0) {
                Instant start = Instant.now();
                List<String> cmds = new ArrayList<>();
                cmds.add("cd cloudProject");
                cmds.add("export JAVA_HOME=/usr/local/jdk1.8.0_101");
                cmds.add("export PATH=${JAVA_HOME}/bin:${PATH}");
                cmds.add("export HADOOP_CLASSPATH=/opt/cloudera/parcels/CDH/lib/hadoop/hadoop-common.jar:/opt/cloudera/parcels/CDH/lib/hadoop-mapreduce/hadoop-mapreduce-client-core.jar");
                cmds.add("hadoop fs -put myData/ .");
                cmds.add("jar cvf topNWords.jar -C topNWords/ .");
                cmds.add("hadoop fs -rm -r output2");
                cmds.add("hadoop jar topNWords.jar TopTenN  'myData/*/*' output2");
                cmds.add("hadoop fs -getmerge output2 collectedResults2");
                List<String> result = SSL.executeCommands(cmds);
                Instant finish = Instant.now();
                long timeElapsed = Duration.between(start, finish).toMillis();  //in millis
                JOptionPane.showMessageDialog(null, "total time for hadoop top N + get top N is " + timeElapsed + " in Millis");
                new TopNGUI(session, 10);
            }

        });
        getTopN.addActionListener(e -> {
            String input =JOptionPane.showInputDialog("Enter Your integer N");
            try {
                new TopNGUI(session, Integer.parseInt(input));

            }catch (Exception ex){
                System.out.println(ex.getMessage());

            }
        });
        uploadBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "Sorry,This feature is not active yet");
            JOptionPane.showMessageDialog(null, "when active you can push your data to ../cloudProject/myData");

        });
    }

    public static abstract class MyUserInfo implements UserInfo, UIKeyboardInteractive {
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

    private static void createUIComponents() {
        // TODO: place custom component creation code here
        MainGUI mainGUI = new MainGUI();
        JFrame  myFrame = new JFrame();
        myFrame.add(mainGUI.panel1);
        myFrame.setSize(600,650);
        mainGUI.searchBtn.setEnabled(false);
        mainGUI.connectLabel.setVisible(false);
        mainGUI.constInvertedBtn.setEnabled(false);
        mainGUI.runHadoopBtn.setEnabled(false);
        mainGUI.topNbtn.setEnabled(false);
        mainGUI.getTopN.setEnabled(false);
        mainGUI.uploadBtn.setEnabled(false);
        myFrame.setVisible(true);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        createUIComponents();
    }
}
