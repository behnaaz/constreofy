package priority.src.priority.semantics;

public class VaiableNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public VaiableNotFoundException(String name) {
		super(name+" not found");
	}
}
