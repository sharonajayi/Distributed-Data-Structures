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

    /**
     * Constructor.
     *
     * @param _msg The string to be encoded in this Message object
     *
     */
    public Message(String _msg){
	switch(_msg)
        {

            case "C": theMessage = "Commit to disk "; break;

            case "R": theMessage = "Rollback"; break;
            
            case "V": theMessage = "Viewing data"; break;
            
            default: 
                theMessage = _msg;

        }
        
    }
    
    public Message(String met, int value){
        this.val = value;
       
        switch(met)
        {
            case "A": theMessage = "Add " + value; break;

            case "D": theMessage = "Delete " + value; break;

        }
  
    }
    
    public Message(int pos, int value){
        this.pos = pos;
        this.val = value;
        
        theMessage = "Insert " + value + " at position " + pos;
    }

    public int getVal() {
        return val;
    }

    public int getPos() {
        return pos;
    }
    
    
}  //-- End class Message