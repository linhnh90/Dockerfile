#include <jni.h>
#include <string>
#include "stylSerial/inc/stylComm.h"
//#include "JNIHelp.h"
#include <fcntl.h>
#include <termios.h>
#include "ql_log.h"
#include<unistd.h>

namespace android {
    static void throw_NullPointerException(JNIEnv *env, const char *msg) {
        jclass clazz;
        clazz = env->FindClass("java/lang/NullPointerException");
        env->ThrowNew(clazz, msg);
    }

    int uartSetSerial(int speed, int databits, int stopbits, char parity, int fd) {
        int i;
        int status;
        int speed_arr[] = {B115200, B38400, B19200, B9600, B4800, B2400, B1200, B300,
                           B38400, B19200, B9600, B4800, B2400, B1200, B300, B57600};
        int name_arr[] = {115200, 38400, 19200, 9600, 4800, 2400, 1200, 300, 38400, 19200, 9600,
                          4800, 2400, 1200, 300, 57600};

        struct termios options;

        if (tcgetattr(fd, &options) != 0) {
            perror("SetupSerial 1");
            return -1;
        }

        for (i = 0; i < sizeof(speed_arr) / sizeof(int); i++) {
            if (speed == name_arr[i]) {
                cfsetispeed(&options, speed_arr[i]);
                cfsetospeed(&options, speed_arr[i]);
            }
        }

        options.c_cflag |= CLOCAL;

        options.c_cflag |= CREAD;

        options.c_cflag &= ~CSIZE;
        switch (databits) {
            case 5    :
                options.c_cflag |= CS5;
                break;
            case 6    :
                options.c_cflag |= CS6;
                break;
            case 7    :
                options.c_cflag |= CS7;
                break;
            case 8:
                options.c_cflag |= CS8;
                break;
            default:
                fprintf(stderr, "Unsupported data size/n");
                return (-1);
        }

        switch (parity) {
            case 'n':
            case 'N':
                options.c_cflag &= ~PARENB;
                options.c_iflag &= ~INPCK;
                break;
            case 'o':
            case 'O':
                options.c_cflag |= (PARODD | PARENB);
                options.c_iflag |= INPCK;
                break;
            case 'e':
            case 'E':
                options.c_cflag |= PARENB;
                options.c_cflag &= ~PARODD;
                options.c_iflag |= INPCK;
                break;
            case 's':
            case 'S':
                options.c_cflag &= ~PARENB;
                options.c_cflag &= ~CSTOPB;
                break;
            default:
                fprintf(stderr, "Unsupported parity/n");
                return (-1);
        }

        switch (stopbits) {
            case 1:
                options.c_cflag &= ~CSTOPB;
                break;
            case 2:
                options.c_cflag |= CSTOPB;
                break;
            default:
                fprintf(stderr, "Unsupported stop bits/n");
                return (-1);
        }

        options.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG);  /*Input*/
        options.c_oflag &= ~OPOST;


        options.c_cc[VTIME] = 1;

        options.c_cc[VMIN] = 1;

        // options.c_iflag &=~(ICRNL | IGNCR );
        options.c_iflag &= ~(IXON | IXOFF | IXANY | ICRNL | INLCR | IGNCR);

        tcflush(fd, TCIFLUSH);


        if (tcsetattr(fd, TCSANOW, &options) != 0) {

            perror("com set error!/n");

            return (-1);

        }

