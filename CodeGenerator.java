import fi.tkk.cs.tkkcc.slx.*;

public class CodeGenerator {

  public int lab;
  public SlxProgram program = new SlxProgram();

  public CodeGenerator() {
    lab = 0;
  }

  public void emit(final CommandWord command) {
    program.emit(command);
  }

  public void emit(final CommandWord command, final int param1) {
    program.emit(command, param1);
    if(command == CommandWord.LAB) lab++;
  }

  public void emit(final CommandWord command, final int param1, final int param2) {
    program.emit(command, param1, param2);
  }

  public String toString() {
    return program.toString();
  }
}
