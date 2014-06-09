

import fi.tkk.cs.tkkcc.slx.CommandWord;



public class Parser {
	public static final int _EOF = 0;
	public static final int _identifier = 1;
	public static final int _integer = 2;
	public static final int maxT = 31;

	static final boolean T = true;
	static final boolean x = false;
	static final int minErrDist = 2;

	public Token t;    // last recognized token
	public Token la;   // lookahead token
	int errDist = minErrDist;
	
	public Scanner scanner;
	public Errors errors;

	CodeGenerator gen;
SymbolTable tab;


public static final int UNDEF = 0;
public static final int INTEGER = 1;
public static final int BOOL = 2;
public static final int AND = 3;
public static final int LT = 4;
public static final int GT = 5;
public static final int ADD = 6;
public static final int SUB = 7;
public static final int MUL = 8;
public static final int DIV = 9;
public static final int LOAD = 10;
public static final int STORE = 11;



	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new Errors();
	}

	void SynErr (int n) {
		if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr (String msg) {
		if (errDist >= minErrDist) errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}
	
	void Get () {
		for (;;) {
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT) {
				++errDist;
				break;
			}

			la = t;
		}
	}
	
	void Expect (int n) {
		if (la.kind==n) Get(); else { SynErr(n); }
	}
	
	boolean StartOf (int s) {
		return set[s][la.kind];
	}
	
	void ExpectWeak (int n, int follow) {
		if (la.kind == n) Get();
		else {
			SynErr(n);
			while (!StartOf(follow)) Get();
		}
	}
	
	boolean WeakSeparator (int n, int syFol, int repFol) {
		int kind = la.kind;
		if (kind == n) { Get(); return true; }
		else if (StartOf(repFol)) return false;
		else {
			SynErr(n);
			while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}
	
	void SLXProject() {
		MainFuncDecl();
	}

	void MainFuncDecl() {
		Expect(3);
		FuncBody();
		gen.emit(CommandWord.HLT); 
	}

	void FuncBody() {
		Expect(4);
		VarDecl();
		StatementList();
		ReturnStatement();
		Expect(5);
	}

	void VarDecl() {
		String name; int type; 
		while (la.kind == 27 || la.kind == 28) {
			type = Type();
			name = Ident();
			if(tab.FindObj(name) != null) { SemErr("Symbol " + name + " already declared"); } /* Semantic error if symbol already exists in symbol table */
			else { tab.NewObj(name, type); } /* Otherwise insert into symbol table */ 
			Expect(6);
		}
	}

	void StatementList() {
		if (StartOf(1)) {
			Statement();
			StatementList();
		}
	}

	void ReturnStatement() {
		int type; 
		Expect(7);
		type = Expr();
		Expect(6);
	}

	int  Type() {
		int  type;
		type = UNDEF; 
		if (la.kind == 27) {
			Get();
			type = INTEGER; 
		} else if (la.kind == 28) {
			Get();
			type = BOOL; 
		} else SynErr(32);
		return type;
	}

	String  Ident() {
		String  name;
		Expect(1);
		name = t.val; 
		return name;
	}

	int  Expr() {
		int  type;
		int type2, type3, op; type = UNDEF; 
		if (StartOf(2)) {
			type2 = BaseExpr();
			type = type2; 
			if (StartOf(3)) {
				op = Operation();
				type3 = BaseExpr();
				if(type2==INTEGER) {
				if(type3==BOOL) { SemErr("Incompatible types Integer and Boolean in operation, expected type Integer and Integer"); }
				else if(op == AND) { SemErr("Invalid operation for types Integer and Integer"); }
				                          else {
				if(op == ADD) { gen.emit(CommandWord.ADD); type = INTEGER; }
				if(op == SUB) { gen.emit(CommandWord.SUB); type = INTEGER; }
				if(op == MUL) { gen.emit(CommandWord.MUL); type = INTEGER; }
				if(op == DIV) { gen.emit(CommandWord.DIV); type = INTEGER; }
				if(op == LT) { gen.emit(CommandWord.RLT); type = BOOL; }
				if(op == GT) { gen.emit(CommandWord.RGT); type = BOOL; }
				}
				} else if(type2==BOOL) {
				if(type3==INTEGER) { SemErr("Incompatible types Boolean and Integer in operation, expected type Boolean and Boolean"); }
				else if(op == ADD || op == SUB || op == MUL || op == DIV || op == LT || op == GT) {
				SemErr("Invalid operation for types Boolean and Boolean");
				} else {
				if(op == AND) {
				/* SLX has no AND instruction so let's emulate it with a series of commands
				tested to produce correct results with 0&&0=0 0&&1=0 1&&0=0 1&&1=1 */
				gen.emit(CommandWord.NOT);
				gen.emit(CommandWord.ENT, 1);
				gen.emit(CommandWord.ADD);
				gen.emit(CommandWord.REQ);
				type = BOOL;
				}
				}
				} 
			}
		} else if (la.kind == 18) {
			Get();
			type2 = BaseExpr();
			gen.emit(CommandWord.NOT); type = BOOL; 
		} else SynErr(33);
		return type;
	}

	void Statement() {
		Obj a; String name; int type; 
		if (la.kind == 8) {
			Get();
			Expect(9);
			type = Expr();
			Expect(10);
			if(type!=BOOL) SemErr("Boolean type required for if-clause");
			gen.emit(CommandWord.JZE, gen.lab); /* If Expr value in the stack Equals 0, Jump to next emitted label */ 
			Expect(11);
			Statement();
			gen.emit(CommandWord.JMP, gen.lab + 1); /* Expr did not equal zero, jump till the end after executing Statement code */ 
			Expect(12);
			gen.emit(CommandWord.LAB, gen.lab); /* Label for "false" statement */ 
			Statement();
			Expect(13);
			gen.emit(CommandWord.LAB, gen.lab); /* Label for end of if statement */ 
		} else if (la.kind == 14) {
			Get();
			gen.emit(CommandWord.LAB, gen.lab); /* Label for start of program code to be repeated */ 
			Statement();
			Expect(15);
			Expect(9);
			type = Expr();
			Expect(10);
			Expect(6);
			gen.emit(CommandWord.JZE, gen.lab - 1); 
		} else if (la.kind == 16) {
			Get();
			Expect(9);
			type = Expr();
			Expect(10);
			Expect(6);
			gen.emit(CommandWord.WRI); /* Emit write */ 
		} else if (la.kind == 4) {
			Get();
			StatementList();
			Expect(5);
		} else if (la.kind == 1) {
			name = IdAccess();
			a = tab.FindObj(name); if(a != null) { gen.emit(CommandWord.ENT, a.adr); } /* Variable address from symbol table to stack */ 
			Expect(17);
			type = Expr();
			Expect(6);
			if(a != null) { 
			if(type != a.type) SemErr("Assigning incompatible type");
			gen.emit(CommandWord.STM);
			} 
		} else SynErr(34);
	}

	String  IdAccess() {
		String  name;
		Obj a;  
		name = Ident();
		a = tab.FindObj(name); if(a == null) { SemErr("Undeclared variable: " + name); } /* Less code to do this here */ 
		return name;
	}

	int  BaseExpr() {
		int  type;
		int type2; Obj a; String name; type = UNDEF; 
		if (la.kind == 9) {
			Get();
			type = Expr();
			Expect(10);
		} else if (la.kind == 1) {
			name = IdAccess();
			a = tab.FindObj(name); if(a != null) { type = a.type; gen.emit(CommandWord.ENT, a.adr); gen.emit(CommandWord.LDM); } 
		} else if (la.kind == 2) {
			Integer();
			type = INTEGER; 
		} else if (la.kind == 29 || la.kind == 30) {
			Boolean();
			type = BOOL; 
		} else if (la.kind == 26) {
			Get();
			Expect(9);
			Expect(10);
			gen.emit(CommandWord.REA); type = INTEGER; 
		} else SynErr(35);
		return type;
	}

	int  Operation() {
		int  op;
		op = UNDEF; 
		switch (la.kind) {
		case 19: {
			Get();
			op = AND; 
			break;
		}
		case 20: {
			Get();
			op = LT; 
			break;
		}
		case 21: {
			Get();
			op = GT; 
			break;
		}
		case 22: {
			Get();
			op = ADD; 
			break;
		}
		case 23: {
			Get();
			op = SUB; 
			break;
		}
		case 24: {
			Get();
			op = MUL; 
			break;
		}
		case 25: {
			Get();
			op = DIV; 
			break;
		}
		default: SynErr(36); break;
		}
		return op;
	}

	void Integer() {
		int temp = 0; 
		Expect(2);
		try { temp = Integer.parseInt(t.val); gen.emit(CommandWord.ENT, temp); /* Try to parse Integer */
		} catch(Exception e) { SemErr("Integer overflow"); } /* Exception generated. Cannot parse Integer */ 
	}

	void Boolean() {
		if (la.kind == 29) {
			Get();
			gen.emit(CommandWord.ENT, 1); 
		} else if (la.kind == 30) {
			Get();
			gen.emit(CommandWord.ENT, 0); 
		} else SynErr(37);
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		SLXProject();
		Expect(0);

	}

	private static final boolean[][] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,T,x,x, T,x,x,x, T,x,x,x, x,x,T,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x},
		{x,T,T,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,T,x, x,T,T,x, x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,T,T,T, T,T,x,x, x,x,x,x, x}

	};
} // end Parser


