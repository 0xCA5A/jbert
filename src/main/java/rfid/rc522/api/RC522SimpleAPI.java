package com.chimpim.rc522.api;


import java.util.Arrays;

/**
 * RC522读卡器简单的API
 *
 * @author liuyonghui
 */
public class RC522SimpleAPI {


    private volatile static RC522SimpleAPI api;
    private RaspRC522 mRaspRC522;
    private byte[] tempUid = new byte[5];
    private int backLen[] = new int[1];

    private boolean isDebug;

    private RC522SimpleAPI() {
        mRaspRC522 = new RaspRC522();
    }

    public static RC522SimpleAPI getInstance() {
        if (api == null) {
            if (api == null) {
                synchronized (RC522SimpleAPI.class) {
                    api = new RC522SimpleAPI();
                }
            }
        }
        return api;
    }

    public boolean isDebug() {
        return isDebug;
    }


    public RC522SimpleAPI setDebug(boolean debug) {
        isDebug = debug;
        return this;
    }

    /**
     * 取得 RaspRC522 对象
     *
     * @return ：RaspRC522 对象
     */
    public RaspRC522 getRaspRC522Obj() {
        return mRaspRC522;
    }

    /**
     * 寻卡
     *
     * @return :当前对象
     */
    public RC522SimpleAPI findCards() throws SimpleAPIException {
        mRaspRC522.reset();
        if (mRaspRC522.Request(RaspRC522.PICC_REQIDL, backLen) == RaspRC522.MI_OK
                && mRaspRC522.AntiColl(tempUid) == RaspRC522.MI_OK)
            return this;
        if (isDebug) {
            System.out.println("backLen = " + Arrays.toString(backLen));
            System.out.println("tempUid = " + Arrays.toString(tempUid));
        }
        throw new SimpleAPIException("Failed to find card !");
    }


    /**
     * 取得UID（序列号）
     *
     * @param uid: 长度为5的字节数组
     * @return ：当前对象
     */
    public RC522SimpleAPI getUid(byte[] uid) {
        System.arraycopy(this.tempUid, 0, uid, 0, 5);
        return this;
    }

    /**
     * @param backLen:长度为5的整形数组
     * @return ：当前对象
     */
    public RC522SimpleAPI getBackLen(int[] backLen) {
        System.arraycopy(this.backLen, 0, backLen, 0, 1);
        return this;
    }

    /**
     * 切换到操作
     *
     * @return ：操作对象
     */
    public Operate operate() {
        return new Operate(tempUid);
    }


    /**
     * 操作类
     */
    public class Operate {

        private byte[] uid;
        // 默认密码
        private byte[] defaultKey = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};


        Operate(byte[] uid) {
            this.uid = uid;
        }

        boolean selectCard() {
            if (mRaspRC522.Select_Tag(uid) == 0) return true;
            if (isDebug) System.out.println("uid = " + Arrays.toString(uid));
            return false;
        }

        boolean authCard(byte sector, byte block, byte[] key) {
            selectCard();
            int status;
            if (mRaspRC522.Auth_Card(RaspRC522.PICC_AUTHENT1A, sector, block, key, uid) == RaspRC522.MI_OK
                    || (status = mRaspRC522.Auth_Card(RaspRC522.PICC_AUTHENT1B, sector, block, key, uid)) == RaspRC522.MI_OK) {
                return true;
            }
            if (isDebug) System.out.println("authCard() -> status = " + status);
            return false;
        }

        /**
         * 读卡
         *
         * @param sector：扇区
         * @param block：块区
         * @param key：密码
         * @return :读到的数据
         * @throws SimpleAPIException
         */
        public byte[] readCard(byte sector, byte block, byte[] key) throws SimpleAPIException {
            int status = RaspRC522.MI_NOTAGERR;
            if (authCard(sector, block, key)) {
                byte[] data = new byte[16];
                if ((status = mRaspRC522.Read(sector, block, data)) == RaspRC522.MI_OK) return data;
            }
            if (isDebug) System.out.println("readCard() -> Read -> status = " + status);
            throw new SimpleAPIException("Read error !");
        }

        /**
         * 读卡,使用默认密码
         *
         * @param sector：扇区
         * @param block：块区
         * @return :读到的数据
         * @throws SimpleAPIException
         */
        public byte[] readCard(byte sector, byte block) throws SimpleAPIException {
            return readCard(sector, block, defaultKey);
        }

        /**
         * 写卡
         *
         * @param sector：扇区
         * @param block：块区
         * @param key：密码
         * @param data:     需要写进去的数据
         * @throws SimpleAPIException
         */
        public void writeCard(byte sector, byte block, byte[] key, byte[] data) throws SimpleAPIException {
            int status;
            if (authCard(sector, block, key)) {
                if ((status = mRaspRC522.Write(sector, block, data)) != RaspRC522.MI_OK) {
                    if (isDebug) System.out.println("writeCard() -> Read -> status = " + status);
                    throw new SimpleAPIException("Write error !");
                }
            }
        }

        /**
         * 写卡,使用默认密码
         *
         * @param sector：扇区
         * @param block：块区
         * @param data:     需要写进去的数据
         * @throws SimpleAPIException
         */
        public void writeCard(byte sector, byte block, byte[] data) throws SimpleAPIException {
            writeCard(sector, block, defaultKey, data);
        }
    }

    public class SimpleAPIException extends Exception {

        SimpleAPIException(String message) {
            super(message);
        }

        SimpleAPIException(String message, Throwable cause) {
            super(message, cause);
        }

        SimpleAPIException(Throwable cause) {
            super(cause);
        }
    }


}
