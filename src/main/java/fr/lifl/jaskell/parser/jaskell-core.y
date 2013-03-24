/************************************************************************
 * Grammar file for Jaskell
 *
 * This file is a byacc/J grammar for the Jaskell language, a subset 
 * of Haskell used for specifying functions in FIDL component specifications
 *
 *  Version : $Id: jaskell-core.y 1154 2005-11-24 21:43:37Z nono $
 * History : $Log: jaskell-core.y,v $
 * History : Revision 1.37  2004/07/23 06:55:33  bailly
 * History : ajout set/getModule + correction sur patterns message
 * History :
 * History : Revision 1.36  2004/07/03 20:34:22  bailly
 * History : correction pattern matching sur les litt?aux
 * History : modification r?solution nom dans Module pour prendre en compte
 * History : les modules import?s renomm?s
 * History : correction dans le parseur
 * History :
 * History : Revision 1.35  2004/07/01 15:57:41  bailly
 * History : suppression de l'interface Namespace au profit de fr.lifl.parsing.Namespace
 * History : modification de la g?n?ration de code pour les constructeurs et les types de donnees
 * History : creation d'un type JEvent et d'un constructeur Event
 * History : modification du parser pour creer des Event lors de l'analyse syntaxique
 * History :
 * History : Revision 1.34  2004/04/01 08:12:55  bailly
 * History : Fix handling of load/store in alteratives
 * History : TODO: remove unnecessary @variables
 * History :
 * History : Revision 1.33  2004/03/16 13:27:01  bailly
 * History : Integration du pattern matching de messages dans le TypeChecker et la generation de code
 * History :
 * History : Revision 1.32  2004/03/10 22:21:29  bailly
 * History : Modified type checking and code generation
 * History :
 * History : Revision 1.31  2004/03/10 20:06:49  bailly
 * History : *** empty log message ***
 * History :
 * History : Revision 1.30  2004/02/23 16:46:25  bailly
 * History : Added generation of MessagePattern from grammar
 * History : Fixed constant propagation -> methods are properly
 * History : resolved and called if they exist
 * History :
 * History : Revision 1.29  2004/02/20 16:19:32  nono
 * History : Modified grammar to properly handle message constructions
 * History :
 **************************************************************************/
%{

 import fr.lifl.jaskell.compiler.core.*;
 import fr.lifl.jaskell.compiler.bytecode.*;
 import fr.lifl.jaskell.compiler.datatypes.*;
 import fr.lifl.jaskell.compiler.types.*;
 import java.util.*;
 import java.io.InputStream;
 import java.io.FileInputStream;
 import java.io.Reader; 
 import java.io.IOException;
 import fr.lifl.parsing.Parser;
 import fr.lifl.parsing.ParserError;
 import fr.lifl.parsing.SymbolException;
 import fr.lifl.parsing.ParserException;
 import fr.lifl.parsing.ParserListener;
 import fr.lifl.parsing.ParserConfiguration;
 import fr.lifl.parsing.ParserListenerDelegate;
import fr.lifl.parsing.ParserPosition;
  import fr.lifl.parsing.Namespace;
  
%}


%token QUALIFIED QCONID CONID QVARID VARID QVARSYM VARSYM QCONSYM CONSYM 
%token INTEGER FLOAT CHAR STRING BOOLEAN
%token MODULE WHERE TYPE DATA NEWTYPE CLASS INSTANCE DEFAULT DERIVING AS IMPORT 
%token LET IN IF THEN ELSE CASE OF DO AT IRREF EQ ENUM 
%token L_BRACE R_BRACE L_PAREN R_PAREN L_BRACKET R_BRACKET L_ANGLE R_ANGLE
%token SEMICOLON COMMA COLON DOUBLECOLON CONTEXT_OP FUNOP QUALOP STRICT 
%token MINUS BAR INFIXL INFIXR INFIX LAMBDA WILDCARD INFIXOP
%token OPENPRAGMA CLOSEPRAGMA OPENCOMMENT CLOSECOMMENT NATIVE
%token POINT

%left IN ELSE FUNOP QOP
%left MINUS
%left UMINUS

%%

module: MODULE modid 
{
  String mname = ((Variable)$2).getName();
  module = (Module)Module.getToplevels().get(mname);
  if(module == null) {
    module = new Module(mname,null);
    module.putTag(lex.makePosition());
  }
  equations = new HashMap(); 
} WHERE L_BRACE body  R_BRACE
{
  /* normalize module equations */
    /* normalize equations */
    Normalizer nz = new Normalizer(module);
    try {
      nz.normalize(equations);  
    } catch(SymbolException ex) {
      /* notify event */
      parseError("Cannot normalize equations :"+ex);
    }
    modules.add(module); 
} 
| 
{ 
  /* default module is Main - ??? */
  if(module == null)
     module = new Module("Main",null); 
   module.putTag(lex.makePosition());
  equations = new HashMap(); 
} body {
/* normalize equations */
    Normalizer nz = new Normalizer(module);
    try {
      nz.normalize(equations);  
    } catch(SymbolException ex) {
      /* notify event */
      parseError("Cannot normalize equations :"+ex);
    }
    modules.add(module); 
} ;

body: topdecls;

topdecls: topdecls SEMICOLON topdecl
| topdecl ;


decls: L_BRACE  R_BRACE { $$ = new ExpressionList(); } 
| L_BRACE decls R_BRACE { $$ = $2; }
;

