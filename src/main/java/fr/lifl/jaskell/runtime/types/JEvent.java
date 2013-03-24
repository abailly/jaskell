/*
 * Created on Jul 1, 2004
 * 
 */
package fr.lifl.jaskell.runtime.types;

/**
 * Type for Events
 * 
 * @author nono
 * @version $Id: JEvent.java 1153 2005-11-24 20:47:55Z nono $
 */
public abstract class JEvent extends JValue {

}

/* 
 * $Log: JEvent.java,v $
 * Revision 1.1  2004/07/01 15:57:41  bailly
 * suppression de l'interface Namespace au profit de fr.lifl.parsing.Namespace
 * modification de la g?n?ration de code pour les constructeurs et les types de donnees
 * creation d'un type JEvent et d'un constructeur Event
 * modification du parser pour creer des Event lors de l'analyse syntaxique
 *
*/