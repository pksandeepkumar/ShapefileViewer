package com.texus.shapefileviewer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.texus.shapefileviewer.R;
import com.texus.shapefileviewer.datamodel.ShapeFieldData;

import java.util.ArrayList;

public class FieldDataAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	Context context;
	ArrayList<ShapeFieldData> shapeFieldDatas;

	public FieldDataAdapter(Context context, ArrayList<ShapeFieldData> shapeFieldDatas) {
		this.context = context;
		this.shapeFieldDatas = shapeFieldDatas;
	}

	@Override
	public int getCount() {
        if(shapeFieldDatas == null) return 0;
		return shapeFieldDatas.size();
	}

	@Override
	public ShapeFieldData getItem(int position) {
		return shapeFieldDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.list_element,
						null);
			
			holder = new ViewHolder();
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tvText);
			holder.rlHolder  = (RelativeLayout) convertView.findViewById(R.id.rlHolder);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ShapeFieldData shapeFieldData = null;
		try {
			shapeFieldData = shapeFieldDatas.get(position);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (shapeFieldData != null) {
			holder.tvTitle.setText( shapeFieldData.fieldData );
		}
		return convertView;
	}
	
//	public void setClickable(RelativeLayout rlLayout) {
//		rlLayout.setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View arg0, MotionEvent arg1) {
//				switch (arg1.getAction()) {
//				case MotionEvent.ACTION_DOWN: {
////					setBgColor(true);
//					tvCatName.setBackgroundResource(R.drawable.round_bg_sub_cat_hover);
//					break;
//				}
//				case MotionEvent.ACTION_UP:
//				case MotionEvent.ACTION_CANCEL: {
//					tvCatName.setBackgroundResource(R.drawable.round_bg_sub_cat);
////					setBgColor(false);
//					break;
//				}
//				}
//				return false;
//			}
//
//		});
//	}

	public static class ViewHolder {
		public TextView tvTitle;
		public RelativeLayout rlHolder;

	}
	
}


