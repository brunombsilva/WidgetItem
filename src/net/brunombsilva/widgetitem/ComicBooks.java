package net.brunombsilva.widgetitem;

import java.util.HashMap;

import net.brunombsilva.widgetitem.items.ComicBookRar;
import net.brunombsilva.widgetitem.items.ComicBookZip;
import net.brunombsilva.widgetitem.items.Directory;

public class ComicBooks extends AbstractFactory {
	
	public HashMap<String,Class<? extends WidgetItem>> getSupported(){
		HashMap<String,Class<? extends WidgetItem>> supported = new HashMap<String,Class<? extends WidgetItem>>();
		supported.put("", Directory.class);
		supported.put(".cbr", ComicBookRar.class);
		supported.put(".cbz", ComicBookZip.class);
		return supported;
	}

	public ComicBooks(float dipWidth, float dipHeight) {
		super(dipWidth, dipHeight);
	}

}
