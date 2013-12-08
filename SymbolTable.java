

public class SymbolTable {

  /* Symbol Tables implemented as a simple linked list */

  Parser parser;
  public Obj first;
  int adrNext;

  public SymbolTable(Parser parser) {
    this.parser = parser;
  }

  public Obj NewObj(String name, int type) {
    Obj last;
    Obj iterator;
    Obj newObj = new Obj();
    newObj.name = name;
    /* Undefined = 0, Integer = 1, Boolean = 2. Originally used enum but that resulted in ugly code */
    newObj.type = type;
    iterator = first;
    last = null;
    // Find to last object in the chain
    while(iterator != null) {
      if(iterator.name == name) parser.SemErr(name + " already exists in symbol table");
      last = iterator;
      iterator = iterator.next;
    }
    // If no symbols found then we are inserting the first symbol
    if(last == null) first = newObj; else { last.next = newObj; }
    // Assign an address to object and increment next address pointer
    newObj.adr = this.adrNext++;
    return newObj;
  }

  public Obj FindObj(String name) {
    Obj iter;
    iter = first;
    while(iter != null) {
      if(iter.name.equals(name)) return iter;
      iter = iter.next;
      }
    return null;
  }

}

// Symbol in memory
class Obj {
  public String name;
  public int type;
  public int adr; // Object's address in memory
  public Obj next;
}

