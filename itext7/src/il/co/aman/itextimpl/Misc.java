package il.co.aman.itextimpl;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;

public class Misc {
	
	public static Color colorDecode(String code) {
		if (code == null) {
			return null;
		} else {
			try {
				String[] RGB = code.split(",");
				return new DeviceRgb(Integer.parseInt(RGB[0]),
						Integer.parseInt(RGB[1]), Integer.parseInt(RGB[2]));
			} catch (NumberFormatException e) {
				return null;
			}
		}
	}



}
