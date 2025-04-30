package com.doctordaddysir;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PluginController loader = new PluginController();
        loader.registerPlugin("main.plugins.test.HelloPlugin");
        loader.registerPlugin("main.plugins.test.GoodbyePlugin");
        loader.start(scanner);
    }
}