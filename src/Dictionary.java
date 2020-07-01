import java.io.*;
import java.util.HashMap;

public class Dictionary{
    private HashMap<String, Integer> wordCount;
    private String bufferLine;
    private InputOutput IO;

    public Dictionary(){
        wordCount = new HashMap<>();
        IO = new InputOutput();
    }

    public void addToDictionary(String filePath){
        try{
            if(filePath.endsWith(".txt")){
                FileReader fr = new FileReader(filePath);
                BufferedReader br = new BufferedReader(fr);
                while((bufferLine = br.readLine()) != null){
                    mapMerge(wordCount, lineToMap(bufferLine.toLowerCase()));
                }
                wordCount.values().removeIf(value -> value<7);
                wordCount.keySet().removeIf(key -> key.length()<3);
                br.close();
                fr.close();
                System.out.println("Dictionary successfully added;");
            } else {
                IO.pathErr();
            }
        } catch(IOException e){
            IO.uknErr();
        }
    }

    public HashMap<String, Integer> getWordCount() {
        return wordCount;
    }

    //transforms a line to a hashMap, also cleaning non-literal characters
    private HashMap<String, Integer> lineToMap(String line) {
        HashMap<String, Integer> map = new HashMap<>();
        for (String element : line.split("\\s+")){
            element = element.replaceAll("[^\\p{L}]+", "");
            if(map.containsKey(element)){
                map.put(element, (map.get(element) + 1));
            } else{
                map.put(element, 1);
            }
        }
        return map;
    }

    //puts all secondary values to the basic one
    private void mapMerge(HashMap<String, Integer> mapBasic, HashMap<String, Integer> mapSecondary){
        for(HashMap.Entry<String, Integer> element : mapSecondary.entrySet()){
            if(mapBasic.containsKey(element.getKey())){
                mapBasic.put(element.getKey(), (element.getValue() + mapBasic.get(element.getKey())));
            } else{
                mapBasic.put(element.getKey(), element.getValue());
            }
        }
    }
}
