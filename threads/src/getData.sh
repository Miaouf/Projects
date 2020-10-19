#!/bin/bash

max=$3
data_file="graph-data/$1.csv"
if [ $# -eq $((4)) ]
then
  max=$4
fi

for ((i=10 ; $max - $i ; i++)) #nombre de thread allant de 10 à nombre jusqu'au nombre max de thread)
do
  iter=0
  time=0
  for ((j=0 ; $((10)) - $j ; j++))
  do
    num=$(echo $(./install/bin/$1 $i $3 | grep ":" | cut -d : -f 2 | cut -d ' ' -f 2 ) | awk '{ print sprintf("%.7f", $num); }' ) #on récupère le temps en us dans le printf :)
    one=1.0
    if (( $(echo "$num < $one" | bc -l) ))  ; then #si on est dans fibo, le resultat est en seconde et donc inferieur à 1, on fait la conversio necessaire en microseconde...
      conv=1000000
      num=$( bc -l <<<"$num * $conv" )
    fi
    num=${num/.*}
    time=$(($time + $num))
    iter=$(($iter + 1))
  done
  average_time=$(($time/$iter)) #moyenne du temps d'execution sur 10 fois (pour une courbe moins pointue...)
  echo "$i,$average_time" >> $data_file
done
