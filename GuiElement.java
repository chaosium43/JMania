import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;

public class GuiElement {
    //master gui element
    public static HashSet<FrameElement> gui = new HashSet<>();
    public static HashSet<TextButton> textButtons = new HashSet<>();
    public static HashSet<ImageButton> imageButtons = new HashSet<>();

    public static class FrameElement {
        public double[] size = {0, 100, 0, 100};
        public double[] position = {0, 0, 0, 0};
        public double[] anchorPoint = {0, 0};
        public Color backgroundColor = new Color(255, 255, 255);
        public boolean backgroundVisible = true;
        public boolean visible = false;
        public int displayOrder = 0;

        //returns the absolute size of the element in a double array {x, y}
        public double[] absoluteSize(Graphics g) {
            Rectangle windowBounds = g.getClipBounds();
            double windowX = windowBounds.getWidth();
            double windowY = windowBounds.getHeight();
            double[] aSize = {size[0] * windowX + size[1], size[2] * windowY + size[3]};
            
            return aSize;
        }

        //returns the absolute position of the element in a double array {x, y}
        public double[] absolutePosition(Graphics g) {
            Rectangle windowBounds = g.getClipBounds();
            double windowX = windowBounds.getWidth();
            double windowY = windowBounds.getHeight();

            double[] size = absoluteSize(g);
            double[] aPos = {
                position[0] * windowX - size[0] * anchorPoint[0] + position[1],
                position[2] * windowY - size[1] * anchorPoint[1] + position[3]
            };
            return aPos;
        }

        public void render(Graphics g) {
            //check if the frame should be rendered
            if (visible && backgroundVisible) {
                //reset the affine transform for the graphics page
                //get sizes for x and y
                double[] aSize = absoluteSize(g);
                double[] aPos = absolutePosition(g);
                
                g.setColor(backgroundColor);
                g.fillRect((int)aPos[0], (int)aPos[1], (int)aSize[0], (int)aSize[1]);
            }
        }

        //empty because values are usually initialized in the first place
        public FrameElement() {
            gui.add(this);
        }

        //dereferences the gui object from the gui hash set
        public void destroy() {
            gui.remove(this);
        }
    }

    //frame with text drawn onto it
    public static class TextElement extends FrameElement {
        public Font font = new Font("Callibri", Font.PLAIN, 10);;
        public int fontSize = 21;
        public String text = "TextElement";
        public Color textColor = new Color(0, 0, 0);
        public double[] textAnchorPoint = {0, 0};
        public double[] textBorderInset = {15, 25};
        
        public void render(Graphics g) {
            super.render(g); //render the frame behind the text element
            if (visible) {
                //get the amount of space required to render the text on the textlabel
                FontMetrics fm = g.getFontMetrics(font);
                Rectangle2D renderSize = fm.getStringBounds(text, g);
                double xSize = renderSize.getWidth() + 4;
                double ySize = font.getSize();
                double[] aSize = absoluteSize(g);
                aSize[0] -= textBorderInset[0];
                aSize[1] -= textBorderInset[1];

                //Create the necessary affine transform to scale the text
                AffineTransform sizer = new AffineTransform();
                double xTransform = aSize[0] / xSize;
                double yTransform = aSize[1] / ySize;

                //we want to rescale the text to the constraint that is more pressing
                double minTransform = Math.min(xTransform, yTransform);
                sizer.scale(minTransform, minTransform);
                g.setFont(font.deriveFont(sizer));

                //calculate the position of the text within the frame based on the text anchor point
                double[] aPos = absolutePosition(g);
                int xDraw = (int)(aPos[0] + textBorderInset[0] + (aSize[0] - xSize * minTransform) * textAnchorPoint[0]);
                int yDraw = (int)(aPos[1] + textBorderInset[1] + aSize[1] - (aSize[1] - (ySize - 5) * minTransform) * (1 - textAnchorPoint[1]));

                //finally draw the string onto the screen
                g.setColor(textColor);
                g.drawString(text, xDraw, yDraw);
            }
        }
        public TextElement() {
            super();
        }
    }

    public static class ImageElement extends FrameElement {
        public BufferedImage image; //the image that is going to be displayed on to the image element
        public boolean fixedAspectRatio = true;
        public double[] imageAnchorPoint = {0, 0};
        public void render(Graphics g) {
            super.render(g); //render the frame element behind the image
            if (visible) {
                //get stats for the render
                double[] aPos = absolutePosition(g);
                double[] aSize = absoluteSize(g);

                //calculate the dimensions of the image and apply it to the rescale
                int imageX = image.getWidth();
                int imageY = image.getHeight();
                double xGap = 0;
                double yGap = 0;
                Image scaledImage;

                //create the scaled image instance
                double scaleRatio;
                if (fixedAspectRatio) {
                    scaleRatio = Math.min(aSize[0] / imageX, aSize[1] / imageY);
                    scaledImage = image.getScaledInstance((int)(imageX * scaleRatio), (int)(imageY * scaleRatio), imageY);

                    //image anchor point must be dealt with in the case of fixed aspect ratio image rendering
                    xGap = aSize[0] - imageX * scaleRatio;
                    yGap = aSize[1] - imageY * scaleRatio;
                } else {
                    scaleRatio = (aSize[1] / imageY);
                    scaledImage = image.getScaledInstance((int)aSize[0], (int)aSize[1], 0);
                }

                //draw the resized image onto the graphics
                g.drawImage(scaledImage, (int)(aPos[0] + xGap * imageAnchorPoint[0]), (int)(aPos[1] + yGap * imageAnchorPoint[1]), null);
            }
        }
        public ImageElement() {
            super();
        }
    }

    public static class TextButton extends TextElement {
        public void onClick() {};
        public void render(Graphics g) {
            super.render(g);
        }
        public TextButton() {
            super();
            textButtons.add(this);
        }

        //in addition to removing this from the gui list, it must be removed from the button list
        public void destroy() {
            super.destroy();
            textButtons.remove(this);
        }
    }

    public static class ImageButton extends ImageElement {
        public void onClick() {};
        public void render(Graphics g) {
            super.render(g);
        }
        public ImageButton() {
            super();
            imageButtons.add(this);
        }

        //in addition to removing this from the gui list, it must be removed from the button list
        public void destroy() {
            super.destroy();
            imageButtons.remove(this);
        }
    }
}