decls: decls SEMICOLON decl 
{ 
  if($1 instanceof ExpressionList) {
    ((ExpressionList)$1).add($3);
    $$  = $1;
  } else {
    ExpressionList decls = new ExpressionList();
    if($1 != null) decls.add($1);
    if($3 != null) decls.add($3);
    $$ = decls;
  };
}
| decl { ExpressionList el = new ExpressionList(); if($1 != null) el.add($1); $$ = el;} ;


topdecl: 
DATA optype EQ constrs /* deriving */
{ 
  Type t = $2.getType();
  /* bind name in module */
  String lname = null;
  if(t instanceof TypeConstructor) 
    lname = ((TypeConstructor)t).getName();
  else if(t instanceof TypeApplication) 
    lname = ((TypeConstructor)t.getConstructor()).getName();
  else 
    System.err.println("Type of optype :"+$2.getClass()+"("+$2+")");
  /* create definition for type */
  DataDefinition ddef = new DataDefinition(lname,t,module);
  ddef.putTag(lex.makePosition());
  /* bind constructors */
  Iterator it = ((ExpressionList)$4).iterator();
  while(it.hasNext()) 
    ddef.addConstructor((ConstructorDefinition)it.next());
  /* done */
}
| decl 
{
  if($1 != null) {
    Equation eq = (Equation)$1;
    ExpressionList lhs =  (ExpressionList)eq.getLhs();
    String fname = ((Variable)lhs.get(0)).getName();
    addEquation(fname,eq);
  }
} 
| vardecl 
{
  Iterator it  =((ExpressionList)$1).iterator();
  while(it.hasNext()) {
    Definition def = (Definition)it.next();
    module.bind(def.getName(),def);
  }
} 
| pragma
;

pragma: OPENPRAGMA  directive CLOSEPRAGMA
| OPENPRAGMA CLOSEPRAGMA ;

/* directive for native declarations */
directive: 
NATIVE DATA optype STRING  /* native data types */
{
  /* create PrimitiveData object and bind it in given module */
  Type t = $3.getType();
  String lname = null;
  if(t instanceof TypeConstructor) 
    lname = ((TypeConstructor)t).getName();
  else if(t instanceof TypeApplication) 
    lname = ((TypeConstructor)t.getConstructor()).getName();
  else 
    System.err.println("Type of optype :"+$3.getClass()+"("+$3+")");
  /* resolve java class */
  Class cls;
  try  {
    cls = Class.forName(((StringLiteral)$4).getString());
      /* create definition for type */
    PrimitiveData ddef = new PrimitiveData(lname,t,cls,module);  
  }catch(ClassNotFoundException cnfex) {
    System.err.println("Error in native datatype definition : cannot find class "+((StringLiteral)$4).getString());    
  }
}
| NATIVE atype EQ atype STRING /* native constructors */ 
{ 
  /* resolve data definition */
  Type t = $2.getType();
  String tname = null;
  if(t instanceof TypeConstructor) 
    tname = ((TypeConstructor)t).getName();
  else if(t instanceof TypeApplication) 
    tname = ((TypeConstructor)t.getConstructor()).getName();
  else 
    System.err.println("Type of optype :"+$2.getClass()+"("+$2+")");

  DataDefinition ddef = (DataDefinition)module.resolveType(tname);
  if(ddef != null) {
    /* create primitive constructor */
    try  {
      Class cls = Class.forName(((StringLiteral)$5).getString());
      String cname = null;
      t = $4.getType();
      if(t instanceof TypeConstructor) 
	cname = ((TypeConstructor)t).getName();
      else if(t instanceof TypeApplication) 
	cname = ((TypeConstructor)t.getConstructor()).getName();
      else 
	System.err.println("Type of optype :"+$2.getClass()+"("+$2+")");
      /* create definition for ctor */
      PrimitiveConstructor ctor = new PrimitiveConstructor(cname,ddef,new Type[0],cls,module);
    }catch(ClassNotFoundException cnfex) {
      System.err.println("Error in native constructor definition : cannot find class "+((StringLiteral)$5).getString());    
    }
  } else 
      System.err.println("Error in native constructor definition : cannot find data definition for "+ tname);    
}
| NATIVE atype EQ atype con_params STRING /* native constructors */ 
{
  
  /* resolve data definition */
  /* resolve data definition */
  Type t = $2.getType();
  String tname = null;
  if(t instanceof TypeConstructor) 
    tname = ((TypeConstructor)t).getName();
  else if(t instanceof TypeApplication) 
    tname = ((TypeConstructor)t.getConstructor()).getName();
  else 
    System.err.println("Type of optype :"+$2.getClass()+"("+$2+")");
  DataDefinition ddef = (DataDefinition)module.resolveType(tname);
  if(ddef != null) {
    /* create primitive constructor */
    try  {
      Class cls = Class.forName(((StringLiteral)$6).getString());
      String cname = null;
      t = $4.getType();
      if(t instanceof TypeConstructor) 
	cname = ((TypeConstructor)t).getName();
      else if(t instanceof TypeApplication) 
	cname = ((TypeConstructor)t.getConstructor()).getName();
      else 
	System.err.println("Type of optype :"+$2.getClass()+"("+$2+")");
      /* create definition for ctor */
      PrimitiveConstructor cdef = new PrimitiveConstructor(cname,ddef,new Type[0],cls,module);
      /* range over parameters - if any */
      if($5 != null) {
	Iterator it =((ExpressionList)$5).iterator();
	while(it.hasNext()) {
	  TypeExpression tex = (TypeExpression)it.next();
	  if(tex.isStrict()) 
	    cdef.addStrictParameter(tex.getType());
	  else
	    cdef.addParameter(tex.getType());
	}
      }
    }catch(ClassNotFoundException cnfex) {
      System.err.println("Error in native constructor definition : cannot find class "+((StringLiteral)$5).getString());    
    }
  } else 
      System.err.println("Error in native constructor definition : cannot find data definition for "+ tname);    
}
| NATIVE var DOUBLECOLON type STRING /*  a primitive function */
{
  String vname = ((Variable)$2).getName();
  Type t = $4.getType();
  try {
    Class cls = Class.forName(((StringLiteral)$5).getString());
    PrimitiveFunction pf = new PrimitiveFunction(vname,module,t,cls);
    /* register static primitive with bytecode generator */
    PrimitivesCodeGenerator.registerStaticPrimitive(pf);
  } catch(ClassNotFoundException cnfex) {
      System.err.println("Error in native function definition : cannot find class "+((StringLiteral)$5).getString());    
    }
}


