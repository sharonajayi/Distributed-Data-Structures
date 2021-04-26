package server_sample;
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
            
            default: 
                theMessage = _msg;

        }
        
    }
    
    public Message(String met, int value){
       
        switch(met)
        {
            case "A": theMessage = "Update: add " + value; break;

            case "D": theMessage = "Update: delete " + value; break;

        }
  
    }
    
    public Message(int pos, int value){
        
        theMessage = "Update: " + value + " at position " + pos;
    }

}  //-- End class Message