package control_model;




import View.GUI;
import edu.cmu.sphinx.util.props.PropertyException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Chester
 */
public class Controller implements ActionListener, KeyListener
{
    private int menu_num = 0;
    private int sel_level = 0;
    private String sel_char = "";
    private int sound_vol;
    private int score;
    private int question_count; 
    private int input_count = 0;
    private String input_word = "";
    
    private int key;
    private String input_key = "";
    private static Boolean wordFlag = true;
    
    static DBConnect db;
    static GUI gui;
    static WordInfo wd = new WordInfo();
    
    static LinkedList<Line> speakers = new LinkedList<Line>();
    
    FindInitial fi;
    PronunPrac pp;
    
    public Controller() throws InterruptedException
    {
        db = new DBConnect();
        if( db.DBConnect() == false ) exitDBError();
        
        gui = new GUI();
        gui.showMenu();
        
        //jButton 리스너 추가
        gui.addAction_jButton1(this);
        gui.addAction_jButton2(this);
        gui.addAction_jButton3(this);
        gui.addAction_jButton4(this);
        gui.addAction_jButton5(this);
        gui.addAction_jButton6(this);
        gui.addAction_jButton7(this);
        gui.addAction_jButton8(this);
        gui.addAction_jButton9(this);
        gui.addAction_jButton10(this);
        gui.addAction_jButton11(this);
        gui.addAction_jButton12(this);
        gui.addAction_jButton13(this);
        gui.addAction_jButton14(this);
        gui.addAction_jButton15(this);
        gui.addAction_jButton16(this);
        gui.addAction_jButton17(this);
        gui.addAction_jButton18(this);
        gui.addAction_jButton19(this);
        gui.addAction_jButton20(this);
        
        gui.addKeyListener(this);
        gui.requestFocus();
    }
    
    public void selectMenu(int num)
    {
        this.menu_num = num;
    }
    
    public void selectLevel(int num)
    {
        this.sel_level = num;
    }
    
    public void selectChar(String input)
    {
        this.sel_char = input;
    }

    public void createKey() // issue : x와 같은 단어들은 단어의 갯수가 한정적이라서 뽑아내는데 시간이 좀 걸림.
    {
    	int temp_key = 0;
    	Random rand = new Random();
    	
    	temp_key += sel_level * 1000 + rand.nextInt(1000);
    	
    	key = temp_key;
    }
 
    public void inputWord()
    {
        // 초기화 해주는 부분
        input_count = 0;
        input_word = "";
        
        while(true)
        {
            createKey();
            wd = db.query(key);
            if(wd.word != null) break;
        }

        if(menu_num == 2)
            gui.setWordInfo(wd.word, wd.def,wd.phonics, wd.img_path);
        else if(menu_num == 3)
            gui.setSpeakInfo(wd.word, wd.def, wd.phonics, wd.img_path);
        
        outputSound();
        startTimer();
    }

    public void outputSound()
    {
        try
        {
            File theFile = new File(wd.sound_path); // C:\\eng_play\\sound\\test.wav 이 형식이어야 함!
            FileInputStream fis = new FileInputStream(theFile); 
            AudioStream as = new AudioStream(fis);
            AudioPlayer.player.start(as);
        }
        catch(IOException ex)
        {
            System.out.println("");
        }
    }
    
    public void outputSound(boolean flag)
    {
        try
        {
            File theFile = null;
            if(flag == true)
            {
                theFile = new File("eng_play/program_sound/answer_effect.wav");
            }
            else
            {
                theFile = new File("eng_play/program_sound/wrong_effect.wav");
            }
            FileInputStream fis = new FileInputStream(theFile); 
            AudioStream as = new AudioStream(fis);
            AudioPlayer.player.start(as);
        }
        catch(IOException ex)
        {
            System.out.println("");
        }
    }
    
    public void setInit()
    {
        menu_num = 0;
        sel_level = 0;
        sel_char = "";
        score = 0;
        question_count = 0; 
        input_count = 0;
        input_word = "";
    
        key = 0;
        input_key = "";
        wordFlag = true;
        gui.setInit();
        wd.sound_path = "";
    }
    
