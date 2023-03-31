package com.psj.ipfs_server.utils;

import android.content.Context;
import android.os.Build;

import com.psj.ipfs_server.event.ExecLog;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;


/**
 * IpfsExec 操作方法
 */
public class IpfsExec {
    private Context context;
    private static IpfsExec ipfsNode;

    public static IpfsExec getInstance(Context context) {
        if (ipfsNode == null) {
            ipfsNode = new IpfsExec(context);
        }
        return ipfsNode;
    }

    public IpfsExec(Context context) {
        this.context = context;
    }

    public void init() throws IOException {
        save();

        File versionFile = new File(Constants.Dir.getSDdir(context) + "/.ipfsNode/version");

        LogUtils.e("version exists :" + versionFile.exists());
        LogUtils.e(versionFile.getAbsolutePath());

        if (!versionFile.exists()) {

            Process init = command("init");

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(init.getInputStream()))) {

                String log = null;

                while ((log = bufferedReader.readLine()) != null) {
                    LogUtils.e("log: " + log + "\n");
                    EventBus.getDefault().post(new ExecLog(log + ""));
                }

                init.waitFor();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * 当前节点信息
     * @return
     * @throws IOException
     */
    public String nodeIdInfo() throws IOException {
        Process exec = command("id");
        BufferedReader daemon = new BufferedReader(new InputStreamReader(exec.getInputStream()));

        StringBuilder logBuf = new StringBuilder();

        String log;

        while ((log = daemon.readLine()) != null) {
            logBuf.append(log + "\n");
        }

        String logStr = logBuf.toString();

        LogUtils.e(logStr);

        return logStr;
    }

    /**
     * 节点列表
     * @return
     * @throws IOException
     */
    public List<String> nodeList() throws IOException {
        List<String> nodeList = new LinkedList<>();

//        Process exec = command("bootstrap list");
        Process exec = command("swarm peers");
        BufferedReader daemon = new BufferedReader(new InputStreamReader(exec.getInputStream()));
        String log = null;

        while ((log = daemon.readLine()) != null) {
            nodeList.add(log);
            LogUtils.e(log + "\n");
        }

        return nodeList;
    }

    /**
     * 添加节点
     * @param nodeInfo
     * @return
     * @throws IOException
     */
    public String addNode(String nodeInfo) throws IOException {

        Process exec01 = command("bootstrap add " + nodeInfo);
        Process exec02 = command("swarm peering add " + nodeInfo);

        BufferedReader daemon01 = new BufferedReader(new InputStreamReader(exec01.getInputStream()));
        BufferedReader daemon02 = new BufferedReader(new InputStreamReader(exec02.getInputStream()));

        String log01 = daemon01.readLine();
        String log02 = daemon02.readLine();

        LogUtils.e(log01 + "\n");
        LogUtils.e(log02 + "\n");

        return  "{" + log01 + "," + log02 + "}";

    }

    /**
     * 移除节点
     * @param nodeId
     * @return
     * @throws IOException
     */
    public String rmNode(String nodeId)throws IOException{
//        Process exec01 = command("bootstrap rm " + nodeId);
        Process exec = command("swarm peering rm " + nodeId);
        BufferedReader daemon = new BufferedReader(new InputStreamReader(exec.getInputStream()));
        String log = daemon.readLine();
        LogUtils.e(log + "\n");
        return  log;
    }

    /**
     * 启动
     *
     * @return true is running
     * @throws IOException
     */
    public boolean daemon() throws IOException {
        Process exec = command("daemon");
        BufferedReader daemon = new BufferedReader(new InputStreamReader(exec.getInputStream()));
        String log = null;
        while ((log = daemon.readLine()) != null) {
            LogUtils.e(log + "\n");
            EventBus.getDefault().post(new ExecLog(log + ""));
            if (log.equals("Daemon is ready")) {
                daemon.close();
                return true;
            }
        }
        return false;
//        return command("daemon");
    }

    /**
     * 关闭
     *
     * @throws IOException
     */

    public void shutDown() throws IOException {
        command("shutdown");
    }


    public String add(String absPath) throws IOException, InterruptedException {
        if (new File(absPath).exists()) {
            Process exec = command("Add " + absPath);
            BufferedReader command = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            String log = null;
            while (true) {
//                Thread.sleep(1);
                if ((log = command.readLine()) != null) {
                    LogUtils.e(log + "\n");
                    if (log.contains("added")) {
                        return log.split(" ")[1];
                    }
                }
            }
        } else {
            throw new FileNotFoundException();
        }
    }

    /**
     * 保存文件到本地存储
     *
     * @throws IOException
     */
    private void save() throws IOException {
        if (!new File(Constants.Dir.getLocalDir(context) + "/ipfsNode").exists()) {
            InputStream open;
            if (Build.CPU_ABI.contains("x86")) {
                open = context.getAssets().open("x86");
            } else if (Build.CPU_ABI.contains("arm")) {
                open = context.getAssets().open("arm");
            } else {
                return;
            }
            LogUtils.e(Constants.Dir.getLocalDir(context) + "");
            FileOutputStream fileOutputStream = new FileOutputStream(Constants.Dir.getLocalDir(context) + "/ipfsNode");
            byte[] bytes = new byte[1024];
            int size = 0;
            while ((size = open.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, size);
            }
            open.close();
            fileOutputStream.close();
            Process exec = Runtime.getRuntime().exec("chmod 777 " + Constants.Dir.getLocalDir(context) + "/ipfsNode");
            try {
                exec.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * cmd
     *
     * @param cmd
     * @throws IOException
     */
    public Process command(String cmd) throws IOException {

//        ProgressDialog progressDialog = new ProgressDialog(context);
//        progressDialog.show();

        String[] envp = {("IPFS_PATH=" + Constants.Dir.getSDdir(context) + "/.ipfsNode")};
        String command = Constants.Dir.getLocalDir(context) + "/ipfsNode " + cmd;
        //        BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
        return Runtime.getRuntime().exec(command, envp);
//        String log = null;
//        while((log = inputStreamReader.readLine()) != null) {
//                LogUtils.e(log + "\n");
//            if (log.equals("Daemon is ready")) {
//                return true;
//            }
//        }
//        return false;
//        progressDialog.dismiss();
    }
}
