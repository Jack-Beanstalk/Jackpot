package bee.design.jackpot.ui.plus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlusViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is plus Fragment"
    }
    val text: LiveData<String> = _text
}