package de.diavololoop.chloroplast.cmodelmaker;

import de.diavololoop.chloroplast.cmodelmaker.model.DataModel;
import de.diavololoop.chloroplast.cmodelmaker.model.DataModelBlock;
import de.diavololoop.chloroplast.cmodelmaker.model.DataModelFace;
import de.diavololoop.chloroplast.cmodelmaker.view.ModelViewer;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by gast2 on 05.09.17.
 */
public class CModelMaker extends Application{

    public enum FACING {NORTH, EAST, SOUTH, WEST, UP, DOWN}

    public static void main(String[] args){

        CModelMaker.launch(args);

    }




    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);
        root = loader.load(CModelMaker.class.getResourceAsStream("../../../../gui/gui.fxml"));
        root.setTop(getMenu(stage));

        initFields();
        linkFields();
        registerListeners();

        Scene scene = new Scene(root, 900, 600);

        stage.setTitle("CModelMaker by Chloroplast");
        stage.setScene(scene);
        stage.show();
    }

    public MenuBar getMenu(Stage stage) {

        MenuItem menuItemNew  = new MenuItem("New");
        MenuItem menuItemLoad = new MenuItem("Load Json");
        MenuItem menuItemSave = new MenuItem("Save JSon");
        MenuItem menuItemTexture = new MenuItem("ImportTexture");
        MenuItem menuItemExit = new MenuItem("Exit");

        menuItemLoad.setOnAction(event -> {
            FileChooser fc = new FileChooser();
            load(fc.showOpenDialog(stage));
        });

        menuItemSave.setOnAction(event -> {
            FileChooser fc = new FileChooser();
            save(fc.showSaveDialog(stage));
        });

        menuItemTexture.setOnAction(event -> {
            FileChooser fc = new FileChooser();
            sideTexture.getItems().add(new Texture(fc.showOpenDialog(stage)));
        });

        Menu menuFile = new Menu("File");
        menuFile.getItems().add(menuItemNew);
        menuFile.getItems().add(menuItemLoad);
        menuFile.getItems().add(menuItemSave);
        menuFile.getItems().add(menuItemTexture);
        menuFile.getItems().add(menuItemExit);


        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menuFile);

        return menuBar;
    }


    BorderPane root;

    public final Map <FACING, Pane> wrapperMap  = new HashMap<FACING, Pane>();
    public final Map <FACING, Canvas> canvasMap = new HashMap<FACING, Canvas>();
    public final static Map <FACING, Color> faceColor   = new HashMap<FACING, Color>();

    DataModelBlock currentModelBlock;
    DataModel      currentModel = new DataModel();

    ModelViewer modelViewMain;
    ModelViewer modelViewXY;
    ModelViewer modelViewYZ;
    ModelViewer modelViewXZ;

    private void initFields() {

        wrapperMap.put( FACING.NORTH, wrapperNorth );
        wrapperMap.put( FACING.EAST,  wrapperEast  );
        wrapperMap.put( FACING.SOUTH, wrapperSouth );
        wrapperMap.put( FACING.WEST,  wrapperWest  );
        wrapperMap.put( FACING.UP,    wrapperUp    );
        wrapperMap.put( FACING.DOWN,  wrapperDown  );

        canvasMap.put( FACING.NORTH, canvasNorth );
        canvasMap.put( FACING.EAST,  canvasEast  );
        canvasMap.put( FACING.SOUTH, canvasSouth );
        canvasMap.put( FACING.WEST,  canvasWest  );
        canvasMap.put( FACING.UP,    canvasUp    );
        canvasMap.put( FACING.DOWN,  canvasDown  );

        faceColor.put( FACING.NORTH, Color.BLUE   );
        faceColor.put( FACING.EAST,  Color.GREEN  );
        faceColor.put( FACING.SOUTH, Color.RED    );
        faceColor.put( FACING.WEST,  Color.YELLOW );
        faceColor.put( FACING.UP,    Color.CYAN   );
        faceColor.put( FACING.DOWN,  Color.ORANGE );

        sideMenu.getItems().addAll(FACING.values());
        sideMenu.getSelectionModel().selectFirst();

        sideTexture.getItems().add(Texture.NONE);
        sideTextureRotation.getItems().addAll(new Integer(0), new Integer(90), new Integer(180), new Integer(270));

        modelViewMain = new ModelViewer(viewMainPane, ModelViewer.Target.MODEL);
        modelViewXY   = new ModelViewer(viewXY, ModelViewer.Target.XY);
        modelViewYZ   = new ModelViewer(viewYZ, ModelViewer.Target.YZ);
        modelViewXZ   = new ModelViewer(viewXZ, ModelViewer.Target.XZ);

    }

    private void linkFields(){

        for(FACING face: FACING.values()){
            Canvas canvas = canvasMap.get(face);
            Pane   pane   = wrapperMap.get(face);

            canvas.widthProperty().bind(pane.widthProperty());
            canvas.heightProperty().bind(pane.heightProperty());

            pane.minWidthProperty().bind(root.heightProperty().divide(6));
            pane.prefWidthProperty().bind(root.heightProperty().divide(6));
        }

        viewXY.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT)));
        viewYZ.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT)));
        viewXZ.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT)));

    }

    private void registerListeners(){

        cubeList.getSelectionModel()
                .selectedItemProperty().addListener((observable, o, t1) -> {

            if(t1 == null)return;

            Optional<DataModelBlock> block = currentModel.elements.stream().filter(element -> element.name.equals(t1.toString())).findAny();
            if(block.isPresent()){
                currentModelBlock = block.get();
                updateBlockInfo();
                updateTexturePreview();
            }else{
                System.err.println("block "+t1.toString()+"was not found in data. this should not happen");
            }
        });

        cubeSizeX.setOnAction(event -> rewriteBlockBounds());
        cubeSizeY.setOnAction(event -> rewriteBlockBounds());
        cubeSizeZ.setOnAction(event -> rewriteBlockBounds());
        cubePositionX.setOnAction(event -> rewriteBlockBounds());
        cubePositionY.setOnAction(event -> rewriteBlockBounds());
        cubePositionZ.setOnAction(event -> rewriteBlockBounds());

        cubeSizeX.focusedProperty().addListener(prop -> rewriteBlockBounds());
        cubeSizeY.focusedProperty().addListener(prop -> rewriteBlockBounds());
        cubeSizeZ.focusedProperty().addListener(prop -> rewriteBlockBounds());
        cubePositionX.focusedProperty().addListener(prop -> rewriteBlockBounds());
        cubePositionY.focusedProperty().addListener(prop -> rewriteBlockBounds());
        cubePositionZ.focusedProperty().addListener(prop -> rewriteBlockBounds());

        uvx0.setOnAction(event -> rewriteFaceUV());
        uvy0.setOnAction(event -> rewriteFaceUV());
        uvx1.setOnAction(event -> rewriteFaceUV());
        uvy1.setOnAction(event -> rewriteFaceUV());

        uvx0.focusedProperty().addListener(prop -> rewriteFaceUV());
        uvy0.focusedProperty().addListener(prop -> rewriteFaceUV());
        uvx1.focusedProperty().addListener(prop -> rewriteFaceUV());
        uvy1.focusedProperty().addListener(prop -> rewriteFaceUV());


        cubeName.setOnAction(event -> renameCurrent(cubeName.getText()));
        cubeName.focusedProperty().addListener(prop ->  renameCurrent(cubeName.getText()));

        sideMenu.getSelectionModel().selectedItemProperty().addListener((observable, o, t1) -> {
            updateTexturePreview();
            updateBlockInfo();
        });

        sideTexture.getSelectionModel().selectedItemProperty().addListener(  (observable, o, t1) -> setFaceTexture(t1)  );
        sideTextureRotation.getSelectionModel().selectedItemProperty().addListener(  (observable, o, t1) -> setFaceRotation(t1)  );
    }

    private void setFaceRotation(int faceRotation) {
        if(currentModelBlock == null){
            return;
        }
        FACING f = sideMenu.getSelectionModel().getSelectedItem();
        DataModelFace face = currentModelBlock.faces.get(f.toString().toLowerCase());

        face.rotation = faceRotation;
        viewUpdateTexRotation();
    }

    private void setFaceTexture(Texture faceTexture) {
        if(currentModelBlock == null || faceTexture == null){
            return;
        }
        FACING f = sideMenu.getSelectionModel().getSelectedItem();
        DataModelFace face = currentModelBlock.faces.get(f.toString().toLowerCase());

        face.texture = faceTexture.registerName;

        updateTexturePreview();
        viewUpdateTex();

    }

    private void rewriteFaceUV() {
        if(currentModelBlock == null){
            return;
        }
        FACING f = sideMenu.getSelectionModel().getSelectedItem();
        DataModelFace face = currentModelBlock.faces.get(f.toString().toLowerCase());

        int x0 = uvx0.getText().matches("\\d+\\.?\\d*") ? (int)(10 * Double.parseDouble(uvx0.getText())) : 0;
        int y0 = uvy0.getText().matches("\\d+\\.?\\d*") ? (int)(10 * Double.parseDouble(uvy0.getText())) : 0;
        int x1 = uvx1.getText().matches("\\d+\\.?\\d*") ? (int)(10 * Double.parseDouble(uvx1.getText())) : 0;
        int y1 = uvy1.getText().matches("\\d+\\.?\\d*") ? (int)(10 * Double.parseDouble(uvy1.getText())) : 0;

        uvx0.setText( x0%10 == 0 ? ""+(x0/10) : ""+(x0/10d));
        uvy0.setText( y0%10 == 0 ? ""+(y0/10) : ""+(y0/10d));
        uvx1.setText( x1%10 == 0 ? ""+(x1/10) : ""+(x1/10d));
        uvy1.setText( y1%10 == 0 ? ""+(y1/10) : ""+(y1/10d));

        face.uv[0] = x0/10d;
        face.uv[1] = y0/10d;
        face.uv[2] = x1/10d;
        face.uv[3] = y1/10d;

        updateTexturePreview();
        viewUpdateTex();
    }


    private void rewriteBlockBounds() {
        if(currentModelBlock == null){
            return;
        }

        if(!cubePositionX.getText().matches("\\d\\d?")){ cubePositionX.setText("0"); }
        if(!cubePositionY.getText().matches("\\d\\d?")){ cubePositionY.setText("0"); }
        if(!cubePositionZ.getText().matches("\\d\\d?")){ cubePositionZ.setText("0"); }
        if(!cubeSizeX.getText().matches("\\d\\d?")){ cubeSizeX.setText("0"); }
        if(!cubeSizeY.getText().matches("\\d\\d?")){ cubeSizeY.setText("0"); }
        if(!cubeSizeZ.getText().matches("\\d\\d?")){ cubeSizeZ.setText("0"); }

        currentModelBlock.from[0] = Integer.parseInt(cubePositionX.getText());
        currentModelBlock.from[1] = Integer.parseInt(cubePositionY.getText());
        currentModelBlock.from[2] = Integer.parseInt(cubePositionZ.getText());

        currentModelBlock.to[0] = currentModelBlock.from[0] + Integer.parseInt(cubeSizeX.getText());
        currentModelBlock.to[1] = currentModelBlock.from[1] + Integer.parseInt(cubeSizeY.getText());
        currentModelBlock.to[2] = currentModelBlock.from[2] + Integer.parseInt(cubeSizeZ.getText());

        viewRecreate();
    }

    private void viewRecreate(){
        modelViewMain.rebuildFromDataModel(currentModel);
        modelViewXY.rebuildFromDataModel(currentModel);
        modelViewYZ.rebuildFromDataModel(currentModel);
        modelViewXZ.rebuildFromDataModel(currentModel);
    }
    private void viewUpdate(){
        modelViewMain.updateDataFromModel(currentModel);
        modelViewXY.updateDataFromModel(currentModel);
        modelViewYZ.updateDataFromModel(currentModel);
        modelViewXZ.updateDataFromModel(currentModel);
    }
    private void viewUpdateTex(){
        modelViewMain.updateTexFromModel(currentModelBlock);
        modelViewXY.updateTexFromModel(currentModelBlock);
        modelViewYZ.updateTexFromModel(currentModelBlock);
        modelViewXZ.updateTexFromModel(currentModelBlock);
    }
    private void viewUpdateTexRotation(){
        modelViewMain.updateTexRotation(currentModelBlock);
        modelViewXY.updateTexRotation(currentModelBlock);
        modelViewYZ.updateTexRotation(currentModelBlock);
        modelViewXZ.updateTexRotation(currentModelBlock);
    }

    private void updateBlockInfo() {

        cubeSizeX.setText(""+(currentModelBlock.to[0] - currentModelBlock.from[0]));
        cubeSizeY.setText(""+(currentModelBlock.to[1] - currentModelBlock.from[1]));
        cubeSizeZ.setText(""+(currentModelBlock.to[2] - currentModelBlock.from[2]));

        cubePositionX.setText(""+currentModelBlock.from[0]);
        cubePositionY.setText(""+currentModelBlock.from[1]);
        cubePositionZ.setText(""+currentModelBlock.from[2]);

        cubeName.setText(currentModelBlock.name);
        cubeGroup.setText("TODO");

        String side = sideMenu.getSelectionModel().getSelectedItem().toString().toLowerCase();
        DataModelFace face = currentModelBlock.faces.get(side);

        sideTextureRotation.getSelectionModel().selectFirst();
        sideTextureRotation.getSelectionModel().select(new Integer(face.rotation));

        String current = face.texture;
        sideTexture.getSelectionModel().clearSelection();
        sideTexture.getSelectionModel().select(Texture.get(face.texture));
        face.texture = current;

        uvChange(0,  0);
        uvChange(1,  0);
        uvChange(2,  0);
        uvChange(3,  0);

    }

    private void renameCurrent(String text) {
        if(currentModelBlock == null || currentModelBlock.name.equals(text)){
            return;
        }
        currentModelBlock.name = text;
        cubeList.refresh();
    }

    private void updateTexturePreview(){

        for(FACING face: FACING.values()){

            Canvas canvas = canvasMap.get(face);
            GraphicsContext context = canvas.getGraphicsContext2D();


            if(currentModelBlock == null || currentModelBlock.faces.get(face.toString().toLowerCase()).texture == null){
                context.setFill( faceColor.get(face) );
                context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            }else{
                context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

                DataModelFace mface = currentModelBlock.faces.get(face.toString().toLowerCase());

                Texture tex = Texture.IMAGES.get(mface.texture);
                if(tex == null){
                    context.setFill( faceColor.get(face) );
                    context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    continue;
                }
                Image image = tex.getImage();
                if(image != null){
                    context.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());

                    double x0 = mface.uv[0]/16d;
                    double y0 = mface.uv[1]/16d;
                    double x1 = mface.uv[2]/16d;
                    double y1 = mface.uv[3]/16d;

                    if(x0 > x1){
                        double temp = x0;
                        x0 = x1;
                        x1 = temp;
                    }
                    if(y0 > y1){
                        double temp = y0;
                        y0 = y1;
                        y1 = temp;
                    }
                    context.setStroke(Color.BLACK);
                    context.setLineWidth(1.5);
                    context.strokeRect(x0*canvas.getWidth()-1.5, y0*canvas.getHeight()-1.5, (x1-x0)*canvas.getWidth()+3, (y1-y0)*canvas.getHeight()+3);

                }


            }
        }
    }
    private void save(File file) {
        if(file == null){
            return;
        }

        try {

            currentModel.textures.clear();
            Texture.IMAGES.forEach(  (key,val) ->  currentModel.textures.put(val.registerName.substring(1), "blocks/"+val.name) );
            currentModel.save(file);

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
    }


    private void load(File file) {
        if(file == null){
            return;
        }

        try {
            currentModel = DataModel.load(file);
            cubeList.getItems().clear();
            currentModel.elements.forEach(cubeList.getItems()::add);
            viewRecreate();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }

    }

    public void uvChange(int batch, int delta){
        if(currentModelBlock==null){
            return;
        }

        FACING f = sideMenu.getSelectionModel().getSelectedItem();
        DataModelFace face = currentModelBlock.faces.get(f.toString().toLowerCase());
        TextField field;
        if(batch == 0){
            field = uvx0;
        }else if(batch == 1){
            field = uvy0;
        }else if(batch == 2){
            field = uvx1;
        }else{
            field = uvy1;
        }

        face.uv[batch] += delta;

        int value = (int)(face.uv[batch] * 10);

        field.setText( value%10==0 ? Integer.toString(value/10) : Double.toString(face.uv[batch]));

        updateTexturePreview();
        viewUpdateTex();
    }

    public void changeSize(int batch, int delta){
        if(currentModelBlock==null){
            return;
        }
        currentModelBlock.to[batch] += delta;

        if(batch == 0){
            cubeSizeX.setText(""+ currentModelBlock.getSizeX());
        }else if(batch == 1){
            cubeSizeY.setText(""+ currentModelBlock.getSizeY());
        }else if(batch == 2){
            cubeSizeZ.setText(""+ currentModelBlock.getSizeZ());
        }

        viewUpdate();
    }

    public void changePos(int batch, int delta){
        if(currentModelBlock==null){
            return;
        }
        currentModelBlock.to[batch] += delta;
        currentModelBlock.from[batch] += delta;

        if(batch == 0){
            cubePositionX.setText(""+ currentModelBlock.from[batch]);
        }else if(batch == 1){
            cubePositionY.setText(""+ currentModelBlock.from[batch]);
        }else if(batch == 2){
            cubePositionZ.setText(""+ currentModelBlock.from[batch]);
        }

        viewUpdate();
    }

    @FXML private Parent containerModeling;
    @FXML private Parent containerGrouping;

    @FXML private Pane wrapperNorth;
    @FXML private Pane wrapperEast;
    @FXML private Pane wrapperSouth;
    @FXML private Pane wrapperWest;
    @FXML private Pane wrapperUp;
    @FXML private Pane wrapperDown;

    @FXML private Canvas canvasNorth;
    @FXML private Canvas canvasEast;
    @FXML private Canvas canvasSouth;
    @FXML private Canvas canvasWest;
    @FXML private Canvas canvasUp;
    @FXML private Canvas canvasDown;

    @FXML private Pane viewXY;
    @FXML private Pane viewYZ;
    @FXML private Pane viewXZ;
    @FXML private Pane viewMainPane;

    @FXML private ListView<DataModelBlock> cubeList;

    @FXML private TextField cubeSizeX;
    @FXML private TextField cubeSizeY;
    @FXML private TextField cubeSizeZ;
    @FXML private TextField cubePositionX;
    @FXML private TextField cubePositionY;
    @FXML private TextField cubePositionZ;

    @FXML private TextField cubeName;
    @FXML private TextField cubeGroup;

    @FXML private ChoiceBox<FACING>  sideMenu;
    @FXML private ChoiceBox<Texture> sideTexture;
    @FXML private ChoiceBox<Integer> sideTextureRotation;

    @FXML private TextField uvx0;
    @FXML private TextField uvx1;
    @FXML private TextField uvy0;
    @FXML private TextField uvy1;

    @FXML public void onAddSizeX(){changeSize(0, 1);}
    @FXML public void onAddSizeY(){changeSize(1, 1);}
    @FXML public void onAddSizeZ(){changeSize(2, 1);}

    @FXML public void onAddPosX(){changePos(0, 1);}
    @FXML public void onAddPosY(){changePos(1, 1);}
    @FXML public void onAddPosZ(){changePos(2, 1);}

    @FXML public void onRemSizeX(){changeSize(0, -1);}
    @FXML public void onRemSizeY(){changeSize(1, -1);}
    @FXML public void onRemSizeZ(){changeSize(2, -1);}

    @FXML public void onRemPosX(){changePos(0, -1);}
    @FXML public void onRemPosY(){changePos(1, -1);}
    @FXML public void onRemPosZ(){changePos(2, -1);}

    @FXML public void onCubeCopy(){
        if(currentModelBlock == null){
            return;
        }
        DataModelBlock copy = currentModelBlock.clone();
        cubeList.getItems().add(copy);
        currentModel.elements.add(copy);
        updateTexturePreview();
        viewRecreate();
    }
    @FXML public void onCubeRemove(){
        if(currentModelBlock == null){
            return;
        }
        currentModel.elements.remove(currentModelBlock);
        cubeList.getItems().remove(cubeList.getSelectionModel().getSelectedItem());
        cubeList.getSelectionModel().clearSelection();
        currentModelBlock = null;
        updateTexturePreview();
        viewRecreate();
    }
    @FXML public void onCubeAdd(){
        DataModelBlock block = new DataModelBlock();
        currentModel.elements.add(block);
        cubeList.getItems().add(block);
        viewRecreate();
    }

    @FXML public void onUVx0Add(){uvChange(0,  1);}
    @FXML public void onUVy0Add(){uvChange(1,  1);}
    @FXML public void onUVx1Add(){uvChange(2,  1);}
    @FXML public void onUVy1Add(){uvChange(3,  1);}

    @FXML public void onUVx0Rem(){uvChange(0, -1);}
    @FXML public void onUVy0Rem(){uvChange(1, -1);}
    @FXML public void onUVx1Rem(){uvChange(2, -1);}
    @FXML public void onUVy1Rem(){uvChange(3, -1);}

}
