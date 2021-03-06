
package com.cachirulop.whereiparked.common.exception;

public class ConfigurationException
        extends Exception
{
    /**
     * Default serialVersionUID generated by the compiler
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new object with the message received as parameter
     * 
     * @param msg
     *            Error message of the exception
     */
    public ConfigurationException (String msg)
    {
        super (msg);
    }
}
