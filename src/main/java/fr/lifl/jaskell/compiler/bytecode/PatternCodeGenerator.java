/*
 * Created on Jun 4, 2003 by Arnaud Bailly - bailly@lifl.fr
 * Copyright 2003 - Arnaud Bailly 
 */
package fr.lifl.jaskell.compiler.bytecode;

import fr.lifl.jaskell.compiler.JaskellVisitorAdapter;
import fr.lifl.jaskell.compiler.core.Alternative;
import fr.lifl.jaskell.compiler.core.BooleanLiteral;
import fr.lifl.jaskell.compiler.core.CharLiteral;
import fr.lifl.jaskell.compiler.core.Constructor;
import fr.lifl.jaskell.compiler.core.ConstructorPattern;
import fr.lifl.jaskell.compiler.core.DoubleLiteral;
import fr.lifl.jaskell.compiler.core.FloatLiteral;
import fr.lifl.jaskell.compiler.core.IntegerLiteral;
import fr.lifl.jaskell.compiler.core.LocalBinding;
import fr.lifl.jaskell.compiler.core.Pattern;
import fr.lifl.jaskell.compiler.core.StringLiteral;
import fr.lifl.jaskell.compiler.datatypes.ConstructorDefinition;
import fr.lifl.jaskell.compiler.types.Type;
import fr.lifl.jaskell.runtime.types.JObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.norsys.klass.bytes.ClassData;
import fr.norsys.klass.bytes.ClassFile;
import fr.norsys.klass.bytes.FieldRefData;
import fr.norsys.klass.bytes.Instruction;
import fr.norsys.klass.bytes.InterfaceMethodData;
import fr.norsys.klass.bytes.MethodRefData;
import fr.norsys.klass.bytes.Sequence;
import fr.norsys.klass.bytes.StringData;
import fr.norsys.klass.bytes.TypeHelper;

/**
 * A visitor to generate tests for patterns
 * 
 * This Visitor class generates instructions sequences to assert that
 * a given value matches a given pattern. This instruction sequences 
 * should leave on the stack a value which is non zero if pattern matches 
 * and zero otherwise.
 * 
 * @author bailly
 * @version $Id: PatternCodeGenerator.java 1207 2006-05-26 08:55:33Z nono $
 */
public class PatternCodeGenerator extends JaskellVisitorAdapter {

	private CodeGenerator codegen;

	private Class currentClass;

  private ClassFile classFile;
	
