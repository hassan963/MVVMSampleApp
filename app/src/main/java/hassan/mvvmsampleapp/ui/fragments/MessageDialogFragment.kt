package hassan.mvvmsampleapp.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import dagger.hilt.android.AndroidEntryPoint
import hassan.mvvmsampleapp.R

@AndroidEntryPoint
class MessageDialogFragment : DialogFragment() {
    private var title: String = ""
    private var message: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments?.getString(KEY_DIALOG_TITLE, "") ?: ""
        message = arguments?.getString(KEY_DIALOG_MSG, "") ?: ""
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.done)) { _, _ -> }
            .create()

    companion object {
        const val TAG_NAME = "MessageDialogFragment"
        const val KEY_DIALOG_TITLE = "keyDialogTitle"
        const val KEY_DIALOG_MSG = "keyDialogMsg"

        fun newInstance(title: String, message: String): DialogFragment {
            val args = Bundle()
            args.putString(KEY_DIALOG_TITLE, title)
            args.putString(KEY_DIALOG_MSG, message)
            val fragment = MessageDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}