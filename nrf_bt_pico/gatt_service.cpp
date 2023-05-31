#include <stdio.h>
#include "btstack.h"
#include <sstream>

#include "LOCATION_GATT.h"
#include "gatt_service.h"

#define APP_AD_FLAGS 0x06
static uint8_t adv_data[] = {
    // Flags general discoverable
    0x02, BLUETOOTH_DATA_TYPE_FLAGS, APP_AD_FLAGS,
    // Name
    0x0B, BLUETOOTH_DATA_TYPE_COMPLETE_LOCAL_NAME, 'P', 'i', 'c', 'o', ' ', 'W', ' ', 'G', 'P', 'S',
    0x03, BLUETOOTH_DATA_TYPE_COMPLETE_LIST_OF_16_BIT_SERVICE_CLASS_UUIDS, 0x1a, 0x18,
};
static const uint8_t adv_data_len = sizeof(adv_data);

int le_notification_enabled;
hci_con_handle_t con_handle;
float acceleration[3] = {0,0,0}, gyro[3] = {0,0,0},  current_temp = 0;
bool initialized = false;

char buffer[SIZE + 1];     // for the RX node
uint8_t tx_address[5] = {0xFF,0xFF,0xFF,0xFF,0x55};
uint8_t rx_address[5] = {0xFF,0xFF,0xFF,0xFF,0xAA};

float last_latitude;
float last_longitude;
float last_speed;

float latitude;
float longitude;
float speed;

void packet_handler(uint8_t packet_type, uint16_t channel, uint8_t *packet, uint16_t size) {
    UNUSED(size);
    UNUSED(channel);
    bd_addr_t local_addr;
    if (packet_type != HCI_EVENT_PACKET) return;

    uint8_t event_type = hci_event_packet_get_type(packet);
    
    switch(event_type){
        case BTSTACK_EVENT_STATE:
        {
            if (btstack_event_state_get_state(packet) != HCI_STATE_WORKING) return;
            gap_local_bd_addr(local_addr);
            printf(" and running on %s.\n", bd_addr_to_str(local_addr));

            // setup advertisements
            uint16_t adv_int_min = 800;
            uint16_t adv_int_max = 800;
            uint8_t adv_type = 0;
            bd_addr_t null_addr;
            memset(null_addr, 0, 6);
            gap_advertisements_set_params(adv_int_min, adv_int_max, adv_type, 0, null_addr, 0x07, 0x00);
            assert(adv_data_len <= 31); // ble limitation
            gap_advertisements_set_data(adv_data_len, (uint8_t*) adv_data);
            gap_advertisements_enable(1);

            poll_radio();
        }
        break;
        case HCI_EVENT_DISCONNECTION_COMPLETE:
            le_notification_enabled = 0;
        break;
    }
}

uint16_t att_read_callback(hci_con_handle_t connection_handle, uint16_t att_handle, uint16_t offset, uint8_t * buffer, uint16_t buffer_size) {
    UNUSED(connection_handle);   
    //No readable attributes on this project
    return 0;
}

int att_write_callback(hci_con_handle_t connection_handle, uint16_t att_handle, uint16_t transaction_mode, uint16_t offset, uint8_t *buffer, uint16_t buffer_size) {
    UNUSED(transaction_mode);
    UNUSED(offset);
    UNUSED(buffer_size);
    int32_t x = 0;
    float *aux;
     switch (att_handle) {
        case ATT_CHARACTERISTIC_ORG_BLUETOOTH_CHARACTERISTIC_LOCATION_AND_SPEED_01_VALUE_HANDLE:
            x = little_endian_read_32(buffer, 0);
            aux = (float *) (&x);
            speed = *aux;
            printf("value: %.5f\n",speed );
        break;

       case ATT_CHARACTERISTIC_ORG_BLUETOOTH_CHARACTERISTIC_LONGITUDE_01_VALUE_HANDLE:
            x = little_endian_read_32(buffer, 0);
            aux = (float *) (&x);
            longitude = *aux;
            printf("value: %.5f\n",longitude );
        break;

        case ATT_CHARACTERISTIC_ORG_BLUETOOTH_CHARACTERISTIC_LATITUDE_01_VALUE_HANDLE:
            x = little_endian_read_32(buffer, 0);
            aux = (float *) (&x);
            latitude = *aux;
            printf("value: %.5f\n",latitude );
        break;
    }
    
    return 0;
}

void poll_radio(void) { 
    if (radio.available()) {
        uint8_t teste[32];
        radio.read(&teste, 32);
        printf("--------read------\n");
        printf("read: %s\n", teste);
        printf("--------read------\n");
    }
   
    bool need_send = ( last_speed != speed ) | ( last_latitude != latitude ) | ( last_longitude != longitude );
    if(need_send) {
        // additional setup specific to the node's role
        radio.stopListening(); // put radio in TX mode 

        bool wrote = true;
        int count = 5;

        if ( last_latitude != latitude ) {
            count = 5;
            snprintf ( buffer, 32, "{{\"LAT\": %.5f}", latitude);          
            buffer[0] = 32;
            //buffer[0] = strlen(buffer); just for USB receiver USE
            printf("data: %s\n", buffer);
            bool aux = true;
            while( count > 0 ) {
                aux = radio.write(&buffer, 32);
                if( aux ) {
                    break;
                }
                sleep_ms(2);
                count--;
            }
            wrote &= aux;
            last_latitude = latitude;
        }

        if ( last_longitude != longitude ) {
            count = 5;
            snprintf ( buffer, 32, "{{\"LNG\": %.5f}", longitude);          
            buffer[0] = 32;
            //buffer[0] = strlen(buffer); just for USB receiver USE
            printf("data: %s\n", buffer);
            bool aux = true;
            while( count > 0 ) {
                aux = radio.write(&buffer, 32);
                if( aux ) {
                    break;
                }
                sleep_ms(2);
                count--;
            }
            wrote &= aux;
            last_longitude = longitude;
        }

        if ( last_speed != speed ) {
            count = 5;
            snprintf ( buffer, 32, "{{\"VEL\": %.5f}", speed);
            buffer[0] = 32;
            //buffer[0] = strlen(buffer); just for USB receiver USE
            printf("data: %s\n", buffer);
            bool aux = true;
            while( count > 0 ) {
                aux = radio.write(&buffer, 32);
                if( aux ) {
                    break;
                }
                sleep_ms(2);
                count--;
            }
            wrote &= aux;
            last_speed = speed;
        }

        if (!wrote) {
            gpio_put(27, 1);
            gpio_put(22, 0);
        } else {
            gpio_put(27, 0);
            gpio_put(22, 1);
        }

       radio.startListening(); // put radio in RX mode  
    }
 }