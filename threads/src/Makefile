BIN_DIR=install/lib
CFLAGS=-g -O0 -Wall
CFLAGS_PS=$(CFLAGS) -DPREEMPTION

all: ../$(BIN_DIR)/libthread.so \
	../$(BIN_DIR)/libthread-ps.so


../$(BIN_DIR)/libthread.so: thread.c thread.h
	gcc $(CFLAGS) -lrt -fPIC -shared -o $@ $<


../$(BIN_DIR)/libthread-ps.so: thread.c thread.h
	gcc $(CFLAGS_PS) -lrt -fPIC -shared  -o $@ $<
