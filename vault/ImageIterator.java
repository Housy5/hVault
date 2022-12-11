package vault;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import vault.format.FormatDetector;
import vault.gui.MessageDialog;
import vault.nfsys.FilePointer;

public class ImageIterator implements Iterator<Image> {

    private final List<Image> images;
    private int cursor = 0;
    
    private boolean validateImageType(Image img) {
        try {
            BufferedImage test = (BufferedImage) img;
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }
    
    private boolean compareImages(Image img1, Image img2) {
        if (!validateImageType(img1) || !validateImageType(img2)) {
            throw new IllegalArgumentException("Please ensure that both images are of type BufferedImage");
        }
         
        BufferedImage imgA = (BufferedImage) img1;
        BufferedImage imgB = (BufferedImage) img2;
        
        if (imgA.getWidth() != imgB.getWidth() || imgA.getHeight() != imgB.getHeight()) {
            return false;
        }
        
        int width = imgA.getWidth();
        int height = imgA.getHeight();
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (imgA.getRGB(j, i) != imgB.getRGB(j, i)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public ImageIterator(List<Image> imageList) {
        images = new ArrayList<>();
        images.addAll(imageList);
    }
    
    public boolean seek(Image image) {
        for (int i = 0; i < images.size(); i++) {
            if (compareImages(image, images.get(i))) {
                cursor = i;
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public boolean hasNext() {
        return !images.isEmpty() && cursor < images.size() - 1;
    }

    @Override
    public Image next() {
        cursor++;
        return images.get(cursor);
    }
    
    public Image prev() {
        cursor--;
        return images.get(cursor);
    }
    
    public boolean hasPrev() {
        return !images.isEmpty() && cursor > 0;
    }
    
    public Image curr() {
        return images.get(cursor);
    }
    
    public boolean hasCurr() {
        try {
            images.get(cursor);
            return true; 
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public static ImageIterator fromCurrentFolder() {
        var pointers = Main.frameInstance.user.fsys.getCurrentFolder().getFiles();
        List<Image> imgs = new ArrayList<>();
        for (FilePointer pointer : pointers) {
            if (FormatDetector.instance().detectFormat(pointer.getName()) == FormatDetector.IMAGE) {
                try {
                    imgs.add(IconUtil.getInstance().getImage(pointer));
                } catch (IOException e) {
                    MessageDialog.show(Main.frameInstance, e.getMessage());
                }
            }
        }
        return new ImageIterator(imgs);
    }
}
