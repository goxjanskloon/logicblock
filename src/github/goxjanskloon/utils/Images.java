package github.goxjanskloon.utils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
public class Images{
    public static BufferedImage read(String path)throws IOException{
        return ImageIO.read(new File(path));
    }
}
