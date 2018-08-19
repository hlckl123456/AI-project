package hm3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class homework {
	static ArrayList<String> results = new ArrayList<>(); //results
	static ArrayList<Structure> queryList = new ArrayList<>();
	static ArrayList<ArrayList<Structure>> clauseList = new ArrayList<>();
	// when some when all the precondition are fullfilled, we take the result as a solved precondition.
	// use cTors to find the position of this result as precondition
	static HashMap<String, ArrayList<String>> cTors; // precondition to result
	static HashMap<String, ArrayList<ArrayList<Structure>>> rsToc; // result to precondition
	static Set<String> visited;
	private static final String FALSE = "FALSE";
	private static final String TRUE = "TRUE";
	private static boolean end = false;
	
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		// separate predicate and element and make it into a structure
		readInputFile(); 
		for (int i = 0; i < queryList.size(); i++) {	
			cTors = new HashMap<>();
			rsToc = new HashMap<>();
			visited = new HashSet<String>();
			System.out.println("--------------------new beginning----------------------");
			String result = FALSE;
			Queue<Structure> plan = new LinkedList<>();
			plan.offer(queryList.get(i));
			System.out.println("add:" +queryList.get(i).toString());
			rsToc.put("1", new ArrayList<ArrayList<Structure>>());
			rsToc.get("1").add(new ArrayList<Structure>());
			rsToc.get("1").get(0).add(queryList.get(i));
			cTors.put(queryList.get(i).toString(), new ArrayList<String>());
			cTors.get(queryList.get(i).toString()).add("1");
			visited.add(queryList.get(i).toString());
			forwardChaining(plan, result);
			
		}
		outPutFile();
	}
	private static void outPutFile() throws IOException {
		File writename = new File("output.txt");
		writename.createNewFile();
		BufferedWriter out = new BufferedWriter(new FileWriter(writename));
		
		for (int i = 0; i < results.size(); i++){
			out.write(results.get(i));
			out.write("\n");
		}
		out.flush();
		out.close();
	}
	private static void forwardChaining(Queue<Structure> plan, String result) {
		while(!plan.isEmpty()) {
			// validate
			Structure goal = plan.poll();
			// use each goal to match the operator in clauseList and operatorList
			if (MatchAndResolution(plan, goal) && end) {	
				result = TRUE;
				break;
			}
		}
		results.add(result);
		end = false;
	}
	private static ArrayList<Structure> readInputFile() throws NumberFormatException, IOException{
		// parse input.txt
		String pathname = "input.txt";
		File filename = new File(pathname);
		InputStreamReader reader =new InputStreamReader(
				new FileInputStream(filename));
		// create an object and transfer the content that computer can understand
		BufferedReader br = new BufferedReader(reader);
		String query = "";
		String clause = "";
		// Structure query
		int querySize = Integer.parseInt(br.readLine());
		for (int i = 0; i < querySize; i++) {
			query = br.readLine();
			Structure curt = tokenize(query);
			queryList.add(curt);
		}
		// Structure clause
		int clauseSize = Integer.parseInt(br.readLine());
		for (int i = 0; i < clauseSize; i++) {
			clause = br.readLine();
			String[] clouse = clause.split(" \\| ");
			ArrayList<Structure> curt = new ArrayList<>();
			for (int j = 0; j < clouse.length; j++) {
				curt.add(tokenize(clouse[j]));
			}
			clauseList.add(curt);
		}				
		return null;
	}
	private static Structure tokenize (String query) {
		ArrayList<String> con = new ArrayList<String>();
		Structure curt =  new Structure();
		String content = query;
		if (query.contains("~")) {
			String[] q1 = query.split("~");
			// add isNegate
			curt.isNegate = true;
			content = q1[1];
		}
		String[] q2 = content.split("\\(");
		// add pred
		curt.pred = q2[0];
		content = q2[1];
		if (content.contains(",")) {
			String[] q3 = content.split("\\,");
			for (int i = 0; i < q3.length - 1; i++) {
				con.add(q3[i]);
			}
			content = q3[q3.length - 1];
		}
		con.add(content.split("\\)")[0]);
		curt.con = con;
		return curt;
	}
	private static void unify(ArrayList<Structure> clause, Structure goal, int clsIndex, Queue<Structure> plan) {
		System.out.println("hello");
    	HashMap<String, String> clsMap = new HashMap<>(); 
    	Structure clsElem = clause.get(clsIndex);
    	for (int i = 0; i < clsElem.con.size(); i++) {
    		if (clsElem.con.get(i).length() == 1) {
    			if (goal.con.get(i).length() != 1) {
    				clsMap.put(clsElem.con.get(i), goal.con.get(i));
    			}
    		} 
    	}
    	// unify element, and to plan, visited
    	// establish rs to c and c to rs
    	ArrayList<Structure> tt = new ArrayList<>();
    	for (int i = 0; i < clause.size(); i++) {
    		if (i == clsIndex) {
    			System.out.println("remove:"+ clause.get(i).toString());
    			clause.remove(i);
    			continue;
    		}
    		Structure temp = clause.get(i);
    		for (int j = 0; j < temp.con.size(); j++) {
    			if (clsMap.containsKey(temp.con.get(j))) {
    				temp.con.set(j, clsMap.get(temp.con.get(j)));
    			} 
    		}	
    	} 
	}
	private static boolean unifyAndResolution(ArrayList<Structure> clause, Structure goal, int clsIndex, Queue<Structure> plan) {
    	// Unify, only consider 1-n situation, 1-1 is solved at match
		System.out.println("hello");
    	HashMap<String, String> clsMap = new HashMap<>(); 
    	HashMap<String, String> goalMap = new HashMap<>(); 
    	Structure clsElem = clause.get(clsIndex);
    	for (int i = 0; i < clsElem.con.size(); i++) {
    		if (clsElem.con.get(i).length() == 1) {
    			if (goal.con.get(i).length() != 1) {
    				clsMap.put(clsElem.con.get(i), goal.con.get(i));
    			}
    		} 
    		if (goal.con.get(i).length() == 1) {
    			if (clsElem.con.get(i).length() != 1) {
    				goalMap.put(goal.con.get(i), clsElem.con.get(i));
    			}
    		} 
    	}
    	
    	for (int i = 0; i < clsElem.con.size(); i++) {
    		String c = clsMap.containsKey(clsElem.con.get(i)) ? clsMap.get(clsElem.con.get(i)) : clsElem.con.get(i);
    		String g = goalMap.containsKey(goal.con.get(i)) ? goalMap.get(goal.con.get(i)) : goal.con.get(i);
    		if (!c.equals(g) && c.length() != 1 && g.length() != 1) {
    			System.out.println("heyheyheyheyheyheyheyheyheyheyheyheyheyheyheyheyheyhey");
    			return false;
    		}
    	}
    	
    	// unify element, and to plan, visited
    	// establish rs to c and c to rs
    	ArrayList<Structure> tt = new ArrayList<>();
    	String rs = goal.toString();
    	if (rsToc.get(rs) == null) {
			rsToc.put(rs, new ArrayList<ArrayList<Structure>>());			
				rsToc.get(rs).add(new ArrayList<Structure>());
		}
    	for (int i = 0; i < clause.size(); i++) {
    		if (i == clsIndex) {	
    			continue;
    		}
    		Structure temp = new Structure(clause.get(i));
    		for (int j = 0; j < temp.con.size(); j++) {
    			if (clsMap.containsKey(temp.con.get(j))) {
    				temp.con.set(j, clsMap.get(temp.con.get(j)));
    			} 
    		}
    		temp.isNegate = !temp.isNegate;
    		if (!visited.contains(temp.toString())){
    			plan.add(temp);
    			System.out.println("Add:" + temp.toString());
    			visited.add(temp.toString());    			   			
//    			System.out.println("Add:"+temp.toString());
//    			System.out.println("Addre:"+cTors.get(c).get(0));
    		}
			tt.add(temp);
			String c = temp.toString();
			if (cTors.get(c) == null) {
				cTors.put(c, new ArrayList<String>());
			}
			cTors.get(c).add(rs);
    	} 
    	rsToc.get(rs).add(tt);
    	return false;
    }
	private static boolean canResolve (ArrayList<Structure> clause, Structure goal, int clsIndex) {
		System.out.println("come in ??????");
		HashMap<String, String> clsMap = new HashMap<>(); 
    	HashMap<String, String> goalMap = new HashMap<>(); 
    	Structure clsElem = clause.get(clsIndex);
    	for (int i = 0; i < clsElem.con.size(); i++) {
    		if (clsElem.con.get(i).length() == 1) {
    			if (goal.con.get(i).length() != 1) {
    				clsMap.put(clsElem.con.get(i), goal.con.get(i));
    			}
    		} 
    		if (goal.con.get(i).length() == 1) {
    			System.out.println(goal.con.get(i));
    			System.out.println(clsElem.con.get(i));
    			if (clsElem.con.get(i).length() != 1) {
    				System.out.println(clsElem.con.get(i));
    				goalMap.put(goal.con.get(i), clsElem.con.get(i));
    			}
    		} 
    	}
    	
    	for (int i = 0; i < clsElem.con.size(); i++) {
    		String c = clsMap.containsKey(clsElem.con.get(i)) ? clsMap.get(clsElem.con.get(i)) : clsElem.con.get(i);
    		String g = goalMap.containsKey(goal.con.get(i)) ? goalMap.get(goal.con.get(i)) : goal.con.get(i);
    		System.out.println(g + "" + c);
    		if (!c.equals(g) && c.length() != 1 && g.length() != 1) {
    			System.out.println("heyheyheyheyheyheyheyheyheyheyheyheyheyheyheyheyheyhey");
    			return false;
    		}
    	}
    	return true;
	}
    private static boolean MatchAndResolution(Queue<Structure> plan, Structure goal){
    	System.out.println("goal:"+goal.toString()+" "+goal.pred + goal.isNegate);
    	for (int i = 0; i < clauseList.size(); i++) {
    		ArrayList<Structure> curtClause = clauseList.get(i);
    		for (int j = 0; j < curtClause.size(); j++) {
    			boolean match = false;
    			Structure curtElem = curtClause.get(j);
    			System.out.println("curtElem:"+curtElem.toString()+ " "+curtElem.pred + curtElem.isNegate);
    			if (curtElem.pred.equals(goal.pred)
    				&& curtElem.isNegate == goal.isNegate) {
    				System.out.println("come in");
    				for (int h = 0; h < goal.con.size(); h++) {
						if (curtElem.con.get(h).equals(goal.con.get(h))
							|| curtElem.con.get(h).length() == 1
							|| goal.con.get(h).length() == 1) {
						} else {
							break;
						}	
						if (h == goal.con.size() - 1) {
							match = true;
						}
					}
    				boolean transferable = true;
    				if(match) {
						int m = 0;
						for (int h = 0; h < goal.con.size(); h++) {
							if (curtElem.con.get(h).length() == 1
								&& goal.con.get(h).length() == 1) {
								m++;
							}
							if (m == goal.con.size()) {
								transferable =  false;
							}
						}	
					}
    				if (match) {
    					System.out.println("match");
    					if (curtClause.size() == 1 && transferable && canResolve(curtClause, goal, j)) {
    						if (backTrack(goal.toString(), plan)) {
    							return true;
    						}
    					} else if (curtClause.size() != 1){
    						unifyAndResolution(curtClause, goal, j, plan);
    					}
					}
    			}
    		}
    	}   	
    	return false;
    }
    private static boolean backTrack(String find, Queue<Structure> plan) {
    	ArrayList<String> rs = cTors.get(find);
    	for (int i = 0; i < rs.size(); i++) {
    		String curt = rs.get(i);
    		if (curt.equals("1")) {
    			System.out.println("end point:"+find);
        		end = true;
        		return true;
    		}
    		ArrayList<ArrayList<Structure>> cl = rsToc.get(curt);
    		for (int j = 0; j < cl.size(); j++) {
    			ArrayList<Structure> c = cl.get(j);
    			for (int h = 0; h < c.size(); h++) {
    				Structure e = c.get(h);
    				if (e.toString().equals(find)) {
    					if (c.size() == 1 ) {
    						System.out.println("remove:" + c.get(0).toString());
    						cl.remove(j);
    						backTrack(curt, plan);
    					} else {
    						for (int k = 0; k < c.size(); k++) {
    							if (c.get(k).toString().equals(find)) {
    								unify(c, tokenize(find), k, plan);
    							}    							
    						}
    					}
    				}
    			}
    		}
    				
    	}
    	return true;
    }
}






