package support;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.xuggle.*;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

// Download Xuggle from: 
// http://xuggle.googlecode.com/svn/trunk/repo/share/java/xuggle/xuggle-xuggler/5.4/xuggle-xuggler-5.4.jar
/**
 * This class contains methods for frame-to-video conversions.
 */
public class FramesToVideo {
	private static final double FRAME_RATE = 8;
	private static final int SECONDS_TO_RUN_FOR = 1;
	private static final String outputFilename = "test.mp4";
	private static Dimension frameDimension = new Dimension(800,600);
	File file;
	
	
	/**
	 * TODO: this
	 * @param frames a list with all frames to compose the final video
	 */
    public FramesToVideo(ArrayList<String> frames) {
    	int i;
    	Collections.sort(frames);

    	
    	// TODO: this
    	// based on http://examples.javacodegeeks.com/desktop-java/xuggler/create-video-from-image-frames-with-xuggler/
    	// let's make a IMediaWriter to write the file.
    	final IMediaWriter writer = ToolFactory.makeWriter(outputFilename);
    	
		// We tell it we're going to add one video stream, with id 0,
		// at position 0, and that it will have a fixed frame rate of FRAME_RATE.
		
    	// for x264 see http://www.acnenomor.com/347418p2/can-xuggler-play-video-from-an-array-of-bufferedimages
    	//writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264,
    	writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4,
				frameDimension.width, frameDimension.height);
		long startTime = System.nanoTime();
		
		for (i = 0; i < frames.size(); i++) {
			System.out.println("Starting with " + frames.get(i) + "...");
			// take the image
            BufferedImage img = null;
            try {
                img = ImageIO.read(new File(frames.get(i)));
    			// convert to the right image type
                BufferedImage image = ConverterFactory.convertToType(img, BufferedImage.TYPE_3BYTE_BGR);
                IConverter converter = ConverterFactory.createConverter(img, IPixelFormat.Type.YUV420P);
                IVideoPicture frame = converter.toPicture(image, (System.nanoTime() - startTime)/ 1000);
                //frame.setKeyFrame(i == 0);
                frame.setQuality(0);
                
                // this line errors after one frame for h264
                writer.encodeVideo(0, frame);
                
               
    			//BufferedImage bgrScreen = ConverterFactory.convertToType(img, BufferedImage.TYPE_INT_RGB);
    			// encode the image to stream #0
    			//writer.encodeVideo(0, bgrScreen, System.nanoTime() - startTime,TimeUnit.NANOSECONDS);
    			
    			
    			try {
    				// sleep for frame rate milliseconds
        			Thread.sleep((long) (1000 / FRAME_RATE));
    			}
    			catch (InterruptedException e) {
    				// ignore
    				System.out.println("exception while sleeping");
    			}
                
            } catch (IOException e) {
            	System.out.println("exception in FramesToVideo image IO");
            }
            writer.flush();
            System.out.println("Done with " + frames.get(i));

		}

		// tell the writer to close and write the trailer if needed
		writer.close();
		
    	// print out arraylist elements
    	/*for (i = 0; i < frames.size(); i++) {
    		file = new File(frames.get(i));
      	    System.out.println("Image file " + frames.get(i) + " deleted status: " + file.delete());
    	} */
		
    }

    
}