package server_sample;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package server_sample;

/**
 *
 * @author marianky
 */
public class Message2 implements java.io.Serializable{
    /** The text string encoded in this Message object */
    public String theMessage;

    /**
     * Constructor.
     *
     * @param _msg The string to be encoded in this Message object
     *
     */
    public Message2(String _msg){
	theMessage = _msg;
    }
}
