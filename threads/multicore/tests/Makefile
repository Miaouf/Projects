all: build

BUILD_DIR=build
BIN_DIR=install/bin
LIB_DIR=install/lib
CFLAGS=-g -O0 -Wall

build: ../$(BIN_DIR)/00-multicore-multi \
	../$(BIN_DIR)/01-main-multi \
	../$(BIN_DIR)/02-switch-multi \
	../$(BIN_DIR)/11-join-multi \
	../$(BIN_DIR)/12-join-main-multi \
	../$(BIN_DIR)/21-create-many-multi \
	../$(BIN_DIR)/22-create-many-recursive-multi \
	../$(BIN_DIR)/23-create-many-once-multi \
	../$(BIN_DIR)/31-switch-many-multi \
	../$(BIN_DIR)/32-switch-many-join-multi \
	../$(BIN_DIR)/33-switch-many-cascade-multi \
	../$(BIN_DIR)/51-fibonacci-multi

../$(BIN_DIR)/00-multicore-multi: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 00-mutlicore.c -lthread -pthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/01-main-multi: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 01-main.c -lthread -pthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/02-switch-multi: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 02-switch.c -lthread -pthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/11-join-multi: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 11-join.c -lthread -pthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/12-join-main-multi: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 12-join-main.c -lthread -pthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/21-create-many-multi: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 21-create-many.c -lthread -pthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/22-create-many-recursive-multi: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 22-create-many-recursive.c -lthread -pthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/23-create-many-once-multi: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 23-create-many-once.c -lthread -pthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/31-switch-many-multi: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 31-switch-many.c -lthread -pthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/32-switch-many-join-multi: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 32-switch-many-join.c -lthread -pthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/33-switch-many-cascade-multi: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 33-switch-many-cascade.c -lthread -pthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/51-fibonacci-multi: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 51-fibonacci.c -lthread -pthread -lrt -L../$(LIB_DIR)

pthreads: 01-main-pthread \
	02-switch-pthread \
	11-join-pthread \
	12-join-main-pthread \
	21-create-many-pthread \
	22-create-many-recursive-pthread \
	23-create-many-once-pthread \
	31-switch-many-pthread \
	32-switch-many-join-pthread \
	33-switch-many-cascade-pthread \
	51-fibonacci-pthread

01-main-pthread:
	gcc -I../src -o ../$(BIN_DIR)/$@ 01-main.c -DUSE_PTHREAD -pthread

02-switch-pthread:
	gcc -I../src -o ../$(BIN_DIR)/$@ 02-switch.c -DUSE_PTHREAD -pthread

11-join-pthread:
	gcc -I../src -o ../$(BIN_DIR)/$@ 11-join.c -DUSE_PTHREAD -pthread

12-join-main-pthread:
	gcc -I../src -o ../$(BIN_DIR)/$@ 12-join-main.c -DUSE_PTHREAD -pthread

21-create-many-pthread:
	gcc -I../src -o ../$(BIN_DIR)/$@ 21-create-many.c -DUSE_PTHREAD -pthread

22-create-many-recursive-pthread:
	gcc -I../src -o ../$(BIN_DIR)/$@ 22-create-many-recursive.c -DUSE_PTHREAD -pthread

23-create-many-once-pthread:
	gcc -I../src -o ../$(BIN_DIR)/$@ 23-create-many-once.c -DUSE_PTHREAD -pthread

31-switch-many-pthread:
	gcc -I../src -o ../$(BIN_DIR)/$@ 31-switch-many.c -DUSE_PTHREAD -pthread

32-switch-many-join-pthread:
	gcc -I../src -o ../$(BIN_DIR)/$@ 32-switch-many-join.c -DUSE_PTHREAD -pthread

33-switch-many-cascade-pthread:
	gcc -I../src -o ../$(BIN_DIR)/$@ 33-switch-many-cascade.c -DUSE_PTHREAD -pthread

51-fibonacci-pthread:
	gcc -I../src -o ../$(BIN_DIR)/$@ 51-fibonacci.c -DUSE_PTHREAD -pthread
