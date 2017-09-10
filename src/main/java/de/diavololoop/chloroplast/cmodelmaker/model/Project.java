package de.diavololoop.chloroplast.cmodelmaker.model;

import de.diavololoop.chloroplast.cmodelmaker.model.basic.DataModel;
import de.diavololoop.chloroplast.cmodelmaker.model.texture.TextureHandler;

/**
 * Created by Chloroplast on 10.09.2017.
 */
public class Project {

    public static Project CURRENT = new Project();

    public Project(){
        minecraftModel  = new DataModel();
        textures        = new TextureHandler();
    }

    public DataModel minecraftModel;
    public TextureHandler textures;


}
