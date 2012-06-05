package net.brunombsilva.widgetitem;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import net.brunombsilva.widgetitem.items.ComicBookRar;
import net.brunombsilva.widgetitem.items.ComicBookZip;
import net.brunombsilva.widgetitem.items.Directory;
import net.brunombsilva.widgetitem.items.Unknown;
import android.content.Context;
import android.util.Log;

public class Factory{
	
	private static HashMap<String, Class<?>> items = new HashMap<String, Class<?>>();
	static {
		items.put("", Directory.class);
		//items.put("??", Unknown.class);
		items.put(".cbr", ComicBookRar.class);
		items.put(".cbz", ComicBookZip.class);
	}
	
	private float dipWidth;
	private float dipHeight;
	
	public float getDipWidth(){
		return dipWidth;
	}
	
	public float getDipHeight(){
		return dipHeight;
	}
	
	public Factory(float dipWidth, float dipHeight){
		this.dipWidth = dipWidth;
		this.dipHeight = dipHeight;
	}
	public boolean isSupported(File file){
		if(file.isDirectory()){
			return true;
		}
		String fileName = file.getName().toLowerCase();
		Log.w("ComicsWidget", "WidgetItemFactory: items - "+items.size());
		for (String s : items.keySet()) {
			Log.w("ComicsWidget", "WidgetItemFactory: Testing "+ fileName + " for type " + s);
			if (fileName.endsWith(s)) {
				return true;
			}
		}
		return false;
	}
	
	public WidgetItem getWidgetItem(File file,Context context){
		if(file.isDirectory()){
			Log.w(context.getString(R.string.app_name), "WidgetItemFactory: returned Directory");
			return new Directory(file,context, this);
		}
		if(file.getName().lastIndexOf('.')==-1){
			Log.w(context.getString(R.string.app_name), "WidgetItemFactory: returned Unknown");
			return new Unknown(file,context, this);
		}
		String extension = file.getName().substring(file.getName().lastIndexOf('.'));	
		
		Class<?> wItem = items.get(extension);
		if(wItem == null){
			Log.w(context.getString(R.string.app_name), "WidgetItemFactory: returned Unknown");
			return new Unknown(file,context, this);
		}
		
		try{
			Constructor<?> cons = wItem.getConstructor(new Class[]{File.class, Context.class, Factory.class});
			Log.w(context.getString(R.string.app_name), "WidgetItemFactory: returned "+wItem.getName());
			return (WidgetItem)cons.newInstance(file,context, this);
		}catch(Exception ex){
			Log.w(context.getString(R.string.app_name), "WidgetItemFactory: returned Unknown");
			return new Unknown(file,context, this);
		}
		 
	}
}