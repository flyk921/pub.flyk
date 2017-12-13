package pub.flyk.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import pub.flyk.utils.CommonUtil;
import pub.flyk.utils.ConfigUtil;
import pub.flyk.utils.LoggerFactory;

public class ClientFrame extends JFrame{
	
	private static final long serialVersionUID = 9092857137087708777L;

	private static Logger logger = LoggerFactory.getLogger(ClientFrame.class);
	
	private FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER, 30, 15);
	
	private static String title = "flynet";
	
	private SystemTray systemTray = null;

	private TrayIcon trayIcon = null;

	private Image logo = null;

	private int frameWidth = 480;
	
	private int frameHeight = 270;
	
	private String serverHost = "";
	
	private String serverPort = "";
	
	private String password = "";
	
	private String proxyPort = "";

	private JTextField serverHostField = null;
	
	private JTextField serverPortField = null;
	
	private JTextField proxyPortField = null;
	
	private JPasswordField passwordField = null;
	
	private Dimension btnDimension = new Dimension(80, 28);
	
	private Dimension fieldDimension = new Dimension(180, 24);
	
	private Dimension infoLabelDimension = new Dimension(100, 24);
	
	private Font tipsFont = new Font("SimSun", Font.BOLD, 25);
	private Color tipsFontColor = new Color(255, 255, 255);
	
	private Font infoFont = new Font("SimSun", Font.BOLD, 15);
	private Color infoFontColor = new Color(255, 255, 255);
	
	private Font infoFieldFont = new Font("SimSun", Font.PLAIN, 15);
	private Color infoFieldFontColor = new Color(255, 255, 255);
	private Color infoFieldBgColor = new Color(55, 127, 177);
	
	private Font btnFont = new Font("SimSun", Font.PLAIN, 14);
	private Color btnFontColor = new Color(77, 122, 141);
	private Color btnBgColor = new Color(186, 226, 251);
	
