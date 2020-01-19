This tool applies constraint satisfaction techniques to compute the formal semantics of a Reo network.

It uses REDUCE Algebra system (https://reduce-algebra.sourceforge.io/) for solving the constraints and GraphViz (https://www.graphviz.org/) to generate the result automata. The image below is an example of its output.

To make it easier to use, the tool will be dockerized in near future.


Examples:

1) A FIFO channel (Unmerged results)
Graphical output
![FIFO1](src/test/resources/output/FIFO/outthesis106.svg.png)
![FIFO1](src/test/resources/output/FIFO/outthesis106info.txt)
Label Information
![FIFO1](src/test/resources/output/FIFO/outthesislabels.txt)



2) A more complex example ![Image description](src/main/resources/graph0.png)

