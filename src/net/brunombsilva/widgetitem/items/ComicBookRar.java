package net.brunombsilva.widgetitem.items;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.brunombsilva.widgetitem.Factory;
import net.brunombsilva.widgetitem.WidgetItem;


import de.innosystec.unrar.Archive;
import de.innosystec.unrar.rarfile.FileHeader;

import android.content.Context;
import android.graphics.Bitmap;

public class ComicBookRar extends WidgetItem {

	public ComicBookRar(File file, Context context, Factory factory) {
		super(file, context, factory);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getMimeType() {
		return "application/x-cbr";
	}

	@Override
	public Bitmap generateThumbnail() throws Exception {
		File temporaryImage = getTemporaryImageFile();
		FileOutputStream output_file = new FileOutputStream(temporaryImage);
		BufferedOutputStream bos = new BufferedOutputStream(output_file);
		Archive rar = new Archive(getFile());
		List<FileHeader> files = rar.getFileHeaders();
		
		if(files.size()==0){
			return getDefaultThumbnail();
		}
		
		Collections.sort(files, new Comparator<FileHeader>() {
            public int compare(FileHeader e1, FileHeader e2) {
                return e1.getFileNameString().compareTo(e2.getFileNameString());
            }});
		FileHeader fh = null;
		for(FileHeader e : files){
			String name = e.getFileNameString().toLowerCase();
			if(!e.isDirectory() && (name.endsWith(".jpg")||name.endsWith(".png"))){
				fh = e;
				break;
			}
		}
		if(fh == null || !fh.isFileHeader()){
			return getDefaultThumbnail();
		}
		rar.extractFile(fh, bos);
		rar.close();
		bos.close();
		Bitmap thumbnail = resizeImage(temporaryImage.getAbsolutePath(),
				getThumbnailMaxWidth(), getThumbnailMaxHeight());
		temporaryImage.delete();
		return thumbnail;
	}

}
