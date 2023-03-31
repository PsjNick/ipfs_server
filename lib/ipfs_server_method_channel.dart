import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'ipfs_server_platform_interface.dart';

class MethodChannelIpfsServer extends IpfsServerPlatform {
  @visibleForTesting
  final methodChannel = const MethodChannel('ipfs_server');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String?>('getPlatformVersion');
    return version;
  }

  @override
  Future<List<String>?> nodeList() async {
    final List? nodes = await methodChannel.invokeMethod<List?>('nodeList');

    List<String>? newNode = nodes?.map((e) => e.toString()).toList();

    return newNode;
  }

  @override
  Future<bool?> removeNode({required String nodeUrl}) async {
    bool? isSuccess = await methodChannel.invokeMethod<bool?>(
      'removeNode',
      nodeUrl,
    );
    return isSuccess;
  }

  @override
  Future<bool?> addNode({required String nodeUrl}) async {
    bool? isSuccess = await methodChannel.invokeMethod<bool?>(
      'addNode',
      nodeUrl,
    );
    return isSuccess;
  }

  @override
  Future<String?> uploadFileToIPFS({required String filePath}) async {
    final cid = await methodChannel.invokeMethod<String?>(
      'uploadFileToIPFS',
      filePath,
    );
    return cid;
  }

  @override
  Future<String?> getUrlByCID({required String cid}) async {
    final String? url = await methodChannel.invokeMethod<String?>(
      'getUrlByCID',
      cid,
    );
    return url;
  }

  @override
  Future<bool?> closeIPFS() async {
    bool? isSuccess = await methodChannel.invokeMethod<bool?>('closeIPFS');
    return isSuccess;
  }

  @override
  Future<bool?> openIPFS() async {
    bool? isSuccess = await methodChannel.invokeMethod<bool?>('openIPFS');
    return isSuccess;
  }

  @override
  Future<String?> getCurrentIdInfo() async {
    String? idInfo =
        await methodChannel.invokeMethod<String?>('getCurrentIdInfo');
    return idInfo;
  }
}
