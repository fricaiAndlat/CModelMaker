package de.diavololoop.chloroplast.cmodelmaker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.diavololoop.chloroplast.cmodelmaker.model.Project;
import de.diavololoop.chloroplast.cmodelmaker.model.basic.DataModel;
import de.diavololoop.chloroplast.cmodelmaker.model.basic.DataModelFace;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by Chloroplast on 10.09.2017.
 */
public class IOHandler {

    private File lastDirectory;
    private Stage stage;

    private GsonBuilder gsonBuilder;

    public IOHandler(Stage stage){
        this.stage = stage;

        gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
    }



    public void importModel(Project current, Consumer<Project> consumer){
        FileChooser fc = new FileChooser();
        fc.setTitle("Import Model");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Model files (*.json)", "*.json");
        fc.setSelectedExtensionFilter(extFilter);

        if(lastDirectory != null){
            fc.setInitialDirectory(lastDirectory);
        }

        File file = fc.showOpenDialog(stage);

        if(file == null){
            return;
        }

        lastDirectory = file.getParentFile();

        try{

            Reader reader = new FileReader(file);

            Gson gson = gsonBuilder.create();

            DataModel model = gson.fromJson(reader, DataModel.class);

            Set<String> knownNames = new HashSet<>();
            model.elements.forEach(dmBlock -> {

                if(knownNames.contains(dmBlock.name)){
                    dmBlock.name = ""+System.nanoTime();
                }

                knownNames.add(dmBlock.name);

                for(CModelMaker.FACING face: CModelMaker.FACING.values()){
                    if(!dmBlock.faces.containsKey(face.toString().toLowerCase())){
                        dmBlock.faces.put(face.toString().toLowerCase(), new DataModelFace());
                    }
                }

            });

            current.minecraftModel = model;

            consumer.accept(current);

        }catch (Exception e){
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Something went wrong while loading the file");
            alert.setHeaderText("Something went wrong while loading the file");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void importImage(Project project, Consumer<Texture> consumer) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Import Image");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Images (*.png)", "*.png");
        fc.setSelectedExtensionFilter(extFilter);

        if(lastDirectory != null){
            fc.setInitialDirectory(lastDirectory);
        }

        File file = fc.showOpenDialog(stage);

        if(file == null){
            return;
        }

        lastDirectory = file.getParentFile();

        try{

            consumer.accept(new Texture(file));

        }catch (Exception e){
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Something went wrong while loading the file");
            alert.setHeaderText("Something went wrong while loading the file");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }


    }

    public void loadProject(Consumer<Project> main) {

        FileChooser fc = new FileChooser();
        fc.setTitle("Load Project");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Project Files (*.project.json)", "*.project.json");
        fc.setSelectedExtensionFilter(extFilter);

        if(lastDirectory != null){
            fc.setInitialDirectory(lastDirectory);
        }

        File file = fc.showOpenDialog(stage);

        if(file == null){
            return;
        }

        lastDirectory = file.getParentFile();

        try{

            Reader reader = new FileReader(file);

            Gson gson = gsonBuilder.create();

            Project project = gson.fromJson(reader, Project.class);

            Set<String> knownNames = new HashSet<>();
            project.minecraftModel.elements.forEach(dmBlock -> {

                System.out.println("loaded element: "+dmBlock.name);

                if(knownNames.contains(dmBlock.name)){
                    dmBlock.name = ""+System.nanoTime();
                }

                knownNames.add(dmBlock.name);

                for(CModelMaker.FACING face: CModelMaker.FACING.values()){
                    if(!dmBlock.faces.containsKey(face.toString().toLowerCase())){
                        dmBlock.faces.put(face.toString().toLowerCase(), new DataModelFace());
                    }
                }

            });

            project.textures.load();

            main.accept(project);

        }catch (Exception e){
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Something went wrong while loading the file");
            alert.setHeaderText("Something went wrong while loading the file");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }


    }

    public void saveProject(Project project) {

        FileChooser fc = new FileChooser();
        fc.setTitle("Save Project");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Project Files (*.project.json)", "*.project.json");
        fc.setSelectedExtensionFilter(extFilter);

        if(lastDirectory != null){
            fc.setInitialDirectory(lastDirectory);
        }

        File file = fc.showSaveDialog(stage);

        if(file == null){
            return;
        }

        lastDirectory = file.getParentFile();

        project.textures.save();
        Texture.IMAGES.forEach( (regName, texture) -> project.minecraftModel.textures.put(regName, texture.getName()));

        try{

            FileWriter writer = new FileWriter(file);
            Gson gson = gsonBuilder.create();

            gson.toJson(project, writer);

            writer.flush();
            writer.close();

        }catch (Exception e){
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Something went wrong while loading the file");
            alert.setHeaderText("Something went wrong while loading the file");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void exportModel(Project project) {
    }

    public void exportImages(Project project) {
    }



    public void promptExit() {
    }
}
