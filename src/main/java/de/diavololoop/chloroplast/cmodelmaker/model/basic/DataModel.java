package de.diavololoop.chloroplast.cmodelmaker.model.basic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.diavololoop.chloroplast.cmodelmaker.CModelMaker;

import java.io.*;
import java.util.*;

/**
 * Created by gast2 on 05.09.17.
 */
public class DataModel {

    public static DataModel load(File file) throws FileNotFoundException {

        Reader reader = new FileReader(file);


        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();

        Gson gson = gsonBuilder.create();
        DataModel model = gson.fromJson(reader, DataModel.class);

        Set<String> knownNames = new HashSet<>();

        model.elements.forEach(dmBlock -> {

            if(knownNames.contains(dmBlock.name)){
                dmBlock.name = ""+System.nanoTime();
            }

            knownNames.add(dmBlock.name);

            for(CModelMaker.FACING face: CModelMaker.FACING.values()){
                if(!dmBlock.faces.containsKey(face.toString().toLowerCase())){
                    dmBlock.faces.put(face.toString().toLowerCase(), new DataModelFace());
                }
            }

        });

        return model;

    }

    public void save(File f) throws IOException {
        Writer writer = new FileWriter(f);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();

        Gson gson = gsonBuilder.create();

        gson.toJson(this, writer);

        writer.flush();
        writer.close();
    }

    public DataModel(){
        textures = new HashMap<String, String>();
        elements = new LinkedList<DataModelBlock>();
    }

    public String credit = "made with CModelMaker by Chloroplast";
    public String parent = "block/block";

    public Map<String, String> textures;
    public List<DataModelBlock> elements;

    public boolean ambientocclusion = false;

}
