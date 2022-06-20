package hr.unipu.tfpu.rp.projekt;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class Images {

    private File image;
    private  boolean isFront;
    private String filename;

    public String getFilename() {
        return filename;
    }

    public boolean isFront() {
        return isFront;
    }

    public File getImage() {
        return image;
    }

    public static ArrayList<Images> images = new ArrayList<Images>();
    public Images(File image, boolean isFront, String filename) {
        this.image = image;
        this.isFront = isFront;
        this.filename = filename;
    }

    public Images() {
        super();
        getImages();
    }

    public static void getImages() {
        File[] directories = new File("razglednice").listFiles(File::isDirectory);
        for (File f : directories) {
            for (File img : f.listFiles(File::isFile)) {
                String name = img.getName().substring(0, img.getName().lastIndexOf('.'));
                String fullName = f.getName() + " - " + name.substring(0, name.length()-2);
                boolean front = (name.charAt(name.length()-1) == 'F' ? true : false);
                images.add(new Images(img, front, fullName));
            }
        }
    }
}
