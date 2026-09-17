package com;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Font;
import javax.swing.SwingConstants;

public class PurchaseUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel lblFileName;    // 顯示上傳檔名
	private JLabel lblTargetDir;   // 顯示特定資料夾路徑

	/**
	 * Launch the application.
	 */
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

	/**
	 * Create the frame.
	 */
	public PurchaseUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 602, 358); // 擴大視窗以完整顯示路徑
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// 1. 建立「上傳合約」按鈕
		JButton btnUpload = new JButton("上傳合約 (PDF)");
		btnUpload.setBounds(39, 116, 150, 30);
		contentPane.add(btnUpload);

		// 2. 建立顯示「檔名」的標籤
		lblFileName = new JLabel("目前檔名：尚未上傳任何檔案");
		lblFileName.setBounds(39, 166, 520, 30);
		contentPane.add(lblFileName);

		// 3. 建立顯示「存放目標資料夾路徑」的標籤
		lblTargetDir = new JLabel("存放路徑：尚未設定");
		lblTargetDir.setBounds(39, 206, 520, 30);
		contentPane.add(lblTargetDir);
		
		JLabel lblNewLabel = new JLabel("供應商上傳合約");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("微軟正黑體 Light", Font.BOLD, 18));
		lblNewLabel.setBounds(39, 39, 520, 41);
		contentPane.add(lblNewLabel);

		// 4. 綁定按鈕點擊事件
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				uploadAndSaveContract();
			}
		});
	}

	/**
	 * 核心功能：選擇 PDF、複製到特定資料夾、更新 UI 顯示
	 */
	private void uploadAndSaveContract() {
		// 指定特定的存放資料夾（這裡設定在專案目錄下的 "contracts" 資料夾）
		// 如果您想指定絕對路徑，可以改成如：Paths.get("C:/MyProject/UploadedContracts")
		Path targetDirectory = Paths.get("contracts");

		// 建立檔案選擇器
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));
		
		// 限制只能選擇 PDF
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF 合約檔 (*.pdf)", "pdf");
		fileChooser.setFileFilter(filter);
		fileChooser.setAcceptAllFileFilterUsed(false);

		// 彈出選單並取得結果
		int result = fileChooser.showOpenDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) {
			// 取得使用者選取的原始檔案
			File sourceFile = fileChooser.getSelectedFile();
			
			try {
				// 確保特定資料夾存在，如果不存在就自動建立
				if (!Files.exists(targetDirectory)) {
					Files.createDirectories(targetDirectory);
				}

				// 組合出檔案在目標資料夾的新路徑 (保留原始檔名)
				Path destinationPath = targetDirectory.resolve(sourceFile.getName());

				// 執行檔案複製 (REPLACE_EXISTING 表示若有同名檔案就覆蓋)
				Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

				// ⭐ 更新介面顯示
				lblFileName.setText("目前檔名：" + sourceFile.getName());
				lblTargetDir.setText("存放路徑：" + targetDirectory.toAbsolutePath().toString());

				// 彈出成功提示
				JOptionPane.showMessageDialog(this, "合約上傳並儲存成功！");

			} catch (IOException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "檔案儲存失敗：" + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
			}
			
		} else if (result == JFileChooser.CANCEL_OPTION) {
			System.out.println("使用者取消上傳");
		}
	}
}
