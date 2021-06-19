package geneticAlgorithm;

import java.util.*;

public class Population {
	private final static int popNum = 1000;
	private final static int iterNum = 1000;
	private final static float elitRate = (float)0.5;
	private final static float bestScore = (float) 50000;
	
	private static LinkedList<Chromosome> popList = new LinkedList<>();
	
	public static void main(String[] args) {
		
		for (int i = 0; i < popNum; i++) {
			Chromosome c = new Chromosome(14, 30, 0);
			c.generateChromosome();
			c.fitness();
			popList.add(c);
		}
		
		Iterator<Chromosome> through1 = popList.iterator();
		
		float averageScore1=0;
		float bestScore1=Integer.MAX_VALUE;
		
		while (through1.hasNext()) {
			
			float temp=through1.next().getScore();
			if(temp<bestScore1) {
				bestScore1=temp;
			}
			averageScore1+=temp;
			
		}
		System.out.println(bestScore1 + "\t" + averageScore1/popNum);
		
		int k=1;
		while(k<=iterNum) {
			float minValue = Integer.MAX_VALUE;
			for (int j = 0; j < popList.size() ; j++) {
				
				Chromosome c1 = RouletteSelection();
				float cromValue=c1.getScore();
				
				if(cromValue<=minValue) {
					minValue=cromValue;
					popList.add(c1);
					c1 = RouletteSelection();
				}
				
				Random r = new Random();
				if (  c1.getScore()<bestScore && r.nextFloat()<elitRate) {
					popList.add(c1);		
				}else {
					
					Chromosome c2 = RouletteSelection();
					popList.add(c2);
					
					Random rand = new Random();
					
					if(rand.nextFloat()<0.8) {
						c1=c1.TwoPointCrossover(c2);
							
							if(rand.nextFloat()<0.1) {
								c1.SwapMutation();
							}
							c1.fitness();
							popList.add(c1);
					}else {
						popList.add(c1);
					}	
				}
			}
			
			if ( k%10==0) {
				Iterator<Chromosome> through = popList.iterator();
				float averageScore2=0;
				float bestScore2=Integer.MAX_VALUE;
				
				while (through.hasNext()) {
					float temp=through.next().getScore();
					if(temp<bestScore2) {
						bestScore2=temp;
					}
					averageScore2+=temp;
				}
				System.out.println( bestScore2 + "\t" + averageScore2/popNum);
			}
			k++;
		}
	}
	
	private static Chromosome RouletteSelection() {
		float totalSum=0;
		Collections.shuffle(popList);
		for (int i = 0; i < popList.size()/100; i++) {
			totalSum+=popList.get(i).getScore();
		}
		
		Random r = new Random();
		float rand= r.nextFloat()*totalSum;
		float partialSum=0;
		
		for (int i = 0; i < popList.size()/100; i++) {
			partialSum+=popList.get(i).getScore();
			
			if(partialSum >= rand) {
				Chromosome curr = popList.get(i);
				popList.remove(i);
				return curr;
			}
		}
		return popList.pollFirst();
	}
	
	private static void sort() {
		
		Collections.sort(popList, new Comparator<Chromosome>() {
			public int compare(Chromosome p1, Chromosome p2) {
				if (p1.getScore() > p2.getScore()) {
					return 1;
				} else if (p1.getScore() < p2.getScore()) {
					return -1;
				} else {
					return 0;
				}
			}
		});
	}
}
