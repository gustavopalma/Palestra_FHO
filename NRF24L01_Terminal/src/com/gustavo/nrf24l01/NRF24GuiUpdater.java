package com.gustavo.nrf24l01;

public interface NRF24GuiUpdater {

	public void updateRadioData(boolean radioStatus,Integer baudRate,String receiveAddress,String sendAddress, Integer channel, 
			Float frequency, Integer transmissionPower, Integer CRCtype,Integer speedTransmission);
	
	public void updateTXAddr(boolean success);
	public void updateRXAddr(boolean success);
	public void updateReceivedData(String data);
	public void updateReceivedData(byte[] data);
	public void updateSerialPort(Integer baudRate);
	
}