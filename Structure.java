package hm3;

import java.util.ArrayList;
import java.util.HashMap;

public class Structure {
		String pred = ""; //predicate
		ArrayList<String> con = new ArrayList<>(); // constant
		boolean isNegate ;

		public Structure(String pred, ArrayList<String> con, boolean isCon, boolean isNegate)
		{
			this.pred = pred;
			this.con = con;
			this.isNegate = isNegate;
		}
		public Structure(Structure str)
		{
			pred = str.pred;
			for (int i = 0; i < str.con.size(); i++) {
				con.add(str.con.get(i));
			}
			isNegate = str.isNegate;					
		}
		public Structure() {
			this.pred = null;
			this.con = null;
			this.isNegate = false;
		}
	    public boolean equal(Structure str) {
	    	boolean eq = false;
	    	if (pred == str.pred
	    		&& isNegate == str.isNegate
	    		&& con.size() == str.con.size()){
	    		for (int i = 0; i < str.con.size(); i++) {
	    		     if ((con.get(i).length() == 1 && str.con.get(i).length() == 1)
	    		    	 || (con.get(i).equals(str.con.get(i)))) {
	    		     } else {
	    		    	 return false;
	    		     }
	    		}
	    		return true;
	    	}
	    	return false;
	    }
	    public String toString() {
	    	String node = "";
	    	if (isNegate) {
	    		node = node + "~";
	    	}
	    	node = node + pred + "(" + con.get(0);
	    	for (int i = 1; i < con.size(); i++) {
	    		node = node + "," +  con.get(i);
	    	}
	    	node = node + ")";
			return node;
	    }
	    
}
