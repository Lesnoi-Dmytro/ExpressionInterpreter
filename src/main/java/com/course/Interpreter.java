	package com.course;

	import javafx.application.Application;
	import javafx.fxml.FXMLLoader;
	import javafx.scene.Scene;
	import javafx.scene.image.Image;
	import javafx.stage.Stage;

	import java.io.IOException;

	public class Interpreter extends Application {

		@Override
		public void start(Stage stage) throws IOException {
			FXMLLoader fxmlLoader = new FXMLLoader(Interpreter.class.getResource("main-view.fxml"));

			Scene scene = new Scene(fxmlLoader.load(), 600, 925);
			stage.setTitle("Expression interpreter");
			stage.setScene(scene);
			stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
			stage.show();
		}

		public static void main(String[] args) {
			launch();
		}
	}