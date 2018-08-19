package calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExpressionCalculator {
	
	private static final Logger LOGGER = LogManager.getLogger(ExpressionCalculator.class);
	
	/*
	 * Using HashMap to store variable and their values set by 'let' command
	 */

	static HashMap<String, Double> variables = new HashMap<String, Double>();

	public double breakTheExpression(String expression) {
		// Storing chunks of expression in ArrayList
		
		LOGGER.debug("Inside breakTheExpression() method");
		
		ArrayList<String> expressionList = new ArrayList<String>();

		// let(a, let(b, 10, add(b, b)), let(b, 20, add(a, b)))
		/*
		 * The idea here is to keep the individual chunks of expression along with their
		 * nested expressions(sub-expressions). an operator and its nested expression
		 * are placed in the same position at first. Then those chunks are passed in the
		 * same function to parse
		 * 
		 */

		// Assuming the first element will always be an operator
		try {
			int startIndex = expression.indexOf('(');

			if (startIndex < 0) { // i.e; we are possibly dealing with individual variable. Find if it's value is
									// set in HashMap
				if (variables.get(expression) != null) {
					return variables.get(expression);
				}
				//return Integer.parseInt(expression);
				return Double.parseDouble(expression);
			}
			LOGGER.debug("Adding sub-expression to ArrayList: "+expression.substring(0, startIndex).trim());
			expressionList.add(expression.substring(0, startIndex).trim()); // First operator

			// Continuing loop from the index where first bracket was found

			int brackets = 0; // Keeping track of brackets count to check if inside or outside a
								// sub-expression

			for (int i = startIndex; i < expression.length(); i++) {
				if (expression.charAt(i) == '(') {
					brackets++;
				}
				if (expression.charAt(i) == ')') {
					brackets--;
				}
				if (expression.charAt(i) == ',' && brackets == 1 || brackets == 0) {
					// Add the substring to ArrayList when a ',' is encountered inside a bracket OR
					// outside brackets. But it shouldn't be inside of subexpression. Therefore,
					// checking for brackets == 0 || 1 
					LOGGER.debug("Adding sub-expression to ArrayList: "+expression.substring(startIndex + 1, i).trim());
					expressionList.add(expression.substring(startIndex + 1, i).trim());
					startIndex = i;
				}

				if (brackets < 0)
					return 0;
			}
		} catch (Exception e) {
			//e.printStackTrace();
			LOGGER.error(e.getMessage(), e);
		}
		return calculate(expressionList);
	}

	public double calculate(ArrayList<String> expressions) {

		LOGGER.debug("Inside calculate() method");
		double result = 0;
		
		String operator = expressions.get(0);
		LOGGER.debug("Operator: "+operator);
		
		if (operator.equalsIgnoreCase("mult")) {
			
			LOGGER.debug("expression 1: "+expressions.get(1));
			LOGGER.debug("expression 2: "+expressions.get(2));
			
			return breakTheExpression(expressions.get(1)) * breakTheExpression(expressions.get(2));
		} else if (operator.equalsIgnoreCase("add")) {
			
			LOGGER.debug("expression 1: "+expressions.get(1));
			LOGGER.debug("expression 2: "+expressions.get(2));
			
			return breakTheExpression(expressions.get(1)) + breakTheExpression(expressions.get(2));
		} else if (operator.equalsIgnoreCase("sub")) {
			
			LOGGER.debug("expression 1: "+expressions.get(1));
			LOGGER.debug("expression 2: "+expressions.get(2));
			
			return breakTheExpression(expressions.get(1)) - breakTheExpression(expressions.get(2));
		} else if (operator.equalsIgnoreCase("div")) {
			
			LOGGER.debug("expression 1: "+expressions.get(1));
			LOGGER.debug("expression 2: "+expressions.get(2));
			
			return breakTheExpression(expressions.get(1)) / breakTheExpression(expressions.get(2));
		} else if (operator.equalsIgnoreCase("let")) { // if operator is let, the next two elements would be a variable and its value
			
			String letVariable = expressions.get(1);
			Double value = variables.get(letVariable);
			variables.put(letVariable, breakTheExpression(expressions.get(2)));

			result = breakTheExpression(expressions.get(3));
			LOGGER.debug("Putting let variables in HashMap == variable: "+letVariable+", value: "+value);
			variables.put(letVariable, value);
		}
		LOGGER.debug("Calculation complete. Returning result");
		return result;
	}

	public boolean checkFormat(String expression) {

		int brackets = 0;
		boolean check = false;
		LOGGER.debug("Inside checkFormat() method");
		if(expression.length() == 0) {
			LOGGER.error("No expression provided");
			return check;
		}
		LOGGER.debug("Checking for bracket descripansy");
		for(int i = 0; i < expression.length(); i++) {
			if(expression.charAt(i) == '(') {
				brackets++;
			}else if(expression.charAt(i) == ')'){
				brackets--;
			}
		}
		if(brackets > 0 || brackets < 0) {
			LOGGER.error("Expression format is not proper. Check for missing or extra bracket");
			check = false;
		}else {
			check = true;
		}
		return check;
	}
	
	public static void main(String[] args) {
		LOGGER.info("Enter the expression in * add (1,2) * format");
		ExpressionCalculator cal = new ExpressionCalculator();
		LOGGER.debug("Calculator is running");
		Scanner scan = new Scanner(System.in);
		System.out.println("\nInsert an expression: ");
		String inputString = scan.nextLine();
		LOGGER.info("Checking the expression for validity");
		boolean isValid = cal.checkFormat(inputString);
		if(!isValid) {
			//System.out.println("Expression format is not proper. Check for missing or extra bracket.");
			System.exit(0);
		}else {
			double result = cal.breakTheExpression(inputString);
			if(result < Integer.MIN_VALUE || result > Integer.MAX_VALUE) {
				System.out.println("Result is not between Integer.MIN_VALUE and Integer.MAX_VALUE");
				System.exit(0);
			}
			System.out.println(result);
		}
		scan.close();
	}
}
