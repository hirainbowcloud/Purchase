package com;

import java.awt.BorderLayout; // 📦 新增
import java.awt.Cursor;       // 📦 新增
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter; // 📦 新增：監聽滑鼠點擊
import java.awt.event.MouseEvent;  // 📦 新增
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog; // 📦 新增：彈出式視窗
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane; // 📦 新增：滾動條
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PurchaseUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel lblFileName;    
	private JLabel lblTargetDir;   
	private JLabel lblPreview;     
	
	// ⭐ 新增：將目前讀取的 PDF 檔案記錄下來，方便放大鏡視窗讀取
	private File currentPDFFile = null; 

	public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                	PurchaseUI frame = new PurchaseUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

	public PurchaseUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 550); 
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnUpload = new JButton("上傳合約 (PDF)");
		btnUpload.setBounds(10, 100, 268, 30);
		contentPane.add(btnUpload);

		lblFileName = new JLabel("目前檔名：尚未上傳任何檔案");
		lblFileName.setBounds(10, 150, 250, 30);
		contentPane.add(lblFileName);

		lblTargetDir = new JLabel("存放路徑：尚未設定");
		lblTargetDir.setBounds(10, 200, 250, 30);
		contentPane.add(lblTargetDir);
		
		JLabel lblNewLabel = new JLabel("供應商上傳合約");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("微軟正黑體 Light", Font.BOLD, 18));
		lblNewLabel.setBounds(250, 10, 335, 41);
		contentPane.add(lblNewLabel);

		// 🎨 預覽框 (加上提示訊息，可以點擊放大)
		lblPreview = new JLabel("PDF 預覽區域 (點擊可放大)");
		lblPreview.setFont(new Font("新細明體", Font.PLAIN, 16));
		lblPreview.setHorizontalAlignment(SwingConstants.CENTER);
		lblPreview.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.GRAY)); 
		lblPreview.setBounds(300, 80, 450, 400); 
		contentPane.add(lblPreview);
		
		JButton btnSend = new JButton("送出");
		btnSend.setBounds(10, 450, 269, 30);
		contentPane.add(btnSend);

		// ⭐ 新增：給預覽圖綁定滑鼠點擊事件（模擬放大鏡功能）
		lblPreview.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 如果已經有上傳檔案，點擊就彈出放大鏡視窗
				if (currentPDFFile != null) {
					showZoomDialog();
				}
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				// 當滑鼠移入預覽圖時，指標變成「手型」，提示使用者這裡可以點擊
				if (currentPDFFile != null) {
					lblPreview.setCursor(new Cursor(Cursor.HAND_CURSOR));
				}
			}
		});

		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				uploadAndSaveContract();
			}
		});
	}

	private void uploadAndSaveContract() {
		Path targetDirectory = Paths.get("contracts");

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF 合約檔 (*.pdf)", "pdf");
		fileChooser.setFileFilter(filter);
		fileChooser.setAcceptAllFileFilterUsed(false);

		int result = fileChooser.showOpenDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) {
			File sourceFile = fileChooser.getSelectedFile();
			
			try {
				if (!Files.exists(targetDirectory)) {
					Files.createDirectories(targetDirectory);
				}

				Path destinationPath = targetDirectory.resolve(sourceFile.getName());
				Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

				lblFileName.setText("檔名:" + sourceFile.getName());
				lblTargetDir.setText("存放路徑：" + targetDirectory.toAbsolutePath().toString());

				// ⭐ 記錄目前檔案路徑，供放大鏡使用
				currentPDFFile = destinationPath.toFile();
				
				previewPDF(currentPDFFile);

				JOptionPane.showMessageDialog(this, "合約上傳並儲存成功！");

			} catch (IOException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "檔案儲存失敗：" + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void previewPDF(File pdfFile) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try (PDDocument document = Loader.loadPDF(pdfFile)) {
					PDFRenderer pdfRenderer = new PDFRenderer(document);
					BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 72);
					java.awt.Image scaledImage = bim.getScaledInstance(lblPreview.getWidth(), lblPreview.getHeight(), java.awt.Image.SCALE_SMOOTH);
					
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							lblPreview.setText(""); 
							lblPreview.setIcon(new ImageIcon(scaledImage)); 
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * ⭐ 新增核心功能：彈出高清放大鏡視窗 (含滾動條)
	 */
	private void showZoomDialog() {
		// 建立一個彈出式對話框，設定為非強制阻擋 (Modal = false)
		JDialog zoomDialog = new JDialog(this, "🔍 合約詳細放大預覽", false);
		zoomDialog.setSize(900, 800); // 放大視窗尺寸加大
		zoomDialog.setLocationRelativeTo(this); // 居中顯示

		// 在新視窗中間建立一個顯示大圖的 Label
		JLabel lblFullImage = new JLabel("正在載入高清預覽...", SwingConstants.CENTER);

		// 使用 JScrollPane 將 Label 包起來，當圖片太大時會自動出現捲動條
		JScrollPane scrollPane = new JScrollPane(lblFullImage);
		zoomDialog.getContentPane().add(scrollPane, BorderLayout.CENTER);

		// 非同步讀取高清圖像
		new Thread(new Runnable() {
			@Override
			public void run() {
				try (PDDocument document = Loader.loadPDF(currentPDFFile)) {
					PDFRenderer pdfRenderer = new PDFRenderer(document);
					
					// ⭐ 將 DPI 提高到 150 或 200，這樣放大的合約文字才會非常清晰，不會模糊
					BufferedImage highResImage = pdfRenderer.renderImageWithDPI(0, 150);
					
					EventQueue.invokeLater(new Runnable() {
						@Override
						public void run() {
							lblFullImage.setText("");
							lblFullImage.setIcon(new ImageIcon(highResImage));
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
					lblFullImage.setText("❌ 高清渲染失敗");
				}
			}
		}).start();

		// 顯示放大視窗
		zoomDialog.setVisible(true);
	}
}
