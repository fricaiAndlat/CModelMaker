package de.diavololoop.chloroplast.cmodelmaker.model;

import java.util.Map;

/**
 * Created by gast2 on 05.09.17.
 */
public class DataModelBlock {

    /*public enum FACES {
        NORTH, EAST, SOUTH, WEST, UP, DOWN
    }*/

    public String name;

    public int[] from;
    public int[] to;

    public Map<String, DataModelFace> faces;

    public String toString(){
        return name;
    }

}
