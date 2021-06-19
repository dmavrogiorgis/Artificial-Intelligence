#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <unistd.h>
#include <limits.h>
#include "global.h"
#include "board.h"
#include "move.h"
#include "comm.h"
#include "client.h"
#include "LinkedList.h"
#include "HashMap.h"
#include "ZobristTable.h"

/**********************************************************/
Position gamePosition;		// Position we are going to use

Move moveReceived;			// temporary move to retrieve opponent's choice
Move myMove;				// move to save our choice and send it to the server
int myMoveScore;

char myColor;				// to store our color
int mySocket;				// our socket

char * agentName = "Ants TUC Master";		//default name.. change it! keep in mind MAX_NAME_LENGTH

char * ip = "127.0.0.1";	// default ip (local machine)

hashtable_t* zobristHash;

int currentDepth;
/**********************************************************/

int main( int argc, char ** argv ){
	initTable();
	zobristHash = ht_create(10000000);

	int c;
	opterr = 0;

	while( ( c = getopt ( argc, argv, "i:p:h" ) ) != -1 )
		switch( c )
		{
			case 'h':
				printf( "[-i ip] [-p port]\n" );
				return 0;
			case 'i':
				ip = optarg;
				break;
			case 'p':
				port = optarg;
				break;
			case '?':
				if( optopt == 'i' || optopt == 'p' )
					printf( "Option -%c requires an argument.\n", ( char ) optopt );
				else if( isprint( optopt ) )
					printf( "Unknown option -%c\n", ( char ) optopt );
				else
					printf( "Unknown option character -%c\n", ( char ) optopt );
				return 1;
			default:
			return 1;
		}

	connectToTarget( port, ip, &mySocket );

	char msg;
 /**********************************************************/
 // used in random
	srand( time( NULL ) );
	int i, j, k;
	int jumpPossible;
	int playerDirection;
 /**********************************************************/

	while( 1 )
	{
		msg = recvMsg( mySocket );
		switch ( msg )
		{
			case NM_REQUEST_NAME:		//server asks for our name
				sendName( agentName, mySocket );
				break;

			case NM_NEW_POSITION:		//server is trying to send us a new position
				getPosition( &gamePosition, mySocket );
				printPosition( &gamePosition );
				break;

			case NM_COLOR_W:			//server indorms us that we have WHITE color
				myColor = WHITE;
				printf("My color is %d\n",myColor);
				break;

			case NM_COLOR_B:			//server indorms us that we have BLACK color
				myColor = BLACK;
				printf("My color is %d\n",myColor);
				break;

			case NM_PREPARE_TO_RECEIVE_MOVE:	//server informs us that he will send opponent's move
				getMove( &moveReceived, mySocket );
				moveReceived.color = getOtherSide( myColor );
				doMove( &gamePosition, &moveReceived );		//play opponent's move on our position
				printPosition( &gamePosition );
				break;

			case NM_REQUEST_MOVE:		//server requests our move
				myMove.color = myColor;
				assert(gamePosition.turn==myMove.color);
				if(gamePosition.turn==BLACK){
					myMoveScore = INT_MIN;
					//iterative_deepening_search_with_a_b(&gamePosition, MAX_DEPTH, TRUE);
					currentDepth = MAX_DEPTH;
					alpha_beta_pruning(&gamePosition, currentDepth, INT_MIN, INT_MAX, TRUE);
					//MTDF(&gamePosition, 0, currentDepth, TRUE);
					//iterative_deepening_search(&gamePosition, MAX_DEPTH, TRUE);
				}else{
					myMoveScore = INT_MAX;
					//iterative_deepening_search_with_a_b(&gamePosition, MAX_DEPTH, FALSE);
					currentDepth = MAX_DEPTH;
					alpha_beta_pruning(&gamePosition, currentDepth, INT_MIN, INT_MAX, FALSE);
					//MTDF(&gamePosition, 0, currentDepth, FALSE);
					//iterative_deepening_search(&gamePosition, MAX_DEPTH, FALSE);
				}

				sendMove( &myMove, mySocket );			//send our move
				//printf("i chose to go from (%d,%d), to (%d,%d)\n",myMove.tile[0][0],myMove.tile[1][0],myMove.tile[0][1],myMove.tile[1][1]);
				doMove( &gamePosition, &myMove );		//play our move on our position
				printPosition( &gamePosition );
				assert(gamePosition.turn!=myColor);
				break;

			case NM_QUIT:			//server wants us to quit...we shall obey
				close( mySocket );
				return 0;
		}

	}

	return 0;
}

