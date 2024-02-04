package com.course.expr;

import com.course.oper.Operation;
import com.course.struct.ModQue;
import com.course.struct.Stack;
import com.course.util.Util;
import com.course.util.WrongFormatException;

import java.util.function.BinaryOperator;

public class InfixExpression implements Expression {
	private InfixExpression parent;
	private InfixExpression left;
	private InfixExpression right;
	private Operation operEnum;
	private BinaryOperator<Double> operation;
	private long execTime;

	private InfixExpression() {
		left = right = null;
		operEnum = null;
		parent = null;
		execTime = 0;
	}

	private InfixExpression(Operation operEnum) {
		left = right = null;
		this.operEnum = operEnum;
		operation = operEnum.getOperation();
	}

	public static InfixExpression build(String expression, String variable) throws WrongFormatException {
		long start = System.nanoTime();
		InfixExpression result = build(expression, variable, 0, expression.length());
		if (result == null || result.isWrong()) {
			throw new WrongFormatException();
		}
		result.execTime = System.nanoTime() - start;
		return result;
	}

	private static InfixExpression build(String exp, String var, int start, int end) throws WrongFormatException {
		InfixExpression res = null;
		while (start < end) {
			if (Character.isSpaceChar(exp.charAt(start))) {
				start++;
				continue;
			}

			InfixExpression parent = res;
			res = new InfixExpression();
			res.parent = parent;
			if (parent != null) {
				parent.right = res;
			}

			int size = 0;
			if (Operation.isOperand(exp.charAt(start))) {
				size++;
			} else {
				while (start + size < end && Character.isLetter(exp.charAt(start + size))) {
					size++;
				}
			}

			if ((res.operEnum = Operation.getOperation(exp.substring(start, start + size), var)) == null) {
				if (Character.isDigit(exp.charAt(start))) {
					double x = exp.charAt(start++) - '0';
					while (start < end && Character.isDigit(exp.charAt(start))) {
						x *= 10;
						x += exp.charAt(start++) - '0';
					}

					if (start < end && exp.charAt(start) == '.') {
						start++;
						double pow = 0.1;
						while (start < end && Character.isDigit(exp.charAt(start))) {
							x += (exp.charAt(start++) - '0') * pow;
							pow /= 10;
						}
					}
					res.operEnum = Operation.CONSTANT;
					res.operation = res.operEnum.getConstant(x);
				} else {
					throw new WrongFormatException();
				}
			} else {
				start += size;
				res.operation = res.operEnum.getOperation();
			}

			if (res.operEnum == Operation.BRACKETS) {
				int bracketsStart = start;
				int requred = 1;
				while (requred != 0) {
					if (start == end) {
						throw new WrongFormatException();
					}
					if (exp.charAt(start) == '(') {
						requred++;
					} else if (exp.charAt(start) == ')') {
						requred--;
					}
					start++;
				}
				res.left = build(exp, var, bracketsStart, start - 1);
				if (parent != null && parent.operEnum.getPriority() == 4) {
					InfixExpression multiply = new InfixExpression(Operation.MULTIPLY);

					parent.right = null;
					multiply.parent = parent.parent;
					multiply.right = res;
					multiply.left = parent;

					parent.parent.right = multiply;
					parent.parent = multiply;
				}
			} else {
				res.setNode();
			}
		}

		if (res != null) {
			while (res.parent != null) {
				res = res.parent;
			}
			res.removeBrackets();
		}
		return res;
	}

	private void setNode() {
		if (parent != null && operEnum.getPriority() == 4 && parent.operEnum.getPriority() == 4) {
			InfixExpression multiply = new InfixExpression(Operation.MULTIPLY);
			parent.right = null;

			multiply.parent = parent.parent;
			multiply.right = this;
			multiply.left = parent;

			parent.parent = multiply;
			parent = multiply;
			if (parent.parent != null) {
				parent.parent.right = multiply;
			}
		} else {
			while (parent != null && operEnum.getPriority() <= parent.operEnum.getPriority()) {
				InfixExpression parent = this.parent;
				parent.right = left;
				if (left != null) {
					left.parent = parent.right;
				}
				left = parent;
				this.parent = parent.parent;
				if (this.parent != null) {
					this.parent.right = this;
				}
				parent.parent = this;
			}
		}
	}

	private InfixExpression removeBrackets() {
		if (right != null) {
			right = right.removeBrackets();
		}
		if (left != null) {
			left = left.removeBrackets();
		}

		if (operEnum == Operation.BRACKETS) {
			if (left != null) {
				left.parent = parent;
			}
			return left;
		}
		return this;
	}

	public static InfixExpression build(PostfixExpression pin) {
		long start = System.nanoTime();
		ModQue<Operation> operations = pin.getOperations();
		ModQue<Double> values = pin.getValues();
		Stack<InfixExpression> expressions = new Stack<>();

		while (!operations.isEmpty()) {
			InfixExpression expr = new InfixExpression();
			expr.operEnum = operations.get();

			if (expr.operEnum == Operation.CONSTANT) {
				expr.operation = expr.operEnum.getConstant(values.get());
			} else {
				if (expr.operEnum.getPriority() == 4) {
					values.get();
				}
				expr.operation = expr.operEnum.getOperation();
			}

			switch (expr.operEnum.getSide()) {
				case 'l' -> expr.left = expressions.pop();
				case 'r' -> expr.right = expressions.pop();
				case 'b' -> {
					expr.right = expressions.pop();
					expr.left = expressions.pop();
				}
			}
			expressions.push(expr);
		}

		operations.reset();
		values.reset();
		InfixExpression expression = expressions.pop();
		expression.execTime = System.nanoTime() - start;
		return expression;
	}

