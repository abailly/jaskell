/*
 * Created on Feb 23, 2004
 */
package fr.lifl.jaskell.runtime.types;

/**
 * @author nono
 * @version $Log: Event.java,v $
 * @version Revision 1.1  2004/07/01 15:57:41  bailly
 * @version suppression de l'interface Namespace au profit de fr.lifl.parsing.Namespace
 * @version modification de la g?n?ration de code pour les constructeurs et les types de donnees
 * @version creation d'un type JEvent et d'un constructeur Event
 * @version modification du parser pour creer des Event lors de l'analyse syntaxique
 * @version
 * @version Revision 1.4  2004/06/29 15:25:26  bailly
 * @version modification des types pour les messages
 * @version
 * @version Revision 1.3  2004/04/15 14:26:15  bailly
 * @version added toString implementations for all types
 * @version
 * @version Revision 1.2  2004/03/11 14:53:44  bailly
 * @version *** empty log message ***
 * @version
 * @version Revision 1.1  2004/02/23 16:46:25  bailly
 * @version Added generation of MessagePattern from grammar
 * @version Fixed constant propagation -> methods are properly
 * @version resolved and called if they exist
 * @version
 */
public class Event extends JEvent {
  
	/**
	 * @param cnx
	 * @param dir
	 * @param name
	 * @param args
	 */
	public Event(String cnx, JMessage jmsg) {
		this._0 = cnx;
		this._1 = jmsg;
	}


	public String _0;
	
	public JMessage _1;
	

	/**
	 * 
	 */
	public Event() {		
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "{"+_0+'.'+_1+'}';
	}

}
