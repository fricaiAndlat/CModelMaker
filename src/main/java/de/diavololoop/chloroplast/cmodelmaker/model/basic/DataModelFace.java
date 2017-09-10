package de.diavololoop.chloroplast.cmodelmaker.model.basic;

/**
 * Created by Chloroplast on 05.09.17.
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

    public DataModelFace clone(){
        DataModelFace face = new DataModelFace();
        face.rotation = rotation;
        face.texture = texture;
        face.uv = new double[]{uv[0], uv[1], uv[2], uv[3]};
        return face;
    }

}
