import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputOutput{
    private Scanner sc = new Scanner(System.in);
    public String askDictionaryPath(){
        System.out.print("Enter the path to convert text file to a dictionary: ");
        return sc.next();
    }

    public void entryMessage(){
        System.out.print("Welcome at the spell-check application! \nMenu:\n1. Add a new dictionary" +
                "\n2. Check spelling in a document\n3. Exit\nEnter the number of command you'd like to choose: ");
    }

    public String awaitInput(){
        return sc.next();
    }

    public String askCorrectionPath(){
        System.out.print("Enter the path to correct the text file: ");
        return sc.next();
    }

    public String getCommand(){
        String line = sc.next();
        Matcher matcher;
        String result;
        Pattern pattern = Pattern.compile("\\d+");
        matcher = pattern.matcher(line);
        if(matcher.find()) {
            result = matcher.group();
        }else {
            result = getCommand();
        }
        return result;
    }

    public void commandErr(){
        System.out.println("Command not valid, try again!");
    }

    public void pathErr(){
        System.out.println("Not valid path!");
    }

    public void uknErr(){
        System.out.println("Unknown error!");
    }
}
