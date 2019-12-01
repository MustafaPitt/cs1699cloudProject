import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Temp {


    private static String[][] convertListTo2dArray(String str) {

        int indx =0;
        String [] splits = str.split("\\s|\n|,");
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

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();

        String s1 ="his 27614\n" +
                "he 28826\n" +
                "that 31623\n" +
                "I 37264\n" +
                "in 38561\n" +
                "a 47839\n" +
                "to 61824\n" +
                "of 67088\n" +
                "and 71518\n" +
                "the 124402";


        String [][] td = convertListTo2dArray(s1);
        System.out.println(Arrays.deepToString(td));

    }
}
