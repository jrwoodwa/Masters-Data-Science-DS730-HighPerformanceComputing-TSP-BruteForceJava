Problem 2, Part A
Multithreading Traveling Salesperson Problem (TSP)

Main Java code:
FinalRunner.java
PermutationGenerator.java

There are folders containing the same named input2.txt file but with differing square matrices according to the problem size for 4, 7, 10, 12, and 13. To test out different runs, I would copy the input2.txt file from the folder and rerun it with the substituted input2.txt file.

Note:
I performed quality assurance on the Java code by compiling and executing it on the Hortonworks/AWS E2 cluster. 
Running it on the 13-building TSP took roughly 23 minutes on two threads- while simultaneously running Spark in Zeppelin to solve Problem 1f; in contrast, on eight threads on my computer, it ran in under 4 minutes.