import fi.tkk.cs.tkkcc.*;
import fi.tkk.cs.tkkcc.slx.*;

public class UseCompiler  {

  public static void main(String[] args) {
    Compiler c = new Compiler();
    SlxProgram a = c.compile(args[0]);
    System.out.println(a.toString());
  }
}
