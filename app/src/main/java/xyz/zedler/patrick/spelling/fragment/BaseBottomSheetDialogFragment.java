package xyz.zedler.patrick.spelling.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class BaseBottomSheetDialogFragment extends BottomSheetDialogFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
            if(dialog == null) return;

            View sheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if(sheet == null) return;

            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity) requireContext()).getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(metrics);

            BottomSheetBehavior.from(sheet).setPeekHeight(metrics.heightPixels / 2);
        });
    }
}
