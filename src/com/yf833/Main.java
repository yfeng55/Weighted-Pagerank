package com.yf833;

public class Main {


    private static String input_path;   // -docs
    private static float f_param;       // -f



    public static void main(String[] args) {

        //get input values from args array
        getInput(args);

        



    }



    private static void getInput(String[] args){
        for(int i=0; i<args.length; i++){
            switch(args[i]){
                case "-docs":
                    input_path = args[i+1];
                    i++;
                    break;
                case "-f":
                    f_param = Float.parseFloat(args[i + 1]);
                    i++;
                    break;
                default:
                    throw new IllegalArgumentException("Must provide arguments: " + "-docs [input path] -f [F parameter]");
            }
        }

        System.out.println("-docs: " + input_path);
        System.out.println("-f: " + f_param);

    }


}










