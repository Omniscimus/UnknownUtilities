package net.omniscimus.unknownutilities;

/**
 * Thrown if there is an error in an UnknownUtility.
 */
public class ModuleException extends Exception {
    private static final long serialVersionUID = 1L;

    public ModuleException(String string) {
	super(string);
    }

    public ModuleException(Exception ex) {
	super(ex);
    }
}
