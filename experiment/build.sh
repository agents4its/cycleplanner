#!/bin/bash

# By default, class inside the java file is MLC.class, so do not change first object inside the array
# Do not forget import all specified classes in Run.java!!!
classes=(MLC MLC MLCJaccardPruning MLCRatioPruning MLCBucketsThreeCriteria MLCEpsilon MLCCostPruning)

length=${#classes[@]}
let "length -= 1"

rm jarsB/*.jar

# First rebuild rest of the cycleplanner
cd ../data/cycleway-graphs
mvn clean install -Dmaven.test.skip=true
cd ../../cycle-planner
mvn clean install -Dmaven.test.skip=true
cd ../experiment

for i in `seq 1 $length`
do
	old="Class<?> clazz = "${classes[$i-1]}".class;"
	new="Class<?> clazz = "${classes[$i]}".class;"
	sed -i "s/$old/$new/" src/main/java/cz/agents/cycleplanner/experiment/Run.java
	mvn clean install -Dmaven.test.skip=true
	mv target/mlc-experiment-jar-with-dependencies.jar jarsB/${classes[i]}.jar 
done

# Rewrite the class back to the MLC.class
sed -i "s/Class<?> clazz =.\+class;/Class<?> clazz = MLC.class;/" src/main/java/cz/agents/cycleplanner/experiment/Run.java
