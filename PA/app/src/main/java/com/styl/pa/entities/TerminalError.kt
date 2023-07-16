package com.styl.pa.entities

import com.styl.pa.R

class TerminalError {

    val terminalErrMap = HashMap<Int?, Int>()

    init {
        terminalErrMap[SUCCESS] = 0
        terminalErrMap[PENDING] = R.string.terminal_pending
        terminalErrMap[TIMEOUT] = R.string.terminal_timeout
        terminalErrMap[INVALID_PARAMETER] = R.string.terminal_invalid_parameter
        terminalErrMap[INCORRECT_FLOW] = R.string.terminal_incorrect_low
        terminalErrMap[MSG_INTEGRITY_FAILED] = R.string.terminal_msg_integrity_failed
        terminalErrMap[MSG_TYPE_NOT_SUPPORTED] = R.string.terminal_msg_type_not_supported
        terminalErrMap[MSG_CODE_NOT_SUPPORTED] = R.string.terminal_msg_msg_code_not_supported
        terminalErrMap[MSG_AUTHENTICATION_REQUIRED] = R.string.terminal_msg_authentication_required
        terminalErrMap[MSG_AUTHENTICATION_FAILED] = R.string.terminal_msg_authentication_required
        terminalErrMap[MSG_MAC_FAILED] = R.string.terminal_msg_mac_failed
        terminalErrMap[MSG_LENGTH_MISMATCH] = R.string.terminal_msg_length_mismatch
        terminalErrMap[MSG_LENGTH_MINIMUM] = R.string.terminal_msg_length_minimum
        terminalErrMap[MSG_LENGTH_MAXIMUM] = R.string.terminal_msg_length_maximum
        terminalErrMap[MSG_VERSION_INVALID] = R.string.terminal_msg_version_invalid
        terminalErrMap[MSG_CLASS_INVALID] = R.string.terminal_msg_class_invalid
        terminalErrMap[MSG_STATUS_INVALID] = R.string.terminal_msg_status_invalid
        terminalErrMap[MSG_ALGORITHM_INVALID] = R.string.terminal_msg_algorithm_invalid
        terminalErrMap[MSG_ALGORITHM_IS_MANDATORY] = R.string.terminal_msg_algorithm_is_mandatory
        terminalErrMap[MSG_KEY_INDEX_INVALID] = R.string.terminal_msg_key_index_invalid
        terminalErrMap[MSG_NOTIFICATION_INVALID] = R.string.terminal_msg_notification_invalid
        terminalErrMap[MSG_COMPLETION_INVALID] = R.string.terminal_msg_completion_invalid
        terminalErrMap[MSG_DATA_TRANSPARENCY_ERROR] = R.string.terminal_msg_data_transparency_error
        terminalErrMap[MSG_INCOMPLETE] = R.string.terminal_msg_incomplete
        terminalErrMap[FIELD_MANDATORY_MISSING] = R.string.terminal_field_mandatory_missing
        terminalErrMap[FIELD_TYPE_INVALID] = R.string.terminal_field_type_invalid
        terminalErrMap[FIELD_LENGTH_INVALID] = R.string.terminal_field_length_invalid
        terminalErrMap[FIELD_LENGTH_MINIMUM] = R.string.terminal_field_length_minimum
        terminalErrMap[FIELD_ENCODING_INVALID] = R.string.terminal_field_encoding_invalid
        terminalErrMap[FIELD_DATA_INVALID] = R.string.terminal_field_data_invalid
        terminalErrMap[FIELD_PARSING_ERROR] = R.string.terminal_field_parsing_error
        terminalErrMap[CARD_NOT_DETECTED] = R.string.terminal_card_not_detected
        terminalErrMap[CARD_ERROR] = R.string.terminal_card_error
        terminalErrMap[CARD_BLACKLISTED] = R.string.terminal_card_blacklisted
        terminalErrMap[CARD_BLOCKED] = R.string.terminal_card_blocked
        terminalErrMap[CARD_EXPIRED] = R.string.terminal_card_expired
        terminalErrMap[CARD_INVALID_ISSUER_ID] = R.string.terminal_card_invalid_issuer_id
        terminalErrMap[CARD_INVALID_PURSE_VALUE] = R.string.terminal_card_invalid_purse_value
        terminalErrMap[CARD_CREDIT_NOT_ALLOWED] = R.string.terminal_card_credit_not_allowed
        terminalErrMap[CARD_DEBIT_NOT_ALLOWED] = R.string.terminal_card_debit_not_allowed
        terminalErrMap[CARD_INSUFFICIENT_FUND] = R.string.terminal_card_insufficient_fund
        terminalErrMap[CARD_EXCEEDED_PURSE_VALUE] = R.string.terminal_card_exceeded_purse_value
        terminalErrMap[CARD_CREDIT_FAILED] = R.string.terminal_card_credit_failed
        terminalErrMap[CARD_CREDIT_UNCONFIRMED] = R.string.terminal_card_credit_unconfirmed
        terminalErrMap[CARD_DEBIT_FAILED] = R.string.terminal_card_debit_failed
        terminalErrMap[CARD_DEBIT_UNCONFIRMED] = R.string.terminal_card_debit_unconfirmed
        terminalErrMap[CARD_NOT_REMOVED] = R.string.terminal_card_not_removed
        terminalErrMap[COMMUNICATION_NO_RESPONSE] = R.string.terminal_communication_no_response
        terminalErrMap[COMMUNICATION_ERROR] = R.string.terminal_communication_error
        terminalErrMap[SOF_INVALID_CARD] = R.string.terminal_sof_invalid_card
        terminalErrMap[SOF_INCORRECT_PIN] = R.string.terminal_sof_incorrect_pin
        terminalErrMap[SOF_INSUFFICIENT_FUND] = R.string.terminal_sof_insufficient_fund
        terminalErrMap[SOF_CLOSED] = R.string.terminal_sof_closed
        terminalErrMap[SOF_BLOCKED] = R.string.terminal_sof_blocked
        terminalErrMap[SOF_REFER_TO_BANK] = R.string.terminal_sof_refer_to_bank
        terminalErrMap[SOF_CANCEL] = R.string.terminal_sof_cancel
        terminalErrMap[SOF_HOST_RESPONSE_DECLINE] = R.string.terminal_sof_host_response_decline
        terminalErrMap[SOF_LOGON_REQUIRED] = R.string.terminal_sof_logon_required
    }

