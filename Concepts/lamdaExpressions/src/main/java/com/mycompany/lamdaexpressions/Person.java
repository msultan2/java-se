////////////////////////////////////////////////////////////////////////////////
//
//  THIS SOFTWARE IS PROVIDED BY COSTAIN INTEGRATED TECHNOLOGY SOLUTIONS
//  LIMITED ``AS IS'', WITH NO WARRANTY, TERM OR CONDITION OF ANY KIND,
//  EXPRESS OR IMPLIED, AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING,
//  BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
//  FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL COSTAIN
//  INTEGRATED TECHNOLOGY SOLUTIONS LIMITED BE LIABLE FOR ANY LOSSES, CLAIMS
//  OR DAMAGES OF WHATEVER NATURE, INCLUDING ANY DIRECT, INDIRECT, INCIDENTAL,
//  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES EVEN IF ADVISED OF THE
//  POSSIBILITY OF SUCH DAMAGES OR LOSSES (INCLUDING, BUT NOT LIMITED TO,
//  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
//  OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
//  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
//  OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE.
//
//  Copyright 2016 (C) Costain Integrated Technology Solutions Limited.
//  All Rights Reserved.
//
////////////////////////////////////////////////////////////////////////////////
package com.mycompany.lamdaexpressions;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mohamed
 */
public class Person {

    private String givenName;
    private String surName;
    private int age;
    private String gender;
    private String eMail;
    private String phone;
    private String address;

    public Person(String givenName,
            String surName,
            int age,
            String gender) {
        this.givenName = givenName;
        this.surName = surName;
        this.age = age;
        this.gender = gender;
    }

    public static List<Person> createShortList() {
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Mohamed", "Sultan", 11, "Male"));
        persons.add(new Person("Mohamed4", "Sultan4", 14, "Male"));
        persons.add(new Person("Mohamed3", "Sultan3", 19, "Male"));
        persons.add(new Person("Mohamed2", "Sultan2", 16, "Male"));
        return persons;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getSurName() {
        return surName;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String geteMail() {
        return eMail;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    void printName() {
        System.out.println(givenName + " " + surName);
    }

}
