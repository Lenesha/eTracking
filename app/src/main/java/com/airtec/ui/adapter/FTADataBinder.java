package com.airtec.ui.adapter;

import android.view.View;


public abstract class FTADataBinder<T> {
	public abstract void bind(T model, View view, int groupPosition);

	@SuppressWarnings("hiding")
	public <T> T findViewToDatabind(View view, int id) {
		@SuppressWarnings("unchecked")
		T btn = (T) view.findViewById(id);
		return btn;
	}
	

}