constrs: constrs BAR constr { ((ExpressionList)$1).add($3); $$ = $1; }
| constr { ExpressionList el = new ExpressionList(); el.add($1); $$ = el; } ; 

constr: tycon 
{
  /* create definition */
  ConstructorDefinition cdef = new ConstructorDefinition(((Variable)$1).getName(),null,module) ;
  cdef.putTag(lex.makePosition());
  $$ = cdef;
}
|
tycon con_params 
{
  /* create definition */
  ConstructorDefinition cdef = new ConstructorDefinition(((Variable)$1).getName(),null,module) ;
  cdef.putTag(lex.makePosition());
  /* range over parameters - if any */
  if($2 != null) {
      Iterator it =((ExpressionList)$2).iterator();
      while(it.hasNext()) {
	  TypeExpression tex = (TypeExpression)it.next();
	  if(tex.isStrict()) 
	      cdef.addStrictParameter(tex.getType());
	  else
	      cdef.addParameter(tex.getType());
      }
  }
  $$ = cdef;
}
|  conoptype conop conoptype
{
  /* create definition */
  ConstructorDefinition cdef = new ConstructorDefinition(((Variable)$2).getName(),null,module) ;
  cdef.putTag(lex.makePosition());
  TypeExpression tex = (TypeExpression)$1;
  if(tex.isStrict()) 
      cdef.addStrictParameter(tex.getType());
  else
      cdef.addParameter(tex.getType());
  tex = (TypeExpression)$3;
  if(tex.isStrict()) 
      cdef.addStrictParameter(tex.getType());
  else
      cdef.addParameter(tex.getType());
  $$ = cdef;
}
       //       |  {record} con l_brace fielddecls r_brace
;

con_params:  con_params con_param 
{ 
    if( $1 != null) {
	((ExpressionList)$1).add($2);
	$$ = $1;
    }  else {
	ExpressionList el = new ExpressionList();
	el.add($2);
	$$ = el;
    }
} 
| con_param { ExpressionList el = new ExpressionList(); el.add($1); $$ = el; } ;

con_param: STRICT atype { ((TypeExpression)$2).setStrict(true); $$ = $2; } 
| atype { $$ = $1;} ;  

conoptype: btype  {$$= $1; } 
| STRICT atype {  ((TypeExpression)$2).setStrict(true); $$ = $2 ; } 
;
/*
  fielddecls =  fielddecl [fields]:fielddecls_rest* 
  ;
  fielddecls_rest = comma fielddecl; 
  
  newconstr =  {con} con atype 
  |  {record} con l_brace var doublecolon type r_brace 
  ; 
  
  fielddecl = vars doublecolon fieldtype ; 
  
  fieldtype = {lazy} type 
  | {strict} strict atype 
  ;
  
  deriving = derivingt dclasses ;
  
  dclasses = {class} dclass
  | {unit} l_parenthese r_parenthese
  | {tuple} l_parenthese dclass dclasses_rest* r_parenthese
  ;
  
  dclasses_rest = comma dclass;
  
  dclass = qtycls ;
*/


decl:  
funlhs rhs 
{
  Equation eq = new Equation();
  eq.setLhs($1);
  eq.setRhs($2);
  $$ = eq;
}
| pat0n rhs
{
  Equation eq = new Equation();
  eq.setLhs($1);
  eq.setRhs($2);
  $$ = eq;
}
| error { yyerror("Syntax error"); $$ = null; }
;


vardecl: vars DOUBLECOLON optype 
{ 
  Iterator it = ((ExpressionList)$1).iterator();
  ExpressionList el  =new ExpressionList();
  while(it.hasNext()) {
    Definition def= new Definition(); 
    def.setName(((Variable)it.next()).getName()); 
    if($3 != null) 
      def.setType($3.getType());
    el.add(def);
  }
  $$ = el;
};

vars: vars COMMA var 
{ 
  ((ExpressionList)$1).add($3); $$ = $1; 
} 
| var 
{
  ExpressionList el  =new ExpressionList(); el.add($1); $$=el; 
} ;

optype:  type
{
  $$ = $1;
}
; 

type: btype FUNOP type { $$ = new TypeExpression(PrimitiveType.makeFunction($1.getType(),$3.getType())); }
|  btype { $$ = $1; } 
      ;

