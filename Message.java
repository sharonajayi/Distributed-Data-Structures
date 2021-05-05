package server;

/**
 * Marian Zaki (COSC 2354)
 * Very primitive-simple message class.  By implementing the Serializble
 * interface, objects of this class can be serialized automatically by
 * Java to be sent across IO streams.  
 *
 *  
 */
public class Message implements java.io.Serializable
{
    /** The text string encoded in this Message object */
    public String theMessage;
    private int val;
    private int pos;
    public boolean check;

    /**
     * Constructor.
     *
     * @param _msg The string to be encoded in this Message object
     * @param check
     *
     */
    public Message(String _msg, boolean check){
	switch(_msg)
        {

            case "C": theMessage = "Commit to disk "; break;

            case "R": theMessage = "Rollback"; break;
            
            case "V": theMessage = "Viewing data"; break;
            
            default: 
                theMessage = _msg;

        }
        
        this.check = check;
        
    }
    
    public Message(String met, int value, boolean check){
        this.val = value;
       
        switch(met)
        {
            case "A": theMessage = "Add " + value; break;

            case "D": theMessage = "Delete " + value; break;

        }
        
        this.check = check;
    }
    
    public Message(int pos, int value, boolean check){
        this.pos = pos;
        this.val = value;
        
        theMessage = "Insert " + value + " at position " + pos;
        this.check = check;
    }

    public int getVal() {
        return val;
    }

    public int getPos() {
        return pos;
    }
    
    
}  //-- End class Message