//	private Color bgColor = new Color(180, 205, 230);
//	private Color bgColor = new Color(238, 238, 238);
	private Color bgColor = new Color(81, 162, 207);
	
	
	public ClientFrame() {
		super(title);
		init();
	}
	
	private void init() {
		initTray();
		initFrame();
		initTips();
		initPanel();
		initOption();
		setVisible(true);
	}
	
	private void initTray() {
		try {
			logo = Toolkit.getDefaultToolkit().getImage("image/logo.png");
			
			if (!SystemTray.isSupported()) {
				return;
			}
			
			trayIcon = new TrayIcon(logo, title, null);
			trayIcon.setImageAutoSize(true);
			trayIcon.addActionListener(listener -> {
				setState(NORMAL);
				setVisible(true);
			});
			
			systemTray = SystemTray.getSystemTray();
			
			systemTray.add(trayIcon);
			
		} catch (Exception e) {
			logger.warning("添加系统托盘图标出错：" + e.getMessage());
		}
	}
	
	private void initFrame() {
		try {
			setSize(frameWidth, frameHeight);
			
			setIconImage(logo);
			
			setLocationRelativeTo(null);
			
			setResizable(false);
			
			this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			
			BorderLayout borderLayout = new BorderLayout();
			setLayout(borderLayout);
			getContentPane().setBackground(bgColor);
			
		} catch (Exception e) {
			logger.warning("初始化窗口出错：" + e.getMessage());
		}
	}
	
	private void initTips() {
		try {
			JPanel tipsPanel = new JPanel();
			tipsPanel.setLayout(flowLayout);
			tipsPanel.setBackground(bgColor);
			JLabel label = new JLabel("Welcome To Use Flynet!");
			label.setFont(tipsFont);
			label.setForeground(tipsFontColor);
			tipsPanel.add(label);
			add(tipsPanel,BorderLayout.NORTH);
		} catch (Exception e) {
			logger.warning("初始化提示信息出错：" + e.getMessage());
		}
	}

	private void initPanel() {
		try {
			loadConfig();
			
			JPanel infoPanel = new JPanel();
			infoPanel.setLayout(new GridLayout(4, 1, 0, 0));
			infoPanel.setBackground(bgColor);
			infoPanel.add(initItemPanel("ServerHost",serverHostField));
			infoPanel.add(initItemPanel("ServerPort",serverPortField));
			infoPanel.add(initItemPanel("Password",passwordField));
			infoPanel.add(initItemPanel("ProxyPort",proxyPortField));
			
			add(infoPanel, BorderLayout.CENTER);
			
		} catch (Exception e) {
			logger.warning("初始化信息面板出错：" + e.getMessage());
		}
		
		
	}

	private void loadConfig() {
		serverHostField  = new JTextField();
		serverPortField  = new JTextField();
		proxyPortField  = new JTextField();
		passwordField  = new JPasswordField();
		
		Map<String, String> clientConfig = ConfigUtil.getClientConfig();
		if (clientConfig != null) {
			serverHostField.setText(CommonUtil.null2String(clientConfig.get("serverHost")));
			serverPortField.setText(CommonUtil.null2String(clientConfig.get("serverPort")));
			passwordField.setText(CommonUtil.null2String(clientConfig.get("password")));
			proxyPortField.setText(CommonUtil.null2String(clientConfig.get("proxyPort")));
		}
	}
	
	
	private JPanel initItemPanel(String labelName, JTextField field) {
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
		jPanel.setBackground(bgColor);
		
		field.setPreferredSize(fieldDimension);
		field.setFont(infoFieldFont);
		field.setForeground(infoFieldFontColor);
		field.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
		field.setBackground(infoFieldBgColor);
		
		JLabel label = new JLabel(labelName);
		label.setPreferredSize(infoLabelDimension);
		label.setHorizontalAlignment(JLabel.LEFT);
		label.setFont(infoFont);
		label.setForeground(infoFontColor);
		
		jPanel.add(label);
		jPanel.add(field);
		return jPanel;
	}
	
	private void initOption() {
		try {
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(flowLayout);
			buttonPanel.setBackground(bgColor);
			
			JButton exitButton = initBtn("exit");
			exitButton.addActionListener(l -> {
				setVisible(false);
				if (systemTray != null && trayIcon != null) {
					systemTray.remove(trayIcon);
				}
				System.exit(0);
			});
			
			JButton startButton = initBtn("start");
			startButton.addActionListener(l -> {
				saveOrUpdateConfig();
				start();
				setVisible(false);
			});
			
			JButton cancelButton = initBtn("cancel");
			cancelButton.addActionListener(l -> {
				setVisible(false);
			});
			
			buttonPanel.add(exitButton);
			buttonPanel.add(startButton);
			buttonPanel.add(cancelButton);
			
			add(buttonPanel, BorderLayout.SOUTH);
			
		} catch (Exception e) {
			logger.warning("初始化操作按钮出错：" + e.getMessage());
		}
		
	}
	private JButton initBtn(String title) {
		JButton btn = new JButton(title);
		btn.setPreferredSize(btnDimension);
		btn.setFont(btnFont);
		btn.setForeground(btnFontColor);
		btn.setBackground(btnBgColor);
		btn.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		return btn;
	}

	private void saveOrUpdateConfig() {
		serverHost = CommonUtil.null2String(serverHostField.getText());
		serverPort = CommonUtil.null2String(serverPortField.getText());
		password = CommonUtil.null2String(String.valueOf(passwordField.getPassword()));
		proxyPort = CommonUtil.null2String(proxyPortField.getText());
		
		Map<String, String> clientConfig = new HashMap<String, String>();
		clientConfig.put("serverHost", serverHost);
		clientConfig.put("serverPort", serverPort);
		clientConfig.put("password", password);
		clientConfig.put("proxyPort", proxyPort);
		ConfigUtil.saveClientConfig(clientConfig);
	}

	

	public void start() {

//		if (client != null && !client.isKill()) {
//
//			if (serverHost.equals(client.getServerHost()) && serverPort.equals(String.valueOf(client.getServerPort())) && password.equals(client.getPassword()) && proxyPort.equals(String.valueOf(client.getListenPort()))) {
//
//				return;
//
//			} else {
//
//				client.kill();
//
//			}
//
//		}
//
//		client = new Client(serverHost, serverPort, password, proxyPort);
//
//		client.start();


	}
	

	public static void main(String[] args) throws IOException {
		new ClientFrame();
	}

}
