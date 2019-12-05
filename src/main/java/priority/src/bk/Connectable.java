package priority.src.bk;

public interface Connectable {
	void output(Connector connector);
	
	Connector fullFifo(String src, String snk);
	Connector merger(String p1, String p2, String p3);
	Connector router(String p1, String p2, String p3);
	Connector syncDrain(String c1, String c2);
	Connector sync(String src, String snk);
	Connector prioritySync(String src, String snk);
	Connector lossySync(String src, String snk);
}
