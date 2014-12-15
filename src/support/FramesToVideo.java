package support;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import javax.imageio.ImageIO;
import com.xuggle.xuggler.Configuration;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

// Download Xuggle from: 
// http://xuggle.googlecode.com/svn/trunk/repo/share/java/xuggle/xuggle-xuggler/5.4/xuggle-xuggler-5.4.jar
// documentation at: http://www.xuggle.com/public/documentation/java/api/

// code based on
// https://github.com/niavok/ps2yt/blob/master/src/com/niavok/VideoEncoder.java

/**
 * This class contains methods for frame-to-video conversions.
 */
public class FramesToVideo {
	private static final String outputFilename = "test.mp4";
	private static Dimension frameDimension = new Dimension(800,600);
	private IPacket packet;
	private IRational frameRate;
	private IContainer container;
	private IStreamCoder videoStreamCoder;
	private int numberOfFrames;
	private int writeFrameCount;
	private int generatedFrameCount;
	private long positionInMicroseconds;
	private BufferedImage image;


	/**
	 * Take an ArrayList of frame filenames, then sort the files by <br>
	 * filename and make a video with them.
	 * @param frames	a list with all frames to compose the final video
	 */
	public FramesToVideo(ArrayList<String> frames) {
		int i;
		Collections.sort(frames);
		numberOfFrames = frames.size();

		// open a container
		container = IContainer.make();
		container.open(outputFilename, IContainer.Type.WRITE, null);
		initVideoStream();

		// write the header
		container.writeHeader();
		positionInMicroseconds = 0;

		generatedFrameCount = 0;
		writeFrameCount = 0;
		packet = IPacket.make();

		// take it or leave it, same thing either way
		Configuration.configure("libx264-lossless_ultrafast.ffpreset", videoStreamCoder);
		
		// loop through all frames and write them to stream
		for (i = 0; i < numberOfFrames; i++) {
			try {
			image = ImageIO.read(new File(frames.get(i)));
			} catch (IOException e){
				System.out.println("Error on image read: image(" + i + ")");
			}
			image = convert(image, BufferedImage.TYPE_3BYTE_BGR);
			writeFrame(i);
		}

		close();
		
		// delete png files
		/*File file;
		for (i = 0; i < numberOfFrames; i++) {
		file = new File(frames.get(i));
		System.out.println("Image file " + frames.get(i) + " deleted status: " + file.delete());
		}*/
	}

	/**
	 * Write a frame to the video stream.
	 * @param frameId number of frame being added, for logging purposes only
	 */
	private void writeFrame(int frameId) {
		System.out.println("writeFrame " + frameId);
		BufferedImage outputImage = image;
		IConverter converter = ConverterFactory.createConverter(outputImage,
				videoStreamCoder.getPixelType());
		IVideoPicture frame = converter.toPicture(outputImage, positionInMicroseconds);
		frame.setQuality(0); // max quality: lossless
		if (videoStreamCoder.encodeVideo(packet, frame, 0) < 0) {
			throw new RuntimeException("Unable to encode video.");
		}
		if (packet.isComplete()) {
			System.out.println("write video packet");
			if (container.writePacket(packet,true) < 0) {
				writeFrameCount++;
				throw new RuntimeException("Could not write packet to container.");
			}
		}
		positionInMicroseconds += (1/frameRate.getDouble() * Math.pow(1000, 2));
		generatedFrameCount++;
	}
	
	/**
	 * Convert a BufferedImage to the right colorspace.
	 * @param value the image
	 * @param type the color type to convert to
	 * @return a bufferedimage with the desired colorspace
	 */
	private static BufferedImage convert(BufferedImage value, int type) {
		if (value.getType() == type)
			return value;
		BufferedImage result = new BufferedImage(value.getWidth(), value.getHeight(),
				type);
		result.getGraphics().drawImage(value, 0, 0, null);
		return result;
	}

	/**
	 * Close video stream and container.
	 */
	public void close(){
		System.out.println("Closing stream.");
		System.out.println("generatedFrameCount = " + generatedFrameCount);
		System.out.println("writeFrameCount = " + writeFrameCount);
		while(writeFrameCount < generatedFrameCount) {
			int encodeVideoResult = videoStreamCoder.encodeVideo(packet, null, 0);
			// System.out.println("encodeVideoResult="+ encodeVideoResult);
			if (encodeVideoResult >= 0) {
				if (packet.isComplete()) {
					writeFrameCount++;
					// System.out.println("write flush packet "+ writeFrameCount);
					if (container.writePacket(packet, true) < 0) {
						throw new RuntimeException("Could not write packet to container.");
					}
				} else {
					break;
				}
			} else {
				break;
			}
		}
		/*while(true) { // is this only for audio?
			int encodeVideoResult = audioStreamCoder.encodeVideo(packet, null, 0);
			// System.out.println("encodeVideoResult="+ encodeVideoResult);
			if (encodeVideoResult >= 0) {
				if (packet.isComplete()) {
					writeFrameCount++;
					// System.out.println("write flush packet "+ writeFrameCount);
					if (container.writePacket(packet,true) < 0) {
						throw new RuntimeException("Could not write packet to container.");
					}
				} else {
					break;
				}
			} else {
				break;
			}
		}*/
		container.writeTrailer();
		container.close();
	}


	/**
	 * Initialise the video stream and all of its parameters.
	 * Size, codec and framerate are specified here.
	 */
	private void initVideoStream() {
		ICodec videoCodec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_H264);
		IStream videoStream = container.addNewStream(videoCodec);
		videoStreamCoder = videoStream.getStreamCoder();
		frameRate = IRational.make(24, 1); // 15 fps
		videoStreamCoder.setWidth(frameDimension.width);
		videoStreamCoder.setHeight(frameDimension.height);
		videoStreamCoder.setFrameRate(frameRate);
		videoStreamCoder.setTimeBase(IRational.make(frameRate.getDenominator(),
				frameRate.getNumerator()));
		//videoStreamCoder.setBitRate(3500000);
		videoStreamCoder.setNumPicturesInGroupOfPictures(numberOfFrames);
		videoStreamCoder.setPixelType(IPixelFormat.Type.YUV420P);
		videoStreamCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, true);
		videoStreamCoder.setGlobalQuality(0);
		videoStreamCoder.open(null, null);
	}

}