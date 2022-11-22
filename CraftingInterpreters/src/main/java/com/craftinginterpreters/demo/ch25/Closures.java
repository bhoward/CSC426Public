package com.craftinginterpreters.demo.ch25;

import java.util.function.Function;

/**
 * Examples of some closure-like things in Java.
 * 
 * @author bhoward
 */
public class Closures {
    private int n;

    public Closures(int n) {
        this.n = n;
    }

    public Object demo1(String s) {
        int n = 37;

        // A non-static inner class gets a reference to the enclosing "this"
        // as well as copies of (effectively) final local variables
        class Foo {
            @Override
            public String toString() {
                return String.format("outer n = %d; inner n = %d; s = %s", Closures.this.n, n, s);
            }
        }

        // Uncomment for an error:
        // n++;

        return new Foo();
    }

    public Function<Integer, Integer> demo2(String s) {
        int m = 12;

        // Uncomment for an error:
        // m++;

        return n -> {
            System.out.printf("outer n = %d; inner n = %d; inner m = %d; inner s = %s\n", this.n, n, m, s);
            return n + 1;
        };
    }

    static class Demo3 {
        private Closures closures;
        private int m;
        private String s;

        public Demo3(final Closures closures, final int m, final String s) {
            this.closures = closures;
            this.m = m;
            this.s = s;
        }

        public int apply(int n) {
            System.out.printf("outer n = %d; inner n = %d; inner m = %d; inner s = %s\n", closures.n, n, m, s);
            return n + 1;
        }
    }

    public Demo3 demo3(String s) {
        int m = 12;

        return new Demo3(this, m, s);
    }

    public static void main(String[] args) {
        Closures x = new Closures(42);
        System.out.println(x.demo1("Hello"));
        x.n++;
        System.out.println(x.demo1("Goodbye"));

        var f = x.demo2("Lambda!");
        System.out.println(f.apply(37));

        var g = x.demo3("Class!");
        System.out.println(g.apply(37));
    }
}
