package vault;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public final class IconUtil {

    private static IconUtil instance;

    private final Map<String, BufferedImage> map;

    private IconUtil() {
        map = new HashMap<>();
    }

    public final ImageIcon getUIModeIcon(UIMode mode) throws IOException {
        return switch (mode) {
            case DARK ->
                getIcon("/res/eye_dark_mode.png");
            case LIGHT ->
                getIcon("/res/eye_light_mode.png");
        };
    }

    public final BufferedImage getImage(String path) throws IOException {
        if (map.containsKey(path)) {
            return map.get(path);
        }
        var img = ImageIO.read(getClass().getResource(path));
        map.put(path, img);
        return img;
    }

    public final ImageIcon getIcon(String path) throws IOException {
        return new ImageIcon(getImage(path));
    }

    public static IconUtil getInstance() {
        if (instance == null) {
            instance = new IconUtil();
        }
        return instance;
    }

    public final ImageIcon rotateIcon(ImageIcon icon, int degree) {
        BufferedImage bimg = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = bimg.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        return new ImageIcon(rotateImage(bimg, degree));
    }
    
    public final BufferedImage rotateImage(BufferedImage image, int degree) {
        var rads = Math.toRadians(degree);
        var sin = Math.abs(Math.sin(rads));
        var cos = Math.abs(Math.cos(rads));

        var w = (int) Math.floor(image.getWidth() * cos + image.getHeight() * sin);
        var h = (int) Math.floor(image.getHeight() * cos + image.getWidth() * sin);
        var rotatedImg = new BufferedImage(w, h, image.getType());
        var at = new AffineTransform();
        at.translate(w / 2, h / 2);
        at.rotate(rads, 0, 0);
        at.translate(-image.getWidth() / 2, -image.getHeight() / 2);

        AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        rotateOp.filter(image, rotatedImg);
        return rotatedImg;
    }
}
