#ifndef GATT_SERVER_H
#define GATT_SERVER_H

#include "nrf24/RF24.h" 

#define SIZE 32            // this is the maximum for this example. (minimum is 1)


extern int le_notification_enabled;
extern hci_con_handle_t con_handle;
extern RF24 radio;
extern float acceleration[3], gyro[3], current_temp;
extern uint8_t const profile_data[];
extern char buffer[SIZE + 1];
extern uint8_t tx_address[5];
extern uint8_t rx_address[5];

void packet_handler(uint8_t packet_type, uint16_t channel, uint8_t *packet, uint16_t size);
uint16_t att_read_callback(hci_con_handle_t connection_handle, uint16_t att_handle, uint16_t offset, uint8_t * buffer, uint16_t buffer_size);
int att_write_callback(hci_con_handle_t connection_handle, uint16_t att_handle, uint16_t transaction_mode, uint16_t offset, uint8_t *buffer, uint16_t buffer_size);
void poll_radio(void);

#endif