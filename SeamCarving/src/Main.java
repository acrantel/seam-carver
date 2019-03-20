import java.util.Arrays;
import java.util.Scanner;

public class Main {
	public static boolean debug = false;
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		while (true) {
			SeamCarver sc = null;
			// load original picture
			while (true) {
				System.out.print("Load picture: ");
				String path = in.nextLine();
				try {
					sc = new SeamCarver(new Picture(path)); 
					break;
				} catch (Exception e) {
					System.out.println(e.getMessage());
					System.out.println("Unable to load picture, try again");
				}
			}
			// get new width/height of picture
			System.out.println("Current size of picture(WxH): " + sc.width() + "x" + sc.height());
			System.out.print("Enter new width of picture: ");
			int width = readInt(in);// no error checking here add later
			System.out.print("Enter new height of picture: ");
			int height = readInt(in);
			
			while (sc.width() > width) {
				int[] vert = sc.findVerticalSeam();
				sc.removeVerticalSeam(vert);
				if (debug) { System.out.println(Arrays.toString(vert)); }
			}
			while (sc.height() > height) {
				int[] horiz = sc.findHorizontalSeam();
				sc.removeHorizontalSeam(horiz);
				if (debug) { System.out.println(Arrays.toString(horiz)); }
			}
			System.out.print("Enter file name to save as: ");
			sc.savePicture(in.nextLine());
			System.out.print("Done? (y/n): ");
			String input = in.nextLine();
			if (input.toLowerCase().equals("yes") || input.toLowerCase().equals("y")) {
				break;
			}
		}
		System.out.println("Done");
	}
	public static int readInt(Scanner in) {
		int x = -1;
		while (true) {
			try {
				x = Integer.parseInt(in.nextLine());
				break;
			} catch (Exception e) {}
		}
		return x;
	}
}