        return (0);

    }

    int uartWrite(char *send_buf, int data_len, int fd) {
        int len = 0;
        LOGD("send_buf: %s", send_buf);
        len = write(fd, send_buf, data_len);
        if (len != data_len) {
            LOGD("<<<>>>>has send data %d, but not equal %d", len, data_len);
//        LOGW("<<<>>>>has send data %d, but not equal %d",len,data_len);
        } else {
            LOGD("send data to uart: %d, fd is %d", len, fd);
        }
        return len;
    }


    static void uartClose(int fd) {
        close(fd);
    }


    static int uartOpen(char const *deviceName) {
        LOGE("uartOpen()-->:deviceName = %s", deviceName);
        int fd = open(deviceName, O_RDWR | O_NONBLOCK);
        if (fd < 0) {
            LOGE("uartOpen()-->:fd open failure");
            return -1;
        }

        LOGW("uartOpen()-->: open device success");
        return fd;


    }

    int set_mode(int nMode, int showLog, int mTtyfd) {

        LOGW("set_mode:nMode%d,nshowLog=%d", nMode, showLog);
        struct termios options;
        struct termios options_read;

        if (tcgetattr(mTtyfd, &options) != 0) {
            LOGE("setup serial failure");
            return -1;
        }

        if (false && showLog == 1) {
            LOGI("=============read termios=============");
            LOGI("options c_cflag.CS7:%d,CS8:%d", options.c_cflag & CS7, options.c_cflag & CS8);
            LOGI("options c_cflag.PARENB:%d,PARODD:%d", options.c_cflag & PARENB,
                 options.c_cflag & PARODD);
            LOGI("options c_iflag.INPCK%d,ISTRIP:%d", options.c_iflag & INPCK,
                 options.c_iflag & ISTRIP);
            LOGI("option c_ispeed:%d,c_ospeed:%d", cfgetispeed(&options), cfgetospeed(&options));
            LOGI("options c_cflag.CSTOPB:%d,", options.c_cflag & CSTOPB);
            LOGI("options c_cc.VTIME:%d,VMIN:%d", options.c_cc[VTIME], options.c_cc[VMIN]);
            LOGI("options c_cflag.CLOCAL:%d,CREAD:%d", options.c_cflag & CLOCAL,
                 options.c_cflag & CREAD);
            LOGI("options c_lflag.ICANON:%d,ECHO:%d,ECHOE:%d,ISIG:%d", options.c_lflag & ICANON,
                 options.c_lflag & ECHO, options.c_lflag & ECHOE, options.c_lflag & ISIG);
            LOGI("options c_oflag.OPOST:%d,", options.c_oflag & OPOST);
            LOGI("=============read termios endi=============");
        }


        if (nMode == 0) {
            options.c_iflag &= ~(IXON | IXOFF);
            options.c_cflag &= ~(CRTSCTS);
        } else if (nMode == 1) {
            options.c_iflag |= (IXON | IXOFF);
            options.c_cflag &= ~(CRTSCTS);
        } else if (nMode == 2) {
            options.c_iflag &= ~(IXON | IXOFF);
            options.c_cflag |= (CRTSCTS);
        } else if (nMode == 3) {
            options.c_iflag |= (IXON | IXOFF);
            options.c_cflag |= (CRTSCTS);
        }

        if (tcsetattr(mTtyfd, TCSANOW, &options) != 0) {
            LOGE("tcsetattr device fail");
            return -1;
        }

        if (tcgetattr(mTtyfd, &options_read) != 0) {
            LOGE("setup serial failure");
            return -1;
        }

        if (false && showLog == 1) {
            LOGI("=============write termios=============");
            LOGI("options_read c_cflag.CS7:%d,CS8:%d", options_read.c_cflag & CS7,
                 options_read.c_cflag & CS8);
            LOGI("options_read c_cflag.PARENB:%d,PARODD:%d", options_read.c_cflag & PARENB,
                 options_read.c_cflag & PARODD);
            LOGI("options_read c_iflag.INPCK%d,ISTRIP:%d", options_read.c_iflag & INPCK,
                 options_read.c_iflag & ISTRIP);
            LOGI("options_read c_ispeed:%d,c_ospeed:%d", cfgetispeed(&options_read),
                 cfgetospeed(&options_read));
            LOGI("options_read c_cflag.CSTOPB:%d,", options_read.c_cflag & CSTOPB);
            LOGI("options_read c_cc.VTIME:%d,VMIN:%d", options_read.c_cc[VTIME],
                 options_read.c_cc[VMIN]);
            LOGI("options c_cflag.CLOCAL:%d,CREAD:%d", options_read.c_cflag & CLOCAL,
                 options_read.c_cflag & CREAD);
            LOGI("options_read c_lflag.ICANON:%d,ECHO:%d,ECHOE:%d,ISIG:%d",
                 options_read.c_lflag & ICANON, options_read.c_lflag & ECHO,
                 options_read.c_lflag & ECHOE, options_read.c_lflag & ISIG);
            LOGI("options_read c_oflag.OPOST:%d,", options_read.c_oflag & OPOST);
            LOGI("=============write termios end=============");
        }

        return 0;

    }

    static int uart_readable(int timeout, int fd) {
        int ret;
        fd_set set;
        struct timeval tv = {timeout / 1000, (timeout % 1000) * 1000};

        FD_ZERO (&set);
        FD_SET (fd, &set);

        ret = select(fd + 1, &set, NULL, NULL, &tv);

        if (ret > 0) {
            return 1;
        }

        return 0;
    }

    static int uart_read(char *buf, int size, int timeout, int fd) {
//	LOGI(" ===> [UART] read size %d ; timeout %d ms......", size, timeout);

        int got = 0, ret;
        do {
            ret = read(fd, buf + got, size - got);
            //     LOGI("ret %d.....",ret);
            if (ret > 0) {
                got = ret;
                //        LOGI(" ===> [UART] got : %d bytes:  ",got);
                //        LOGI(" ===> [UART] buff1: %s ....", buf);
                break;
            }
//        if (got >= size) {
//         break;
//        }
            //       if (ret > 0 ) {
            //      LOGI(" ===> [UART] got: %d bytes ",got);
            //          LOGI("uart_read: buff1 %s....",buf);
            //          got += ret;
            //          break;
            //     }
        } while (uart_readable(timeout, fd));

        //LOGI("got %d.....",got);
        //LOGI("uart_read: buff1 %s....",buf);
        return got;
    }

    static int uartread(char *readBuff, int buffSize, int fd, int uartBlock) {
        int readCount = 0;
        if (uartBlock == O_NONBLOCK) {
            readCount = uart_read(readBuff, buffSize, 10, fd);
        } else {
            readCount = read(fd, readBuff, buffSize);
        }
        if (readCount < 0) {
            LOGI("read uart data under NONBLOCK error: %d", readCount);
        } else {
            LOGD("read uart data: %d; fd is %d", readCount, fd);
        }
        return readCount;
    }

    static int uart_select(JNIEnv *env, jobject clazz, jint timeout, jint fd) {
        int ret;
        fd_set set;
        struct timeval tv = {timeout / 1000, (timeout % 1000) * 1000};

        FD_ZERO (&set);
        FD_SET (fd, &set);

        ret = select(fd + 1, &set, NULL, NULL, &tv);

        if (ret > 0) {
            return 1;
        }

        return 0;
    }

    static int open2(JNIEnv *env, jobject clazz, jstring uartName) {
        const char *uartNameStr = env->GetStringUTFChars(uartName, 0);
        int openInt = uartOpen(uartNameStr);
        return openInt;
    }

    static void close(JNIEnv *env, jobject clazz, jint fd) {
        uartClose(fd);
    }

    static int setBlock(JNIEnv *env, jobject clazz, jint blockmode, jint fd) {
        int oldflags = fcntl(fd, F_GETFL, 0);
        if (oldflags == -1) {
            LOGE("serial set block error");
            return -1;
        }
        if (blockmode == 0) {
            oldflags |= O_NONBLOCK;
        } else {
            oldflags &= ~O_NONBLOCK;
        }
        int setFlags = fcntl(fd, F_SETFL, oldflags);
        LOGI("serial set block value=%d", setFlags);
        return 0;
    }

    static int
    setSerialPortParams(JNIEnv *env, jobject clazz, jint baudrate, jint dataBits, jint stopBits,
                        jint parity, jint fd) {
        return uartSetSerial(baudrate, dataBits, stopBits, parity, fd);
    }

    static int setFlowControlMode(JNIEnv *env, jobject clazz, jint flowcontrol, jint fd) {
        LOGI("serial set flow control: %d", flowcontrol);
        return set_mode(flowcontrol, 0, fd);
    }


    static int
    read2(JNIEnv *env, jobject clazz, jbyteArray buf, jint bufSize, jint fd, jint uartBlock) {
        LOGI("read data from uart to buffer[%d]", bufSize);
        jbyte *arrayData = (jbyte *) env->GetByteArrayElements(buf, 0);
        int readCount = uartread((char *) arrayData, bufSize, fd, uartBlock);
        env->ReleaseByteArrayElements(buf, arrayData, 0);
        return readCount;
    }

    static int write(JNIEnv *env, jobject clazz, jbyteArray buf, jint bufSize, jint fd) {
        jbyte *arrayData = (jbyte *) env->GetByteArrayElements(buf, 0);
        jsize arrayLength = env->GetArrayLength(buf);
        char *byteData = (char *) arrayData;
        int len = (int) arrayLength;
        int writeCount = uartWrite(byteData, bufSize, fd);
        env->ReleaseByteArrayElements(buf, arrayData, 0);
        return writeCount;
    }

    static JNINativeMethod method_table[] = {
            {"close_native",               "(I)V",                  (void *) close},
            {"open_native",                "(Ljava/lang/String;)I", (void *) open2},
            {"setBlock_native",            "(II)I",                 (void *) setBlock},
            {"setSerialPortParams_native", "(IIIII)I",              (void *) setSerialPortParams},
            {"setFlowControlMode_native",  "(II)I",                 (void *) setFlowControlMode},
            {"select_native",              "(II)I",                 (void *) uart_select},
            {"read_native",                "([BIII)I",              (void *) read2},
            {"write_native",               "([BII)I",               (void *) write},
    };

    extern "C"
    jint
    Java_com_styl_pa_modules_peripheralsManager_uart_config_UartService_write_1native(JNIEnv *env,
                                                                                      jobject instance,
                                                                                      jbyteArray buf_,
                                                                                      jint writesize,
                                                                                      jint fd) {
        jbyte *arrayData = (jbyte *) env->GetByteArrayElements(buf_, 0);

        int writeCount = uartWrite((char *) arrayData, writesize, fd);

        //release buf
        env->ReleaseByteArrayElements(buf_, (jbyte *) arrayData, 0);

        return writeCount;

        //return uartWrite((char *) arrayData, writesize, fd);
    }

    extern "C"
    jint
    Java_com_styl_pa_modules_peripheralsManager_uart_config_UartService_open_1native(JNIEnv *env,
                                                                                     jobject instance,
                                                                                     jstring uartName) {

        const char *s = env->GetStringUTFChars(uartName, 0);
        char item_value[128];
        strcpy(item_value, s);
        env->ReleaseStringUTFChars(uartName, s);
        LOGD("item_value = %s", item_value);
        return uartOpen(item_value);
    }

    extern "C"
    jbyteArray
    Java_com_styl_pa_modules_peripheralsManager_uart_config_UartService_read_1data_1native(
            JNIEnv *env, jobject instance,
            jint timeout, jint fd) {
        jbyteArray buf = env->NewByteArray(1024);
        jsize arrayLength = env->GetArrayLength(buf);
        int bufsize = (int) arrayLength;

        char *data = (char *) env->GetByteArrayElements(buf, NULL);

        // char* data = (char*)env->GetByteArrayElements(buf_, NULL);
        int num = uart_read(data, bufsize, timeout, fd);


        jbyteArray arr = env->NewByteArray(num);
        env->SetByteArrayRegion(arr, 0, num, (jbyte *) data);

        //release buf
        env->ReleaseByteArrayElements(buf, (jbyte *) data, 0);
        return arr;
    }

    extern "C"
    void
    Java_com_styl_pa_modules_peripheralsManager_uart_config_UartService_close_1native(JNIEnv *env,
                                                                                      jobject instance,
                                                                                      jint fd) {
        uartClose(fd);
    }


    extern "C"
    jint
    Java_com_styl_pa_modules_peripheralsManager_uart_config_UartService_setSerialPortParams_1native(
            JNIEnv *env,
            jobject instance,
            jint baudrate, jint dataBits,
            jint stopBits, char parity,
            jint fd) {

        jint mint = uartSetSerial(baudrate, dataBits, stopBits, parity, fd);
        LOGD("mint = %d", mint);
        return mint;
    }


    int stylGetUsbPortNumber(char *comPortPath, char *usbPort) {
        char *bus_port;
        char *path_temp;
        char portPath[COM_PATH_LENGTH];
        char usbport[PORT_LENGTH];

        memset(portPath, '\0', sizeof(portPath));
        memset(usbport, '\0', sizeof(usbport));
        strncpy(portPath, comPortPath, strlen(comPortPath));

        path_temp = strtok(portPath, ":");
        if (path_temp == NULL) {
            return -1;
        }

        bus_port = strtok(path_temp, "/");
        while (bus_port != NULL) {
            memset(usbport, '\0', sizeof(usbport));
            strncpy(usbport, bus_port, strlen(bus_port));
            bus_port = strtok(NULL, "/");
        }

        strncpy(usbPort, usbport, strlen(usbport));
        __android_log_print(ANDROID_LOG_ERROR, "stylGetUsbPortNumber: usbPort= [%s]\n", "%s",
                            usbPort);

        return 0;
    }

    int stylGetBusUsbDevice(char *comPortPath, char *pidVidPath) {
        char *path_temp;
        char portPath[COM_PATH_LENGTH];
        char port_usb[PORT_LENGTH];

        memset(portPath, '\0', sizeof(portPath));
        memset(port_usb, '\0', sizeof(port_usb));

        strncpy(portPath, comPortPath, strlen(comPortPath));
        stylGetUsbPortNumber(portPath, port_usb);
        path_temp = strtok(portPath, ":");
        if (path_temp == NULL) {
            return -1;
        }

        strncpy(pidVidPath, path_temp, strlen(path_temp) - strlen(port_usb) - 1);
        __android_log_print(ANDROID_LOG_ERROR, "stylGetBusUsbDevice: pidVidPath = [%s]\n", "%s",
                            pidVidPath);
        return 0;
    }

    int stylGetComPortName(char *comPortPath, char *comPortName) {
        char *port_name;
        char portPath[COM_PATH_LENGTH];
        char port[PORT_LENGTH];

        memset(portPath, '\0', sizeof(portPath));
        memset(port, '\0', sizeof(port));
        strncpy(portPath, comPortPath, strlen(comPortPath));
        port_name = strtok(portPath, "/");
        while (port_name != NULL) {
            memset(port, '\0', sizeof(port));
            strncpy(port, port_name, strlen(port_name));
            port_name = strtok(NULL, "/");
        }
        strncpy(comPortName, port, strlen(port));
        __android_log_print(ANDROID_LOG_ERROR, "-----", "stylGetUsbPortNumber: comPortName= [%s]\n",
                            comPortName);
        return 0;
    }

    int stylReadInformationFile(char *filePath, char *fileContent, int bufferSize) {
        FILE *infoFile;
        int fileSize = 0, readBytes = 0;
        char *fileBuffer;

        infoFile = fopen(filePath, "r");
        if (NULL == infoFile) {
//        LOG_PRINT("[%s] Open file [%s] ---> Failed", __func__ , filePath);
            return -1;
        }

        fseek(infoFile, 0, SEEK_END);
        fileSize = ftell(infoFile);
        if (fileSize > bufferSize) {
            // LOG_PRINT("[%s] Buffer to store FileContent [%d] is less than fileSize[%d] ", __func__ , bufferSize, fileSize);
            readBytes = bufferSize;
            // goto exit;
        } else {
            readBytes = fileSize;
        }

        fseek(infoFile, 0, SEEK_SET);

        fileBuffer = (char *) calloc(fileSize, sizeof(char));
        if (NULL == fileBuffer) {
//        LOG_PRINT("[%s] Malloc buffer for file [%s] ---> Failed", __func__ , filePath);
            goto exit;
        }

        if (NULL == fgets(fileBuffer, readBytes, infoFile)) {
//        LOG_PRINT("[%s] Read file [%s] ---> Failed", __func__ , filePath);
            goto exit;
        }

        strncpy(fileContent, fileBuffer, strlen(fileBuffer));
        if (fileContent[strlen(fileBuffer) - 1] == '\n') {
            fileContent[strlen(fileBuffer) - 1] = '\0';
        }

        // LOG_PRINT("[%s] Content of [%s] --> [%s]", __func__ , filePath, fileContent);

        exit:
        if (fileBuffer) {
            free(fileBuffer);
        }

        if (infoFile) {
            fclose(infoFile);
        }
        return 0;

    }


    int stylGetComPorts(const char *pid, const char *vid, stylComPortInfo_t *comPortInfo,
                        int *portCount) {

        int retVal = -1;
        char path[COM_PATH_LENGTH], pid_cmd[COM_PATH_LENGTH], vid_cmd[COM_PATH_LENGTH];
        int dev_cnt = 0;
        char busUSBDevice[COM_PATH_LENGTH], usbPortNumber[PORT_LENGTH];
        char comPortPath[COM_PATH_LENGTH], comPortName[COMPORT_NAME_LENGTH];
        char devPortName[COMPORT_NAME_LENGTH];

        char idProductFile[COM_PATH_LENGTH], idProduct[COMPORT_NAME_LENGTH];
        char idVendorFile[COM_PATH_LENGTH], idVendor[COMPORT_NAME_LENGTH];
        char SerialNumberFile[COM_PATH_LENGTH], SerialNumber[USB_SERIAL_LENGTH];

        FILE *fpath, *fpid, *fvid;
        fpath = popen("find /sys/devices/platform -name \"ttyACM*\" -or -name \"ttyUSB*\"", "r");

        if (fpath == NULL) {
            *portCount = 0;
            return -1;
        }

        while (fgets(path, sizeof(path), fpath) != NULL) {
            path[strlen(path) - 1] = 0;

            memset(comPortPath, '\0', sizeof(comPortPath));
            memset(busUSBDevice, '\0', sizeof(busUSBDevice));
            memset(usbPortNumber, '\0', sizeof(usbPortNumber));
            memset(comPortName, '\0', sizeof(comPortName));
            memset(idProductFile, '\0', sizeof(idProductFile));
            memset(idProduct, '\0', sizeof(idProduct));
            memset(idVendorFile, '\0', sizeof(idVendorFile));
            memset(idVendor, '\0', sizeof(idVendor));
            memset(SerialNumberFile, '\0', sizeof(SerialNumberFile));
            memset(SerialNumber, '\0', sizeof(SerialNumber));

            strncpy(comPortPath, path, strlen(path));

            if (stylGetBusUsbDevice(comPortPath, busUSBDevice) < 0) {
                continue;
            }

            if (stylGetUsbPortNumber(comPortPath, usbPortNumber) < 0) {
                continue;
            }

            stylGetComPortName(comPortPath, comPortName);

            sprintf(idProductFile, "%s/idProduct", busUSBDevice);
            stylReadInformationFile(idProductFile, idProduct, sizeof(idProduct));
            sprintf(idVendorFile, "%s/idVendor", busUSBDevice);
            stylReadInformationFile(idVendorFile, idVendor, sizeof(idProduct));
            sprintf(SerialNumberFile, "%s/serial", busUSBDevice);
            stylReadInformationFile(SerialNumberFile, SerialNumber, sizeof(idProduct));

            __android_log_print(ANDROID_LOG_ERROR,
                                "",
                                "-----------------------------------------------------------------------------idProduct %s",
                                idProduct);

            __android_log_print(ANDROID_LOG_ERROR,
                                "",
                                "-----------------------------------------------------------------------------idVendor %s",
                                idVendor);
            __android_log_print(ANDROID_LOG_ERROR,
                                "",
                                "-----------------------------------------------------------------------------idSerial %s",
                                SerialNumber);

            if (strlen(pid) != strlen(idProduct) || strlen(vid) != strlen(idVendor)) {
                continue;
            }

            if (memcmp(pid, idProduct, strlen(pid)) == 0 &&
                memcmp(vid, idVendor, strlen(vid)) == 0) {
                memset(devPortName, '\0', sizeof(devPortName));
                sprintf(devPortName, "/dev/%s", comPortName);

                strncpy(comPortInfo[dev_cnt].comPath, comPortPath, strlen(comPortPath));
                strncpy(comPortInfo[dev_cnt].pidVidPath, busUSBDevice, strlen(busUSBDevice));
                strncpy(comPortInfo[dev_cnt].plugPort, usbPortNumber, strlen(usbPortNumber));
                strncpy(comPortInfo[dev_cnt].portName, devPortName, strlen(devPortName));
                strncpy(comPortInfo[dev_cnt].serial, SerialNumber, strlen(SerialNumber));

                dev_cnt++;
                retVal = 0;
                if (dev_cnt >= NUMBER_USB_DEVICEs) break;
            }
        }

        pclose(fpath);
        *portCount = dev_cnt;
        __android_log_print(ANDROID_LOG_ERROR,
                            "-----------------------------------------------------------------------------",
                            "");
        __android_log_print(ANDROID_LOG_ERROR, "<The number COM port of [%s:%s] is [%d] >", vid,
                            pid, *portCount);

        for (int i = 0; i < dev_cnt; ++i) {
            __android_log_print(ANDROID_LOG_ERROR,
                                "************************************************************", "");
            __android_log_print(ANDROID_LOG_ERROR, "comPath:           [%s] ", "%s",
                                comPortInfo[i].comPath);
            __android_log_print(ANDROID_LOG_ERROR, "USBDevicePath:     [%s] ", "%s",
                                comPortInfo[i].pidVidPath);
            __android_log_print(ANDROID_LOG_ERROR, "USBPort:           [%s] ", "%s",
                                comPortInfo[i].plugPort);
            __android_log_print(ANDROID_LOG_ERROR, "comName:           [%s] ", "%s",
                                comPortInfo[i].portName);
            __android_log_print(ANDROID_LOG_ERROR, "serialNumber:      [%s] ", "%s",
                                comPortInfo[i].serial);
            __android_log_print(ANDROID_LOG_ERROR,
                                "************************************************************", "");
        }
//        LOG_PRINT_INFO(
//                "-----------------------------------------------------------------------------");
        return retVal;
    }

    /*_____________ stylGetComport ______________________________________________________________________________________________*/
    int stylGetComport(const char *pid, const char *vid, char *portname) {

        int retVal = -1;
        char path[COM_PATH_LENGTH];
        int dev_cnt = 0;
        char busUSBDevice[COM_PATH_LENGTH], usbPortNumber[PORT_LENGTH];
        char comPortPath[COM_PATH_LENGTH], comPortName[COMPORT_NAME_LENGTH];
        char devPortName[COMPORT_NAME_LENGTH];

        char idProductFile[COM_PATH_LENGTH], idProduct[COMPORT_NAME_LENGTH];
        char idVendorFile[COM_PATH_LENGTH], idVendor[COMPORT_NAME_LENGTH];
        char SerialNumberFile[COM_PATH_LENGTH], SerialNumber[USB_SERIAL_LENGTH];
        stylComPortInfo_t comPortInfo[NUMBER_USB_DEVICEs];

        FILE *fpath;
        fpath = popen("find /sys/devices/platform -name \"ttyACM*\" -or -name \"ttyUSB*\"", "r");

        if (fpath == NULL) {
            return -1;
        };

        memset(comPortInfo, '\0', sizeof(comPortInfo));

        while (fgets(path, sizeof(path), fpath) != NULL) {
            path[strlen(path) - 1] = 0;

            memset(comPortPath, '\0', sizeof(comPortPath));
            memset(busUSBDevice, '\0', sizeof(busUSBDevice));
            memset(usbPortNumber, '\0', sizeof(usbPortNumber));
            memset(comPortName, '\0', sizeof(comPortName));
            memset(idProductFile, '\0', sizeof(idProductFile));
            memset(idProduct, '\0', sizeof(idProduct));
            memset(idVendorFile, '\0', sizeof(idVendorFile));
            memset(idVendor, '\0', sizeof(idVendor));
            memset(SerialNumberFile, '\0', sizeof(SerialNumberFile));
            memset(SerialNumber, '\0', sizeof(SerialNumber));

            strncpy(comPortPath, path, strlen(path));

            if (stylGetBusUsbDevice(comPortPath, busUSBDevice) < 0) {
                continue;
            }

            if (stylGetUsbPortNumber(comPortPath, usbPortNumber) < 0) {
                continue;
            }

            stylGetComPortName(comPortPath, comPortName);

            sprintf(idProductFile, "%s/idProduct", busUSBDevice);
            __android_log_print(ANDROID_LOG_ERROR, "%s/idProduct", "%s/idProduct", busUSBDevice);
            stylReadInformationFile(idProductFile, idProduct, sizeof(idProduct));

            sprintf(idVendorFile, "%s/idVendor", busUSBDevice);
            stylReadInformationFile(idVendorFile, idVendor, sizeof(idProduct));

            sprintf(SerialNumberFile, "%s/serial", busUSBDevice);
            stylReadInformationFile(SerialNumberFile, SerialNumber, sizeof(idProduct));

            __android_log_print(ANDROID_LOG_ERROR,
                                "",
                                "-----------------------------------------------------------------------------idProduct %s",
                                idProduct);

            __android_log_print(ANDROID_LOG_ERROR,
                                "",
                                "-----------------------------------------------------------------------------idVendor %s",
                                idVendor);
            __android_log_print(ANDROID_LOG_ERROR,
                                "",
                                "-----------------------------------------------------------------------------idSerial %s",
                                SerialNumber);

            if (strlen(pid) != strlen(idProduct) || strlen(vid) != strlen(idVendor)) {
                continue;
            }

            if (memcmp(pid, idProduct, strlen(pid)) == 0 &&
                memcmp(vid, idVendor, strlen(vid)) == 0) {
                memset(devPortName, '\0', sizeof(devPortName));
                sprintf(devPortName, "%s", comPortName);

                strncpy(comPortInfo[dev_cnt].comPath, comPortPath, strlen(comPortPath));
                strncpy(comPortInfo[dev_cnt].pidVidPath, busUSBDevice, strlen(busUSBDevice));
                strncpy(comPortInfo[dev_cnt].plugPort, usbPortNumber, strlen(usbPortNumber));
                strncpy(comPortInfo[dev_cnt].portName, devPortName, strlen(devPortName));
                strncpy(comPortInfo[dev_cnt].serial, SerialNumber, strlen(SerialNumber));

                dev_cnt++;
                retVal = 0;

                __android_log_print(ANDROID_LOG_ERROR, "F:      [%s] ", "+++++++++++++%s",
                                    devPortName);

                break;
            } else {

                __android_log_print(ANDROID_LOG_ERROR,
                                    "",
                                    "-----------------------------------------------------------------------------memcmp(pid, idProduct, strlen(pid)) %d",
                                    memcmp(pid, idProduct, strlen(pid)));

                __android_log_print(ANDROID_LOG_ERROR,
                                    "",
                                    "----------------------------------------------------------------------------- memcmp(vid, idVendor, strlen(vid)) %d",
                                    memcmp(vid, idVendor, strlen(vid)));

            }
        }

        pclose(fpath);
        if (dev_cnt > 0) {

            memcpy(portname, comPortInfo[0].portName, strlen(comPortInfo[0].portName));
        }

        __android_log_print(ANDROID_LOG_ERROR,
                            "",
                            "-----------------------------------------------------------------------------");
        __android_log_print(ANDROID_LOG_ERROR, "The number COM",
                            "<The number COM port of [%s:%s] is [%d] >", vid,
                            pid, dev_cnt);

        for (int i = 0; i < dev_cnt; ++i) {
            __android_log_print(ANDROID_LOG_ERROR,
                                "", "************************************************************");
            __android_log_print(ANDROID_LOG_ERROR, "comPath:           [%s] ",
                                "comPath:           %s",
                                comPortInfo[i].comPath);
            __android_log_print(ANDROID_LOG_ERROR, "USBDevicePath:     [%s] ", "USBDevicePath: %s",
                                comPortInfo[i].pidVidPath);
            __android_log_print(ANDROID_LOG_ERROR, "USBPort:           [%s] ", "USBPort: %s",
                                comPortInfo[i].plugPort);
            __android_log_print(ANDROID_LOG_ERROR, "comName:           [%s] ", "comName: %s",
                                comPortInfo[i].portName);
            __android_log_print(ANDROID_LOG_ERROR, "serialNumber:      [%s] ", "serialNumber:  %s",
                                comPortInfo[i].serial);
            __android_log_print(ANDROID_LOG_ERROR,
                                "", "************************************************************");
        }
        __android_log_print(ANDROID_LOG_ERROR,
                            "-----------------------------------------------------------------------------",
                            "");
        __android_log_print(ANDROID_LOG_ERROR, "portname:      [%s] ", "portname: %s", portname);
        return retVal;
    }




    // Get Com Port
    extern "C" JNIEXPORT jstring JNICALL
    Java_com_styl_pa_modules_peripheralsManager_uart_config_UartService_stylGetComport(JNIEnv *env,
                                                                                       jobject obj,
                                                                                       jstring pid,
                                                                                       jstring vid) {
        const char *mPid = env->GetStringUTFChars(pid, (jboolean *) false);

        const char *mVid = env->GetStringUTFChars(vid, (jboolean *) false);

        char mPortName[COMPORT_NAME_LENGTH] = {'\0'};
        memset(mPortName, '\0', sizeof(mPortName));

        stylGetComport((char *) mPid, (char *) mVid, mPortName);

        env->ReleaseStringUTFChars(pid, mPid);
        env->ReleaseStringUTFChars(vid, mVid);

        __android_log_print(ANDROID_LOG_ERROR, "mPortName: -", "-----------------------%s",
                            mPortName);


        return env->NewStringUTF(mPortName);
    }

    extern "C" JNIEXPORT jobjectArray JNICALL
    Java_com_styl_pa_modules_peripheralsManager_uart_config_UartService_stylGetComPorts(JNIEnv *env,
                                                                                        jobject obj,
                                                                                        jstring pid,
                                                                                        jstring vid) {
        const char *mPid = env->GetStringUTFChars(pid, (jboolean *) false);
        const char *mVid = env->GetStringUTFChars(vid, (jboolean *) false);
        stylComPortInfo_t comPortInfo[NUMBER_USB_DEVICEs];
        jobjectArray ret;
        int i = 0, deviceCount = 0;

        memset(comPortInfo, '\0', sizeof(comPortInfo));

        stylGetComPorts((char *) mPid, (char *) mVid, (stylComPortInfo_t *) comPortInfo,
                        &deviceCount);

        env->ReleaseStringUTFChars(pid, mPid);
        env->ReleaseStringUTFChars(vid, mVid);

        ret = (jobjectArray) env->NewObjectArray(NUMBER_USB_DEVICEs,
                                                 env->FindClass("java/lang/String"),
                                                 env->NewStringUTF(""));

        for (i = 0; i < deviceCount; i++) {
            env->SetObjectArrayElement(ret, i, env->NewStringUTF(comPortInfo[i].portName));
        }
        return (ret);
    }

};