int minimax(Position* position, int depth, int isMaximizing){
	if (depth==0 || checkForVictory(position)){
		return evaluteBoard(position);
	}

	if(isMaximizing){
		//printf(" MAX \n");
		int maxValue = INT_MIN;
		node_t* posMoves = getAllMoves(position);
		Move bestMove;

		while(!isEmpty(posMoves)){

			Move curMove = pop_front(&posMoves);
			Position* tempPos = (Position*)malloc(sizeof(Position));
			memcpy(tempPos, position, sizeof(Position));
			doMove(tempPos, &curMove);

			int val = minimax(tempPos, depth-1, FALSE);
			free(tempPos);

			if (val > maxValue){
				maxValue = val;
				bestMove = curMove;
			}
		}

		myMove = bestMove;
		delete_list(posMoves);
		return maxValue;
	}else{
		//printf(" MIN \n");
		int minValue = INT_MAX;
		node_t* posMoves = getAllMoves(position);
		Move bestMove;

		while(!isEmpty(posMoves)){

			Move curMove = pop_front(&posMoves);
			Position* tempPos = (Position*)malloc(sizeof(Position));
			memcpy(tempPos, position, sizeof(Position));
			doMove(tempPos, &curMove);

			int val= minimax(tempPos, depth-1, TRUE);
			free(tempPos);

			if(val<minValue){
				minValue=val;
				bestMove=curMove;
			}
		}
		myMove = bestMove;
		delete_list(posMoves);
		return minValue;
	}
}

int alpha_beta_pruning(Position* position, int depth, int a, int b, int isMaximizing){
	int val, maxValue, minValue;

	if (depth==0 || checkForVictory(position)){
		return Quiescence(position, 1, a, b);
	}

	if(isMaximizing){
		//printf(" MAX \n");
		maxValue = INT_MIN;
		node_t* posMoves = getAllMoves(position);
		rearrange(&posMoves);

		while(!isEmpty(posMoves)){

			Move curMove = pop_front(&posMoves);
			assert(position->turn==curMove.color);
			Position* tempPos = (Position*)malloc(sizeof(Position));
			memcpy(tempPos, position, sizeof(Position));
			doMove(tempPos, &curMove);
			assert(tempPos->turn!=position->turn);
			val = alpha_beta_pruning(tempPos, depth-1, a, b, FALSE);
			free(tempPos);
			//printf(" MAX V: %d - D: %d ", val, depth);
			if (val > maxValue){
				maxValue = val;
				//printf(" MAX VALUE: %d \n", maxValue);
				if(depth == currentDepth && val > myMoveScore){
					printMove(&curMove, val);
					myMove = curMove;	// Only if we are in the initial call we take the best move
					myMoveScore = val;
				}
			}
			a = max(a, val);
			if(a >= b)
				break;
		}
		delete_list(posMoves);
		//printf(" RETURN MAX VALUE: %d \n", maxValue);
		return maxValue;
	}else{
		//printf(" MIN \n");
		minValue = INT_MAX;
		node_t* posMoves = getAllMoves(position);
		rearrange(&posMoves);
		Move bestMove;

		while(!isEmpty(posMoves)){

			Move curMove = pop_front(&posMoves);
			assert(position->turn==curMove.color);
			Position* tempPos = (Position*)malloc(sizeof(Position));
			memcpy(tempPos, position, sizeof(Position));
			doMove(tempPos, &curMove);
			assert(tempPos->turn!=position->turn);
			val = alpha_beta_pruning(tempPos, depth-1, a, b, TRUE);
			free(tempPos);
			//printf(" MIN V: %d - D: %d ", val, depth);
			if(val<minValue){
				minValue = val;
				//printf(" MIN VALUE: %d \n", minValue);
				if (depth == currentDepth && val < myMoveScore){
					printMove(&curMove, val);
					myMove = curMove; // Only if we are in the initial call we take the best move
					myMoveScore = val;
				}
			}
			b = min(b, val);
			if(a >= b)
				break;
		}
		delete_list(posMoves);
		//printf(" RETURN MIN VALUE: %d \n", minValue);
		return minValue;
	}
}

