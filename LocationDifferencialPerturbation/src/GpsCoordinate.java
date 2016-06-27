
public class GpsCoordinate {
	private double latitude;//weidu
	private double longtitude;
	public void setLat(double X){
		this.latitude=X;
	}
	public void setLong(double Y){
		this.longtitude=Y;
	}
	public double getLat(){
		return latitude;
	}
	public double getLong(){
		return longtitude;
	}
public String toString(){
	return "Î³¶È"+latitude+"\t"+"¾­¶È"+longtitude+"\t";
	
}
}