class Errors {
	public int count = 0;                                    // number of errors detected
	public java.io.PrintStream errorStream = System.out;     // error messages go to this stream
	public String errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line, 1=column, 2=text
	
	protected void printMsg(int line, int column, String msg) {
		StringBuffer b = new StringBuffer(errMsgFormat);
		int pos = b.indexOf("{0}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, line); }
		pos = b.indexOf("{1}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, column); }
		pos = b.indexOf("{2}");
		if (pos >= 0) b.replace(pos, pos+3, msg);
		errorStream.println(b.toString());
	}
	
	public void SynErr (int line, int col, int n) {
		String s;
		switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "identifier expected"; break;
			case 2: s = "integer expected"; break;
			case 3: s = "\"main\" expected"; break;
			case 4: s = "\"{\" expected"; break;
			case 5: s = "\"}\" expected"; break;
			case 6: s = "\";\" expected"; break;
			case 7: s = "\"return\" expected"; break;
			case 8: s = "\"if\" expected"; break;
			case 9: s = "\"(\" expected"; break;
			case 10: s = "\")\" expected"; break;
			case 11: s = "\"then\" expected"; break;
			case 12: s = "\"else\" expected"; break;
			case 13: s = "\"fi\" expected"; break;
			case 14: s = "\"repeat\" expected"; break;
			case 15: s = "\"until\" expected"; break;
			case 16: s = "\"print\" expected"; break;
			case 17: s = "\":=\" expected"; break;
			case 18: s = "\"!\" expected"; break;
			case 19: s = "\"&&\" expected"; break;
			case 20: s = "\"<\" expected"; break;
			case 21: s = "\">\" expected"; break;
			case 22: s = "\"+\" expected"; break;
			case 23: s = "\"-\" expected"; break;
			case 24: s = "\"*\" expected"; break;
			case 25: s = "\"/\" expected"; break;
			case 26: s = "\"read\" expected"; break;
			case 27: s = "\"int\" expected"; break;
			case 28: s = "\"boolean\" expected"; break;
			case 29: s = "\"true\" expected"; break;
			case 30: s = "\"false\" expected"; break;
			case 31: s = "??? expected"; break;
			case 32: s = "invalid Type"; break;
			case 33: s = "invalid Expr"; break;
			case 34: s = "invalid Statement"; break;
			case 35: s = "invalid BaseExpr"; break;
			case 36: s = "invalid Operation"; break;
			case 37: s = "invalid Boolean"; break;
			default: s = "error " + n; break;
		}
		printMsg(line, col, s);
		count++;
	}

	public void SemErr (int line, int col, String s) {	
		printMsg(line, col, s);
		count++;
	}
	
	public void SemErr (String s) {
		errorStream.println(s);
		count++;
	}
	
	public void Warning (int line, int col, String s) {	
		printMsg(line, col, s);
	}
	
	public void Warning (String s) {
		errorStream.println(s);
	}
} // Errors


class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;
	public FatalError(String s) { super(s); }
}
