package support;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import com.sun.jersey.core.util.Base64;

// metadata http://stackoverflow.com/questions/6495518/writing-image-metadata-in-java-preferably-png
// https://code.google.com/p/pngj/

public class CreateImage {	
	ImageOutputStream outstream;
	BufferedImage img;
	/**
	 * Reads an array with pixel values, then creates a png image from them.
	 * 
	 * @param width image width
	 * @param height image height
	 * @param inPixels input array with pixel values
	 * @param outfile output png image file
	 * @throws IOException
	 */
	public CreateImage(int width, int height, int[] inPixels, String outfile) throws IOException {
		int x, y; //for coordinates
		//int [][] data = new int[width][height]; //for pixel data		

		this.img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); //define image type and size
		//FileReader file = new FileReader(infile); //open file with data for reading
		//Scanner input = new Scanner(file); //create scanner object to read the file
		//ImageOutputStream outstream;

/*		for (y=0; y<height; y++){
			for (x=0; x<width; x++){
				data[x][y] = input.nextInt(); //read integers from file with default delimiters
				//img.setRGB(x, y, input.nextInt());
			}
		}*/
		for (y=0; y<height; y++){
			for (x=0; x<width; x++){
				this.img.setRGB(x, y, inPixels[y * width + x]); //write pixels to the image
			}
		}
		//input.close();
		//file.close();
		//File f = new File(outfile); //create path to image file
		//ImageIO.write(img, "PNG", f); //create the image file
		//outstream = null;
		//ImageIO.write(img, "PNG", outstream);
		System.out.println("File successfully created: " + outfile);
	}
	
	public BufferedImage getBufferedImage() {
		return this.img;
	}
	
	
	// Following methods based on: 
	// https://github.com/benbai123/JSP_Servlet_Practice/blob/master/Practice/JAVA/Commons/src/test/ImageUtils.java
	/**
	 * Decode string to image
	 * @param imageString The string to decode
	 * @return decoded image
	 */
	public static BufferedImage decodeToImage(String imageString) {
		BufferedImage image = null;
		byte[] imageByte;
		try {
			imageByte = Base64.decode(imageString);
			ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
			image = ImageIO.read(bis);
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}
	
	/**
	 * Encode image to string
	 * @param image The image to encode
	 * @param type png, jpeg, bmp, ...
	 * @return encoded string
	 */
	public static String encodeToString(BufferedImage image, String type) {
		String imageString = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, type, bos);
			byte[] imageBytes = bos.toByteArray();
			imageString = new String(Base64.encode(imageBytes));
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageString;
	}
}