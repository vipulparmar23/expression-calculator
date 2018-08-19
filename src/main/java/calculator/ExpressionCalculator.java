package calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ExpressionCalculator {

	/*
	 * Using HashMap to store variable and their values set by 'let' command
	 */

	static HashMap<String, Double> variables = new HashMap<String, Double>();

	public double breakTheExpression(String expression) {
		// Storing chunks of expression in ArrayList
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

					expressionList.add(expression.substring(startIndex + 1, i).trim());
					startIndex = i;
				}

				if (brackets < 0)
					return 0.001;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return calculate(expressionList);
	}

	public double calculate(ArrayList<String> expressions) {

		double result = 0;

		if ("mult".equals(expressions.get(0))) {
			return breakTheExpression(expressions.get(1)) * breakTheExpression(expressions.get(2));
		} else if ("add".equals(expressions.get(0))) {
			return breakTheExpression(expressions.get(1)) + breakTheExpression(expressions.get(2));
		} else if ("sub".equals(expressions.get(0))) {
			return breakTheExpression(expressions.get(1)) - breakTheExpression(expressions.get(2));
		} else if ("div".equals(expressions.get(0))) {
			return breakTheExpression(expressions.get(1)) / breakTheExpression(expressions.get(2));
		} else if ("let".equals(expressions.get(0))) {
			String variableName = expressions.get(1);
			Double value = variables.get(variableName);
			variables.put(variableName, breakTheExpression(expressions.get(2)));

			result = breakTheExpression(expressions.get(3));

			variables.put(variableName, value);
		}
		return result;
	}

	public boolean checkFormat(String expression) {

		int brackets = 0;
		boolean check = false;
		
		if(expression.length() == 0) {
			System.out.println("No input provided");
			return check;
		}
		
		for(int i = 0; i < expression.length(); i++) {
			if(expression.charAt(i) == '(') {
				brackets++;
			}else if(expression.charAt(i) == ')'){
				brackets--;
			}
		}
		if(brackets > 0 || brackets < 0) {
			System.out.println("Expression format is not proper. Check for missing or extra bracket");
			check = false;
		}else {
			check = true;
		}
		return check;
	}
	
	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		System.out.println("Insert an expression: ");
		String inputString = scan.nextLine();

		ExpressionCalculator cal = new ExpressionCalculator();

		boolean isValid = cal.checkFormat(inputString);
		if(!isValid) {
			//System.out.println("Expression format is not proper. Check for missing or extra bracket.");
			System.exit(0);
		}else {
			double result = cal.breakTheExpression(inputString);
			System.out.println(result);
		}
		scan.close();
	}
}
