import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Menu {

    private InputOutput IO;
    private Dictionary dict;
    private Spellchecker spCh;


    public Menu(){
        IO = new InputOutput();
        dict = new Dictionary();
    }

    public void mainLoop(){
        IO.entryMessage();
        int choice = Integer.parseInt(IO.getCommand());
        System.out.println("Your choice: " + choice);
        switch (choice) {
            case 1:
                dict.addToDictionary(IO.askDictionaryPath());
                spCh = new Spellchecker(dict.getWordCount());
                break;
            case 2:
                if (!dict.getWordCount().isEmpty()) {
                    spCh.correctSpelling(IO.askCorrectionPath());
                } else {
                    System.out.println("Add any dictionaries first!");
                }
                break;
            case 3:
                return;
            default:
                IO.commandErr();
                break;
        }
        mainLoop();
    }
}
