import java.awt.Color;
import java.util.Arrays;
import java.util.Scanner;

public class SeamCarver {
	private Picture picture;
	public SeamCarver(Picture pic) {
		this.picture = pic;
	}
	public int width() {
		return picture.width();
	}
	public int height() {
		return picture.height();
	}
	/**
	 * Calculates the energy of a pixel using the dual-gradient energy function
	 * The energy of pixel (x, y) is Δx^2(x, y) + Δy^2(x, y), where the square of the 
	 * x-gradient Δx^2(x, y) = Rx(x, y)^2 + Gx(x, y)^2 + Bx(x, y)^2, and where the central 
	 * differences Rx(x, y), Gx(x, y), and Bx(x, y) are the absolute value in differences 
	 * of red, green, and blue components between pixel (x + 1, y) and pixel (x − 1, y). 
	 * The square of the y-gradient Δy^2(x, y) is defined in an analogous manner. To handle
	 * pixels on the borders of the image, calculate energy by defining the leftmost and 
	 * rightmost columns as adjacent and the topmost and bottommost rows as adjacent. For 
	 * example, to compute the energy of a pixel (0, y) in the leftmost column, we use its
	 * right neighbor (1, y) and its left neighbor (W − 1, y).
	 * @return
	 */
	public int energy(int x, int y) {
		if (x >= width() || x < 0 || y >= height() || y < 0) {
			throw new IndexOutOfBoundsException("Arguments not in range. x: " + x + " y: " + y + " width: " + width() + " height: " + height());
		}
		Color top = picture.get(x, (y-1 + height()) % height()); // use mod to loop around if we are on a top or bottom edge
		Color bottom = picture.get(x, (y+1) % height());
		Color left = picture.get((x-1 + width()) % width(), y); // use mod to loop around the edges
		Color right = picture.get((x+1) % width(), y);
		int Rx = Math.abs(left.getRed() - right.getRed());
		int Gx = Math.abs(left.getGreen() - right.getGreen());
		int Bx = Math.abs(left.getBlue() - right.getBlue());
		int Ry = Math.abs(top.getRed() - bottom.getRed());
		int Gy = Math.abs(top.getGreen() - bottom.getGreen());
		int By = Math.abs(top.getBlue() - bottom.getBlue());
		return Rx*Rx + Gx*Gx + Bx*Bx + Ry*Ry + Gy*Gy + By*By;
	}
	public int[] findHorizontalSeam() {
		// use dynamic programming to calculate the path of least energy horizontally through the picture
		long[][] dynamic = new long[picture.width()][picture.height()];
		// set everything in the first column to its starting energy
		for (int r = 0; r < picture.height(); r++) {
			dynamic[0][r] = this.energy(0, r);
		}
		int leastEnergyRow = -1; // the row with the least energy sum in the last col. Used to start backtracking later
		for (int c = 1; c < picture.width(); c++) {
			for (int r = 0; r < picture.height(); r++) {
				long minEnergy = dynamic[c-1][r];
				if (r != 0 && dynamic[c-1][r-1] < minEnergy) { minEnergy = dynamic[c-1][r-1]; }
				if (r != picture.height() - 1 && dynamic[c-1][r+1] < minEnergy) { minEnergy = dynamic[c-1][r+1]; }
				dynamic[c][r] = minEnergy + energy(c, r);
				if (leastEnergyRow == -1 || (c == picture.width()-1 && dynamic[c][r] < dynamic[c][leastEnergyRow])) {
					leastEnergyRow = r;
				}
			}
		}
		// backtrack through the generated array to find the path of least energy, starting with index leastEnergyRow
		int[] horizontalSeam = new int[picture.width()];
		horizontalSeam[picture.width()-1] = leastEnergyRow;
		int prevRow = leastEnergyRow;
		for (int c = picture.width()-2; c >= 0; c--) {
			int minEnergyRow = prevRow; // the row with least energy in the col that is at most a distance of 2 to curRow
			if (prevRow != 0 && dynamic[c][minEnergyRow] > dynamic[c][prevRow-1]) { minEnergyRow = prevRow-1; }
			if (prevRow != picture.height()-1 && dynamic[c][minEnergyRow] > dynamic[c][prevRow+1]) { minEnergyRow = prevRow+1; } 
			horizontalSeam[c] = minEnergyRow;
			prevRow = minEnergyRow;
		}
		return horizontalSeam;
	}
	public int[] findVerticalSeam() {
		// use dynamic programming to calculate the path of least energy vertically through the picture
		long[][] dynamic = new long[picture.width()][picture.height()];
		for (int c = 0; c < picture.width(); c++) {
			dynamic[c][0] = this.energy(c, 0);
		}
		int leastEnergyCol = -1; // the col with the least energy sum in the last row. Used to start backtracking later
		for (int r = 1; r < picture.height(); r++) {
			for (int c = 0; c < picture.width(); c++) {
				long minEnergy = dynamic[c][r-1];
				if (c != 0 && dynamic[c-1][r-1] < minEnergy) { minEnergy = dynamic[c-1][r-1]; }
				if (c != picture.width() - 1 && dynamic[c+1][r-1] < minEnergy) { minEnergy = dynamic[c+1][r-1]; }
				dynamic[c][r] = minEnergy + energy(c, r);
				if (r == picture.height()-1 && (leastEnergyCol == -1 || (dynamic[c][r] < dynamic[leastEnergyCol][r]))) {
					leastEnergyCol = c;
				}
			}
		}
		// backtrack through the generated array to find the path of least energy, starting with index leastEnergyRow
		int[] verticalSeam = new int[picture.height()];
		verticalSeam[picture.height()-1] = leastEnergyCol;
		int prevCol = leastEnergyCol;
		for (int r = picture.height()-2; r >= 0; r--) {
			int minEnergyCol = prevCol; // the row with least energy in the col that is at most a distance of 2 to curRow
			if (prevCol != 0 && dynamic[prevCol-1][r] < dynamic[minEnergyCol][r]) { minEnergyCol = prevCol-1; }
			if (prevCol != picture.width()-1 && dynamic[prevCol+1][r] < dynamic[minEnergyCol][r]) { minEnergyCol = prevCol+1; } 
			verticalSeam[r] = minEnergyCol;
			prevCol = minEnergyCol;
		}
		return verticalSeam;
	}
	public void removeHorizontalSeam(int[] seam) {
		if (picture.height() == 1) {
			throw new IllegalArgumentException("Can't remove horizontal seam if height of picture is 1 because height cannot become <= 0");
		}
		Picture newPic = new Picture(picture.width(), picture.height()-1);
		for (int c = 0; c < seam.length; c++) { // go through all the columns, removing the one pixel
			int newR = 0;
			int prevR = 0;
			while (newR < picture.height()-1) {
				if (seam[c] != prevR) { // if we do not remove this pixel, add it to the new picture
					newPic.set(c, newR, picture.get(c, prevR));
					newR++;
				}
				prevR++;
			}
		}
		this.picture = newPic;
	}
	public void removeVerticalSeam(int[] seam) {
		if (picture.width() == 1) {
			throw new IllegalArgumentException("Can't remove vertical seam if width of picture is 1 because width cannot become <= 0");
		}
		Picture newPic = new Picture(picture.width()-1, picture.height());
		for (int r = 0; r < seam.length; r++) {
			int newC = 0;
			int prevC = 0;
			while (newC < picture.width()-1) {
				if (seam[r] != prevC) {
					newPic.set(newC, r, picture.get(prevC, r));
					newC++;
				}
				prevC++;
			}
		}
		this.picture = newPic;
	}
	public void savePicture(String fileName) {
		picture.save(fileName);
	}
	
}
