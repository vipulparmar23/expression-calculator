package assignmentTest;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.junit.Test;

import calculator.ExpressionCalculator;

public class ExpressionCalculatorTest {

	ExpressionCalculator calTest = new ExpressionCalculator();

	/*
	 * Testing checkFormat() method with valid and invalid input expressions
	 */
	
	@Test
	public void testCheckFormat1() { // valid expression
		String expression = "add(1,2)";
		// boolean expectedResult = true;
		boolean actualResult = calTest.checkFormat(expression);
		assertTrue(actualResult);
	}

	@Test
	public void testCheckFormat2() { // Expression with missing comma
		String expression = "add(1,2";
		boolean actualResult = calTest.checkFormat(expression);
		assertFalse(actualResult);
	}

	@Test
	public void testCheckFormat3() { // Expression with decimal values
		String expression = "add(1,2.3)";
		boolean actualResult = calTest.checkFormat(expression);
		assertFalse(actualResult);
	}

	/*
	 * Testing setLogLevel() method if it really sets the root logger level 
	 */
	
	@Test
	public void testSetLogLevel1() {
		String expectedResult = "ERROR";
		calTest.setLogLevel("error");
		String actualResult = LogManager.getRootLogger().getLevel().toString();
		assertEquals(expectedResult, actualResult);
	}
	
	@Test
	public void testSetLogLevel2() {
		String expectedResult = "DEBUG";
		calTest.setLogLevel("debug");
		String actualResult = LogManager.getRootLogger().getLevel().toString();
		assertEquals(expectedResult, actualResult);
	}
	
	@Test
	public void testSetLogLevel3() {
		String expectedResult = "INFO";
		calTest.setLogLevel("info");
		String actualResult = LogManager.getRootLogger().getLevel().toString();
		assertEquals(expectedResult, actualResult);
	}

	@Test
	public void testSetLogLevel4() {
		boolean exceptionThrown = false;
		try {
			calTest.setLogLevel("abcd");
		}catch(RuntimeException e) {
			exceptionThrown = true;
		}
		
		assertTrue(exceptionThrown);
	}

	
	/*
	 * Testing BreakTheExpression() method with various inputs 
	 */
	
	@Test
	public void testBreakTheExpression1() { //normal values with add and mult
		String expression = "add(10, mult(3,4))";
		int expectedResult = 22;
		int actualResult = calTest.breakTheExpression(expression);
		assertEquals(expectedResult, actualResult);
	}

	@Test
	public void testBreakTheExpression2() { // with let
		String expression = "let(a, 10, mult(3, a))";
		int expectedResult = 30;
		int actualResult = calTest.breakTheExpression(expression);
		assertEquals(expectedResult, actualResult);
	}

	@Test
	public void testBreakTheExpression3() { // sub and mult
		String expression = "sub(10, mult(3, 4))";
		int expectedResult = -2;
		int actualResult = calTest.breakTheExpression(expression);
		assertEquals(expectedResult, actualResult);
	}

	@Test
	public void testBreakTheExpression4() { //div and add
		String expression = "div(9, add(2, 1))";
		int expectedResult = 3;
		int actualResult = calTest.breakTheExpression(expression);
		assertEquals(expectedResult, actualResult);
	}
	
	@Test
	public void testBreakTheExpression5() { // Long expression with multiple let
		String expression = "let(a, let(b, 10, add(b, b)), let(b, 20, add(a, b)))";
		int expectedResult = 40;
		int actualResult = calTest.breakTheExpression(expression);
		assertEquals(expectedResult, actualResult);
	}
	
	@Test
	public void testBreakTheExpression6() { // Long expression with multiple let
		String expression = "mult(-735, add(543, 901))";
		int expectedResult = -1061340;
		int actualResult = calTest.breakTheExpression(expression);
		assertEquals(expectedResult, actualResult);
	}

}
