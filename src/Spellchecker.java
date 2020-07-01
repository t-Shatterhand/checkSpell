import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Spellchecker {

    char[] alphabet = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
    private final InputOutput IO;
    private final HashMap<String, Integer> dictionary;
    private int corrections, totalWords;

    Spellchecker(HashMap<String, Integer> dict){
        IO = new InputOutput();
        dictionary = dict;
    }

    public void correctSpelling(String filePath){
        String line, lineToCorrect, correctedName, textToCorrect = "";
        try {
            if (filePath.endsWith(".txt")  || filePath.endsWith(".err") || filePath.endsWith(".tex")){
                correctedName = filePath.substring(0, filePath.length() - 4) + "_corrected" + filePath.substring(filePath.length() - 4);
                File correctedTextFile = new File(correctedName);
                if (correctedTextFile.createNewFile()) {
                    System.out.println("File created: " + correctedTextFile.getName());
                } else {
                    System.out.println("File already exists.");
                }

                File fileToCorrect = new File(filePath);
                BufferedReader reader = new BufferedReader(new FileReader(fileToCorrect));
                while((line = reader.readLine()) != null) {
                    lineToCorrect = line;
                    for (String word : lineToCorrect.split("[-\\s]")){
                        word = word.replaceAll("[^\\p{L}]+", "");
                        totalWords++;
                        if((word.length() > 2) && !dictionary.containsKey(word.toLowerCase())){
                            lineToCorrect = lineToCorrect.replaceAll(word, correctWord(word));
                        }
                    }
                    textToCorrect += lineToCorrect + "\r\n";
                }
                reader.close();

                PrintWriter writer = new PrintWriter(correctedTextFile, "UTF-8");
                writer.println(textToCorrect);
                writer.close();
                System.out.println("Correction complete.\nTotal words checked: " + totalWords + ".\nTotal words corrected: " + corrections + ".\nCorrected file path is: " + correctedName + "\n");

            } else {
                IO.pathErr();
            }
        }
        catch (IOException e){
            IO.uknErr();
        }
    }

    //Master function to correct word with keeping the register and using the most optimal method
    private String correctWord(String word){
        HashMap<String, Integer> solutions = new HashMap<>();
        String solution = "";
        char[] letters = word.toCharArray();
        int maxvalue = 0;
        //firstly, remembering the uppercase characters
        boolean[] letterCase = new boolean[letters.length+10]; //making bigger boolean array in case that word will be enlarged
        for(char letter : letters){
            if(Character.isUpperCase(letter)){
                letterCase[word.indexOf(letter)] = true;
            }
        }
        HashSet<String> possibleVars = listAllPossible(word.toLowerCase());
        //filling in all present in dict variants
        for(String variant : possibleVars) {
            if (dictionary.containsKey(variant)) {
                solutions.put(variant, dictionary.get(variant));
            }
        }
        if(!solutions.isEmpty()){
            //if there are solutions with edit distance 1, finding the most used one
            for(Map.Entry<String, Integer> entry : solutions.entrySet()){
                if(entry.getValue() > maxvalue){
                    maxvalue = entry.getValue();
                    solution = entry.getKey();
                }
            }
            System.out.println("Short function was used to correct word " + word + ". Levenshtein distance is 1.");
        } else {
            //if there are no solutions with edit distance 1, finding the word with long search function
            solution = correctWordLong(word);
        }
        //returning uppercase values
        char[] solutionChar = solution.toCharArray();
        for(int i = 0; i < solutionChar.length; i++){
            if(letterCase[i]){
                solutionChar[i] = Character.toUpperCase(solutionChar[i]);
            }
        }
        solution = new String(solutionChar);
        System.out.println("Word " + word + " corrected to " + solution + ".\n");
        corrections++;
        return solution;
    }

    //recursive function to find edit distance between 2 strings
    private int getEditDistance(String sourceWord, String targetWord) {
        if (sourceWord == null || targetWord == null) {
            throw new IllegalArgumentException("Parameter must not be null");
        }
        int sourceLength = sourceWord.length();
        int targetLength = targetWord.length();
        if (sourceLength == 0) return targetLength;
        if (targetLength == 0) return sourceLength;
        int[][] dist = new int[sourceLength + 1][targetLength + 1];
        for (int i = 0; i < sourceLength + 1; i++) {
            dist[i][0] = i;
        }
        for (int j = 0; j < targetLength + 1; j++) {
            dist[0][j] = j;
        }
        for (int i = 1; i < sourceLength + 1; i++) {
            for (int j = 1; j < targetLength + 1; j++) {
                int cost = sourceWord.charAt(i - 1) == targetWord.charAt(j - 1) ? 0 : 1;
                dist[i][j] = Math.min(Math.min(dist[i - 1][j] + 1, dist[i][j - 1] + 1), dist[i - 1][j - 1] + cost);
                if (i > 1 &&
                        j > 1 &&
                        sourceWord.charAt(i - 1) == targetWord.charAt(j - 2) &&
                        sourceWord.charAt(i - 2) == targetWord.charAt(j - 1)) {
                    dist[i][j] = Math.min(dist[i][j], dist[i - 2][j - 2] + cost);
                }
            }
        }
        return dist[sourceLength][targetLength];
    }

    //function for finding the Long correction for the word - it checks the whole dictionary and finds the nearest word possible.
    //Applied only if Short method (finding all words with edit dist.1 and choosing the best one) didn't give any solutions.
    private String correctWordLong(String word){
        int minimalDist = 999;
        int maximalWordCases = 0;
        int currentDist, currentWordCases;
        String nearestWord = "";
        //at first, finding minimal word distance across the whole dictionary
        for(Map.Entry<String, Integer> element : dictionary.entrySet()) {
            currentDist = getEditDistance(word, element.getKey());
            if(currentDist <= minimalDist) {
                minimalDist = currentDist;
            }
        }
        //at second, checking all words with this distance and pulling out the most popular one
        for(Map.Entry<String, Integer> element : dictionary.entrySet()) {
            currentDist = getEditDistance(word, element.getKey());
            if(currentDist == minimalDist){
                if((currentWordCases = element.getValue()) > maximalWordCases){
                    maximalWordCases = currentWordCases;
                    nearestWord = element.getKey();
                }
            }
        }
            System.out.println("Long function was used to correct word " + word + ". Levenshtein distance is " + minimalDist + ". Number of mentions in dictionary is " + maximalWordCases + ".");
        return nearestWord;
    }

    //this function lists all possible ways to change the source word using edit distance 1
    private HashSet<String> listAllPossible(String word){
        HashSet<String> allVariants = new HashSet<>();
        String changedWord;

        //Listing all remove 1 character changes
        for(int symbol = 0; symbol < word.length(); symbol++) {
            changedWord = word.substring(0, symbol) + word.substring(symbol + 1);
            allVariants.add(changedWord);
        }
        //Listing all add 1 character changes
        for(int symbol = 0; symbol < word.length() + 1; symbol++){
            for (char letter : alphabet) {
                changedWord = word.substring(0, symbol) + letter + word.substring(symbol);
                allVariants.add(changedWord);
            }
        }
        //Listing all replace 1 character changes
        for(int symbol = 0; symbol < word.length(); symbol++){
            for (char letter : alphabet) {
                changedWord = word.substring(0, symbol) + letter + word.substring(symbol + 1);
                allVariants.add(changedWord);
            }
        }

        //Listing all swap 2 characters changes
        for(int symbol = 0; symbol < word.length(); symbol++){
            for(int symbol2 = 0; symbol2 < word.length(); symbol2++){
                if(symbol != symbol2) {
                    changedWord = swapChars(word, symbol, symbol2);
                    allVariants.add(changedWord);
                }
            }
        }
        return allVariants;
    }

    //function to swap two characters in the word (on positions 1 and 2)
    private String swapChars(String str, int charPos1, int charPos2) {

        char[] letters = str.toCharArray();
        char temp = letters[charPos1];
        letters[charPos1] = letters[charPos2];
        letters[charPos2] = temp;

        return new String(letters);
    }
}