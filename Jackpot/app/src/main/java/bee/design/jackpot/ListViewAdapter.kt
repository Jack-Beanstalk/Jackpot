package bee.design.jackpot
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.custom_list_item.view.*

class ListViewAdapter(private val items:ArrayList<PlantInfo>): BaseAdapter() {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): PlantInfo = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        var convertView = view
        if (convertView == null) convertView = LayoutInflater.from(parent?.context).inflate(R.layout.custom_list_item, parent, false)
        val item: PlantInfo = items[position]
        var url = "http://www.nongsaro.go.kr/"+item.rtnFileCours+"/"+item.rtnStreFileNm
        convertView!!.text_title.text = item.plantName
        return convertView
    }


}