package lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
  static boolean hadError = false;

  public static void main(String[] args) throws IOException {
    if(args.length > 1) {
      System.out.println("Useage: jlox [script]");
      System.exit(64); //using conventions defined in UNIX sysexits.h
    } else if(args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }
  //if the user supplies a path, will file at path and execute it
  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charset.defaultCharset()));
    
    //indicate an error in the exit code
    if(hadError) System.exit(65);
  }

  //if user does not supply path, runs as a REPL instead
  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    for(;;) {
      System.out.print("> ");
      String line = reader.readLine();
      if(line == null) break;
      run(line);
      hadError = false;
    }
  }

  private static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    //just prints the tokens for now
    for (Token token : tokens) {
      System.out.println(token);
    }
  }

  //basic error reporting
  static void error(int line, String message) {
    report(line, "", message);
  }

  //error reporting helper function
  //prints an error and the line where it occured
  private static void report(int line, String where, 
                            String message) {
    System.err.println(
        "[line " + line + "] Error" + where + ": " + message);
    hadError = true;
  }
}
