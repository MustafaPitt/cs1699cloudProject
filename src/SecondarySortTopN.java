import java.io.*;
import java.util.*;


/***
 *
 * this is secondary sorting application. It will read the output of TopNCount.java and then construct tree map
 * <integer count, term > by defult it will sort by key
 * it accept one arg integer N and return top N terms
 * this class is running on the cluster
 *
 * */
public class SecondarySortTopN {


    public static void main(String[] args) {
        SortedMap<Integer,String> topNHashmap = new TreeMap<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    "collectedResults2"));
            String line = reader.readLine();
            while (line != null) {
                // read next line
                line = reader.readLine();
                if(line == null)continue;
//                System.out.println(line);
                String [] splits = line.split("\\{|}|\\s+|,|=|:");
                if(splits.length == 0)continue;
                for (int i = 0; i<splits.length ;i++){
                    if(splits[i].length() == 0 )continue;
                   topNHashmap.put(Integer.valueOf(splits[1]), splits[0]);
                }
            }
//            writeObjectToFile(topNHashmap);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int arg2 = Integer.parseInt(args[0]);
        int count =0;
        System.out.println("<"); // mark point for the top n array so I can slice it in Top N GUI
        for(Integer key : topNHashmap.keySet()){
            if( count >= topNHashmap.size() - arg2   ){
                System.out.println(topNHashmap.get(key) + " " +key);
            }
            count++;
        }
        System.out.println(">"); // mark point for the top n array so I can slice it in Top N GUI

    }

    private static void writeObjectToFile(Object serObj) {

        try {

            FileOutputStream fileOut = new FileOutputStream("HashMapTree");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(serObj);
            objectOut.close();
            System.out.println("The Object  was successfully written to a file");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