	public boolean isWrong() {
		if (left != null && left.isWrong()) {
			return true;
		}
		if (right != null && right.isWrong()) {
			return true;
		}

		char side = operEnum.getSide();
		if (side == 'b' || side == 'l') {
			if (left == null) {
				return true;
			}
		} else {
			if (left != null) {
				return true;
			}
		}

		if (side == 'b' || side == 'r') {
			return right == null;
		} else {
			return right != null;
		}
	}

	public double calculateWithTime(double x) {
		long start = System.nanoTime();
		double res = calculate(x);
		execTime = System.nanoTime() - start;
		return res;
	}

	@Override
	public double calculate(double x) {
		return operation.apply(left == null ? x : left.calculate(x), right == null ? x : right.calculate(x));
	}

	public void javaExpression(StringBuilder expr, StringBuilder begin, String var) {
		if (operEnum == Operation.RETURN) {
			expr.append(var);
		} else if (operEnum == Operation.CONSTANT) {
			expr.append(calculate(0));
		} else if (operEnum == Operation.POWER) {
			StringBuilder leftOper = new StringBuilder();
			left.javaExpression(leftOper, begin, var);
			StringBuilder rightOper = new StringBuilder();
			right.javaExpression(rightOper, begin, var);
			expr.append(operEnum.getJava()).append("(").append(leftOper).append(",").append(rightOper).append(")");
		} else if (operEnum == Operation.FACTORIAL) {
			StringBuilder factNum = new StringBuilder();
			left.javaExpression(factNum, begin, var);
			int num = Util.height(begin.toString()) / 4;
			begin.append("\tint fact").append(num).append(" = 1;\n\t").append("for (int i = 0; i < (int)(").append(factNum).append("); i++) {\n\t").append("\tfact").append(num).append(" *= i;\n\t").append("};\n");
			expr.append("fact").append(num);
		} else {

			if (left != null) {
				if (left.operEnum.getPriority() < operEnum.getPriority() || left.operEnum.getPriority() == 4 && operEnum.getPriority() == 3) {
					expr.append('(');
					left.javaExpression(expr, begin, var);
					expr.append(')');
				} else {
					left.javaExpression(expr, begin, var);
				}
			}

			expr.append(operEnum.getJava());

			if (right != null) {
				if (right.operEnum.getPriority() < operEnum.getPriority() || right.operEnum.getPriority() == 4 && operEnum.getPriority() == 3) {
					expr.append('(');
					right.javaExpression(expr, begin, var);
					expr.append(')');
				} else {
					right.javaExpression(expr, begin, var);
				}
			}
		}
	}

	public void printTree(int level) {
		if (left != null) {
			left.printTree(level + 1);
		}
		System.out.println(level + "\t".repeat(level) + (operEnum == Operation.CONSTANT ? +operation.apply(0.0, 0.0) : operEnum));
		if (right != null) {
			right.printTree(level + 1);
		}
	}

	@Override
	public String toString(String var) {
		return toStringbuilder(var).toString();
	}

	private StringBuilder toStringbuilder(String var) {
		StringBuilder leftString = new StringBuilder();
		StringBuilder rightString = new StringBuilder();
		if (left != null) {
			leftString = left.toStringbuilder(var);
			if (operEnum.getPriority() > left.operEnum.getPriority() ||
					operEnum == Operation.SUBTRACT && left.operEnum == Operation.SUBTRACT ||
					operEnum == Operation.DIVIDE && left.operEnum == Operation.DIVIDE) {
				leftString.insert(0, "(").append(")");
			}
			if (operEnum.getPriority() < 3) {
				leftString.append(" ");
			}
		}
		if (right != null) {
			rightString = right.toStringbuilder(var);
			if (operEnum.getPriority() > right.operEnum.getPriority() ||
					operEnum.getPriority() == 3 && right.operEnum.getPriority() == 4 ||
					operEnum == Operation.SUBTRACT && right.operEnum.getPriority() == 1 ||
					operEnum == Operation.DIVIDE && right.operEnum.getPriority() == 2) {
				rightString.insert(0, "(").append(")");
			}
			if (operEnum.getPriority() < 3) {
				rightString.insert(0, " ");
			}
		}

		if (operEnum == Operation.RETURN) {
			return leftString.append(var).append(rightString);
		} else if (operEnum == Operation.CONSTANT) {
			return leftString.append(operation.apply(0.0, 0.0)).append(rightString);
		}
		return leftString.append(operEnum.getWrite()).append(rightString);
	}

	public long getExecTime() {
		return execTime;
	}

	boolean isEmpty() {
		return operEnum == null;
	}

	InfixExpression getLeft() {
		return left;
	}

	InfixExpression getRight() {
		return right;
	}

	Operation getOperation() {
		return operEnum;
	}

	Double getValue() {
		return operation.apply(0.0, 0.0);
	}
}