/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kursaach.quarantinelab;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import kursaach.view.QuarantineController;
import kursaach.utils.FileHelper;

/**
 *
 * @author user
 */
public class QuarantineLab {

    private volatile QuarantineController controller;
    private static int interval = 500;

    private static volatile int i = 0;
    private static volatile int le = 0;
    public static final String pathSrcText = System.getProperty("user.dir") + "/srcDirtyText.txt";
    public static final String pathCleanSrcText = System.getProperty("user.dir") + "/srcCleanText.txt";
    public static final String pathCleanText = System.getProperty("user.dir") + "/resultCleanText.txt";

    private static volatile Character ch;
    private static volatile Queue<Character> fifo = new LinkedList<Character>();
    private static volatile boolean isLive = true;
    private static final boolean test = true;

    private static final Object monitor = new Object();

    private static boolean isBuffered = false;
    private static boolean isPaused = false;
    private volatile String filtredSymbols;

    private String tmpString1 = "";
    private String tmpString2 = "";
    private String console1String = "";
    private String console2String = "";
    private Thread myThread1;
    private Thread myThread2;

    public QuarantineLab(QuarantineController ctrl) {
        controller = ctrl;
        interval = controller.getQuSpeed();

    }

    public void test(String msg) {
        controller.console1Txt.setText(msg);
    }

    public void toggleRunning() {
        interval = controller.getQuSpeed();
        isPaused = !isPaused;
    }

    public void stopProcess() throws InterruptedException {
        fifo = new LinkedList<>();
        ch = null;
        isLive = false;
        isPaused = true;
        i = 0;
        le = 0;
        tmpString1 = "";
        tmpString2 = "";
        console1String = "";
        console2String = "";
        myThread1.join();
        myThread2.join();
    }

