/*
Name: Yufei Zhao
NetID: yzhao87
CSC172 Project3
Lab session: TR 6:15-7:30
No lab partners
*/

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StreetMap extends JFrame {

    public StreetMap(Graph g) {
        setTitle("Street Map");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 1000);
        setResizable(true);

    }

    public static void main(String[] args) {
        Graph myMap = new Graph();
        StreetMap Graphmap=new StreetMap(myMap);
        Canvas canvas=new Canvas(myMap);
        Graphmap.add(canvas);

        try {
            BufferedReader mapScan = new BufferedReader(new InputStreamReader(StreetMap.class.getResourceAsStream(args[0])));
            if (args[0].equals("ur.txt")){
                canvas.ismapUR=true;
            }
            if (args[0].equals("monroe.txt")){
                canvas.isMonroe=true;
            }
            if (args[0].equals("nys.txt")){
                canvas.isNY=true;
            }

            String inputLine;
            while ((inputLine=mapScan.readLine())!=null) {
                String[] inputArray = inputLine.split("\t");
                if (inputArray[0].equals("i")) {
                    Intersection i = new Intersection(inputArray[1], Double.parseDouble(inputArray[2]), Double.parseDouble(inputArray[3]));
                    myMap.intersectionAL.add(i); //add to the end of the list
                    myMap.addHash(i);
                }

                else if (inputArray[0].equals("r")) {
                    Intersection i1=(Intersection) myMap.getHash().get(inputArray[2]);
                    Intersection i2=(Intersection) myMap.getHash().get(inputArray[3]);

                    if (i1 == null || i2 == null) {
                        throw new NullPointerException("Intersection ID is not assigned yet");
                    }

                    i1.neighbors.add(i2);
                    i2.neighbors.add(i1);

                    Road r = new Road(inputArray[1], i1, i2);
                    r.weight=myMap.distance(i1, i2);
                    myMap.roadAL.add(r);
                }
            }
        }
        catch (IOException e){
            System.out.println("Cannot find the input map");
        }

        //Command line: map.txt [-show]
        //or Command line: map.txt [-meridianmap]
        if (args.length==2){
            if (args[1].equals("-show")){
                //show the map
                Graphmap.setVisible(true);
                canvas.paintMap(myMap);
            }
            else if (args[1].equals("-meridianmap")){
                //just print the minimum spanning tree in the command line
                myMap.Kruskal();
            }
        }

        //Command line: map.txt [-show] [-meridianmap]
        else if (args.length==3){
            //show the map
            Graphmap.setVisible(true);
            canvas.paintMap(myMap);

            //show the minimum spanning tree
            canvas.paintTree(myMap.Kruskal());
        }

        //Command line: map.txt [-directions startIntersection endIntersection]
        else if (args.length==4) {
            String startID = args[2];
            String endID = args[3];
            System.out.println(startID);
            System.out.println(endID);

            //just print the shortest path in the command line
            myMap.Dijkstra((Intersection) myMap.getHash().get(startID));
            myMap.directionPath(startID, endID);
        }

        //Command line: map.txt [-show] [-directions startIntersection endIntersection]
        //or Command line: map.txt [-directions startIntersection endIntersection] [-meridianmap]
        else if (args.length==5){
            if (args[1].equals("-show")){
                //show the map
                canvas.paintMap(myMap);
                Graphmap.setVisible(true);

                //show the shortest paths
                String startID = args[3];
                String endID = args[4];
                Intersection start=(Intersection) myMap.getHash().get(startID);
                Intersection end=(Intersection) myMap.getHash().get(endID);
                myMap.Dijkstra(start);
                canvas.paintPath(start, end);
                myMap.directionPath(startID, endID);

            }
            else if (args[4].equals("-meridianmap")){
                //print the shortest path in the command line
                String startID = args[2];
                String endID = args[3];
                myMap.Dijkstra((Intersection) myMap.getHash().get(startID));
                myMap.directionPath(startID, endID);

                //print the minimum spanning tree in the command line
                myMap.Kruskal();
            }
        }

        //Command line: map.txt [-show] [-directions startIntersection endIntersection] [-meridianmap]
        else if (args.length==6){
            //show the map; show the shortest path; show the minimum spanning tree
            Graphmap.setVisible(true);
            canvas.paintMap(myMap);

            //shortest path
            String startID = args[3];
            String endID = args[4];
            Intersection start=(Intersection) myMap.getHash().get(startID);
            Intersection end=(Intersection) myMap.getHash().get(endID);
            myMap.Dijkstra(start);
            canvas.paintPath(start, end);
            myMap.directionPath(startID, endID);

            //mininum spanning tree
            canvas.paintTree(myMap.Kruskal());
        }

        //Command line is not in the required format
        else {
            System.out.println("Wrong command input.");
        }

    }
}

