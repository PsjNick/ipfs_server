import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'ipfs_server_method_channel.dart';

abstract class IpfsServerPlatform extends PlatformInterface {
  IpfsServerPlatform() : super(token: _token);

  static final Object _token = Object();

  static IpfsServerPlatform _instance = MethodChannelIpfsServer();

  static IpfsServerPlatform get instance => _instance;

  static set instance(IpfsServerPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  //  开启IPFS
  Future<bool?> openIPFS() {
    throw UnimplementedError('openIPFS() has not been implemented.');
  }

  //  关闭IPFS
  Future<bool?> closeIPFS() {
    throw UnimplementedError('closeIPFS() has not been implemented.');
  }

  //  当前节点信息
  Future<String?> getCurrentIdInfo() {
    throw UnimplementedError('getCurrentIdInfo() has not been implemented.');
  }

  //  通过CID获取本地IPFS文件连接
  Future<String?> getUrlByCID({required String cid}) {
    throw UnimplementedError('getUrlByCID() has not been implemented.');
  }


  //  上传一个文件到IPFS，返回CID
  Future<String?> uploadFileToIPFS({required String filePath,}) {
    throw UnimplementedError('uploadFileToIPFS() has not been implemented.');
  }

  //  添加节点
  Future<bool?> addNode({required String nodeUrl,}) {
    throw UnimplementedError('addNode() has not been implemented.');
  }

  //  移除节点
  Future<bool?> removeNode({required String nodeUrl,}) {
    throw UnimplementedError('removeNode() has not been implemented.');
  }

  //  节点列表
  Future<List<String>?> nodeList() {
    throw UnimplementedError('nodeList() has not been implemented.');
  }




}
