package timwforce.stufftoremindmeof;

public class Reminder {
	private String message;
	private int    minutes;
	
	public Reminder(String message, int minutes) {
		this.message = message;
		this.minutes = minutes;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public int getMinutes() {
		return minutes;
	}
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

}