    public void processQuarantine(boolean mode) throws IOException {
        // TODO code application logic here
        filtredSymbols = controller.filtredSymbols;
        isBuffered = mode;
        isLive = true;
        isPaused = false;
        interval = controller.getQuSpeed();
        System.out.println("" + mode);

        Timeline updaterUI = new Timeline(new KeyFrame(new javafx.util.Duration(10), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                if (!isPaused) {
                    if (ch != null && !isBuffered) {
                        controller.sharedCharTxt.setText(ch.toString());
                    }

                    controller.console1Txt.appendText(tmpString1);
                    controller.console2Txt.appendText(tmpString2);
                    controller.console1_1Txt.appendText(console1String);
                    controller.console2_1Txt.appendText(console2String);
                    tmpString1 = "";
                    tmpString2 = "";
                    console1String = "";
                    console2String = "";
                    synchronized (monitor) {
                        controller.bufferTxt.setText(fifo.toString());
                    }

                }
//                controller.bufferTxt.setText(fifo.);
            }
        }));
        updaterUI.setCycleCount(Timeline.INDEFINITE);
        updaterUI.play();

        myThread1 = new Thread(new Runnable() {
            String s = "";

            @Override
            public void run() //Этот метод будет выполняться в побочном потоке
            {
                try {
                    s = FileHelper.getString(pathSrcText);
                } catch (IOException ex) {
                    System.out.println("OLOSDLASDOASODOASDO");
                    Logger.getLogger(QuarantineLab.class.getName()).log(Level.SEVERE, null, ex);
                }
                le = s.length();
                synchronized (monitor) {
                    monitor.notify();
                }
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(QuarantineLab.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                while (isLive) {
                    if (!isPaused) {

                        if (i < s.length()) {
                            try {
                                s = FileHelper.getString(pathSrcText);
                            } catch (IOException ex) {
                                Logger.getLogger(QuarantineLab.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            synchronized (monitor) {
                                ch = s.charAt(i);
                                console1String += ">Processing char: \'" + ch + "\'.\n";
                                tmpString1 += ch.toString();
                                if (isBuffered) //для режима с буфером
                                {
                                    fifo.add(ch);
                                    console1String += "Char \'" + ch + "\' added to buffer.\n";
                                } else {
                                    console1String += "Char \'" + ch + "\' placed to shared char.\n";
                                }
                            }
                        }
                        i++;

                        try {
                            Thread.sleep((int) (interval * .9));
                        } catch (InterruptedException ex) {
                            Logger.getLogger(QuarantineLab.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        Thread.yield();
                    }
                }
            }
        });
        myThread2 = new Thread(new Runnable() {
            Character tmp;
            String s = "";
            String result;

            public void run() //Этот метод будет выполняться в побочном потоке
            {
                synchronized (monitor) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(QuarantineLab.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
                synchronized (monitor) {
                    monitor.notify();
                }

                while (isLive) {
                    if (!isPaused) {
                        try {
                            s = FileHelper.getString(pathSrcText);
                        } catch (IOException ex) {
                            Logger.getLogger(QuarantineLab.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        //принимаем символы из буфера и отсеиваем ненужные символы
                        synchronized (monitor) {

                            tmp = ch;
                            if (isBuffered && fifo.size() > 0) {
                                console2String += ">Getting char from buffer.\n";
                                console2String += "Processing char: \'" + fifo.peek() + "\'.\n";
                                tmpString2 += fifo.peek().toString();
                                if (!fifo.peek().toString().matches(filtredSymbols)) {
                                    console2String += "Char \'" + fifo.peek() + "\' is \'CLEAN\' char.\n";
                                    console2String += "Writing char to result string.\n";
                                    if (result == null) {
                                        result = fifo.poll().toString();
                                    } else {
                                        result += fifo.poll().toString();
                                    }
                                    
                                } else {
                                    console2String += "Char \'" + fifo.peek() + "\' is \'DIRT\' char.\n";
                                    console2String += "Ignoring this char.\n";
                                    fifo.remove();
                                }
                            }
                        }

                        if (!isBuffered && tmp != null) {
                            console2String += ">Getting char from shared char.\n";
                            console2String += "Processing char: \'" + tmp + "\'.\n";
                            tmpString2 += tmp;

                        }
                        if (!isBuffered && i < le && tmp != null) {
                            if (!tmp.toString().matches(filtredSymbols)) {
                                result += tmp.toString();
                                console2String += "Char \'" + tmp + "\' is \'CLEAN\' char.\n";
                                console2String += "Writing char to result string.\n";
                            } else {
                                console2String += "Char \'" + tmp + "\' is \'DIRT\' char.\n";
                                console2String += "Ignoring this char.\n";

                            }
                        } else if (i > le && (fifo.size() == 0 || !isBuffered)) {
                            //запись чистого текста и проверкуа на потери
                            FileHelper.writeString(result, pathCleanText, false);
                            try {
                                if (isBuffered) {
                                    controller.resultTxt.appendText("Buffered mode:\n");
                                } else {
                                    controller.resultTxt.appendText("Non buffered mode:\n");
                                }
                                controller.resultTxt.appendText("res=" + result.length() + " src=" + FileHelper.getString(pathCleanSrcText).length() + "\n"
                                        + ((float) result.length() / (float) FileHelper.getString(pathCleanSrcText).length() * 100) + "%\n");
                                System.out.println("res=" + result.length() + " src=" + FileHelper.getString(pathCleanSrcText).length() + " " + result);
                                System.out.println("" + ((float) result.length() / (float) FileHelper.getString(pathCleanSrcText).length() * 100) + "%");
                            } catch (IOException ex) {
                                Logger.getLogger(QuarantineLab.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            isLive = false;
//                            updaterUI.stop();
                        }

                        try {
                            Thread.sleep((int) (interval));
                        } catch (InterruptedException ex) {
                            Logger.getLogger(QuarantineLab.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        Thread.yield();
                    }
                }
            }
        });

        myThread1.setDaemon(true);
        myThread2.setDaemon(true);

        myThread2.start();	//Запуск потока
        myThread1.start();	//Запуск потока

    }

    public static String generateString(Random rng, String characters, int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }

}
