package com.course;

import com.course.expr.InfixExpression;
import com.course.util.Util;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;

public class CopyController {
	@FXML
	private TextArea function;
	@FXML
	private TextField name;
	@FXML
	private TextField variable;
	private Stage stage;
	private Parent root;
	private InfixExpression expression;
	private StringBuilder expressionString;
	private StringBuilder beginString;
	private final static String funcPart1 = "public double ";
	private final static String funcPart2 = " (double ";
	private final static String funcPart3 = ") {\n";
	private final static String funcPart4 = "\treturn ";
	private final static String funcPart5 = ";\n}";

	public void show() {
		expressionString = new StringBuilder();
		beginString = new StringBuilder();
		expression.javaExpression(expressionString, beginString, variable.getText());
		double height = Math.min(300, 16 + Util.height(functionToText()) * 18);
		function.setText(functionToText());
		function.setPrefHeight(height);

		stage.setScene(new Scene(root, 400, 165 + height));
		stage.showAndWait();
	}

	private String functionToText() {
		return funcPart1 + name.getText() + funcPart2 + variable.getText()
				+ funcPart3 + beginString + funcPart4 + expressionString + funcPart5;
	}

	@FXML
	protected boolean onCreate() {
		if (name.getText().isEmpty()) {
			Util.showWarn("Can't create function", "Function name is empty");
			return false;
		}
		if (variable.getText().isEmpty()) {
			Util.showWarn("Can't create function", "Function variable is empty");
			return false;
		}
		expressionString = new StringBuilder();
		beginString = new StringBuilder();
		expression.javaExpression(expressionString, beginString, variable.getText());
		function.setText(functionToText());
		return true;
	}

	@FXML
	protected void onFunctionCopy() {
		if (onCreate()) {
			Clipboard clipboard = Clipboard.getSystemClipboard();
			ClipboardContent content = new ClipboardContent();
			content.putString(function.getText());
			clipboard.setContent(content);
		}
	}

	@FXML
	protected void onExpressionCopy() {
		if (onCreate()) {
			Clipboard clipboard = Clipboard.getSystemClipboard();
			ClipboardContent content = new ClipboardContent();
			content.putString(beginString.toString() + expressionString.toString());
			clipboard.setContent(content);
		}
	}

	@FXML
	protected void onCloseClick() {
		stage.close();
	}

	public void setStage(Stage stage, Parent root) {
		this.stage = stage;
		this.root = root;
	}

	public void setExpression(InfixExpression expression) {
		this.expression = expression;
	}
}