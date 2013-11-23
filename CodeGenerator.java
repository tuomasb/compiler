import fi.tkk.cs.tkkcc.slx.*;

public class CodeGenerator {

  SlxProgram program = new SlxProgram();
  public void emit(final CommandWord command) {
    program.emit(command);
  }

  public void emit(final CommandWord command, final int param1) {
    program.emit(command, param1);
  }

  public void emit(final CommandWord command, final int param1, final int param2) {
    program.emit(command, param1, param2);
  }

  public String toString() {
    return program.toString();
  }
}
