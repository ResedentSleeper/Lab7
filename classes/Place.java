package classes;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Objects;

public class Place implements Serializable {
    protected int x;
    protected int y;

    public Place(){
        this.x = 0;
        this.y = 0;
    }

    public Place(int x, int y){
        this.x = x;
        this.y = y;
    }
    public Place(Integer[] array){
        this.x = array[0];
        this.y = array[1];
    }

    public Place(JSONObject ob){
        this();
        for(String str: ob.keySet()){
            switch (str){
                case "x":{
                    x = ob.getInt("x");
                    break;
                }
                case "y":{
                    y = ob.getInt("y");
                    break;
                }
            }
        }
    }


    public int[] getLoc(){
        int[] arr ={x,y};
        return arr;
    }
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return x == place.x &&
                y == place.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "{ x:" + x + " y:" + y + "}";
    }
}