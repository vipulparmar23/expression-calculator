package calculator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

public class ExpressionCalculator {

	// Assuming the calculator returns integer value after performing divisions
	
	protected static Logger LOGGER = LogManager.getLogger(ExpressionCalculator.class);

	/*
	 * Using HashMap to store variable and their values set by 'let' command
	 */

	static HashMap<String, Integer> variables = new HashMap<String, Integer>();

	public int breakTheExpression(String expression) {
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
				// return Integer.parseInt(expression);
				return Integer.parseInt(expression);
			}
			LOGGER.debug("Adding sub-expression to ArrayList: " + expression.substring(0, startIndex).trim());
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
					LOGGER.debug(
							"Adding sub-expression to ArrayList: " + expression.substring(startIndex + 1, i).trim());
					expressionList.add(expression.substring(startIndex + 1, i).trim());
					startIndex = i;
				}

				if (brackets < 0)
					//LOGGER.error();
					return Integer.MIN_VALUE;
			}
		} catch (Exception e) {
			// e.printStackTrace();
			LOGGER.error(e.getMessage(), e);
		}
		LOGGER.debug("Passing the expressionList to calculate() method");
		return calculate(expressionList);
	}

	public int calculate(ArrayList<String> expressions) {

		LOGGER.debug("Inside calculate() method");
		int result = 0;

		String operator = expressions.get(0);
		LOGGER.debug("Operator: " + operator);

		if (operator.equalsIgnoreCase("mult")) {

			LOGGER.debug("expression 1: " + expressions.get(1));
			LOGGER.debug("expression 2: " + expressions.get(2));

			return breakTheExpression(expressions.get(1)) * breakTheExpression(expressions.get(2));
		} else if (operator.equalsIgnoreCase("add")) {

			LOGGER.debug("expression 1: " + expressions.get(1));
			LOGGER.debug("expression 2: " + expressions.get(2));

			return breakTheExpression(expressions.get(1)) + breakTheExpression(expressions.get(2));
		} else if (operator.equalsIgnoreCase("sub")) {

			LOGGER.debug("expression 1: " + expressions.get(1));
			LOGGER.debug("expression 2: " + expressions.get(2));

			return breakTheExpression(expressions.get(1)) - breakTheExpression(expressions.get(2));
		} else if (operator.equalsIgnoreCase("div")) {

			LOGGER.debug("expression 1: " + expressions.get(1));
			LOGGER.debug("expression 2: " + expressions.get(2));

			return breakTheExpression(expressions.get(1)) / breakTheExpression(expressions.get(2));
		} else if (operator.equalsIgnoreCase("let")) { // if operator is let, the next two elements would be a variable
														// and its value

			String letVariable = expressions.get(1);
			Integer value = variables.get(letVariable);
			variables.put(letVariable, breakTheExpression(expressions.get(2)));

			result = breakTheExpression(expressions.get(3));
			LOGGER.debug("Putting let variables in HashMap == variable: " + letVariable + ", value: " + value);
			variables.put(letVariable, value);
		}
		LOGGER.debug("Calculation complete. Returning result");
		return result;
	}

	public boolean checkFormat(String expression) {

		int brackets = 0;
		boolean checkResult = false;
		int dots = 0;

		LOGGER.debug("Inside checkFormat() method");
		if (expression.length() == 0) {
			LOGGER.error("No expression provided");
			return false;
		}
		LOGGER.debug("Checking for bracket descripansy and non-integer values");
		for (int i = 0; i < expression.length(); i++) {
			if (expression.charAt(i) == '(') {
				brackets++;
			} else if (expression.charAt(i) == ')') {
				brackets--;
			} else if (expression.charAt(i) == '.') {
				dots++;
			}
		}
		if (brackets > 0 || brackets < 0) {
			LOGGER.error("Expression format is not proper. Check for missing or extra bracket");
			checkResult = false;
		} else if (dots > 0) {
			LOGGER.error("Expression contains non-integer values");
			checkResult = false;
		} else {
			checkResult = true;
		}
		return checkResult;
	}

	public void setLogLevel(String logLevel) {

		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();
		LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);

		switch (logLevel.toLowerCase()) {
			case "error":
				loggerConfig.setLevel(Level.ERROR);
				break;
			case "info":
				loggerConfig.setLevel(Level.INFO);
				break;
			case "debug":
				loggerConfig.setLevel(Level.DEBUG);
				break;
			default:
				LOGGER.error("Invalid log level");
				throw new RuntimeException();
		}
		ctx.updateLoggers();
	}

	public static void main(String[] args) {

		LOGGER.info("Enter the expression in * add (1,2) * format");

		ExpressionCalculator cal = new ExpressionCalculator();

		LOGGER.debug("Calculator is running");

		Scanner scan = new Scanner(System.in);
		System.out.println("\nInsert an expression: ");
		String inputString = scan.nextLine();

		System.out.println("\nInsert log level: ");
		String logLevel = scan.nextLine();
		cal.setLogLevel(logLevel);

		LOGGER.info("Checking the expression for validity");

		boolean isValid = cal.checkFormat(inputString);
		if (!isValid) {
			// System.out.println("Expression format is not proper. Check for missing or
			// extra bracket.");
			System.exit(0);
		} else {
			int result = cal.breakTheExpression(inputString);
			if (result < Integer.MIN_VALUE || result > Integer.MAX_VALUE) {
				System.out.println("Result is not between Integer.MIN_VALUE and Integer.MAX_VALUE");
				System.exit(0);
			}
			System.out.println("\n" + result);
		}
		scan.close();
	}
}
