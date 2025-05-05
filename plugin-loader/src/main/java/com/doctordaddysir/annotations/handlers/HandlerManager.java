package com.doctordaddysir.annotations.handlers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HandlerManager {
    private final List<ReflectionScanner> handlers = new ArrayList<>();




    public void registerHandler(ReflectionScanner handler){
        handlers.add(handler);
    }

    public void execute(){
        handlers.stream().forEach(handler->{
            try {
                handler.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
