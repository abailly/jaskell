package fr.lifl.jaskell.compiler.types;


/**
 * This class represents application of a type to another type
 *
 * @author bailly
 * @version $Id: TypeApplication.java 1153 2005-11-24 20:47:55Z nono $
 */
public class TypeApplication extends Type {

    private Type domain;
    private Type range;

    /**
     * Constructor for TypeApplication.
     */
    TypeApplication(Type domain, Type range) {
        /* check kind */
        checkKind(domain, range);
        this.domain = domain;
        this.range = range;
    }

    /**
     * Method checkKind.
     * <p/>
     * This method verifies that the type domain can be applied to
     * the type range
     *
     * @param domain
     * @param range
     */
    public static void checkKind(Type domain, Type range) {
        try {
            Kind kd = domain.getKind();
            Kind kr = range.getKind();
            /* range is a type variable without kind - set it to * */
            if (kr == null)
                range.setKind(SimpleKind.K);
            if (kd == null) /* set kind of domain to account for range */
                domain.setKind(new FunctionKind(SimpleKind.K, range.getKind()));
            return;
        } catch (ClassCastException cex) {
            throw new TypeError(
                    "Invalid type application from type "
                            + domain.toString()
                            + "("
                            + domain.getKind()
                            + ")"
                            + " to "
                            + range.toString()
                            + "("
                            + range.getKind()
                            + ")");
        }

    }

    public <T> T visit(TypeVisitor<T> v) {
        return v.visit(this);
    }

    public boolean contains(TypeVariable variableType) {
        return domain.contains(variableType) || range.contains(variableType);
    }

    /**
     * Returns the domain.
     *
     * @return Type
     */
    public Type getDomain() {
        return domain;
    }

    /**
     * Returns the range.
     *
     * @return Type
     */
    public Type getRange() {
        return range;
    }

    public Kind getKind() {
        try {
            return domain.getKind().apply(getRange().getKind());
        } catch (NullPointerException npex) {
            return null;
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String format = domain.getApplyFormatter().formatApply(domain, range);
        if (getKind() == SimpleKind.K)
            return "(" + format + ")";
        return format;

    }

    public Type getConstructor() {
        Type t = domain;
        while (t instanceof TypeApplication)
            t = ((TypeApplication) t).getDomain();
        return t;
    }


    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof TypeApplication))
            return false;
        TypeApplication ta = (TypeApplication) obj;
        return domain.equals(ta.domain) && range.equals(ta.range);
    }

    /**
     * This method is called by checkKind to infer the kind of a domain of
     * an application.
     */
    public void setKind(Kind kind) {
        domain.setKind(new FunctionKind(range.getKind(), kind));
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.types.Type#setContext(jaskell.compiler.core.Expression)
     */
    public void setContext(TypeContext expression) {
        domain.setContext(expression);
        range.setContext(expression);
    }

    /* (non-Javadoc)
     * @see jaskell.compiler.types.Type#compare(jaskell.compiler.types.Type)
     */
    public int compare(Type other) throws UncomparableException {
        try {
            TypeApplication ta = (TypeApplication) other;
            if (this.getConstructor().compare(ta.getConstructor()) == 0)
                return Type.getComparatorFor(this.getConstructor()).compare(this, other);
            else
                throw new UncomparableException("Cannot compare " + this + " with " + other);
        } catch (ClassCastException ccex) {
			/* other is not a ta - maybe a variable */
            return other.compare(this);
        }
    }

}
