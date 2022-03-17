/*
Name: Yufei Zhao
NetID: yzhao87
CSC172 Project3
Lab session: TR 6:15-7:30
No lab partners
*/

import java.io.File;
import java.util.*;

public class Graph {
    HashMap<String, Intersection> dictionary=new HashMap<>(); //Intersection: each points; ArrayList: all neighbors
    ArrayList<Road> roadAL=new ArrayList<>();
    ArrayList<Intersection> intersectionAL=new ArrayList<>();
    Set<Intersection> settled=new HashSet<>();
    PriorityQueue<Intersection> pq=new PriorityQueue<>();
    int Vsize=0;
    int Esize=0;

    public Graph(ArrayList<Intersection> intersectionAL, ArrayList<Road> roadAL){
        this.intersectionAL=intersectionAL;
        this.roadAL=roadAL;
        Vsize=intersectionAL.size();
        Esize=roadAL.size();
    }

    public Graph(){
    }

    public void addHash(Intersection i){
        dictionary.put(i.getID(), i);
    }

    public HashMap getHash(){
        return dictionary;
    }


    //function to get neighbors of the given intersection
    public ArrayList<Intersection> getNeighbors(Intersection vertex){
        return vertex.getNeightbors();
    }

    public double haversine(double lat1, double lon1, double lat2, double lon2){
        double dLat=Math.toRadians(lat2-lat1);
        double dLon=Math.toRadians(lon2-lon1);
        lat1=Math.toRadians(lat1);
        lat2=Math.toRadians(lat2);
        double a =Math.pow(Math.sin(dLat/2), 2) + Math.pow(Math.sin(dLon/2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double rad=6731;
        double c=2 * Math.asin(Math.sqrt(a));
        return rad*c;
    }

    //weight of edge/road
    public double distance (Intersection i1, Intersection i2){
        double lon1=i1.getLongitude();
        double lon2=i2.getLongitude();
        double lat1=i1.getLatitude();
        double lat2=i2.getLatitude();
        double x=haversine(lat1, lon1, lat2, lon2);
        return x*1000;
    }

    //i is the source intersection
    public void Dijkstra(Intersection i){
        if (!getHash().containsKey(i.getID())){
            System.out.println("The start point is not in the graph");
            return;
        }
        for (int j=0; j<intersectionAL.size(); j++){
            intersectionAL.get(j).distance=Double.POSITIVE_INFINITY;
            intersectionAL.get(j).known=false;
        }
        i.distance=0;
        i.path=null;

        //add source intersection to the priority queue
        pq.add(i);
        while (!pq.isEmpty()){
            //remove the minimum distance intersection from the priority queue
            Intersection u=pq.remove();
            u.known=true;
            //add the intersection whose distance is finalized
            settled.add(u); //settled: known
            evaluatingNeighbors(u);
        }
    }

    public void evaluatingNeighbors(Intersection u) {
        for (Intersection v: getNeighbors(u)){
            if (!settled.contains(v)){
                if (distance(v, u) + u.distance < v.distance){
                    v.distance=distance(v, u)+u.distance;
                    u.path=v;
                    pq.add(v);
                }
            }
        }
    }

    //for spanning tree, sort the edges in non-descending orde
    //minimum weight spanning tree
    public Road[] Kruskal(){
        Road result[]=new Road[intersectionAL.size()-1];
        PriorityQueue<Road> queue=new PriorityQueue<>();
        //add all roads to the priority queue, sort the roads on weighted with method distance
        for (int i=0; i<roadAL.size(); i++){
            queue.add(roadAL.get(i));
        }

        HashMap<Intersection, Intersection> parent=new HashMap<>();
        //set original parent to itself
        for (int i=0; i<intersectionAL.size(); i++){
            parent.put(intersectionAL.get(i), intersectionAL.get(i)); //Map: Intersection, parent
        }

        int e=0;
        while (!queue.isEmpty() && e<intersectionAL.size()-1){
            Road next=queue.remove();
            Intersection x=find(parent, next.getIntersection1());
            Intersection y=find(parent, next.getIntersection2());
            if (!x.equals(y)){
                result[e++]=next;
                union(parent, x, y);
            }
        }
        for (int i=0; i<result.length; i++){
            if (result[i]!=null){
                System.out.println(result[i].getIntersection1().getID()+" --> "+result[i].getIntersection2().getID()+
                    " == "+result[i].weight);
            }
        }

        return result;
    }


    public void union(HashMap<Intersection, Intersection> parentMap, Intersection x, Intersection y) {
        Intersection xroot=find(parentMap, x);
        Intersection yroot=find(parentMap, y);

        parentMap.put(xroot, yroot);
    }


    public Intersection find(HashMap<Intersection, Intersection> parentMap, Intersection vertex){
        //chain of parent pointers from the vertex upwards through the tree
        //until an element is reached whose parent is itself
        if (!parentMap.get(vertex).equals(vertex)){
            parentMap.put(vertex, find(parentMap, parentMap.get(vertex)));
        }
        return parentMap.get(vertex);

    }

    //display the shortest path, print the path and distance
    public ArrayList<Intersection> directionPath(String startID, String endID){
        double totalDis=0;
        Intersection start=null;
        Intersection end=null;
        for (Intersection i: intersectionAL){
            if (i.getID().equals(startID)){
                start=i;
            }
            if (i.getID().equals(endID)){
                end=i;
            }
        }
        if (start==null || end==null){
            throw new NullPointerException();
        }
        Dijkstra(start);
        Intersection i=end;
        ArrayList<Intersection> pathList = new ArrayList<>();
        if (end.path!=null){
            while (i.path!=null){
                totalDis=totalDis+distance(i, i.path);
                pathList.add(i);
                i=i.path;
            }
            pathList.add(start);
            for (int j=pathList.size()-1; j>0; j--){
                System.out.print(pathList.get(j).getID()+" -> ");
            }
            System.out.print(end.getID());
            System.out.println();
            System.out.println("The distance of the path is "+totalDis+" miles.");
        }
        else {
            System.out.println("These two intersections are not connected");
        }

        return pathList;
    }

    public void ReadFile(String pathname){
         File file=new File(pathname);
         In in=new In(file);
         while (in.hasNextLine()){
             String line=in.readLine();
             String[] input=line.split("\t");
             if (input[0].equals("i")) {
                 Intersection i=new Intersection(input[1], Double.parseDouble(input[2]), Double.parseDouble(input[3]));
                 intersectionAL.add(i); //add to the end of the list
             }
             else if (input[0].equals("r")){
                 Intersection i1=null;
                 Intersection i2=null;
                 for (Intersection temp1:  intersectionAL){
                     if (input[2].equals(temp1.getID())){
                         i1=temp1;
                         break;
                     }
                 }
                 for (Intersection temp2:  intersectionAL){
                     if (input[3].equals(temp2.getID())){
                         i2=temp2;
                         break;
                     }
                 }
                 if (i1 == null || i2 == null) {
                     throw new NullPointerException("Intersection ID is not assigned yet");
                 }
                 Road r=new Road(input[1], i1, i2);
                 roadAL.add(r);
             }
         }
    }


}
