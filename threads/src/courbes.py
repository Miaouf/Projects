#/usr/bin/python

import numpy as np
import matplotlib.pyplot as plt

def computeCurve(th_results, pth_results, figure, x, y):
    file_th = open(th_results, "r")
    file_pth = open(pth_results, "r")
    lines_th = file_th.readlines()
    lines_pth = file_pth.readlines()
    nb_th = np.zeros(len(lines_th))
    time_th = np.zeros(len(lines_pth))
    time_pth = np.zeros(len(lines_pth))
    i = 0
    j = 0
    for line in lines_th :
        nb_th[i] = line.split(",")[0] #valeur premiere colonne
        time_th[i] = line.split(",")[1] #temps passé à créer nb_th[i] thread
        i = i+1
    for line_p in lines_pth :
        time_pth[j] = line_p.split(",")[1]
        j = j+1

    axies = plt.axes()
    plt.plot(nb_th, time_th, label = "Thread library")
    plt.plot(nb_th, time_pth, label = "Pthread library")
    plt.xlabel(x)
    plt.ylabel(y)
    plt.legend()
    plt.title(figure)
    plt.show()
    file_th.close()
    file_pth.close()



computeCurve("graph-data/21-create-many.csv","graph-data/21-create-many-pthread.csv","Create many thread", "Number of threads", "Time (us)")

computeCurve("graph-data/22-create-many-recursive.csv","graph-data/22-create-many-recursive-pthread.csv","Create many recursive", "Number of threads", "Time (us)")

computeCurve("graph-data/23-create-many-once.csv","graph-data/23-create-many-once-pthread.csv","Create many once", "Number of threads", "Time (us)")

computeCurve("graph-data/31-switch-many.csv","graph-data/31-switch-many-pthread.csv","Switch many", "Number of threads", "Time (us)")

computeCurve("graph-data/32-switch-many-join.csv","graph-data/32-switch-many-join-pthread.csv","Switch many-join", "Number of threads", "Time (us)")

computeCurve("graph-data/33-switch-many-cascade.csv","graph-data/33-switch-many-cascade-pthread.csv","Switch many cascade", "Number of threads", "Time (us)")

computeCurve("graph-data/51-fibonacci.csv","graph-data/51-fibonacci-pthread.csv","Fibonacci", "Fibo(N)", "Time (us)")
