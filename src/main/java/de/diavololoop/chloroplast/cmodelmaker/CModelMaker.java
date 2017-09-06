package de.diavololoop.chloroplast.cmodelmaker;

import de.diavololoop.chloroplast.cmodelmaker.model.DataModel;
import de.diavololoop.chloroplast.cmodelmaker.model.DataModelBlock;
import de.diavololoop.chloroplast.cmodelmaker.model.DataModelFace;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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


    public enum FACING {NORTH, EAST, SOUTH, WEST, UP, DOWN};

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

    Map <FACING, Pane> wrapperMap  = new HashMap<FACING, Pane>();
    Map <FACING, Canvas> canvasMap = new HashMap<FACING, Canvas>();
    Map <FACING, Color> faceColor   = new HashMap<FACING, Color>();

    DataModelBlock currentModelBlock;
    DataModel      currentModel;

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

    }

    private void linkFields(){

        for(FACING face: FACING.values()){
            Canvas canvas = canvasMap.get(face);
            Pane   pane   = wrapperMap.get(face);

            canvas.widthProperty().bind(pane.widthProperty());
            canvas.heightProperty().bind(pane.heightProperty());

            pane.minWidthProperty().bind(root.heightProperty().divide(6));
            pane.prefWidthProperty().bind(root.heightProperty().divide(6));

            //pane.setBackground(new Background(new BackgroundFill(Color.color(Math.random(), Math.random(), Math.random()), null, null)));
        }

        viewXY.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT)));
        viewYZ.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT)));
        viewXZ.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT)));

    }

    private void registerListeners(){

        cubeList.getSelectionModel()
                .selectedItemProperty().addListener((observable, o, t1) -> {

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

        sideMenu.getSelectionModel().selectedItemProperty().addListener((observable, o, t1) -> {
            updateBlockInfo();
            updateTexturePreview();
        });
    }

    private void rewriteBlockBounds() {
        if(currentModelBlock == null){
            return;
        }

        if(!cubePositionX.getText().matches("\\d\\d")){ cubePositionX.setText("0"); }
        if(!cubePositionY.getText().matches("\\d\\d")){ cubePositionY.setText("0"); }
        if(!cubePositionZ.getText().matches("\\d\\d")){ cubePositionZ.setText("0"); }
        if(!cubeSizeX.getText().matches("\\d\\d")){ cubeSizeX.setText("0"); }
        if(!cubeSizeY.getText().matches("\\d\\d")){ cubeSizeY.setText("0"); }
        if(!cubeSizeZ.getText().matches("\\d\\d")){ cubeSizeZ.setText("0"); }

        currentModelBlock.from[0] = Integer.parseInt(cubePositionX.getText());
        currentModelBlock.from[1] = Integer.parseInt(cubePositionY.getText());
        currentModelBlock.from[2] = Integer.parseInt(cubePositionZ.getText());

        currentModelBlock.to[0] = currentModelBlock.from[0] + Integer.parseInt(cubeSizeX.getText());
        currentModelBlock.to[1] = currentModelBlock.from[1] + Integer.parseInt(cubeSizeY.getText());
        currentModelBlock.to[2] = currentModelBlock.from[2] + Integer.parseInt(cubeSizeZ.getText());
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

        sideTexture.getSelectionModel().selectFirst();
        sideTexture.getSelectionModel().select(Texture.get(face.texture));

        uvx0.setText(""+face.uv[0]);
        uvy0.setText(""+face.uv[1]);
        uvx1.setText(""+face.uv[2]);
        uvy1.setText(""+face.uv[3]);

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

                    double x0 = mface.uv[0]/16;
                    double y0 = mface.uv[1]/16;
                    double x1 = mface.uv[2]/16;
                    double y1 = mface.uv[3]/16;

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
                    context.strokeRect(x0*canvas.getWidth()-1.5, y0*canvas.getHeight()-1.5, (x1-x0)*canvas.getHeight()+3, (y1-y0)*canvas.getWidth()+3);


                }


            }
        }
    }
    private void save(File file) {
        if(file == null){
            return;
        }

        try {
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


        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }

    }
    /*public void setTexturePreview(DataModelBlock block){
        //if(true)return;

        System.out.println("Coloring");

        Map<FACING, GraphicsContext> context = new HashMap<FACING, GraphicsContext>();

        texturePreview.forEach((face, canvas) -> context.put(face, canvas.getGraphicsContext2D()));

        if(block == null){

            texturePreview.forEach((face, canvas) -> {
                GraphicsContext c = context.get(face);
                c.setFill(faceColor.get(face));
                c.fillRect(0,0, canvas.getWidth(), canvas.getHeight());

                c.setStroke(Color.BLACK);
                c.strokeText(face.toString().substring(0, 1).toUpperCase(), 5, 20);
            });

        }
    }*/

    @FXML Pane wrapperNorth;
    @FXML Pane wrapperEast;
    @FXML Pane wrapperSouth;
    @FXML Pane wrapperWest;
    @FXML Pane wrapperUp;
    @FXML Pane wrapperDown;

    @FXML Canvas canvasNorth;
    @FXML Canvas canvasEast;
    @FXML Canvas canvasSouth;
    @FXML Canvas canvasWest;
    @FXML Canvas canvasUp;
    @FXML Canvas canvasDown;

    @FXML Pane viewXY;
    @FXML Pane viewYZ;
    @FXML Pane viewXZ;

    @FXML ListView<DataModelBlock> cubeList;

    @FXML TextField cubeSizeX;
    @FXML TextField cubeSizeY;
    @FXML TextField cubeSizeZ;
    @FXML TextField cubePositionX;
    @FXML TextField cubePositionY;
    @FXML TextField cubePositionZ;

    @FXML TextField cubeName;
    @FXML TextField cubeGroup;

    @FXML ChoiceBox<FACING> sideMenu;
    @FXML ChoiceBox<Texture> sideTexture;
    @FXML ChoiceBox<Integer> sideTextureRotation;

    @FXML TextField uvx0;
    @FXML TextField uvx1;
    @FXML TextField uvy0;
    @FXML TextField uvy1;

    @FXML public void onAddSizeX(){root.layout();}
    @FXML public void onAddSizeY(){}
    @FXML public void onAddSizeZ(){}

    @FXML public void onAddPosX(){}
    @FXML public void onAddPosY(){}
    @FXML public void onAddPosZ(){}

    @FXML public void onRemSizeX(){}
    @FXML public void onRemSizeY(){}
    @FXML public void onRemSizeZ(){}

    @FXML public void onRemPosX(){}
    @FXML public void onRemPosY(){}
    @FXML public void onRemPosZ(){}

    @FXML public void onCubeCopy(){}
    @FXML public void onCubeRemove(){}
    @FXML public void onCubeAdd(){}

    @FXML public void onUVx0Add(){}
    @FXML public void onUVx1Add(){}
    @FXML public void onUVy0Add(){}
    @FXML public void onUVy1Add(){}

    @FXML public void onUVx0Rem(){}
    @FXML public void onUVx1Rem(){}
    @FXML public void onUVy0Rem(){}
    @FXML public void onUVy1Rem(){}
}
