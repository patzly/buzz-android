package xyz.zedler.patrick.spelling.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Collections;

import xyz.zedler.patrick.spelling.Constants;
import xyz.zedler.patrick.spelling.MainActivity;
import xyz.zedler.patrick.spelling.R;
import xyz.zedler.patrick.spelling.databinding.FragmentBottomsheetNewGameBinding;
import xyz.zedler.patrick.spelling.task.NewGameTask;

public class NewGameBottomSheetDialogFragment extends BaseBottomSheetDialogFragment {

	private final static String TAG = "NewGameBottomSheet";
	private final static boolean DEBUG = false;

	private FragmentBottomsheetNewGameBinding binding;

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new BottomSheetDialog(requireContext(), R.style.Theme_Spelling_BottomSheetDialog);
	}

	@Override
	public View onCreateView(
			@NonNull LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState
	) {
		binding = FragmentBottomsheetNewGameBinding.inflate(
				inflater, container, false
		);

		setCancelable(false);

		Bundle bundle = getArguments();
		assert bundle != null;

		binding.textNewGameHints.setText(
				getString(
						R.string.msg_hints_used,
						bundle.getInt(Constants.BOTTOM_SHEET.HINTS_USED, 0)
				)
		);

		String letters = bundle.getString(
				Constants.BOTTOM_SHEET.LETTERS, Constants.DEFAULT.LETTERS
		);
		String center = bundle.getString(Constants.BOTTOM_SHEET.CENTER, Constants.DEFAULT.CENTER);
		String all = "<font color='#deb853'>" + center + "</font>" + letters;
		binding.textNewGameLetters.setText(Html.fromHtml(all), TextView.BufferType.SPANNABLE);

		binding.buttonNewGame.setOnClickListener(v -> {
			new NewGameTask((MainActivity) getActivity()).execute();
			dismiss();
		});

		ArrayList<String> missed = bundle.getStringArrayList(Constants.BOTTOM_SHEET.MISSED_WORDS);
		if(missed != null) {
			Collections.sort(missed, String.CASE_INSENSITIVE_ORDER);
			binding.textNewGameMissed.setText(TextUtils.join("\n", missed));
		}

		return binding.getRoot();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}

	@NonNull
	@Override
	public String toString() {
		return TAG;
	}
}
