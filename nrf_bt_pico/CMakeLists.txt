cmake_minimum_required(VERSION 3.13)

include(pico_sdk_import.cmake)

project(mpu6050_ble C CXX ASM)
set(CMAKE_C STANDARD 11)
set(CMAKE_CXX_STANDARD 17)
set(PICO_BOARD pico_w)
pico_sdk_init()

# Adds a CMakeLists.txt file from a subdirectory
add_subdirectory(nrf24)

add_executable(picow_gps
    main.cpp gatt_service.cpp
)
target_include_directories(picow_gps PRIVATE ${CMAKE_CURRENT_LIST_DIR} )


target_link_libraries(picow_gps pico_btstack_ble pico_btstack_cyw43 pico_cyw43_arch_none pico_cyw43_arch_lwip_threadsafe_background pico_stdlib hardware_spi RF24)

target_include_directories(picow_gps PRIVATE
    ${CMAKE_CURRENT_LIST_DIR} # For btstack config
    )

pico_btstack_make_gatt_header(picow_gps PRIVATE "${CMAKE_CURRENT_LIST_DIR}/LOCATION_GATT.gatt")

pico_enable_stdio_usb(picow_gps 1)
pico_enable_stdio_uart(picow_gps 0)

pico_add_extra_outputs(picow_gps)

