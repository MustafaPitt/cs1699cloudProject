import java.io.*;
import java.util.*;

public class BuildInvertedIndices {


    public static void main(String[] args) {
        HashMap<String, String[][]> invertedIndexesHM = new HashMap<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    "collectedResults"));
            String line = reader.readLine();
            List<String> list = new ArrayList<>();
            while (line != null) {
                // read next line
                line = reader.readLine();
                if(line == null)continue;
//                System.out.println(line);
                String [] splits = line.split("\\{|}|\\s+|,|=|:");
                if(splits.length == 0)continue;

                for (int i = 1; i<splits.length ;i++){
                    if(splits[i].length() == 0 )continue;
                    list.add(splits[i]);
                }
                String [][]datatest = convertListTo2dArray(list);
                invertedIndexesHM.put(splits[0],datatest);
                list.clear();
            }

            writeObjectToFile(invertedIndexesHM);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeObjectToFile(Object serObj) {

        try {

            FileOutputStream fileOut = new FileOutputStream("invertedIndexesHmObject");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(serObj);
            objectOut.close();
            System.out.println("The Object  was successfully written to a file");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String[][] convertListTo2dArray(List<String> list) {

        int col = 4;
        int row = list.size()/col;
        String data [][] = new String[row][col];

            int indx =0;
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    data[i][j] = list.get(indx++);
                }
            }
        return  data;
    }
}
