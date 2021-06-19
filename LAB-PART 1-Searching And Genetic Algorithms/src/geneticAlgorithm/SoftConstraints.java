package geneticAlgorithm;

public class SoftConstraints {
	public static final float totalWeight = 30;
	public static final int day_off = 0;
	public static final int morning = 1;
	public static final int afternoon = 2;
	public static final int night = 3;
	
	public float maxHoursOfWork(Chromosome chromosome) {

		int[][] chr=chromosome.getGenes();
		int totalCount=0;
		
		for (int j=0; j<chromosome.getM(); j++) {
			int totalHours = 0;
			for (int i=0; i<chromosome.getN(); i++) {
				if (chr[i][j]!=day_off) {
					totalHours+=8;
				}
			}
			if (totalHours>70) {
				totalCount+=1000;
			}
		}
		return totalCount;
	}

	public float maxContinuousDays(Chromosome chromosome) {

		int[][] chr=chromosome.getGenes();
		int totalCount=0;
		
		for (int j=0; j<chromosome.getM(); j++) {
			int totalDays=0;
			for (int i=0; i<chromosome.getN(); i++) {
				if (chr[i][j]!=day_off) {
					totalDays+=1;
				}else {
					totalDays=0;
				}
			}
			if (totalDays>7) {
				totalCount+=1000;
			}
		}
		return totalCount;
	}
	
	public float maxContinuousNights(Chromosome chromosome) {

		int[][] chr=chromosome.getGenes();
		int totalCount=0;
		
		for (int j=0; j<chromosome.getM(); j++) {
			int totalDays = 0;
			for (int i=0; i<chromosome.getN(); i++) {
				if (chr[i][j]==night) {
					totalDays+=1;
				}else {
					totalDays=0;
				}
			}
			if (totalDays>4) {
				totalCount+=1000;
			}
		}
		return totalCount;
	}
	
	public float NightAndMorningShift(Chromosome chromosome) {

		int[][] chr=chromosome.getGenes();
		int totalCount=0;
		
		for (int j=0; j<chromosome.getM(); j++) {
			for (int i=0; i<chromosome.getN(); i++) {
				if (chr[i][j]==night && i+1<14 && chr[i+1][j]==morning) {
					totalCount+=1000;
				}
			}
		}
		return totalCount;
	}
	
	public float AfternoonAndMorningShift(Chromosome chromosome) {

		int[][] chr=chromosome.getGenes();
		int totalCount=0;
		
		for (int j=0;  j<chromosome.getM(); j++) {
			for (int i=0; i<chromosome.getN(); i++) {
				if (chr[i][j]==afternoon && i+1<14 && chr[i+1][j]==morning) {
					totalCount+=800;
				}
			}
		}
		return totalCount;
	}
	
	public float NightAndAfternoonShift(Chromosome chromosome) {

		int[][] chr=chromosome.getGenes();
		int totalCount=0;
		
		for (int j=0; j<chromosome.getM(); j++) {
			for (int i=0; i<chromosome.getN(); i++) {
				if (chr[i][j]==night && i+1<chromosome.getN() && chr[i+1][j]==afternoon) {
					totalCount+=800;
				}
			}
		}
		return totalCount;
	}
	
	public float DayOffAfterFourNights(Chromosome chromosome) {

		int[][] chr=chromosome.getGenes();
		int totalCount=0;
		
		for (int j=0; j<chromosome.getM(); j++) {
			int totalNights=0;
			for (int i=0; i<chromosome.getN(); i++) {
				if (chr[i][j]==night && totalNights<4) {
					totalNights+=1;
				}else if (chr[i][j]==night && totalNights==4) {
					if(i+2<chromosome.getN() && chr[i+1][j]==day_off && chr[i+2][j]==day_off) {
						totalCount+=0;
					}else {
						totalCount+=100;
					}
				}else {
					totalNights=0;
				}
			}
		}
		return totalCount;
	}
	
	public float DayOffAfterSevenDays(Chromosome chromosome) {

		int[][] chr=chromosome.getGenes();
		int totalCount=0;
		
		for (int j=0; j<chromosome.getM(); j++) {
			int totalDays=0;
			for (int i=0; i<chromosome.getN(); i++) {
				if (chr[i][j]!=day_off && totalDays<7) {
					totalDays+=1;
				}else if (chr[i][j]!=day_off && totalDays==7) {
					if (i+2<chromosome.getN() && chr[i+1][j]==day_off && chr[i+2][j]==day_off) {
						totalCount+=0;
					} else {
						totalCount+=100;
					}
				}else {
					totalDays=0;
				}
			}
		}
		return totalCount;
	}
	
	public float WorkDayOffWork(Chromosome chromosome) {

		int[][] chr=chromosome.getGenes();
		int totalCount=0;
		
		for (int j=0; j<chromosome.getM(); j++) {
			for (int i=0; i<chromosome.getN(); i++) {
				if(chr[i][j]!=day_off && i+2<chromosome.getN() && chr[i+1][j]==day_off && chr[i+2][j]!=day_off) {
					totalCount+=1;
				}
			}
		}
		return totalCount;
	}
	
	public float DayOffWorkDayOff(Chromosome chromosome) {

		int[][] chr=chromosome.getGenes();
		int totalCount=0;
		
		for (int j=0; j<chromosome.getM(); j++) {
			for (int i=0; i<chromosome.getN(); i++) {
				if(chr[i][j]==day_off && i+2<chromosome.getN() && chr[i+1][j]!=day_off && chr[i+2][j]==day_off) {
					totalCount+=1;
				}
			}
		}
		return totalCount;
	}
	
	public float maxOneWeekend(Chromosome chromosome) {

		int[][] chr=chromosome.getGenes();
		int totalCount=0;
		
		for (int j=0; j<chromosome.getM(); j++) {
			if( (chr[5][j]!=day_off || chr[6][j]!=day_off) && (chr[12][j]!=day_off || chr[13][j]!=day_off)) {
				totalCount+=1;
			}
		}
		return totalCount;
	}
}