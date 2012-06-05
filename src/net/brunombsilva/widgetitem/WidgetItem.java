package net.brunombsilva.widgetitem;

import java.io.File;
import java.io.FileOutputStream;

import net.brunombsilva.widgetitem.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View.MeasureSpec;
import android.widget.RelativeLayout;
import android.widget.TextView;


public abstract class WidgetItem {
	private File file;
	private Context context;
	private AbstractFactory factory;
	private int thumbnailMaxWidth;
	private int thumbnailMaxHeight;
	
	public AbstractFactory getFactory(){
		return this.factory;
	}

	public File getFile() {
		return this.file;
	}
	
	public Context getContext() {
		return this.context;
	}

	
	protected int getThumbnailMaxWidth() {
		return thumbnailMaxWidth;		
	}

	protected int getThumbnailMaxHeight() {
		return thumbnailMaxHeight;		
	}

	public Bitmap getDefaultThumbnail() {
		String title = file.getName().replace('_', ' ')
				.replaceAll("\\(.*?\\)", "");
		if (title.lastIndexOf('.') > -1) {
			title = title.substring(0, title.lastIndexOf('.') - 1);
		}

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout overallRelativeLayout = (RelativeLayout) inflater
				.inflate(R.layout.empty_widget_item, null);

		TextView titleTextView = (TextView) overallRelativeLayout
				.findViewById(R.id.title);
		titleTextView.setText(title);

		overallRelativeLayout.measure(MeasureSpec.makeMeasureSpec(
				getThumbnailMaxWidth(), MeasureSpec.EXACTLY), MeasureSpec
				.makeMeasureSpec(getThumbnailMaxHeight(), MeasureSpec.EXACTLY));
		overallRelativeLayout.layout(0, 0,
				overallRelativeLayout.getMeasuredWidth(),
				overallRelativeLayout.getMeasuredHeight());

		Bitmap bitmap = Bitmap.createBitmap(getThumbnailMaxWidth(),
				getThumbnailMaxHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.BLACK);
		overallRelativeLayout.draw(canvas);
		return bitmap;

	}


	public abstract String getMimeType();
	protected abstract Bitmap generateThumbnail() throws Exception;
	
	protected File getThumbnailFile(){
		return new File(this.context.getCacheDir()
				.getAbsolutePath() + File.separator +file.getAbsolutePath().replace(
				File.separator, "-")
				+ ".thumb"+getThumbnailMaxWidth()+"x"+getThumbnailMaxHeight()+".jpg");
	}
	
	protected File getTemporaryImageFile(){
		return new File(this.context.getCacheDir()
				.getAbsolutePath() + File.separator +file.getAbsolutePath().replace(
				File.separator, "-")
				+ ".orig.jpg");
	}
	
	public Bitmap getThumbnail(){
		File thumbnail_file = getThumbnailFile();

		Bitmap thumbnail = null;
		// if(thumbnail_file.exists()){
		// thumbnail_file.delete();
	//	 }
		try {
			if (!thumbnail_file.exists()) {
				try{
					thumbnail = generateThumbnail();
					Bitmap bitmap = Bitmap.createBitmap(Math.max(thumbnail.getWidth(),getThumbnailMaxWidth()), Math.max(thumbnail.getHeight(),getThumbnailMaxHeight()), Config.ARGB_8888);
					Canvas canvas = new Canvas(bitmap);
					canvas.drawColor(Color.TRANSPARENT);
					canvas.drawBitmap(thumbnail, 0, 0, null);
					thumbnail = bitmap;
					
				}catch(Exception ex){
					Log.w(context.getString(R.string.app_name), ex);
					thumbnail = getDefaultThumbnail();
				}
				thumbnail.compress(CompressFormat.PNG, 70,
						new FileOutputStream(thumbnail_file));
				
			} else {
				thumbnail = BitmapFactory.decodeFile(thumbnail_file
						.getAbsolutePath());
			}
		} catch (Exception e) {
			Log.w(context.getString(R.string.app_name),
					"WidgetItem Exception: " + e.getMessage() + "\n"
							+ Log.getStackTraceString(e));

			thumbnail = getDefaultThumbnail();
		}
		return thumbnail;
	}

	// http://stackoverflow.com/questions/4231817/quality-problems-when-resizing-an-image-at-runtime
	protected Bitmap resizeImage(String originalPath, int desiredWidth,
			int desiredHeight) throws Exception {
		// Get the source image's dimensions
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(originalPath, options);

		int srcWidth = options.outWidth;
		// int srcHeight = options.outHeight;

		// Only scale if the source is big enough. This code is just trying to
		// fit a image into a certain width.
		if (desiredWidth > srcWidth)
			desiredWidth = srcWidth;

		// Calculate the correct inSampleSize/scale value. This helps reduce
		// memory use. It should be a power of 2
		// from:
		// http://stackoverflow.com/questions/477572/android-strange-out-of-memory-issue/823966#823966
		int inSampleSize = 1;
		while (srcWidth / 2 > desiredWidth) {
			srcWidth /= 2;
			// srcHeight /= 2;
			inSampleSize *= 2;
		}

		float desiredScale = (float) desiredWidth / srcWidth;

		// Decode with inSampleSize
		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inSampleSize = inSampleSize;
		options.inScaled = false;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap sampledSrcBitmap = BitmapFactory.decodeFile(originalPath,
				options);
		if (sampledSrcBitmap == null) {
			throw new Exception("Could not decode " + originalPath);
		}
		// Resize
		Matrix matrix = new Matrix();
		matrix.postScale(desiredScale, desiredScale);
		Bitmap scaledBitmap = Bitmap.createBitmap(sampledSrcBitmap, 0, 0,
				sampledSrcBitmap.getWidth(), sampledSrcBitmap.getHeight(),
				matrix, true);
		sampledSrcBitmap = null;

		return scaledBitmap;
	}
	public WidgetItem(File file, Context context, AbstractFactory factory) {
		this.context = context;
		this.file = file;
		this.factory = factory;
		
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.getFactory().getDipWidth(),//162
				this.context.getResources().getDisplayMetrics());
		this.thumbnailMaxWidth =  (int) px;
		px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.getFactory().getDipHeight(),//245
				this.context.getResources().getDisplayMetrics());
		this.thumbnailMaxHeight = (int) px;
	}
}