import fi.tkk.cs.tkkcc.*;
import fi.tkk.cs.tkkcc.slx.*;

public class Compiler implements SlxCompiler {
  public static void main(String[] args) {
    System.out.println("Parsing source file: " + args[0]);
    Printer printer = new Printer(false);
    Scanner scanner = new Scanner(args[0]);
    Parser parser = new Parser(scanner);
    parser.Parse();
    System.out.println(parser.errors.count + " errors detected");
  }

  public boolean isErrors() {
    return false;
  }
    
  public SlxProgram compile(String sourceFilename) {
    return null;
  }

}
