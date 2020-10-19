.PHONY: install check
SRC_DIR=src
BIN_DIR=install/bin
LIB_DIR=install/lib
DATA_DIR=graph-data

all: install

install: src/
	@if [ ! -d install ]; then mkdir install; fi
	@if [ ! -d $(BIN_DIR) ]; then mkdir $(BIN_DIR); fi
	@if [ ! -d $(LIB_DIR) ]; then mkdir $(LIB_DIR); fi
	@if [ ! -d $(DATA_DIR) ]; then mkdir $(DATA_DIR); fi

	cd src && make
	cd tests && make

N = 8 #intervalle de thread pour fibo
N_MAX = 30 #intervalle de thread pour fibo
NBR_YD = 10 #nombre de yield
NBR_TH = 20 #nombre de thread
NBR_TH_MAX = 200

check: install
	#LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib ./$(BIN_DIR)/00-multicore
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib ./$(BIN_DIR)/01-main-multi
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib ./$(BIN_DIR)/02-switch-multi
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib ./$(BIN_DIR)/11-join-multi
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib ./$(BIN_DIR)/12-join-main-multi
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib ./$(BIN_DIR)/21-create-many-multi ${NBR_YD}
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib ./$(BIN_DIR)/22-create-many-recursive-multi ${NBR_YD}
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib ./$(BIN_DIR)/23-create-many-once-multi ${NBR_YD}
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib ./$(BIN_DIR)/31-switch-many-multi ${NBR_TH} ${NBR_YD}
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib ./$(BIN_DIR)/32-switch-many-join-multi ${NBR_TH} ${NBR_YD}
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib ./$(BIN_DIR)/33-switch-many-cascade-multi ${NBR_TH} ${NBR_YD}
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib ./$(BIN_DIR)/51-fibonacci-multi ${N}

valgrind: install
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib valgrind ./$(BIN_DIR)/01-main-multi
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib valgrind ./$(BIN_DIR)/02-switch-multi
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib valgrind ./$(BIN_DIR)/11-join-multi
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib valgrind ./$(BIN_DIR)/12-join-main-multi
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib valgrind ./$(BIN_DIR)/22-create-many-recursive -multi${NBR_YD}
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib valgrind ./$(BIN_DIR)/23-create-many-once-multi ${NBR_YD}
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib valgrind ./$(BIN_DIR)/31-switch-many-multi ${NBR_TH} ${NBR_YD}
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib valgrind ./$(BIN_DIR)/32-switch-many-join-multi ${NBR_TH} ${NBR_YD}
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib ./$(BIN_DIR)/33-switch-many-cascade-multi ${NBR_TH} ${NBR_YD}
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib valgrind ./$(BIN_DIR)/51-fibonacci-multi ${N}

pthreads:
	cd tests && make pthreads

check-pthreads: pthreads
	./$(BIN_DIR)/01-main-pthread
	./$(BIN_DIR)/02-switch-pthread
	./$(BIN_DIR)/11-join-pthread
	./$(BIN_DIR)/12-join-main-pthread
	./$(BIN_DIR)/21-create-many-pthread ${NBR_TH}
	./$(BIN_DIR)/22-create-many-recursive-pthread ${NBR_TH}
	./$(BIN_DIR)/23-create-many-once-pthread ${NBR_TH}
	./$(BIN_DIR)/31-switch-many-pthread ${NBR_TH} ${NBR_YD}
	./$(BIN_DIR)/32-switch-many-join-pthread ${NBR_TH} ${NBR_YD}
	./$(BIN_DIR)/33-switch-many-cascade-pthread ${NBR_TH} ${NBR_YD}
	./$(BIN_DIR)/51-fibonacci-pthread ${N}

graphs: pthreads install
	./${SRC_DIR}/getData.sh 21-create-many-pthread ${NBR_TH} ${NBR_TH_MAX}
	./${SRC_DIR}/getData.sh 22-create-many-recursive-pthread ${NBR_TH} ${NBR_TH_MAX}
	./${SRC_DIR}/getData.sh 23-create-many-once-pthread ${NBR_TH} ${NBR_TH_MAX}
	./${SRC_DIR}/getData.sh 31-switch-many-pthread ${NBR_TH} ${NBR_YD} ${NBR_TH_MAX}
	./${SRC_DIR}/getData.sh 32-switch-many-join-pthread ${NBR_TH} ${NBR_YD} ${NBR_TH_MAX}
	./${SRC_DIR}/getData.sh 33-switch-many-cascade-pthread ${NBR_TH} ${NBR_YD} ${NBR_TH_MAX}
	#./${SRC_DIR}/getData.sh 51-fibonacci-pthread ${N} ${N_MAX}
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib ./${SRC_DIR}/getData.sh 21-create-many-multi ${NBR_TH} ${NBR_TH_MAX}
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib ./${SRC_DIR}/getData.sh 22-create-many-recursive-multi ${NBR_TH} ${NBR_TH_MAX}
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib ./${SRC_DIR}/getData.sh 23-create-many-once-multi ${NBR_TH} ${NBR_TH_MAX}
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib ./${SRC_DIR}/getData.sh 31-switch-many-multi  ${NBR_TH} ${NBR_YD} ${NBR_TH_MAX}
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib ./${SRC_DIR}/getData.sh 32-switch-many-join-multi  ${NBR_TH} ${NBR_YD} ${NBR_TH_MAX}
	LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib ./${SRC_DIR}/getData.sh 33-switch-many-cascade-multi  ${NBR_TH} ${NBR_YD} ${NBR_TH_MAX}
	#LD_LIBRARY_PATH=$$LD_LIBRARY_PATH:$$PWD/install/lib ./${SRC_DIR}/getData.sh 51-fibonacci ${N} ${N_MAX}
	python3 ${SRC_DIR}/courbes.py

clean:
	@if [ -d $(BIN_DIR) ]; then rm -r $(BIN_DIR)/*; fi
	@if [ -d $(LIB_DIR) ]; then rm -r $(LIB_DIR)/*; fi
	@if [ -d $(DATA_DIR) ]; then rm -r $(DATA_DIR); fi

proper:
	rm -r $(BIN_DIR)
