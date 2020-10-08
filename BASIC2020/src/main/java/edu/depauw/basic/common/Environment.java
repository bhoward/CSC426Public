package edu.depauw.basic.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Environment {
	private Stack<Map<String, Integer>> maps;
	
	public Environment() {
		this.maps = new Stack<>();
		maps.push(new HashMap<>());
	}
	
	public Integer get(String lexeme) {
		int index = maps.size() - 1;
		while (index >= 0) {
			Map<String, Integer> map = maps.get(index);
			if (map.containsKey(lexeme)) {
				return map.get(lexeme);
			}
			index--;
		}
		return null;
	}

	public void put(String lexeme, int value) {
		Map<String, Integer> map = maps.peek();
		map.put(lexeme, value);
	}

	public Integer getOrDefault(String lexeme, Integer d) {
		Integer result = get(lexeme);
		if (result == null) {
			return d;
		} else {
			return result;
		}
	}

	public void pushTemporary(String lexeme, int value) {
		Map<String, Integer> map = new HashMap<>();
		map.put(lexeme, value);
		maps.push(map);
	}

	public void popTemporary() {
		maps.pop();
	}
}