int Quiescence(Position* pos, int depth, int alpha, int beta){

	if( depth == MAX_ADDITIONAL_DEPTH || checkForVictory(pos))
		return evaluteBoard(pos);

	int score = evaluteBoard(pos);

	if(score >= beta)
		return beta;

	if(alpha < score)
		alpha = score;

	score = INT_MIN;
	node_t* posJumps = getAllJumps(pos);

	while(!isEmpty(posJumps)){
		Move curJump = pop_front(&posJumps); //Pick next move
		Position* tempPos = (Position*)malloc(sizeof(Position));
		memcpy(tempPos, pos, sizeof(Position));
		doMove(tempPos, &curJump);

		score = Quiescence(tempPos, depth + 1, alpha, beta);
		free(tempPos);

		if(score > alpha){
			if (score >= beta){
				delete_list(posJumps);
				return beta;
			}
			alpha = score;
		}
	}
	delete_list(posJumps);
	return alpha;
}

int iterative_deepening_search_with_a_b(Position* pos, int maxDepth, int isMaximizing){
	int value;
	for (int d = 1; d < maxDepth; d++){
		currentDepth = d;
		value = alpha_beta_pruning(pos, currentDepth, INT_MIN, INT_MAX, isMaximizing);
	}
	return value;
}

int iterative_deepening_search(Position* pos, int maxDepth, int isMaximizing){
	int firstGuess = 0;
	for (int d = 0; d < maxDepth; d++) {
		currentDepth = d;
		firstGuess = MTDF(pos, firstGuess, currentDepth, isMaximizing);
	}
	return firstGuess;
}

int MTDF(Position* position, int firstGuess, int depth, int isMaximizing){
	int guess = firstGuess;
	int upperBound = INT_MAX;
    int lowerBound = INT_MIN;

	int beta;
	currentDepth=depth;
	do{
		beta = max(guess, lowerBound + 1 );

		guess = alpha_beta_with_memory(position, beta-1, beta, depth, isMaximizing);

		if(guess < beta)
			upperBound = guess;
		else
			lowerBound = guess;
	}while (lowerBound < upperBound);

	return guess;
}

int alpha_beta_with_memory(Position* position, int alpha, int beta, int depth, int isMaximizing){
	int value, best;
	Move bestMove;

	unsigned long long int key = computeHash(position->board);

	data_t* node = ht_get(zobristHash, key);

	//printf("%llu\n",key );
	if(node != NULL && node->depth >= currentDepth){
		if(node->lowerBound == node->upperBound){
			return node->bestValue; // stored value is exact
		}
		if(node->lowerBound >= beta){
			return node->lowerBound;
		}
		if(node->upperBound <= alpha){
			return node->upperBound;
		}
		alpha = max(alpha, node->lowerBound);
		beta = min(beta, node->upperBound);
		//if(node->lowerBound > beta && node->upperBound > beta){
			//return node->bestValue;}
	}

	if(depth==0 || checkForVictory(position)){

		value = Quiescence(position, 1 , alpha, beta);

		node = (data_t*)malloc(sizeof(data_t));

		node->lowerBound = alpha;
		node->upperBound = beta;
		node->bestValue = value;
		node->depth = currentDepth;

		ht_put(zobristHash, key, node);

		return value;
	}
	if(isMaximizing){
		best = INT_MIN;

		node_t* posMoves = getAllMoves(position);
		rearrange(&posMoves);

		while (!isEmpty(posMoves)){

			Move curMove = pop_front(&posMoves);
			assert(position->turn==curMove.color);

			Position* tempPos = (Position*)malloc(sizeof(Position));
			memcpy(tempPos, position, sizeof(Position));
			doMove(tempPos, &curMove);
			assert(tempPos->turn!=position->turn);

			value = alpha_beta_with_memory(tempPos, alpha, beta, depth-1, FALSE);
			free(tempPos);

			if(value > best){
				best = value;
				if(depth == currentDepth){
					myMove = curMove;
				}
			}
			alpha = max(alpha, value);
			if(alpha >= beta)
   				break;
		}
		delete_list(posMoves);
	}else{
		best = INT_MAX;

		node_t* posMoves = getAllMoves(position);
		rearrange(&posMoves);

		assert(posMoves!=NULL);
		while (!isEmpty(posMoves)){

			Move curMove = pop_front(&posMoves);
			assert(position->turn==curMove.color);
			assert(isLegal(position,&curMove));
			Position* tempPos = (Position*)malloc(sizeof(Position));

			memcpy(tempPos, position, sizeof(Position));
			doMove(tempPos, &curMove);
			assert(tempPos->turn!=position->turn);

			value = alpha_beta_with_memory(tempPos, alpha, beta, depth-1, TRUE);
			free(tempPos);

			if(value < best){
				best = value;
				if(depth == currentDepth){
					myMove = curMove;
				}
			}
			beta = min(beta, value);
			if(alpha >= beta)
   				break;
		}
		delete_list(posMoves);
	}

	node = (data_t*)malloc(sizeof(data_t));
	if(value <= alpha){
		node->lowerBound = alpha;
		node->upperBound = value;
		node->bestValue = value;
		node->depth = currentDepth-depth;
	}

	if(value >= beta){
		node->lowerBound = value;
		node->upperBound = beta;
		node->bestValue = value;
		node->depth = currentDepth-depth;
	}

	if(value > alpha && value < beta){
		node->lowerBound = value;
		node->upperBound = value;
		node->bestValue = value;
		node->depth = currentDepth-depth;
	}
	ht_put(zobristHash, key, node);

	return best;
}

