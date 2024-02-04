package com.course.struct;

public class Stack<T> {
	private StackNode<T> head;

	public T pop() {
		T info = head.info;
		head = head.next;
		return info;
	}

	public void push(T info) {
		StackNode<T> node = new StackNode<>(info);
		node.next = head;
		head = node;
	}
}