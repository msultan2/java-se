package javabrains;

public class Triangle {
	private String type;
	private String type2;
	private int height;
	
	public Triangle(String type2){
		this.type2=type2;
	}
	public Triangle(int height){
		this.height=height;
	}
	public Triangle(String type2,int height){
		this.type2=type2;
		this.height=height;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType2() {
		return type2;
	}
	public int getHeight() {
		return height;
	}
	public void draw(){
		System.out.println("Type="+getType()+" ,Triangle Drawn");
		System.out.println("Type2="+getType2()+" ,Triangle Drawn of height:"+getHeight());
	}
}
 