btype: btype atype 
{
  /* recompute the kind of type constructor */
  Type tcon  = $1.getType().getConstructor();
  if(!(tcon instanceof PrimitiveType))
    tcon.setKind(null);
  $$ = new TypeExpression(TypeFactory.makeApplication($1.getType(),$2.getType())); 
}
| atype { $$ = $1; } 
      ;

atype:  gtycon { $$ = $1; } 
| tyvar 
{
  $$ = $1;
}
| L_PAREN type COMMA type comma_types R_PAREN 
{
  int i = 1;
  List l = new ArrayList();
  l.add($2.getType());
  l.add($4.getType());
  Iterator it =((ExpressionList)$5).iterator();
  while(it.hasNext()) {
    l.add(it.next());
    i++;
  }
  /* make type constructor */
  /* make type defintiion */
  $$ = new TypeExpression(TypeFactory.makeApplication(PrimitiveType.makeTuple(i),l));
}
| L_BRACKET type R_BRACKET 
{
 $$ = new TypeExpression(TypeFactory.makeApplication(Primitives.LIST,$2.getType())); 
}
| L_PAREN type R_PAREN { $$ = $2; }
      ;

comma_types: comma_types COMMA type | ;

gtycon: tycon { $$ = new TypeExpression(TypeFactory.makeTycon(((Constructor)$1).getName())); }
/* | tycon { $$ = new TypeExpression(TypeFactory.makeTycon(((Constructor)$1).getName())); } */
| L_PAREN R_PAREN { $$ = new TypeExpression(Primitives.UNIT); } 
| L_BRACKET R_BRACKET  { $$ = new TypeExpression(Primitives.LIST); }
| L_PAREN FUNOP R_PAREN  { $$ = new TypeExpression(Primitives.FUNCTION); }
| L_PAREN COMMA R_PAREN { $$ = new TypeExpression(Primitives.TUPLE_2); } 

/* | L_PAREN commas R_PAREN   */
/* { */
  
/*   $$ = TypeFactory.makeTycon(Primitives.UNIT);  */

/* } */
       ;

/* commas: commas COMMA */
/* | COMMA */
/* ; */

// %type Context = [Class]

//context :: Context
//        = [$1]  = class
//        | []    = L_PAREN R_PAREN
//        | $2:$3 = L_PAREN class (comma class)* R_PAREN
//        ;

//class   :: Class
//        = ConClass.. = qtycls tyvar
//        | VarClass.. = qtycls L_PAREN tyvar atype+ R_PAREN
//       ;

//scontext = [$1]  = simpleclass
//         | []    = L_PAREN R_PAREN
//         | $2:$3 = L_PAREN simpleclass (comma simpleclass)* R_PAREN
//         ;

//simpleclass = ConClass.. = qtycls tyvar ;

/* function declarations */

funlhs:  var apats 
{
  ExpressionList el = new ExpressionList();
  el.add($1);
  if($2 != null)
    el.addAll((ExpressionList)$2);
  $$ = el;
}
| pat0n varop pat0n
{
  ExpressionList el = new ExpressionList();
  el.add($2);
  el.add($1);
  el.add($3);
  $$ = el;
}
//     | {nest} L_PAREN funlhs R_PAREN apat+
;


rhs: EQ exp { $$ = $2; } // wheredecls
    ;
/*
wheredecls:  WHERE decls
          |  ;
*/
/* expressions */

exp:  exp0n { $$ = $1; } /*DOUBLECOLON type
	      |  exp0n */
    ;

//%left.6 = '-'
//%left.1 = doublecolon
//%left.0 = 'in', 'else', funop

exp0n: exp0n qop exp0n           //
{ 
  Application app = new Application();
  app.putTag(lex.makePosition());
  app.setFunction($2);
  app.addArgument($1);
  app.addArgument($3);
  $$ = app;
}
| lexp0 { $$ = $1;}
| exp10n { $$ = $1;} 
      ;

lexp0: MINUS aexp 
{ 
  Application app = new Application();
  app.putTag(lex.makePosition());
  QualifiedVariable qv =  new QualifiedVariable("negate");
  qv.putTag(lex.makePosition());
  qv.addPathElement("Prelude");
  app.setFunction(qv);
  app.addArgument($2);
  $$ = app;
} %prec UMINUS
| lexp0 qop exp0n 
{ 
  Application app = new Application();
    app.putTag(lex.makePosition());
app.setFunction($2);
  app.addArgument($1);
  app.addArgument($3);
  $$ = app;
};


