package com.course.struct;

public class ModQue<T> {
	private StackNode<T> start;
	private StackNode<T> current;
	private StackNode<T> end;

	public ModQue() {
		start = current = end = null;
	}

	public ModQue(ModQue<T> que) {
		start = current = new StackNode<>(que.start.info);
		if (que.start.next == null) {
			end = start;
			return;
		}

		start.next = end = new StackNode<>(que.start.next.info);
		StackNode<T> node = que.start.next.next;
		while (node != null) {
			end.next = new StackNode<>(node.info);
			end = end.next;
			node = node.next;
		}
	}

	public void reset() {
		current = start;
	}

	public T get() {
		T info = null;
		if (current != null) {
			info = current.info;
			current = current.next;
		}
		return info;
	}

	public void add(T info) {
		if (start == null) {
			start = current = end = new StackNode<>(info);
		} else {
			end.next = new StackNode<>(info);
			end = end.next;
		}
	}

	public int size() {
		int i = 0;
		StackNode<T> temp = start;
		while (temp != null) {
			temp = temp.next;
			i++;
		}
		return i;
	}

	public boolean isEmpty() {
		return current == null;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		StackNode<T> node = start;
		while (node != null) {
			s.append(node.info).append(" ");
			node = node.next;
		}
		return s.toString();
	}
}