node_t* getAllMoves(Position* curPos){
	node_t* head = NULL;
	char tiles[2][MAXIMUM_MOVE_SIZE];

	if( !canMove( curPos, curPos->turn ) )
	{

		tiles[0][0]=-1;	 //null move  TODOOOOOOOOOOOOOOO
		push_front(&head, tiles, curPos->turn);
		return head;
	}else{
		int playerDirection;
		int jumpPossible;

		if( curPos->turn == WHITE )		// find movement's direction
			playerDirection = 1;
		else
			playerDirection = -1;

		jumpPossible = FALSE;		// determine if we have a jump available

		for(int i = 0; i < BOARD_ROWS; i++ )
		{
			for(int j =(i+1)%2 ; j < BOARD_COLUMNS; j+=2 )
			{
				if( curPos->board[ i ][ j ] == curPos->turn )
				{
					if( canJump( i, j, curPos->turn, curPos )!=0 ){
						jumpPossible = TRUE;
					}
				}
			}
		}

		for(int i = 0; i < BOARD_ROWS; i++ )
		{
			for(int j =(i+1)%2 ; j < BOARD_COLUMNS; j+=2 )
			{
				if( curPos->board[ i ][ j ] == curPos->turn )		//find a piece of ours
				{
					if (jumpPossible == FALSE) {
						setMove(tiles, i, j, i + 1 * playerDirection, j - 1); //LEFT MOVE

						if(isLegalVersionTuc(curPos, tiles, curPos->turn)){
							push_front(&head, tiles, curPos->turn);
						}

						setMove(tiles,i, j, i + 1 * playerDirection, j + 1); //RIGHT MOVE
						if(isLegalVersionTuc(curPos, tiles, curPos->turn)){
							push_front(&head, tiles, curPos->turn);
						}

					}else{
						if(canJump(i, j, curPos->turn, curPos)){
							int row=i;
							int col=j;
							tiles[0][0]=row;
							tiles[1][0]=col;

							int k=1;
							while(canJump(row, col, curPos->turn, curPos)!=0){

								if(canJump(row, col, curPos->turn, curPos) % 2 == 1){
									setJump(tiles, row + 2 * playerDirection, col - 2, k); //LEFT MOVE

								}else if(canJump(row, col, curPos->turn, curPos) > 1){
									setJump(tiles, row + 2 * playerDirection, col + 2, k); //RIGHT MOVE
								}
								if( k + 1 == MAXIMUM_MOVE_SIZE )	//maximum tiles reached
									break;

								tiles[0][k+1]=-1;

								row = tiles[0][k];
								col = tiles[1][k];
								k++;
							}
							push_front(&head, tiles, curPos->turn);
						}
					}
				}
			}
		}
		return head;
	}
}

