package com.mycompany.app;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Account a = new Account(10);
        System.out.println("At first it was all like... " + a.toString());
        a.adjustBy(5);
        System.out.println("And then it was all like... " + a.toString());
        System.out.println( "wassssuuuuppp biatches!" );
    }
}
