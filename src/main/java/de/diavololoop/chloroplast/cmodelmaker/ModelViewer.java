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

    public ModelViewer(Group root, Camera cam){

        Group r = new Group();


        PhongMaterial materialRed = new PhongMaterial();
        materialRed.setDiffuseColor(Color.RED);

        PhongMaterial materialGreen = new PhongMaterial();
        materialGreen.setDiffuseColor(Color.GREEN);

        PhongMaterial materialBlue = new PhongMaterial();
        materialBlue.setDiffuseColor(Color.BLUE);

        Box axisX = new Box(16, 0.1, 0.1);
        axisX.setMaterial(materialRed);
        axisX.setTranslateX(8);

        Box axisY = new Box(0.1, 16, 0.1);
        axisY.setMaterial(materialGreen);
        axisY.setTranslateY(-8);

        Box axisZ = new Box(0.1, 0.1, 16);
        axisZ.setMaterial(materialBlue);
        axisZ.setTranslateZ(8);


        axis = new Group();
        axis.getChildren().addAll(axisX, axisY, axisZ);

        r.getChildren().add(axis);

        root.getChildren().add(r);

    }

}
