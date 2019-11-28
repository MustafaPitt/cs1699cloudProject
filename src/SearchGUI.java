import com.jcraft.jsch.*;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;
import static java.util.logging.Logger.getLogger;

public class SearchGUI {


    public SearchGUI(Session session, String term) {
        if(!session.isConnected()){
            JOptionPane.showMessageDialog(null,"You are not connected.");
            return;
        }
        constructDataTableFromChannel(session,term);
    }

    private void constructDataTableFromChannel(Session session, String term) {
        List<String> cmds = new ArrayList<>();

        cmds.add("cd cloudProject");
        cmds.add("java SearchForTerm "+term);
        List <String>result  =  SSL.executeCommands(cmds);
        String extractedListInString = extraxtStringList(result.get(1));

        String [] splits = extractedListInString.split("\\{|}|\\s+|,|=|:|]|\\[");
        List<String> temp = new ArrayList<>();
        for (int i = 1; i<splits.length ;i++){
            if(splits[i].length() == 0 )continue;
            temp.add(splits[i]);
        }
        String [][]datatest = convertListTo2dArray(temp);
        createUIComponents(datatest,term);
    }

    private String extraxtStringList(String s) {
        System.err.println("debug"+ s);
        StringBuilder sb = new StringBuilder(s.length());
        for(int i = 0 ; i < s.length() ; i++){
            // find the *
            if (s.charAt(i) != '*')continue;
            i++;
            while (s.charAt(i) != '*' && i < s.length()-1) sb.append(s.charAt(i++));
        }
        return sb.toString();
    }


    private static String[][] convertListTo2dArray(List<String> list) {

        int col = 4;
        int row = list.size()/col;
        String data [][] = new String[row][col];
        System.err.println(list);
        int indx =0;
        for (int i = 0; i < row; i++) {
            for (int j = 1; j < col; j++) {
                data[i][j] = list.get(indx++);
            }
        }
        int i = 0 , v =1;
        while (i< data.length)data[i++][0]= Integer.toString(v++);
        return  data;
    }



    private  void createUIComponents(String [][]data, String term) {
        // TODO: place custom component creation code here
        JFrame myFrame = new JFrame();
        myFrame.setTitle("Search term : " + term);
        myFrame.setSize(600,650);
        myFrame.setVisible(true);
        myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        String[] columns = {"Doc ID", "Doc Name","Doc Folder","Frequencies"};
        JTable table1 = new JTable(data, columns);
        JScrollPane jpane = new JScrollPane(table1);
        myFrame.add(jpane);
    }

}
