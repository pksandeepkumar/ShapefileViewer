package com.texus.shapefileviewer.component;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.texus.shapefileviewer.MainActivity;
import com.texus.shapefileviewer.R;
import com.texus.shapefileviewer.datamodel.ShapeFieldData;
import com.texus.shapefileviewer.db.Databases;

import java.util.ArrayList;

public class ComponentInfo extends RelativeLayout {


    ImageButton imClose;
    LinearLayout llInfoHolder;
	RelativeLayout rlHolder = null;
	Context mContext;
    int height;

	public ComponentInfo(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        mContext = context;
		init(context);
	}

	public ComponentInfo(Context context, AttributeSet attrs) {
		super(context, attrs);
        mContext = context;
		init(context);
	}

	public ComponentInfo(Context context, int height) {
		super(context);
        this.height = height;
        mContext = context;
		init(context);
	}

	private void init(Context context) {

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View child = inflater.inflate(R.layout.element_info, this);
        imClose = (ImageButton) child.findViewById(R.id.imClose);
        llInfoHolder = (LinearLayout) child.findViewById(R.id.llInfoHolder);
        rlHolder = (RelativeLayout) child.findViewById(R.id.rlHolder);
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) rlHolder.getLayoutParams();
        rlp.height = height/2;
        rlHolder.setLayoutParams(rlp);
        imClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.removeAddInfoWindow();
            }
        });


	}

	public void setValues( int shapeID) {
        Databases db = new Databases(mContext);
        ArrayList<ShapeFieldData> fieldDatas = ShapeFieldData.getAllFieldDataForAShape(db, shapeID);
        for(ShapeFieldData shapeFieldData : fieldDatas) {
            ComponentFieldData componentFieldData = new ComponentFieldData(mContext,shapeFieldData);
            llInfoHolder.addView(componentFieldData);
        }

	}



}
