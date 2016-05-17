package control_model;




import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Chester
 */
class FindInitial {
    
    public static int createKey(String alphabet, int level)
    {
        int key = 0;
        Random rnd = new Random();
        DBConnect db = new DBConnect();
        WordInfo wd = new WordInfo();
                
        while(true)
        {
            key = level*1000 + rnd.nextInt(1000);
            wd = db.query( key );
            if( wd.word == null ) continue;
            
            String temp_word1 = String.valueOf(wd.word.charAt(0));
            String temp_word2 = String.valueOf(alphabet.charAt(0));
            
            if( temp_word1.toUpperCase().equalsIgnoreCase ( temp_word2.toUpperCase() ) ) break;
        }
        
        return key;
    }
}
