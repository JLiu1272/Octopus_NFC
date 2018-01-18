package com.enterprise.jliu.uitesting.constants;

/**
 * Created by JenniferLiu on 4/12/2017.
 */

public class Station {

    public String name;
    public double amount;

    public Station(String name, double amount)
    {
        this.name = name;
        this.amount = amount;
    }

    public double getAmount()
    {
        return this.amount;
    }

    public String getName()
    {
        return this.name;
    }
}
