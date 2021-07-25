package bee.design.jackpot.ui.plus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import bee.design.jackpot.databinding.FragmentPlusBinding

class plusFragment : Fragment() {

    private lateinit var plusViewModel: PlusViewModel
    private var _binding: FragmentPlusBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        plusViewModel =
            ViewModelProvider(this).get(PlusViewModel::class.java)

        _binding = FragmentPlusBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        plusViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}