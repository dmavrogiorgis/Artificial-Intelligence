#ifndef ZOBRISTTABLE_H
#define ZOBRISTTABLE_H

#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include <errno.h>
#include <setjmp.h>
#include <assert.h>
#include "board.h"


unsigned long long int ZobristTable[BOARD_ROWS][BOARD_COLUMNS][2];


unsigned rand256(){
    static unsigned const limit = RAND_MAX - RAND_MAX % 256;
    unsigned result = rand();
    while ( result >= limit ) {
        result = rand();
    }
    return result % 256;
}

unsigned long long rand64bits(){
    unsigned long long results = 0ULL;
    for ( int count = 8; count > 0; -- count ) {
        results = 256U * results + rand256();
    }
    return results;
}

int indexOf(char piece)
{
    if (piece==WHITE)
        return WHITE;
    if (piece==BLACK)
        return BLACK;
    else
        return -1;
}

// Initializes the table
void initTable()
{
    for (int i = 0; i<BOARD_ROWS; i++)
      for (int j = 0; j<BOARD_COLUMNS; j++)
        for (int k = 0; k<2; k++)
          ZobristTable[i][j][k] = rand64bits();
}

// Computes the hash value of a given board
unsigned long long int computeHash(char board[BOARD_ROWS][BOARD_COLUMNS])
{
    unsigned long long int h = 0;
    for (int i = 0; i<BOARD_ROWS; i++){
        for (int j = 0; j<BOARD_COLUMNS; j++){
            if (board[i][j]!=EMPTY || board[i][j]!=ILLEGAL || board[i][j]!=RTILE)
            {
                int piece = indexOf(board[i][j]);
                h ^= ZobristTable[i][j][piece];
            }
        }
    }
    return h;
}

#endif
