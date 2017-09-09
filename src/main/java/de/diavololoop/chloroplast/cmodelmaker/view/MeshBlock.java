package de.diavololoop.chloroplast.cmodelmaker.view;

import de.diavololoop.chloroplast.cmodelmaker.CModelMaker;
import de.diavololoop.chloroplast.cmodelmaker.Texture;
import de.diavololoop.chloroplast.cmodelmaker.model.DataModelBlock;
import de.diavololoop.chloroplast.cmodelmaker.model.DataModelFace;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gast2 on 09.09.17.
 */
public class MeshBlock extends Group {

    Map<CModelMaker.FACING, ModelMesh> meshes = new HashMap<>();
    Map<CModelMaker.FACING, MeshView> meshViews = new HashMap<>();

    public MeshBlock(){

        Arrays.stream(CModelMaker.FACING.values()).forEach(  (face) -> meshes.put(face, new ModelMesh())  );
        meshes.forEach(  (face, mesh) -> meshViews.put(face, new MeshView(mesh)));

        PhongMaterial materialEmpty = new PhongMaterial();
        materialEmpty.setDiffuseColor(Color.GREEN);

        meshViews.forEach(  (face, view) -> view.setMaterial(materialEmpty));
        meshViews.forEach(  (face, view) -> view.setCullFace(CullFace.BACK));

        Rotate rotateNorth = new Rotate(180, 0.5, 0.5, 0.5, new Point3D(0, 1, 0));
        Rotate rotateEast  = new Rotate(-90, 0.5, 0.5, 0.5, new Point3D(0, 1, 0));
        Rotate rotateSouth = new Rotate(  0, 0.5, 0.5, 0.5, new Point3D(0, 1, 0));
        Rotate rotateWest  = new Rotate( 90, 0.5, 0.5, 0.5, new Point3D(0, 1, 0));
        Rotate rotateDown  = new Rotate( 90, 0.5, 0.5, 0.5, new Point3D(1, 0, 0));
        Rotate rotateUp    = new Rotate(-90, 0.5, 0.5, 0.5, new Point3D(1, 0, 0));

        Rotate rotateY180 = new Rotate(180, 0.5, 0.5, 0.5, new Point3D(0, 1, 0));

        meshViews.get(CModelMaker.FACING.NORTH).getTransforms().addAll(rotateNorth, rotateY180);
        meshViews.get(CModelMaker.FACING.EAST) .getTransforms().addAll(rotateEast,  rotateY180);
        meshViews.get(CModelMaker.FACING.SOUTH).getTransforms().addAll(rotateSouth, rotateY180);
        meshViews.get(CModelMaker.FACING.WEST) .getTransforms().addAll(rotateWest,  rotateY180);
        meshViews.get(CModelMaker.FACING.UP)   .getTransforms().addAll(rotateUp,    rotateY180);
        meshViews.get(CModelMaker.FACING.DOWN) .getTransforms().addAll(rotateDown,  rotateY180);

        this.getChildren().addAll(meshViews.values());
    }

    public void updateTextureRotation(DataModelBlock modelBlock){
        for(CModelMaker.FACING face : CModelMaker.FACING.values()){

            DataModelFace blockFace = modelBlock.faces.get(face.toString().toLowerCase());


            ModelMesh mesh = meshes.get(face);

            int[] faces = mesh.faces;
            if(blockFace.rotation == 0){
                faces[1] = 0; //p0
                faces[3] = 1; //p1
                faces[5] = 3; //p2

                faces[7] = 3; //p2
                faces[9] = 1; //p1
                faces[11]= 2; //p3
            }else if(blockFace.rotation == 90){
                faces[1] = 3; //p0
                faces[3] = 0; //p1
                faces[5] = 2; //p2

                faces[7] = 2; //p2
                faces[9] = 0; //p1
                faces[11]= 1; //p3
            }else if(blockFace.rotation == 180){
                faces[1] = 2; //p0
                faces[3] = 3; //p1
                faces[5] = 1; //p2

                faces[7] = 1; //p2
                faces[9] = 3; //p1
                faces[11]= 0; //p3
            }else if(blockFace.rotation == 270){
                faces[1] = 1; //p0
                faces[3] = 2; //p1
                faces[5] = 0; //p2

                faces[7] = 0; //p2
                faces[9] = 2; //p1
                faces[11]= 3; //p3
            }
            mesh.getFaces().setAll(faces);



        }
    }

    public void updateTexture(DataModelBlock modelBlock){

        for(CModelMaker.FACING face : CModelMaker.FACING.values()){

            DataModelFace blockFace = modelBlock.faces.get(face.toString().toLowerCase());
            PhongMaterial material = new PhongMaterial();

            if(blockFace == null){
                blockFace = new DataModelFace();
                modelBlock.faces.put(face.toString().toLowerCase(), blockFace);
            }

            Texture texture = Texture.get(blockFace.texture);

            if(blockFace.texture == null || texture == null|| texture == Texture.NONE){
                material.setDiffuseColor(CModelMaker.faceColor.get(face));
            }

            if(texture.getImage() != null){
                material.setDiffuseMap(texture.getImage());
            }

            ModelMesh mesh = meshes.get(face);
            float[] texCoords = mesh.texCoords;
            texCoords[0] = (float)blockFace.uv[0]/16;     texCoords[1] = (float)blockFace.uv[1]/16;
            texCoords[2] = (float)blockFace.uv[2]/16;     texCoords[3] = (float)blockFace.uv[1]/16;
            texCoords[4] = (float)blockFace.uv[2]/16;     texCoords[5] = (float)blockFace.uv[3]/16;
            texCoords[6] = (float)blockFace.uv[0]/16;     texCoords[7] = (float)blockFace.uv[3]/16;
            mesh.getTexCoords().setAll(texCoords);


            meshViews.get(face).setMaterial(material);


        }

    }

}
