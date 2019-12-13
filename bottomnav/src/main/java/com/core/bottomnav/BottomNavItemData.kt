package com.core.bottomnav

import java.lang.reflect.Type

/**
 *  @param menuItemId = R.id.mainFragment
 *  @param menuActionId = R.id.to_fragmentNavTab1
 *  @param noInternetAvailable = true if available
 *  @param noAuthAvailable = true if available
 */

data class BottomNavItemData(
    val menuItemId: Int,
    val menuActionId: Int,
    val fragmentType: Type,
    val noInternetAvailable: Boolean = false,
    val noAuthAvailable: Boolean = false,
    val showNavBoolean: Boolean = true
)

// Map<Nav Menu item id, Nav Menu Navigation action id>