package Helper;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class Companies {
	@SerializedName("data")
	private ArrayList<Stock> data = null;
	
	public Companies(ArrayList<Stock> temp_stocks) {
		this.data = temp_stocks;
	}
	
	public ArrayList<Stock> getData() {
		return data;
	}
	
	public void setData(ArrayList<Stock> data) {
		this.data = data;
	}
}
