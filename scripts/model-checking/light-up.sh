#!/bin/bash
########################################################################
# Voici notre script BASH réalisé par PEMEJA Tanguy et ROSAY Maxime (I1)
#
# Note : notre code autorise qu'un mur soit aussi une case illuminée
#        ie chaque case est soit un mur soit illuminée (ou les deux en l'absence de xor)
#########################################################################
## Le code suivant est à conserver: il lit la description de la grille au
## format ci-dessus et stocke les informations dans un tableau WALLS
#########################################################################

### Read the grid from standard input
OLDIFS="$IFS"
IFS="$IFS "

read N

declare -a WALLS

while read I J V || false; do
    WALLS[$((I*N+J))]=$V
done

IFS=$OLDIFS


### Variables propositionnelles
for I in `seq 0 $((N+1))`; do
    for J in `seq 0 $((N+1))`; do
        echo "(declare-const bulb_${I}_${J} Bool)"
        echo "(declare-const wall_${I}_${J} Bool)"
        echo "(declare-const islit_${I}_${J} Bool)"
    done
done

########################################################################
## Jeu sans murs ##
###################

#echo -n pour ne pas revenir à la ligne

#for I in `seq 1 $((N))`; do
#    for J in `seq 1 $((N))`; do
#       echo "(assert islit_${I}_${J})"  # toutes cases illuminées
#	echo -n "(assert (iff islit_${I}_${J} (or "  # condition pour être illuminée
#	for K in `seq 1 $((N))`; do
#	    echo -n "bulb_${K}_${J} "
#	    echo -n "bulb_${I}_${K} "
#	    done
#	echo " )))"
#    done
#done

#lampe ne peut être illuminée par une autre :

#for I in `seq 1 $((N))`; do
#   for J in `seq 1 $((N))`; do
#	echo -n "(assert (implies bulb_${I}_${J} (not (or "
#	for K in `seq 1 $((N))`; do
#	    if [ $K -ne $I ]
#	    then
#		echo -n "bulb_${K}_${J} "
#	    fi
#	    if [ $K -ne $J ]
#	    then
#		echo -n "bulb_${I}_${K} "
#	    fi
#	done
#	echo "))))"
#    done
#done


#echo "(check-sat)"
#echo "(get-model)"

########################################################################
## Jeu avec les murs sans cardinalité ##
########################################

for I in `seq 0 $((N+1))`; do
    echo "(assert (not bulb_0_${I}))"    #contours de la grille
    echo "(assert (not bulb_$(($N+1))_${I}))"
    echo "(assert (not bulb_${I}_0))"
    echo "(assert (not bulb_${I}_$(($N+1))))"
done

# Lecture de la grille

for I in `seq 1 $N`; do
    for J in `seq 1 $N`; do
        if [  "${WALLS[$((I*N+J))]}" = "" ]; then
	    echo "(assert (not wall_${I}_${J}))"
        else
	    #echo "Mur en ($I,$J)  avec valeur ${WALLS[$((I*N+J))]}"
	    echo "(assert wall_${I}_${J})"
        fi
    done
done

# Début des contraintes

for I in `seq 1 $N`; do
    for J in `seq 1 $N`; do
	echo "(assert (or islit_${I}_${J} wall_${I}_${J}))" #toutes cases illuminées
	echo "(assert (implies bulb_${I}_${J} (not wall_${I}_${J})))" #lampe ne peut pas être un mur aussi
    done
done

# mur bloque passage lumière
function murbloque1() #parcours nord
{
    for K in `seq 1 $((I-1))`; do
	echo -n "(or islit_${K}_${J} "
	for L in `seq $K $((I-1))`; do
	    echo -n "wall_${L}_${J} "
	done
	echo -n ")"
    done
}

function murbloque2() #sud
{
    for K in `seq $((I+1)) $N`; do
	echo -n "(or islit_${K}_${J} "
	for L in `seq $((I+1)) $K`; do
	    echo -n "wall_${L}_${J} "
	done
	echo -n ")"
    done
}


function murbloque3() #est
{
    for K in `seq 1 $((J-1))`; do
	echo -n "(or islit_${I}_${K} "
	for L in `seq $K $((J-1))`; do
	    echo -n "wall_${I}_${L} "
	done
	echo -n ")"
    done
}

function murbloque4() #ouest
{
    for K in `seq $((J+1)) $N`; do
	echo -n "(or islit_${I}_${K} "
	for L in `seq $((J+1)) $K`; do
	    echo -n "wall_${I}_${L} "
	done
	echo -n ")"
    done
}

for I in `seq 1 $((N))`; do
    for J in `seq 1 $((N))`; do
	echo -n "(assert (implies bulb_${I}_${J} (and islit_${I}_${J} "
	murbloque1
	murbloque2
	murbloque3
	murbloque4
	echo ")))"
    done
done

#Case illuminée par une lampe
function caseillu1() #parcours nord
{
    for K in `seq 1 $((I-1))`; do
	echo -n "(and bulb_${K}_${J} "
	for L in `seq $K $((I-1))`; do
	    echo -n "(not wall_${L}_${J}) "
	done
	echo -n ")"
    done
}

function caseillu2() #sud
{
    for K in `seq $((I+1)) $N`; do
	echo -n "(and bulb_${K}_${J} "
	for L in `seq $((I+1)) $K`; do
	    echo -n "(not wall_${L}_${J}) "
	done
	echo -n ")"
    done
}

