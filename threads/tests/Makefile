all: build

BUILD_DIR=build
BIN_DIR=install/bin
LIB_DIR=install/lib
CFLAGS=-g -O0 -Wall

build: ../$(BIN_DIR)/01-main \
	../$(BIN_DIR)/02-switch \
	../$(BIN_DIR)/11-join \
	../$(BIN_DIR)/12-join-main \
	../$(BIN_DIR)/21-create-many \
	../$(BIN_DIR)/22-create-many-recursive \
	../$(BIN_DIR)/23-create-many-once \
	../$(BIN_DIR)/31-switch-many \
	../$(BIN_DIR)/32-switch-many-join \
	../$(BIN_DIR)/33-switch-many-cascade \
	../$(BIN_DIR)/51-fibonacci \
	../$(BIN_DIR)/61-mutex \
	../$(BIN_DIR)/62-mutex \
	../$(BIN_DIR)/71-preemption


../$(BIN_DIR)/01-main: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 01-main.c -lthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/02-switch: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 02-switch.c -lthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/11-join: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 11-join.c -lthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/12-join-main: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 12-join-main.c -lthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/21-create-many: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 21-create-many.c -lthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/22-create-many-recursive: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 22-create-many-recursive.c -lthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/23-create-many-once: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 23-create-many-once.c -lthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/31-switch-many: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 31-switch-many.c -lthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/32-switch-many-join: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 32-switch-many-join.c -lthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/33-switch-many-cascade: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 33-switch-many-cascade.c -lthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/51-fibonacci: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 51-fibonacci.c -lthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/61-mutex: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 61-mutex.c -lthread -lrt -L../$(LIB_DIR)

../$(BIN_DIR)/62-mutex: ../install/lib/libthread.so
	gcc $(CFLAGS) -I../src -o $@ 62-mutex.c -lthread -lrt -L../$(LIB_DIR)

preemption: ../$(BIN_DIR)/71-preemption

../$(BIN_DIR)/71-preemption: ../install/lib/libthread-ps.so
	gcc $(CFLAGS) -I../src -o $@  71-preemption.c -lthread-ps -lrt -L../$(LIB_DIR)

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
	51-fibonacci-pthread \
	61-mutex-pthread \
	62-mutex-pthread

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

61-mutex-pthread:
	gcc -I../src -o ../$(BIN_DIR)/$@ 61-mutex.c -DUSE_PTHREAD -pthread

62-mutex-pthread:
	gcc -I../src -o ../$(BIN_DIR)/$@ 62-mutex.c -DUSE_PTHREAD -pthread
