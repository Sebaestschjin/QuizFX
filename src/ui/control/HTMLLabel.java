package ui.control;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Pair;
import main.Paths;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sebastian Stern
 */
public class HTMLLabel extends HBox {

	private String curContent;

	private ImageView imageView = new ImageView();

	private TextFlow textFlow = new TextFlow();

	private DoubleProperty maxHeight;

	private enum Style {
		REGULAR(""),
		BOLD("b"),
		ITALIC("i"),
		STRIKE("s"),
		UNDERLINE("u"),
		;

		String tag;

		Style(String tag) {
			this.tag = tag;
		}
	}

	private List<Pair<String, List<Style>>> splitText = new ArrayList<>();

	private List<Text> texts = new ArrayList<>();

	private Text properties = new Text();

	public HTMLLabel(String content) {
		setPadding(new Insets(10));
		setSpacing(10);
		setAlignment(Pos.CENTER);
		setText(content);

		getChildren().addAll(imageView, textFlow);
	}

	public void setText(String content) {
		curContent = content;
		textFlow.getChildren().clear();
		texts.clear();
		splitText.clear();

		// replace HTML and br tags
		content = content.replaceAll("</?[Hh][Tt][Mm][Ll]>", "");
		content = content.replaceAll("</?[bB][rR]>", "\n");

		// replace style tags
		splitText(content);
		addTexts();
	}

	private void splitText(String content) {
		splitText.add(new Pair<>(content, Collections.singletonList(Style.REGULAR)));
		for (Style s : Style.values()) {
			if (s == Style.REGULAR) {
				continue;
			}
			doSplit(s);
		}
	}

	private void doSplit(Style style) {
		List<Pair<String, List<Style>>> newSplits = new ArrayList<>();

		for (Pair<String, List<Style>> split : splitText) {
			String tag = style.tag + style.tag.toUpperCase();
			String text = split.getKey();
			List<Style> curStyles = split.getValue();
			List<Style> newStyles = new ArrayList<>(curStyles);
			newStyles.add(style);

			Pattern p = Pattern.compile("<[" + tag + "]\\b[^>]*>(.*?)</[" + tag + "]>");
			Matcher m = p.matcher(text);

			int last = 0;
			while (m.find()) {
				newSplits.add(new Pair<>(text.substring(last, m.start()), curStyles));
				newSplits.add(new Pair<>(m.group(1), newStyles));
				last = m.end();
			}

			newSplits.add(new Pair<>(text.substring(last), curStyles));
		}

		splitText = newSplits;
	}

	private void addTexts() {
		for (Pair<String, List<Style>> text : splitText) {
			Text t = new Text(text.getKey());
			addText(t);

			if (text.getValue().contains(Style.BOLD)) {
				t.setStyle("-fx-font-weight: bold;");
			}
			if (text.getValue().contains(Style.ITALIC)) {
				t.setStyle("-fx-font-style: italic;");
			}
			if (text.getValue().contains(Style.UNDERLINE)) {
				t.setUnderline(true);
			}
			if (text.getValue().contains(Style.STRIKE)) {
				t.setStrikethrough(true);
			}
		}
	}

	private void addText(Text t) {
		t.setFill(properties.getFill());
		textFlow.getChildren().add(t);
		texts.add(t);
	}

	public void setTextAlignment(TextAlignment alignment) {
		textFlow.setTextAlignment(alignment);
	}

	public void setTextFill(Color color) {
		properties.setFill(color);
		for (Text text : texts) {
			text.setFill(color);
		}
	}

	public void setImage(File imageFile) {
		imageView.setImage(null);
		imageView.fitHeightProperty().unbind();

		if (imageFile != null) {
			try {
				File relFile = Paths.relative(Paths.resourcesDir, imageFile);
				Image image = new Image(new BufferedInputStream(new FileInputStream(relFile)));
				imageView.setImage(image);
				imageView.setPreserveRatio(true);
				imageView.fitHeightProperty().bind(maxHeight.subtract(maxHeight.divide(5)));
			} catch (Throwable e) {
				System.err.println("Couldn't load image " + imageFile.getName() + ": " + e.getMessage());
			}
		}
	}


	public void setMaxHeight(DoubleProperty maxHeight) {
		this.maxHeight = maxHeight;
		setText(curContent);
	}

	public String getContent() {
		return curContent;
	}

	public double getImageWidth() {
		if (imageView.getImage() != null && !imageView.getImage().isError()) {
			return imageView.getImage().getWidth();
		}

		return 0.0;
	}


}
