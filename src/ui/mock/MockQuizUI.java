package ui.mock;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

import model.Answer;
import model.Category;
import model.GameState;
import model.HallOfFame;
import model.Question;
import model.RoundState;
import model.Settings;
import model.Team;
import ui.ControllerCallback;
import ui.ControllerCallback.TitleScreenOption;
import ui.QuizUI;
import util.SpringUtilities;

public class MockQuizUI implements QuizUI {
	ControllerCallback ccb;

	JFrame window;

	private JSlider timeDisplay;
	private JComponent view;

	{
		window=new JFrame("Sexduell");
		window.setLayout(new BorderLayout());


		JButton cancelGame=new JButton("Spiel abbrechen");
		window.add(cancelGame, BorderLayout.SOUTH);
		cancelGame.addActionListener(ae->ccb.cancelGame());
	}




	public void start() {
		window.setSize(800, 500);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				if(ccb!=null)
					ccb.cancelGame();
			}
		});

	}

	@Override
	public void setControllerCallback(ControllerCallback ccb) {
		this.ccb=ccb;
	}

	private void setViewLater(JComponent jc){
		setViewLater(jc, null);
	}
	private void setViewLater(JComponent jc, JComponent focus){
		later(()->{
			setView(jc);
			if(focus!=null)
				focus.requestFocus();
		});
	}
	private void setView(JComponent jc){
		clear();
		window.add(jc, BorderLayout.CENTER);
		window.validate();
		view=jc;
	}
	private void later(Runnable r){
		SwingUtilities.invokeLater(r);
	}
	private void clear() {
		if(view!=null)
			window.getContentPane().remove(view);
	}

	@Override
	public void showTitleScreen() {
		JPanel titleScreen = new JPanel(new BorderLayout());
		titleScreen.add(new JLabel("<html><h1>Sexduell!</h1>"));
		Box buttons=Box.createVerticalBox();
		titleScreen.add(buttons, BorderLayout.SOUTH);
		JButton start=new JButton("Start");
		JButton shof=new JButton("Hall of Fame anzeigen");
		JButton showSettings=new JButton("Einstellungen anzeigen");
		buttons.add(start);
		buttons.add(shof);
		buttons.add(showSettings);
		start.addActionListener(ae -> ccb.titleScreenDismissed(TitleScreenOption.START_GAME));
		shof.addActionListener(ae -> ccb.titleScreenDismissed(TitleScreenOption.SHOW_HALL_OF_FAME));
		showSettings.addActionListener(ae -> ccb.titleScreenDismissed(TitleScreenOption.EDIT_SETTINGS));
		setViewLater(titleScreen, start);
	}

	@Override
	public void promptForTeamNames() {

		Box startScreen = Box.createVerticalBox();
		startScreen.add(new JLabel("Teamnamen eingeben"));
		JTextField team1Name, team2Name;
		team1Name=new JTextField();
		team2Name=new JTextField();
		ActionListener al= ae -> {
			String t1n = team1Name.getText();
			String t2n = team2Name.getText();
			if(t1n.length()==0)
				team1Name.requestFocus();
			else if(t2n.length()==0)
				team2Name.requestFocus();
			else{
				team1Name.setText("");
				team2Name.setText("");
				ccb.teamNamesEntered(t1n, t2n);
			}
		};
		team1Name.addActionListener(al);
		team2Name.addActionListener(al);
		startScreen.add(team1Name);
		startScreen.add(team2Name);
		team1Name.setMaximumSize(new Dimension(400, 30));
		team2Name.setMaximumSize(new Dimension(400, 30));
		startScreen.add(Box.createGlue());


		setViewLater(startScreen, team1Name);
	}

	@Override
	public void showCategorySelector(boolean team1, GameState gs, Category... categories) {
		final Box b=Box.createVerticalBox();
		b.add(new JLabel("Kategorie auswählen (Team "+gs.getTeam(team1).getName()+" ist dran)"));
		for(int i=0; i<categories.length; ++i){
			final int fi=i;
			Category cat = categories[i];
			BufferedImage bim=null;
			try{
				bim=cat.getImage();
			}catch(IOException x){
				x.printStackTrace();
			}
			JButton button;
			if(bim==null)
				button=new JButton(cat.getTitle());
			else
				button=new JButton(cat.getTitle(), new ImageIcon(bim));
			b.add(button);
			button.addActionListener(ae->ccb.categorySelected(fi));
		}
		b.add(Box.createGlue());

		setViewLater(b);
	}

	@Override
	public void showRoundOverview(GameState gs, int totalRounds, int qprpt) {
		JPanel p=new JPanel(new SpringLayout());
		List<RoundState> rounds = gs.getRounds();
		JButton button=null;
		for(int round=0; round<totalRounds; round++){
			RoundState rs=round<rounds.size()?rounds.get(round):null;
			insertAnswerStatusLabels(p, rs, true, qprpt);
			if(rs!=null)
				p.add(new JLabel(rs.getCategory().getTitle()));
			else if(round==rounds.size()){
				button=new JButton("Nächste Runde!");
				p.add(button);
				button.addActionListener(ae->ccb.roundOverviewDismissed());
			}else
				p.add(new JLabel(" "));
			insertAnswerStatusLabels(p, rs, false, qprpt);
		}
		SpringUtilities.makeCompactGrid(p, totalRounds, 2*qprpt+1, 0, 0, 3, 3);
		Box b=Box.createVerticalBox();
		b.add(p);
		b.add(Box.createGlue());
		setViewLater(b, button);
	}

	static class AnswerStatusLabel extends JComponent{
		Color color;
		public AnswerStatusLabel(Color c) {
			color=c;
		}
		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(color);
			g.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
		}

		private static final long serialVersionUID = -8524253099472733194L;

	}
	private void insertAnswerStatusLabels(JPanel p, RoundState rs, boolean team1, int qprpt) {
		for(int q=0; q<qprpt; ++q){
			Boolean correct=rs==null?null:rs.getTeamResults(team1, q);
			AnswerStatusLabel disp=new AnswerStatusLabel(correct==null?Color.black:correct?Color.green:Color.red);
			disp.setMinimumSize(new Dimension(50, 30));
			disp.setPreferredSize(disp.getMinimumSize());
			p.add(disp);
		}
	}

	@Override
	public void showQuestion(GameState g, Question q, Answer[] permutedAnswers) {
		Box b=Box.createVerticalBox();
		BufferedImage bim=null;
		try {
			bim=q.getQuestionImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(bim!=null)
			b.add(new JLabel(new ImageIcon(bim)));
		b.add(new JLabel(q.getQuestionText()));
		JPanel answers=new JPanel(new GridLayout(1, 2));
		for(int team=1; team<=2; ++team){
			final boolean team1=team==1;
			Box teamAnswers=Box.createVerticalBox();
			answers.add(teamAnswers);
			teamAnswers.add(new JLabel("Antwort Team "+team+":"));
			for(int i=0; i<permutedAnswers.length; ++i){
				int fi=i;
				JButton button=new JButton(permutedAnswers[i].getText());
				teamAnswers.add(button);
				button.addActionListener(ae->{
					if(team1)
						ccb.team1AnswerEntered(fi);
					else
						ccb.team2AnswerEntered(fi);
				});
			}
		}
		b.add(answers);
		b.add(Box.createGlue());
		timeDisplay=new JSlider(0, 10000);

		b.add(timeDisplay);
		b.add(Box.createGlue());
		setViewLater(b);
	}

	@Override
	public void showSolution(GameState g, Question q, Answer[] permutedAnswers, int team1AnswerIndex, int team2AnswerIndex) {
		Box b=Box.createVerticalBox();
		BufferedImage bim=null;
		try {
			bim=q.getAnswerImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(bim!=null)
			b.add(new JLabel(new ImageIcon(bim)));
		b.add(new JLabel(q.getAnswerText()));
		if(q.getAnswerSource()!=null){
			b.add(new JLabel("Quelle:"));
			b.add(new JLabel(q.getAnswerSource()));
		}

		JPanel answers=new JPanel(new GridLayout(1, 2));
		for(int team=1; team<=2; ++team){
			final boolean team1=team==1;
			Box teamAnswers=Box.createVerticalBox();
			answers.add(teamAnswers);
			teamAnswers.add(new JLabel("Antwort Team "+team+":"));
			for(int i=0; i<permutedAnswers.length; ++i){
				JButton button=new JButton(permutedAnswers[i].getText());
				teamAnswers.add(button);
				if(permutedAnswers[i].isCorrect())
					button.setBackground(Color.green);
				else if(i==(team1?team1AnswerIndex:team2AnswerIndex))
					button.setBackground(Color.red);
			}
		}
		b.add(answers);

		JButton button=new JButton("OK");
		b.add(button);

		button.addActionListener(ae->ccb.solutionScreenDismissed());
		b.add(Box.createGlue());
		setViewLater(b, button);
	}

	@Override
	public void setTimerDisplay(long millisRemaining, long millisTotal) {
		later(()->{
			timeDisplay.setValue((int) (millisRemaining*timeDisplay.getMaximum()/millisTotal));
		});
	}

	@Override
	public void showWinner(GameState gs, int totalRounds,	int qprpt) {
		JPanel p=new JPanel(new SpringLayout());
		List<RoundState> rounds = gs.getRounds();
		for(int round=0; round<totalRounds; round++){
			RoundState rs=round<rounds.size()?rounds.get(round):null;
			insertAnswerStatusLabels(p, rs, true, qprpt);
			if(rs!=null)
				p.add(new JLabel(rs.getCategory().getTitle()));
			else 
				p.add(new JLabel(" "));
			insertAnswerStatusLabels(p, rs, false, qprpt);
		}
		SpringUtilities.makeCompactGrid(p, totalRounds, 2*qprpt+1, 0, 0, 3, 3);
		Box b=Box.createVerticalBox();
		int team1Points=gs.getTeamPoints(true);
		int team2Points=gs.getTeamPoints(false);
		b.add(p);
		if(team1Points==team2Points){
			b.add(new JLabel("<html><h1>Unentschieden!</h1>"));
			b.add(new JLabel("Team "+gs.getTeam(true).getName()+" hat "
					+team1Points+" Punkt"+(team1Points!=1?"e":"")));
			b.add(new JLabel("Team "+gs.getTeam(false).getName()+" hat auch "
					+team2Points+" Punkt"+(team2Points!=1?"e":"")));
		}else{
			boolean team1Win=team1Points>team2Points;
			b.add(new JLabel("<html><h1>Und der Gewinner ist:</h1>"));
			int winnerPoints = team1Win?team1Points:team2Points;
			int loserPoints = team1Win?team2Points:team1Points;
			b.add(new JLabel("Team "+gs.getTeam(team1Win).getName()+" mit "
					+winnerPoints+" Punkt"+(winnerPoints!=1?"en":"")));
			b.add(new JLabel("Team "+gs.getTeam(!team1Win).getName()+" hat nur "
					+loserPoints+" Punkt"+(loserPoints!=1?"e":"")));
		}
		b.add(Box.createGlue());
		JButton ok=new JButton("OK");
		b.add(ok);
		ok.addActionListener(ae->ccb.winnerScreenDismissed());
		b.add(Box.createGlue());

		setViewLater(b, ok);

	}

	@Override
	public void showHallOfFame(HallOfFame hof, Team team1, Team team2) {
		JPanel p=new JPanel(new SpringLayout());
		int hofRows=10;
		Iterator<HallOfFame.Entry> it=hof.getEntries().iterator();
		for(int i=0; i<hofRows; ++i){
			if(it.hasNext()){
				HallOfFame.Entry e=it.next();

				Color bg;
				if(team1!=null && e.getTeam().getId()==team1.getId())
					bg=new Color(0xDDDDFF);
				if(team2!=null && e.getTeam().getId()==team2.getId())
					bg=new Color(0xDDDDFF);
				else if(i%2==0)
					bg=new Color(0xFFFFFF);
				else
					bg=new Color(0xDDDDDD);
				
				JLabel tl = new JLabel("Team "+e.getTeam().getName()+"");
				tl.setBackground(bg);
				p.add(tl);
				
				JLabel pl=new JLabel(e.getPoints()+" Punkte");
				pl.setBackground(bg);
				p.add(pl);

				JLabel locl=new JLabel(" "+e.getLocation());
				locl.setBackground(bg);
				p.add(locl);
			}else{
				p.add(new JLabel("---"));
				p.add(new JLabel("---"));
			}
		}
		SpringUtilities.makeCompactGrid(p, hofRows, 3, 0, 0, 20, 3);
		Box b=Box.createVerticalBox();
		b.add(new JLabel("<html><h1>Hall of Fame</h1>"));
		b.add(p);
		JButton ok=new JButton("OK");
		b.add(Box.createGlue());
		b.add(ok);
		b.add(Box.createGlue());
		ok.addActionListener(ae->ccb.hallOfFameDismissed());
		setViewLater(b, ok);
	}

	@Override
	public void giveDoubleAnswerMessage(boolean team1) {
		later(()->{
			System.out.println("Double answer by team "+(team1?1:2));
			Toolkit.getDefaultToolkit().beep();
		});
	}

	@Override
	public void showSettingsScreen(Settings s) {
		JPanel p=new JPanel(new SpringLayout());

		p.add(new JLabel("Zeit pro Frage"));
		SpinnerNumberModel timeout=new SpinnerNumberModel(s.getTimeoutMs()*0.001, 0, 1000000, 1000);
		p.add(new JSpinner(timeout));
		
		p.add(new JLabel("Ort (für Hall of Fame)"));
		JTextField location = new JTextField(s.getLocation());
		p.add(location);
		
		p.add(new JLabel("Strenge Zeitgrenze"));
		JCheckBox strictTimeout=new JCheckBox("                                   ");
		strictTimeout.setSelected(s.isStrictTimeout());
		p.add(strictTimeout);
		
		
		SpringUtilities.makeCompactGrid(p, 3, 2, 0, 0, 3, 3);

		
		Box b=Box.createVerticalBox();
		b.add(new JLabel("<html><h1>Einstellungen</h1>"));
		b.add(p);
		JButton ok=new JButton("OK");
		JButton cancel=new JButton("Abbrechen");
		b.add(Box.createGlue());
		b.add(ok);
		b.add(cancel);
		b.add(Box.createGlue());
		ok.addActionListener(ae->{
			Settings newSettings=s.clone();
			newSettings.setLocation(location.getText());
			newSettings.setStrictTimeout(strictTimeout.isSelected());
			newSettings.setTimeoutMs((int) (timeout.getNumber().doubleValue()*1000));
			ccb.settingsScreenDismissed(newSettings);
		});
		cancel.addActionListener(ae-> ccb.settingsScreenDismissed(null));
		setViewLater(b, ok);
	
	}
}
