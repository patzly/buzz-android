package xyz.zedler.patrick.spelling.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Collections;

import xyz.zedler.patrick.spelling.Constants;
import xyz.zedler.patrick.spelling.R;
import xyz.zedler.patrick.spelling.databinding.FragmentBottomsheetFoundBinding;

public class FoundBottomSheetDialogFragment extends BaseBottomSheetDialogFragment {

	private final static String TAG = "FoundBottomSheet";
	private final static boolean DEBUG = false;

	private FragmentBottomsheetFoundBinding binding;

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
		binding = FragmentBottomsheetFoundBinding.inflate(
				inflater, container, false
		);

		Context context = getContext();
		Bundle bundle = getArguments();
		assert context != null && bundle != null;

		ArrayList<String> missed = bundle.getStringArrayList(Constants.BOTTOM_SHEET.FOUND_WORDS);
		if(missed != null) {
			Collections.sort(missed, String.CASE_INSENSITIVE_ORDER);
			binding.textFound.setText(TextUtils.join("\n", missed));
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
