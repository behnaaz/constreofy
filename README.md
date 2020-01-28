# Constreofy tool:

Constreofy applies constraint satisfaction techniques to compute the formal semantics of a Reo network.

```java
public ConstraintConnector example() {
	final ConnectorFactory factory = new ConnectorFactory();
	connector = factory.prioritySync("a", "b");
	connector.add(factory.merger("c", "d", "e"), "c", "b");
	connector.add(factory.sync("f", "g"), "f", "a");
	connector.add(factory.merger("h", "i", "j"), "h", "g");
	return connector;
}
```

It uses REDUCE Algebra system (https://reduce-algebra.sourceforge.io/) for solving the constraints and GraphViz (https://www.graphviz.org/) to generate the result automata. The image below is an example of its output.

To make it easier to use, the tool will be dockerized in near future.


Example:
A complex network ![Image description](src/main/resources/graph0.png)


More examples are being added to ![](![FIFO1](src/test/resources/output) such as a FIFO channel (Unmerged results)
Graphical output
![](src/test/resources/output/FIFO/outthesis106.svg.png =100x20)
![](src/test/resources/output/FIFO/outthesis106info.txt)
Label Information
![](src/test/resources/output/FIFO/outthesislabels.txt)
