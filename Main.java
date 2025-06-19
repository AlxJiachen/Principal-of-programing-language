import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static Scanner inputScanner; 

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("ERROR: Must provide <codeFile> and <dataFile> as arguments.");
            System.exit(1);
        }

        String codeFile = args[0];
        String dataFile = args[1];

        try {
            inputScanner = new Scanner(new File(dataFile));
            CoreScanner scanner = new CoreScanner(codeFile);
            Procedure mainProc = new Procedure();
            mainProc.parse(scanner);
            mainProc.semanticCheck(new HashMap<>());
            mainProc.execute();
            inputScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("Data file not found: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.out.println("IO Error: " + e.getMessage());
            System.exit(1);
        } catch (RuntimeException e) {
            System.out.println("Runtime Error: " + e.getMessage());
            System.exit(1);
        }
    }
}
