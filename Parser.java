

import fi.tkk.cs.tkkcc.slx.CommandWord;



public class Parser {
	public static final int _EOF = 0;
	public static final int _identifier = 1;
	public static final int _integer = 2;
	public static final int maxT = 29;

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

public void d(String dbgString) {
  System.out.println(dbgString);
}



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
	}

	void FuncBody() {
		Expect(4);
		VarDecl();
		StatementList();
		ReturnStatement();
		Expect(5);
	}

	void VarDecl() {
		String ident; int type; 
		if (la.kind == 27 || la.kind == 28) {
			type = Type();
			ident = Ident();
			tab.NewObj(ident, type); d("Added object: " + ident); d(String.valueOf(type)); 
			Expect(6);
			VarDecl();
		}
	}

	void StatementList() {
		if (StartOf(1)) {
			Statement();
			StatementList();
		}
	}

	void ReturnStatement() {
		Expect(7);
		Expr();
		Expect(6);
	}

	int  Type() {
		int  type;
		type = 0; 
		if (la.kind == 27) {
			Get();
			type = 1; 
		} else if (la.kind == 28) {
			Get();
			type = 2; 
		} else SynErr(30);
		return type;
	}

	String  Ident() {
		String  ident;
		Expect(1);
		ident = t.val; d("Id declare: " + t.val); 
		return ident;
	}

	void Expr() {
		int type1, type2; String op; 
		if (StartOf(2)) {
			type1 = BaseExpr();
			if (StartOf(3)) {
				op = Operation();
				type2 = BaseExpr();
				if(type1 != type2) SemErr("Incomparible types");
				/* Emulate && with 1==(a==b) */
				if(op == "&&") { gen.emit(CommandWord.REQ); gen.emit(CommandWord.ENT, 1); gen.emit(CommandWord.REQ); }
				if(op == "<") { gen.emit(CommandWord.RLT); }
				if(op == ">") { gen.emit(CommandWord.RGT); }
				if(op == "+") { gen.emit(CommandWord.ADD); }
				if(op == "-") { gen.emit(CommandWord.SUB); }
				
			}
		} else if (la.kind == 18) {
			Get();
			type1 = BaseExpr();
		} else SynErr(31);
	}

	void Statement() {
		Obj a; String ident; 
		if (la.kind == 8) {
			Get();
			Expect(9);
			Expr();
			Expect(10);
			Expect(11);
			Statement();
			Expect(12);
			Statement();
			Expect(13);
		} else if (la.kind == 14) {
			Get();
			Statement();
			Expect(15);
			Expect(9);
			Expr();
			Expect(10);
			Expect(6);
		} else if (la.kind == 16) {
			Get();
			Expect(9);
			Expr();
			Expect(10);
			Expect(6);
		} else if (la.kind == 4) {
			Get();
			StatementList();
			Expect(5);
		} else if (la.kind == 1) {
			ident = IdAccess();
			Expect(17);
			Expr();
			Expect(6);
			d("Searching for: " + ident); a = tab.FindObj(ident); gen.emit(CommandWord.ENT, a.adr); gen.emit(CommandWord.STM); 
		} else SynErr(32);
	}

	String  IdAccess() {
		String  ident;
		Expect(1);
		ident = t.val; d("Id access: " + t.val); 
		return ident;
	}

	int  BaseExpr() {
		int  type;
		int a; Obj b; String ident; type = 0; 
		switch (la.kind) {
		case 9: {
			Get();
			Expr();
			Expect(10);
			break;
		}
		case 1: {
			ident = IdAccess();
			b = tab.FindObj(ident); type = b.type; gen.emit(CommandWord.ENT, b.adr); gen.emit(CommandWord.LDM); 
			break;
		}
		case 2: {
			Get();
			a = Integer.parseInt(t.val); type = 1; gen.emit(CommandWord.ENT, a); 
			break;
		}
		case 24: {
			Get();
			gen.emit(CommandWord.ENT, 1); type = 2; 
			break;
		}
		case 25: {
			Get();
			gen.emit(CommandWord.ENT, 0); type = 2; 
			break;
		}
		case 26: {
			Get();
			Expect(9);
			Expect(10);
			gen.emit(CommandWord.REA); type = 1; 
			break;
		}
		default: SynErr(33); break;
		}
		return type;
	}

	String  Operation() {
		String  op;
		op = "undef"; 
		if (la.kind == 19) {
			Get();
			op = t.val; d("Operation: " + op.toString()); 
		} else if (la.kind == 20) {
			Get();
			op = t.val; d("Operation: " + op.toString()); 
		} else if (la.kind == 21) {
			Get();
			op = t.val; d("Operation: " + op.toString()); 
		} else if (la.kind == 22) {
			Get();
			op = t.val; d("Operation: " + op.toString()); 
		} else if (la.kind == 23) {
			Get();
			op = t.val; d("Operation: " + op.toString()); 
		} else SynErr(34);
		return op;
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		SLXProject();
		Expect(0);

	}

	private static final boolean[][] set = {
		{T,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x},
		{x,T,x,x, T,x,x,x, T,x,x,x, x,x,T,x, T,x,x,x, x,x,x,x, x,x,x,x, x,x,x},
		{x,T,T,x, x,x,x,x, x,T,x,x, x,x,x,x, x,x,x,x, x,x,x,x, T,T,T,x, x,x,x},
		{x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,x, x,x,x,T, T,T,T,T, x,x,x,x, x,x,x}

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
			case 24: s = "\"true\" expected"; break;
			case 25: s = "\"false\" expected"; break;
			case 26: s = "\"read\" expected"; break;
			case 27: s = "\"int\" expected"; break;
			case 28: s = "\"boolean\" expected"; break;
			case 29: s = "??? expected"; break;
			case 30: s = "invalid Type"; break;
			case 31: s = "invalid Expr"; break;
			case 32: s = "invalid Statement"; break;
			case 33: s = "invalid BaseExpr"; break;
			case 34: s = "invalid Operation"; break;
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
