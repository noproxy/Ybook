package com.ybook.app;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.test.AndroidTestCase;
import com.ybook.app.net.LoginRequest;
import com.ybook.app.net.WebSocketHelper;
import junit.framework.Assert;
import com.ybook.app.bean.BeanPackage;
import com.ybook.app.net.NetPackage;
import junit.framework.TestResult;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Carlos on 2014/12/26.
 */
public class SocketTest extends AndroidTestCase {


//    public void testLogin() throws Throwable {
//        final CountDownLatch latch = new CountDownLatch(1);
//        final int a = 0;
//        final Result result = new Result();
//        final Handler mHandler = new Handler() {
//            @Override
//            public void handleMessage(@NotNull Message msg) {
//                super.handleMessage(msg);
//                result.result = msg.what;
//                latch.countDown();
//            }
//        };
//        Thread testThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                WebSocketHelper.INSTANCE$.login(new LoginRequest("testName", "testPassword", BeanPackage.getLibCode(), mHandler));
//            }
//        });
//        testThread.start();
//        latch.await();
//        assertEquals(NetPackage.getMSG_ERROR(), result.result);
//
//    }

    private class Result {
        int result = -1;
    }

}