exp10n:  LAMBDA pat pats FUNOP exp
{
  /* build list of patterns */
  List l = null;
 if($3 != null) {
    l =(ExpressionList)$3;  
  } else 
    l = new ArrayList();
    l.add(0,$2);
    /* build list of variables and abstraction  */
    List vars = new ArrayList();
    Abstraction abs = new Abstraction();
    abs.putTag(lex.makePosition());
    for(int i =0;i<l.size();i++) {
      LocalBinding lb = LocalBinding.freshBinding();
      lb.putTag(lex.makePosition());
      abs.bind(lb);
      vars.add(lb);
    } 
    /* build pattern match */
    List pm  =new ArrayList();
    PatternMatch pm1 = new PatternMatch();
    pm1.patterns = l;
    pm1.expr = $5;
    pm.add(pm1);
    /* normalize equation */
    abs.setBody(new Matcher().match(vars,pm,$5));
    /* done */
    $$ = abs;
}  
| LET decls IN exp 
{ 
  Let let = new Let();
  let.setBody($4);
  /* register all declarations into equations map */
  Map eqs = new HashMap();
  Iterator it = ((ExpressionList)$2).iterator();
  while(it.hasNext()) {
    Equation eq  = (Equation) it.next();
    ExpressionList lhs = (ExpressionList)eq.getLhs();
    String fname = ((Variable)lhs.get(0)).getName();
    /* add eq to map */
    List l=(List)eqs.get(fname);
    if(l == null) {
      l = new ArrayList();
      eqs.put(fname,l);
    }
    l.add(eq);
  }
  /* normalize map and store into let construct */
  Normalizer nz  =new Normalizer(let);
    try {
      nz.normalize(eqs);  
    } catch(SymbolException ex) {
      /* notify event */
      parseError("Cannot normalize equations :"+ex);
    }
  $$ = let;
}
| IF exp THEN exp ELSE exp  
{ 
  Conditional cond = new Conditional();
  cond.putTag(lex.makePosition());
  cond.setCondition($2);
  cond.setIfTrue($4);
  cond.setIfFalse($6);
  $$ = cond;
}
| CASE exp OF L_BRACE alts R_BRACE 
{
  Alternative alt  = new Alternative();
  alt.putTag(lex.makePosition());
  alt.setExpression($2);
  Iterator it = ((ExpressionList)$5).iterator();
  while(it.hasNext()) {
    PatternAlternative pa = (PatternAlternative)it.next();
    alt.addPattern(pa.getPattern(),pa.getExpr());
  }
  $$ = alt;
}
///       | DO L_BRACE stmts R_BRACE
| fexp { $$ = $1; } 
       ;

fexp: fexp aexp 
{ 
  if($1 instanceof Application) {
    ((Application)$1).addArgument($2);
    $$ = $1;
  } else {
    Application app = new Application(); 
    app.putTag(lex.makePosition());
    app.setFunction($1);
    app.addArgument($2);
    $$ = app;
  }
}
| aexp { $$ = $1;}
      ;

aexp:  qvar { $$ = $1; }
| var { $$ = $1; }
| ucon { $$ = $1; } 
|  gcon { $$ = $1; }
|  literal { $$ = $1; }
|  L_PAREN exp R_PAREN { $$ = $2; } 
|  L_PAREN exp comma_expr comma_exprs R_PAREN 
{ 
  /* construct a tuple the size of the expression list */
  Application app = new Application();
  app.putTag(lex.makePosition());
  StringBuffer tconsb = new StringBuffer("((,");
  for(int i=0;i< ((ExpressionList)$4).size();i++) 
    tconsb.append(",");
  tconsb.append("))");
  /* data constructor reference */
  QualifiedConstructor qv = new QualifiedConstructor(tconsb.toString());
  qv.putTag(lex.makePosition());
  qv.addPathElement("Prelude");
  app.setFunction(qv);
  /* add all arguments */
  app.addArgument($2);
  app.addArgument($3);
  for(Iterator it = ((ExpressionList)$4).iterator(); it.hasNext();) 
    app.addArgument((Expression)it.next());
    /* done */
  $$ = app;
}
/* list expression */ |  L_BRACKET exp comma_exprs R_BRACKET
{ 
  /* construct a tuple the size of the expression list */
  Application app = new Application();
  app.putTag(lex.makePosition());
  Application cur = app;
  /* data constructor reference */
  QualifiedConstructor qv = new QualifiedConstructor("(:)");
  qv.putTag(lex.makePosition());
  qv.addPathElement("Prelude"); 
  app.setFunction(qv);
  /* add all arguments */
  app.addArgument($2);
  for(Iterator it = ((ExpressionList)$3).iterator(); it.hasNext();) {
    Application tmp  = new Application();
   tmp.putTag(lex.makePosition());
   tmp.setFunction(qv);
    tmp.addArgument((Expression)it.next());
    cur.addArgument(tmp);
    cur = tmp;
  }
  /* done */
  qv = new QualifiedConstructor("([])");
  qv.putTag(lex.makePosition());
  qv.addPathElement("Prelude"); 
  cur.addArgument(qv);
  $$ = app;
}
|  L_BRACKET exp comma_expr ENUM exp R_BRACKET
|  L_BRACKET exp comma_expr ENUM R_BRACKET
|  L_BRACKET exp ENUM exp R_BRACKET
|  L_BRACKET exp ENUM R_BRACKET
/* left section */ |  L_PAREN exp0n qop R_PAREN 
{ 
  Application app =  new Application();
  app.putTag(lex.makePosition());
  app.setFunction($3);
  app.addArgument($2);
  $$ = app;
}
/* right section */ |  L_PAREN qop exp0n R_PAREN
{ 
  Application app =  new Application();
  app.putTag(lex.makePosition());
  app.setFunction($2);
  app.addArgument($3);
  $$ = app;
}
// |  aexp L_BRACE fbinds R_BRACE
;

comma_exprs: comma_exprs comma_expr 
{
  ((ExpressionList)$1).add($2);
  $$ =$1;
} 
|/* comma_expr */
{ 
  $$ = new ExpressionList(); 
  //  ((ExpressionList)$$).add($1); 
};

comma_expr: COMMA exp { $$ = $2; } ;


alts: alts SEMICOLON alt 
{
  if($1 instanceof ExpressionList) {
    ((ExpressionList)$1).add($3);
    $$  = $1;
  } else {
    ExpressionList decls = new ExpressionList();
    decls.add($1);
    decls.add($3);
    $$ = decls;
  };
}
| alt { $$ = $1; } ;

