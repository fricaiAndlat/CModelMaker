package de.diavololoop.chloroplast.cmodelmaker.model.texture;

import de.diavololoop.chloroplast.cmodelmaker.Texture;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chloroplast on 10.09.2017.
 */
public class TextureHandler {

    Map<String, ImageText> textures = new HashMap<>();

    public void load(){
        Texture.clear();
        textures.forEach( (regName, textImage) -> new Texture(regName, textImage.fileName, textImage.getImage()));
    }

    public void save(){
        textures.clear();
        Texture.IMAGES.forEach( (regName, texture) -> textures.put(regName, new ImageText(texture)));
    }

    class ImageText {
        String data;
        String fileName;

        public ImageText(Texture texture){
            this(texture.getImage(), texture.getName());
        }

        public ImageText(Image image, String fileName){
            this.fileName = fileName;

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", buffer);
            } catch (IOException e) {
                //Should never happen
                e.printStackTrace();
                System.exit(-1);
            }

            data = Base64.getMimeEncoder().encodeToString(  buffer.toByteArray()  );
        }

        public Image getImage(){

            byte[] bytes = Base64.getMimeDecoder().decode(data);
            ByteArrayInputStream buffer = new ByteArrayInputStream( bytes );
            Image result = null;

            try {
                result = SwingFXUtils.toFXImage(ImageIO.read(buffer), null);
            } catch (IOException e) {
                //Should never happen
                e.printStackTrace();
                System.exit(-1);
            }

            return result;

        }
    }

}
