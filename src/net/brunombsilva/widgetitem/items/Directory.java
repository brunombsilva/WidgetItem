package net.brunombsilva.widgetitem.items;

import java.io.File;

import net.brunombsilva.widgetitem.AbstractFactory;
import net.brunombsilva.widgetitem.WidgetItem;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;

public class Directory extends WidgetItem {

	public Directory(File file, Context context, AbstractFactory factory) {
		super(file, context, factory);
	}

	@Override
	public String getMimeType() {
		return "";
	}

	@Override
	public Bitmap generateThumbnail() {
		File[] files = getFile().listFiles();		
		int end = Math.min(files.length - 1, 5);
		int width = getThumbnailMaxWidth() + end * 5;
		int height = getThumbnailMaxHeight() + end * 5;
		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.TRANSPARENT);

		Paint paint = new Paint();
		LightingColorFilter filter = new LightingColorFilter(Color.rgb(150,
				150, 150), 1);
		filter = null;

		boolean generated = false;
		for (int i = end; i >= 0; i--) {
			File f = files[i]; 
			if (f.isFile() && getFactory().isSupported(f)) {
				generated = true;				
				Bitmap b = getFactory().getWidgetItem(f, getContext()).getThumbnail();
				if (i > 0) {
					paint.setColorFilter(filter);
				} else {
					paint.setColorFilter(null);
				}
				canvas.drawBitmap(b, i * 5, i * 5, paint);
			}
		}
		if(!generated){
			return getDefaultThumbnail();
		}
		return Bitmap.createScaledBitmap(bitmap, getThumbnailMaxWidth(),
				getThumbnailMaxHeight(), true);
	}

}
