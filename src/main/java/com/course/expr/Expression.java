package com.course.expr;

public interface Expression {
	double calculate(double x);
	boolean isWrong();
	String toString(String var);
}