function caseillu3() #est
{
    for K in `seq 1 $((J-1))`; do
	echo -n "(and bulb_${I}_${K} "
	for L in `seq $K $((J-1))`; do
	    echo -n "(not wall_${I}_${L}) "
	done
	echo -n ")"
    done
}

function caseillu4() #ouest
{
    for K in `seq $((J+1)) $N`; do
	echo -n "(and bulb_${I}_${K} "
	for L in `seq $((J+1)) $K`; do
	    echo -n "(not wall_${I}_${L}) "
	done
	echo -n ")"
    done
}


for I in `seq 1 $((N))`; do
    for J in `seq 1 $((N))`; do
	echo -n "(assert (implies islit_${I}_${J} (or bulb_${I}_${J} "
	caseillu1
	caseillu2
	caseillu3
	caseillu4
	echo ")))"
    done
done

#Lampe ne doit pas être éclairée par une autre lampe

function lampe1() #parcours nord
{
    for K in `seq 1 $((I-1))`; do
	echo -n "(or (not bulb_${K}_${J}) "
	for L in `seq $K $((I-1))`; do
	    echo -n "wall_${L}_${J} "
	done
	echo -n ")"
    done
}

function lampe2() #sud
{
    for K in `seq $((I+1)) $N`; do
	echo -n "(or (not bulb_${K}_${J}) "
	for L in `seq $((I+1)) $K`; do
	    echo -n "wall_${L}_${J} "
	done
	echo -n ")"
    done
}

function lampe3() #est
{
    for K in `seq 1 $((J-1))`; do
	echo -n "(or (not bulb_${I}_${K}) "
	for L in `seq $K $((J-1))`; do
	    echo -n "wall_${I}_${L} "
	done
	echo -n ")"
    done
}

function lampe4() #ouest
{
    for K in `seq $((J+1)) $N`; do
	echo -n "(or (not bulb_${I}_${K}) "
	for L in `seq $((J+1)) $K`; do
	    echo -n "wall_${I}_${L} "
	done
	echo -n ")"
    done
}




for I in `seq 1 $((N))`; do
    for J in `seq 1 $((N))`; do
	echo -n "(assert (implies bulb_${I}_${J} (and "
	lampe1
	lampe2
	lampe3
	lampe4
	echo ")))"
    done
done






########################################################################
# Jeu avec les murs et les cardinalités #
#########################################

# on reprend le code précédent en ajoutant des contraintes pendant la lecture de la grille

n=1
s=0
e=0
w=0
card=0


function cardnesw()
{
    if [ $n -eq 1 ]
    then
	echo -n "bulb_$((I-1))_${J} "
    else
	echo -n "(not bulb_$((I-1))_${J}) "
    fi
    if [ $s -eq 1 ]
    then
	echo -n "bulb_$((I+1))_${J} "
    else
	echo -n "(not bulb_$((I+1))_${J}) "
    fi
    if [ $e -eq 1 ]
    then
	echo -n "bulb_${I}_$((J+1)) "
    else
	echo -n "(not bulb_${I}_$((J+1))) "
    fi
    if [ $w -eq 1 ]
    then
	echo -n "bulb_${I}_$((J-1)) "
    else
	echo -n "(not bulb_${I}_$((J-1))) "
    fi
}

function card0()
{
    n=0; s=0; e=0; w=0
    cardnesw
}

function card1()
{
    echo -n "(or "
    echo -n "(and "
    n=1; s=0; e=0; w=0
    cardnesw
    echo -n ")"
    echo -n "(and "
    n=0; s=1; e=0; w=0
    cardnesw
    echo -n ")"
    echo -n "(and "
    n=0; s=0; e=1; w=0
    cardnesw
    echo -n ")"
    echo -n "(and "
    n=0; s=0; e=0; w=1
    cardnesw
    echo -n "))"
}

function card2()
{
    echo -n "(or "
    echo -n "(and "
    n=1; s=1; e=0; w=0
    cardnesw
    echo -n ")"
    echo -n "(and "
    n=1 s=0; e=1; w=0
    cardnesw
    echo -n ")"
    echo -n "(and "
    n=1; s=0; e=0; w=1
    cardnesw
    echo -n ")"
    echo -n "(and "
    n=0; s=1; e=1; w=0
    cardnesw
    echo -n ")"
    echo -n "(and "
    n=0; s=1; e=0; w=1
    cardnesw
    echo -n ")"
    echo -n "(and "
    n=0; s=0; e=1; w=1
    cardnesw
    echo -n "))"
}


function card3()
{
    echo -n "(or "
    echo -n "(and "
    n=1; s=1; e=1; w=0
    cardnesw
    echo -n ")"
    echo -n "(and "
    n=1 s=1; e=0; w=1
    cardnesw
    echo -n ")"
    echo -n "(and "
    n=1; s=0; e=1; w=1
    cardnesw
    echo -n ")"
    echo -n "(and "
    n=0; s=1; e=1; w=1
    cardnesw
    echo -n "))"
}


function card4()
{
    n=1; s=1; e=1; w=1
    cardnesw
}


for I in `seq 1 $N`; do
    for J in `seq 1 $N`; do
        if [ "${WALLS[$((I*N+J))]}" != "X" -a "${WALLS[$((I*N+J))]}" != "" ]
	then
	    #echo "Mur en ($I,$J)  avec valeur ${WALLS[$((I*N+J))]}"
	    card=${WALLS[$((I*N+J))]}
	    echo -n "(assert (and wall_${I}_${J} "
	    card$card
	    echo "))"
	fi
    done
done


echo "(check-sat)"
echo "(get-model)"


