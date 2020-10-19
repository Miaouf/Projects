BIN_DIR=install/lib
CFLAGS=-g -O0 -Wall

all: ../$(BIN_DIR)/libthread.so

../$(BIN_DIR)/libthread.so: thread_clone.c thread.h
	gcc $(CFLAGS) -pthread -lrt -fPIC -shared  -o $@ $<
