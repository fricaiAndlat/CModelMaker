package de.diavololoop.chloroplast.cmodelmaker;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;


/**
 * Created by Chloroplast on 06.09.2017.
 */
public class ModelViewer {

    private final static int BORDER = 40;

    public enum Target {XY, YZ, XZ, MODEL}

    Group axis;

    SimpleDoubleProperty cameraTurnY = new SimpleDoubleProperty();
    SimpleDoubleProperty cameraTurnX = new SimpleDoubleProperty();

    double lastMouseX;
    double lastMouseY;


    public ModelViewer(Group root, Pane parent){

        parent.setOnMousePressed(event -> {lastMouseX = event.getX(); lastMouseY = event.getY();});
        parent.setOnMouseDragged(event -> {
            double dx = event.getX() - lastMouseX;
            double dy = event.getY() - lastMouseY;
            lastMouseX = event.getX();
            lastMouseY = event.getY();

            cameraTurnY.set(cameraTurnY.get() + dx/2);
            cameraTurnX.set(cameraTurnX.get() + dy/2);
        });


        Translate rootTranslation = new Translate();
        rootTranslation.xProperty().bind(parent.widthProperty().divide(2));
        rootTranslation.yProperty().bind(parent.heightProperty().divide(2));

        Scale rootScale = new Scale();
        rootScale.xProperty().bind(parent.heightProperty().add(-BORDER).divide(16));
        rootScale.yProperty().bind(parent.heightProperty().add(-BORDER).divide(-16));
        rootScale.zProperty().bind(parent.heightProperty().add(-BORDER).divide(16));

        Translate rootInnerTranslation = new Translate();
        rootInnerTranslation.setX(-8);
        rootInnerTranslation.setY(-8);

        Rotate cameraRotationX = new Rotate();
        cameraRotationX.setPivotY(8);
        cameraRotationX.setPivotZ(8);
        cameraRotationX.setAxis(new Point3D(1, 0, 0));
        cameraRotationX.angleProperty().bind(cameraTurnX);

        Rotate cameraRotationY = new Rotate();
        cameraRotationY.setPivotX(8);
        cameraRotationY.setPivotZ(8);
        cameraRotationY.setAxis(new Point3D(0, 1, 0));
        cameraRotationY.angleProperty().bind(cameraTurnY);


        root.getTransforms().addAll(rootTranslation, rootScale, rootInnerTranslation, cameraRotationX, cameraRotationY);

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
        axisY.setTranslateY(8);

        Box axisZ = new Box(0.1, 0.1, 16);
        axisZ.setMaterial(materialBlue);
        axisZ.setTranslateZ(8);


        axis = new Group();
        axis.getChildren().addAll(axisX, axisY, axisZ);

        root.getChildren().add(axis);

    }

}
