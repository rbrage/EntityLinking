package jung;

public class Link {
	double capasity;
	double weigth;
	String id;
	
	public Link(double weigth, double capasity){
		this.weigth = weigth;
		this.capasity = capasity;
		
	}
	
	public String toString(){
		return ""+this.weigth;
	}
}
