
public class State {
	private int leftMis;
	private int leftCan;
	private int rightMis;
	private int rightCan;
	private BoatPosition boatPos;
	
	public State(int leftMis, int leftCan, int rightMis, int rightCan, BoatPosition boatPos) {
		this.leftMis=leftMis;
		this.leftCan=leftCan;
		this.rightMis=rightMis;
		this.rightCan=rightCan;
		this.boatPos = boatPos;
	}

	public BoatPosition getBoatPos() { return boatPos; }
	public void setBoatPos(BoatPosition boatPos) { this.boatPos = boatPos; }
	
	public int getLeftMis() {return leftMis;}
	public void setLeftMis(int leftMis) {this.leftMis = leftMis;}

	public int getLeftCan() {return leftCan;}
	public void setLeftCan(int leftCan) {this.leftCan = leftCan;}
	
	public int getRightMis() {return rightMis;}
	public void setRightMis(int rightMis) {this.rightMis = rightMis;}

	public int getRightCan() {return rightCan;}
	public void setRightCan(int rightCan) {this.rightCan = rightCan;}

	public void goToLeft() { boatPos = BoatPosition.LEFT; }
	public void goToRight() { boatPos = BoatPosition.RIGHT; }
	
	public boolean isOnLeft() { return boatPos == BoatPosition.LEFT; }
	public boolean isOnRigth() { return boatPos == BoatPosition.RIGHT; }
	
	public String toString() {
		if (boatPos == BoatPosition.LEFT) {
			return "[M: "+ this.getLeftMis() + ", C: " + this.getLeftCan() + "]" + 
				   " B: " + this.getBoatPos() + " ------------- " + 
				   "[M: "+ this.getRightMis() + ", C: " + this.getRightCan() + "]";
		}else {
			return "[M: "+ this.getLeftMis() + ", C: " + this.getLeftCan() + "]" + 
				   " ------------ " + "B: "  + this.getBoatPos() + 
				   " [M: "+ this.getRightMis() + ", C: " + this.getRightCan() + "]";
		}
	}
	
	public boolean isTerminal() {
        return (this.getLeftMis() == 0 && this.getLeftCan() == 0 &&
        		this.boatPos == BoatPosition.RIGHT && 
        		this.getRightMis() == 3 && this.getRightCan() == 3);
	}
	
	public boolean isValid() {		
		return ( (this.getLeftMis()>=0 && this.getLeftMis()<= 3) && 
				 (this.getLeftCan()>=0 && this.getLeftCan()<= 3) && 
				 (this.getRightMis()>=0 && this.getRightMis()<= 3) && 
				 (this.getRightCan()>=0 && this.getRightCan()<= 3) && 
				 (this.getLeftMis()==0 || this.getLeftMis()>=this.getLeftCan()) &&
				 (this.getRightMis()==0 || this.getRightMis()>=this.getRightCan()));
	}	
}