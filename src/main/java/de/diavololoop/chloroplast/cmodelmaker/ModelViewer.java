package de.diavololoop.chloroplast.cmodelmaker;

import javafx.scene.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;


/**
 * Created by Chloroplast on 06.09.2017.
 */
public class ModelViewer {

    public enum Target {XY, YZ, XZ, MODEL}

    SubScene scene;
    Camera camera;

    Group axis;

    public ModelViewer(Pane root, Target t){
        /*scene = new SubScene(root, root.getWidth(), root.getHeight(), true, SceneAntialiasing.BALANCED);
        scene.widthProperty().bind(root.widthProperty());
        scene.heightProperty().bind(root.heightProperty());*/

        if(t == Target.MODEL || true){
            camera = new PerspectiveCamera(true);
        }else{
            camera = new ParallelCamera();
        }

        PhongMaterial materialRed = new PhongMaterial();
        materialRed.setDiffuseColor(Color.RED);

        Box axisX = new Box(16, 1, 1);
        axisX.setMaterial(materialRed);

        axis = new Group();
        axis.getChildren().add(axisX);

        root.getChildren().add(axis);
        root.getChildren().add(camera);
    }

}
