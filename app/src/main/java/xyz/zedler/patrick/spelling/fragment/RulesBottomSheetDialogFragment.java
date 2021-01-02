package xyz.zedler.patrick.spelling.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import xyz.zedler.patrick.spelling.R;
import xyz.zedler.patrick.spelling.databinding.FragmentBottomsheetRulesBinding;
import xyz.zedler.patrick.spelling.util.ResUtil;

public class RulesBottomSheetDialogFragment extends BaseBottomSheetDialogFragment {

	private final static String TAG = "RulesBottomSheet";
	private final static boolean DEBUG = false;

	private FragmentBottomsheetRulesBinding binding;

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
		binding = FragmentBottomsheetRulesBinding.inflate(
				inflater, container, false
		);

		Context context = getContext();
		assert context != null;

		binding.textRules.setText(ResUtil.readFromFile(context, "rules.txt"));

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
