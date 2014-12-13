package support;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

// metadata http://stackoverflow.com/questions/6495518/writing-image-metadata-in-java-preferably-png
// https://code.google.com/p/pngj/

public class CreateImage {	

	/**
	 * Reads a file with pixel values (one RGB value on each line), then <br>
	 * creates a png image from them.
	 * 
	 * @param width image width
	 * @param height image height
	 * @param infile input file with pixel values
	 * @param outfile output png image file
	 * @throws IOException
	 */
	public CreateImage(int width, int height, String infile, String outfile) throws IOException {
		int x, y; //for coordinates
		int [][] data = new int[width][height]; //for pixel data		

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); //define image type and size
		FileReader file = new FileReader(infile); //open file with data for reading
		Scanner input = new Scanner(file); //create scanner object to read the file

		for (y=0; y<height; y++){
			for (x=0; x<width; x++){
				data[x][y] = input.nextInt(); //read integers from file with default delimiters
				//img.setRGB(x, y, input.nextInt());
			}
		}
		for (x=0; x<width; x++){
			for (y=0; y<height; y++){
				img.setRGB(x, y, data[x][y]); //write pixels to the image
			}
		}
		input.close();
		file.close();
		File f = new File(outfile); //create path to image file
		ImageIO.write(img, "PNG", f); //create the image file
	}
}