# Palestra_FHO
Exemplos utilizados durante a palestra no 18º Congresso Científico


## NRF24L01_Terminal
 Software de Exemplo, desenvolvido em Java, útil no desenvolvimento de aplicações com o módulo NRF2401 utilizando o [este adaptador USB -> Serial]([https://produto.mercadolivre.com.br/MLB-3156300318-adaptador-usb-para-modulo-nrf24l01-_JM?matt_tool=40343894&matt_word=&matt_source=google&matt_campaign_id=14303413655&matt_ad_group_id=133855953276&matt_match_type=&matt_network=g&matt_device=c&matt_creative=584156655519&matt_keyword=&matt_ad_position=&matt_ad_type=pla&matt_merchant_id=720107929&matt_product_id=MLB3156300318&matt_product_partition_id=1801030559419&matt_target_id=aud-1966009190540:pla-1801030559419&gclid=CjwKCAjwvdajBhBEEiwAeMh1U4ARCDPCMEgAJB2K-79i-jsr0mjDibK3X6WgYShjBLIL6mZ-LwiQlBoC6z0QAvD_BwE]) 
 O Software foi baseado na implementação [destes Comandos]([url](https://github.com/carmelopellegrino/rf24-serial-docs))
 
## BTTracker
Aplicativo Android Desenvolvido para se conectar a Raspberry Pi Pico W, via Bluetooth BLE, a receber do smartphone os dados de localização, para então envia-los através do NRF2401 para a outra Placa;

## BTTracker-Press
Aplicativo Android Desenvolvido com as mesmas caractrísticas do BTTracker original, contudo este é capaz de reproduzir uma seguência de rotas pré gravada, utilizado durante a apresentação para evitar erros

## map_console
Aplicativo Flutter desktop, desenvolido para MacOS (fácilmente portável para windows ou Linux) utilizado para receber os dados enviados via MQTT a partir da Raspberry Pi Pico.
Desenvolvido baseados nos exemplos desta [biblioteca de mapas]([url](https://pub.dev/packages/map)) e basedo nos exemplos desta [biblioteca de MQTT]([url](https://pub.dev/packages/mqtt_client))

## nrf_bt_pico
Firmware para a Raspberry Pi Pico W (transmissor), desenvolvido em C++, com o SDK nativo fornecido para a raspberry pi pico
Utilizando os [exemplos]([url](https://github.com/raspberrypi/pico-examples/tree/master/pico_w/bt)) do SDK para as conexões BLE e esta [biblioteca]([url](https://github.com/nRF24/RF24)) para comunicação com o módudo NRF2401

## pico_mqtt_python
Firmware para a Raspberry Pi Pico W (receptor), desenvolvido utilizando MicroPython, responsável por recever os dados de GPS através do NRF2401 e retransmiti-los através de uma conexão MQTT, utilizando a biblioteca [uMQTT]([url](https://pypi.org/project/micropython-umqtt.simple/))
