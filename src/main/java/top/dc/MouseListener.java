package top.dc; /**
 * @author dc on 2023/4/22
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.input.MouseButton;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MouseListener {
    static boolean down = false;
    static int downNum = 0;
    public static void main(String[] args) {
        try {
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);
            VolumeControl.logger.setLevel(Level.OFF);
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("Failed to register native hook: " + ex.getMessage());
            System.exit(1);
        }
        final ObjectMapper mapper = new ObjectMapper();
        final boolean down = false;

        GlobalScreen.addNativeMouseWheelListener(new NativeMouseWheelListener() {
            @Override
            public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeMouseWheelEvent) {
                // 滚动方向
                // 向上滚动,增加系统音量
                if(MouseListener.down && nativeMouseWheelEvent.getWheelRotation() == -1) {
                    VolumeControl.controlSystemVolume("1");
                }
                if(MouseListener.down && nativeMouseWheelEvent.getWheelRotation() == 1) {
                    VolumeControl.controlSystemVolume("2");
                }
            }
        });

        GlobalScreen.addNativeMouseListener(new NativeMouseInputListener() {
            @Override
            public void nativeMouseClicked(NativeMouseEvent e) {
                // 处理鼠标单击事件
            }

            @Override
            public void nativeMousePressed(NativeMouseEvent e) {
                // 处理鼠标按下事件
                if(e.getButton() == MouseButton.SECONDARY.ordinal()){
                    MouseListener.down = true;
                    MouseListener.downNum ++;
                    // 500 毫秒内连续双击鼠标中键，切换静音和非静音
                    setTimeout(() -> { MouseListener.downNum = 0; },500);
                    if(MouseListener.downNum == 2){
                        VolumeControl.controlSystemVolume("0");
                    }
                }
            }

            @Override
            public void nativeMouseReleased(NativeMouseEvent e) {
                // 处理鼠标松开事件
                MouseListener.down = false;
            }

            @Override
            public void nativeMouseMoved(NativeMouseEvent e) {
                // 处理鼠标移动事件
            }

            @Override
            public void nativeMouseDragged(NativeMouseEvent e) {
                // 处理鼠标拖拽事件
            }
        });
    }
    public static void setTimeout(Runnable runnable, int delay) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runnable.run();
                timer.cancel();
            }
        }, delay);
    }
}