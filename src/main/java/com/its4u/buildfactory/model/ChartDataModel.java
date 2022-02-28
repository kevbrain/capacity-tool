package com.its4u.buildfactory.model;

import java.util.ArrayList;
import java.util.List;

public class ChartDataModel {

	private List<Number> values ;
	private List<String> labels ;
	private List<String> colors ;
	
	
	public ChartDataModel() {
		super();
		values = new ArrayList<Number>();
		labels = new ArrayList<String>();
		colors = new ArrayList<String>();
	}


	public ChartDataModel(List<Number> values, List<String> labels, List<String> colors) {
		super();
		this.values = values;
		this.labels = labels;
		this.colors = colors;
	}


	public List<Number> getValues() {
		return values;
	}


	public void setValues(List<Number> values) {
		this.values = values;
	}


	public List<String> getLabels() {
		return labels;
	}


	public void setLabels(List<String> labels) {
		this.labels = labels;
	}


	public List<String> getColors() {
		return colors;
	}


	public void setColors(List<String> colors) {
		this.colors = colors;
	}
	
	
}
