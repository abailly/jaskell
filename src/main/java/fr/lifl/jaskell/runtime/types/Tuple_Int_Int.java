package fr.lifl.jaskell.runtime.types;

/**
 * @author bailly
 * @version $Id: Tuple_Int_Int.java 1153 2005-11-24 20:47:55Z nono $
 *  */
public class Tuple_Int_Int extends Tuple_2 {

		private int i1,i2;
		
		public Tuple_Int_Int(int a,int b) {
			this.i1 = a;
			this.i2 = b;
		}						

		public Tuple_Int_Int(JInt a,JInt b) {
			super(a,b);
		}						
		
		
		public JObject fst() {
			return new JInt(i1);
		}
		
		public JObject snd() {
			return new JInt(i2);
		}
		
		/**
		 * Unboxed first projection
		 * 
		 * @return an int
		 */
		public int fst_int() {
			return i1;
		}
		
		/**
		 * Unboxed second projection
		 * 
		 * @return an int
		 */
		public int snd_int() {
			return i2;
		}
		
			
}
