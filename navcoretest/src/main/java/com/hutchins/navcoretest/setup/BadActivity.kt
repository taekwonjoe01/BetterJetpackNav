package com.hutchins.navcoretest.setup

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.hutchins.navcore.NavigationActivity
import com.hutchins.navcoretest.R

/**
 * This activity is intended to be used with BadActivityTests to show what could occur if the [navigationHostResourceId]
 * is improperly instantiated.
 */
class BadActivity : NavigationActivity()
