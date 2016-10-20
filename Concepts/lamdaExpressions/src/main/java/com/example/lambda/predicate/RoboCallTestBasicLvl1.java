package com.example.lambda.predicate;

import com.example.lambda.Person;
import java.util.List;

/**
 *
 * @author MikeW
 */
public class RoboCallTestBasicLvl1 {
  
  public static void main(String[] args) {
    
    List<Person> pl = Person.createShortList();
    RoboContactMethodsWithoutLamba robo = new RoboContactMethodsWithoutLamba();
    
    System.out.println("\n==== Test 01 ====");
    System.out.println("\n=== Calling all Drivers ===");
    robo.callDrivers(pl);
    
    System.out.println("\n=== Emailing all Draftees ===");
    robo.emailDraftees(pl);
    
    System.out.println("\n=== Mail all Pilots ===");
    robo.mailPilots(pl);
    
  }

}
