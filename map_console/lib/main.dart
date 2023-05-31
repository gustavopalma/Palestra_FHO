import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:map/map.dart' as map;
import 'package:latlng/latlng.dart';
import 'dart:math';
import 'package:flutter/gestures.dart';
import 'dart:async';
import 'dart:io';
import 'package:mqtt_client/mqtt_client.dart';
import 'package:mqtt_client/mqtt_server_client.dart';
import 'dart:convert';
import 'dart:collection';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Console Rastreamento',
      theme: ThemeData(
       
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'NRF2401L + Bluetooth + WiFi'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;
  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;

  String broker           = '127.0.0.1';
  int port                = 1883;
  String clientIdentifier = 'console';

  double lat = 0;
  double lng = 0;

  MqttClient? client;// = MqttServerClient('localhost', '');
  MqttConnectionState? connectionState;// = MqttConnectionState.disconnected;

  double _temp = 20;

  StreamSubscription? subscription;

  void _subscribeToTopic(String topic) {
    if (connectionState == MqttConnectionState.connected) {
        print('[MQTT client] Subscribing to ${topic.trim()}');
        client?.subscribe(topic, MqttQos.exactlyOnce);
    }
  }

  
  final controller = map.MapController(
    location: const LatLng(0, 0),
    zoom: 2,
  );

  
  String google(int z, int x, int y) {
    //Google Maps
    final url =
      'https://www.google.com/maps/vt/pb=!1m4!1m3!1i$z!2i$x!3i$y!2m3!1e0!2sm!3i420120488!3m7!2sen!5e1105!12m4!1e68!2m2!1sset!2sRoadmap!4e0!5m1!1e0!23i4111425';

    return url;
  }
  var markers = [
    const LatLng(0,0),
  ];
  var speed = [];
  void _incrementCounter() {
    setState(() {
      _gotoDefault();
      _connect();
    });
  }

  void _cleanMarkers() {
    setState(() {
      markers.clear();
    });
  }

   void _gotoDefault() {
    controller.center = const LatLng(-23.182994026212864, -49.398069107628224);
    controller.zoom = 18;
    setState(() {});
  }

  void _onDoubleTap(map.MapTransformer transformer, Offset position) {
    const delta = 0.5;
    final zoom = clamp(controller.zoom + delta, 2, 18);

    transformer.setZoomInPlace(zoom, position);
    setState(() {});
  }

  Offset? _dragStart;
  double _scaleStart = 1.0;
  void _onScaleStart(ScaleStartDetails details) {
    _dragStart = details.focalPoint;
    _scaleStart = 1.0;
  }

  void _onScaleUpdate(ScaleUpdateDetails details, map.MapTransformer transformer) {
    final scaleDiff = details.scale - _scaleStart;
    _scaleStart = details.scale;

    if (scaleDiff > 0) {
      controller.zoom += 0.02;
      setState(() {});
    } else if (scaleDiff < 0) {
      controller.zoom -= 0.02;
      setState(() {});
    } else {
      final now = details.focalPoint;
      final diff = now - _dragStart!;
      _dragStart = now;
      transformer.drag(diff.dx, diff.dy);
      setState(() {});
    }
  }

  Widget _buildMarkerWidget(Offset pos, Color color,
      [IconData icon = Icons.location_on]) {
    return Positioned(
      left: pos.dx - 24,
      top: pos.dy - 24,
      width: 48,
      height: 48,
      child: GestureDetector(
        child: Icon(
          icon,
          color: color,
          size: 48,
        )
      ),
    );
  }
  double clamp(double x, double min, double max) {
    if (x < min) x = min;
    if (x > max) x = max;

    return x;
  }

  @override
  Widget build(BuildContext context) {
 
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.title),
      ),
      
      body:  map.MapLayout(
        controller: controller,
        builder: (context, transformer) {
          final markerPositions = markers.map(transformer.toOffset).toList();
          print("zoom: ${controller.zoom}");
          final markerWidgets = markerPositions.map(
            (pos) => _buildMarkerWidget(pos, Colors.red),
          );
          
          final centerLocation = Offset(
              transformer.constraints.biggest.width / 2,
              transformer.constraints.biggest.height / 2);

          return GestureDetector(
            behavior: HitTestBehavior.opaque,
            onDoubleTapDown: (details) => _onDoubleTap(
              transformer,
              details.localPosition,
            ),
            onScaleStart: _onScaleStart,
            onScaleUpdate: (details) => _onScaleUpdate(details, transformer),
            child: Listener(
              behavior: HitTestBehavior.opaque,
              onPointerSignal: (event) {
                if (event is PointerScrollEvent) {
                  final delta = event.scrollDelta.dy / -1000.0;
                  final zoom = clamp(controller.zoom + delta, 2, 18);

                  transformer.setZoomInPlace(zoom, event.localPosition);
                  setState(() {});
                }
              },
              child: Stack(
                children: [
                  map.TileLayer(
                    builder: (context, x, y, z) {
                      final tilesInZoom = pow(2.0, z).floor();

                      while (x < 0) {
                        x += tilesInZoom;
                      }
                      while (y < 0) {
                        y += tilesInZoom;
                      }

                      x %= tilesInZoom;
                      y %= tilesInZoom;

                      return CachedNetworkImage(
                        imageUrl: google(z, x, y),
                        fit: BoxFit.cover,
                      );
                    },
                  ),
                  ...markerWidgets,
                ],
              ),
            ),
          );
        },
      ),
       floatingActionButtonLocation: FloatingActionButtonLocation.centerDocked,
  floatingActionButton: Padding(
    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 12),
    child: Row(
      children: [
        FloatingActionButton(
          onPressed: _incrementCounter,
          tooltip: 'Connect MQTT',
          child: const Icon(Icons.connect_without_contact_outlined ),
        ),
        Spacer(),
        FloatingActionButton(
          onPressed: _cleanMarkers,
          tooltip: 'Remove Markers',
          child: Icon(Icons.cleaning_services),
        ),
      ],
    ),
  ),
    );
  }

  void _connect() async {
  
    client = MqttServerClient(broker, '');
    client?.port = port;
 
    client?.logging(on: true);

    client?.keepAlivePeriod = 30;

    client?.onDisconnected = _onDisconnected;
   
    final MqttConnectMessage connMess = MqttConnectMessage()
        .withClientIdentifier(clientIdentifier)
        .startClean() // Non persistent session for testing
        .keepAliveFor(30)
        .withWillQos(MqttQos.atMostOnce);
    print('[MQTT client] MQTT client connecting....');
    client?.connectionMessage = connMess;

    try {
      await client?.connect();
    } catch (e) {
      print(e);
      _disconnect();
    }

    /// Check if we are connected
    if (client?.connectionState == MqttConnectionState.connected) {
      print('[MQTT client] connected');
      setState(() {
        connectionState = client?.connectionState;
      });
    } else {
      print('[MQTT client] ERROR: MQTT client connection failed - '
          'disconnecting, state is ${client?.connectionState}');
      _disconnect();
    }

    subscription = client?.updates?.listen(_onMessage);

    _subscribeToTopic("tracker/lat");
    _subscribeToTopic("tracker/lng");
    _subscribeToTopic("tracker/vel");
  }
