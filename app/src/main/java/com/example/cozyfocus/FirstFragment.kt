package com.example.cozyfocus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class FirstFragment : Fragment() {
    private var startButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_first, container, false)

        startButton = view.findViewById(R.id.button)

        startButton?.setOnClickListener {
            (activity as MainActivity).stopMusic()
            val newStudyFragment = StudyFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.flFragment, newStudyFragment, "StudyFragment")
            transaction.addToBackStack(null)
            transaction.commit()
        }
        return view
    }
}
