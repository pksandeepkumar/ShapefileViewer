package com.texus.shapefileviewer.component;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.texus.shapefileviewer.R;
import com.texus.shapefileviewer.datamodel.ShapeFieldData;

public class ComponentFieldData extends RelativeLayout {


    TextView tvFieldName;
    TextView tvFieldData;
    LinearLayout llInfoHolder;
    ShapeFieldData shapeFieldData;
	Context mContext;

	public ComponentFieldData(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        mContext = context;
		init(context);
	}

	public ComponentFieldData(Context context, AttributeSet attrs) {
		super(context, attrs);
        mContext = context;
		init(context);
	}

	public ComponentFieldData(Context context, ShapeFieldData shapeFieldData) {
		super(context);
        this.shapeFieldData = shapeFieldData;
        mContext = context;
		init(context);
	}

	private void init(Context context) {

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View child = inflater.inflate(R.layout.element_field_data, this);
        tvFieldName = (TextView) child.findViewById(R.id.tvFieldName);
        tvFieldData = (TextView) child.findViewById(R.id.tvFieldData);
        if(shapeFieldData != null) {
            tvFieldName.setText(shapeFieldData.fieldName);
            tvFieldData.setText(shapeFieldData.fieldData);
        }
	}

}