void _disconnect() {
    print('[MQTT client] _disconnect()');
    client?.disconnect();
    _onDisconnected();
  }

  void _onDisconnected() {
    print('[MQTT client] _onDisconnected');
    setState(() {
      //topics.clear();
      connectionState = client?.connectionState;
      client = null;
      subscription?.cancel();
      subscription = null;
    });
    print('[MQTT client] MQTT client disconnected');
  }

  void _onMessage(List<MqttReceivedMessage> event) {
    print(event.length);
    final MqttPublishMessage recMess =
    event[0].payload as MqttPublishMessage;
    final String message =
    MqttPublishPayload.bytesToStringAsString(recMess.payload.message);

    print('[MQTT client] MQTT message: topic is <${event[0].topic}>, '
        'payload is <-- ${message} -->');
    print(client?.connectionState);
    print("[MQTT client] message with topic: ${event[0].topic}");
    print("[MQTT client] message with message: ${message}");
    
    String messageAux = message.substring(0,message.indexOf('}') + 1);
    Map<String, dynamic> data = jsonDecode(messageAux);
    if(event[0].topic == "tracker/lng") {
      lng = data["LNG"];
      
    } else if(event[0].topic == "tracker/lat") {
      lat = data["LAT"];
    } else if(event[0].topic == "tracker/vel") {
      var vel = data["VEL"];
      speed.add(vel);
    }
    print("latitude ${lat} longitude ${lng}");
    if (lat != 0 && lng != 0) {
      setState(() {
        markers.add(LatLng(lat,lng));
        lat = 0;
        lng = 0;
      });
    }

  }
}
