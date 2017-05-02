import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.*;
/**
 * Program used the help of www.codejava.com
 */
public class ImagePipeline {
/**
* Resizes picture to width and height
* @throws IOException
*/

	// Constants 
	final int FINAL_WIDTH = 128;
	final int FINAL_HEIGHT= 128;
	final int NUM_PHOTOS = 2;
	final int MAX_REMCOL = 200;
	final int MAX_REMROW = 300;
	final int LABEL_SIZE = 1;

	// Methods to extract the colors from a pixel
	public byte getBlue(int pixel)  { return (byte) ((pixel) & 0xff) ;}
    public byte getRed(int pixel)   { return (byte) ((pixel >> 16) & 0xff) ;}
    public byte getGreen(int pixel) { return (byte) ((pixel >> 8) & 0xff) ;}

	public ImagePipeline(String fname) {

		// read in file
        File inputFile = new File(fname);
        BufferedImage inputImage;
        try {
            inputImage = ImageIO.read(inputFile);
        }
        catch(IOException e){
            return;
        }

        // take in the width and height 
		int pWidth = inputImage.getWidth();
		int pHeight = inputImage.getHeight();

		// run through and perform seam carve
		for (int num = 1; num <= NUM_PHOTOS; num++) {
			int remCol = (int) (Math.random() * MAX_REMCOL);
			int remRow = (int) (Math.random() * MAX_REMROW);

			SeamCarver2 sc = new SeamCarver2(inputImage, remRow, remCol);
			BufferedImage alteredImage = sc.picture();
			BufferedImage resizeImageJpg = alteredImage;
			try{
				// Resize the image 
				resizeImageJpg = PictureResizer.resizeImage(alteredImage, FINAL_WIDTH, FINAL_HEIGHT);
			}
			catch(IOException e){
				System.out.println(e.getMessage());
			}
			if(resizeImageJpg != null) {
				String fileN = fname.split("\\.")[0];
				String outFile = fileN +  "_" + num + "_0.jpg"; 
				//OutputStream outputStream = new FileOutputStream(outFile);

				try {
				    // retrieve image
					File outputfile = new File("Images/" + outFile);
					ImageIO.write(resizeImageJpg, "jpg", outputfile);
				} catch (IOException e) {
					System.out.println("ERROR");
					return;    				
				}

				BufferedImage flippedUp = new BufferedImage(FINAL_WIDTH, FINAL_HEIGHT, resizeImageJpg.getType()); 
				for(int i = 0; i < FINAL_WIDTH; i++) {
					for(int j = 0; j < FINAL_HEIGHT; j++) {
						flippedUp.setRGB(i,j, resizeImageJpg.getRGB(i, FINAL_HEIGHT-j-1));
					}
				}
				try {
				    // retrieve image
				    outFile = fileN +  "_" + num + "_1.jpg"; 
					File outputfile = new File("Images/" + outFile);
					ImageIO.write(flippedUp, "jpg", outputfile);
				} catch (IOException e) {
					System.out.println("ERROR");
					return;    				
				}

				BufferedImage flippedSide = new BufferedImage(FINAL_WIDTH, FINAL_HEIGHT, resizeImageJpg.getType()); 
				for(int i = 0; i < FINAL_WIDTH; i++) {
					for(int j = 0; j < FINAL_HEIGHT; j++) {
						flippedSide.setRGB(i,j, resizeImageJpg.getRGB(FINAL_HEIGHT-i-1, j));
					}
				}
				try {
				    // retrieve image
				    outFile = fileN +  "_" + num + "_2.jpg"; 
					File outputfile = new File("Images/" + outFile);
					ImageIO.write(flippedSide, "jpg", outputfile);
				} catch (IOException e) {
					System.out.println("ERROR");
					return;    				
				}

				BufferedImage flippedBoth = new BufferedImage(FINAL_WIDTH, FINAL_HEIGHT, resizeImageJpg.getType()); 
				for(int i = 0; i < FINAL_WIDTH; i++) {
					for(int j = 0; j < FINAL_HEIGHT; j++) {
						flippedBoth.setRGB(i,j, flippedUp.getRGB(FINAL_HEIGHT-i-1, j));
					}
				}
				try {
				    // retrieve image
				    outFile = fileN +  "_" + num + "_3.jpg"; 
					File outputfile = new File("Images/" + outFile);
					ImageIO.write(flippedBoth, "jpg", outputfile);
				} catch (IOException e) {
					System.out.println("ERROR");
					return;    				
				}

				// Rotate the image 
				for (int i = 0; i < FINAL_HEIGHT / 2; i++) {
					for (int j = 0; j < Math.ceil(((double) FINAL_HEIGHT) / 2.); j++) {
						int temp = resizeImageJpg.getRGB(i,j);
						resizeImageJpg.setRGB(i,j, resizeImageJpg.getRGB(FINAL_HEIGHT-1-j, i));
						resizeImageJpg.setRGB(FINAL_HEIGHT-1-j,i, resizeImageJpg.getRGB(FINAL_HEIGHT-1-i, FINAL_HEIGHT-1-j));
						resizeImageJpg.setRGB(FINAL_HEIGHT-1-i, FINAL_HEIGHT-1-j, resizeImageJpg.getRGB(j, FINAL_HEIGHT-1-i)); 
						resizeImageJpg.setRGB(j, FINAL_HEIGHT-1-i, temp);
					}
				}
				try {
				    // retrieve image
				    outFile = fileN +  "_" + num + "_4.jpg"; 
					File outputfile = new File("Images/" + outFile);
					ImageIO.write(resizeImageJpg, "jpg", outputfile);
				} catch (IOException e) {
					System.out.println("ERROR");
					return;    				
				}

				// Rotate the image 
				for (int i = 0; i < FINAL_HEIGHT / 2; i++) {
					for (int j = 0; j < Math.ceil(((double) FINAL_HEIGHT) / 2.); j++) {
						int temp = resizeImageJpg.getRGB(i,j);
						resizeImageJpg.setRGB(i,j, resizeImageJpg.getRGB(FINAL_HEIGHT-1-j, i));
						resizeImageJpg.setRGB(FINAL_HEIGHT-1-j,i, resizeImageJpg.getRGB(FINAL_HEIGHT-1-i, FINAL_HEIGHT-1-j));
						resizeImageJpg.setRGB(FINAL_HEIGHT-1-i, FINAL_HEIGHT-1-j, resizeImageJpg.getRGB(j, FINAL_HEIGHT-1-i)); 
						resizeImageJpg.setRGB(j, FINAL_HEIGHT-1-i, temp);
					}
				}
				try {
				    // retrieve image
				    outFile = fileN +  "_" + num + "_5.jpg"; 
					File outputfile = new File("Images/" + outFile);
					ImageIO.write(resizeImageJpg, "jpg", outputfile);
				} catch (IOException e) {
					System.out.println("ERROR");
					return;    				
				}
			}
		
		}
	}

	public static void main(String[] args) {
		ImagePipeline ipl = new ImagePipeline(args[0]);
	}
}
