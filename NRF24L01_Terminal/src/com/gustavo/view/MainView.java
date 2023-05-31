package com.gustavo.view;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.MaskFormatter;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import com.gustavo.nrf24l01.NRF24ATController;
import com.gustavo.nrf24l01.NRF24GuiUpdater;

import javax.swing.BoxLayout;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import javax.swing.JFormattedTextField;
import javax.swing.SwingConstants;
import javax.swing.JEditorPane;

public class MainView extends JFrame implements ActionListener, NRF24GuiUpdater {

	
	private static final long serialVersionUID = -4263641591580398134L;
	
	private JPanel contentPane;
	private SerialPort serialPort;
	private JComboBox<String> comboBox;
	private NRF24ATController radioController;
	private JTextField txtStatus;
	private JTextField txtBaudRate;
	private JTextField txtRecAddr;
	private JTextField txtSendAddr;
	private JTextField txtChannel;
	private JTextField txtFreq;
	private JTextField txtTransPower;
	private JTextField txtCRCType;
	private JTextField txtTransSpeed;
	private JButton btnQueryRadio;
	private JFormattedTextField txtChangeTXAddr;
	private JFormattedTextField txtChangeRXAddr;
	private JComboBox<String> jcboBaudRate;
	private JComboBox<String> jcboTaxaTrans;
	private JTextField txtChangeFreq;
	private JComboBox<String> jcboCRC;
	private JTextField txtChangeCanal;
	private JTextField txtDataToSend;
	private JEditorPane RXData;
	private HTMLEditorKit text;
	private JComboBox<String> jcboSerialBaud;
	private JButton btnTXChangeAddress;
	private JButton btnRXChangeAddr;
		
