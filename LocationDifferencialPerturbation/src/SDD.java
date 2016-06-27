import java.util.ArrayList;
import java.util.List;

public class SDD {
	double[] vector;
	double[] theta;
	double PI = 3.141592653589794;
	double epsilon=0.01;

	public double getDistance(double x, double y) {
		
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}

	public double getAngle(double x, double y) {
		if(x==0&&y==0)
			return 0;
		else
		return Math.asin(y / Math.sqrt(x*x+y*y));
	}

	public double differiencialNoise(double B, double epsilon, double a) {
		double noise, uniformDistributionVar;
		double b = 8 * B / epsilon;
		double temp = 2 - Math.exp(-a / b) - Math.exp(-(B - a) / b);
		double division = (1 - Math.exp(-a / b)) / temp;
		uniformDistributionVar = Math.random();
		if (uniformDistributionVar == 0) {
			noise = Double.NEGATIVE_INFINITY;
		} else if (0 < uniformDistributionVar
				&& uniformDistributionVar < division)
			noise = a
					+ b
					* Math.log(temp * uniformDistributionVar + Math.exp(-a / b));
		else
			noise = a
					- b
					* Math.log(temp * (1 - uniformDistributionVar)
							+ Math.exp(-(B - a) / b));

		return noise;

	}

	public List<PlaneCoordinate> SSD(List<PlaneCoordinate> xy) {
		double rho;
		double alpha;
		double MaxDistance=0;
		int length = xy.size();
		System.out.println(length);
		List<PlaneCoordinate> xy_new = new ArrayList<PlaneCoordinate>();
		xy_new.add(xy.get(0));
		vector = new double[length ];
		theta = new double[length ];
		for (int i = 1; i < length; i++) {
			double x = xy.get(i).getX() - xy.get(i - 1).getX();
			double y = xy.get(i).getY() - xy.get(i - 1).getY();
			vector[i] = getDistance(x, y);
		//	System.out.println("第"+i+"个距离"+vector[i] );
			if(MaxDistance<vector[i])
				MaxDistance=vector[i];
			theta[i] = getAngle(x, y);
			//System.out.println("第"+i+"个,X;"+x+"Y"+y+"角度"+theta[i] );
		}
		System.out.println("最大距离"+MaxDistance );
		for (int i=1;i<length-1;i++){
			PlaneCoordinate t=new PlaneCoordinate();
			int num=0;
			double dis;
			do{
				rho=differiencialNoise(MaxDistance,epsilon,vector[i]);
				alpha=differiencialNoise(2*PI,epsilon,theta[i]);
				double xp=rho*Math.cos(alpha);
				double yp=rho*Math.sin(alpha);
				t.setX(xy_new.get(i-1).getX()+xp);
				t.setY(xy_new.get(i-1).getY()+yp);
			//	System.out.println(xp+"VS"+yp);
				num++;
				if(num>5000){
					t.setX(xy.get(i).getX());
					t.setY(xy.get(i).getY());
					
				}
								//	System.out.println(i+"：dici"+num);
			    dis=getDistance(t.getX()-xy.get(i).getX(),t.getY()-xy.get(i).getY());
			}while(dis>vector[i]&&dis>vector[i+1]);
		    System.out.println(i+"相邻qian距离"+vector[i]+",后相邻距离"+vector[i+1]+"偏离距离："+dis );

			xy_new.add(t);	
		//	System.out.println(i+"：个原始"+xy.get(i)+"扰动"+t);
		}
		xy_new.add(xy.get(length-1));
		return xy_new;

	}
}
