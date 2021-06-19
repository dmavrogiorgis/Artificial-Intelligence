package searchingAlgorithms;
import java.util.*;

public class Algorithms {
	private boolean visited[][];
	HashMap<Point, Integer> heuristicTable = new HashMap<>();

	private final static int rowNum[] = { -1, 0, 0, 1 };
	private final static int colNum[] = { 0, -1, 1, 0 };

	private static class Point {
		private Cell cell;
		private int x;
		private int y;
		private Point parent;
		private int fscore;
		private int gscore;

		public Point(Cell c, int x, int y, Point parent, int fscore, int gscore) {
			this.cell = c;
			this.x = x;
			this.y = y;
			this.parent = parent;
			this.fscore = fscore;
			this.gscore = gscore;
		}

		public Cell getCell() {
			return cell;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public Point getParent() {
			return this.parent;
		}

		public int getFscore() {
			return fscore;
		}

		public void setFscore(int fscore) {
			this.fscore = fscore;
		}

		public int getGscore() {
			return gscore;
		}

		public void setGscore(int gscore) {
			this.gscore = gscore;
		}
	}

	public boolean isValid(int i, int j, int tot_rows, int tot_cols) {
		return (i >= 0) && (i < tot_rows) && (j >= 0) && (j < tot_cols);
	}

	public boolean isFree(int row, int col, Grid mygrid) {
		if (isValid(row, col, mygrid.getN(), mygrid.getM()) && (mygrid.getCell(row, col).getCost() == 1
				|| mygrid.getCell(row, col).getCost() == 2 || mygrid.getCell(row, col).getCost() == 10)
				&& !visited[row][col]) {
			return true;
		}
		return false;
	}

	public int[] BFS(Grid mygrid) {

		visited = new boolean[mygrid.getN()][mygrid.getM()];
		int[] start = mygrid.getStart();
		int[] end = mygrid.getTerminal();

		if (mygrid.getCell(start[0], start[1]).isWall() || mygrid.getCell(end[0], end[1]).isWall()) {
			return null;
		}

		visited[start[0]][start[1]] = true;
		Queue<Point> queue = new LinkedList<>();
		queue.add(new Point(mygrid.getCell(start[0], start[1]), start[0], start[1], null, 0, 0));

		int count = 1;
		while (!queue.isEmpty()) {

			Point curr = queue.poll();

			if (curr.getCell().isTerminal()) {
				System.out.println("Extended Nodes: " + count);
				int[] steps = shortestPath(curr, mygrid);
				return steps;
			}
			for (int i = 0; i < 4; i++) {
				int row = curr.getX() + rowNum[i];
				int col = curr.getY() + colNum[i];

				if (isFree(row, col, mygrid)) {
					visited[row][col] = true;
					Point nextp = new Point(mygrid.getCell(row, col), row, col, curr, 0, 0);
					queue.add(nextp);
					count++;
				}
				row = row - rowNum[i];
				col = col - colNum[i];
			}
		}
		return null;
	}

	public int[] DFS(Grid mygrid) {

		visited = new boolean[mygrid.getN()][mygrid.getM()];

		int[] start = mygrid.getStart();
		int[] end = mygrid.getTerminal();

		if (mygrid.getCell(start[0], start[1]).isWall() || mygrid.getCell(end[0], end[1]).isWall()) {
			return null;
		}

		Stack<Point> stack = new Stack<>();
		stack.add(new Point(mygrid.getCell(start[0], start[1]), start[0], start[1], null, 0, 0));

		int count = 1;
		while (!stack.empty()) {
			Point curr = stack.pop();
			visited[curr.getX()][curr.getY()] = true;

			for (int i = 0; i < 4; i++) {
				if (curr.getCell().isTerminal()) {
					System.out.println("Extended Nodes: " + count);
					int[] steps = shortestPath(curr, mygrid);
					return steps;
				}
				int row = curr.getX() + rowNum[i];
				int col = curr.getY() + colNum[i];

				if (isFree(row, col, mygrid)) {
					Point nextp = new Point(mygrid.getCell(row, col), row, col, curr, 0, 0);
					stack.add(nextp);
					count++;
				}
				row = row - rowNum[i];
				col = col - colNum[i];
			}
		}
		return null;
	}

