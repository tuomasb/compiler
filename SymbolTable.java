

public class SymbolTable {

  Parser parser;
  public Obj first;
  public Obj undef;
  int adrNext;

  public SymbolTable(Parser parser) {
    this.parser = parser;
    undef = new Obj();
    undef.adr = 0;
    undef.type = 0;
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
    if(last == null) first = newObj; else { last.next = newObj; System.out.println("Added something\n"); }
    // Assign an address to object and increment next address pointer
    newObj.adr = this.adrNext++;
    System.out.println("Added something at:" + newObj.adr + "\n");
    return newObj;
  }


  public Obj FindObj(String name) {
    Obj iter;
    iter = first; // Start from top scope
    // Iterate over every scope and check all locals in each scope
    while(iter != null) {
      System.out.println("Looking at object: " + iter.name + " and checking if it is " + name);
      if(iter.name.equals(name)) return iter;
      iter = iter.next;
      }
    parser.SemErr(name + " is undefined");
    return undef;
  }

}

// Dual use class for both scopes and types
class Obj {
  public String name;
  public int type;
  public int adr; // Object's address in memory
  public Obj next;
}

