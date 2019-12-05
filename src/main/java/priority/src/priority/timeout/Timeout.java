package priority.src.priority.timeout;

public class Timeout implements Runnable{
	public boolean taskComplete = false;
	public boolean timeout = false;
    public void run() {
        try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if(taskComplete ==false) {
        	timeout = true; 
        }
        else {
            return;
        }
    }
}