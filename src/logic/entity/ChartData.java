package logic.entity;

public class ChartData {
	private int y;
	private int year;
	private int month;
	

	public ChartData(int month, int year, int y) {
		this.month = month;
		this.year = year;
		this.y = y;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}


}
