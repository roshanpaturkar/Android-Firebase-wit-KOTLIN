package ghrce.roshanpaturkar.com.ghrce.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import ghrce.roshanpaturkar.com.ghrce.fragments.MyPostsFragments
import ghrce.roshanpaturkar.com.ghrce.fragments.NewsFeedsFragment
import ghrce.roshanpaturkar.com.ghrce.fragments.VotingsFragments

class SectionPageAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return NewsFeedsFragment()
            1 -> return MyPostsFragments()
            2 -> return VotingsFragments()

        }
        return  null!!
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        when(position){
            0 -> return "Feeds"
            1 -> return "My Posts"
            2 -> return "Voting"
        }
        return null!!
        //return super.getPageTitle(position)
    }
}