    public void exitDBError()
    {
        JOptionPane.showMessageDialog(null, "데이터베이스와 연동을 실패하였습니다.", "데이터 베이스 연동 실패", JOptionPane.WARNING_MESSAGE);
        System.exit(1);
    }

    public void setVolume(float value) {
        // TODO implement here
        
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();

        for(Mixer.Info mixerInfo : mixers)
        {
            if(!mixerInfo.getName().equalsIgnoreCase ("Java Sound Audio Engine")) 
            {
                System.out.println(mixerInfo.getName());
                continue;
            }
                    
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            Line.Info[] lines = mixer.getSourceLineInfo();

            for(Line.Info info : lines)
            {
                try
                {
                    Line line = mixer.getLine(info);
                    speakers.add(line);                  
                }
                catch(LineUnavailableException e){ e.printStackTrace();
                }
            }
            System.out.println("setting volume to "+value);
            for(Line line : speakers)
            {
                try
                {
                    line.open();
                    FloatControl control = (FloatControl)line.getControl(FloatControl.Type.MASTER_GAIN);
                    control.setValue(limit(control,value));
                }
                catch (LineUnavailableException e) { continue; }
                catch(java.lang.IllegalArgumentException e) { continue; }
            }
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
                
        switch(e.getKeyCode())
        {
            case 49 : input_key = "1"; break;
            case 50 : input_key = "2"; break;
            case 51 : input_key = "3"; break;
            case 65 : input_key = "a"; break;
            case 66 : input_key = "b"; break;
            case 67 : input_key = "c"; break;
            case 68 : input_key = "d"; break;
            case 69 : input_key = "e"; break;
            case 70 : input_key = "f"; break;
            case 71 : input_key = "g"; break;
            case 72 : input_key = "h"; break;
            case 73 : input_key = "i"; break;
            case 74 : input_key = "j"; break;
            case 75 : input_key = "k"; break;
            case 76 : input_key = "l"; break;
            case 77 : input_key = "m"; break;
            case 78 : input_key = "n"; break;
            case 79 : input_key = "o"; break;
            case 80 : input_key = "p"; break;
            case 81 : input_key = "q"; break;
            case 82 : input_key = "r"; break;
            case 83 : input_key = "s"; break;
            case 84 : input_key = "t"; break;
            case 85 : input_key = "u"; break;
            case 86 : input_key = "v"; break;
            case 87 : input_key = "w"; break;
            case 88 : input_key = "x"; break;
            case 89 : input_key = "y"; break;
            case 90 : input_key = "z"; break;
            case 189 : input_key = "-"; break;
            default : input_key = ""; break;
        }
        
        if( 49 <= e.getKeyCode() && e.getKeyCode() 
                <= 51 && menu_num == 0 && sel_level == 0 ) // 키보드로 메뉴입력 처리 중 메뉴
        {
            if(e.getKeyCode() == 49) selectMenu(1);
            else if(e.getKeyCode() == 50) selectMenu(2);
            else if(e.getKeyCode() == 51) selectMenu(3);
            
            gui.showLevel();
            gui.requestFocus();
        }
        
        else if( 49 <= e.getKeyCode() && e.getKeyCode() 
                <= 51 && menu_num != 0 && sel_level == 0 ) // 키보드로 메뉴입력 처리 중 레벨
        {
            if(e.getKeyCode() == 49) selectLevel(1);
            else if(e.getKeyCode() == 50) selectLevel(2);
            else if(e.getKeyCode() == 51) selectLevel(3);
            
            if(menu_num == 1) 
            {
                gui.showAlphabet();
                gui.requestFocus();
            }
            else if(menu_num == 2) 
            {
                gui.showWords();
                gui.requestFocus();
                inputWord();
            }
            else if(menu_num == 3)
            {
                inputWord();
                try {
                    pp = new PronunPrac(sel_level);
                } catch (IOException ex) {
                    Logger.getLogger(Controller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                } catch (PropertyException ex) {
                    Logger.getLogger(Controller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(Controller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
                gui.showSpeak();
                gui.requestFocus();
            }
        }
        
        if( 65 <= e.getKeyCode() && e.getKeyCode() <= 90 && menu_num == 1 && sel_level != 0 ) // 단어정보출력, 초성놀이
        {
            selectChar(input_key);
            key = fi.createKey(sel_char, sel_level); // 여기가 랜덤으로 단어 정보 땡겨오는 곳
            wd = db.query(key);
            gui.setAlphaInfo(wd.word, wd.def, wd.phonics, wd.img_path);
            outputSound();
        }
        
        if( 65 <= e.getKeyCode() && e.getKeyCode() <= 90 && menu_num == 2 && sel_level != 0 && input_count != wd.word.length() ) // 단어입력, 단어놀이
        {
            Boolean isCorrect = true;
            gui.inputWord(input_key, input_count);
            input_word += input_key;
            input_count++;
            
            gui.revalidate();
            gui.repaint();
            
            if(input_count == wd.word.length()) // 단어의 길이에 만큼 입력된 경우
            {
                wordFlag = false;
                
                if(input_word.equalsIgnoreCase (wd.word)) 
                {
                    // 맞는 경우
                    score++;
                    isCorrect = true;
                }
                else
                {
                    // 틀린 경우
                    isCorrect = false;
                }
                
                question_count++;
                gui.printAnswer(wd.word);
                outputSound(isCorrect);
                outputSound();
                
            }
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == gui.getButton1()){
            selectMenu(1);
            gui.showLevel();
        }
        else if(e.getSource() == gui.getButton2()){
            selectMenu(2);
            gui.showLevel();
        }
        else if(e.getSource() == gui.getButton3()){
            selectMenu(3);
            gui.showLevel();
        }
        else if(e.getSource() == gui.getButton4()){
            selectMenu(4);
            gui.showVolume();
        }
        
        //여기까지가 show menu에서 button 1~4 클릭 했을때.
        
        else if(e.getSource() == gui.getButton5()){
            selectLevel(1);
            if(menu_num == 1) 
            {
                gui.showAlphabet();
                gui.requestFocus();
            }
            else if(menu_num == 2) 
            {
                gui.showWords();
                gui.requestFocus();
                inputWord();
            }
            else if(menu_num == 3)
            {
                try {
                    pp = new PronunPrac(sel_level);
                } catch (IOException ex) {
                    Logger.getLogger(Controller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                } catch (PropertyException ex) {
                    Logger.getLogger(Controller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(Controller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
                gui.showSpeak();
                gui.requestFocus();
                inputWord();
            }
        }
        else if(e.getSource() == gui.getButton6()){
            selectLevel(2);
            if(menu_num == 1) 
            {
                gui.showAlphabet();
                gui.requestFocus();
            }
            else if(menu_num == 2) 
            {
                gui.showWords();
                gui.requestFocus();
                inputWord();
            }
            else if(menu_num == 3)
            {
                try {
                    pp = new PronunPrac(sel_level);
                } catch (IOException ex) {
                    Logger.getLogger(Controller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                } catch (PropertyException ex) {
                    Logger.getLogger(Controller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(Controller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
                gui.showSpeak();
                gui.requestFocus();
                inputWord();
            }
        }
        else if(e.getSource() == gui.getButton7()){
            selectLevel(3);
            if(menu_num == 1) 
            {
                gui.showAlphabet();
                gui.requestFocus();
            }
            else if(menu_num == 2) 
            {
                gui.showWords();
                gui.requestFocus();
                inputWord();
            }
            else if(menu_num == 3)
            {
                try {
                    pp = new PronunPrac(sel_level);
                } catch (IOException ex) {
                    Logger.getLogger(Controller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                } catch (PropertyException ex) {
                    Logger.getLogger(Controller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(Controller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
                gui.showSpeak();
                gui.requestFocus();
                inputWord();
            }
        }
        else if(e.getSource() == gui.getButton8()){
            this.setInit();
            gui.showMenu();
            gui.requestFocus();
        }
        
        // Level의 button 1~4 클릭 했을때
        
        else if(e.getSource() == gui.getButton9()){
            //홈 버튼
            this.setInit();
            gui.showMenu();
            gui.requestFocus();
            
        }
        else if(e.getSource() == gui.getButton10()){
            //다시 듣기 버튼
            outputSound();
            gui.requestFocus();
        }
        // 알파벳 button 1~2
        
        else if(e.getSource() == gui.getButton11()){
            //홈 버튼
            this.setInit();
            gui.showMenu();
            gui.requestFocus();
        }
        else if(e.getSource() == gui.getButton12()){
            //정답 보기 버튼
            question_count++;
            outputSound(false);
            outputSound();
            gui.printAnswer(wd.word);
            wordFlag = false;
        }
        // words button 1~2
        
        else if(e.getSource() == gui.getButton13()){
            //홈 버튼
            this.setInit();
            gui.showMenu();
            gui.requestFocus();
        }
        else if(e.getSource() == gui.getButton14()){
            //다시 듣기 버튼
            outputSound();
            gui.requestFocus();
        }       
        // speak Button 1~2
        
        else if(e.getSource() == gui.getButton15()){
            //점수보기화면의 홈 버튼
            this.setInit();
            gui.showMenu();
            gui.requestFocus();
        }
        
        // 볼륨설정의 버튼 5개
        
        else if(e.getSource() == gui.getButton16()){
            //볼륨 1 버튼
            sound_vol = 25;
            setVolume(sound_vol);
            JOptionPane.showMessageDialog(null, "볼륨 크기 : 25%", "볼륨설정", JOptionPane.INFORMATION_MESSAGE);
        }
        
        else if(e.getSource() == gui.getButton17()){
            //볼륨 2 버튼
            sound_vol = 50;
            setVolume(sound_vol);
            JOptionPane.showMessageDialog(null, "볼륨 크기 : 50%", "볼륨설정", JOptionPane.INFORMATION_MESSAGE);
        }
        
        else if(e.getSource() == gui.getButton18()){
            //볼륨 3 버튼
            sound_vol = 75;
            setVolume(sound_vol);
            JOptionPane.showMessageDialog(null, "볼륨 크기 : 75%", "볼륨설정", JOptionPane.INFORMATION_MESSAGE);
        }
        
        else if(e.getSource() == gui.getButton19()){
            //볼륨 4 버튼
            sound_vol = 100;
            setVolume(sound_vol);
            JOptionPane.showMessageDialog(null, "볼륨 크기 : 100%", "볼륨설정", JOptionPane.INFORMATION_MESSAGE);
        }
        
        else if(e.getSource() == gui.getButton20()){
            //볼륨설정의 홈 버튼
            this.setInit();
            gui.showMenu();
            gui.requestFocus();
        }
    }
    
    // Timer
    static Timer time;
    static int wordTick = 0;
    
    public void startTimer()
    {
        System.out.println("Timer start!");
        time = new Timer(1000,timeListener);
        time.start();
    }
    private class ActionTime implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            System.out.println("Word Tick : "+wordTick);
            if(menu_num == 3){
                //음성인식일때 동작하는 메소드
                if(wordFlag == true){
                    gui.imageShake();
                     if(wordTick < 10){
                         //한문제당 10초 소요
                        wordTick++;
                        gui.setChance(wordTick);
                        if(wd.word.equals(pp.recognizing()))
                            wordFlag = false;
                    }
                     else{
                         wordFlag = false;
                     }
                }
                else if(wordFlag == false || wordTick >= 10){
                    time.stop();
                    wordTick =0;
                    gui.setChance(wordTick);
                    wordFlag = false;
                    if(question_count == 10){
                        gui.showScore();
                        gui.setScore(score);
                    }
                    else{
                        inputWord();
                    }
                    
                }
            }
            else{
                if(wordFlag == true)
                {
                    gui.imageShake();
                }
                else{
                    if(wordTick < 2)
                    {
                        wordTick ++;
                    }
                    else
                    {
                        time.stop();
                        wordTick = 0;
                        if(question_count == 10){
                            gui.showScore();
                            gui.setScore(score);

                        }
                        else
                        {
                            inputWord();
                        } 
                    }
                }
            }
        }
     }
    private ActionTime timeListener = new ActionTime();

    private float limit(FloatControl control, float value) {
        throw new UnsupportedOperationException( control.getUnits() + " is Not supported volume" + value); //To change body of generated methods, choose Tools | Templates.
    }
}