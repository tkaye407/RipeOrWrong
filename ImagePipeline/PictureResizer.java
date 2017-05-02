import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
/**
 * Program used the help of www.codejava.com
 */
public class PictureResizer {
/**
* Resizes picture to width and height
* @throws IOException
*/
	public static BufferedImage resizeImage(BufferedImage inputImage, int scaledWidth, int scaledHeight) throws IOException {
		// Create the new output image 
		BufferedImage resizedImage = new BufferedImage(scaledWidth, scaledHeight, inputImage.getType());
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
		g.dispose();
		g.setComposite(AlphaComposite.Src);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

		// returns resized image 
 		return resizedImage;
 	}
	public static void main(String[] args) {
		int newWidth = Integer.parseInt(args[2]);
		int newHeight = Integer.parseInt(args[3]);
		String[] fileN = args[0].split("\\.");
		String name = fileN[0] + newWidth + "x" + newHeight + ".jpg";  

		try{
			File inputFile = new File(args[0]);
			BufferedImage inputImage = ImageIO.read(inputFile);
			BufferedImage resizeImageJpg = resizeImage(inputImage, newWidth, newHeight);
			File dir = new File("images");
			dir.mkdirs();
			File tmp = new File(dir, args[1]);
			tmp.createNewFile();
			ImageIO.write(resizeImageJpg, "jpg", tmp);
		}
		catch(IOException e){
			System.out.println(e.getMessage());
		}
	}
}