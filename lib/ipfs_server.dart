
import 'ipfs_server_platform_interface.dart';

class IpfsServer {
  Future<String?> getPlatformVersion() {
    return IpfsServerPlatform.instance.getPlatformVersion();
  }


  Future<bool?> openIPFS(){
    return IpfsServerPlatform.instance.openIPFS();
  }


  Future<bool?> closeIPFS(){
    return IpfsServerPlatform.instance.closeIPFS();
  }


  Future<String?> getUrlByCID({required String cid}){
    return IpfsServerPlatform.instance.getUrlByCID(cid: cid);
  }

  Future<String?> uploadFileToIPFS({required String filePath}){
    return IpfsServerPlatform.instance.uploadFileToIPFS(filePath: filePath);
  }

  Future<bool?> addNode({required String nodeUrl}){
    return IpfsServerPlatform.instance.addNode(nodeUrl: nodeUrl);
  }


  Future<bool?> removeNode({required String nodeUrl}){
    return IpfsServerPlatform.instance.addNode(nodeUrl: nodeUrl);
  }


  Future<List<String>?> nodeList(){
    return IpfsServerPlatform.instance.nodeList();
  }

  Future<String ?> getCurrentIdInfo(){
    return IpfsServerPlatform.instance.getCurrentIdInfo();
  }




}
