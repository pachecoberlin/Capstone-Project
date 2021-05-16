package de.pacheco.bakingapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import de.pacheco.bakingapp.R

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [StepListActivity].
 */
class StepDetailActivity : AppCompatActivity() {
    private var fragment: StepDetailFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step_detail)
        val toolbar = findViewById<Toolbar>(R.id.detail_toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        if (savedInstanceState == null) {
            startFragment()
        }
    }

    private fun startFragment() {
        val arguments = Bundle()
        val stepId = intent.getIntExtra(StepDetailFragment.STEPS_ID, -1)
        arguments.putInt(StepDetailFragment.STEPS_ID, stepId)
        fragment = StepDetailFragment()
        fragment!!.arguments = arguments
        supportFragmentManager.beginTransaction().add(R.id.item_detail_container,
                fragment!!).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            val upIntent = Intent(this, StepListActivity::class.java)
            if (fragment != null) {
                upIntent.putExtra(getString(R.string.recipe), fragment!!.recipe)
                fragment!!.step?.let { upIntent.putExtra(StepDetailFragment.STEPS_ID, it.id) }
            }
            NavUtils.navigateUpTo(this, upIntent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}