	public int[] AStar(Grid mygrid) {

		visited = new boolean[mygrid.getN()][mygrid.getM()];

		int[] start = mygrid.getStart();
		int[] end = mygrid.getTerminal();

		if (mygrid.getCell(start[0], start[1]).isWall() || mygrid.getCell(end[0], end[1]).isWall()) {
			return null;
		}

		PriorityQueue<Point> openSet = new PriorityQueue<Point>(new Comparator<Point>() {
			public int compare(Point p1, Point p2) {
				if (p1.getFscore() > p2.getFscore()) {
					return 1;
				} else if (p1.getFscore() < p2.getFscore()) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		Cell initCell = mygrid.getCell(start[0], start[1]);
		Point initPoint = new Point(initCell, start[0], start[1], null, 0, 0);

		initPoint.setGscore(initCell.getCost());
		initPoint.setFscore(initPoint.getGscore() + heuristic(start, end));

		openSet.add(initPoint);

		int count = 1;
		int[] values = new int[2];

		while (!openSet.isEmpty()) {
			Point curr = openSet.poll();
			visited[curr.getX()][curr.getY()] = true;

			if (curr.getCell().isTerminal()) {
				System.out.println("Extended Nodes: " + count);
				int[] steps = shortestPath(curr, mygrid);
				return steps;
			}

			for (int i = 0; i < 4; i++) {
				int row = curr.getX() + rowNum[i];
				int col = curr.getY() + colNum[i];
				values[0] = row;
				values[1] = col;

				if (isFree(row, col, mygrid)) {
					Cell nextc = mygrid.getCell(row, col);
					Point nextp = new Point(nextc, row, col, curr, 0, 0);

					nextp.setGscore(curr.getGscore() + nextc.getCost());
					nextp.setFscore(nextp.getGscore() + heuristic(values, end));

					openSet.add(nextp);
					count++;
				}
				row = row - rowNum[i];
				col = col - colNum[i];
			}
		}
		return null;
	}

	int heuristic(int[] curr, int[] end) {
		return Math.abs(curr[0] - end[0]) + Math.abs(curr[1] - end[1]);
	}

	public int[] LRTAStar(Grid grid) {
		
		visited = new boolean[grid.getN()][grid.getM()];
		
		int[] start = grid.getStart();
		int[] end = grid.getTerminal();

		if (grid.getCell(start[0], start[1]).isWall() || grid.getCell(end[0], end[1]).isWall()) {
			return null;
		}
		Cell initCell = grid.getCell(start[0], start[1]);
		Point initPoint = new Point(initCell, start[0], start[1], null, 0, 0);
		initPoint.setGscore(initCell.getCost() + heuristic(start, end));
		
		visited[initPoint.getX()][initPoint.getY()] = true;
		
		PriorityQueue<Point> list = new PriorityQueue<Point>(new Comparator<Point>() {
			public int compare(Point p1, Point p2) {
				if (p1.getFscore() > p2.getFscore()) {
					return 1;
				} else if (p1.getFscore() < p2.getFscore()) {
					return -1;
				} else {
					return 0;
				}
			}
		});

		Point curP = initPoint;
		Point nextP = null;

		int count = 1;
		while (!curP.getCell().isTerminal()) {
			if (curP != null) {
				for (int i = 0; i < 4; i++) {
					int row = curP.getX() + rowNum[i];
					int col = curP.getY() + colNum[i];

					if (isFree(row, col, grid)) {
						Cell nextc = grid.getCell(row, col);
						nextP = new Point(nextc, row, col, curP, 0, 0);
						
						if (!heuristicTable.containsKey(nextP)) {
							int[] currindex = { nextP.getX(), nextP.getY() };
							heuristicTable.put(nextP, heuristic(currindex, end));
						}
						
						int score = curP.getCell().getCost() + heuristicTable.get(nextP);
						nextP.setFscore(score);
						list.add(nextP);
						count++;
					}
					row = row - rowNum[i];
					col = col - colNum[i];
				}
				Point sPoint = list.poll();
				visited[sPoint.getX()][sPoint.getY()] = true;
				heuristicTable.put(sPoint, curP.getCell().getCost() + sPoint.getCell().getCost());
				curP = sPoint;
			}
		}
		System.out.println("Extended Nodes: " + count);
		return shortestPath(curP, grid);
	}

	public int[] shortestPath(Point end, Grid grid) {

		int cost = end.getCell().getCost();

		ArrayList<Point> path = new ArrayList<>();
		path.add(end);

		Point next = end.getParent();
		while (next != null) {
			path.add(next);
			cost += next.getCell().getCost();
			next = next.getParent();
		}
		System.out.println("Total cost: " + cost);
		System.out.println("Total distance: " + path.size());

		int[] steps = new int[path.size()];

		for (int i = path.size() - 1; i >= 0; i--) {
			Point curr = path.get(i);
			steps[i] = curr.getX() * grid.getM() + curr.getY();
			System.out.print("(" + curr.getX() + "," + curr.getY() + ") ");
			if (i != 0) {
				System.out.print(" --> ");
			} else {
				System.out.print("\n");
			}
		}
		return steps;
	}
}