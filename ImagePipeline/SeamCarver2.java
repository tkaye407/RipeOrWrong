/******************************************************************************
 *  Name:    Tyler Kaye
 *  Ripe Or Wrong: ImagePipeline
 ******************************************************************************/
// imports
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SeamCarver2 {
    private BufferedImage pic;  

    public int getBlue(int pixel) { return (pixel) & 0xff; }
    public int getRed(int pixel) { return (pixel >> 16) & 0xff; }
    public int getGreen(int pixel) { return (pixel >> 8) & 0xff; }
    
    // create a seam carver object based on the given picture
    public SeamCarver2(BufferedImage image) {
        if (image == null) 
            throw new NullPointerException("Parameter cannot be null");
        
        // create defensive copy 
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());        
        for (int j = 0; j < image.getHeight(); j++){
            for (int i = 0; i < image.getWidth(); i++) {
                newImage.setRGB(i, j, image.getRGB(i, j));   
            }
        }
        pic = newImage;
    }

    // current picture
    public BufferedImage picture() {
        // return new picture
        BufferedImage newImage = new BufferedImage(pic.getWidth(), pic.getHeight(), pic.getType());        
        for (int j = 0; j < pic.getHeight(); j++){
            for (int i = 0; i < pic.getWidth(); i++) {
                newImage.setRGB(i, j, pic.getRGB(i, j));   
            }
        }
        return newImage;
    }

    // width of current picture
    public int width()   { return pic.getWidth();  }
    // height of current picture
    public int height()  { return pic.getHeight(); }

    // energy of pixel at column x and row y
    public double energy(int x, int y) 
    {
        // check for null pointers 
        if (x < 0 || x >= this.width())
            throw new IndexOutOfBoundsException("Index must be within width range");
        if (y < 0 || y >= this.height())
            throw new IndexOutOfBoundsException("Index must be within height range");
        
        // calculate the energy of pixel
        
        double xSQ;
        if (pic.getWidth() < 2) xSQ = 0;
        else {
            int c1, c2;
            if (x == 0) {
                c1 = pic.getRGB(x+1, y);
                c2 = pic.getRGB(pic.getWidth()-1, y);
            }
            else if (x == pic.getWidth() - 1) {
                c1 = pic.getRGB(0, y);
                c2 = pic.getRGB(x - 1, y);
            }
            else {
                c1 = pic.getRGB(x-1, y);
                c2 = pic.getRGB(x+1, y);
            }
            xSQ = colorToDouble(c1, c2);
        }
        
        double ySQ;
        
        if (pic.getHeight() < 2) ySQ = 0;
        
        else 
        {
            int c1, c2;
            if (y == 0) 
            {
                c1 = pic.getRGB(x, y+1);
                c2 = pic.getRGB(x, pic.getHeight() - 1);
            }
            else if (y == pic.getHeight() - 1)
            {
                c1 = pic.getRGB(x, 0);
                c2 = pic.getRGB(x, y - 1);
            }
            else 
            {
                c1 = pic.getRGB(x, y + 1);
                c2 = pic.getRGB(x, y - 1);
            } 
            ySQ = colorToDouble(c1, c2);
        }
        
        //System.out.println( Math.sqrt(xSQ + ySQ) );
        return Math.sqrt(xSQ + ySQ);
    }
    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() 
    {
        // invert the picture
        invertPic();
        // find the path using findVerticalSeam()
        int[] path = findVerticalSeam();
        // invert back 
        invertPic();
        // return the path
        return path;
    }
    // sequence of indices for vertical seam
    public int[] findVerticalSeam() 
    {
        // energies holds the energy of each pixel
        double[][] energies = new double[pic.getWidth()][pic.getHeight()];
        // distTo holds the minDistance to the pixel spot
        double[][] distTo = new double[pic.getWidth()][pic.getHeight()];
        // edgeTo holds the edge that distTo comes from
        int[][] edgeTo = new int[pic.getWidth()][pic.getHeight()];
        
        // iterate through each pixel
        for (int i = 0; i < pic.getWidth(); i++)   
        {
            for (int j = 0; j < pic.getHeight(); j++)
            {
                // set energy 
                energies[i][j] = energy(i, j);
                
                // if first row --> distTo = energy 
                if (j == 0) distTo[i][j] = energies[i][j];
                // otherwise distTo = Positive Infinity
                else  distTo[i][j] = Double.POSITIVE_INFINITY;
                
            }
        }
        
        // iterate by row --> skip last row
        for (int j = 0; j < pic.getHeight() - 1; j++)
        {
            // then by col
            for (int i = 0; i < pic.getWidth(); i++)
            {
                // THIS IS ALL BASICALLY RELAXING
                // CHECK TO MAKE SURE CORRECT VERTEX IS POSSIBLE 
                // THEN RELAX IT
                double tDist = distTo[i][j];
                
                // relax edge below
                if (distTo[i][j+1] > energies[i][j+1] + tDist)
                {
                    distTo[i][j+1] = energies[i][j+1] + tDist;
                    edgeTo[i][j+1] = i;
                }
                // relax lower left edge if possible    
                if (i != 0)
                {
                    if (distTo[i-1][j+1] > energies[i-1][j+1] + tDist)
                    {
                        distTo[i-1][j+1] = energies[i-1][j+1] + tDist;
                        edgeTo[i-1][j+1] = i;
                    }
                }
                //relax lower right edge if possible 
                if (i != pic.getWidth() - 1)
                {
                    if (distTo[i+1][j+1] > energies[i+1][j+1] + tDist)
                    {
                        distTo[i+1][j+1] = energies[i+1][j+1] + tDist;
                        edgeTo[i+1][j+1] = i;
                    }
                }
                
            }
        }
        
        // convert into correct array
        int minBot = 0;
        int jd = pic.getHeight() - 1;
        
        // find the distTo on the bottom row with the smallest value 
        for (int i = 1; i < pic.getWidth(); i++)
        {
            // find lowest value 
            if (distTo[i][jd] < distTo[minBot][jd])
            {
                minBot = i;   
            }
        }
        
        // create correct path
        int[] path = new int[pic.getHeight()];
        path[pic.getHeight() - 1] = minBot;
        
        // fill it in from the back 
        for (int j = pic.getHeight() - 2; j >= 0; j--)
        { 
            path[j] = edgeTo[path[j+1]][j+1];
        }  
 
        // return 
        return path;
    }
    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam)
    {
        if (seam == null)
            throw new NullPointerException("Parameter cannot be null");
        if (seam.length != pic.getWidth())
            throw new IllegalArgumentException("Array must have correct length");
        if (pic.getHeight() == 1)
            throw new IllegalArgumentException("Cannot remove seam when height = 1");
        boolean first = true;
        int last = 0;
        // check to make sure that in the array no entry is more than 1 away from 
        // the previous entry 
        for (int i : seam){
            // if first iteration --> set last and iterate
            if (first) first = false;
            // otherwise throw exception if i is more than 1 away from last
            // if not reset last
            else 
            {
                if (i - last > 1 || last - i > 1) 
                    throw new IllegalArgumentException("Seam indices too far away");
            }
            last = i;
            
            if (i < 0 || i >= pic.getHeight())
                throw new IllegalArgumentException("Seam indices not valid");
        }  
        
        // create new picture and fill it with correct pixels 
        BufferedImage newPic = new BufferedImage(pic.getWidth(), pic.getHeight() - 1, pic.getType());
        
        // fill it with correct pixels
        // exact same as removeVerticalSeasm
        for (int i = 0; i < pic.getWidth(); i++)
        {
            int currIndex = 0;
            int skipIndex = seam[i];
            for (int j = 0; j < pic.getHeight(); j++)
            {
                if (j != skipIndex) 
                {
                    newPic.setRGB(i, currIndex, pic.getRGB(i, j));
                    currIndex++;
                }
            }
        }
        pic = newPic;
    }
    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam)
    {
        if (seam == null)
            throw new NullPointerException("Parameter cannot be null");
        if (seam.length != pic.getHeight())
            throw new IllegalArgumentException("Array must have correct length");
        if (pic.getWidth() == 1)
            throw new IllegalArgumentException("Cannot remove seam when width = 1");
        
        boolean first = true;
        int last = 0;
        // check to make sure that in the array no entry is more than 1 away from 
        // the previous entry 
        for (int i : seam){ 
            // if first iteration --> set last and iterate
            if (first) first = false;
            // otherwise throw exception if i is more than 1 away from last
            // if not reset last
            else  {
                if (i - last > 1 || last - i > 1) 
                    throw new IllegalArgumentException("Seam indices too far away");
            }
            last = i;
            
            if (i < 0 || i >= pic.getWidth())
                throw new IllegalArgumentException("Seam indices not valid");
        } 
        
        // create a new picture
        BufferedImage newPic = new BufferedImage(pic.getWidth() - 1, pic.getHeight(), pic.getType());
        
        // iterate through each row 
        for (int j = 0; j < pic.getHeight(); j++) {
            int currIndex = 0;
            int skipIndex = seam[j];
            // iterate through each column
            for (int i = 0; i < pic.getWidth(); i++) {
                // put in each column that is not the skip index 
                // 1 col less
                if (i != skipIndex) {
                    newPic.setRGB(currIndex, j, pic.getRGB(i, j));
                    currIndex++;
                }
            }
        }
        pic = newPic;
    }
    
    // helper class to find differential between 2 colors
    private double colorToDouble(int c1, int c2)
    {
        // get the difference in colors
        double dRed   = getRed(c1)   - getRed(c2);  
        double dGreen = getGreen(c1) - getGreen(c2);
        double dBlue  = getBlue(c1)  - getBlue(c2);
        
        // square the differences
        dRed = dRed * dRed;
        dBlue = dBlue * dBlue; 
        dGreen = dGreen * dGreen;
        
        // add them and return 
        return dRed + dBlue + dGreen;
    } 
    
    // helper methods to convert 2d to 1d
    private int twoToOne(int i, int j)
    {
        return j * pic.getWidth()  + i;    
    }
    
    private int oneToRow(int num)
    {
        return num / pic.getWidth();
    }
    
    private int oneToCol(int num)
    {
        return num % pic.getWidth();
    }
    
    // helper method to invert the picture
    private void invertPic()
    {
        // go through and just swap i,j with j,i
        BufferedImage npic = new BufferedImage(pic.getHeight(), pic.getWidth(), pic.getType());
        for (int i = 0; i < pic.getWidth(); i++)
        {
            for (int j = 0; j < pic.getHeight(); j++)
            {
                npic.setRGB(j, i, pic.getRGB(i, j));   
            }
        }
        pic = npic;
    }

    // create a seam carver object based on the given picture
    public SeamCarver2(BufferedImage image, int removeRows, int removeColumns) {
        if (image == null) 
            throw new NullPointerException("Parameter cannot be null");
        
        // create defensive copy 
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());        
        for (int j = 0; j < image.getHeight(); j++){
            for (int i = 0; i < image.getWidth(); i++) {
                newImage.setRGB(i, j, image.getRGB(i, j));   
            }
        }
        pic = newImage;

        // heights and widths 
        // get widths
        int picWidth        = newImage.getWidth();
        int picHeight       = newImage.getHeight();
        int newWidth        = picWidth - removeColumns;
        int newHeight       = picHeight - removeRows;

        // make sure it is possible: 
        if (removeColumns < 0 || removeRows < 0 || newWidth <= 0 || newHeight <= 0) { 
            System.out.println("ARGUMENTS CANNOT BE NEGATIVE"); 
            return;
        } 

        int totalseams = removeColumns + removeRows;
        while(totalseams > 0) {
            if (Math.random() < .5 && removeRows > 0)  {
                // remove horizontal seam
                int[] horizontalSeam = findHorizontalSeam();
                removeHorizontalSeam(horizontalSeam);
                removeRows--;
            }
            else if (removeColumns > 0) {
                int[] verticalSeam = findVerticalSeam();
                removeVerticalSeam(verticalSeam);
                removeColumns--;
            }
            totalseams = removeColumns + removeRows;
        }

        System.out.printf("new image size is %d columns by %d rows\n", width(), height());
    }

    public static void main(String[] args)
    {
       if (args.length != 3) {
            System.out.println("Usage:\njava ResizeDemo [image filename] [num columns to remove] [num rows to remove]");
            return;
        }

        // read in file
        File inputFile = new File(args[0]);
        BufferedImage inputImage;
        try {
            inputImage = ImageIO.read(inputFile);
        }
        catch(IOException e){
            return;
        }

        SeamCarver2 sc = new SeamCarver2(inputImage, Integer.parseInt(args[1]), Integer.parseInt(args[2]));

        // make new filename 
        String[] fileN = args[0].split("\\.");
        String name = fileN[0] + "x"  + ".jpg";  

        // try to save the file
        try{
            File dir = new File("images");
            dir.mkdirs();
            File tmp = new File(dir, "new");
            tmp.createNewFile();
            ImageIO.write(sc.picture(), "jpg", tmp);
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        return;
    }
    
}
