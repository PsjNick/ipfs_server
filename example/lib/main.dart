import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:ipfs_server/ipfs_server.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _outputInfo = "";

  String _platformVersion = 'Unknown';
  final _ipfsServerPlugin = IpfsServer();

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    String platformVersion;
    try {
      platformVersion = await _ipfsServerPlugin.getPlatformVersion() ??
          'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: ListView(
          children: [
            Text(
              _platformVersion,
              style: const TextStyle(
                fontSize: 20,
              ),
            ),
            Wrap(
              children: [
                TextButton(
                  onPressed: () async {
                    bool? isOpen = await IpfsServer().openIPFS();

                    debugPrint("isOpen: $isOpen");

                    if (isOpen == true) {
                      setState(() {
                        _outputInfo = "节点已开启";
                      });
                    }
                  },
                  child: const Text(
                    '开启IPFS',
                  ),
                ),
                TextButton(
                  onPressed: () async {
                    bool? isClose = await IpfsServer().closeIPFS();

                    debugPrint("isClose: $isClose");

                    if (isClose == true) {
                      setState(() {
                        _outputInfo = "节点已关闭";
                      });
                    }
                  },
                  child: const Text(
                    '关闭IPFS',
                  ),
                ),
                TextButton(
                  onPressed: () async {
                    String? idInfo = await IpfsServer().getCurrentIdInfo();

                    debugPrint("idInfo: $idInfo");

                    if (idInfo != null) {
                      setState(() {
                        _outputInfo = idInfo;
                      });
                    }
                  },
                  child: const Text(
                    '本机节点信息',
                  ),
                ),
                TextButton(
                  onPressed: () async {
                    List<String>? nodeList = await IpfsServer().nodeList();

                    setState(() {
                      _outputInfo = nodeList?.join("   ") ?? "";
                    });

                    debugPrint("nodeList Size: ${nodeList?.length ?? 0}");

                    nodeList?.forEach((element) {
                      debugPrint("node: $element");
                    });
                  },
                  child: const Text(
                    '连接节点列表',
                  ),
                ),
                TextButton(
                  onPressed: () async {

                    final nodeUrls = [

                      "/ip4/47.108.163.105/tcp/4001/p2p/12D3KooWHxqszCwHaBpVzEJaqcYHUAUMUGB9i3182Tbb4NXMr66Q",
                      // "/ip4/47.108.163.105/udp/4001/quic-v1/p2p/12D3KooWHxqszCwHaBpVzEJaqcYHUAUMUGB9i3182Tbb4NXMr66Q",
                      // "/ip4/47.108.163.105/tcp/4001/p2p/12D3KooWHxqszCwHaBpVzEJaqcYHUAUMUGB9i3182Tbb4NXMr66Q",
                      // "/ip4/47.108.163.105/udp/4001/quic-v1/p2p/12D3KooWHxqszCwHaBpVzEJaqcYHUAUMUGB9i3182Tbb4NXMr66Q",

                    ];

                    for (var nodeUrl in nodeUrls) {

                      bool? isSuccess = await IpfsServer().addNode(nodeUrl: nodeUrl);

                      if (isSuccess == true) {
                        setState(() {
                          _outputInfo = "连接点：$nodeUrl  添加成功";
                        });
                      } else {
                        setState(() {
                          _outputInfo =  "连接点：$nodeUrl 添加失败";
                        });
                      }

                      debugPrint(_outputInfo);


                    }




                  },
                  child: const Text(
                    '添加连接点',
                  ),
                ),
                TextButton(
                  onPressed: () async {
                    FilePickerResult? result =
                    await FilePicker.platform.pickFiles();
                    String? path = result?.files.single.path;

                    if (path != null) {
                      setState(() {
                        _outputInfo = "文件上传中";
                      });
                      String? cid =
                      await IpfsServer().uploadFileToIPFS(filePath: path);
                      if (cid != null) {
                        setState(() {
                          _outputInfo = "文件上传成功 - $cid";
                        });

                        debugPrint(_outputInfo);
                      } else {
                        setState(() {
                          _outputInfo = "文件上传失败";
                        });
                      }
                    }
                  },
                  child: const Text(
                    '上传文件',
                  ),
                ),
                TextButton(
                  onPressed: () async {
                    const cid = "QmaQv15U6eBMoewELbedimGjTQoo33QgQZtyVVjnADW79k";

                    String ? url = await IpfsServer().getUrlByCID(cid: cid);

                    setState(() {
                      _outputInfo = url ?? "";
                    });

                    debugPrint("连接地址：$_outputInfo");
                  },
                  child: const Text(
                    '通过CID获取访问地址',
                  ),
                ),

              ],
            ),



            const SizedBox(
              height: 20,
            ),
            Padding(
              padding: const EdgeInsets.symmetric(
                horizontal: 20,
              ),
              child: SelectableText(
                _outputInfo,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
