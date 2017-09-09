package de.diavololoop.chloroplast.cmodelmaker;

import javafx.scene.image.Image;

import java.io.File;
import java.util.HashMap;

/**
 * Created by gast2 on 06.09.17.
 */
public class Texture {

    public final static HashMap<String, Texture> IMAGES = new HashMap<String, Texture>();
    public final static Texture NONE = new Texture("none") {

    };

    String registerName;
    String name;
    Image image;
    public final int w,h;

    private Texture(String name){
        this.name = "<"+name.toUpperCase()+">";
        this.registerName = "#"+name;
        w = 16;
        h = 16;

    }

    public Texture(Image image, String imageName){
        this.name = imageName;
        this.image = image;

        w = (int)image.getWidth();
        h = (int)image.getHeight();

        int index = 0;
        while(IMAGES.containsKey("#"+index)){
            ++index;
        }
        registerName = "#"+index;

        IMAGES.put(registerName, this);
    }

    public Texture(File file) {

        this(new Image("file:"+file, 512, 512, true, false), file.getName().substring(0, file.getName().lastIndexOf(".")));

    }

    public Image getImage(){
        return image;
    }

    public String getName(){
        return name;
    }

    @Override
    public boolean equals(Object other){
        if(other instanceof Texture){
            return ((Texture) other).registerName.equals(registerName);
        }

        if(other instanceof String){
            return ((String) other).equals(registerName);
        }
        return false;
    }

    @Override
    public String toString(){
        return name+" ("+registerName+")";
    }

    public static Texture get(String str){
        Texture result = IMAGES.get(str);
        return result==null ? Texture.NONE : result;
    }

}
