package org.example;

public class Employee {
    private String name;
    private int accessCode;

    public Employee(String name, int accessCode) {
        this.name = name;
        this.accessCode = accessCode;
    }

    public String getName() {
        return name;
    }

    public int getAccessCode() {
        return accessCode;
    }
}
