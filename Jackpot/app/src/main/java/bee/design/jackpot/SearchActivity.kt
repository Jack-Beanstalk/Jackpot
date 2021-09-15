package bee.design.jackpot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.GlobalScope
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import kotlinx.coroutines.*

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        plantSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                val plantUtil = plantApi()
                var data = ArrayList<PlantInfo>()
                CoroutineScope(Dispatchers.Main).launch {
                    // Show progress from UI thread

                    CoroutineScope(Dispatchers.Default).async {
                        // background thread
                        data = plantUtil.searchPlant(newText)
                    }.await()
                    // UI data update from UI thread
                    // Hide Progress from UI thread
                    val adapter = ListViewAdapter(data)
                    plantListView.adapter = adapter

                }


                return true
            }
        })

    }

}

/*

                runBlocking{
                    val plantList = scope.async{
                        plantUtil.searchPlant(newText)
                     }
                }
 */
