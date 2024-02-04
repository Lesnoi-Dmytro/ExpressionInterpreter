package com.course.util;

import javafx.scene.control.Alert;

public class Util {
	public static int height(String text) {
		int height = 1;
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '\n') {
				height++;
			}
		}
		return height;
	}


	public static void showWarn(String title, String text) {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle(title);
		alert.setContentText(text);
		alert.showAndWait();
	}

	public static String getTime(long nano) {
		return "%.9fs".formatted(nano * 1e-9);
	}

	public static String getTime(long start, long end) {
		return "%.9fs".formatted((end - start) * 1e-9);
	}
}