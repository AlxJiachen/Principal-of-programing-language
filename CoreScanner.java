import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class CoreScanner {
StringBuilder builder = new StringBuilder();
private BufferedReader br;
private String Wholeinput;
private Core currentCore;
private int index;
private String id;
private int constant;
private String stringvalue;

private final HashMap<String, Core> keywords = new HashMap<>();
private final HashMap<String, Core> symbol = new HashMap<>();

{
        // Keywords, following the order in Core.java, also the Keywords tab in the project1.pdf.
        keywords.put("and", Core.AND);
        keywords.put("begin", Core.BEGIN);
        keywords.put("case", Core.CASE);
        keywords.put("do", Core.DO);
        keywords.put("else", Core.ELSE);
        keywords.put("end", Core.END);
        keywords.put("for", Core.FOR);
        keywords.put("if", Core.IF);
        keywords.put("in", Core.IN);
        keywords.put("integer", Core.INTEGER);
        keywords.put("is", Core.IS);
        keywords.put("new", Core.NEW);
        keywords.put("not", Core.NOT);
        keywords.put("object", Core.OBJECT);
        keywords.put("or", Core.OR);
        keywords.put("print", Core.PRINT);
        keywords.put("procedure", Core.PROCEDURE);
        keywords.put("read", Core.READ);
        keywords.put("return", Core.RETURN);
        keywords.put("then", Core.THEN);

        // Symbols,following the order in Core.java, also the Keywords tab in the project1.pdf.
        symbol.put("+", Core.ADD);
        symbol.put("-", Core.SUBTRACT);
        symbol.put("*", Core.MULTIPLY);
        symbol.put("/", Core.DIVIDE);
        symbol.put("=", Core.ASSIGN);
        symbol.put("==", Core.EQUAL);
        symbol.put("<", Core.LESS);
        symbol.put(":", Core.COLON);
        symbol.put(";", Core.SEMICOLON);
        symbol.put(".", Core.PERIOD);
        symbol.put(",", Core.COMMA);
        symbol.put("(", Core.LPAREN);
        symbol.put(")", Core.RPAREN);
        symbol.put("[", Core.LSQUARE);
        symbol.put("]", Core.RSQUARE);
        symbol.put("{", Core.LCURL);
        symbol.put("}", Core.RCURL);
}

// Initialize the scanner
CoreScanner(String arg) throws IOException {

    try {
       
        br = new BufferedReader(new FileReader(arg));
        String line;
        while ((line = br.readLine()) != null) {
            builder.append(line).append("\n");
        }
    } catch (Exception e) {
        System.out.println("ERROR: Unable to open file " + arg);
        e.printStackTrace();
        currentCore = Core.ERROR;
        return;
    }
    Wholeinput = builder.toString();
    index = 0;
    nextToken();
}
    

    // Advance to the next token
    public final void nextToken() throws IOException {
        // Skip whitespace
        while (index < Wholeinput.length() && Character.isWhitespace(Wholeinput.charAt(index))) {
            index++;
        }
        //When reach the end, we set the currentCore to EOS
        if (index >= Wholeinput.length()) {
            currentCore = Core.EOS;
            return;
        }

        //================Now we start the work.
        char c = Wholeinput.charAt(index);

        if (c == '\'') {
            parseString();
            return;
        }

        if (futureMatch()) {
            //Excute purpose
            return;
        }

         if (Character.isDigit(c)) {
            parseNumber();
            return;
        }

        if (Character.isLetter(c)) {
            int temp = index;
            while (index < Wholeinput.length() && Character.isLetterOrDigit(Wholeinput.charAt(index))) {
                index++;
            }
            String word = Wholeinput.substring(temp, index);
            if (keywords.containsKey(word)) {
                currentCore = keywords.get(word);
            } else {
                id = word;
                currentCore = Core.ID;
            }
            return;
        }

        System.out.println("ERROR: Defnitely out of valid range, check project1.pdf for valid range. The invalid char is: '" + c + "'");
        currentCore = Core.ERROR;
        index++;

}
    //============================================Helper functions
    private boolean futureMatch() {
            // This function is trying to peek the future, up to 2 characters, to see if it is a symbol.
        if (index + 1 < Wholeinput.length()) {
            String future2 = Wholeinput.substring(index, index + 2);
            if (symbol.containsKey(future2)) {
                currentCore = symbol.get(future2);
                index += 2;
                return true;
            }
        }
        if (index < Wholeinput.length()) {
            String future = Wholeinput.substring(index, index + 1);
            if (symbol.containsKey(future)) {
                currentCore = symbol.get(future);
                index++;
                return true;
            }
        }
        return false;
    }
    private void parseString() {
        // Skip the opening quote
                index++;
        int temp = index;
        while (index < Wholeinput.length() && Wholeinput.charAt(index) != '\'') {
            index++;
        }
        if (index >= Wholeinput.length()) {
            System.out.println("ERROR: There is no closing quote for the string, make sure there is two '\''");
            currentCore = Core.ERROR;
            return;
        }
        stringvalue = Wholeinput.substring(temp, index);
        currentCore = Core.STRING;
        // Skip the closing quote
        index++;  
    }

    private void parseNumber() {
               int temp = index;
        while (index < Wholeinput.length() && Character.isDigit(Wholeinput.charAt(index))) {
            index++;
        }
        String temp2 = Wholeinput.substring(temp, index);
        try {
            constant = Integer.parseInt(temp2);
            if (constant < 0 || constant > 8191) {
                throw new NumberFormatException();
            }
            currentCore = Core.CONST;
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Your number is invalid,  according to the project1.pdf, valid range is 0--8191， your number is:" + temp2);
            currentCore = Core.ERROR;
        }
    }

 //============================================Helper functions ends


    // Return the current token
    public Core currentToken() {
        return currentCore;
    }

	// Return the identifier string
    public String getId() {
        return id;

    }

	// Return the constant value
    public int getConst() {

    return constant;

    }
	
	// Return the character string
    public String getString() {
        return stringvalue;

    }


    
   

    

}
