package com.mgacreative.tso_egitim

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform