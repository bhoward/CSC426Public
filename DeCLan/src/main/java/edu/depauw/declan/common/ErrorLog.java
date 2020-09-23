package edu.depauw.declan.common;

import java.util.Iterator;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * An ErrorLog accumulates error messages and their corresponding positions
 * while compiling. Errors may be retrieved in order by source position.
 * 
 * @author bhoward
 */
public class ErrorLog implements Iterable<ErrorLog.LogItem> {
	private SortedSet<LogItem> items = new TreeSet<>();

	/**
	 * Add an error message associated with the given source position to the log.
	 * 
	 * @param message
	 * @param position
	 */
	public void add(String message, Position position) {
		items.add(new LogItem(message, position));
	}

	@Override
	public Iterator<LogItem> iterator() {
		return items.iterator();
	}

	/**
	 * A LogItem encapsulates an error message and its corresponding source
	 * position. LogItems may be sorted by position.
	 * 
	 * @author bhoward
	 */
	public static class LogItem implements Comparable<LogItem> {
		private String message;
		private Position position;

		public LogItem(String message, Position position) {
			this.message = message;
			this.position = position;
		}

		public String getMessage() {
			return message;
		}

		public Position getPosition() {
			return position;
		}

		@Override
		public String toString() {
			return "Error: " + message + " at " + position;
		}

		@Override
		public int hashCode() {
			return Objects.hash(message, position);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LogItem other = (LogItem) obj;
			return Objects.equals(message, other.message) && Objects.equals(position, other.position);
		}

		@Override
		public int compareTo(LogItem other) {
			return position.compareTo(other.position);
		}
	}
}