alt:  pat FUNOP exp { $$  = new PatternAlternative((Pattern)$1,$3); } // wheredecls
//     |  pat gdpats wheredecls
//   | Empty   =
     ;
/*
gdpats: gdpats gdpat
| gdpat ; 

gdpat: gd FUNOP exp; 

gd: BAR exp0n ; 


stmts: stmts_list exp SEMICOLON
| stmts_list exp  ;

stmts_list: stmts_list stmt
| ;

stmt: qual SEMICOLON ;
*/

//fbinds: fbinds COMMA fbind 
//| fbind ;

// fbind: qvar EQ exp ;

pats: pats pat0n
{
  if($1 instanceof ExpressionList) {
    ((ExpressionList)$1).add($2);
    $$  = $1;
  } else if( $1 == null) {
    ExpressionList decls = new ExpressionList();
    decls.add($2);
    $$ = decls;
  };
}
| { $$ = null; } ;

/* qual: exp QUALOP exp; */

pat: pat0n { $$ = $1; } ;
    // n+k

pat0n: pat0n qconop pat0n
{
  ConstructorPattern pat = new ConstructorPattern();
  pat.putTag(lex.makePosition());
  pat.setConstructor((Constructor)$2);
  pat.addPattern((Pattern)$1);
  System.err.println("Type of rh arg : "+$3.getClass());
  pat.addPattern((Pattern)$3);
  $$ = pat;
}
// |  MINUS pat0n
|  pat10n { $$ = $1;} 
;

pat10n:  apat { $$ = $1;} ;

apats: apats apat 
{
  if($1 instanceof ExpressionList) {
    ((ExpressionList)$1).add($2);
    $$  = $1;
  } else if( $1 == null) {
    ExpressionList decls = new ExpressionList();
    decls.add($2);
    $$ = decls;
  };
}
| { $$ = null; };

apat:  var 
{
  LocalBinding bind = new LocalBinding(((Variable)$1).getName());
  bind.putTag(lex.makePosition());
  $$ = bind;
} 
// |  var AT apat 
| ucon {  /* list and unit constructors */
  ConstructorPattern pat = new ConstructorPattern(); 
  pat.putTag(lex.makePosition());
  pat.setConstructor((Constructor)$1);
  $$ = pat;
} 
|  gcon apats 
{ 
  ConstructorPattern pat = new ConstructorPattern(); 
  pat.putTag(lex.makePosition());
  pat.setConstructor((Constructor)$1);
  if($2 != null) {
    Iterator it = ((ExpressionList)$2).iterator();
    while(it.hasNext()) 
      pat.addPattern((Pattern)it.next());
  }
  $$ = pat;
}
|  literal { $$ = $1;} 
|  WILDCARD { $$ = LocalBinding.wildcard; }
|  L_PAREN pat R_PAREN { $$ = $2; } 
|  L_PAREN pat comma_pat comma_pats R_PAREN
{ 
  ConstructorPattern pat = new ConstructorPattern(); 
  pat.putTag(lex.makePosition());
  StringBuffer tconsb = new StringBuffer("((,");
  for(int i=0;i< ((ExpressionList)$4).size();i++) 
    tconsb.append(",");
  tconsb.append("))");
  /* data constructor reference */
  QualifiedConstructor qv = new QualifiedConstructor(tconsb.toString());
  qv.putTag(lex.makePosition());
  qv.addPathElement("Prelude");
  pat.setConstructor(qv);
  /* add all arguments */
  pat.addPattern((Pattern)$2);
  pat.addPattern((Pattern)$3);
  for(Iterator it = ((ExpressionList)$4).iterator(); it.hasNext();) 
    pat.addPattern((Pattern)it.next());
  /* done */
  $$ = pat;
}
|  L_BRACKET pat comma_pats R_BRACKET
{ 
  /* construct a tuple the size of the expression list */
  List l = new ArrayList();
  l.add($2);
  l.addAll((ExpressionList)$3);
  ConstructorPattern app = makeListPattern(l);
    $$ = app;
}
| IRREF message { $$ = $2;} /* hack !! */
// |  IRREF apat 
// |  L_PAREN comma+ R_PAREN
// |  qcon recordcon
;

//recordcon: L_BRACE fpats R_BRACE ;

comma_pats: comma_pats COMMA comma_pat
{
  if($1 instanceof ExpressionList) {
    ((ExpressionList)$1).add($3);
    $$  = $1;
  } else if( $1 == null) {
    ExpressionList decls = new ExpressionList();
    decls.add($3);
    $$ = decls;
  };
}
| { $$  = null; } ;

comma_pat: COMMA pat { $$  =$2; } ;

