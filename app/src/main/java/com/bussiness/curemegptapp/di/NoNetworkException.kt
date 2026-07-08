package com.bussiness.curemegptapp.di

import java.io.IOException

class NoNetworkException(message: String = "No internet connection") : IOException(message)