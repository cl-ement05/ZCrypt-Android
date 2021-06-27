package com.clement.zcrypt.core

class OperationStatus {
    companion object {
        const val SUCCESS = -1
        const val PENDING = 0
        const val BLANK_FIELDS = 1
        const val ZCRYPT_ALGO_ERROR = 2
        const val NO_FILE_SELECTED = 3
        const val IO_OR_FILE_ERROR = 4
        const val INVALID_FILE_FORMAT = 5
    }
}