/* regles pour l'analyse des messages */
message: arrow_method_name message_content    /* synchronous */
{
  ConstructorPattern cp = new ConstructorPattern();
  QualifiedConstructor qv = new QualifiedConstructor("Event");
  qv.addPathElement("Prelude");
  cp.setConstructor(qv);
  cp.addPattern(LocalBinding.wildcard);
  ConstructorPattern cp2  = (ConstructorPattern)$1;
  cp.putTag(lex.makePosition());
  if($2 != null) {
    Iterator it = ((ExpressionList)$2).iterator();
    while(it.hasNext()) 
      cp2.addPattern((Pattern)it.next());
  }
  cp.addPattern(cp2);
  $$ = cp;
}
| port_name arrow_method_name message_content  /* synchronous */
{
  ConstructorPattern cp = new ConstructorPattern();
  QualifiedConstructor qv = new QualifiedConstructor("Event");
  qv.addPathElement("Prelude");
  cp.setConstructor(qv);
  cp.addPattern((Pattern)$1);
  cp.putTag(lex.makePosition());
  ConstructorPattern cp2  = (ConstructorPattern)$2;
  /* qualify constructor with port name */
 QualifiedConstructor qc = new QualifiedConstructor(cp2.getConstructor().getName());
 qc.addPathElement((String)((Literal)$1).unpack());
 cp2.setConstructor(qc);
  if($2 != null) {
    Iterator it = ((ExpressionList)$3).iterator();
    while(it.hasNext()) 
      cp2.addPattern((Pattern)it.next());
  }
  cp.addPattern(cp2);
  $$ = cp;
}
| port_name L_BRACKET message_parametres R_BRACKET  /* asynchronous */
{
  /* TODO */
  $$ = null;
};

/*
  return a incomplete MessagePattern object
*/
message_content: L_PAREN message_parametres R_PAREN
{
  $$ = $2;
}
| L_ANGLE exception R_ANGLE
{
  $$ = $2;
};

/*
  generate cosntructor pattern
 */
arrow_method_name: QUALOP method_name
{
  ConstructorPattern pat = new ConstructorPattern();
  Constructor ctor = new Constructor("Return$"+ ((StringLiteral)$2).getString());
  pat.setConstructor(ctor);
  pat.putTag(lex.makePosition());
  ctor.putTag(lex.makePosition());
  $$ = pat;
}
| FUNOP method_name
{
  ConstructorPattern pat = new ConstructorPattern();
  Constructor ctor = new Constructor("Call$"+ ((StringLiteral)$2).getString());
  pat.setConstructor(ctor);
  pat.putTag(lex.makePosition());
  ctor.putTag(lex.makePosition());
  $$ = pat;
}
| POINT method_name
{
  ConstructorPattern pat = new ConstructorPattern();
  Constructor ctor = new Constructor("Full$"+ ((StringLiteral)$2).getString());
  pat.setConstructor(ctor);
  pat.putTag(lex.makePosition());
  ctor.putTag(lex.makePosition());
  $$ = pat;
}
;

method_name: var { $$ = new StringLiteral(((Variable)$1).getName());} | WILDCARD { $$ = LocalBinding.wildcard; } /* | CONID */;

port_name: var {  $$ = new StringLiteral(((Variable)$1).getName());}| WILDCARD { $$ = LocalBinding.wildcard; }/*  | CONID */;
 
exception_name: var {  $$ = new StringLiteral(((Variable)$1).getName());}| WILDCARD { $$ = LocalBinding.wildcard; } /* | CONID */;

message_parametres: list_message_parametres 
{
  $$ = $1;
}
| return_message 
{
  ExpressionList el = new ExpressionList();
  el.add($1);
  $$ = el;
}
| list_message_parametres return_message 
{
    ((ExpressionList)$1).add($2);
    $$  = $1;
}
|
{
  ExpressionList el = new ExpressionList();
  $$ = el;
};

list_message_parametres: list_message_parametres COMMA message_parametre
{
  if($1 instanceof ExpressionList) {
    ((ExpressionList)$1).add($3);
    $$  = $1;
  } else if( $1 == null) {
    ExpressionList decls = new ExpressionList();
    decls.add($3);
    $$ = decls;
  };
}
| message_parametre 
{
    ExpressionList decls = new ExpressionList();
    decls.add($1);
    $$ = decls;
};

return_message: COLON pat { $$ = $2;} ;

message_parametre: pat { $$ = $1;};

exception: exception_name L_BRACKET list_message_parametres R_BRACKET 
{
  $$ = null;
}
| exception_name
{
  $$ = null;
}
;



//fpats: fpats comma_fpat
      ;

//comma_fpat: COMMA fpat;

//fpat: qvar EQ pat ;

ucon: 
L_PAREN R_PAREN { Constructor ctor = new Constructor("(())"); ctor.putTag(lex.makePosition()); $$ = ctor; }
|  L_BRACKET R_BRACKET { Constructor ctor = new Constructor("([])"); ctor.putTag(lex.makePosition()); $$ = ctor; };

gcon: 
//  L_PAREN comma commas R_PAREN |
qcon { $$ = $1;} 
     ;


// vardecl: var { $$ = $1;} ;

modid: CONID {$$ = $1;} | QCONID { $$ = $1; } ; 

var:  VARID               { $$ = $1; }
|  L_PAREN VARSYM R_PAREN { $$ = $2; } 
;

qvar:  QVARID { $$ = $1; }
       |  L_PAREN QVARSYM R_PAREN { $$ = $2; } ;
qcon:  tycon { $$  = $1;} ;
varop:  VARSYM { $$ = $1; }
       |  INFIXOP VARID INFIXOP { $$ = $2; } ;
qvarop:  QVARSYM { $$ = $1; }
       |  INFIXOP QVARID INFIXOP  { $$ = $2; } ;
conop:  CONSYM { $$ = $1; }
       |  INFIXOP CONID INFIXOP  { $$ = $2; };
qconop:  gconsym { $$ = $1; }
       |  INFIXOP QCONID INFIXOP { $$ = $2; } ;
qop:  varop { $$ = $1;} | qvarop  {$$ = $1;} | conop {$$ = $1;} | qconop {$$ = $1;} | MINUS {Variable var = new Variable("(-)"); var.putTag(lex.makePosition()); $$ = var; } %prec QOP;
gconsym:  COLON { Constructor ctor = new Constructor("(:)"); ctor.putTag(lex.makePosition()); $$ = ctor; } | QCONSYM {$$ = $1;} ;


