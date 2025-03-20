import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.api.palette.presentation.work.WorkPosterFragment
import com.api.palette.presentation.work.WorkVideoFragment

class WorkPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> WorkPosterFragment()
            1 -> WorkVideoFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
