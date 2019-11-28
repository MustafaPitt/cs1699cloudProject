public class Temp {

    public static void main(String[] args) {
        String sss = "index 1= The Object has been read from the file\n" +
                "*[[hamlet, tragedies, 2, 207], [rapeoflucrece, poetry, 2, 9], [timonofathens, tragedies, 2, 1], [muchadoaboutnothing, comedies, 2, 1], [war_and_peace.txt, Tolstoy, 2, 58], [coriolanus, tragedies, 2, 1], [asyoulikeit, comedies, 2, 1], [macbeth, tragedies, 2, 38], [tamingoftheshrew, comedies, 2, 1], [kingjohn, histories, 2, 247], [1kinghenryvi, histories, 2, 139], [merrywivesofwindsor, comedies, 2, 3], [1kinghenryiv, histories, 2, 137], [merchantofvenice, comedies, 2, 3], [winterstale, comedies, 2, 60], [2kinghenryvi, histories, 2, 246], [kinghenryviii, histories, 2, 255], [twogentlemenofverona, comedies, 2, 3], [cymbeline, comedies, 2, 41], [antonyandcleopatra, tragedies, 2, 8], [twelfthnight, comedies, 2, 3], [venusandadonis, poetry, 2, 2], [juliuscaesar, tragedies, 2, 4], [troilusandcressida, comedies, 2, 7], [periclesprinceoftyre, comedies, 2, 51], [kinghenryv, histories, 2, 329], [sonnets, poetry, 2, 2], [kingrichardiii, histories, 2, 306], [measureforemeasure, comed\n" +
                "index 2= ies, 2, 6], [kingrichardii, histories, 2, 282], [Miserables.txt, Hugo, 2, 124], [2kinghenryiv, histories, 2, 140], [various, poetry, 2, 3], [midsummersnightsdream, comedies, 2, 6], [kinglear, tragedies, 2, 311], [titusandronicus, tragedies, 2, 12], [NotreDame_De_Paris.txt, Hugo, 2, 300], [3kinghenryvi, histories, 2, 403], [allswellthatendswell, comedies, 2, 136], [othello, tragedies, 2, 1], [anna_karenhina.txt, Tolstoy, 2, 7], [romeoandjuliet, tragedies, 2, 2], [tempest, comedies, 2, 41], [loveslabourslost, comedies, 2, 40]]*\n" +
                "(4) ric-edge-01 $ \n" +
                "(4) ric-edge-01 $ exit\n" +
                "logout\n";

        StringBuilder sb = new StringBuilder(sss.length());
        for(int i = 0 ; i < sss.length() ; i++){
            // find the *
            if (sss.charAt(i) != '*')continue;
            i++;
            while (sss.charAt(i)!='*') sb.append(sss.charAt(i++));
        }
        System.out.println(sb.toString());

        String s2 = "Miserables.txt,Hugo";
        int i = 0;
        while (s2.charAt(i++)!= ',' && i < s2.length());
        System.out.println(s2.substring(i));


    }
}