    companion object {

        private var instance: TerminalError? = null
        fun getInstance(): TerminalError {
            if (instance == null) {
                instance = TerminalError()
            }
            return instance as TerminalError
        }


        const val SUCCESS                       = 0x00000000
        const val PENDING                       = 0x00000001
        const val TIMEOUT                       = 0x00000002
        const val INVALID_PARAMETER             = 0x00000003
        const val INCORRECT_FLOW                = 0x00000004
        const val MSG_INTEGRITY_FAILED          = 0x10000000
        const val MSG_TYPE_NOT_SUPPORTED        = 0x10000001
        const val MSG_CODE_NOT_SUPPORTED        = 0x10000002
        const val MSG_AUTHENTICATION_REQUIRED   = 0x10000003
        const val MSG_AUTHENTICATION_FAILED     = 0x10000004
        const val MSG_MAC_FAILED                = 0x10000005
        const val MSG_LENGTH_MISMATCH           = 0x10000006
        const val MSG_LENGTH_MINIMUM            = 0x10000007
        const val MSG_LENGTH_MAXIMUM            = 0x10000008
        const val MSG_VERSION_INVALID           = 0x10000009
        const val MSG_CLASS_INVALID             = 0x1000000A
        const val MSG_STATUS_INVALID            = 0x1000000B
        const val MSG_ALGORITHM_INVALID         = 0x1000000C
        const val MSG_ALGORITHM_IS_MANDATORY    = 0x1000000D
        const val MSG_KEY_INDEX_INVALID         = 0x1000000E
        const val MSG_NOTIFICATION_INVALID      = 0x1000000F
        const val MSG_COMPLETION_INVALID        = 0x10000010
        const val MSG_DATA_TRANSPARENCY_ERROR   = 0x10000011
        const val MSG_INCOMPLETE                = 0x10000012
        const val FIELD_MANDATORY_MISSING       = 0x11000001
        const val FIELD_LENGTH_MINIMUM          = 0x11000002
        const val FIELD_LENGTH_INVALID          = 0x11000003
        const val FIELD_TYPE_INVALID            = 0x11000004
        const val FIELD_ENCODING_INVALID        = 0x11000005
        const val FIELD_DATA_INVALID            = 0x11000006
        const val FIELD_PARSING_ERROR           = 0x11000007
        const val CARD_NOT_DETECTED             = 0x20000000
        const val CARD_ERROR                    = 0x20000001
        const val CARD_BLACKLISTED              = 0x20000002
        const val CARD_BLOCKED                  = 0x20000003
        const val CARD_EXPIRED                  = 0x20000004
        const val CARD_INVALID_ISSUER_ID        = 0x20000005
        const val CARD_INVALID_PURSE_VALUE      = 0x20000006
        const val CARD_CREDIT_NOT_ALLOWED       = 0x20000007
        const val CARD_DEBIT_NOT_ALLOWED        = 0x20000008
        const val CARD_INSUFFICIENT_FUND        = 0x20000009
        const val CARD_EXCEEDED_PURSE_VALUE     = 0x2000000A
        const val CARD_CREDIT_FAILED            = 0x2000000B
        const val CARD_CREDIT_UNCONFIRMED       = 0x2000000C
        const val CARD_DEBIT_FAILED             = 0x2000000D
        const val CARD_DEBIT_UNCONFIRMED        = 0x2000000E
        const val CARD_NOT_REMOVED              = 0x2000000F
        const val COMMUNICATION_NO_RESPONSE     = 0x30000000
        const val COMMUNICATION_ERROR           = 0x30000001
        const val SOF_INVALID_CARD              = 0x40000000
        const val SOF_INCORRECT_PIN             = 0x40000001
        const val SOF_INSUFFICIENT_FUND         = 0x40000002
        const val SOF_CLOSED                    = 0x40000003
        const val SOF_BLOCKED                   = 0x40000004
        const val SOF_REFER_TO_BANK             = 0x40000005
        const val SOF_CANCEL                    = 0x40000006
        const val SOF_HOST_RESPONSE_DECLINE     = 0x40000007
        const val SOF_LOGON_REQUIRED            = 0x40000008
    }

}