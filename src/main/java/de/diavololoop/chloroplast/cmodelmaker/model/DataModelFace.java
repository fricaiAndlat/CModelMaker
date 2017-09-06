package de.diavololoop.chloroplast.cmodelmaker.model;

/**
 * Created by gast2 on 05.09.17.
 */
public class DataModelFace {

    public DataModelFace(){
        uv = new double[]{0, 0, 1, 1};
        texture = null;
        rotation = 0;
    }

    public double[] uv;

    public String texture;

    public int rotation;

}
