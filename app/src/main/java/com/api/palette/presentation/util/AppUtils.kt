package com.api.palette.presentation.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.api.palette.R

/**
 * Fragment를 mainContent 영역에 교체합니다.
 *
 * @param fragment 전환할 Fragment
 * @param manager FragmentManager 인스턴스
 * @param addToBackStack 뒤로가기 스택에 추가 여부
 */
fun changeFragment(
    fragment: Fragment,
    manager: FragmentManager,
    addToBackStack: Boolean = false
) {
    manager.beginTransaction().apply {
        replace(R.id.mainContent, fragment)
        if (addToBackStack) addToBackStack(null)
        commit()
    }
}

/**
 * 현재 Fragment가 루트 Fragment인지 여부를 확인합니다.
 */
fun isRootFragment(manager: FragmentManager): Boolean = manager.backStackEntryCount == 0
