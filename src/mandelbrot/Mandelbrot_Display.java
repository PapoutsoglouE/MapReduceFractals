package mandelbrot;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import javax.swing.JFrame;
import support.MyComplex;
import support.CreateImage;

// Based on code found at http://rosettacode.org/wiki/Mandelbrot_set#Java

@SuppressWarnings("serial")
public class Mandelbrot_Display extends JFrame {
	private BufferedImage I;
	


	/**
	 * Creates a Mandelbrot fractal with the specified parameters, <br>
	 * then writes the pixel values to a file and displays it.
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
	public Mandelbrot_Display(int width, int height, double zoomIn, int max_iter, int horCenter, int verCenter, int colorShift) throws IOException {
		/* 
		  default values: width = 800, height = 600, zoomIn = 150, max_iter = 570
						   horCenter = 400, verCenter = 300, colorShift = 8
		 */
		
		// Calculated pixel values will be written to out.txt.
		File pixel_values = new File("out.txt");
		FileOutputStream fos = new FileOutputStream(pixel_values);
		OutputStreamWriter osw = new OutputStreamWriter(fos);
		double tmp;

		setBounds(100, 100, width, height); // window position horizontally, window position vertically, width, height 
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		I = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < height; y++) {  
			for (int x = 0; x < width; x++) { 
				MyComplex z = new MyComplex();
				MyComplex c = new MyComplex();
				c.setReal((x - horCenter) / zoomIn);
				c.setImag((y - verCenter) / zoomIn);
				int iter = max_iter;
				// iterate over formula: Znew = Zold^2 + C
				while (z.inCircle(2) && iter > 0) { 
					tmp = z.square().getReal() + c.getReal();
					z.setImag(z.square().getImag() + c.getImag());
					z.setReal(tmp);
					iter--;	
				}
				
				I.setRGB(x, y, iter | (iter << colorShift));
				osw.write((iter | (iter << colorShift))  + "\n"); // write pixel value to file
			}
		}
		osw.close();
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(I, 0, 0, this);
	}

	/**
	 * Creates a Mandelbrot fractal, then writes it out as a png image.
	 */
	public static void main(String[] args) throws IOException {
		// fractal variables, see Mandelbrot class for details
		int width  = 800;    // default: 800
		int height = 600;    // default: 600
		int zoom   = 300;    // default: 150
		int iter   = 570;    // default: 570
		int horCenter = 600; // default: 400 or width/2
		int verCenter = 300; // default: 300 or height/2
		int colShift  = 13;  // default: 8
		
		new Mandelbrot_Display(width, height, zoom, iter, horCenter, verCenter, colShift).setVisible(true);
		new CreateImage(width, height, "out.txt", "output.png");
	}

}