node_t* getAllJumps(Position* curPos){

	node_t* head = NULL;
	char tiles[2][MAXIMUM_MOVE_SIZE];

	if( !canMove( curPos, curPos->turn ) )
	{
		tiles[0][0]=-1;	 //null move  TODOOOOOOOOOOOOOOO
		push_front(&head, tiles, curPos->turn);
		return head;
	}else{

		int playerDirection;

		if( curPos->turn == WHITE )		// find movement's direction
			playerDirection = 1;
		else
			playerDirection = -1;

		for(int i = 0; i < BOARD_ROWS; i++ )
		{
			for(int j =(i+1)%2 ; j < BOARD_COLUMNS; j+=2 )
			{
				if( curPos->board[ i ][ j ] == curPos->turn )		//find a piece of ours
				{
					if(canJump(i, j, curPos->turn, curPos)){
						int row=i;
						int col=j;
						tiles[0][0]=row;
						tiles[1][0]=col;

						int k=1;
						while(canJump(row, col, curPos->turn, curPos)!=0){

							if(canJump(row, col, curPos->turn, curPos) % 2 == 1){
								setJump(tiles, row + 2 * playerDirection, col - 2, k); //LEFT MOVE

							}else if(canJump(row, col, curPos->turn, curPos) > 1){
								setJump(tiles, row + 2 * playerDirection, col + 2, k); //RIGHT MOVE
							}
							if( k + 1 == MAXIMUM_MOVE_SIZE )	//maximum tiles reached
								break;

							tiles[0][k+1]=-1;

							row = tiles[0][k];
							col = tiles[1][k];
							k++;
						}
						push_front(&head, tiles, curPos->turn);
					}
				}
			}
		}
		return head;
	}
}

void setJump(char tiles[2][MAXIMUM_MOVE_SIZE], char nextRow, char nextCol, char numberofJumps){

	//if(numberofJumps<5){
		tiles[0][numberofJumps]=nextRow;
		tiles[1][numberofJumps]=nextCol;
	//}
	return;
}

void setMove(char tiles[2][MAXIMUM_MOVE_SIZE], char curRow, char curCol, char nextRow, char nextCol){

	tiles[0][0]=curRow;
	tiles[1][0]=curCol;
	tiles[0][1]=nextRow;
	tiles[1][1]=nextCol;
	tiles[0][2]=-1;
	return;
}

int evaluteBoard(Position* position){
	int blackAnts=0;
	int whiteAnts=0;
	int inDanger=0;
	for(int i = 0; i < BOARD_ROWS; i++ ){
		for(int j =(i+1)%2 ; j < BOARD_COLUMNS; j+=2){
			if (position->board[i][j]==BLACK){
				blackAnts += 1;
			}else if(position->board[i][j]==WHITE){
				whiteAnts += 1;
			}
			if(AntInDanger(position, i, j)){
				inDanger = 1;
}
		}
	}

	if(blackAnts == whiteAnts){
		for(int i = 0; i < BOARD_ROWS; i++ ){
			for(int j =(i+1)%2 ; j < BOARD_COLUMNS; j+=2){
				if (position->board[i][j]==BLACK){
					blackAnts += tieBreakBoard[i][j];

				}else if(position->board[i][j]==WHITE){
					whiteAnts += tieBreakBoard[i][j];

				}
			}
		}
	}

	return  blackAnts - whiteAnts + inDanger;
}

int AntInDanger(Position* position,int i,int j){
	int dangerFlag=FALSE;

	if(position->turn==WHITE){

		if(position->board[i][j]==position->turn){

			if(canJump(i+1, j-1, !position->turn, position)!=0 || canJump(i+1, j+1, !position->turn, position)!=0 )
				dangerFlag = TRUE;
		}
	}
	if(position->turn==BLACK){

		if(position->board[i][j]==position->turn){

			if(canJump(i-1, j-1, !position->turn, position)!=0 || canJump(i-1, j+1, !position->turn, position)!=0 )
				dangerFlag = TRUE;
		}
	}
	return dangerFlag;
}

int checkForVictory(Position* pos){

	if( !canMove( pos, WHITE ) && !canMove( pos, BLACK ) ){
        return TRUE;
    }else{
        return FALSE;
    }
}

void printMove(Move* move, int score){
	for (int j = 0; j < MAXIMUM_MOVE_SIZE; j++){
		if(move->tile[0][j] == -1){
			printf("No more moves! -- ");
			break;
		}
		printf("(%d,%d) -> ", move->tile[0][j], move->tile[1][j]);
	}
	printf("score: %d\n", score);
	return;
}
