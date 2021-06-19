
#ifndef LINKEDLIST_H
#define LINKEDLIST_H

#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include <errno.h>
#include <setjmp.h>
#include <assert.h>
#include "board.h"

typedef struct node {
    Move move;
    struct node * next;
} node_t;

void push_front(node_t **head, char tile[2][MAXIMUM_MOVE_SIZE], char color){
    node_t* newNode = (node_t*)malloc(sizeof(node_t));

    Move temp;
    for(int i=0;i<2;i++){
      for(int j=0;j<MAXIMUM_MOVE_SIZE;j++){
        temp.tile[i][j] = tile[i][j];
      }
    }
    temp.color = color;
    newNode->move=temp;

    newNode->next = *head;
    *head = newNode;
    return;
}

Move pop_front(node_t** head) {

    Move retMove;
    node_t* next_node = NULL;

    if (*head == NULL) {
        return retMove;
    }
    next_node = (*head)->next;
    retMove = (*head)->move;
    free(*head);
    *head = next_node;
    return retMove;
}

int isEmpty(node_t *head){
    return (head==NULL) ? TRUE : FALSE;
}

void delete_list(node_t* head) {
    node_t  *current = head,
            *next = head;
    while (current) {
        next = current->next;
        free(current);
        current = next;
    }
}

void reverselist(node_t** head){

    node_t *prev = NULL, *curr = *head, *next;

    while (curr) {
        next = curr->next;
        curr->next = prev;
        prev = curr;
        curr = next;
    }

    *head = prev;
}

void rearrange(node_t** head){

    node_t *slow = *head, *fast = slow->next;
    while (fast && fast->next) {
        slow = slow->next;
        fast = fast->next->next;
    }

    node_t* head1 = *head;
    node_t* head2 = slow->next;
    slow->next = NULL;

    reverselist(&head2);

    char temp[2][MAXIMUM_MOVE_SIZE] = {0,0,0,0,0,0,0,0,0,0,0,0};

    push_front(head,temp, 0);

    node_t* curr = *head;
    while (head1 || head2) {

        if (head1) {
            curr->next = head1;
            curr = curr->next;
            head1 = head1->next;
        }

        if (head2) {
            curr->next = head2;
            curr = curr->next;
            head2 = head2->next;
        }
    }
    node_t* next = (*head)->next;
    free(*head);
    *head = next;
}

int max(int num1, int num2)
{
    return (num1 > num2 ) ? num1 : num2;
}

int min(int num1, int num2)
{
    return (num1 > num2 ) ? num2 : num1;
}

#endif
