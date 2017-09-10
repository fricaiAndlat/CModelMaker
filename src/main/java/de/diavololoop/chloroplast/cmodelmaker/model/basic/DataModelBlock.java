package de.diavololoop.chloroplast.cmodelmaker.model.basic;

import de.diavololoop.chloroplast.cmodelmaker.CModelMaker;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chloroplast on 05.09.17.
 */
public class DataModelBlock {

    public DataModelBlock(){
        name = System.nanoTime() + "";
        faces = new HashMap<String, DataModelFace>();

        for(CModelMaker.FACING face: CModelMaker.FACING.values()){
            faces.put(face.toString().toLowerCase(), new DataModelFace());
        }

    }


    public String name;

    public int[] from = {0, 0, 0};
    public int[] to = {1, 1, 1};

    public Map<String, DataModelFace> faces;

    public String toString(){
        return name;
    }

    public DataModelBlock clone(){
        DataModelBlock block = new DataModelBlock();
        block.name = System.nanoTime() + "";
        block.from = new int[]{from[0], from[1], from[2]};
        block.to   = new int[]{to[0],   to[1],   to[2]};
        block.faces = new HashMap<String, DataModelFace>();
        faces.forEach((key, val) -> block.faces.put(key, val.clone()));
        return block;
    }

    public int getSizeX(){  return to[0] - from[0];}
    public int getSizeY(){  return to[1] - from[1];}
    public int getSizeZ(){  return to[2] - from[2];}

}
