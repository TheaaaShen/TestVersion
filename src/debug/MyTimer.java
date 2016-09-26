package debug;

import java.util.ArrayList;

public class MyTimer {
    
    // all time are stored by milli second(1/1000 second)
    long startTime;
    ArrayList<Long> endTime;
    
    static MyTimer instance = null;
    
    public MyTimer(){
        startTime = System.currentTimeMillis();
        endTime = new ArrayList<>();
    }
    public static MyTimer getInstance(){
        if(instance == null){
            instance = new MyTimer(); 
        }
        return instance;
    }
    
    public static void setStart(){
        getInstance().startTime = System.currentTimeMillis();
    }
    
    public static void addEndingTime(){
        long end = System.currentTimeMillis();
        getInstance().endTime.add(new Long(end));
    }
    
    public static void showLastTime(String hit){
        MyTimer ins = getInstance();
        long gap = ins.endTime.get(ins.endTime.size()-1) - ins.startTime;
        double time = (double)gap / 1000;
        System.out.println("Timer: " + hit + "\t" + time);
    }
    
    public static void showTime(String hit){
        addEndingTime();
        showLastTime(hit);
    }
    
}
