import java.util.ArrayList;


public class Main {

	public static void main(String[] args) {
		
		Node initialNode = new Node(new State(3,3,0,0,BoatPosition.LEFT),null);
		
		Algorithms algorithms = new Algorithms();
		Node terminal = algorithms.BFS(initialNode);
		printSolution(terminal);		
	}

	private static void printSolution(Node terminal) {
		
		if(terminal==null) {
			System.out.println("There is no solution!");
			return;
		}
		ArrayList<Node> path = new ArrayList<>();
		path.add(terminal);

		Node next = terminal.getParentNode();
		while (next != null) {
			path.add(next);
			next = next.getParentNode();
		}
		
		System.out.println("Total States: " + path.size());

		for (int i = path.size() - 1; i >= 0; i--) {
			State curr = path.get(i).getState();
			System.out.println(curr.toString());
		}
		
	}
}
