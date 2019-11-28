import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

public class SearchForTerm {


    public static void main(String[] args) {


        //Read object from file
        HashMap<String, String[][]> hashMap = (HashMap) ReadObjectFromFile("invertedIndexesHmObject");
        assert hashMap != null;

        String [][] toReturn = hashMap.get(args[0]);
        if(toReturn == null) System.out.println("null");
        System.out.println("*"+Arrays.deepToString(toReturn)+"*");


    }

    public static Object ReadObjectFromFile(String filepath) {

        try {

            FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            Object obj = objectIn.readObject();

            System.out.println("The Object has been read from the file");
            objectIn.close();
            return obj;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