	/**
	 * Create the frame.
	 */
	public MainView() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1156, 580);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Configura\u00E7\u00F5es da Porta Serial", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JLabel lblNewLabel = new JLabel("Porta Serial");
		panel.add(lblNewLabel);
		
		comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>(SerialPortList.getPortNames()));
		panel.add(comboBox);
		
		JButton btnStart = new JButton("Iniciar");
		btnStart.setActionCommand("start");
		btnStart.addActionListener(this);
		
		JLabel lblNewLabel_18 = new JLabel("BaudRate:");
		panel.add(lblNewLabel_18);
		
		jcboSerialBaud = new JComboBox<String>();
		jcboSerialBaud.setModel(new DefaultComboBoxModel<String>(new String[] {"Selecione:", String.valueOf(SerialPort.BAUDRATE_4800),
				String.valueOf(SerialPort.BAUDRATE_4800),String.valueOf(SerialPort.BAUDRATE_9600),String.valueOf(SerialPort.BAUDRATE_14400),
				String.valueOf(SerialPort.BAUDRATE_19200),String.valueOf(SerialPort.BAUDRATE_38400),String.valueOf(SerialPort.BAUDRATE_57600),
				String.valueOf(SerialPort.BAUDRATE_115200)}));
		panel.add(jcboSerialBaud);
		panel.add(btnStart);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Status Atuais do Radio", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		contentPane.add(panel_1, BorderLayout.WEST);
		
		btnQueryRadio = new JButton("Consultar Rádio");
		btnQueryRadio.setEnabled(false);
		btnQueryRadio.setActionCommand("query");
		btnQueryRadio.addActionListener(this);
		panel_1.add(btnQueryRadio);
		
		JLabel lblNewLabel_1 = new JLabel("Status do Rádio:");
		panel_1.add(lblNewLabel_1);
		
		txtStatus = new JTextField();
		txtStatus.setEditable(false);
		panel_1.add(txtStatus);
		txtStatus.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("BaudRate:");
		panel_1.add(lblNewLabel_2);
		
		txtBaudRate = new JTextField();
		txtBaudRate.setEditable(false);
		panel_1.add(txtBaudRate);
		txtBaudRate.setColumns(10);
		
		JLabel lblNewLabel_3 = new JLabel("Endereço de Recepção:");
		panel_1.add(lblNewLabel_3);
		
		txtRecAddr = new JTextField();
		txtRecAddr.setEditable(false);
		panel_1.add(txtRecAddr);
		txtRecAddr.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("Endereço de Envio:");
		panel_1.add(lblNewLabel_4);
		
		txtSendAddr = new JTextField();
		txtSendAddr.setEditable(false);
		panel_1.add(txtSendAddr);
		txtSendAddr.setColumns(10);
		
		JLabel lblNewLabel_5 = new JLabel("Canal:");
		panel_1.add(lblNewLabel_5);
		
		txtChannel = new JTextField();
		txtChannel.setEditable(false);
		panel_1.add(txtChannel);
		txtChannel.setColumns(10);
		
		JLabel lblNewLabel_6 = new JLabel("Frequência:");
		panel_1.add(lblNewLabel_6);
		
		txtFreq = new JTextField();
		txtFreq.setEditable(false);
		panel_1.add(txtFreq);
		txtFreq.setColumns(10);
		
		JLabel lblNewLabel_7 = new JLabel("Potência de Transmissão:");
		panel_1.add(lblNewLabel_7);
		
		txtTransPower = new JTextField();
		txtTransPower.setEditable(false);
		panel_1.add(txtTransPower);
		txtTransPower.setColumns(10);
		
		JLabel lblNewLabel_8 = new JLabel("Tipo de CRC:");
		panel_1.add(lblNewLabel_8);
		
		txtCRCType = new JTextField();
		txtCRCType.setEditable(false);
		panel_1.add(txtCRCType);
		txtCRCType.setColumns(10);
		
		JLabel lblNewLabel_9 = new JLabel("Taxa de Transmissão:");
		panel_1.add(lblNewLabel_9);
		
		txtTransSpeed = new JTextField();
		txtTransSpeed.setEditable(false);
		panel_1.add(txtTransSpeed);
		txtTransSpeed.setColumns(10);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "A\u00E7\u00F5es", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2 .setLayout(new BorderLayout(0, 0));
		contentPane.add(panel_2, BorderLayout.CENTER);
		
		JPanel panel_3 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_3.getLayout();
		flowLayout.setVgap(1);
		flowLayout.setHgap(1);
		panel_3.setBorder(new TitledBorder(null, "Alterar Endere\u00E7os", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.add(panel_3, BorderLayout.NORTH);
		
		JLabel lblNewLabel_10 = new JLabel("Envio:");
		panel_3.add(lblNewLabel_10);
		
		try {
			txtChangeTXAddr = new JFormattedTextField(new MaskFormatter("0xHH,0xHH,0xHH,0xHH,0xHH"));
			panel_3.add(txtChangeTXAddr);
			((MaskFormatter) txtChangeTXAddr.getFormatter()).setPlaceholderCharacter('F');
			txtChangeTXAddr.setColumns(15);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		btnTXChangeAddress = new JButton("Gravar");
		btnTXChangeAddress.addActionListener(this);
		btnTXChangeAddress.setActionCommand("writeTXAddr");
		panel_3.add(btnTXChangeAddress);
		
		
		JLabel lblNewLabel_11 = new JLabel("Recepção");
		panel_3.add(lblNewLabel_11);
		
		try {
			txtChangeRXAddr = new JFormattedTextField(new MaskFormatter("0xHH,0xHH,0xHH,0xHH,0xHH"));
			panel_3.add(txtChangeRXAddr);
			((MaskFormatter) txtChangeRXAddr.getFormatter()).setPlaceholderCharacter('F');
			txtChangeRXAddr.setColumns(15);
			

		} catch (ParseException e) {
			e.printStackTrace();
		}
		btnRXChangeAddr = new JButton("Gravar");
		btnRXChangeAddr.addActionListener(this);
		btnRXChangeAddr.setActionCommand("writeRXAddr");
		panel_3.add(btnRXChangeAddr);
		
		JPanel panel_4 = new JPanel();
		panel_4.setLayout(new BoxLayout(panel_4, BoxLayout.Y_AXIS));
		panel_4.setBorder(new TitledBorder(null, "Configurar", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(panel_4,BorderLayout.EAST);
		
		JLabel lblNewLabel_12 = new JLabel("BaudRate:");
		panel_4.add(lblNewLabel_12);
		
		jcboBaudRate = new JComboBox<String>();
		jcboBaudRate.setModel(new DefaultComboBoxModel<String>(new String[] {"Selecione:","4800","9600","14400","19200","38400","57600","115200"}));
		panel_4.add(jcboBaudRate);
		
		JLabel lblNewLabel_13 = new JLabel("Taxa de Transmissão:");
		lblNewLabel_13.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(lblNewLabel_13);
		
		jcboTaxaTrans = new JComboBox<String>();
		jcboTaxaTrans.setModel(new DefaultComboBoxModel<String>(new String[] {"Selecione:","250","1","2"}));
		panel_4.add(jcboTaxaTrans);
		
		JLabel lblNewLabel_14 = new JLabel("Frequência:");
		panel_4.add(lblNewLabel_14);
		
		txtChangeFreq = new JTextField();
		txtChangeFreq.setText("2.400");
		txtChangeFreq.setEditable(false);
		panel_4.add(txtChangeFreq);
		txtChangeFreq.setColumns(10);
		
		JLabel lblNewLabel_15 = new JLabel("Tamanho do CRC:");
		lblNewLabel_15.setAlignmentY(0.0f);
		lblNewLabel_15.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(lblNewLabel_15);
		
		jcboCRC = new JComboBox<String>();
		jcboCRC.setModel(new DefaultComboBoxModel<String>(new String[] {"Selecione:","8","16"}));
		panel_4.add(jcboCRC);
		
		JLabel lblNewLabel_16 = new JLabel("Canal:");
		panel_4.add(lblNewLabel_16);
		
		txtChangeCanal = new JTextField();
		panel_4.add(txtChangeCanal);
		txtChangeCanal.setColumns(10);
		
		JButton btnUpdateParamData = new JButton("Gravar");
		btnUpdateParamData.addActionListener(this);
		btnUpdateParamData.setActionCommand("updatePaaramData");
		panel_4.add(btnUpdateParamData);
		
		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(null, "Enviar Dadaos", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.add(panel_5,BorderLayout.SOUTH);
		
		JLabel lblNewLabel_17 = new JLabel("Mensagem");
		panel_5.add(lblNewLabel_17);
		
		txtDataToSend = new JTextField();
		txtDataToSend.setHorizontalAlignment(SwingConstants.TRAILING);
		panel_5.add(txtDataToSend);
		txtDataToSend.setColumns(25);
		
		JButton btnSendData = new JButton("Enviar");
		btnSendData.setActionCommand("sendData");
		btnSendData.addActionListener(this);
		panel_5.add(btnSendData);
		
		JPanel panel_6 = new JPanel();
		panel_6.setBorder(new TitledBorder(null, "Dados Recebidos", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_6.setLayout(new BorderLayout(0, 0));
		panel_2.add(panel_6, BorderLayout.CENTER);
		
		text = new HTMLEditorKit();
		
		RXData = new JEditorPane();
		RXData.setEditable(false);
		RXData.setEditorKit(text);
		RXData.setText("<html><body id='body'></body></html>");
		
		
		panel_6.add( new JScrollPane(RXData), BorderLayout.CENTER);
		
	
		
		setLocationRelativeTo(null);
		setTitle("NRF24L01 - Console");
		
	}
	private boolean initSerial() {
		
			if (jcboSerialBaud.getSelectedIndex() <= 0) {
				JOptionPane.showMessageDialog(null, "Selecione um BaudRate para iniciar");
				return false;
			}
			if(serialPort != null) {
				if(!serialPort.isOpened()) {
					try {					
						if(!serialPort.openPort()) {
							JOptionPane.showMessageDialog(null, "Falha ao Abrir Porta Serial!");
							return false;
						}
						serialPort.setParams(Integer.parseInt((String) jcboSerialBaud.getSelectedItem()), SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
						return true;
					} catch (HeadlessException | SerialPortException e1) {
						e1.printStackTrace();
					}
				} else {
					try {
						serialPort.closePort();
						return false;
					} catch (SerialPortException e1) {
						e1.printStackTrace();
					}
				}
			} else {
				serialPort = new SerialPort((String) comboBox.getSelectedItem());
				try {
					if(!serialPort.openPort()) {
						JOptionPane.showMessageDialog(null, "Falha ao Abrir Porta Serial!");
						return false;
					}
					serialPort.setParams(Integer.parseInt((String) jcboSerialBaud.getSelectedItem()), SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
					return true;
				} catch (SerialPortException e1) {
					e1.printStackTrace();
				}
			}
			return false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("start")) {
			if (!initSerial()) {
				JButton s = (JButton) e.getSource();
				s.setText("Iniciar");
				btnQueryRadio.setEnabled(false);
			} else {
				btnQueryRadio.setEnabled(true);
				JButton s = (JButton) e.getSource();
				s.setText("Desconectar");
				
				try {
					if(this.radioController == null)
						this.radioController = new NRF24ATController(this.serialPort, this,Integer.parseInt((String) jcboSerialBaud.getSelectedItem()));
				} catch (SerialPortException e1) {
					e1.printStackTrace();
				}
			}
			
		} else if (e.getActionCommand().equals("query")) {
			this.radioController.queryRadio();
		} else if (e.getActionCommand().equals("writeTXAddr")) {
			this.radioController.changeTXAddr(txtChangeTXAddr.getText());	
		} else if (e.getActionCommand().equals("writeRXAddr")) {
			this.radioController.changeRXAddr(txtChangeRXAddr.getText());
		} else if (e.getActionCommand().equals("updatePaaramData")) {
			System.out.println("To be Implemented");
		} else if (e.getActionCommand().equals("sendData")) {
			this.radioController.sendData(txtDataToSend.getText());			
		}
		
	}

	@Override
	public void updateRadioData(boolean radioStatus, Integer baudRate, String receiveAddress, String sendAddress,
			Integer channel, Float frequency, Integer transmissionPower, Integer CRCtype, Integer speedTransmission) {
		this.txtStatus.setText(radioStatus ? "OK" : "Erro");
		this.txtBaudRate.setText(String.valueOf(baudRate));
		this.txtRecAddr.setText(receiveAddress);
		this.txtSendAddr.setText(sendAddress);
		this.txtChannel.setText(String.valueOf(channel));
		this.txtFreq.setText(String.valueOf(frequency));
		this.txtTransPower.setText(String.valueOf(transmissionPower));
		this.txtCRCType.setText(String.valueOf(CRCtype));
		this.txtTransSpeed.setText(String.valueOf(speedTransmission));
		this.jcboBaudRate.setSelectedItem(String.valueOf(baudRate));
		this.jcboTaxaTrans.setSelectedItem(String.valueOf(speedTransmission));
		this.txtChangeCanal.setText(String.valueOf(channel));
		this.jcboCRC.setSelectedItem(CRCtype);
	}

	@Override
	public void updateTXAddr(boolean success) {
		String message = success ? "Endereço de Envio Alterado com Sucesso!" : "Falha ao Alterar Endereço";
		JOptionPane.showMessageDialog(null, message);
		
	}

	@Override
	public void updateRXAddr(boolean success) {
		String message = success ? "Endereço de Recepção Alterado com Sucesso!" : "Falha ao Alterar Endereço";
		JOptionPane.showMessageDialog(null, message);
		
	}

	@Override
	public void updateReceivedData(String data) {
		  HTMLDocument doc = (HTMLDocument) RXData.getDocument();
	      Element elem = doc.getElement("body");
	      String htmlText = String.format("<p>%s</p>", data);
	      try {
	        doc.insertBeforeEnd(elem, htmlText);
	      } catch (BadLocationException | IOException ex) {
	        ex.printStackTrace();
	      }
		
	}

	@Override
	public void updateReceivedData(byte[] data) {
		System.out.println("To be Implemented");
	}
	@Override
	public void updateSerialPort(Integer baudRate) {
		System.out.println("To be Implemented");
	}
}
