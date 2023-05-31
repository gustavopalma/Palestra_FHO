package com.gustavo.nrf24l01;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class NRF24ATController implements SerialPortEventListener{

	private SerialPort serialPort;
	private String lastCommand;
	private Map<String, Runnable> commands;
	private NRF24GuiUpdater updateCallback;
		
	private boolean radioStatus;
	private Integer baudRate;
	private String receiveAddress;
	private String sendAddress;
	private Integer channel;
	private Float frequency;
	private Integer transmissionPower;
	private Integer CRCtype;
	private Integer speedTransmission;
	private Map<Integer,Integer> selectedBaudResp;
	private Map<Integer,Integer> dataResp;
	private Integer serialPortBaudRate;
	
	//To be used during the processing of TX or RX data changes
	private String auxAddr;
	
	private Integer auxBaud;
	
	private static final String QUERY_RADIO_DATA = "AT?";
	private static final String WRITE_TX_ADDR = "AT+TXA=";
	private static final String WRITE_RX_ADDR = "AT+RXA=";
	private static final String WRITE_BAUD = "AT+BAUD=";
	private static final String OK = "OK";
	private static final Float FREQ_CONST = 2400f; 
	
	public NRF24ATController(SerialPort serialPort,NRF24GuiUpdater updateCallback, Integer serialPortBaudRate) throws SerialPortException {
		this.serialPort = serialPort;
		this.serialPort.addEventListener(this);
		this.commands = new HashMap<>();
		this.createATTCommands();
		this.updateCallback = updateCallback;
		this.selectedBaudResp = new HashMap<>();
		this.createBaudNExpecBytes();
		this.dataResp = new HashMap<>();
		this.createBaudQueryExpectedBytes();
		this.serialPortBaudRate = serialPortBaudRate;
	}
	
	private Runnable processChangeTXData() {
		return new Runnable() {
			
			@Override
			public void run() {
				try {
					byte[] aux = serialPort.readBytes(57);
					String ret = filterReadableChars(aux);
			        updateCallback.updateTXAddr(ret.contains(auxAddr));
					queryRadio();
				} catch (SerialPortException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
			}
		};
	}
	private Runnable processChangeRXData() {
		return new Runnable() {
			
			@Override
			public void run() {
				try {
					byte[] aux = serialPort.readBytes(57);
					String ret = filterReadableChars(aux);
					updateCallback.updateRXAddr(ret.contains(auxAddr));
					queryRadio();
				} catch (SerialPortException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
			}
		};
	}
	
	private Runnable processChangeBaudRate(){
		return new Runnable() {
			@Override
			public void run() {
				try {
					byte[] aux = serialPort.readBytes(selectedBaudResp.get(auxBaud));
					String ret = filterReadableChars(aux);
					updateCallback.updateSerialPort(Integer.parseInt(ret));
					auxBaud = 0;
				} catch (SerialPortException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
			}
		};
	}
	
	private Runnable processRadioData() {
		return new Runnable() {
			
			@Override
			public void run() {
				try {
					byte[] aux = serialPort.readBytes(dataResp.get(serialPortBaudRate));
				    String tmpLines[] = filterReadableChars(aux).split("\n");
				    int param = 0;
				    for (String line : tmpLines) {
				    	if(!line.trim().isEmpty()) {
				    		line = line.trim().replace("\n", "").replace("\r", "");
				    		if(param == 0) {
				    			radioStatus = line.equals(OK);
				    			param++;
				    			continue;
				    		}
				    		if(param == 1) {
				    			baudRate = Integer.parseInt(line);
				    			param++;
				    			continue;
				    		}
				    		if(param == 2) {
				    			receiveAddress = line;
				    			param++;
				    			continue;
				    		}
				    		if(param == 3) {
				    			sendAddress = line;
				    			param++;
				    			continue;
				    		}
				    		if(param == 4) {
				    			String freq = line.replace("H", "").replace("z", "").replace('G','\0');
				    			channel = (int) ((Float.parseFloat(freq) * 1000 - FREQ_CONST));
				    			frequency =  (Float.parseFloat(freq)) - ((float) (channel / 1000)) ;
				    			param++;
				    			continue;
				    		}
				    		if(param == 5) {
				    			CRCtype = Integer.parseInt(line.replace("CRC", ""));
				    			param++;
				    			continue;
				    		}
				    		if(param == 6) {
				    			transmissionPower = Integer.parseInt(line.replace("dBm", ""));
				    			param++;
				    			continue;
				    		}
				    		if(param == 7) {
				    			speedTransmission = Integer.parseInt(line.replace("Mbps", ""));
				    			param++;
				    			continue;
				    		}
				    	}
				    }	
				    updateCallback.updateRadioData(radioStatus, baudRate, receiveAddress, sendAddress, channel, frequency, transmissionPower, CRCtype, speedTransmission);
				} catch (SerialPortException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				lastCommand = null;
			}
		};
		
	}
	
	private String filterReadableChars(byte[] buffer) throws UnsupportedEncodingException {
		byte[] result = new byte[buffer.length];
		for (int i = 0,j = 0; i < buffer.length; i++ ) {
			byte e = buffer[i];
				if( (e >' ' && e <= '@') || (e >= '0' && e <= '9') || (e >= 'a' && e <= 'z') || (e >= 'A' && e <= 'Z') || (e == '\n') ) {
					result[j] = buffer[i];
					j++;
				}
		}
		return new String(result, "UTF-8").trim();
	}
	
	private void createATTCommands() {
		this.commands.put(QUERY_RADIO_DATA, this.processRadioData());
		this.commands.put(WRITE_TX_ADDR.replace("=", ""), this.processChangeTXData());
		this.commands.put(WRITE_RX_ADDR.replace("=", ""), this.processChangeRXData());
		this.commands.put(WRITE_BAUD.replace("=", ""), this.processChangeBaudRate());
		
	}
	
	private void createBaudQueryExpectedBytes() {
		this.dataResp.put(4800,125);
		this.dataResp.put(9600,214);
		this.dataResp.put(14400,215);
		this.dataResp.put(19200,215);
		this.dataResp.put(38400,215);
		this.dataResp.put(57600,215);
		this.dataResp.put(115200,216);
	}
	
	private void createBaudNExpecBytes() {
		//   1      2       3       4       5       6        7
		//{"4800","9600","14400","19200","38400","57600","115200"}
		this.selectedBaudResp.put(1,33);
		this.selectedBaudResp.put(2,33);
		this.selectedBaudResp.put(3,34);
		this.selectedBaudResp.put(4,34);
		this.selectedBaudResp.put(5,34);
		this.selectedBaudResp.put(6,34);
		this.selectedBaudResp.put(7,38);
	}
	
	public void sendData(String data) {
		byte dataToSend[] = new byte[data.length() + 1];
		
		dataToSend[0] = (byte) data.length();
		for (int i = 0; i < data.length(); i++) {
			dataToSend[i + 1] = (byte) data.charAt(i);
		}
		try {
			this.serialPort.writeBytes(dataToSend);
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void sendCommand(String command) {
		try {
			serialPort.writeBytes(command.getBytes());
			if (command.contains("=")) {
				this.lastCommand = command.split("=")[0];
			} else {
				this.lastCommand = command;
			}
		} catch (SerialPortException e1) {
			e1.printStackTrace();
		}
	}

	public void changeTXAddr(String TXAddr) {
		this.sendCommand(WRITE_TX_ADDR + TXAddr);
		this.auxAddr = TXAddr;
	}
	public void changeRXAddr(String RXAddr) {
		this.sendCommand(WRITE_RX_ADDR + RXAddr);
		this.auxAddr = RXAddr;
	}
	
	public void updateRadioParams(Integer baudRate, String receiveAddress, String sendAddress,
			Integer channel, Float frequency, Integer transmissionPower, Integer CRCtype, Integer speedTransmission) {
		this.sendCommand(WRITE_BAUD + String.valueOf(baudRate));
		this.auxBaud = baudRate;
		
	}
	
	public void queryRadio() {
		this.sendCommand(QUERY_RADIO_DATA);
	}

	@Override
	public void serialEvent(SerialPortEvent serialPortEvent) {
		if(serialPortEvent.isRXCHAR() || serialPortEvent.isRXFLAG()) {
			if ( lastCommand != null) {
				this.commands.get(this.lastCommand).run();
			} else {
				String x;
				try {
					x = this.serialPort.readString();
					if(updateCallback != null)
						updateCallback.updateReceivedData(x);
					System.out.println(x);
				} catch (SerialPortException e) {
					e.printStackTrace();
				}
			
			}
		} 
	}
	
}
