package apps;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                
    
	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   
	
	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;
    
    /**
     * String containing all delimiters (characters other than variables and constants), 
     * to be used with StringTokenizer
     */
    public static final String delims = " \t*+-/()[]";
    
    /**
     * Initializes this Expression object with an input expression. Sets all other
     * fields to null.
     * 
     * @param expr Expression
     */
    public Expression(String expr) {
        this.expr = expr;
    }

    /**
     * Populates the scalars and arrays lists with symbols for scalar and array
     * variables in the expression. For every variable, a SINGLE symbol is created and stored,
     * even if it appears more than once in the expression.
     * At this time, values for all variables are set to
     * zero - they will be loaded from a file in the loadSymbolValues method.
     */
    public void buildSymbols() 
    {
    	arrays = new ArrayList<ArraySymbol>(); //creates a new array list of array symbols
    	scalars = new ArrayList<ScalarSymbol>(); //creates a new array list of scalar symbols
    	Stack <String> symbols = new Stack<String>(); // creates a new Stack of symbols
    	StringTokenizer st = new StringTokenizer(expr, delims, true); //creates a new StringTokenizer, that will separate each phrase in a String
    	String token = ""; //initializes token
    	
    	while (st.hasMoreTokens()) //checks if there are any remaining tokens left in the tokenizer
    	{
    		token = st.nextToken();
    		if ((token.charAt(0) >= 'a' && token.charAt(0) <= 'z') || (token.charAt(0) >= 'A' && token.charAt(0) <= 'Z' || token.equals("[")))
    		{
    			//if the token is equal to any letter or an '[' it will push it into the stack of symbols
    			symbols.push(token);
    		}
    	}
    	while (!symbols.isEmpty()) //loops until the stack is empty
    	{
    		token = symbols.pop();
    		if (token.equals("["))
    		{
    			//separates all the array symbols and adds them to the arraylist of array symbols 
    			token = symbols.pop();
    			ArraySymbol asymbol = new ArraySymbol(token);
    			if(arrays.indexOf(asymbol) == -1)
    			{
    				arrays.add(asymbol);
    			}
    		}
    		else
    		{
    			//separates all the scalar variables and adds them to a arraylist of scalar symbols
    			ScalarSymbol ssymbol = new ScalarSymbol(token);
    			if (scalars.indexOf(ssymbol) == -1)
    			{
    				scalars.add(ssymbol);
    			}
    		}
    	}
    }
    
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    
    
    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions.
     * 
     * @return Result of evaluation
     */
    public float evaluate() 
    {
    	return recursEval(expr);	
    }
    private float recursEval(String s)
    {
    	int counterOpen = 0, counterClose = 0, frontIndex = 0, indexCounter = 0;
		String currentResult; 
		while(s.contains("("))
		{
			counterOpen = 1; counterClose = 0;
			frontIndex = s.indexOf("(");
			indexCounter = frontIndex + 1; 
			while(counterOpen != counterClose)
			{ 
				//this while loops creates a subexpression that exists between the first 2 open/closed parantheses
				if(s.charAt(indexCounter) == '(')
				{
					counterOpen++;
				}
				else if(s.charAt(indexCounter) == ')')
				{
					counterClose++;
				}
				indexCounter++;
			}
			currentResult = Float.toString(recursEval(s.substring(frontIndex+1, indexCounter-1))); //calls the same method on that subexpression
			s = s.substring(0, frontIndex) + currentResult + s.substring(indexCounter);
		}
		while(s.contains("["))
		{
			//for the first subexpression, goes through this loop and created another 
			//subexpression between the open/closed square brackets
			counterOpen = 1; counterClose = 0;
			frontIndex = s.indexOf("[");
			indexCounter = frontIndex + 1;
			while(counterOpen != counterClose)
			{
				if(s.charAt(indexCounter) == '[')
				{
					counterOpen++;
				}
				else if(s.charAt(indexCounter) == ']')
				{
					counterClose++;
				}
				indexCounter++;
			}
			currentResult = Float.toString(recursEval(s.substring(frontIndex+1, indexCounter-1)));
			s = s.substring(0, frontIndex) + " "+ currentResult + s.substring(indexCounter);
		}
		Stack<Float> num = new Stack<Float>(); //subexpression that is between the first 2 square brackets is the first to do this
		Stack<String> op = new Stack<String>();
		StringTokenizer numToken = new StringTokenizer(s,delims); 
		String allCharacters = " .()[]0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"; //this is for the next tokenizer to delim all these characters
		StringTokenizer opToken = new StringTokenizer(s,allCharacters);
		String newOp;
		float result = 0;
		float a = 0; //variable to push into specific stacks
		int x = 0;
		String name;
		boolean moveFoward = true, negative = false;
		while(numToken.hasMoreTokens())
		{
			if(moveFoward)
			{
				name = numToken.nextToken();
				if(name.charAt(0)=='0' || name.charAt(0)=='1' || name.charAt(0)=='2' || name.charAt(0)=='3' || name.charAt(0)=='4' || name.charAt(0)=='5' || name.charAt(0)=='6' || name.charAt(0)=='7' || name.charAt(0)=='8' || name.charAt(0)=='9')
				{
					//will check if the token is one of these number values and push it to a stack of num
					a = Float.parseFloat(name);
					if(negative)
					{
						a *= -1;
						negative = false;
					}
					num.push(a);
				}
				else
				{
					x=0;
					while(x < scalars.size())
					{
						if(name.equals(scalars.get(x).name))
						{
							//if name is equal to the name of the scalar variable it will push it to the stack of numbers
							a = scalars.get(x).value;
							if(negative)
							{
								a *= -1;
								negative = false;
							}
							num.push(a);
							break;
						}
						x++;
					}
					x = 0;
					while(x < arrays.size())
					{
						if(name.equals(arrays.get(x).name))
						{
							//if the values in the array are equal, then it will push the values into the stack of numbers
							String t = numToken.nextToken();
							int p = t.indexOf('.'); 
							String b = t.substring(0, p);
							p = Integer.parseInt(b);
							a = arrays.get(x).values[p];
							if(negative)
							{
								a *= -1;
								negative = false;
							}
							num.push(a);
							break;
						}
						x++;
					}
				}
			}
			boolean skip = !moveFoward;
			moveFoward = true;
			if(opToken.hasMoreTokens())
			{
				x = 0;
				newOp = opToken.nextToken();
				if(newOp.equals("*"))
				{
					//does the multiply operation
					name = numToken.nextToken();
					if(name.charAt(0)=='0' || name.charAt(0)=='1' || name.charAt(0)=='2' || name.charAt(0)=='3' || name.charAt(0)=='4' || name.charAt(0)=='5' || name.charAt(0)=='6' || name.charAt(0)=='7' || name.charAt(0)=='8' || name.charAt(0)=='9')
					{
						a = Float.parseFloat(name);
					}
					else{ 
						while(x < scalars.size())
						{
							if(name.equals(scalars.get(x).name))
							{
								a = scalars.get(x).value;
								break;
							}
							x++;
						}
					}
					num.push(num.pop()* a);
					moveFoward = false;
				}
				else if(newOp.equals("/"))
				{
					//does the divide operation
					name = numToken.nextToken();
					if(name.charAt(0)=='0' || name.charAt(0)=='1' || name.charAt(0)=='2' || name.charAt(0)=='3' || name.charAt(0)=='4' || name.charAt(0)=='5' || name.charAt(0)=='6' || name.charAt(0)=='7' || name.charAt(0)=='8' || name.charAt(0)=='9')
					{
						System.out.println(a);
						a = Float.parseFloat(name);
					}
					else
					{
						while(x < scalars.size())
						{
							if(name.equals(scalars.get(x).name))
							{
								a = scalars.get(x).value;
								break;
							}
							x++;
						}
					}
					num.push(num.pop()/a);
					moveFoward = false; 
				}
				else
				{
					if(skip && newOp.equals("-"))
					{
						negative = true;
						op.push("+");
					}
					else
					{
						op.push(newOp);
					}
				}
			}
		}
		float g = 0;
		float i = 0;
		if(num.size() == 1)
		{ 
			result = num.pop();
			return result;
		}
		String operator;
		while(!num.isEmpty() && !op.isEmpty())
		{
			if(num.size() > 1)
			{ 
				//adds or subtracts the first two numbers in the stack
				i = num.pop(); 
				g = num.pop();
				operator = op.pop();
				if(operator.equals("+"))
				{ 
					result = result + g + i;
				}
				else if(operator.equals("-")) 
				{ 
					result = result + g - i;
				}
			}
			else
			{
				//adds or subtracts a number to the top number in the stack
				g = num.pop();
				operator = op.pop();
				if(operator.equals("+"))
				{
					result = result + g;
				}
				else if(operator.equals("-"))
				{
					result = result - g;
				}
			}
		}
		return result; //returns to evaluate method which returns a float value
    		
    }
    /**
     * Utility method, prints the symbols in the scalars list
     */
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    		for (ArraySymbol as: arrays) {
    			System.out.println(as);
    		}
    }

}
