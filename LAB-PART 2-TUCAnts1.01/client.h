#ifndef _CLIENT_H
#define _CLIENT_H

#include "global.h"
#include "board.h"
#include "move.h"
#include "comm.h"
#include "LinkedList.h"
#include "HashMap.h"
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <unistd.h>

#define MAX_DEPTH 9
#define MAX_ADDITIONAL_DEPTH 5
/**********************************************************/
const char tieBreakBoard[BOARD_ROWS][BOARD_COLUMNS] = {
		0 ,	  4 ,   0 ,   4 ,   0 ,   4 ,   0 ,   4 ,
		4 ,   0 ,   3 ,   0 ,   3 ,   0 ,   3 ,   0 ,
		0 ,	  3 ,   0 ,   2 ,   0 ,   2 ,   0 ,   4 ,
		4 ,   0 ,   2 ,   0 ,   2 ,   0 ,   3 ,   0 ,
		0 ,	  3 ,   0 ,   3 ,   0 ,   3 ,   0 ,   4 ,
		4 ,   0 ,   4 ,   0 ,   4 ,   0 ,   4 ,   0 ,
		0 ,	  4 ,   0 ,   4 ,   0 ,   4 ,   0 ,   4 ,
		4 ,   0 ,   3 ,   0 ,   3 ,   0 ,   3 ,   0 ,
		0 ,	  3 ,   0 ,   2 ,   0 ,   2 ,   0 ,   4 ,
		4 ,   0 ,   2 ,   0 ,   2 ,   0 ,   3 ,   0 ,
		0 ,	  3 ,   0 ,   3 ,   0 ,   3 ,   0 ,   4 ,
		4 ,   0 ,   4 ,   0 ,   4 ,   0 ,   4 ,   0 ,

	};
/**********************************************************/

int minimax( Position* position, int depth, int isMaximizing);

int alpha_beta_pruning(Position* position, int depth, int a, int b, int isMaximizing);

int Quiescence(Position* position, int depth, int alpha, int beta);

int iterative_deepening_search_with_a_b(Position* pos, int maxDepth, int isMaximizing);

int iterative_deepening_search(Position* position, int maxDepth, int isMaximizing);

int MTDF(Position* position, int firstGuess, int depth, int isMaximizing);

int alpha_beta_with_memory(Position* position, int alpha, int beta, int depth, int isMaximizing);

int evaluteBoard(Position* position);

int AntInDanger(Position* position,int i,int j);

void setMove(char tiles[2][MAXIMUM_MOVE_SIZE], char curRow, char curCol, char nextRow, char nextCol);

void setJump(char tiles[2][MAXIMUM_MOVE_SIZE], char nextRow, char nextCol, char numberofJumps);

node_t* getAllMoves(Position* curPos);

node_t* getAllJumps(Position* curPos);

int checkForVictory(Position* pos);

void printMove(Move* move, int value);
#endif
