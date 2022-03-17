/*
Name: Yufei Zhao
NetID: yzhao87
CSC172 Project3
Lab session: TR 6:15-7:30
No lab partners
*/
public class Road implements Comparable<Road>{
    String ID;
    Intersection Intersection1ID;
    Intersection Intersection2ID;
    double weight;

    public Road(String ID, Intersection intersection1, Intersection intersection2) {
        this.ID = ID;
        this.Intersection1ID = intersection1;
        this.Intersection2ID = intersection2;
    }


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Intersection getIntersection1() {
        return Intersection1ID;
    }

    public void setIntersection1(Intersection intersection1) {
        this.Intersection1ID = intersection1;
    }

    public Intersection getIntersection2() {
        return Intersection2ID;
    }

    public void setIntersection2(Intersection intersection2) {
        this.Intersection2ID = intersection2;
    }

    @Override
    public int compareTo(Road o) {
        if (weight<o.weight){
            return -1;
        }
        else if (weight>o.weight){
            return 1;
        }
        else return 0;
    }
}
