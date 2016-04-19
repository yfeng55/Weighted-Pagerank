import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.io.File;

public class Main {


    // command line args //
    private static String input_path;   // -docs
    private static float f_param;       // -f (probability of tails)

    private static ArrayList<Page> pages;
    private static int n = 0;
    private static float epsilon;
    private static float sum_base = 0;

    private static Hashtable<String, Integer> lookup = new Hashtable<>();
    private static float weights[][];



    public static void main(String[] args) throws IOException {

        //get input values from args array
        getInput(args);

        //store docs as page objects in pages array
        pages = processInputPages(input_path);



        ///// (1) Initialize epsilon, base scores, base scores sum, scores /////

        epsilon = (float) 0.01/n;
        //System.out.println("EPSILON: " + epsilon);

        for(Page p : pages){ sum_base += p.base; }
        for(Page p : pages){ p.score = p.base = p.base / sum_base; }
        //System.out.println("SUM OF BASE SCORES: " + sum_base);


        ///// (2) Initialize link weights for outlinks of all pages /////
        weights = new float[n][n];

        for(Page p : pages){

            // if page has no outlinks, then assign w[p] a weight of 1/n for all q
            if(p.outlinks.size() == 0){
                for(int j=0; j<n; j++){
                    weights[j][lookup.get(p.title)] = (float) 1.0/n;
                }
            }

            // compute link weights
            else{

                // get unnormalized weights
                for(Link l : p.outlinks){
                    weights[lookup.get(l.to)][lookup.get(l.from)] += calculateWeight(l, p);
                }

                //get sum
                float sum = 0;
                for(int j=0; j<n; j++){
                    sum += weights[j][lookup.get(p.title)];
                }

                //adjust weights to be normalized
                for(int j=0; j<n; j++){
                    weights[j][lookup.get(p.title)] = weights[j][lookup.get(p.title)] / sum;
                }

            }

        }


        ///// (3) Main loop to compute scores /////
        boolean changed = true;

        while(changed){
            changed = false;

            for(Page p : pages){

//                System.out.println("----- " + p.title + " -----");
//                System.out.println("p.base: " + p.base);
//                System.out.println("f param: " + f_param);


                float q_sum = 0;
                for(int j=0; j<n; j++){

//                    System.out.print(weights[j][lookup.get(p.title)] + "\t | \t");
//                    System.out.print(pages.get(j).score + "\n");

                    q_sum += (weights[lookup.get(p.title)][j] * pages.get(j).score);
                }

//                System.out.println("q_sum: " + q_sum + "\n");

                p.newscore = (1 - f_param) * p.base + f_param * q_sum;

//                System.out.println("newscore: " + p.newscore);

                if (Math.abs(p.newscore - p.score) > epsilon){
//                    System.out.println("DIFF: " + Math.abs(p.newscore - p.score));
                    changed = true;
                }

            }

            //initialize scores
            for(Page p : pages){
                p.score = p.newscore;
            }

        }




        ///// (*) PRINT DEBUGGING INFO /////

//        System.out.println("\n-------------------------------------\n");

//        Util.print2DArray(weights);
//        System.out.println(pages.toString());


        Collections.sort(pages, new Comparator<Page>() {
            public int compare(Page p1, Page p2) {
                return p1.score > p2.score ? -1 : 1;
            }
        });

        for(int j=0; j<n; j++){
            System.out.print(String.format("%-15s", pages.get(j).title));
                    System.out.println(pages.get(j).score);
        }



    }



    // compute a weight given a link (l) and a page (p)
    private static int calculateWeight(Link l, Page p){

        int score = 1;

        if(l.isBold){
            score++;
        }

        return score;
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


                    Link newlink = new Link(p.title, link.attr("href").replaceFirst("[.][^.]+$", ""));
                    // give link a "bold" flag if it is in a header or bold tag

                    String parent_tag = link.parent().tagName();
                    if(parent_tag.equals("b") || parent_tag.equals("em") || parent_tag.equals("h1") || parent_tag.equals("h2") || parent_tag.equals("h3") || parent_tag.equals("h4")){
                        newlink.isBold = true;
                    }

                    p.outlinks.add(newlink);

                }

                // get the document's wordcount and initialize its base val
                p.wordcount = doc.text().split(" +").length;
                p.base = (float) (Math.log(p.wordcount) / Math.log(2));
                //System.out.println("P.BASE: " + p.base);


                // store page object in page array
                pages.add(p);

                // add an index for the page
                lookup.put(p.title, n);

                //increment page count
                n++;
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

//        System.out.println("-docs: " + input_path);
//        System.out.println("-f: " + f_param);

    }


}










