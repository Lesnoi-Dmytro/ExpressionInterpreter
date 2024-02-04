package com.course.oper;

import java.util.function.BinaryOperator;

public enum Operation {
	CONSTANT(4, 'n', "", ""),
	E(4, 'n', "e", "Math.E"),
	PI(4, 'n', "pi", "Math.PI"),
	RETURN(4, 'n', "", ""),
	ADD(1, 'b', "+", " + "),
	SUBTRACT(1, 'b', "-", " - "),
	MULTIPLY(2, 'b', "*", " * "),
	DIVIDE(2, 'b', "/", " / "),
	REMINDER(2, 'b', "%", " % "),
	POWER(3, 'b', "^", "Math.pow"),
	FACTORIAL(3, 'l', "!", ""),
	SQRT(3, 'r', "sqrt", "Math.sqrt"),
	SIN(3, 'r', "sin", "Math.sin"),
	COS(3, 'r', "cos", "Math.cos"),
	TG(3, 'r', "tg", "Math.tg"),
	CTG(3, 'r', "ctg", "1 / Math.tg"),
	ASIN(3, 'r', "arcsin", "Math.asin"),
	ACOS(3, 'r', "arccos", "Math.arccos"),
	ATG(3, 'r', "arctg", "Math.atg"),
	ACTG(3, 'r', "arctg", "Math.PI / 2 - Math.atg"),
	LN(3, 'r', "ln", "Math.log"),
	LG(3, 'r', "lg", "Math.log10"),
	BRACKETS(4, 'n', "(", "");

	private final int priority;
	private final char side;
	private final String write;
	private final String java;

	Operation(int p, char s, String w, String j) {
		priority = p;
		side = s;
		write = w;
		java = j;
	}

	public BinaryOperator<Double> getOperation() {
		return switch (this) {
			case RETURN -> (a, b) -> a;
			case E -> (a, b) -> Math.E;
			case PI -> (a, b) -> Math.PI;
			case ADD -> Double::sum;
			case SUBTRACT -> (a, b) -> a - b;
			case MULTIPLY -> (a, b) -> a * b;
			case DIVIDE -> (a, b) -> a / b;
			case REMINDER -> (a, b) -> a % b;
			case POWER -> Math::pow;
			case FACTORIAL -> (a, b) -> {
				double res = 1;
				for (int i = a.intValue(); i > 1; i--) {
					res *= i;
				}
				return res;
			};
			case SQRT -> (a, b) -> Math.sqrt(b);
			case SIN -> (a, b) -> Math.sin(b);
			case COS -> (a, b) -> Math.cos(b);
			case TG -> (a, b) -> Math.tan(b);
			case CTG -> (a, b) -> 1 / Math.tan(b);
			case ASIN -> (a, b) -> Math.asin(b);
			case ACOS -> (a, b) -> Math.acos(b);
			case ATG -> (a, b) -> Math.atan(b);
			case ACTG -> (a, b) -> Math.PI / 2 - Math.atan(b);
			case LN -> (a, b) -> Math.log(b);
			case LG -> (a, b) -> Math.log10(b);
			case CONSTANT -> (a, b) -> 0.0;
			case BRACKETS -> (a, b) -> 0.0;
		};
	}

	public BinaryOperator<Double> getConstant(double x) {
		return (a, b) -> x;
	}

	public static Operation getOperation(String operator, String variable) {
		if (operator.isEmpty()) {
			return null;
		}
		if (operator.equals(variable)) {
			return RETURN;
		}
		for (Operation o : values()) {
			if (o.write.equals(operator)) {
				return o;
			}
		}
		return null;
	}

	public static boolean isOperand(char c) {
		return c == '+' || c == '-' || c == '*' || c == '/' ||
				c == '%' || c == '^' || c == '!' || c == '(';
	}

	public int getPriority() {
		return priority;
	}

	public char getSide() {
		return side;
	}

	public String getWrite() {
		return write;
	}

	public String getJava() {
		return java;
	}
}