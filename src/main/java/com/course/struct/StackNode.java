package com.course.struct;

public class StackNode<T> {
	StackNode<T> next;
	T info;

	StackNode(T info) {
		this.info = info;
		next = null;
	}
}