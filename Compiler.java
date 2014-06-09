import fi.tkk.cs.tkkcc.*;
import fi.tkk.cs.tkkcc.slx.*;

public class Compiler implements SlxCompiler {

  Scanner scanner;
  Parser parser;
  boolean errors;

  public Compiler() {
  }

  public boolean isErrors() {
    return errors;
  }
    
  public SlxProgram compile(String sourceFilename) {
    scanner = new Scanner(sourceFilename);
    parser = new Parser(scanner);
    parser.gen = new CodeGenerator();
    parser.tab = new SymbolTable(parser);
    parser.Parse();
    if(parser.errors.count > 0) { errors = true; }
    if(parser.errors.count == 0) { errors = false; } /* In case same instance is used to compile multiple programs */
    return parser.gen.program;
  }
}
