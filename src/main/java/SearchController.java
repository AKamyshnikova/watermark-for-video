
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;


import javax.imageio.ImageIO;
import javax.swing.*;


public class SearchController {

    private String fileName;
    private String newFileName;

    @FXML
    private Button search;


    public void search() {
        JFileChooser fileChooser = new JFileChooser();
        int res = fileChooser.showDialog(null, "Set watermark to");
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            fileName = file.getAbsolutePath();
            try {
                String answer = JOptionPane.showInputDialog(null, "Please, set full new file name");
                if (correctNewFileName(answer)){
                    newFileName=answer;
                }
                Converter.convert(newFileName, fileName);
            }
            catch (IOException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            int exit=JOptionPane.showConfirmDialog(null, "Watermark was set successfully to "+ fileName+ "!\n" +
                    " New file is "+newFileName
                    + " Do you want to exit?","Success",JOptionPane.YES_NO_OPTION);
            if (exit==JOptionPane.YES_OPTION){
                System.exit(0);
            }
        }
    }

    private boolean correctNewFileName(String fileName){
        if ((fileName.endsWith(".mp4") || fileName.endsWith(".avi") ||fileName.endsWith(".mkv")) &&
                new File(fileName.substring(0,fileName.lastIndexOf("\\"))).exists())
            return  true;
        else
            return false;
    }
}