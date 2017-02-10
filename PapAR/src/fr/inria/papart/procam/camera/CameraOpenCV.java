/* 
 *  Copyright Inria and Bordeaux University.
 *  Author Jeremy Laviole. jeremy.laviole@inria.fr
 *  PapAR project is the open-source version of the
 *  PapARt project. License is LGPLv3, distributed with the sources.
 *  This project can also distributed with standard commercial
 *  licence for closed-sources projects.
 */
package fr.inria.papart.procam.camera;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import processing.core.PImage;

/**
 *
 * @author jiii
 */
public class CameraOpenCV extends Camera {

    private OpenCVFrameGrabber grabber;
    private final OpenCVFrameConverter.ToIplImage converter;

    protected CameraOpenCV(int cameraNo) {
        this.systemNumber = cameraNo;
        this.setPixelFormat(PixelFormat.BGR);
        converter = new OpenCVFrameConverter.ToIplImage();
    }

    @Override
    public void start() {
        OpenCVFrameGrabber grabberCV = new OpenCVFrameGrabber(this.systemNumber);
        grabberCV.setImageWidth(width());
        grabberCV.setImageHeight(height());
        grabberCV.setImageMode(FrameGrabber.ImageMode.COLOR);
 
        try {
            grabberCV.start();
            this.grabber = grabberCV;
            this.isConnected = true;
        } catch (Exception e) {
            System.err.println("Could not start frameGrabber... " + e);

            System.err.println("Could not camera start frameGrabber... " + e);
            System.err.println("Camera ID " + this.systemNumber + " could not start.");
            System.err.println("Check cable connection, ID and resolution asked.");

            this.grabber = null;
        }
    }

    @Override
    public void grab() {

        if (this.isClosing()) {
            return;
        }
        try {
            IplImage img = converter.convertToIplImage(grabber.grab());
            if (img != null) {
                this.updateCurrentImage(img);
            }
        } catch (Exception e) {
            System.err.println("Camera: OpenCV Grab() Error ! " + e);
        }
    }

    @Override
    public PImage getPImage() {

        if (currentImage != null) {
            this.checkCamImage();
            camImage.update(currentImage);
            return camImage;
        }
        // TODO: exceptions !!!
        return null;
    }

    @Override
    public void close() {
        this.setClosing();
        if (grabber != null) {
            try {
                grabber.stop();
                System.out.println("Stopping grabber");
               
            } catch (Exception e) {
                System.out.println("Impossible to close " + e);
            }
        }
    }

}
