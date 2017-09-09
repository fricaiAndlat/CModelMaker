package de.diavololoop.chloroplast.cmodelmaker.view;

import de.diavololoop.chloroplast.cmodelmaker.model.DataModel;
import de.diavololoop.chloroplast.cmodelmaker.model.DataModelBlock;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Chloroplast on 06.09.2017.
 */
public class ModelViewer {

    private final double BORDER;

    private Camera camera;
    private SubScene scene;
    private Group root3D;

    public enum Target {XY, YZ, XZ, MODEL}

    private SimpleDoubleProperty cameraTurnY = new SimpleDoubleProperty();
    private SimpleDoubleProperty cameraTurnX = new SimpleDoubleProperty();

    private double lastMouseX;
    private double lastMouseY;

    private Group models;


    public ModelViewer(Pane viewMainPane, Target t) {

        root3D = new Group();

        if(t == Target.MODEL){
            BORDER = 0.6;

            viewMainPane.setOnMousePressed(event -> {lastMouseX = event.getX(); lastMouseY = event.getY();});
            viewMainPane.setOnMouseDragged(event -> {
                double dx = event.getX() - lastMouseX;
                double dy = event.getY() - lastMouseY;
                lastMouseX = event.getX();
                lastMouseY = event.getY();

                double ctx = cameraTurnX.get() - dy/2;
                ctx = Math.max(ctx, -90);
                ctx = Math.min(ctx, 90);

                cameraTurnY.set(cameraTurnY.get() - dx/2);
                cameraTurnX.set(ctx);
            });
        }else if(t == Target.YZ){
            BORDER = 0.8;
            cameraTurnY.set(-90);
        }else if(t == Target.XZ){
            BORDER = 0.8;
            cameraTurnX.set(-90);
        }else{
            BORDER = 0.8;
        }




        Translate rootTranslation = new Translate();
        rootTranslation.xProperty().bind(viewMainPane.widthProperty().divide(2));
        rootTranslation.yProperty().bind(viewMainPane.heightProperty().divide(2));

        Scale rootScale = new Scale();
        rootScale.xProperty().bind(viewMainPane.heightProperty().multiply(BORDER).divide(16));
        rootScale.yProperty().bind(viewMainPane.heightProperty().multiply(BORDER).divide(-16));
        if(t == Target.MODEL){
            rootScale.zProperty().bind(viewMainPane.heightProperty().multiply(BORDER).divide(16));
        } else {
            rootScale.zProperty().setValue(1);
        }


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


        root3D.getTransforms().addAll(rootTranslation, rootScale, rootInnerTranslation, cameraRotationX, cameraRotationY);

        models = new Group();

        Group axis;
        if(t == Target.MODEL){
            axis = buildAxes(0.1, 0.04);
        }else {
            axis = buildAxes(0.2, 0.1);
        }


        root3D.getChildren().addAll(models, axis);


        scene = new SubScene(root3D, 800, 800, true, SceneAntialiasing.BALANCED);
        if(t == Target.MODEL){
            camera = new PerspectiveCamera();
        }else{
            camera = new ParallelCamera();
        }

        camera.setNearClip(0.01);
        camera.setFarClip(400);

        scene.setCamera(camera);
        scene.setDepthTest(DepthTest.ENABLE);
        scene.setBlendMode(BlendMode.SRC_OVER);
        scene.setFill(Color.BLACK);
        scene.widthProperty().bind(viewMainPane.widthProperty());
        scene.heightProperty().bind(viewMainPane.heightProperty());

        viewMainPane.getChildren().add(scene);
    }

    private Group buildAxes(double size, double minSize){
        PhongMaterial materialRed = new PhongMaterial();
        materialRed.setDiffuseColor(Color.RED);

        PhongMaterial materialGreen = new PhongMaterial();
        materialGreen.setDiffuseColor(Color.GREEN);

        PhongMaterial materialBlue = new PhongMaterial();
        materialBlue.setDiffuseColor(Color.BLUE);

        Box axisX = new Box(16, size, size);
        axisX.setMaterial(materialRed);
        axisX.setTranslateX(8);

        Box axisY = new Box(size, 16, size);
        axisY.setMaterial(materialGreen);
        axisY.setTranslateY(8);

        Box axisZ = new Box(size, size, 16);
        axisZ.setMaterial(materialBlue);
        axisZ.setTranslateZ(8);

        Group axis = new Group();
        axis.getChildren().addAll(axisX, axisY, axisZ);

        PhongMaterial materialGray = new PhongMaterial();
        materialGray.setDiffuseColor(Color.GRAY);
        for(int i = 1; i < 16; ++i){
            Box subAxe = new Box(16, minSize, minSize);
            subAxe.setMaterial(materialGray);
            subAxe.setTranslateX(8);
            subAxe.setTranslateZ(i);
            axis.getChildren().add(subAxe);

        }
        for(int i = 1; i < 16; ++i){
            Box subAxe = new Box(minSize, minSize, 16);
            subAxe.setMaterial(materialGray);
            subAxe.setTranslateX(i);
            subAxe.setTranslateZ(8);
            axis.getChildren().add(subAxe);

        }

        return axis;
    }

    Map<DataModelBlock, MeshBlock> actualModels = new HashMap<>();

    public void updateDataFromModel(DataModel data){
        actualModels.forEach((model, mesh) -> {

            mesh.setScaleX(model.getSizeX());
            mesh.setScaleY(model.getSizeY());
            mesh.setScaleZ(model.getSizeZ());

            mesh.setTranslateX(model.from[0] + model.getSizeX()/2d - 0.5);
            mesh.setTranslateY(model.from[1] + model.getSizeY()/2d - 0.5);
            mesh.setTranslateZ(model.from[2] + model.getSizeZ()/2d - 0.5);

        });
    }

    public void rebuildFromDataModel(DataModel data){

        actualModels.clear();

        data.elements.forEach(modelBlock -> actualModels.put(modelBlock, new MeshBlock()));

        updateDataFromModel(data);

        models.getChildren().clear();
        models.getChildren().addAll(actualModels.values());
    }

    public void updateTexFromModel(DataModelBlock currentModelBlock) {
        actualModels.get(currentModelBlock).updateTexture(currentModelBlock);
    }
    public void updateTexRotation(DataModelBlock currentModelBlock) {
        actualModels.get(currentModelBlock).updateTextureRotation(currentModelBlock);
    }



}
