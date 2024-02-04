package com.course;

import com.course.expr.PostfixExpression;
import com.course.expr.InfixExpression;
import com.course.util.Util;
import com.course.util.WrongFormatException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
	@FXML
	private AnchorPane pane;
	@FXML
	private Label inLabel;
	@FXML
	private Label outLabel;
	@FXML
	private TextField in;
	@FXML
	private TextField out;
	@FXML
	private TextField value;
	@FXML
	private TextField result;
	@FXML
	private TextField variable;
	@FXML
	private TextField timeINF;
	@FXML
	private TextField timePTF;
	@FXML
	private TextField minX;
	@FXML
	private TextField maxX;
	@FXML
	private TextField minY;
	@FXML
	private TextField maxY;
	@FXML
	private ComboBox<String> type;
	@FXML
	private ComboBox<String> operations;
	@FXML
	private ImageView plane;
	@FXML
	private Label coordinates;
	private InfixExpression infixExpression;
	private PostfixExpression postfixExpression;
	private Group graph;
	private double WIDTH;
	private double HEIGHT;
	private double minValX;
	private double maxValX;
	private double minValY;
	private double maxValY;
	private double sizeY;


	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		infixExpression = null;
		postfixExpression = null;

		setComboBoxes();
		setLabels();

		graph = new Group();
		pane.getChildren().add(pane.getChildren().size() - 1, graph);

		Image image = new Image(getClass().getResourceAsStream("plane.png"));
		WIDTH = image.getWidth();
		HEIGHT = image.getHeight();
		plane.setImage(image);
		onBoundaryChange();

		coordinates.setBackground(new Background(
				new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		plane.setOnMouseClicked(this::showCoordinates);
		coordinates.setOnMouseClicked(this::showCoordinates);
		graph.setOnMouseClicked(this::showCoordinates);
	}

	private void setComboBoxes() {
		type.getItems().addAll(
				"Infix notation", "Postfix notation");
		type.getSelectionModel().selectFirst();

		setOperations();
	}

	@FXML
	protected void setOperations() {
		if (invalidVariable()) {
			return;
		}
		operations.getItems().clear();
		if (type.getSelectionModel().getSelectedItem().equals("Infix notation")) {
			String var = variable.getText();
			operations.getItems().addAll(
					"Values", "e", "pi", "Operations", " + ",
					" - ", " * ", " / ", " % ",
					" ^ ", "! ", "sqrt(" + var + ")",
					"sin(" + var + ")", "cos(" + var + ")",
					"tg(" + var + ")", "ctg(" + var + ")",
					"arcsin(" + var + ")", "arccos(" + var + ")",
					"arctg(" + var + ")", "arcctg(" + var + ")",
					"ln(" + var + ")", "lg(" + var + ")",
					"log(" + var + "," + var + ")");
		} else {
			operations.getItems().addAll(
					"Values", "e", "pi", "Operations", "+",
					"-", "*", "/", " % ",
					"^", "!", "sqrt ",
					"sin ", "cos ", "tg ", "ctg ",
					"arcsin ", "arccos ", "arctg ", "arcctg ",
					"ln ", "lg ", "log ");
		}
		operations.getSelectionModel().select(3);
	}

	private boolean invalidVariable() {
		String variable = this.variable.getText();
		if (variable.contains("(") || variable.contains(")")) {
			Util.showWarn("Invalid variable", "Variable contains brackets");
			return true;
		}
		if (variable.contains(" ")) {
			Util.showWarn("Invalid variable", "Variable contains space");
			return true;
		}
		String pattern1 = variable + "\\(.*\\)";
		String pattern2 = variable + " ";
		for (String s : operations.getItems()) {
			if (s.matches(pattern1) || s.matches(pattern2) || s.equals(variable)) {
				Util.showWarn("Invalid variable", "Variable matches operator");
				return true;
			}
		}
		return false;
	}

	@FXML
	protected boolean onCalculateClick() {
		if (invalidVariable()) {
			return false;
		}

		try {
			if (type.getSelectionModel().getSelectedItem().equals("Infix notation")) {
				infixExpression = InfixExpression.build(in.getText(), variable.getText());
				System.out.println("INF from STR: " + Util.getTime(infixExpression.getExecTime()));

				postfixExpression = PostfixExpression.build(infixExpression);
				System.out.println("PTF from INF: " + Util.getTime(postfixExpression.getExecTime()));

				out.setText(postfixExpression.toString(variable.getText()));
			} else {
				postfixExpression = PostfixExpression.build(in.getText(), variable.getText());
				System.out.println("PTF from STR: " + Util.getTime(postfixExpression.getExecTime()));

				infixExpression = InfixExpression.build(postfixExpression);
				System.out.println("INF from PTF: " + Util.getTime(infixExpression.getExecTime()));

				out.setText(infixExpression.toString(variable.getText()));
			}
			System.out.println();
		} catch (WrongFormatException e) {
			Util.showWarn("Can't interpreter an expression", "Wrong expression format");
			return false;
		}

		onBoundaryChange();

		if (value.getText().isEmpty()) {
			Util.showWarn("Can't calculate result", "Value is empty");
		} else {
			try {
				double x = Double.parseDouble(value.getText());
				double res;
				if (type.getSelectionModel().getSelectedItem().equals("Infix notation")) {
					res = infixExpression.calculate(x);
				} else {
					res = postfixExpression.calculate(x);
				}
				result.setText(Double.toString(res));
			} catch (NumberFormatException e) {
				Util.showWarn("Can't calculate result", "Wrong value format");
			}
		}

		setTime();

		return true;
	}

	@FXML
	protected void onCopyFunction() {
		if (onCalculateClick()) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("copy-view.fxml"));
			Stage stage = new Stage();
			stage.setTitle("Java function");

			Parent root;
			try {
				root = loader.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			CopyController copy = loader.getController();
			copy.setStage(stage, root);
			copy.setExpression(infixExpression);
			copy.show();
		}
	}

	@FXML
	protected void onBoundaryChange() {
		coordinates.setText("");
		coordinates.setBorder(new Border(
				new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
						CornerRadii.EMPTY, BorderWidths.EMPTY)));

		double minX;
		double maxX;
		double minY;
		double maxY;
		try {
			minX = Double.parseDouble(this.minX.getText());
			maxX = Double.parseDouble(this.maxX.getText());
			minY = Double.parseDouble(this.minY.getText());
			maxY = Double.parseDouble(this.maxY.getText());
		} catch (NumberFormatException e) {
			Util.showWarn("Can't change boundaries", "Wrong number format");
			return;
		}
		if (minX >= maxX || minY >= maxY) {
			Util.showWarn("Can't change boundaries", "Min greater than max");
			return;
		}
		if (Math.abs(minX) > 100 || Math.abs(maxX) > 100
				|| Math.abs(minY) > 100 || Math.abs(maxY) > 100) {
			Util.showWarn("Can't change boundaries", "Boundaries must be in [-100;100] limit");
			return;
		}

		minValX = minX;
		maxValX = maxX;
		minValY = minY;
		maxValY = maxY;

		setPlain();

		if (infixExpression != null) {
			printGraph();
		}
	}

	private void setTime() {
		long sumInfix = 0;
		long sumPTF = 0;
		for (int i = 0; i < 10000; i++) {
			infixExpression.calculateWithTime(i);
			sumInfix += infixExpression.getExecTime();

			postfixExpression.calculate(i);
			sumPTF += postfixExpression.getExecTime();
		}
		timeINF.setText(Util.getTime(sumInfix));
		timePTF.setText(Util.getTime(sumPTF));
	}

	private void setPlain() {
		double centerX = WIDTH / 2;
		double centerY = HEIGHT / 2;
		double sizeX = (maxValX - minValX) * WIDTH / 200;
		double sizeY = (maxValY - minValY) * HEIGHT / 200;
		plane.setViewport(new Rectangle2D(
				centerX + minValX * WIDTH / 200, centerY - maxValY * HEIGHT / 200, sizeX, sizeY));

		plane.setLayoutX(300 - 270 * Math.min(sizeX / sizeY, 1));
		plane.setLayoutY(625 - 270 * Math.min(sizeY / sizeX, 1));
		AnchorPane.setTopAnchor(graph, plane.getLayoutY());
		AnchorPane.setLeftAnchor(graph, plane.getLayoutX());
		this.sizeY = 540 * Math.min(sizeY / sizeX, 1);
	}

	private void printGraph() {
		double sizeX = 600 - 2 * plane.getLayoutX();
		double step = (maxValX - minValX) / sizeX;
		double prev = postfixExpression.calculate(minValX);

		graph.getChildren().clear();
		Line line = new Line(0, 0, sizeX, -sizeY);
		line.setStrokeWidth(0);
		graph.getChildren().add(line);

		for (double x = minValX + step; x <= maxValX; x += step) {
			double result = postfixExpression.calculate(x);
			if (isInPlane(prev) && isInPlane(result)) {
				line = new Line(inPaneX(x - step), inPaneY(prev),
						inPaneX(x), inPaneY(result));
				line.setStroke(Color.BLUE);
				line.setStrokeWidth(3);
				graph.getChildren().add(line);
			}
			prev = result;
		}
	}

	private boolean isInPlane(double y) {
		return y >= minValY && y <= maxValY;
	}

	private double inPaneX(double x) {
		return (600 - 2 * plane.getLayoutX()) * (x - minValX) / (maxValX - minValX);
	}

	private double inPaneY(double y) {
		return sizeY * (minValY - y) / (maxValY - minValY);
	}

	@FXML
	protected void showCoordinates(MouseEvent event) {
		if (isInPane(event.getSceneX(), event.getSceneY())) {
			coordinates.setText("%.1f;%.1f".formatted(
					xInPane(event.getSceneX()), yInPane(event.getSceneY())));
			coordinates.setLayoutX(event.getSceneX() - coordinates.getWidth() / 2);
			coordinates.setLayoutY(event.getSceneY());
			coordinates.setBorder(new Border(
					new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
							CornerRadii.EMPTY, new BorderWidths(1))));
		} else {
			coordinates.setText("");
			coordinates.setBorder(new Border(
					new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
							CornerRadii.EMPTY, BorderWidths.EMPTY)));
		}
	}

	private boolean isInPane(double x, double y) {
		return x > plane.getLayoutX() && x < 600 - plane.getLayoutX()
				&& y > plane.getLayoutY() && y < plane.getLayoutY() + sizeY;
	}

	private double xInPane(double x) {
		return (x - plane.getLayoutX()) / (600 - 2 * plane.getLayoutX()) * (maxValX - minValX) + minValX;
	}

	private double yInPane(double y) {
		return maxValY + (plane.getLayoutY() - y) / sizeY * (maxValY - minValY);
	}

	@FXML
	protected void onSetType() {
		setLabels();
		setOperations();

		if (type.getSelectionModel().getSelectedItem().equals("Infix notation")) {
			if (postfixExpression != null) {
				in.setText(infixExpression.toString(variable.getText()));
				postfixExpression = PostfixExpression.build(infixExpression);
				out.setText(postfixExpression.toString(variable.getText()));
			}
		} else {
			if (infixExpression != null && !invalidVariable()) {
				in.setText(postfixExpression.toString(variable.getText()));
				infixExpression = InfixExpression.build(postfixExpression);
				out.setText(infixExpression.toString(variable.getText()));
			}
		}
	}

	@FXML
	protected void onSetOperation() {
		if (operations.getItems().isEmpty()) {
			return;
		}
		if (!operations.getSelectionModel().getSelectedItem().equals("Operations")) {
			in.setText(in.getText() + operations.getSelectionModel().getSelectedItem());
		}
		operations.getSelectionModel().select(3);
	}

	private void setLabels() {
		inLabel.setText("Input " + type.getSelectionModel().getSelectedItem() + " notation expression");
		String text = "In ";
		if (type.getSelectionModel().getSelectedItem().equals("Infix notation")) {
			text = text + "Postfix";
		} else {
			text = text + "Infix";
		}
		text = text + " notation";
		outLabel.setText(text);
	}
}