import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import fr.inria.papart.procam.*; 
import fr.inria.papart.procam.display.*; 
import fr.inria.papart.procam.camera.*; 
import fr.inria.papart.drawingapp.*; 
import org.bytedeco.javacpp.*; 
import toxi.geom.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class manualLocationCam extends PApplet {








// Undecorated frame 
public void init() {
  frame.removeNotify(); 
  frame.setUndecorated(true); 
  frame.addNotify(); 
  super.init();
}

Papart papart;
ARDisplay cameraDisplay;
Camera camera;

float objectWidth = 420;
float objectHeight = 297;

float rectAroundWidth = 10;

public void setup() {

  Papart.seeThrough(this);

  papart =  Papart.getPapart();
  cameraDisplay = papart.getARDisplay();
  cameraDisplay.manualMode();

  camera = papart.getCameraTracking();
  
  object = new PVector[4];
  image = new PVector[4]; 

  // 10 cm. 
  object[0] = new PVector(0, 0, 0);
  object[1] = new PVector(objectWidth, 0, 0);
  object[2] = new PVector(objectWidth, objectHeight, 0);
  object[3] = new PVector(0, objectHeight, 0);

  image[0] = new PVector(100, 100);
  image[1] = new PVector(200, 100);
  image[2] = new PVector(200, 200);
  image[3] = new PVector(100, 200);
}

PMatrix3D mat, pos;
PVector object[];
PVector image[];

public void draw() {

    PImage img = camera.getImage();
    if(img == null)
	return;

    background(0);
    image(img, 0, 0, width, height);
    
  ProjectiveDeviceP pdp = cameraDisplay.getProjectiveDeviceP();
  mat = pdp.estimateOrientation(object, image);
  //    mat.print();

  PGraphicsOpenGL g1 = cameraDisplay.beginDraw();  

  g1.clear();


  g1.modelview.apply(mat);

  g1.fill(50, 50, 200, 100);
  // g1.translate(-10, -10, 0);
  g1.rect(-rectAroundWidth,
	  -rectAroundWidth,
	  objectWidth + rectAroundWidth*2,
	  objectHeight + rectAroundWidth*2);


  
  g1.translate(objectWidth + 100, objectHeight + 100, 0);
  // 5cm rect 
  g1.fill(0, 191, 100, 100);
  g1.rect(150, 80, 100, 100);

  cameraDisplay.endDraw();

  DrawUtils.drawImage((PGraphicsOpenGL) g, 
		      cameraDisplay.render(), 
		      0, 0, width, height);


  fill(255, 100);
  quad(image[0].x, image[0].y, 
       image[1].x, image[1].y, 
       image[2].x, image[2].y, 
       image[3].x, image[3].y);


  
  if (test) { 
    mat.print();
    test = false;
  }
}

public void mouseDragged() {

  image[currentPt] = new PVector(mouseX, mouseY);
}


boolean test = true;
int currentPt = 0;
boolean set = false;

public void keyPressed() {

  if (key == '0')
    currentPt = 0;

  if (key == '1')
    currentPt = 1;

  if (key == '2')
    currentPt = 2;

  if (key == '3')
    currentPt = 3;

  if (key == 't')
    test = !test;

  if (key == 's') {
      papart.saveCalibration("marker.xml", mat);
      println("Saved");
  }
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "manualLocationCam" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
