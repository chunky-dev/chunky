package se.llbit.chunky.launcher;

import java.awt.Toolkit;
import java.net.URL;

import javax.swing.ImageIcon;

public class Icons {

	public static ImageIcon expandIcon, collapseIcon, expandHoverIcon, collapseHoverIcon;
	static {
		URL url = Icons.class.getResource("/expand.png");
		if (url != null) {
			expandIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
		}
		url = Icons.class.getResource("/collapse.png");
		if (url != null) {
			collapseIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
		}
		url = Icons.class.getResource("/expand-hover.png");
		if (url != null) {
			expandHoverIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
		}
		url = Icons.class.getResource("/collapse-hover.png");
		if (url != null) {
			collapseHoverIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(url));
		}
	}

}
