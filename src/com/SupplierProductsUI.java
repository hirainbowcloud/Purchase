package com;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SupplierProductsUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtProductName;       // 產品名稱輸入框
	private JTextArea txtProductContent;     // 產品內文輸入框
	private JComboBox<String> comboCategory; // 分類下拉選單
	private JLabel lblImagePreview;          // 圖片預覽區域
	private File selectedImageFile = null;   // 記錄使用者選取的圖片檔案

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SupplierProductsUI frame = new SupplierProductsUI();
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
	public SupplierProductsUI() {
		setTitle("供應商管理系統 - 新增產品");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 設定關閉此視窗時不要把整個主程式關掉
		setBounds(100, 100, 716, 642);
		setLocationRelativeTo(null); // 視窗置中
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null); // 使用絕對座標佈局，方便與 WindowBuilder 整合
		
		// 1. 頂部大標題
		JLabel lblTitle = new JLabel("供應商上傳產品");
		lblTitle.setBounds(10, 11, 680, 41);
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("微軟正黑體 Light", Font.BOLD, 20));
		contentPane.add(lblTitle);
		
		// 2. 產品名稱 標籤與欄位
		JLabel lblName = new JLabel("產品名稱：");
		lblName.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
		lblName.setBounds(40, 80, 80, 30);
		contentPane.add(lblName);
		
		txtProductName = new JTextField();
		txtProductName.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
		txtProductName.setBounds(130, 80, 500, 30);
		contentPane.add(txtProductName);
		txtProductName.setColumns(10);
		
		// 3. 產品分類 標籤與下拉選單
		JLabel lblCategory = new JLabel("產品分類：");
		lblCategory.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
		lblCategory.setBounds(40, 130, 80, 30);
		contentPane.add(lblCategory);
		
		// 建立下拉選單項目
		String[] categories = { "文具", "電腦", "電腦週邊" };
		comboCategory = new JComboBox<>(categories);
		comboCategory.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
		comboCategory.setBounds(130, 130, 200, 30);
		contentPane.add(comboCategory);
		
		// 4. 產品內文 標籤與多行文字區
		JLabel lblContent = new JLabel("產品內文：");
		lblContent.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
		lblContent.setBounds(40, 180, 80, 30);
		contentPane.add(lblContent);
		
		txtProductContent = new JTextArea();
		txtProductContent.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
		txtProductContent.setLineWrap(true); // 自動換行
		
		// 使用 JScrollPane 包裹文字區，字數多的時候會自動出現滾動條
		JScrollPane scrollPane = new JScrollPane(txtProductContent);
		scrollPane.setBounds(130, 180, 500, 120);
		contentPane.add(scrollPane);
		
		// 5. 產品圖片 標籤、上傳按鈕與預覽框
		JLabel lblImage = new JLabel("產品圖片：");
		lblImage.setFont(new Font("微軟正黑體", Font.PLAIN, 14));
		lblImage.setBounds(40, 320, 80, 30);
		contentPane.add(lblImage);
		
		JButton btnUploadImage = new JButton("選擇產品照片");
		btnUploadImage.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
		btnUploadImage.setBounds(130, 320, 130, 30);
		contentPane.add(btnUploadImage);
		
		// 圖片預覽 JLabel (設定灰色邊框)
		lblImagePreview = new JLabel("點擊上方按鈕上傳照片", SwingConstants.CENTER);
		lblImagePreview.setFont(new Font("微軟正黑體", Font.PLAIN, 12));
		lblImagePreview.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
		lblImagePreview.setBounds(130, 365, 300, 200);
		contentPane.add(lblImagePreview);
		
		// 6. 底部提交按鈕
		JButton btnSubmit = new JButton("確認上傳產品");
		btnSubmit.setFont(new Font("微軟正黑體", Font.BOLD, 14));
		btnSubmit.setBounds(488, 527, 140, 35);
		contentPane.add(btnSubmit);
		
		// 7. 所有商品按鈕
		JButton btnMyPro = new JButton("所有商品");
		btnMyPro.setFont(new Font("微軟正黑體", Font.BOLD, 14));
		btnMyPro.setBounds(487, 363, 140, 35);
		contentPane.add(btnMyPro);
		
		// ====== 🛠️ 事件綁定一：點擊所有商品按鈕邏輯 ======
		btnMyPro.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        // 建立全新畫面的實例並顯示出來
		        SupplierAllProductsUI frame = new SupplierAllProductsUI();
		        frame.setVisible(true);
		    }
		});

		// ====== 🛠️ 事件綁定二：圖片上傳與預覽邏輯 ======
		btnUploadImage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));
				
				// 限制檔案格式只能為常用圖檔
				FileNameExtensionFilter filter = new FileNameExtensionFilter("圖片檔案 (*.jpg, *.png, *.jpeg)", "jpg", "png", "jpeg");
				fileChooser.setFileFilter(filter);
				fileChooser.setAcceptAllFileFilterUsed(false);
				
				int result = fileChooser.showOpenDialog(SupplierProductsUI.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					selectedImageFile = fileChooser.getSelectedFile();
					
					// 讀取圖片並進行平滑縮放，使其完美符合預覽框大小 (300 x 200)
					ImageIcon rawIcon = new ImageIcon(selectedImageFile.getAbsolutePath());
					Image scaledImg = rawIcon.getImage().getScaledInstance(
							lblImagePreview.getWidth(), 
							lblImagePreview.getHeight(), 
							Image.SCALE_SMOOTH
					);
					
					// 更新 UI 顯示圖片
					lblImagePreview.setText(""); // 清除原先提示文字
					lblImagePreview.setIcon(new ImageIcon(scaledImg));
				}
			}
		});

		// ====== 🛠️ 事件綁定三：確認提交按鈕邏輯 ======
		btnSubmit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 取得欄位輸入內容
				String productName = txtProductName.getText().trim();
				String productCategory = (String) comboCategory.getSelectedItem();
				String productContent = txtProductContent.getText().trim();
				
				// 表單驗證防呆
				if (productName.isEmpty()) {
					JOptionPane.showMessageDialog(SupplierProductsUI.this, "❌ 請輸入產品名稱！", "提示", JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (productContent.isEmpty()) {
					JOptionPane.showMessageDialog(SupplierProductsUI.this, "❌ 請填寫產品內文！", "提示", JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (selectedImageFile == null) {
					JOptionPane.showMessageDialog(SupplierProductsUI.this, "❌ 請上傳產品圖片！", "提示", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				// 成功收集到所有資料，彈出提示
				String successMessage = String.format(
						"🎉 產品建立成功！\n\n【品名】%s\n【分類】%s\n【內文】%s\n【圖片檔名】%s",
						productName, productCategory, productContent, selectedImageFile.getName()
				);
				JOptionPane.showMessageDialog(SupplierProductsUI.this, successMessage, "成功", JOptionPane.INFORMATION_MESSAGE);
				
				// 💡 這裡之後可以串接您的 MySQL INSERT 語法，把資料與圖片路徑存進資料庫
			}
		});
		
	} // 🔓 這裡正確關閉了 SupplierProductsUI 建構子
} // 🔓 這裡正確關閉了整個類別 (Class)
