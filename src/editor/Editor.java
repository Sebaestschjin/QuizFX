package editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import json.DataFormatException;
import json.JSOCodec;
import json.JSONRenderer;
import json.JSOWithPosition;
import json.UnexpectedDataTypeException;
import json.parser.JSONParser;
import json.parser.ParserException;
import json.parser.PosBuffer;
import main.Paths;
import model.Answer;
import model.Category;
import model.Question;
import util.Colors;
import util.SpringUtilities;

public class Editor extends JFrame{
	private static final long serialVersionUID = -6193549599266486312L;
	protected static final Color TEXTFIELD_ERROR_BACKGROUND = new Color(0xFFDDDD);
	protected static final Color TEXTFIELD_OK_BACKGROUND = new Color(0xFFFFFF);
	JFileChooser jsonFC=new JFileChooser(Paths.resourcesDir);
	JFileChooser imgFC=new JFileChooser(Paths.resourcesDir);
	JColorChooser jcc=new JColorChooser();
	File opened=null;
	JList<Category> loadedCategories=new JList<>(new DefaultListModel<>());
	JList<Question> questionList=new JList<>(new DefaultListModel<>());
	Category currentCategory=null;
	Question currentQuestion=null;
	boolean changed=false;
	JTabbedPane tabs=new JTabbedPane();
	List<Question> questionClipboard=Collections.emptyList();
	public static void main(String[] args) {
		Editor ed=new Editor();
		if(args.length>0){
			ed.addData(new File(args[0]));
			ed.changed=false;
		}
		ed.pack();
		ed.setVisible(true);
	}
	public Editor(){
		super("Quizdaten-Editor");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(dataLossWarning()) return; else dispose();
			}
		});
		setLayout(new BorderLayout());
		add(tabs);
		makeFileTab();
		makeCategoriesTab();
		makeQuestionsTab();
		tabs.setEnabledAt(2, false);

	}
	{
		jsonFC.addChoosableFileFilter(new FileFilter() {
			@Override public String getDescription() {return "JSON-Dateien";}
			@Override public boolean accept(File f) {return f.getName().toLowerCase().endsWith(".json");}
		});		
		imgFC.addChoosableFileFilter(new FileFilter() {
			@Override public String getDescription() {return "Bilddatein-Dateien (JPEG, PNG, GIF, BMP)";}
			@Override public boolean accept(File f) {
				String name = f.getName().toLowerCase();
				return name.endsWith(".jpg")||name.endsWith(".jpeg")||name.endsWith(".png")||
						name.endsWith(".gif")||name.endsWith(".bmp");
			}
		});
	}
	void makeQuestionsTab(){
		JSplitPane qTab=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		tabs.addTab("Fragen", qTab);
		JPanel lp=new JPanel(new BorderLayout());
		lp.add(new JScrollPane(questionList), BorderLayout.CENTER);
		Box lb=Box.createVerticalBox();
		lp.add(lb, BorderLayout.SOUTH);
		JButton addQ=new JButton("Neue Frage");
		JButton delQ=new JButton("Frage löschen");
		JButton copyQ=new JButton("Frage kopieren");
		JButton pasteQ=new JButton("Frage einfügen");
		pasteQ.setEnabled(false);
		JPanel edButtons=new JPanel(new GridLayout(2,2));
		edButtons.add(addQ); edButtons.add(copyQ);
		edButtons.add(delQ); edButtons.add(pasteQ);
		lb.add(edButtons);
		qTab.setLeftComponent(lp);

		Box eb=Box.createVerticalBox();
		JPanel ep=new JPanel(new SpringLayout());
		eb.add(Box.createGlue());
		eb.add(ep);
		eb.add(Box.createGlue());
		qTab.setRightComponent(eb);

		ep.add(new JLabel("Fragetext"));
		JTextArea qText=new JTextArea(3, 30);
		ep.add(new JScrollPane(qText));

		JButton qImgB=new JButton("Fragebild...");
		ep.add(qImgB);
		JTextField qImgT=new JTextField(30);
		ep.add(qImgT);

		ep.add(new JLabel("Antwort-Erklärungstext"));
		JTextArea aText=new JTextArea(3, 30);
		ep.add(new JScrollPane(aText));

		ep.add(new JLabel("Antwort-Quellenangabe"));
		JTextArea sText=new JTextArea(3, 30);
		ep.add(new JScrollPane(sText));

		JButton aImgB=new JButton("Antwort-Erklärungsbild...");
		ep.add(aImgB);
		JTextField aImgT=new JTextField(30);
		ep.add(aImgT);

		JTextField[] answers=new JTextField[4];
		for(int i=0; i<answers.length; ++i){
			ep.add(new JLabel(i==0?"Richtige Antwort":"Falsche Antwort"));
			answers[i]=new JTextField(50);
			ep.add(answers[i]);
		}

		ep.add(new JLabel("Gewicht"));
		SpinnerNumberModel weight=new SpinnerNumberModel(1, 0, 10, 1);
		JSpinner weightSpinner = new JSpinner(weight);
		ep.add(weightSpinner);

		SpringUtilities.makeCompactGrid(ep, 6+answers.length, 2, 0, 0, 10, 10);

		delQ.setEnabled(false);
		copyQ.setEnabled(false);
		qText.setEnabled(false);
		qImgB.setEnabled(false);
		qImgT.setEnabled(false);
		aText.setEnabled(false);
		sText.setEnabled(false);
		aImgB.setEnabled(false);
		aImgT.setEnabled(false);
		weightSpinner.setEnabled(false);
		for(int i=0; i<answers.length; ++i)
			answers[i].setEnabled(false);
		questionList.addListSelectionListener(lse -> {
			boolean sel=questionList.getSelectedIndex()!=-1;
			delQ.setEnabled(sel);
			copyQ.setEnabled(sel);
			qText.setEnabled(sel);
			qImgB.setEnabled(sel);
			qImgT.setEnabled(sel);
			aText.setEnabled(sel);
			sText.setEnabled(sel);
			aImgB.setEnabled(sel);
			aImgT.setEnabled(sel);
			weightSpinner.setEnabled(sel);
			for(int i=0; i<answers.length; ++i)
				answers[i].setEnabled(sel);
			if(sel){
				currentQuestion=questionList.getSelectedValue();
				qText.setText(currentQuestion.getQuestionText());
				aText.setText(currentQuestion.getAnswerText());
				sText.setText(currentQuestion.getAnswerSource());
				weight.setValue(currentQuestion.getWeight());
				File qFile = currentQuestion.getQuestionImageFile();
				qImgT.setText(qFile==null?"":qFile.getPath());
				File aFile = currentQuestion.getAnswerImageFile();
				aImgT.setText(aFile==null?"":aFile.getPath());
				List<Answer> aList = currentQuestion.getAnswers();
				for(int i=0; i<answers.length; ++i)
					answers[i].setText(aList.get(i).getText());	
			}else{
				currentQuestion=null;
				qText.setText("");
				aText.setText("");
				sText.setText("");
				weight.setValue(1);
				qImgT.setText("");
				aImgT.setText("");
				for(int i=0; i<answers.length; ++i)
					answers[i].setText("");	
			}
		});
		addQ.addActionListener(ae -> {
			Answer[] as=new Answer[answers.length];
			for(int i=0; i<as.length; ++i)
				as[i]=new Answer("", i);
			Question nq=new Question("?", null, "", null, "", 1, as);
			appendQuestion(nq);
		});
		delQ.addActionListener(ae->{
			int i=questionList.getSelectedIndex();
			DefaultListModel<Question> m = questionListModel();
			m.remove(i);
			ArrayList<Question> qs=new ArrayList<>(currentCategory.getQuestions());
			qs.remove(i);
			updateCurrentCategegory(currentCategory.withQuestions(qs));
			if(m.size()>0)
				if(m.size()>i)
					questionList.setSelectedIndex(i);
				else
					questionList.setSelectedIndex(m.size()-1);
		});
		copyQ.addActionListener(ae->{
			pasteQ.setEnabled(true);
			List<Question> qs = questionList.getSelectedValuesList();
			copyToClipboard(qs);
		});
		pasteQ.addActionListener(ae-> {
			List<Question> qs=getClipboardContent();
			for(Question q: qs)
				appendQuestion(q);
		});
		weight.addChangeListener(ce -> {
			if(currentQuestion==null) return;
			updateCurrentQuestion(currentQuestion.withWeight(weight.getNumber().doubleValue()));
		});
		qText.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				if(currentQuestion!=null)
					updateCurrentQuestion(currentQuestion.withQuestionText(qText.getText()));
			}
		});
		aText.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				if(currentQuestion!=null)
					updateCurrentQuestion(currentQuestion.withAnswerText(aText.getText()));
			}
		});
		sText.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				if(currentQuestion!=null)
					updateCurrentQuestion(currentQuestion.withAnswerSource(sText.getText()));
			}
		});

		qImgB.addActionListener(ae -> {
			if(currentQuestion==null) return;
			int opt=imgFC.showOpenDialog(qImgB);
			if(opt!=JFileChooser.APPROVE_OPTION) return;
			File f = imgFC.getSelectedFile();
			f=Paths.asRelativeTo(Paths.resourcesDir, f);
			qImgT.setText(f.getPath());
		});
		qImgT.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				if(currentQuestion==null) return;
				String txt=qImgT.getText();
				File file;
				if(txt.length()==0)
					file=null;
				else
					file=new File(txt);
				Question q=currentQuestion.withQuestionImageFile(file);
				try{
					q.getQuestionImage();
					qImgT.setBackground(TEXTFIELD_OK_BACKGROUND);
				}catch(Exception x){
					qImgT.setBackground(TEXTFIELD_ERROR_BACKGROUND);
				}
				updateCurrentQuestion(q);
			}
		});
		aImgB.addActionListener(ae -> {
			if(currentQuestion==null) return;
			int opt=imgFC.showOpenDialog(aImgB);
			if(opt!=JFileChooser.APPROVE_OPTION) return;
			File f = imgFC.getSelectedFile();
			f=Paths.asRelativeTo(Paths.resourcesDir, f);
			aImgT.setText(f.getPath());
		});
		aImgT.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				if(currentQuestion==null) return;
				String txt=aImgT.getText();
				File file;
				if(txt.length()==0)
					file=null;
				else
					file=new File(txt);
				Question q=currentQuestion.withAnswerImageFile(file);
				try{
					q.getAnswerImage();
					aImgT.setBackground(TEXTFIELD_OK_BACKGROUND);
				}catch(Exception x){
					aImgT.setBackground(TEXTFIELD_ERROR_BACKGROUND);
				}
				updateCurrentQuestion(q);
			}
		});

		DocumentListener answerListener=new DocumentAdapter() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				if(currentQuestion==null) return;
				for(int i=0; i<answers.length; ++i)
					if(e.getDocument()==answers[i].getDocument()){
						Answer na=new Answer(answers[i].getText(), i);
						updateCurrentQuestion(currentQuestion.withAnswer(na));
						break;
					}
			}
		};
		for(int i=0; i<answers.length; ++i)
			answers[i].getDocument().addDocumentListener(answerListener);
	}
	private List<Question> getClipboardContent() {
		return questionClipboard;
	}
	private void copyToClipboard(List<Question> qs) {
		questionClipboard=qs;
	}
	private void appendQuestion(Question nq) {
		DefaultListModel<Question> m = questionListModel();
		m.addElement(nq);
		ArrayList<Question> qs=new ArrayList<>(currentCategory.getQuestions());
		qs.add(nq);
		updateCurrentCategegory(currentCategory.withQuestions(qs));
		questionList.setSelectedIndex(m.size()-1);
	}
	void makeFileTab(){
		Box fileTab=Box.createVerticalBox();
		tabs.addTab("Datei", fileTab);
		JButton newFile=new JButton("Neu");
		JButton open=new JButton("Öffnen...");
		JButton add=new JButton("Dateiinhalt hinzufügen...");
		JButton save=new JButton("Speichern");
		JButton saveAs=new JButton("Speichern unter...");
		fileTab.add(newFile);
		fileTab.add(open);
		fileTab.add(add);
		fileTab.add(save);
		fileTab.add(saveAs);
		save.addActionListener(ae-> save());
		saveAs.addActionListener(ae -> showSaveAs());
		add.addActionListener(ae -> showAddDialog());
		open.addActionListener(ae -> showOpenDialog());
		newFile.addActionListener(ae->showNewFileDialog());
		save.setAlignmentX(.5f);
		saveAs.setAlignmentX(.5f);
		open.setAlignmentX(.5f);
		add.setAlignmentX(.5f);
		newFile.setAlignmentX(.5f);
	}
	void makeCategoriesTab(){
		JSplitPane catTab=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		tabs.addTab("Kategorien", catTab);
		JPanel lp=new JPanel(new BorderLayout());
		Box lb=Box.createVerticalBox();
		lp.add(new JScrollPane(loadedCategories), BorderLayout.CENTER);
		lp.add(lb, BorderLayout.SOUTH);
		JButton addCat=new JButton("Neue Kategorie");
		JButton delCat=new JButton("Kategorie löschen");
		lb.add(addCat);
		lb.add(delCat);
		catTab.setLeftComponent(lp);





		JPanel ep=new JPanel(new SpringLayout());
		catTab.setRightComponent(ep);
		ep.add(Box.createGlue());
		ep.add(Box.createGlue());

		ep.add(new JLabel("Titel"));
		JTextField title=new JTextField(30);
		ep.add(title);

		ep.add(new JLabel("Gewicht"));
		SpinnerNumberModel weight=new SpinnerNumberModel(1, 0, 10, 1);
		JSpinner weightSpinner = new JSpinner(weight);
		ep.add(weightSpinner);

		JButton catImgB=new JButton("Bild...");
		ep.add(catImgB);
		JTextField catImgT=new JTextField(30);
		ep.add(catImgT);

		JButton colorB=new JButton("Farbe...");
		ep.add(colorB);
		JTextField colorT=new JTextField(30);
		ep.add(colorT);	

		ep.add(Box.createGlue());
		ep.add(Box.createGlue());
		SpringUtilities.makeCompactGrid(ep, 6, 2, 0, 0, 10, 10);

		delCat.setEnabled(false);
		title.setEditable(false);
		weightSpinner.setEnabled(false);
		catImgB.setEnabled(false);
		catImgT.setEnabled(false);
		colorB.setEnabled(false);
		colorT.setEnabled(false);
		loadedCategories.addListSelectionListener(lse -> {
			boolean sel=loadedCategories.getSelectedIndex()!=-1;
			delCat.setEnabled(sel);
			title.setEditable(sel);
			weightSpinner.setEnabled(sel);
			catImgB.setEnabled(sel);
			catImgT.setEnabled(sel);
			colorB.setEnabled(sel);
			colorT.setEnabled(sel);
			tabs.setEnabledAt(2, sel);
			if(sel){
				currentCategory=loadedCategories.getSelectedValue();
				title.setText(currentCategory.getTitle());
				weight.setValue(currentCategory.getWeight());
				File file = currentCategory.getImageFile();
				catImgT.setText(file==null?"":file.getPath());
				colorT.setText(Colors.toString(currentCategory.getColor()));
				DefaultListModel<Question> qm = questionListModel();
				qm.removeAllElements();
				for(Question q: currentCategory.getQuestions()){
					qm.addElement(q);
					questionList.setSelectedIndex(0);
				}
			}else{
				currentCategory=null;
				title.setText("");
				weight.setValue(1);
				catImgT.setText("");
				colorT.setText("");
			}
		});
		addCat.addActionListener(ae -> {
			Category nc=new Category("Unbenannt", javafx.scene.paint.Color.WHITE, null, 1, Collections.emptySet());
			DefaultListModel<Category> m = categoryListModel();
			addCategory(nc);
			loadedCategories.setSelectedIndex(m.size()-1);
		});
		delCat.addActionListener(ae->{
			int i=loadedCategories.getSelectedIndex();
			DefaultListModel<Category> m = categoryListModel();
			m.remove(i);
			if(m.size()>0)
				if(m.size()>i)
					loadedCategories.setSelectedIndex(i);
				else
					loadedCategories.setSelectedIndex(m.size()-1);

		});
		title.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				if(currentCategory==null) return;
				updateCurrentCategegory(currentCategory.withTitle(title.getText()));
			}
		});
		weight.addChangeListener(ce -> {
			if(currentCategory==null) return;
			updateCurrentCategegory(currentCategory.withWeight(weight.getNumber().doubleValue()));
		});
		catImgB.addActionListener(ae -> {
			if(currentCategory==null) return;
			int opt=imgFC.showOpenDialog(catImgB);
			if(opt!=JFileChooser.APPROVE_OPTION) return;
			File f = imgFC.getSelectedFile();
			f=Paths.asRelativeTo(Paths.resourcesDir, f);
			catImgT.setText(f.getPath());
		});
		catImgT.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				if(currentCategory==null) return;
				String txt=catImgT.getText();
				File file;
				if(txt.length()==0)
					file=null;
				else
					file=new File(txt);
				Category c=currentCategory.withImageFile(file);
				try{
					c.getImage();
					catImgT.setBackground(TEXTFIELD_OK_BACKGROUND);
				}catch(Exception x){
					catImgT.setBackground(TEXTFIELD_ERROR_BACKGROUND);
				}
				updateCurrentCategegory(c);
			}
		});
		colorB.addActionListener(ae->{
			if(currentCategory==null) return;
			Color c=JColorChooser.showDialog(colorB, "Farbe für Kategorie auswählen", currentCategory.getAwtColor());
			if(c==null) return;
			colorT.setText(c.getRed()+"/"+c.getGreen()+"/"+c.getBlue());
		});
		colorT.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				if(currentCategory==null) return;
				try{
					javafx.scene.paint.Color c=Colors.parseColor(colorT.getText(), new PosBuffer(""));
					updateCurrentCategegory(currentCategory.withColor(c));
					Color cawt=Colors.toAwt(c);
					colorB.setBackground(cawt);
					colorB.setForeground(Colors.getReadable(cawt));
					colorT.setBackground(TEXTFIELD_OK_BACKGROUND);
				}catch(DataFormatException x){
					colorT.setBackground(TEXTFIELD_ERROR_BACKGROUND);
				}
			}

		});

	}








	private void updateCurrentQuestion(Question q) {
		if(currentCategory==null) return;
		if(currentQuestion==null) return;
		currentQuestion=q;
		int i = questionList.getSelectedIndex();
		questionListModel().set(i, q);
		ArrayList<Question> qs=new ArrayList<>(currentCategory.getQuestions());
		qs.set(i, q);
		updateCurrentCategegory(currentCategory.withQuestions(qs));

		changed=true;
	}

	private void updateCurrentCategegory(Category c) {
		if(currentCategory==null) return;
		currentCategory=c;
		categoryListModel().set(loadedCategories.getSelectedIndex(), c);
		changed=true;
	}
	private void showSaveAs() {
		while(true){
			int opt=jsonFC.showSaveDialog(this);
			if(opt!=JFileChooser.APPROVE_OPTION)
				return;
			if(jsonFC.getSelectedFile().exists()){
				int r=JOptionPane.showConfirmDialog(this, "Die gewählte Datei exstiert bereits. \n"
						+ "Soll sie wirklich überschrieben werden?.", "Warnung: Datei Überschreiben", 
						JOptionPane.YES_NO_CANCEL_OPTION);			
				if(r==JOptionPane.CANCEL_OPTION) return;
				if(r==JOptionPane.YES_OPTION) break;
			}else{
				break;
			}
		}
		opened=jsonFC.getSelectedFile();
		save();
	}
	private void showNewFileDialog() {
		boolean o=dataLossWarning();
		if(o) return;
		opened=null;
		clearData();
		changed=false;
	}
	private void clearData() {
		categoryListModel().removeAllElements();
	}
	private DefaultListModel<Category> categoryListModel() {
		return (DefaultListModel<Category>)loadedCategories.getModel();
	}
	private DefaultListModel<Question> questionListModel() {
		return (DefaultListModel<Question>)questionList.getModel();
	}
	private boolean dataLossWarning() {
		if(!changed) return false;
		int r=JOptionPane.showConfirmDialog(this, "Willst du das wirklich tun? \n"
				+ "Die geladenen Daten haben ungespeicherte Änderungen.", "Warnung: Ungespeicherte Änderungen", 
				JOptionPane.YES_NO_OPTION);
		return r!=JOptionPane.YES_OPTION;
	}
	private void showOpenDialog() {
		boolean o=dataLossWarning();
		if(o) return;
		int opt=jsonFC.showOpenDialog(this);
		if(opt!=JFileChooser.APPROVE_OPTION)
			return;
		clearData();
		addData(jsonFC.getSelectedFile());
		changed=false;
		opened=jsonFC.getSelectedFile();
	}
	private void addData(File file) {
		JSOWithPosition quizDataJSO;
		try {
			quizDataJSO = new JSONParser().parse(Paths.relative(Paths.resourcesDir, file));
			List<Category> quizData=JSOCodec.std.decodeList(quizDataJSO, Category.class);
			for(Category c: quizData)
				addCategory(c);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Ein-/Ausgabefehler", "Fehler beim Laden der Datei", JOptionPane.ERROR_MESSAGE);
		} catch (DataFormatException e) {
			JOptionPane.showMessageDialog(this, "Datenformatfehler in "+e.getPosition()+": "+e.getMessage(), 
					"Fehler beim Laden der Datei", JOptionPane.ERROR_MESSAGE);
		} catch (UnexpectedDataTypeException e) {
			JOptionPane.showMessageDialog(this, "Datenformatfehler in "+e.getPosition()+": "+e.getMessage(), 
					"Fehler beim Laden der Datei", JOptionPane.ERROR_MESSAGE);
		} catch (ParserException e) {
			JOptionPane.showMessageDialog(this, "JSON-Syntaxfehler in "+e.getPosition()+": "+e.message, 
					"Fehler beim Laden der Datei", JOptionPane.ERROR_MESSAGE);
		}

	}
	private void addCategory(Category c) {
		changed=true;
		DefaultListModel<Category> m=categoryListModel();
		m.addElement(c);
		if(m.size()==1)
			loadedCategories.setSelectedIndex(0);
	}
	private void showAddDialog() {
		int opt=jsonFC.showOpenDialog(this);
		if(opt!=JFileChooser.APPROVE_OPTION)
			return;
		addData(jsonFC.getSelectedFile());
	}
	private void save() {
		if(opened==null){
			showSaveAs();
			return;
		}

		try{
			List<Object> cats=new ArrayList<>();
			for(Object c: categoryListModel().toArray())
				cats.add(JSOCodec.std.encode(c));
			JSONRenderer.render(cats, opened, "\t");
			changed=false;
		}catch(IOException e){
			JOptionPane.showMessageDialog(this, "Ein-/Ausgabefehler", "Fehler beim Speichern der Datei", JOptionPane.ERROR_MESSAGE);
		}
	}

}
