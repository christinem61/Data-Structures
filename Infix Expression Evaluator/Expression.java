package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression { //Christine Mathews

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * DO NOT create new vars and arrays - they are already created before being sent in
    	 to this method - you just need to fill them in.
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	String toAdd="";
    	for (int i=0;i<expr.length();i++) {
    		char c=expr.charAt(i);
    		if (Character.isLetter(c)) {
    			toAdd=toAdd+c;}
    		else{
    			if (c=='[') {           //store in arr
    				if(toAdd!="") {
    					Array addarr=new Array(toAdd);boolean repeat=false;
    					for(int j=0;j<arrays.size();j++) {
    						if (addarr.equals(arrays.get(j)))
    							repeat=true;}
    					if (!repeat)
    						arrays.add(addarr);
    				}
    				toAdd="";
    			}
    			else {                 //store in var
    				if(toAdd!="") {
    					Variable addvar=new Variable(toAdd);boolean repeat=false;
    					for(int j=0;j<vars.size();j++) {
    						if (addvar.equals(vars.get(j)))
    							repeat=true;}
    					if (!repeat)
    						vars.add(addvar);
    				}
    				toAdd="";
    			}
    		}	
    	}
    	if (toAdd!="") {			  //when end of string is reached & all vars haven't been added
    		Variable addvar=new Variable(toAdd);boolean repeat=false;
			for(int j=0;j<vars.size();j++) {
				if (addvar.equals(vars.get(j)))
					repeat=true;}
			if (!repeat)
				vars.add(addvar);
    	}
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                while (st.hasMoreTokens()) {// following are (index,val) pairs
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    private static boolean isANum(String str) {
    	try {  
		    Float.parseFloat(str);  
		    return true;
		  } catch(NumberFormatException e){  
		    return false;  
		  }  
    }
    private static int isAVar(String str,ArrayList<Variable>vars) {
    	for (int i=0;i<vars.size();i++) {
    		if((vars.get(i).name.equals(str)))
    			return i;
    		}return -1;
    }
    private static int isAnArr(String str,ArrayList<Array>arrays) {
    	for (int i=0;i<arrays.size();i++) {
    		if((arrays.get(i).name.equals(str)))
    			return i;
    		}return -1;
    }
    private static boolean pemdas(String curr,String old) {
    	if (old.equals("(")||old.equals("["))
    		return false;
    	else if ((curr.equals("*")||curr.equals("/"))&&(old.equals("+")||old.equals("-")))
    		return false;
    	return true;
    }
    private static float math(Float x, Float y, String z) {
		if (z.equals("+")) 
			return x+y;
		else if (z.equals("-")) 
			return y-x;
		else if (z.equals("*")) 
			return x*y;
		else if (z.equals("/")) 
			return y/x;
		return 0;
    }
    public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	expr=expr.replaceAll(" ","");
    	expr=expr.replaceAll("\t","");
    	Stack<Float> nums = new Stack<>();
     	Stack<String> ops = new Stack<>();
     	StringTokenizer broken = new StringTokenizer(expr, delims, true);
    	while(broken.hasMoreElements()) {
    		String elem=broken.nextToken();
    		if(isANum(elem)) 
    			nums.push(Float.parseFloat(elem));
    		else if(isAVar(elem,vars)!=-1) 
    			nums.push((float)vars.get(isAVar(elem,vars)).value);
    		else if (isAnArr(elem,arrays)!=-1)
    			ops.push(elem);
    		else if (elem.equals("+")||elem.equals("-")||elem.equals("*")||elem.equals("/")||elem.equals("(")||elem.equals("[")) {
    				while (!ops.isEmpty()&&pemdas(elem,ops.peek())&&!elem.equals("(")&&!elem.equals("[")) {
    					float x=nums.pop();float y=nums.pop();String z=ops.pop();
    					nums.push(math(x,y,z));
    				}
    				ops.push(elem);
    		}
    		else if (elem.equals(")")) {
    			while(!ops.peek().equals("(")) {
    				float x=nums.pop();float y=nums.pop();String z=ops.pop();
    				nums.push(math(x,y,z));
    			}
    			ops.pop();
    		}
    		else if (elem.equals("]")) {
    			while(!ops.peek().equals("[")) {
    				float x=nums.pop();float y=nums.pop();String z=ops.pop();
    				nums.push(math(x,y,z));
    			}
    			ops.pop();
    			float in=nums.pop(),fin=-1;
    			int index=(int) in;
    			String arr=ops.pop();
    			for (int k=0;k<arrays.size();k++) {
    				if (arrays.get(k).name.equals(arr)) {
    					Array x=arrays.get(k);
    					fin=(float)x.values[index];
    					break;}
    			}
    			nums.push(fin);
    		}
    	}
    	while (!ops.isEmpty()) {
    		float x=nums.pop(),y=nums.pop();String z=ops.pop();
    		nums.push(math(x,y,z));
    	}
    	return nums.pop();
    }
}
