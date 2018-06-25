package cn.lemonit.lemage.lemageutil;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * 获取应用的信息
 * @author: zhaoguangyang
 */
public class SystemInfo {

    /**
     * 获取当前应用的包名
     * @param context
     * @return  PackageName
     */
    public static String getApplicationPackageName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid)//得到当前应用
                return info.processName;//返回包名
        }
        return "";
    }
}
