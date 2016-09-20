package javabrains;

import java.util.List;

public class TriangleList {

	private List<Point> points;
	
	public List<Point> getPoints() {
		return points;
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

	public void draw(){
		System.out.println("List Triagle");
		for (Point point : points){
			System.out.println("PointA=("+point.getX()+", "+point.getY()+")");	
		}
	}
}
 