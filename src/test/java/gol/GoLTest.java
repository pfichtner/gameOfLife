package gol;

import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.stream.Stream;

import org.hamcrest.Description;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;

public class GoLTest {

	private static final class BoardMatcher extends TypeSafeMatcher<Board> {

		private final String expected;

		private BoardMatcher(String... rows) {
			expected = Stream.of(rows).collect(joining("\n"));
		}

		@Override
		public void describeTo(Description description) {
			description.appendText(expected);
		}

		@Override
		public boolean matchesSafely(Board board) {
			return actual(board).equals(expected);
		}

		@Override
		public void describeMismatch(Object itemDescription mismatchDescription) {
		}

		private String actual(Board board) {
			return range(0, board.getHeight()).mapToObj(y -> row(board, y)).collect(joining("\n"));
		}

		private String row(Board board, int y) {
			return range(0, board.getHeight()).mapToObj(x -> board.isLifeAt(x, y) ? "X" : " ").collect(joining());
		}
	}

	private Board board;

	@Test
	public void canCreateBoardAndSetLife() {
		aNewBoard(1, 1);
		withLifeAt(0, 0);
		assertThat(isLifeAt(0, 0), is(true));
	}

	@Test
	public void whereNoLifeIsSetThereIsNoLife() {
		aNewBoard(2, 1);
		withLifeAt(1, 0);
		assertThat(isLifeAt(0, 0), is(false));
	}

	@Test
	public void afterTickThereIsNoLife() {
		aNewBoard(1, 1);
		withLifeAt(0, 0);
		whenTicked();
		assertThat(isLifeAt(0, 0), is(false));
	}

	@Test
	public void cellsWithThreeNeighboursWillSurvive() {
		aBoard( //
				"XX", //
				"XX" //
		);
		whenTicked();
		assertThat(board, is(board( //
				"XX", //
				"XX" //
		)));
	}

	@Test
	public void cellsWithTwoNeighboursWillSurvive() {
		aBoard( //
				"X ", //
				"XX" //
		);
		whenTicked();
		assertThat(board, is(board( //
				"X ", //
				"XX" //
		)));
	}

	@Test
	public void newLifeIsBorn() {
		aBoard( //
				"XXX", //
				"   " //
		);
		whenTicked();
		assertThat(board, is(board( //
				" X ", //
				" X " //
		)));
	}

	private TypeSafeMatcher<Board> board(String... rows) {
		return new BoardMatcher(rows);
	}

	private void aBoard(String... rows) {
		aNewBoard(2, rows.length);
		for (int y = 0; y < rows.length; y++) {
			char[] charArray = rows[y].toCharArray();
			for (int x = 0; x < charArray.length; x++) {
				if (charArray[x] == 'X') {
					withLifeAt(x, y);
				}
			}
		}

	}

	private boolean isLifeAt(int x, int y) {
		return board.isLifeAt(x, y);
	}

	private void whenTicked() {
		board.tick();

	}

	private void withLifeAt(int x, int y) {
		this.board.setAlive(x, y);
	}

	private void aNewBoard(int width, int height) {
		this.board = new Board(width, height);
	}

}
