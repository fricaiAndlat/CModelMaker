package de.diavololoop.chloroplast.cmodelmaker.view;

import javafx.scene.shape.TriangleMesh;

/**
 * Created by gast2 on 09.09.17.
 */
public class ModelMesh extends TriangleMesh {
    /*
      (1,1)
        p0 ---- p1
        |       |
        |       |
        |       |
        p2 ---- p3

     */
    float[] points = {
            0, 1, 0, //p0
            1, 1, 0, //p1
            0, 0, 0, //p2
            1, 0, 0, //p3
    };

    float[] texCoords = {
            0, 0, //x0, y0
            1, 0, //x1, y0
            1, 1, //x1, y1
            0, 1  //x0, y1
    };

    int[] faces = {
            0, 0, 1, 1, 2, 3, //upper left
            2, 3, 1, 1, 3, 2 //lower right
    };

    public ModelMesh(){
        this.getPoints().setAll(points);
        this.getTexCoords().setAll(texCoords);
        this.getFaces().setAll(faces);
    }

}
