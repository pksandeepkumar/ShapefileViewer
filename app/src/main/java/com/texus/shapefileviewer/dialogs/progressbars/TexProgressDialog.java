package com.texus.shapefileviewer.dialogs.progressbars;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.texus.shapefileviewer.R;


public class TexProgressDialog extends Dialog {

	public Context context;

	public TexProgressDialog(Context context) {
		super(context);
		this.context = context;
        Initialize();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	public void closeThisDialog() {
		dismiss();
	}

	public void Initialize() {
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		this.setContentView(R.layout.dialog_progress);
		setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

}