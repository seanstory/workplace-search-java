package com.sstory.workplace.search.client.ssl

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

internal class NullHostnameVerifier : HostnameVerifier {
    override fun verify(hostname: String, session: SSLSession): Boolean {
        return true
    }
}
