/**
 * @defgroup SerialProtocol Serial Protocol
 * @brief Serial Protocol
 *
 * @{
 */

/**
 * @defgroup SerialComm Serial Communication
 * @ingroup SerialProtocol
 * @brief Serial Communication
 *
 * Basic communication API for Serial port
 *
 * @{
 */

/*
 * @file stylComm.h
 * @brief
 *
 * @date 	Dec 15, 2015
 * @author
 */
#ifndef _STYLCOMM_H_
#define _STYLCOMM_H_

#ifdef __cplusplus
extern "C"
{
#endif


/********** Include section **************************************************************************************************/

#if (PLATFORM_SEL_LINUX == 1)
#include <stdio.h>   /* Standard input/output definitions */
#include <string.h>  /* String function definitions */
#include <unistd.h>  /* UNIX standard function definitions */
#include <fcntl.h>   /* File control definitions */
#include <errno.h>   /* Error number definitions */
#include <termios.h> /* POSIX terminal control definitions */
#include <linux/serial.h>
#include <sys/ioctl.h>

#include <pthread.h>
#include <semaphore.h>
#include <sys/queue.h>
#include <sys/msg.h>
#include <unistd.h>
#include <malloc.h>
#endif

/**
 * @defgroup SerialCommDef Serial Communication Definition
 * @ingroup SerialComm
 * @{
 */
/********** Constant  and compile switch definition section ******************************************************************/
/**
 * @defgroup SerialCommConst Serial Communication Constants Definition
 * @ingroup SerialCommDef
 * @{
 */
#if (PLATFORM_SEL_LINUX == 1)
#define SERIAL_HOST_DEVICE_PORT_CDC				""
#define SERIAL_PORT_DBG_TO_HOST					""
#define SERIAL_HOST_PORT_CHANNEL				""
#endif //(PLATFORM_SEL_LINUX == 1)

#if (PLATFORM_SEL_WINDOWS == 1)
#define SERIAL_HOST_DEVICE_PORT_CDC				""
#define SERIAL_PORT_DBG_TO_HOST					""
#define SERIAL_HOST_DEVICE_PORT_PHY				""
#define SERIAL_RF_PROCESSOR_PORT_CMD			""
#define SERIAL_RF_PROCESSOR_PORT_DBG			""
#endif //(PLATFORM_SEL_WINDOWS == 1)

/** @} */ // End of 'SerialCommConst' group

/********** Type definition section ******************************************************************************************/
/**
 * @defgroup SerialCommEnum Serial Communication Enumeration Definition
 * @ingroup SerialCommDef
 * @{
 */
/*! @brief: declare frame type that support */
typedef enum {
    FR_8BIT,
    FR_7BIT,
    FR_6BIT,
    FR_5BIT,

} stylFrameType_t;

/*! @brief: declare parity that support */
typedef enum {
    PARITY_NONE,
    PARITY_SPACE,
    PARITY_EVEN,
    PARITY_ODD,

} stylParity_t;

/*! @brief: declare stop bit that support */
typedef enum {
    STOP_1BIT,
    STOP_2BIT,

} stylStopbit_t;

/** @} */ // End of 'SerialCommEnum' group

/**
 * @defgroup SerialCommTypes Serial Communication Types Definition
 * @ingroup SerialCommDef
 * @{
 */
#if (PLATFORM_SEL_LINUX == 1)
/*! \brief Declare function pointer to stylCIComm_Windows_SerialRead or stylCIComm_Linux_SerialRead */
typedef stylErrorCode_t (* serialReadFn)(Void* dataBuff, UInt32 lenToRead, Int32* lenReaded, Int32 timeOutMs);

/*! \brief Declare function pointer to stylCIComm_Windows_SerialWrite or stylCIComm_Linux_SerialWrite */
typedef stylErrorCode_t (* serialWriteFn)(Const Void* dataBuff, UInt32 lenToWrite, UInt32* lenWritten, Int32 timeOutMs);

/*! \brief Declare function pointer to stylCIComm_Windows_SerialInit or stylCIComm_Linux_SerialInit */
typedef stylErrorCode_t (* serialInitFn)(void);

/*! \brief Declare function pointer to stylCIComm_Windows_SerialDeinit or stylCIComm_Linux_SerialDeinit */
typedef stylErrorCode_t (* serialDeinitFn)(void);

/*! \brief Declare function pointer to stylCIComm_Windows_SerialFlush or stylCIComm_Linux_SerialFlush */
typedef stylErrorCode_t (* serialFlushFn)(void);

/*! \brief Declare a struct contain of function pointers */
typedef struct stylCIPCommConf
{
    serialInitFn	fnSerialInit;		/** Function pointer to serial initialize */
    serialDeinitFn	fnSerialDeinit;		/** Function pointer to serial deInitialize */
    serialReadFn	fnSerialRead;		/** Function pointer to serial read */
    serialWriteFn	fnSerialWrite;		/** Function pointer to serial write */
    serialFlushFn   fnSerialFlush;		/** Function pointer to serial flush */

} stylCICommConf_t;
#endif

/*! \brief Declare a struct contain of information of COM Port */
#define  NUMBER_USB_DEVICEs      64

#define  USB_SERIAL_LENGTH      256
#define  COM_PATH_LENGTH        512
#define  PID_VID_PATH_LENGTH    512
#define  PORT_LENGTH            64
#define  COMPORT_NAME_LENGTH    64

typedef struct stylComPortInfo {
    char comPath[COM_PATH_LENGTH];       /** Full path of COM Port in device platfom */
    char pidVidPath[PID_VID_PATH_LENGTH];    /** Path to get PID VID info */
    char plugPort[PORT_LENGTH];        /** Port number that usb had been plugged */
    char portName[COMPORT_NAME_LENGTH];        /** Com port Name */
    char serial[USB_SERIAL_LENGTH];        /** Serial of USB device */

} stylComPortInfo_t;

/*! \brief Serial Information Structure*/
typedef struct {
#if (PLATFORM_SEL_LINUX == 1)
    char *portName;
    int fd;
    struct termios options;
    stylBaudrate_t baudrate;
    stylFrameType_t frametype;
    stylParity_t parity;
    stylStopbit_t stopbit;
    Bool hwFlowCtrlFlag;
    Bool swFlowCtrlFlag;
    Bool isUSB;
#elif (PLATFORM_SEL_WINDOWS == 1)
    WCHAR portName[100];
    LPCTSTR portFD;
    UInt32 baudrate;
    UInt8 initFlag;
#endif

} stylSerialInfo_t;

/** @} */ // End of 'SerialCommTypes' group

/********** Macro definition section******************************************************************************************/
/** @} */ // End of 'SerialCommDef' group

/**
 * @defgroup SerialCommFuncs Serial Communication Functions Declaration
 * @ingroup SerialCommDef
 * @{
 */
/********** Function declaration section *************************************************************************************/



/**
 * \brief Get pointer to Host's communication serial port struture
 * \return Pointer to Host's communication serial port struture
 */
stylSerialInfo_t *stylSerialGetDirectPort();

#if (PLATFORM_SEL_LINUX == 1)
/*!
 * \brief Check if USB CDC port has plugged in and readable
 * \param port: pointer to serial port structure
 * \return \link stylErrorCode Common Error Code \endlink
 * \retval STYL_SUCCESS: Success
 * \retval Otherwise: Error
 */
stylErrorCode_t stylLinuxCheckUsbReadable(stylSerialInfo_t *port);

/*!
 * \brief Check if USB CDC port has plugged in and writable
 * \param port: pointer to serial port structure
 * \return \link stylErrorCode Common Error Code \endlink
 * \retval STYL_SUCCESS: Success
 * \retval Otherwise: Error
 */
stylErrorCode_t stylLinuxCheckUsbWritable(stylSerialInfo_t *port);

/*!
 * \brief Parse config value in integer (cfg file) to stylBaudrate_t value
 * \param cfgBaudVal: UInt32 value of baud rate
 *
 * \return stylBaudrate_t baud rate value accordingly
 */
stylBaudrate_t stylLinuxSerialParseConfigFileValue2stylBaudrate(UInt32 cfgBaudVal);

/*!
 * \brief Parse config value in stylBaudrate_t to integer (cfg file) value
 * \param stylBaudVal: baud rate value
 *
 * \return UInt32 U32 value of baud rate
 */
UInt32 stylLinuxSerialParseConfigFileValue2U32(stylBaudrate_t stylBaudVal);

/*!
 * \brief Set blocking config pf serial port
 * \param port: pointer to serial port structure
 * \param should_block: blocking attribute
 *	- 1: set blocking
 *	- 0: set non-blocking
 * \param timeout: timeout attribute
 * \return Nothing
 */
Void set_blocking(stylSerialInfo_t *port, int should_block, int timeout);

/*!
 * \brief Read action on serial port
 * \param port: pointer to serial port structure
 * \param dataBuff: pointer to buffer contain read data
 * \param lenToRead: wanted len of read data
 * \param lenReaded: actual len of read data
 * \param timeOutMs: max timeout that need to read exact lenToRead bytes
 * \return \link stylErrorCode Common Error Code \endlink
 * \retval STYL_SUCCESS: Success
 * \retval Otherwise: Error
 */
stylErrorCode_t stylLinuxSerialRead(
        stylSerialInfo_t *port,
        Void* dataBuff,
        UInt32 lenToRead,
        UInt32* lenReaded,
        Int32 timeOutMs
);

/**
 * \brief Write a buffer to serial with lenToWrite length in timeOutMs. lenWritten is actual bytes written.
 *
 * \param port: pointer to serial port structure
 * \param dataBuff: a Const Void*, lenReaded a UInt32*
 * \param lenToWrite: number of bytes to write
 * \param lenWritten: number of actual bytes written
 * \param timeOutMs: not use param
 * \return \link stylErrorCode Common Error Code \endlink
 * \retval STYL_SUCCESS: Success
 * \retval Otherwise: Error
 */
stylErrorCode_t stylLinuxSerialWrite(
        stylSerialInfo_t *port,
        Const Void* dataBuff,
        UInt32 lenToWrite,
        UInt32* lenWritten,
        Int32 timeOutMs
);

/**
 * \brief Flush serial port buffer
 * \param port pointer to serial port structure
 * \return \link stylErrorCode Common Error Code \endlink
 * \retval STYL_SUCCESS: Success
 * \retval Otherwise: Error
 */
stylErrorCode_t stylLinuxSerialFlush(stylSerialInfo_t *port);

/**
 * \brief Blocking write for serial port
 * \param port pointer to serial port structure
 * \return \link stylErrorCode Common Error Code \endlink
 * \retval STYL_SUCCESS: All bytes written.
 * \retval STYL_ERROR: Error when trying to write.
 */
stylErrorCode_t stylLinuxSerialFlushWrite(stylSerialInfo_t *port);

/**
 * \brief Get pointer to AppProcessor's Command receiving serial port structure
 * \return pointer to AppProcessor's Command receiving serial port structure
 */
stylSerialInfo_t *stylSerialGetCmdChannel();

/**
 * \brief Set value of pointer to AppProcessor's Command receiving serial port
 * \param port port that Communication channel will point to
 * \return \link stylErrorCode Common Error Code \endlink
 * \retval STYL_SUCCESS: Set operation successful
 * \retval STYL_ERROR: Null pointer parameter
 */
stylErrorCode_t stylSerialSetCmdChannel(stylSerialInfo_t *port);

#endif //(PLATFORM_SEL_LINUX == 1)

#if (PLATFORM_SEL_WINDOWS == 1)

/*!
 * \brief Convert char* to wchar*
 * \param[in]	src: Source (char*) buffer
 * \param[out]	des: Destination (wchar*) buffer
 * \return Nothing
 */
Void stylUltil_CharToWChar(const Char *src, WCHAR *des);

/*!
 * \brief Convert wchar* to char*
 * \param[in]	src: Source (wchar*) buffer
 * \param[out]	des: Destination (char*) buffer
 * \return Nothing
 */
Void stylUltil_WCharToChar(const WCHAR *src, Char *des);

/*!
 * \brief Serial Read
 * \param[out]	dataBuff: response buffer
 * \param[in]	lenToRead: expected length to be read
 * \param[out]	lenReaded: response length
 * \param[in]	timeOutMs: read timeout in milliseconds
 * \return \link stylErrorCode Common Error Code \endlink
 * \retval STYL_SUCCESS: Success
 * \retval Otherwise: Error
 */
stylErrorCode_t stylCIComm_Windows_SerialRead(Void* dataBuff, UInt32 lenToRead, UInt32* lenReaded, Int32 timeOutMs);

/*!
 * \brief Serial Write
 * \param[in]	dataBuff: buffer contain data to be written
 * \param[in]	lenToWrite: length of data to be written
 * \param[out]	lenWritten: written length
 * \param[in]	timeOutMs: timeout in milliseconds
 * \return \link stylErrorCode Common Error Code \endlink
 * \retval STYL_SUCCESS: Success
 * \retval Otherwise: Error
 */
stylErrorCode_t stylCIComm_Windows_SerialWrite(Const Void* dataBuff, UInt32 lenToWrite, UInt32* lenWritten, Int32 timeOutMs);

/*!
 * \brief Serial Flush
 * \return \link stylErrorCode Common Error Code \endlink
 * \retval STYL_SUCCESS: Success
 * \retval Otherwise: Error
 */
stylErrorCode_t stylCIComm_Windows_SerialFlush();

#endif //(PLATFORM_SEL_WINDOWS == 1)

/** @} */ // End of 'SerialCommFuncs' group

#ifdef __cplusplus
}
#endif

#endif /* _STYLCOMM_H_ */

/*! @}*/ //End of 'SerialComm' group

/*! @}*/ //End of 'SerialProtocol' group