tycon: QCONID { $$ = $1; }
|  L_PAREN gconsym R_PAREN { $$ = $2; } 
| CONID { $$ = $1; } ;
tyvar: VARID {$$ = new TypeExpression(TypeFactory.makeTypeVariable(((Variable)$1).getName())); };
/*tycls: CONID  { $$ = new TypeExpression(new TypeClass(((Variable)$1).getName()),null); };
qtycls: QCONID { $$ = new TypeExpression(new TypeClass(((Variable)$1).getName()),null); } ;
*/
literal: INTEGER {$$ = $1;} | FLOAT {$$ = $1;}  | CHAR {$$ = $1;} | STRING {$$ = $1;} | BOOLEAN {$$ = $1;} ;

%%


/** the lexer we use */
private Yylex lex;

/** the current module */
private Module module;

/** the list of generated modules */
private List modules = new ArrayList();

/** a Map storing name to Equation list equations */
private Map equations = new HashMap();

/** constructor for strict parameters */
private ConstructorDefinition constructor;

/** identifies source file */
private String sourcefile;

/** line offset we are parsing from */
private int startLine;

/** column offset */
private int startColumn;

/** the reader */
private java.io.Reader reader;

/** handle listener objects */
private ParserListenerDelegate listenerDelegate = new ParserListenerDelegate();

private ConstructorPattern makeListPattern(List l) {
  ConstructorPattern app = null,cur = app;
  QualifiedConstructor qv;
  for(Iterator it = l.iterator(); it.hasNext();) {
    ConstructorPattern tmp  = new ConstructorPattern();
      tmp.putTag(lex.makePosition());
      qv = new QualifiedConstructor("(:)");
      qv.putTag(lex.makePosition());
      qv.addPathElement("Prelude"); 
      tmp.setConstructor(qv);
      tmp.addPattern((Pattern)it.next());
      if(app == null) 
	app = tmp;
      else 
        cur.addPattern(tmp);
      cur = tmp;
  }
  /* done */
  ConstructorPattern tmp  = new ConstructorPattern();
  tmp.putTag(lex.makePosition());
  qv = new QualifiedConstructor("([])");
  qv.putTag(lex.makePosition());
  qv.addPathElement("Prelude"); 
  tmp.setConstructor(qv);
  cur.addPattern(tmp);
  return app;
}

private ConstructorPattern makeEmptyListPattern() {
  /* done */
  ConstructorPattern tmp  = new ConstructorPattern();
  tmp.putTag(lex.makePosition());
  QualifiedConstructor qv = new QualifiedConstructor("([])");
  qv.putTag(lex.makePosition());
  qv.addPathElement("Prelude"); 
  tmp.setConstructor(qv);
  return tmp;
}

public void setStartScope(fr.lifl.parsing.Namespace ns) {
   this.module = (Module)ns;
}

public void setModule(Module module) {
  this.module = module;
}

public Module getModule() {
return module;
}

public List getModules() {
  return modules;
}

public Map getEquations() {
  return equations;
}

public List getEquation(String name) {
  return (List)equations.get(name);
}

public void addEquation(String name, Equation eq) {
  List l = (List)equations.get(name);
  if(l == null) {
    l =  new ArrayList();
    equations.put(name,l);
  }
  l.add(eq);
}

public void setSourceFile(String sf) {
  this.sourcefile = sf;
}

public String getSourceFile() {
  return this.sourcefile;
}

public void setStartLine(int line) {
  this.startLine = line;
}

public void setStartPosition(ParserPosition pos) {
this.startLine = pos.getLine();
this.startColumn = pos.getColumn();
}

public void setReader(java.io.Reader reader) {
this.reader = reader;
}

public void start() throws ParserException {
   if(reader == null)
   	throw new ParserException("Nothing to parse !");
   parse(reader);
   }
   	
   
public void parse(String fname ) {
  try {
    FileInputStream fis = new FileInputStream(fname);
    setSourceFile(fname);
    parse(fis);
  }catch(IOException ioex) {
    System.err.println("Invalid file name "+fname);
    throw new ParseException(ioex);
  }
}

public void addParserListener(ParserListener list) {
  listenerDelegate.addParserListener(list);
  }
  
/**
 * Method parse.
 * @param is
 */
public void parse(InputStream is) {
  lex = new Yylex(is);
  /* init start line */ 
  lex.setLine(startLine);
  lex.setColumn(startColumn);
  lex.parser = this;
  /* start parsing */
  this.yyparse();
}

/**
 * Method parse.
 * @param rd
 */
public void parse(Reader rd) {
  lex = new Yylex(rd);
  /* init start line */ 
  lex.setLine(startLine);
  lex.setColumn(startColumn);
  lex.parser = this;
  /* start parsing */
  this.yyparse();
}

public int yylex() {
  try {
    return lex.yylex();
  } catch (IOException ioex) {
    throw new ParseException(ioex);
  }
}


public void setParserConfiguration(ParserConfiguration config) {
}

public void parseError(String msg) {
  ParserError er = new ParserError();
  er.setPosition(new ParserPosition(lex.getLine(),lex.getColumn()));
  er.setSource(this);
  er.setMessage(msg);
  listenerDelegate.notify(er);
}

public void yyerror(String msg) {
  lex.yyerror(msg);
}

