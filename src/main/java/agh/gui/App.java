package agh.gui;
import agh.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class App extends Application implements ISymPosChangeObserver {
    private final int sceneWidth = 1200, sceneHeight = 800;
    public Scene scene;
    private AbstractWorldMap map1,map2;
    private GridPane gridStrict,gridBorderless,gridGenes,alertGrid;
    private BorderPane borderPane;
    private SimulationEngine engineStrict,engineBorderless;
    private final Graf grafStrict=new Graf("Strict"), grafBorderless=new Graf("Borderless");
    private int mapWidth, mapHeight, startEnergy, moveEnergy, jungleRatio, plantEnergy, dayStrict,dayBorderless,amountOfAnimal;
    private boolean magicFiveStrict=false, magicFiveBorderless=false, strictThreadStoped=false, borderlessThreadStoped2=false;
    Button submitBtn,magicBtnS,magicBtnB;
    TextField widthTxt, heightTxt, startEnergyTxt, moveEnergyTxt, jungleRatioTxt, plantEnergyTxt,amountOfAnimalTxt;
    Genes dominantGenStrict, dominantGenBorderless;
    SaveCSV saveCsvStrict=new SaveCSV("StrictStats"),saveCsvBorderless=new SaveCSV("BorderlesstStats");


    public void init() {
        //        -------formularz startowy--------
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        
        Text scenetitle = new Text("I HOPE U LIKE IT :)");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
        grid.add(scenetitle, 0, 0, 6, 1);

        Label amountOfAnimalL = new Label("startAnimals:");
        grid.add(amountOfAnimalL, 0, 1);

        amountOfAnimalTxt = new TextField("10");
        grid.add(amountOfAnimalTxt, 1, 1);

        Label widthL = new Label("Width:");
        grid.add(widthL, 0, 2);

        widthTxt = new TextField("9");
        grid.add(widthTxt, 1, 2);

        Label heightL = new Label("Height:");
        grid.add(heightL, 0, 3);

        heightTxt = new TextField("9");
        grid.add(heightTxt, 1, 3);

        Label startEnergyL = new Label("startEnergy:");
        grid.add(startEnergyL, 0, 4);

        startEnergyTxt = new TextField("9");
        grid.add(startEnergyTxt, 1, 4);

        Label moveEnergyL = new Label("moveEnergy:");
        grid.add(moveEnergyL, 0, 5);

        moveEnergyTxt = new TextField("1");
        grid.add(moveEnergyTxt, 1, 5);

        Label plantEnergyL = new Label("plantEnergy:");
        grid.add(plantEnergyL, 0, 6);

        plantEnergyTxt = new TextField("9");
        grid.add(plantEnergyTxt, 1, 6);

        Label jungleRatioL = new Label("jungleRatio:");
        grid.add(jungleRatioL, 0, 7);

        jungleRatioTxt = new TextField("2");
        grid.add(jungleRatioTxt, 1, 7);

        submitBtn = new Button("SUBMIT");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(submitBtn);
        grid.add(hbBtn, 1, 9);

        
//        ----------przyciski do magic5-------
        GridPane gridLeft = new GridPane();
        gridLeft.setAlignment(Pos.CENTER);
        magicBtnS = new Button("Magic OFF");
        HBox hbBtn1 = new HBox(20);
        hbBtn1.setAlignment(Pos.CENTER);
        hbBtn1.getChildren().add(magicBtnS);
        Label magicLeft = new Label("Active magicFive for Strict");
        gridLeft.add(hbBtn1, 0, 1);
        gridLeft.add(magicLeft, 0, 0);
        magicBtnS.setOnAction(e ->{ magicFiveStrict=true; magicBtnS.setText("Magic ON"); });


        GridPane gridRight = new GridPane();
        gridRight.setAlignment(Pos.CENTER);
        magicBtnB = new Button("Magic OFF");
        HBox hbBtn2 = new HBox(20);
        hbBtn2.setAlignment(Pos.CENTER);
        hbBtn2.getChildren().add(magicBtnB);
        Label magicRight = new Label("Active magicFive for Borderless");
        gridRight.add(hbBtn2, 0, 1);
        gridRight.add(magicRight, 0, 0);
        magicBtnB.setOnAction(e ->{ magicFiveBorderless=true; magicBtnB.setText("Magic ON"); });



//        -----ustawinie elem na planszy-------
        borderPane = new BorderPane();
        borderPane.setCenter(grid);
        borderPane.setLeft(gridLeft);
        borderPane.setRight(gridRight);
        BorderPane.setMargin(gridLeft,new Insets(0,0,0,100));
        BorderPane.setMargin(gridRight,new Insets(0,100,0,0));
        
        scene = new Scene(borderPane, sceneWidth, sceneHeight);
    }


    @Override
    public void start(Stage primaryStage) {
        submitBtn.setOnAction(event -> {
//            ------budowanie parametrow do symulacji-------
            mapWidth=Integer.parseInt(widthTxt.getText());
            mapHeight=Integer.parseInt(heightTxt.getText());
            startEnergy=Integer.parseInt(startEnergyTxt.getText());
            moveEnergy=Integer.parseInt(moveEnergyTxt.getText());
            jungleRatio=Integer.parseInt(jungleRatioTxt.getText());
            plantEnergy=Integer.parseInt(plantEnergyTxt.getText());
            amountOfAnimal=Integer.parseInt(amountOfAnimalTxt.getText());

            map1 = new StrictMap(mapWidth,mapHeight, jungleRatio);
            map2 = new FlexMap(mapWidth,mapHeight, jungleRatio);
            engineStrict = new SimulationEngine(map1, startEnergy, mapWidth, mapHeight ,moveEnergy, plantEnergy, amountOfAnimal, magicFiveStrict);
            engineBorderless = new SimulationEngine(map2, startEnergy, mapWidth, mapHeight ,moveEnergy, plantEnergy, amountOfAnimal, magicFiveBorderless);
            engineStrict.addMap(this);
            engineBorderless.addMap(this);

            Thread engineThread1 = new Thread(engineStrict);
            Thread engineThread2 = new Thread(engineBorderless);
            engineThread1.start();
            engineThread2.start();

            borderPane = new BorderPane();

            
//            ----------tworzenie plansz---------
            gridStrict = new GridPane();
            gridBorderless = new GridPane();
            for(int i=0; i<=mapWidth+1; i++){
                gridStrict.getColumnConstraints().add(new ColumnConstraints(20));
                gridBorderless.getColumnConstraints().add(new ColumnConstraints(20));}
            for(int i=0; i<=mapHeight+1; i++){
                gridStrict.getRowConstraints().add(new RowConstraints(20));
                gridBorderless.getRowConstraints().add(new RowConstraints(20));}

            drawGrid(gridStrict,map1);
            drawGrid(gridBorderless,map2);

            
//            --------------sekcja strict------------
            Button stopBtn1 = new Button("STOP Strict");
            stopBtn1.setOnAction(e -> {
                if(strictThreadStoped){ engineThread1.resume(); strictThreadStoped=false; stopBtn1.setText("STOP Strict"); }
                else{ engineThread1.suspend(); strictThreadStoped=true; stopBtn1.setText("START Strict"); }
            });
            HBox hbBtn1 = new HBox(10);
            hbBtn1.setAlignment(Pos.BOTTOM_CENTER);
            hbBtn1.getChildren().add(stopBtn1);
            GridPane gridStrict1 = new GridPane();
            gridStrict1.add(gridStrict,0,0);
            gridStrict1.add(hbBtn1,0,1);
            borderPane.setLeft(gridStrict1);
            BorderPane.setMargin(gridStrict1,new Insets(200,0,0,0));

            
//            -----------sekcja borderless--------
            Button stopBtn2 = new Button("STOP Borderless");
            stopBtn2.setOnAction(e -> {
                if(borderlessThreadStoped2){ engineThread2.resume(); borderlessThreadStoped2=false; stopBtn2.setText("STOP Strict"); }
                else{ engineThread2.suspend(); borderlessThreadStoped2=true; stopBtn2.setText("START Strict"); }
            });
            HBox hbBtn2 = new HBox(10);
            hbBtn2.setAlignment(Pos.BOTTOM_CENTER);
            hbBtn2.getChildren().add(stopBtn2);
            GridPane gridBorderless2 = new GridPane();
            gridBorderless2.add(gridBorderless,0,0);
            gridBorderless2.add(hbBtn2,0,1);
            borderPane.setRight(gridBorderless2);
            BorderPane.setMargin(gridBorderless2,new Insets(200,0,0,0));

            
//            ----------wykresy-------
            GridPane gridStats = new GridPane();
            gridStats.add(grafStrict.getLineChart(),0,0);
            gridStats.add(grafBorderless.getLineChart(),1,0);
            gridStats.setAlignment(Pos.BASELINE_CENTER);
            borderPane.setCenter(gridStats);
            BorderPane.setMargin(gridStats,new Insets(170,0,0,0));


            
//            -----------geny---------
            gridGenes = new GridPane();
            gridGenes.setAlignment(Pos.BASELINE_CENTER);
            drawGenes();
            borderPane.setTop(gridGenes);


//            ---------alert magic5-------
            alertGrid = new GridPane();
            alertGrid.setAlignment(Pos.CENTER);
            alertGrid.setHgap(50);
            borderPane.setBottom(alertGrid);


            scene = new Scene(borderPane, sceneWidth, sceneHeight);
            primaryStage.setScene(scene);
            primaryStage.show();

        });
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(e ->{
            saveCsvBorderless.addAvg();
            saveCsvStrict.addAvg();
            System.exit(0);
        });
    }
    
    private void drawGenes(){
        gridGenes.getChildren().clear();
        gridGenes.setHgap(30);
        dominantGenStrict = map1.genDomination();
        dominantGenBorderless = map2.genDomination();
        Label label1 = new Label("Dominant S "+dominantGenStrict.toString());
        GridPane.setHalignment(label1, HPos.CENTER);
        gridGenes.add(label1,0,0);
        Label label2 = new Label("Dominant B "+dominantGenBorderless.toString());
        GridPane.setHalignment(label2, HPos.CENTER);
        gridGenes.add(label2,1,0);
    }


    private void drawGrid(GridPane grid,AbstractWorldMap m){
        grid.getChildren().clear();
        grid.setGridLinesVisible(false);
        grid.setGridLinesVisible(true);

        grid.add(new Label("X/Y"), 0, 0);
        for(int i=1; i<=mapWidth+1; i++){
            int q=i-1;
            Label label = new Label(Integer.toString(q));
            GridPane.setHalignment(label, HPos.CENTER);
            grid.add(label, i, 0);
        }
        for(int j=0; j<=mapHeight; j++) {
            int q = mapHeight - j;
            Label label = new Label(Integer.toString(q));
            GridPane.setHalignment(label, HPos.CENTER);
            grid.add(label, 0, j+1);
        }

        for(int i=0; i<=mapWidth+1; i++) {
            for (int j = 0; j <= mapHeight + 1; j++) {
                Vector2d v=new Vector2d(i - 1, mapHeight - j + 1);
                if (m.isOccupied(v)) {
                    VBox vbox = new GuiElementBox((IMapElement) m.objectAt(v)).elementVisualization();
                    grid.add(vbox, i, j);
                }
            }
        }
    }

    @Override
    public void magicFiveAlert(AbstractWorldMap m){
        if(m.isStrict()){
            Platform.runLater( () -> drawAlert("Strict",0) );
        }
        else{
            Platform.runLater( () -> drawAlert("Borderless",1) );
        }
    }

    private void drawAlert(String s, int i){
        Label alert = new Label("MagicFive wystapilo juz 3 razy w "+s+"!!");
        alertGrid.add(alert,i,0);
    }


    @Override
    public void positionChanged(AbstractWorldMap map){
        if(map.isStrict()) {
            Platform.runLater( () -> {drawGrid(gridStrict,map);drawGenes();} );
            dayStrict++;
            int[] data= {dayStrict,map1.getAnimalsQuantity(),map1.getGrassQuantity(),map1.averageEnergy(),map1.averageLifeTime(), map1.averageChildren()};
            grafStrict.newAnimalData(dayStrict, data[1]);
            grafStrict.newGrassData(dayStrict, data[2]);
            grafStrict.newEnergyData(dayStrict, data[3]);
            grafStrict.newLiveTimeData(dayStrict, data[4]);
            grafStrict.newChildrenData(dayStrict, data[5]);
            saveCsvStrict.saveToCsv(data);
            dominantGenStrict = map1.genDomination();
        }
        else{
            Platform.runLater( () -> {drawGrid(gridBorderless,map);drawGenes();} );
            dayBorderless++;
            int[] data= {dayBorderless ,map2.getAnimalsQuantity(),map2.getGrassQuantity(),map2.averageEnergy(),map2.averageLifeTime(),map2.averageChildren()};
            grafBorderless.newAnimalData(dayBorderless, data[1]);
            grafBorderless.newGrassData(dayBorderless, data[2]);
            grafBorderless.newEnergyData(dayBorderless, data[3]);
            grafBorderless.newLiveTimeData(dayBorderless, data[4]);
            grafBorderless.newChildrenData(dayBorderless, data[5]);
            saveCsvBorderless.saveToCsv(data);
            dominantGenBorderless = map2.genDomination();
        }
    }

}