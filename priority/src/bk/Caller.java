package bk;

//import orbital.logic.imp.Formula;
//import orbital.logic.sign.Expression;
//import orbital.moon.logic.ClassicalLogic;
//import orbital.moon.logic.ClassicalLogic.Utilities;

public class Caller {
/*
 * public String convertToCNF(String formula){//??TODO
		String res = null;
		ClassicalLogic log = new ClassicalLogic();
		String renamed = formula;// convertWord2Symbol(formula);
		try {
				Expression ex = log.createExpression(renamed);
				String m = ex.toString();
				@SuppressWarnings("deprecation")
				Formula f = log.createFormula(m);
				f = Utilities.conjunctiveForm(f, true);
				res = f.toString();
				System.out.print(res);
		} catch (orbital.logic.sign.ParseException | IllegalArgumentException e) {
				e.printStackTrace();
		}
		return res;
		
	}
 * 
	private void exampleTwo() {
		PriorityColoring coloring = new PriorityColoring();
		Connector connector = coloring.router("a1", "a2", "a3");
		coloring.output(connector);
		coloring.add(connector, coloring.fullFifo("b1", "b2"), "b1", "a2");
		connector.output(connector);
		connector.add(coloring.router("c1", "c2", "c3"), "c1", "b2");
		connector.output(connector);
		connector.add(coloring.prioritySync("d1", "d2"), "d1", "c3");
		connector.output(connector);
		connector.add(coloring.syncDrain("e1", "e2"), "e1", "d2");
		connector.output(connector);
		connector.add(coloring.router("f2", "f1", "f3"), "f1", "e2");
		connector.output(connector);
		connector.add(coloring.lossySync("g2", "g1"), "g1","f2");
		connector.output(connector);
		connector.add(coloring.syncDrain("h1", "h2"), "h1", "f3");
		connector.output(connector);
		connector.add(coloring.prioritySync("i2", "i1"), "i1", "h2");
		connector.output(connector);
		connector = connector.connect("i2","a3");
		connector.output(connector);
		connector = connector.ground(new String[]{"a1", "c2", "g2"});
		connector.output(connector);
	}

	void exampleOne() {
		PriorityColoring coloring = new PriorityColoring();
		Connector connector = coloring.prioritySync("a", "b");
		connector.output(connector);
		connector.add(coloring.merger("c", "d", "e"), "c", "b");
		connector.output(connector);
		connector.add(coloring.sync("f", "g"), "f", "a");
		connector.output(connector);
		connector.add(coloring.merger("h", "i", "j"), "h", "g");
		connector.output(connector);
		connector = connector.ground(new String[]{"d", "e", "i", "j"});
		connector.output(connector);
	}*/
}
