import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Algorithms {

	public Node BFS(Node node) {

		Queue<Node> queue = new LinkedList<>();
		queue.add(node);

		while (!queue.isEmpty()) {
			Node curr = queue.poll();

			if (curr.getState().isTerminal()) {
				return curr;
			}
			List<Node> children = curr.childrenNodes();

			for (Node child : children) {
				if (!child.isVisited()) {
					child.setVisited(true);
					queue.add(child);
				}
			}
		}
		return null;
	}
}
