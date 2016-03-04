#!/bin/bash
find . -type f -name '*.class' -delete
javac -cp ../lib/snakeyaml-1.9.jar:. ./org/cmu/edu/driver/Logger.java 
javac -cp ../lib/snakeyaml-1.9.jar:. ./org/cmu/edu/driver/NodeDriver.java
javac -cp ../lib/snakeyaml-1.9.jar:. ./org/cmu/edu/driver/MulticastDriver.java
javac -cp ../lib/snakeyaml-1.9.jar:. ./org/cmu/edu/driver/MutualExclusionDriver.java
