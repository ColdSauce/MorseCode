
package morsecode;


public class Letter {
    private char[] code = null;
    protected Letter(char[] code){
        this.code = code;
    }
    protected char[] getMorseCode(){
        return code.clone();
    }
}
