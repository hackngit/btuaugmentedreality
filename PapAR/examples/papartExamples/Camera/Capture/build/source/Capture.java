import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import fr.inria.papart.procam.*; 
import fr.inria.papart.procam.camera.*; 
import fr.inria.papart.procam.display.*; 
import org.bytedeco.javacpp.*; 
import org.reflections.*; 
import TUIO.*; 
import toxi.geom.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Capture extends PApplet {

// PapARt library








Papart papart;
MarkerBoard markerBoard;
PVector boardSize = new PVector(297, 210);  //  21 * 29.7 cm
TrackedView boardView;

Camera cameraTracking;

public void settings(){
    size((int) (boardSize.x * 2) , (int) (boardSize.y * 2), P3D);
}


public void setup(){

    papart = new Papart(this);

    papart.initCamera();
    
    BaseDisplay display = papart.getDisplay();

    // The drawing is not automatic. 
    display.manualMode();

    cameraTracking = papart.getCameraTracking();

    MarkerBoard markerBoard = new MarkerBoard
	(Papart.markerFolder + "A3-small1.cfg", (int) boardSize.x, (int) boardSize.y);

					      
    // Ask the camera to track this markerboard
    cameraTracking.trackMarkerBoard(markerBoard);

    // Filtering on the tracking
    markerBoard.setDrawingMode(cameraTracking, true, 10);
    markerBoard.setFiltering(cameraTracking, 30, 4);

    // Create a view of part of the tracked piece of paper. 
    // The resolution (two last arguments) should be at maximum the camera resolution.
    boardView = new TrackedView(markerBoard);
    boardView.setImageWidthPx(256);
    boardView.setImageHeightPx(256);
    boardView.init();

    // Start tracking the pieces of paper. 
    cameraTracking.trackSheets(true);
}


PImage out = null;

public void draw(){

    background(0);
    out = boardView.getViewOf(cameraTracking);

    if(out != null){
	image(out, 0, 0, width, height);
    }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Capture" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
