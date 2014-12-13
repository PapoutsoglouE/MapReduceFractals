package support;

/**
 * Small library made to support complex number operations <br>
 * for fractal calculations.
 */
public class MyComplex {
	
	double real; // real part
	double imag; // imaginary part
	
	/**
	 * Creates a new complex number Z = 0 + 0i
	 */
	public MyComplex()
    {
		this.real = 0;
		this.imag = 0;
    }
	
	/**
	 * Creates a new complex number Z = x + yi
	 * 
	 * @param x real part
	 * @param y imaginary part
	 */
	public MyComplex(double x, double y)
    {
		this.real = x;
		this.imag = y;
    }
	
	/**
	 * @return real part of a complex number
	 */
	public double getReal() {
		return this.real;
	}
	
	/**
	 * @return imaginary part of a complex number
	 */
	public double getImag() {
		return this.imag;
	}
	
	/**
	 * Sets the real part of a complex number to x.
	 * 
	 * @param x real part to be set
	 */
	public void setReal(double x) {
		this.real = x;
	}
	
	/**
	 * Sets the imaginary part of a complex number to y.
	 * 
	 * @param y imaginary part to be set
	 */
	public void setImag(double y) {
		this.imag = y;
	}
	
	/**
	 * Adds two complex numbers and returns their result.
	 * Neither of the original numbers are changed.
	 * 
	 * @param z1 augend
	 * @param z2 addend
	 * @return sum: z1 + z2
	 */
	public MyComplex add(MyComplex z1, MyComplex z2) {
		this.real = z1.getReal() + z2.getReal();
		this.imag = z1.getImag() + z2.getImag();
		return this;
	}
	
	/**
	 * Multiplies two complex numbers and returns their result.
	 * Neither of the original numbers are changed.
	 * 
	 * @param z1 multiplicand
	 * @param z2 multiplier
	 * @return product: z1 * z2
	 */
	public MyComplex multiply(MyComplex z1, MyComplex z2) {
		this.real = z1.getReal() * z2.getReal() - z1.getImag() * z2.getImag();
		this.imag = z1.getReal() * z2.getImag() + z1.getImag() * z2.getReal();
		return this;
	}
	
	/**
	 * Returns the square of a complex number, leaving the original unchanged.
	 * 
	 * @return z^2
	 */
	public MyComplex square() {
		MyComplex temp = new MyComplex();
		temp.setReal(this.real * this.real - this.imag * this.imag);
		temp.setImag(2 * this.real * this.imag);
		return temp;	
	}
	
	/**
	 * @return absolute value of a complex number.
	 */
	public double abs() {
		return Math.sqrt(this.real * this.real + this.imag * this.imag);
	}
	
	/**
	 * Checks if a complex number is in or on a circle <br>
	 * centered at the origin, with radius R.
	 * 
	 * @param R radius of circle
	 * @return true if in or on the circle, false otherwise
	 */
	public boolean inCircle(double R) {
		double absSquared = this.real * this.real + this.imag * this.imag;
		if (absSquared <= R * R)
			return true;
		else
			return false;	
	}

}