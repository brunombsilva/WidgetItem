package net.brunombsilva.widgetitem.items;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.brunombsilva.widgetitem.Factory;
import net.brunombsilva.widgetitem.WidgetItem;


import android.content.Context;
import android.graphics.Bitmap;

public class ComicBookZip extends WidgetItem {

	public ComicBookZip(File file, Context context, Factory factory) {
		super(file, context, factory);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getMimeType() {
		return "application/x-cbz";
	}

	@Override
	public Bitmap generateThumbnail() throws Exception {
		File temporaryImage = getTemporaryImageFile();
		FileOutputStream output_file = new FileOutputStream(temporaryImage);
		BufferedOutputStream bos = new BufferedOutputStream(output_file);

		ZipFile zf = new ZipFile(getFile());
		Enumeration<? extends ZipEntry> en = zf.entries();
		if (!en.hasMoreElements()) {
			return getDefaultThumbnail();
		}
		ZipEntry entry = en.nextElement();
		String name = entry.getName();
		while (entry.isDirectory()
				&& !(name.endsWith(".jpg") || name.endsWith(".png"))) {
			if (!en.hasMoreElements()) {
				return getDefaultThumbnail();
			}
			entry = en.nextElement();
			name = entry.getName();
		}
		InputStream is = zf.getInputStream(entry);

		BufferedInputStream bis = new BufferedInputStream(is);

		byte[] buffer = new byte[2 * 1024];
		while (bis.read(buffer) != -1) {
			bos.write(buffer);
		}
		is.close();
		bos.flush();
		bos.close();

		Bitmap thumbnail = resizeImage(temporaryImage.getAbsolutePath(),
				getThumbnailMaxWidth(), getThumbnailMaxHeight());
		temporaryImage.delete();
		return thumbnail;
	}

}
