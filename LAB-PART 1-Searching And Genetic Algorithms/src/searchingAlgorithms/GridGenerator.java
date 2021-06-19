package searchingAlgorithms;
/**
			INTELLIGENCE LAB
	course		: 	COMP 417 - Artificial Intelligence
	authors		:	A. Vogiatzis, N. Trigkas
	excercise	:	1st Programming
	term 		: 	Spring 2019-2020
	date 		:   March 2020
*/
import java.util.Random;

import javax.swing.*;
import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import java.awt.Canvas;

class GridGenerator{
	public static void VisualizeGrid(String frame_name, int N, int M, int [] walls, int [] grass, int start_idx, int terminal_idx ){
		JFrame frame = new JFrame(frame_name);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Canvas canvas = new Drawing(N,M,walls,grass,start_idx,terminal_idx);
		canvas.setSize(M*30,N*30);
		frame.add(canvas);
		frame.pack();
		frame.setVisible(true);
	}
	public static void VisualizeGrid(String frame_name, int N, int M, int [] walls, int [] grass, int [] steps ,int start_idx, int terminal_idx ){
		JFrame frame = new JFrame(frame_name);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Canvas canvas = new Drawing(N,M,walls,grass, steps, start_idx,terminal_idx);
		canvas.setSize(M*30,N*30);
		frame.add(canvas);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		String frame = "Random World";
		Grid mygrid;
		if (args.length<1)
			mygrid = new Grid();
		else if (args[0].equals("-i")){
			mygrid = new Grid(args[1]);
			frame = args[1].split("/")[1];
		}else if (args[0].equals("-d")){
			mygrid = new Grid(Integer.parseInt(args[1]),Integer.parseInt(args[2]));
		}else{
			mygrid = new Grid("world_examples/default.world");
			frame = "default.world";
		}
		mygrid = new Grid("world_examples/hard_c.world");
		int N = mygrid.getNumOfRows();
		int M = mygrid.getNumOfColumns();
		Algorithms a = new Algorithms();
		int[] step1= a.BFS(mygrid);
		VisualizeGrid(frame,N,M,mygrid.getWalls(),mygrid.getGrass(),step1,mygrid.getStartidx(),mygrid.getTerminalidx());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int[] step2= a.DFS(mygrid);
		VisualizeGrid(frame,N,M,mygrid.getWalls(),mygrid.getGrass(),step2,mygrid.getStartidx(),mygrid.getTerminalidx());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int[] step3= a.AStar(mygrid);
		VisualizeGrid(frame,N,M,mygrid.getWalls(),mygrid.getGrass(),step3,mygrid.getStartidx(),mygrid.getTerminalidx());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int[] step4= a.LRTAStar(mygrid);
		VisualizeGrid(frame,N,M,mygrid.getWalls(),mygrid.getGrass(),step4,mygrid.getStartidx(),mygrid.getTerminalidx());
	}
		
}