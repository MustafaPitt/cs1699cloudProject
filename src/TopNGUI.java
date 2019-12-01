import com.jcraft.jsch.*;
import javax.swing.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TopNGUI {


    public TopNGUI(Session session, int n) {
        if(!session.isConnected()){
            JOptionPane.showMessageDialog(null,"You are not connected.");
            return;
        }
        constructDataTableFromChannel(n);
    }

    private void constructDataTableFromChannel( int  n) {
        List<String> cmds = new ArrayList<>();

        //Measure execution time for this method

        Instant start = Instant.now();

        cmds.add("cd cloudProject");
        cmds.add("java SecondarySortTopN "+ n);
        List <String>result  =  SSL.executeCommands(cmds);


        String resultStr = extractString(result);

        String [][]dataset = convertListTo2dArray(resultStr);

        System.err.println(Arrays.deepToString(dataset));

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();  //in millis
        JOptionPane.showMessageDialog(null, "Execution time  in in millis" + timeElapsed);

        createUIComponents(dataset,"Top N " + n + " Execution time  in in millis " + timeElapsed);
    }


    private static String extractString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        boolean foundMark = false;
        for (String s : list ) {
            for (int i = 0; i < s.length(); i++) {


                if (foundMark == false) {
                    if(s.charAt(i) == '<') {
                        foundMark = true;
                        continue;
                    }
                    else if (s.charAt(i) != '<') continue;
                }

                if (s.charAt(i) == '>')break;
                if (s.charAt(i) != '>' && i <  s.length() - 1 )
                    sb.append(s.charAt(i));
            }
        }
        return sb.toString();
    }


    private static String[][] convertListTo2dArray(String str) {


        StringBuilder temp = new StringBuilder();
        int indx =0;
        String [] splits = str.split("\\s|\n|,");
        for (String s : splits){
            if (s.length() > 0) {
                temp.append(s);
            temp.append(" ");
            }
        }
        splits =  temp.toString().split("\\s|\n|,");
        int col = 2;
        int row = splits.length/col;
        String data [][] = new String[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (splits[indx].length() > 0 ){
                    data[i][j] = splits[indx++];
                }else {
                    while (splits[indx].length() == 0 )indx++;
                    data[i][j] = splits[indx++];
                }

            }
        }
        return  data;
    }



    private  void createUIComponents(String [][]data, String term) {
        // TODO: place custom component creation code here
        JFrame myFrame = new JFrame();
        myFrame.setTitle(" Top-N Frequent Terms : " + term);
        myFrame.setSize(600,650);
        myFrame.setVisible(true);
        myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        String[] columns = {"Term","Total Frequencies"};
        JTable table1 = new JTable(data, columns);
        JScrollPane jpane = new JScrollPane(table1);
        myFrame.add(jpane);
    }

}
