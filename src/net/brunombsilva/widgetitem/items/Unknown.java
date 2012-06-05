package net.brunombsilva.widgetitem.items;

import java.io.File;

import net.brunombsilva.widgetitem.AbstractFactory;
import net.brunombsilva.widgetitem.WidgetItem;


import android.content.Context;
import android.graphics.Bitmap;

public class Unknown extends WidgetItem {

	public Unknown(File file, Context context, AbstractFactory factory) {
		super(file, context, factory);
	}

	@Override
	public String getMimeType() {
		return "application/data";
	}

	@Override
	public Bitmap generateThumbnail() {
		return getDefaultThumbnail();
	}

}
