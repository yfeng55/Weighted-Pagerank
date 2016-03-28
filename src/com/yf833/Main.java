package com.yf833;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.File;

public class Main {


    // command line args //
    private static String input_path;   // -docs
    private static float f_param;       // -f (probability of tails)

    private static ArrayList<Page> pages;
    private static int n;
    private static float epsilon;


    public static void main(String[] args) throws IOException {

        //get input values from args array
        getInput(args);

        //store docs as page objects in pages array
        pages = processInputPages(input_path);
        System.out.println(pages.toString());




    }


    //count the number of docs there are, initialize an array of Page objects, for each page object compute a wordcount/score
    private static ArrayList<Page> processInputPages(String input_path) throws IOException {

        ArrayList<Page> pages = new ArrayList<>();
        File dir = new File(input_path);
        File[] directoryListing = dir.listFiles();

        if (directoryListing != null) {
            for (File child : directoryListing) {

                // create a Page object from child file, extract name of page
                Page p = new Page(child.getName().replaceFirst("[.][^.]+$", ""));

                // create an outlinks array for the page
                Document doc = Jsoup.parse(child, "UTF-8");
                Elements links = doc.select("a[href]");

                for(Element link : links){
                    p.outlinks.add(link.attr("href").replaceFirst("[.][^.]+$", ""));
                }

                // get the document's wordcount
                p.wordcount = doc.text().split(" +").length;

                // store page object in page array
                pages.add(p);

            }
        } else {
            System.out.println(input_path + " is not a valid directory");
        }

        return pages;
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










