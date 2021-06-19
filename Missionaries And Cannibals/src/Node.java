import java.util.ArrayList;
import java.util.List;


public class Node {
	private final static int numOfMis[] = { 1, 0, 1, 2, 0};
	private final static int numOfCan[] = { 0, 1, 1, 0, 2};
	
	private State state;
	private boolean isVisited=false;
	private Node parentNode;
	
	public Node(State state, Node parentNode) {
		this.state = state;
		this.parentNode = parentNode;
	}

	public boolean isVisited() {return isVisited;}
	public void setVisited(boolean isVisited) {this.isVisited = isVisited;}

	public State getState() { return state; }
	public void setState(State state) { this.state = state; }

	public Node getParentNode() { return parentNode; }
	public void setParentNode(Node parentNode) { this.parentNode = parentNode; }
	
	public List<Node> childrenNodes(){
		
		State parentS = this.getState();
		List<Node> children = new ArrayList<>();
		
		if(parentS.isOnLeft()) {
			for (int i=0; i<numOfCan.length;i++) {
				
				State childS = new State(parentS.getLeftMis()-numOfMis[i],parentS.getLeftCan()-numOfCan[i],
								         parentS.getRightMis()+numOfMis[i],parentS.getRightCan()+numOfCan[i],BoatPosition.RIGHT);
				if(childS.isValid()) {
					Node childN = new Node(childS, this);
					children.add(childN);
				}
			}
		}else {
			for (int i=0; i<numOfCan.length;i++) {
				State childS = new State(parentS.getLeftMis()+numOfMis[i],parentS.getLeftCan()+numOfCan[i],
								         parentS.getRightMis()-numOfMis[i],parentS.getRightCan()-numOfCan[i],BoatPosition.LEFT);
				if(childS.isValid()) {
					Node childN = new Node(childS, this);
					children.add(childN);
				}
			}
		}
		return children;
	}
}