	PatternCodeGenerator(CodeGenerator codegen,ClassFile classFile) {
		this.codegen = codegen;
		this.classFile = classFile;
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.BooleanLiteral)
	 */
	public Object visit(BooleanLiteral a) {
		Sequence seq = new Sequence(classFile);
		LocalBinding e = ((Alternative) a.getParent()).getBinding();
		seq.add((Instruction) codegen.generateLoad(e));
		if (a.getBoolean())
			seq._ifeq(0); /* e == True iff e == 1 */
		else
			seq._ifne(0); /* e == False iff e == 0 */
		return seq;
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.CharLiteral)
	 */
	public Object visit(CharLiteral a) {
		Sequence seq = new Sequence(classFile);
		LocalBinding e = ((Alternative) a.getParent()).getBinding();
		seq.add((Instruction) codegen.generateLoad(e)).add(
			codegen.generateConstantInt(a.getChar()));
		seq._if_icmpne(0);
		return seq;
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.ConstructorPattern)
	 */
	public Object visit(ConstructorPattern a) {
		Constructor ctor = a.getConstructor();
		/* retrieve definition */
		ConstructorDefinition cdef =
			(ConstructorDefinition) ctor.lookup(ctor.getName());
		Type2Class tc = new Type2Class(codegen.getNamespace());
		Class dcls = (Class) cdef.visit(tc);
		/* generate test for class */
		short cref =
			ClassData.create(
				codegen.getClassFile().getConstantPool(),
				dcls.getName().replace('.', '/'));
		List l = new ArrayList();
		Sequence seq = new Sequence(classFile);
		int nargs = cdef.getParameters().size();
		int sz = 0;
		/* generate primary instanceof test */
		LocalBinding e = ((Alternative) a.getParent()).getBinding();
		seq.add((Instruction) codegen.generateLoad(e));
		seq._instanceof(cref)._ifeq(0);
		l.add(seq);
		/* add subpatterns to list of tests */
		Iterator it = a.getSubPatterns();
		int i = 0;
		while (it.hasNext()) {
			/* compute type of parameter */
			Type ptype = (Type) cdef.getParameters().get(i);
			Class cls = (Class) ptype.visit(tc);
			/* compute signature for field */
			String psig = TypeHelper.getInternalName(cls);
			short fref;

			if (cdef.isStrict(i))
				fref =
					FieldRefData.create(
						codegen.getClassFile().getConstantPool(),
						dcls.getName().replace('.', '/'),
						"_" + i,
						psig);
			else
				fref =
					FieldRefData.create(
						codegen.getClassFile().getConstantPool(),
						dcls.getName().replace('.', '/'),
						"_" + i,
						"Lfr/lifl/jaskell/runtime/types/JObject;");

			/* extract parameter from ctor */
			Sequence pseq =
				new Sequence(classFile)
					.add((Instruction) codegen.generateLoad(e))
					._checkcast(cref)
					._getfield(fref, codegen.getClassFile());
			/* call eval if parameter is not strict */
			if (!cdef.isStrict(i)) {
				short evalref = 
				InterfaceMethodData.create(
					codegen.getClassFile().getConstantPool(),
					"fr/lifl/jaskell/runtime/types/JObject",
					"eval",
					"()Lfr/lifl/jaskell/runtime/types/JObject;");
					pseq._invokeinterface(evalref,1,codegen.getClassFile());
				/* handle boxed values */
				if(cls.isPrimitive() || cls.equals(java.lang.String.class)) {
					String ev = codegen.typedEval(ptype);
				short evref =
					MethodRefData.create(
						codegen.getClassFile().getConstantPool(),
						"fr/lifl/jaskell/runtime/types/JValue",
						ev,
						"()"+psig);
					short clref = ClassData.create(codegen.getClassFile().getConstantPool(),
					"fr/lifl/jaskell/runtime/types/JValue");
				pseq._checkcast(clref)._invokevirtual(evref, codegen.getClassFile());
				}
			}
			/* store current class for local bindings */
			currentClass = cls;
			pseq.add((Instruction) ((Pattern) it.next()).visit(this));
			l.add(pseq);
			/* next field */
			i++;
		}
		/* append jump instructions */
		it = l.iterator();
		/* advance iterator to leave a boolean on stack at end of evaluation */
		seq = new Sequence(classFile);
		while (it.hasNext()) {
			Instruction ti = (Instruction) it.next();
			/* test bloc */
			seq.add(ti);
		}
		return seq;
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.DoubleLiteral)
	 */
	public Object visit(DoubleLiteral a) {
		// TODO Auto-generated method stub
		return super.visit(a);
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.FloatLiteral)
	 */
	public Object visit(FloatLiteral a) {
		Sequence seq = new Sequence(classFile);
		LocalBinding e = ((Alternative) a.getParent()).getBinding();
		seq
			.add((Instruction) codegen.generateLoad(e))
			.add(codegen.generateConstantFloat(a.getFloat(),classFile))
			._fcmpg();
		/* compute jump address */
		seq._ifne(0);
		return seq;
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.IntegerLiteral)
	 */
	public Object visit(IntegerLiteral a) {
		Sequence seq = new Sequence(classFile);
		LocalBinding e = ((Alternative) a.getParent()).getBinding();
		seq.add((Instruction) codegen.generateLoad(e)).add(
			codegen.generateConstantInt(a.getInteger()));
		/* decrement size of test for computing jump */
		seq._if_icmpne(0);
		return seq;
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.StringLiteral)
	 */
	public Object visit(StringLiteral a) {
		/* reference for string constante */
		short sref =
			StringData.create(
				codegen.getClassFile().getConstantPool(),
				a.getString());
		/* reference for equals() method */
		short eqref =
			MethodRefData.create(
				codegen.getClassFile().getConstantPool(),
				"java/lang/Object",
				"equals",
				"(Ljava/lang/Object;)Z");
		Sequence seq = new Sequence(classFile);
		LocalBinding e = ((Alternative) a.getParent()).getBinding();
		seq.add((Instruction) codegen.generateLoad(e));
		seq._ldc(sref)._invokevirtual(eqref, codegen.getClassFile());
		seq._ifeq(0);
		return seq;
	}

	/* (non-Javadoc)
	 * @see jaskell.compiler.JaskellVisitor#visit(jaskell.compiler.core.LocalBinding)
	 */
	public Object visit(LocalBinding a) {
		/*  
		 * we simply store the content at top of stack to local variable
		 * at index of binding 
		 */
		int idx = a.getIndex();
		Type2Class tc = new Type2Class(codegen.getNamespace());
		Class cls = (Class) a.getType().visit(tc);
		Sequence seq = new Sequence(classFile);
		if(cls.isPrimitive() && (JObject.class.isAssignableFrom(currentClass)))
			seq.add(Type2Class.unbox(codegen.getClassFile(),cls));
		/* localbindings are irrefutable this means we must leave a true on stack */
		return seq.add(codegen.generateStore(idx,a));
	}

}
