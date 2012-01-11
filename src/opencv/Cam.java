package opencv;

import java.text.DecimalFormat;

import app.Configuration;
import app.Controller;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

public class Cam {
	private Controller 		controller;
	private Configuration 	configuration;
	private int 			xPx, yPx, thPx, minPx;
	
	public Cam(Controller controller) {
		this.controller 	= controller;
		this.configuration 	= this.controller.getConfiguration();
		
		this.yPx 	= this.configuration.getYres();
		this.xPx 	= this.yPx * 4 / 3;
		this.thPx	= this.xPx * this.configuration.getBorder() / 200;
		this.minPx 	= this.xPx * this.configuration.getMin() / 100;
		
		this.controller.getGui().printConsole("Auflösung (px): " + this.xPx + "x" + this.yPx);
		this.controller.getGui().printConsole("Minimal Objektgröße (px): " + this.minPx);
		this.controller.getGui().printConsole("Threshold (px): " + this.thPx + " und " + (this.xPx - this.thPx));	
	}
	
    public void run() throws Exception {
        String classifierName = "src/resources/opencv/haarcascade_upperbody.xml";

        // Preload the opencv_objdetect module to work around a known bug.
        Loader.load(opencv_objdetect.class);

        // We can "cast" Pointer objects by instantiating a new object of the desired class.
        CvHaarClassifierCascade classifier = new CvHaarClassifierCascade(cvLoad(classifierName));
        if (classifier.isNull()) {
            System.err.println("Error loading classifier file \"" + classifierName + "\".");
            System.exit(1);
        }

        // CanvasFrame is a JFrame containing a Canvas component, which is hardware accelerated.
        // It can also switch into full-screen mode when called with a screenNumber.
        CanvasFrame frame = new CanvasFrame("Kamera Feed");
        frame.setCanvasSize(160, 120);
        
        frame.addKeyListener(this.controller.getGui().getKL());

        // OpenCVFrameGrabber uses opencv_highgui, but other more versatile FrameGrabbers
        // include DC1394FrameGrabber, FlyCaptureFrameGrabber, OpenKinectFrameGrabber,
        // VideoInputFrameGrabber, and FFmpegFrameGrabber.
        FrameGrabber grabber = new OpenCVFrameGrabber(this.configuration.getDeviceId());
        grabber.setImageHeight(this.yPx);
        grabber.setImageWidth(this.xPx);
        grabber.start();

        // FAQ about IplImage:
        // - For custom raw processing of data, getByteBuffer() returns an NIO direct
        //   buffer wrapped around the memory pointed by imageData.
        // - To get a BufferedImage from an IplImage, you may call getBufferedImage().
        // - The createFrom() factory method can construct an IplImage from a BufferedImage.
        // - There are also a few copy*() methods for BufferedImage<->IplImage data transfers.
        IplImage 	grabbedImage 	= grabber.grab();
        int 		width  			= grabbedImage.width();
        int 		height 			= grabbedImage.height();
        IplImage 	grayImage    	= IplImage.create(width, height, IPL_DEPTH_8U, 1);

        // Objects allocated with a create*() or clone() factory method are automatically released
        // by the garbage collector, but may still be explicitly released by calling release().
        CvMemStorage storage = CvMemStorage.create();

        while (frame.isVisible() && (grabbedImage = grabber.grab()) != null) {
            // Let's try to detect some faces! but we need a grayscale image...
            cvCvtColor(grabbedImage, grayImage, CV_BGR2GRAY);
            CvSeq 	faces 	= cvHaarDetectObjects(grayImage, classifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
            int 	total 	= faces.total();
            CvRect rFin 	= null;
            
            for (int i = 0; i < total; i++) {
                CvRect r = new CvRect(cvGetSeqElem(faces, i));
               
        		if (
        			(r.width() > this.minPx)
        			&& (
        				(rFin == null)
        				|| (r.width() > rFin.width())
        			)
        		) {
        			rFin = r;
        		}
            }
            
            if (rFin != null) {
                int x = rFin.x(), y = rFin.y(), w = rFin.width(), h = rFin.height();
                
                int cX = x + w/2;
                int cY = y + h/2;
                
                float wf = (float) w;
                float hf = (float) h;
                double dia = Math.sqrt((wf * wf) + (hf * hf));

                double dist = this.log(0.45, dia - 42) + 6.6;
                
                DecimalFormat df = new DecimalFormat("###.##");
                
            	this.controller.getGui().printConsole("Körper gefunden: " + ( cX ) + ":" + ( cY ) + " " + df.format(dist) + "m");
                cvRectangle(grabbedImage, cvPoint(x, y), cvPoint(x+w, y+h), CvScalar.RED, 1, CV_AA, 0);
                
                if (cX < this.thPx) {
                	this.controller.getGui().printConsole("Nach links: " + (this.thPx - cX) * 100 / this.thPx + "%");
                	this.controller.getSerial().send(3);
                } else if (cX > (this.xPx - this.thPx)) {
                	this.controller.getGui().printConsole("Nach rechts: " + ((cX - (this.xPx - this.thPx)) * 100 / this.thPx) + "%"); 
                	this.controller.getSerial().send(4);
                }
            }
            
            cvLine(grabbedImage, cvPoint(this.thPx, 0), cvPoint(this.thPx, this.yPx), CvScalar.GREEN, 1, CV_AA, 0);
            cvLine(grabbedImage, cvPoint(this.xPx - this.thPx, 0), cvPoint(this.xPx - this.thPx, this.yPx), CvScalar.GREEN, 1, CV_AA, 0);
            
            frame.showImage(grabbedImage);
            cvClearMemStorage(storage);
        }
        grabber.stop();
        frame.dispose();
    }
    
    private double log(double base, double x)
    {
    	// Math.log is base e, natural log, ln
    	return Math.log( x ) / Math.log( base );
    }
}