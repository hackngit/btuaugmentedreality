/* 
 *  Copyright Inria and Bordeaux University.
 *  Author Jeremy Laviole. jeremy.laviole@inria.fr
 *  PapAR project is the open-source version of the
 *  PapARt project. License is LGPLv3, distributed with the sources.
 *  This project can also distributed with standard commercial
 *  licence for closed-sources projects.
 */
package fr.inria.papart.procam;

import processing.core.PApplet;
import processing.core.PFont;
import processing.opengl.PGraphicsOpenGL;
import processing.core.PImage;

import processing.opengl.*;
import processing.core.PConstants;
import static processing.core.PConstants.QUADS;

/**
 *
 * @author jeremylaviole
 */
public class DrawUtils implements PConstants{

    public static PApplet applet;

    static public void drawImage(PGraphics3D pg3d, PImage img, int x, int y, int w, int h) {
        pg3d.pushMatrix();
        pg3d.translate(x, y);
        pg3d.scale(-1, 1, 1);
        pg3d.rotate(PApplet.PI);
        pg3d.image(img, 0, 0, w, h);
        pg3d.popMatrix();
    }
    
    static public void drawImage2(PGraphicsOpenGL g, PImage img, int x, int y, int w, int h) {
        g.pushMatrix();
        g.translate(x, y);
        g.scale(-1, 1, 1);
        g.rotate(PApplet.PI);
        g.image(img, 0, 0, w, h);
        g.popMatrix();
    }

    static public void drawImage(PGraphicsOpenGL g, PImage img, int x, int y, int w, int h) {
//        g.pushMatrix();
//        g.translate(x, y);
//        g.scale(-1, 1, 1);
//        g.rotate(PApplet.PI);
//        g.image(img, 0, 0, w, h);
//        g.popMatrix();

//        g.beginShape(QUADS);
//        g.texture(img);
//        g.vertex(x, y, 0, h);
//        g.vertex(x, y + h, 0, 0);
//        g.vertex(x + w, y + h, w, 0);
//        g.vertex(x + w, y, w, h);
//        g.endShape();

        g.pushMatrix();
        g.translate(x, y);
        g.beginShape(QUADS);
        g.textureMode(NORMAL);
        g.texture(img);
        g.vertex(0, 0, 0, 1);
        g.vertex(0, h, 0, 0);
        g.vertex(w, h, 1, 0);
        g.vertex(w, 0, 1, 1);
        g.endShape();
        
        g.popMatrix();

    }
    
//    static public void drawImage(PGraphicsOpenGL g, Texture tex, int x, int y, int w, int h) {
//
//        PGL pgl = g.beginPGL();
//        
//        pgl.drawTexture(PGL.TEXTURE_2D, tex.glName, 
//                        w, h, 
//                        x, y, 
//                        x + w, x + h);
//        g.endPGL();
//        
//    }

//    static public void drawImage(GLGraphicsOffScreen pg3d, PImage img, int x, int y, int w, int h) {
//        pg3d.pushMatrix();
//        pg3d.translate(x, y);
//        pg3d.scale(-1, 1, 1);
//        pg3d.rotate(PApplet.PI);
//        pg3d.image(img, 0, 0, w, h);
//        pg3d.popMatrix();
//    }
    static public void drawText(PGraphicsOpenGL pg3d, String text, PFont font, int x, int y) {
        
        pg3d.pushMatrix();
        pg3d.translate(x, y);
        pg3d.text(text, 0, 0);
        pg3d.popMatrix();
        
//        pg3d.pushMatrix();
//        pg3d.translate(x, y);
//        pg3d.scale(-1, 1, 1);
//        pg3d.rotate(PApplet.PI);
//        pg3d.textMode(PApplet.MODEL);
//        pg3d.textFont(font);
//        pg3d.text(text, 0, 0);
//        pg3d.popMatrix();
    }

    static public void drawText(PGraphicsOpenGL pg3d, String text, PFont font, int fontSize, int x, int y) {
        pg3d.pushMatrix();
        pg3d.translate(x, y);
        pg3d.textMode(PApplet.MODEL);
        pg3d.textFont(font, fontSize);
        pg3d.text(text, 0, 0);
        pg3d.popMatrix();

        
//        pg3d.pushMatrix();
//        pg3d.translate(x, y);
//        pg3d.scale(-1, 1, 1);
//        pg3d.rotate(PApplet.PI);
//        pg3d.textMode(PApplet.MODEL);
//        pg3d.textFont(font, fontSize);
//        pg3d.text(text, 0, 0);
//        pg3d.popMatrix();
    }

    static public void drawText(PGraphicsOpenGL g, String text, PFont font, int fontSize, int x, int y, int w, int h) {
        g.textFont(font, fontSize);
        g.text(text, x, y, w, h);
    }
}
