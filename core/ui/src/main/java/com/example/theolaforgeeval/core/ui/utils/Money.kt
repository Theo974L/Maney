package com.example.theolaforgeeval.core.ui.utils

import java.util.Locale

fun Double.formatEuro(): String = "%,.2f €".format(Locale.FRANCE, this)
