package byebyewatermark;

import javax.imageio.*;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.Canvas;
import java.awt.Button;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;

import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_imgproc.*;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;

import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_core.*;
import javax.swing.ImageIcon;
import java.awt.Toolkit;


public class UI extends JFrame {

	private JPanel contentPane;
	private String watermarkedImgPath, thumnbailImgPath, maskedImgPath;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UI frame = new UI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public UI() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(UI.class.getResource("/com/sun/javafx/scene/web/skin/FontBackgroundColor_16x16_JFX.png")));
		watermarkedImgPath = "";
		thumnbailImgPath = "";
		maskedImgPath = "";
		
		setTitle("byebyewatermark v0.1 alpha");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 625, 182);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel statusLabel = new JLabel("...");
		statusLabel.setBounds(10, 120, 589, 14);
		contentPane.add(statusLabel);
		
		JLabel lblWatermarkPath = new JLabel("no image selected");
		lblWatermarkPath.setBounds(319, 15, 280, 14);
		contentPane.add(lblWatermarkPath);
		
		JLabel lblThumbnailPath = new JLabel("no image selected");
		lblThumbnailPath.setBounds(319, 40, 280, 14);
		contentPane.add(lblThumbnailPath);
		
		JLabel lblMaskedPath = new JLabel("no image selected");
		lblMaskedPath.setBounds(319, 65, 280, 14);
		contentPane.add(lblMaskedPath);
		
		JButton previewImgBtn = new JButton("preview unwatermarked");
		previewImgBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// If the image path isn't set
				if (watermarkedImgPath.equals("") || thumnbailImgPath.equals("") || maskedImgPath.equals("")){					
					statusLabel.setText("please select yer images");
				}
				else{
					IplImage watermarkedImage = cvLoadImage(watermarkedImgPath);
					
					// Our cleaned image
					IplImage cleanedImage = new IplImage();
					cleanedImage = IplImage.create(watermarkedImage.cvSize(), watermarkedImage.depth(), watermarkedImage.nChannels());
					cleanedImage = getCleanImage();
									
					CanvasFrame frame = new CanvasFrame("preview", CanvasFrame.getDefaultGamma());
					OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
					Frame newImage = converter.convert(cleanedImage);
					frame.showImage(newImage);
				}
			}
		});
		previewImgBtn.setBounds(10, 90, 285, 23);
		contentPane.add(previewImgBtn);
		
		JLabel lblWatermarkedImage = new JLabel("watermarked image:");
		lblWatermarkedImage.setBounds(10, 15, 200, 14);
		contentPane.add(lblWatermarkedImage);
		
		JButton watermarkedBrowseBtn = new JButton("browse");
		watermarkedBrowseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String filepath = getFilePathString();
				if (filepath != ""){
					watermarkedImgPath = filepath;
					lblWatermarkPath.setText(filepath);
				}
				else{
					watermarkedImgPath = "";
					lblWatermarkPath.setText("no image selected...");
				}
			}
		});
		watermarkedBrowseBtn.setBounds(220, 11, 89, 23);
		contentPane.add(watermarkedBrowseBtn);
		
		JLabel lblThumbnailImage = new JLabel("thumbnail image:");
		lblThumbnailImage.setBounds(10, 40, 200, 14);
		contentPane.add(lblThumbnailImage);
		
		JButton thumbnailBrowseBtn = new JButton("browse");
		thumbnailBrowseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filepath = getFilePathString();
				if (filepath != ""){
					thumnbailImgPath = filepath;
					lblThumbnailPath.setText(filepath);
				}
				else{
					thumnbailImgPath = "";
					lblThumbnailPath.setText("no image selected...");
				}
			}
		});
		thumbnailBrowseBtn.setBounds(220, 36, 89, 23);
		contentPane.add(thumbnailBrowseBtn);
		
		JLabel lblMaskedIamge = new JLabel("masked image:");
		lblMaskedIamge.setBounds(10, 65, 200, 14);
		contentPane.add(lblMaskedIamge);
		
		JButton maskedBrowseBtn = new JButton("browse");
		maskedBrowseBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String filepath = getFilePathString();
				if (filepath != ""){
					maskedImgPath = filepath;
					lblMaskedPath.setText(filepath);
				}
				else{
					maskedImgPath = "";
					lblMaskedPath.setText("no image selected...");
				}
			}
		});
		maskedBrowseBtn.setBounds(220, 61, 89, 23);
		contentPane.add(maskedBrowseBtn);
		
		JButton unwatermarkBtn = new JButton("save unwatermark");
		unwatermarkBtn.setIcon(new ImageIcon(UI.class.getResource("/com/sun/javafx/scene/web/skin/FontBackgroundColor_16x16_JFX.png")));
		unwatermarkBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// If the image path isn't set
				if (watermarkedImgPath.equals("") || thumnbailImgPath.equals("") || maskedImgPath.equals("")){					
					statusLabel.setText("please select yer images");
				}
				else{
					IplImage watermarkedImage = cvLoadImage(watermarkedImgPath);
					
					// Filenames
					String outdir = watermarkedImgPath.split("\\.")[0];
					String extension = watermarkedImgPath.split("\\.")[1];
					String outName = outdir+"_unwatermarked."+extension;
					
					// Our cleaned image
					IplImage cleanedImage = new IplImage();
					cleanedImage = IplImage.create(watermarkedImage.cvSize(), watermarkedImage.depth(), watermarkedImage.nChannels());
					cleanedImage = getCleanImage();
									
					statusLabel.setText("Successfully wrote " + outName);
					cvSaveImage(outName, cleanedImage);
				}
			}
		});
		unwatermarkBtn.setBounds(314, 90, 285, 23);
		contentPane.add(unwatermarkBtn);								
	}
	
	// Gets the file path for the image selected
	private String getFilePathString(){
		JFileChooser fileChooser = new JFileChooser();
		
		int result = fileChooser.showOpenDialog(null);
		
		if(result == JFileChooser.APPROVE_OPTION){
			File currentFile = fileChooser.getSelectedFile();
			String muhfilepath = currentFile.getAbsolutePath();
			
			return muhfilepath;
		}
		return "";
	}

	// Gets converted picture
	private IplImage getCleanImage(){
		IplImage watermarkedImage = cvLoadImage(watermarkedImgPath);
		IplImage thumnbailImage = cvLoadImage(thumnbailImgPath);
		IplImage maskedImage = cvLoadImage(maskedImgPath);
		
		// Resize thumbnail
		IplImage thumbnail_resized = new IplImage();
		thumbnail_resized = IplImage.create(watermarkedImage.cvSize(), watermarkedImage.depth(), watermarkedImage.nChannels());
		cvResize(thumnbailImage, thumbnail_resized, CV_INTER_LANCZOS4);
		
		// Resize mask
		IplImage masked_resized = new IplImage();
		masked_resized = IplImage.create(watermarkedImage.cvSize(), watermarkedImage.depth(), watermarkedImage.nChannels());
		cvResize(maskedImage, masked_resized, CV_INTER_NN);
		
		// Our cleaned image
		IplImage cleanedImage = new IplImage();
		cleanedImage = IplImage.create(watermarkedImage.cvSize(), watermarkedImage.depth(), watermarkedImage.nChannels());
		cvCopy(watermarkedImage, cleanedImage);
		
		// Copy thumbnail resized over to mask
		for (int x = 0; x < watermarkedImage.cvSize().width(); x++){
			for (int y = 0; y < watermarkedImage.cvSize().height(); y++){
				CvScalar g = cvGet2D(masked_resized, y, x);
				
				if (g.red() == 0 && g.green() == 0 && g.blue() == 0){
					CvScalar s = cvGet2D(thumbnail_resized, y, x);
					cvSet2D(cleanedImage, y, x, s);
				}
			}
		}
		
		return cleanedImage;
	}
}
