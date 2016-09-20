package javabrains;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DrawingApp {

	public static void main(String[] args) {
//		Triangle triangle=new Triangle();
		
//		BeanFactory factory = new XmlBeanFactory (new FileSystemResource("Spring.xml"));
		
		ApplicationContext context= new ClassPathXmlApplicationContext("spring.xml");
		Triangle triangle =(Triangle) context.getBean("triangle");
		Triangle triangle2 =(Triangle) context.getBean("triangle2");
		Triangle triangle3 =(Triangle) context.getBean("triangle3");
		triangle.draw();
		System.out.println("Second Triangle");
		triangle2.draw();
		System.out.println("Third Triangle");
		triangle3.draw();
		
		TriangleInject triangleInject =(TriangleInject) context.getBean("triangleInjection");
		triangleInject.draw();
		TriangleInject triangleInject1 =(TriangleInject) context.getBean("triangleInjection1");
		triangleInject1.draw();
		TriangleInject triangleInjectionInnerBean =(TriangleInject) context.getBean("triangleInjectionInnerBean");
		triangleInjectionInnerBean.draw();
		
		TriangleList triangleList=(TriangleList) context.getBean("triangleList");
		triangleList.draw();
		
		TriangleInject triangleAutowiring=(TriangleInject) context.getBean("triangleAutowiring");
		triangleAutowiring.draw();
		
	}

}
	