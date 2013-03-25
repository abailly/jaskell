package fr.lifl.jaskell.compiler.types;

public class TypeClass {
    private final String className;

    public TypeClass(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return className;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeClass typeClass = (TypeClass) o;

        if (className != null ? !className.equals(typeClass.className) : typeClass.className != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return className != null ? className.hashCode() : 0;
    }
}