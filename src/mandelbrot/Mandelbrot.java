package mandelbrot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import support.MyComplex;


// Based on code found at http://rosettacode.org/wiki/Mandelbrot_set#Java

public class Mandelbrot {

	/**
	 * Creates a Mandelbrot fractal with the specified parameters, <br>
	 * then writes the pixel values to a file.
	 * 
	 * @param width the width of the image to be generated
	 * @param height the height of the image to be generated
	 * @param zoomIn zoom factor, try 500s range
	 * @param max_iter iterations over which the function values will be tested
	 * @param horCenter horizontal center of the image
	 * @param verCenter vertical center of the image
	 * @param colorShift adjusts color range for fractal
	 * @throws IOException for pixel file creation
	 */
	public Mandelbrot(int width, int height, double zoomIn, int max_iter, int horCenter, int verCenter, int colorShift, String output) throws IOException {
		/* 
		  default values: width = 800, height = 600, zoomIn = 150, max_iter = 570
						   horCenter = 400, verCenter = 300
		 */
		
		// Calculated pixel values will be written to out.txt.
		File pixel_values = new File(output); //Create path to output file.
		FileOutputStream fos = new FileOutputStream(pixel_values); //Create FileOutputStream object in order to enable writing to the file.
		OutputStreamWriter osw = new OutputStreamWriter(fos); //Create OutputStreamWriter object which will conduct the writing to the file.
		double tmp; //Temporary variable used for storing the real part of Znew

		for (int y = 0; y < height; y++) {  
			for (int x = 0; x < width; x++) { //For every pixel in the image iterate.
				MyComplex z = new MyComplex();
				MyComplex c = new MyComplex();
				c.setReal((x - horCenter) / zoomIn); //Adjust horizontal window position.
				c.setImag((y - verCenter) / zoomIn); //Adjust vertical window position.
				int iter = max_iter;
				// iterate over formula: Znew = Zold^2 + C
				while (z.inCircle(2) && iter > 0) {  
					tmp = z.square().getReal() + c.getReal();
					z.setImag(z.square().getImag() + c.getImag()); //Calculate new pixel.
					z.setReal(tmp);
					iter--;	
				}
				osw.write((iter | (iter << colorShift))  + "\n"); // write pixel value to file
			}
		}
		osw.close();
	}

	/**
	 * Creates a Mandelbrot fractal, then writes it out as a png image.
	 */
	/*
	public static void main(String[] args) throws IOException {
		// fractal variables, see Mandelbrot class for details
		int width  = 800;    // default: 800
		int height = 600;    // default: 600
		int zoom   = 300;    // default: 150
		int iter   = 570;    // default: 570
		int horCenter = 600; // default: 400 or width/2
		int verCenter = 300; // default: 300 or height/2
		int colShift  = 13;  // default: 8
		String pixelOutput = "out.txt"; // default: output.txt
		
		new Mandelbrot(width, height, zoom, iter, horCenter, verCenter, colShift, pixelOutput);
		new CreateImage(width, height, "out.txt", "output.png");
	}
	*/

}