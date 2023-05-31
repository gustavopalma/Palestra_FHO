#include <stdio.h>
#include "btstack.h"
#include "pico/cyw43_arch.h"
#include "pico/btstack_cyw43.h"
#include "pico/stdlib.h"

#include "gatt_service.h"
#include "LOCATION_GATT.h"

#include "nrf24/RF24.h" 


#define HEARTBEAT_PERIOD_MS 50

#define NRF24_CE 21
#define NRF24_IRQ 20
#define NRF24_CSN 17
#define TX_ERR_LED 27
#define TX_SUC_LED 22
#define RF24_SPI_SPEED 10000000

static btstack_timer_source_t heartbeat;
static btstack_packet_callback_registration_t hci_event_callback_registration;

RF24 radio(NRF24_CE, NRF24_CSN); // pin numbers connected to the radio's CE and CSN pins (respectively)

static void heartbeat_handler(struct btstack_timer_source *ts) {
    static uint32_t counter = 0;
    counter++;

    // Update the temp every 50ms
    if (counter % 1 == 0) {
        poll_radio();
        
        if (le_notification_enabled) {
            att_server_request_can_send_now_event(con_handle);
        }
    }

    // Invert the led
    static int led_on = true;
    led_on = !led_on;
    cyw43_arch_gpio_put(CYW43_WL_GPIO_LED_PIN, led_on);

    // Restart timer
    btstack_run_loop_set_timer(ts, HEARTBEAT_PERIOD_MS);
    btstack_run_loop_add_timer(ts);
}

int main()
{
    stdio_init_all();

    // initialize CYW43 driver architecture (will enable BT if/because CYW43_ENABLE_BLUETOOTH == 1)
    if (cyw43_arch_init()) {
        printf("failed to initialise cyw43_arch\n");
        return -1;
    }
   
    gpio_init(NRF24_IRQ);
    gpio_set_dir(NRF24_IRQ, GPIO_IN);
    gpio_set_pulls (NRF24_IRQ,false,true); 

    gpio_init(TX_ERR_LED);   
    gpio_set_dir(TX_ERR_LED, GPIO_OUT);
    gpio_init(TX_SUC_LED);   
    gpio_set_dir(TX_SUC_LED, GPIO_OUT);

    // initialize the transceiver on the SPI bus
    while (!radio.begin()) {
        printf("radio hardware is not responding!!\n");
    }
      
    
    printf("radio begins!!\n");

    radio.setPayloadSize(SIZE); // default value is the maximum 32 bytes
    radio.setAutoAck(true);
    radio.setDataRate(RF24_2MBPS);
    radio.setChannel(76);
    radio.setPALevel(RF24_PA_LOW);

    // set the TX address of the RX node into the TX pipe
    radio.openWritingPipe(tx_address); // always uses pipe 0

    // set the RX address of the TX node into a RX pipe
    radio.openReadingPipe(1, rx_address); // using pipe 1
    radio.startListening(); // put radio in RX mode  
    //radio.maskIRQ(true, true, true); // args = "data_sent", "data_fail", "data_read

    l2cap_init();
    sm_init();

    att_server_init(profile_data, att_read_callback, att_write_callback);    

    // inform about BTstack state
    hci_event_callback_registration.callback = &packet_handler;
    hci_add_event_handler(&hci_event_callback_registration);

    // register for ATT event
    att_server_register_packet_handler(packet_handler);

    // set one-shot btstack timer
    heartbeat.process = &heartbeat_handler;
    btstack_run_loop_set_timer(&heartbeat, HEARTBEAT_PERIOD_MS);
    btstack_run_loop_add_timer(&heartbeat);

    // turn on bluetooth!
    hci_power_control(HCI_POWER_ON);

    radio.setPALevel(RF24_PA_LOW);
    btstack_run_loop_execute();
}