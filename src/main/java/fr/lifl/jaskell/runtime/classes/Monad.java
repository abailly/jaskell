package fr.lifl.jaskell.runtime.classes;

import fr.lifl.jaskell.runtime.types.Closure;
import fr.lifl.jaskell.runtime.types.JObject;

/**
 * @author bailly
 * @version $Id: Monad.java 1154 2005-11-24 21:43:37Z nono $
 *  */
public interface Monad {

	/**
	 * return :: a -> m a
	 */
	public Monad _return(JObject obj);

	/**
	 * (>>=) :: m a -> ( a -> m b) -> m b
	 */
	public Monad _3c_3c_3d(Monad m, Closure mon);

	/**
	 * (>>) :: m a ->  m b -> m b
	 */
	public Monad _3c_3c(Monad ma, Monad mb);

	/**
	 * fail :: String -> m a
	 */
	public Monad fail(String s);

}
