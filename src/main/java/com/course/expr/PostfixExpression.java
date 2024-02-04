package com.course.expr;

import com.course.oper.Operation;
import com.course.struct.ModQue;
import com.course.struct.Stack;
import com.course.util.WrongFormatException;

import java.util.Objects;

public class PostfixExpression implements Expression {
	private ModQue<Operation> operations;
	private ModQue<Double> values;
	private Stack<Double> curValues;
	private long execTime;

	private PostfixExpression() {
		operations = new ModQue<>();
		values = new ModQue<>();
		curValues = new Stack<>();
		execTime = 0;
	}

	public static PostfixExpression build(String exp, String var) throws WrongFormatException {
		long srt = System.nanoTime();
		PostfixExpression res = new PostfixExpression();
		for (int start = 0; start < exp.length(); ) {
			if (Character.isSpaceChar(exp.charAt(start))) {
				start++;
				continue;
			}

			int size = 0;
			if (Operation.isOperand(exp.charAt(start))) {
				size++;
			} else {
				while (start + size < exp.length() && Character.isLetter(exp.charAt(start + size))) {
					size++;
				}
			}

			Operation operation;
			if ((operation = Operation.getOperation(exp.substring(start, start + size), var)) == null) {
				if (Character.isDigit(exp.charAt(start))) {
					double x = exp.charAt(start++) - '0';
					while (start < exp.length() && Character.isDigit(exp.charAt(start))) {
						x *= 10;
						x += exp.charAt(start++) - '0';
					}

					if (start < exp.length() && exp.charAt(start) == '.') {
						start++;
						double pow = 0.1;
						while (start < exp.length() && Character.isDigit(exp.charAt(start))) {
							x += (exp.charAt(start++) - '0') * pow;
							pow /= 10;
						}
					}
					res.values.add(x);
					res.operations.add(Operation.CONSTANT);
				} else {
					throw new WrongFormatException();
				}
			} else {
				if (operation == Operation.BRACKETS) {
					throw new WrongFormatException();
				}
				start += size;
				if (operation.getPriority() == 4) {
					if (operation == Operation.RETURN) {
						res.values.add(null);
					} else {
						res.values.add(operation.getOperation().apply(0.0, 0.0));
					}
					res.operations.add(operation);
				} else {
					res.operations.add(operation);
				}
			}
		}
		if (res.isWrong()) {
			throw new WrongFormatException();
		}
		res.execTime = System.nanoTime() - srt;
		return res;
	}

	public static PostfixExpression build(InfixExpression expression) {
		if (expression != null && !expression.isEmpty()) {
			long start = System.nanoTime();
			PostfixExpression pin = new PostfixExpression();
			pin.toPTF(expression);
			pin.execTime = System.nanoTime() - start;
			return pin;
		} else {
			return null;
		}
	}

	private void toPTF(InfixExpression expression) {
		if (expression.getLeft() != null) {
			toPTF(expression.getLeft());
		}
		if (expression.getRight() != null) {
			toPTF(expression.getRight());
		}

		operations.add(expression.getOperation());
		if (expression.getOperation().getPriority() == 4) {
			if (expression.getOperation() == Operation.RETURN) {
				values.add(null);
			} else {
				values.add(expression.getValue());
			}
		}
	}

	@Override
	public boolean isWrong() {
		int valuesNum = 1;
		Operation operation;
		while ((operation = operations.get()) != null) {
			if (operation.getSide() == 'b') {
				valuesNum++;
			}
		}
		operations.reset();
		values.reset();
		return valuesNum != values.size();
	}

	@Override
	public double calculate(double x) {
		long start = System.nanoTime();
		Operation operation;
		while ((operation = operations.get()) != null) {
			if (operation.getPriority() == 4) {
				Double value = values.get();
				curValues.push(Objects.requireNonNullElse(value, x));
			} else {
				if (operation.getSide() == 'b') {
					Double value = curValues.pop();
					curValues.push(operation.getOperation().apply(curValues.pop(), value));
				} else if (operation.getSide() == 'r') {
					curValues.push(operation.getOperation().apply(0.0, curValues.pop()));
				} else {
					curValues.push(operation.getOperation().apply(curValues.pop(), 0.0));
				}
			}
		}
		operations.reset();
		values.reset();
		execTime = System.nanoTime() - start;
		return curValues.pop();
	}

	@Override
	public String toString(String var) {
		StringBuilder builder = new StringBuilder();
		Operation operation;
		operation = operations.get();
		if (operation != null) {
			builder.append(operationWrite(operation, var));
		}
		while ((operation = operations.get()) != null) {
			builder.append(" ");
			builder.append(operationWrite(operation, var));
		}
		operations.reset();
		values.reset();
		return builder.toString();
	}

	private String operationWrite(Operation operation, String var) {
		if (operation == Operation.RETURN) {
			values.get();
			return var;
		} else if (operation == Operation.CONSTANT) {
			return values.get().toString();
		} else if (operation.getPriority() == 4) {
			values.get();
		}
		return operation.getWrite();
	}

	public long getExecTime() {
		return execTime;
	}

	ModQue<Operation> getOperations() {
		return new ModQue<>(operations);
	}

	ModQue<Double> getValues() {
		return new ModQue<>(values);
	}
}