#ifndef HASHMAP_H
#define HASHMAP_H

#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include <errno.h>
#include <setjmp.h>
#include <assert.h>
#include "board.h"
#include "client.h"


typedef struct data_t{
    int bestValue; 
    int lowerBound;
    int upperBound;
    int depth;
}data_t;

typedef struct hash_elem_t{
    unsigned long long int key;
    data_t* data;
    struct hash_elem_t *next;
}hash_elem_t;

//Hashtabe structure
typedef struct {
	unsigned int capacity;	// Hashtable capacity (in terms of hashed keys)
	hash_elem_t** table;	// The table containaing elements
} hashtable_t;

//Structure used for iterations
typedef struct {
	hashtable_t* ht; 	// The hashtable on which we iterate
	unsigned int index;	// Current index in the table
	hash_elem_t* elem; 	// Curent element in the list
} hash_elem_it;

// Inititalize hashtable iterator on hashtable 'ht'
#define HT_ITERATOR(ht) {ht, 0, ht->table[0]}

char err_ptr;
void* HT_ERROR = &err_ptr; // Data pointing to HT_ERROR are returned in case of error


int ht_hash_function(hashtable_t *t, unsigned long long int key){
    if(key<0)
        return -(key%(t->capacity));
    return (key%(t->capacity));
}

hashtable_t* ht_create(unsigned int capacity){

	hashtable_t* hasht = (hashtable_t*)malloc(sizeof(hashtable_t));
	if(!hasht)
		return NULL;

	if((hasht->table = (hash_elem_t**)malloc(capacity*sizeof(hash_elem_t*))) == NULL){
		free(hasht->table);
		return NULL;
	}

	hasht->capacity = capacity;
	unsigned int i;
	for(i = 0; i < capacity; i++)
		hasht->table[i] = NULL;
	return hasht;
}

void* ht_put(hashtable_t* hasht, unsigned long long int key, data_t* data){

	if(data == NULL)
		return NULL;
	unsigned int h = ht_hash_function(hasht, key);
	hash_elem_t* e = hasht->table[h];

	while(e != NULL){
		if(e->key == key){
      data_t* ret = e->data;
			e->data = data;
			return ret;
		}
		e = e->next;
	}

	// Getting here means the key doesn't already exist
	if((e = (hash_elem_t*)malloc(sizeof(hash_elem_t)+sizeof(key)+1)) == NULL)
		return HT_ERROR;

  	e->data = data;
	e->key = key;

	// Add the element at the beginning of the linked list
	e->next = hasht->table[h];
	hasht->table[h] = e;

	return NULL;
}

/* Retrieve data from the hashtable */
void* ht_get(hashtable_t* hasht, unsigned long long int key)
{
	unsigned int h = ht_hash_function(hasht, key);
	hash_elem_t* e = hasht->table[h];
	while(e != NULL)
	{
		if(e->key == key)
			return e->data;
		e = e->next;
	}
	return NULL;
}

/* 	Remove data from the hashtable. Return the data removed from the table
	so that we can free memory if needed */
void* ht_remove(hashtable_t* hasht, unsigned long long int key)
{
	unsigned int h = ht_hash_function(hasht, key);
	hash_elem_t* e = hasht->table[h];
	hash_elem_t* prev = NULL;
	while(e != NULL)
	{
		if(e->key == key){

			data_t* ret = e->data;
			if(prev != NULL)
				prev->next = e->next;
			else
				hasht->table[h] = e->next;
			free(e);
			e = NULL;
			return ret;
		}
		prev = e;
		e = e->next;
	}
	return NULL;
}

/* Iterate through table's elements. */
hash_elem_t* ht_iterate(hash_elem_it* iterator)
{
	while(iterator->elem == NULL)
	{
		if(iterator->index < iterator->ht->capacity - 1)
		{
			iterator->index++;
			iterator->elem = iterator->ht->table[iterator->index];
		}
		else
			return NULL;
	}
	hash_elem_t* e = iterator->elem;
	if(e)
		iterator->elem = e->next;
	return e;
}

/* Iterate through keys. */
unsigned long long int ht_iterate_keys(hash_elem_it* iterator)
{
	hash_elem_t* e = ht_iterate(iterator);
	return (e == NULL) ? 0 : e->key;
}

/* Iterate through values. */
void* ht_iterate_values(hash_elem_it* iterator)
{
	hash_elem_t* e = ht_iterate(iterator);
	return (e == NULL ? NULL : e->data);
}

/* 	Removes all elements stored in the hashtable.
	if free_data, all stored datas are also freed.*/
void ht_clear(hashtable_t* hasht, int free_data)
{
	hash_elem_it it = HT_ITERATOR(hasht);
	unsigned long long int k = ht_iterate_keys(&it);
	while(k != 0)
	{
		free_data ? free(ht_remove(hasht, k)) : (void)ht_remove(hasht, k);
		k = ht_iterate_keys(&it);
	}
}

/* 	Destroy the hash table, and free memory.
	Data still stored are freed*/
void ht_destroy(hashtable_t* hasht)
{
	ht_clear(hasht, 1); // Delete and free all.
	free(hasht->table);
	free(hasht);
}

#endif
