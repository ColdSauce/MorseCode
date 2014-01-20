/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package morsecode;

import java.util.ArrayList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author stefan987
 */
public class MorseCode {
    
    //each unit of morse code will be 240 miliseoncds long
    private final int oneUnitOfMorseCode = 240;
    
    
    //Morse code letter configuration
    private ArrayList<Letter> morseCodeAlphabet = new ArrayList<Letter>() {
        {
            add(new Letter(new char[]{'.', '-'}));               //A 
            add(new Letter(new char[]{'-', '.', '.', '.'}));     //B
            add(new Letter(new char[]{'-', '.', '-', '.'}));     //C
            add(new Letter(new char[]{'-', '.', '.'}));          //D
            add(new Letter(new char[]{'.'}));                    //E
            add(new Letter(new char[]{'.', '.', '-', '.'}));     //F
            add(new Letter(new char[]{'-', '-', '.'}));          //G
            add(new Letter(new char[]{'.', '.', '.', '.'}));     //H
            add(new Letter(new char[]{'.', '.'}));               //I
            add(new Letter(new char[]{'.', '-', '-', '-'}));     //J
            add(new Letter(new char[]{'-', '.', '-'}));          //K
            add(new Letter(new char[]{'.', '-', '.', '.'}));     //L
            add(new Letter(new char[]{'-', '-'}));               //M
            add(new Letter(new char[]{'-', '.'}));               //N
            add(new Letter(new char[]{'-', '-', '-'}));          //O
            add(new Letter(new char[]{'.', '-', '-', '.'}));     //P
            add(new Letter(new char[]{'-', '-', '.', '-'}));     //Q
            add(new Letter(new char[]{'.', '-', '.'}));          //R
            add(new Letter(new char[]{'.', '.', '.'}));          //S
            add(new Letter(new char[]{'-'}));                    //T
            add(new Letter(new char[]{'.', '.', '-'}));          //U
            add(new Letter(new char[]{'.', '.', '.', '-'}));     //V
            add(new Letter(new char[]{'.', '-', '-'}));          //W
            add(new Letter(new char[]{'-', '.', '.', '-'}));     //X 
            add(new Letter(new char[]{'-', '.', '-', '-'}));     //Y
            add(new Letter(new char[]{'-', '-', '.', '.'}));     //Z
            
            add(new Letter(new char[]{'.', '-', '-', '-', '-'}));//1
            add(new Letter(new char[]{'.', '.', '-', '-', '-'}));//2
            add(new Letter(new char[]{'.', '.', '.', '-', '-'}));//3
            add(new Letter(new char[]{'.', '.', '.', '.', '-'}));//4
            add(new Letter(new char[]{'.', '.', '.', '.', '.'}));//5
            add(new Letter(new char[]{'-', '.', '.', '.', '.'}));//6
            add(new Letter(new char[]{'-', '-', '.', '.', '.'}));//7
            add(new Letter(new char[]{'-', '-', '-', '.', '.'}));//8
            add(new Letter(new char[]{'-', '-', '-', '-', '.'}));//9
            add(new Letter(new char[]{'-', '-', '-', '-', '-'}));//0


        }
    };
    //Sound rate
    private float SAMPLERATE = 8000;

    private void createTone(int hz, int msecs, double vol) throws LineUnavailableException {
        byte[] bufffer = new byte[1];

        //First param is float sampleRate
        //Second param is int sample size in bits
        //Third param is the int amount of channels
        //Fourth param is boolean signed
        //Fifth param is enable big endian
        AudioFormat af = new AudioFormat(SAMPLERATE, 8, 1, true, false);


        SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(af);
        sourceDataLine.open(af);
        sourceDataLine.start();
        for (int i = 0; i < msecs * 8; i++) {
            double angle = i / (SAMPLERATE / hz) * 2.0 * Math.PI;
            bufffer[0] = (byte) (Math.sin(angle) * 120.0 * vol);
            sourceDataLine.write(bufffer, 0, 1);
        }
        sourceDataLine.drain();
        sourceDataLine.stop();
        sourceDataLine.close();
    }

    public void playMessage(String message) throws Exception {
        //This method basically converts the parameter to a char[] seeing as it's most logical
        //The user shouldn't need to do this
        playMessage(message.toLowerCase().toCharArray());
    }

    private void playMessage(char[] message) throws Exception {
        //Tests if message contains anything other than 0-9 and a-z
        //throws an exception if a letter isn't alphanumeric.
        testAlphaNumeric(message);
        
        
        //These coorespond to units used in morse code
        final int spaceBetweenLetters = 3;
        final int spaceBetweenWords = 7;
        
        //Iterates through the message pausing where it needs to and plays beeps of morse code
        for (int i = 0; i < message.length; i++) {
            char c = message[i];
            if(c == ' '){
                Thread.sleep(oneUnitOfMorseCode*spaceBetweenWords);
            }
            else{
                playLetter(c);
                Thread.sleep(oneUnitOfMorseCode*spaceBetweenLetters);
            }
            
        }
    }

    private void testAlphaNumeric(char[] message) throws Exception {
        for (char letter : message) {
            if (!isCharacterAlphabet(letter) && !isCharacterNumeric(letter) && letter != ' ') {
                throw new Exception("Message must be ALPHA-NUMERICAL");
            }
        }
    }
    
    private boolean isCharacterAlphabet(char letter) {
        return ((letter >= 'a') && (letter <= 'z'));
    }

    private boolean isCharacterNumeric(char letter) {
        return ((letter >= '0') && (letter <= '9'));
    }

    private void playLetter(char letter) throws Exception{
        //ALGORITHM:
        //
        //offSet refers to the offset required to "normalize" numbers. Converting ascii codes to alphabet and numeral codes.
        
        // example: ASCII 97 == > alphabet == > a
        // this is used to make accessing the "alphabet" arraylist much easier.
        // this is due to the fact that the alphabets arraylist works as though it was a literal alphabet
        // in that the 0th index is a, the 1st index is b and so on
        
        //numbers work differently in the sense that number 0 is the 26th index of the alphabets arraylist
        //to access it, a person needs to convert the ascii value to its real value 'example: ascii 48 converts to 0'
        // then a person needs to add 25 to this value
        
        
        //Unitl a final int is declared, it can be changed.
        final int offSet;
        int normalizedValue;
        
        //offset needs to be determined case by case in regards to either being an alphabetical character or numerical character
        if(isCharacterAlphabet(letter)){
            offSet = 97;
        }
        else{
            offSet = 48;
        }
        
        normalizedValue = letter - offSet;

        
        char[] specificCode = this.morseCodeAlphabet.get(normalizedValue).getMorseCode();
        
        
        //////numbers coorespond to units/////////////
        final int dashLength = 3;
        final int dotLength = 1;
        
        final int spaceBetweenOneLetter = 1;
        /////////////////////////////////////////////
        //Iterate through the character array
        for (int i = 0; i < specificCode.length; i++) {
            char c = specificCode[i];
            if(c == '-'){
                this.createTone(600, oneUnitOfMorseCode*dashLength, 0.5);
            }
            else if(c == '.'){
                this.createTone(600, oneUnitOfMorseCode*dotLength, 0.5);
            }
            Thread.sleep(oneUnitOfMorseCode*spaceBetweenOneLetter);
        }
        
        
    }
}
