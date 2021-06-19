package geneticAlgorithm;
import java.util.*;

public class Chromosome {
	private int[][] genes;
	private int N;
	private int M;
	private float score;
	
	public Chromosome(int N,int M, int score) {
		this.genes = new int[N][M];
		this.N=N;
		this.M=M;
		this.score=score;
	}
	
	public int getN() {return N;}
	public void setN(int n) {N = n;}

	public int getM() {return M;}
	public void setM(int m) {M = m;}

	public int[][] getGenes() {return genes;}
	public void setGenes(int[][] genes) {this.genes = genes;}
	
	public float getScore() {return score;}
	public void setScore(float score) {this.score = score;}
	
	public void generateChromosome() {
		
		for (int i = 0; i < this.N; i++) {
			ArrayList<Integer> row = new ArrayList<>();
			for (int j = 0; j < this.M; j++) {
				
				if(i == 0 || i == 1 || i==7 || i==8) {
					if(j<10){
						row.add(1);
					}else if (j>=10 && j<20) {
						row.add(2);
					}else if(j>=20 && j<25){
						row.add(3);
					}else {
						row.add(0);
					}
					Collections.shuffle(row);
					genes[i]=toArray(row);
				}
				
				if(i == 2 || i == 4 || i==9 || i==11) {
					if(j<5){
						row.add(1);
					}else if (j>=5 && j<15) {
						row.add(2);
					}else if(j>=15 && j<20){
						row.add(3);
					}else {
						row.add(0);
					}
					Collections.shuffle(row);
					genes[i]=toArray(row);
				}
				
				if(i == 3 || i == 5 || i == 6 || i == 10 || i == 12 || i == 13) {
					if(j<5){
						row.add(1);
					}else if (j>=5 && j<10) {
						row.add(2);
					}else if(j>=10 && j<15){
						row.add(3);
					}else {
						row.add(0);
					}
					Collections.shuffle(row);
					genes[i]=toArray(row);
				}
			}
		}
	}
	
	private int[] toArray(ArrayList<Integer> arraylsit) {
		
		int[] array = new int[arraylsit.size()];

		for(int i=0; i<arraylsit.size();i++) {
			array[i]=arraylsit.get(i);
		}
		return array;
	}
	
	public void printChromosome() {
		for (int i = 0; i < this.N; i++) {
			for (int j = 0; j < this.M; j++) {
				System.out.print(genes[i][j] + " ");
			}
			System.out.print("\n");
		}
	}
	
	public static void shuffleArray(int[] array) {
		
		  List<Integer> list = new ArrayList<>();
		  for (int i=0;i<array.length;i++) {
		    list.add(array[i]);
		  }
		
		  Collections.shuffle(list);

		  for (int i = 0; i < list.size(); i++) {
		    array[i] = list.get(i);
		  }    
	}
	
	public void fitness() {
		
		float totalScore = 0;
		
		SoftConstraints sc = new SoftConstraints();
		
		totalScore+=sc.maxHoursOfWork(this);
		totalScore+=sc.maxContinuousDays(this);
		totalScore+=sc.maxContinuousNights(this);
		totalScore+=sc.NightAndMorningShift(this);
		totalScore+=sc.AfternoonAndMorningShift(this);
		totalScore+=sc.NightAndAfternoonShift(this);
		totalScore+=sc.DayOffAfterFourNights(this);
		totalScore+=sc.DayOffAfterSevenDays(this);
		totalScore+=sc.WorkDayOffWork(this);
		totalScore+=sc.DayOffWorkDayOff(this);
		totalScore+=sc.maxOneWeekend(this);
		
		this.setScore(totalScore);
		return;
	}
	
	public Chromosome UniformCrossover(Chromosome chr){
		int[][]chr1 = this.getGenes();
		int[][]chr2 = chr.getGenes();
		
		int[][]temp1 = new int[N][M];
		
		Random rand = new Random();
		
		for (int i = 0; i < this.getN(); i++) {
			int coin = rand.nextInt(2);
			if(coin==0) {
				temp1[i]=chr1[i];
			}else {
				temp1[i]=chr2[i];
			}
		}
		
		this.setGenes(temp1);
		
		return this;
	}
	
	public Chromosome TwoPointCrossover(Chromosome chr){
		int[][]chr1 = this.getGenes();
		int[][]chr2 = chr.getGenes();
		
		int[][]temp1 = new int[N][M];
		
		Random rand = new Random();
		int point1 = rand.nextInt(7);
		int point2 = rand.nextInt(7)+7;
		
		for (int i = 0; i < this.getN(); i++) {
			if(i>=point1 && i<=point2) {
				temp1[i]=chr2[i];
			}else {
				temp1[i]=chr1[i];
			}
		}
		
		this.setGenes(temp1);	
		
		return this;
	}

	public Chromosome UniformMutation(){
		int[][]chr1 = this.getGenes();
		
		int[][]temp1 = new int[N][M];
		
		Random rand = new Random();
		
		for (int i = 0; i < this.getN(); i++) {
			int coin = rand.nextInt(1);
			if(coin==0) {
				temp1[i]=chr1[i];
			}else {
				shuffleArray(chr1[i]);
				temp1[i]=chr1[i];
			}
		}
		this.setGenes(temp1);
		
		return this;
	}
	
	public Chromosome SwapMutation(){
		int[][]chr1 = this.getGenes();
		
		int[][]temp1 = new int[N][M];
		
		Random rand = new Random();
		
		for (int i = 0; i < this.getN()/2; i++) {
			int coin = rand.nextInt(1);
			if(coin==0 ) {
				temp1[i]=chr1[i+7];
				temp1[i+7]=chr1[i];
			}else {
				temp1[i]=chr1[i];
				temp1[i+7]=chr1[i+7];
			}
		}
		
		this.setGenes(temp1);
		
		return this;
	}
}
