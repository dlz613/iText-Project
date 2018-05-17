package il.co.aman.formit;

import java.util.ArrayList;

/**
 * Encapsulates {@link String} values and error codes/error messages.
 *
 * @author davidz
 */
public class StringResults {

    String[] values, messages;
    Integer[] codes;
    int index, num;
    boolean allValid;
    ArrayList<String> _values, _messages;
    ArrayList<Integer> _codes;
    boolean _synced;

    /**
     * Constructor for use when the total number of values is not known in
     * advance.
     */
    public StringResults() {
        this._values = new ArrayList<String>();
        this._messages = new ArrayList<String>();
        this._codes = new ArrayList<Integer>();
        this.allValid = true;
        this.num = -1;
        this._synced = false;
    }

    /**
     * Constructor for use when the total number of values is known in advance.
     *
     * @param num The number of values.
     */
    public StringResults(int num) {
        this.values = new String[num];
        this.codes = new Integer[num];
        this.messages = new String[num];
        this.index = 0;
        this.allValid = true;
        this.num = num;
        this._synced = true;
    }

    /**
     *
     * @param val The {@link String} value.
     * @param code Error code - 0 indicates there was no error.
     * @param message The error message.
     */
    public void addResult(String val, int code, String message) {
        if (this.num < 0) { // dynamic allocation
            this._values.add(val);
            this._codes.add(code);
            this._messages.add(message);
            if (code != 0) {
                this.allValid = false;
            }
        } else {
            if (this.index < this.num) {
                this.values[this.index] = val;
                this.codes[this.index] = code;
                this.messages[this.index] = message;
                this.index++;
                if (code != 0) {
                    this.allValid = false;
                }
            }
        }
    }

    /**
     * When the no argument Constructor was used, this must be called after all of the results are added.
     */
    public void sync() {
        this.num = this._values.size();
        this.values = this._values.toArray(new String[this.num]);
        this.codes = this._codes.toArray(new Integer[this.num]);
        this.messages = this._messages.toArray(new String[this.num]);
        this._synced = true;
    }

    public boolean isAllValid() {
        return this.allValid;
    }

    /**
     * 
     * @param num
     * @return if <code>sync()</code> needs to be called and was not, returns <code>false</code>.
     */
    public boolean isValid(int num) {
        if (this._synced) {
            if (num < this.num) {
                return this.codes[num] == 0;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 
     * @return  if <code>sync()</code> needs to be called and was not, returns <code>null</code>.
     */
    public String[] getValues() {
        if (this._synced) {
            return this.values;
        } else {
            return null;
        }
    }

    public String getValue(int num) {
        if (this._synced) {
            if (num < this.num) {
                return this.values[num];
            } else {
                return "ERROR: array bound exceeded.";
            }
        } else {
            return "ERROR: ArrayList not synchronized.";
        }
    }

    /**
     * 
     * @param num
     * @return  if <code>sync()</code> needs to be called and was not, returns <code>-200</code>.
     */
    public int getCode(int num) {
        if (this._synced) {
            if (num < this.num) {
                return this.codes[num];
            } else {
                return -100;
            }
        } else {
            return -200;
        }
    }

    public String getMessage(int num) {
        if (this._synced) {
            if (num < this.num) {
                return this.messages[num];
            } else {
                return "ERROR: array bound exceeded.";
            }
        } else {
            return "ERROR: ArrayList not synchronized.";
        }
    }

    public String getValueOrError(int num) {
        if (this._synced) {
            if (this.isValid(num)) {
                return this.getValue(num);
            } else {
                return "ERROR: " + this.getMessage(num);
            }
        } else {
            return "ERROR: ArrayList not synchronized.";
        }
    }

    public String[] getValuesOrErrors() {
        if (this._synced) {
            String[] res = new String[num];
            for (int i = 0; i < this.num; i++) {
                res[i] = this.getValueOrError(i);
            }
            return res;
        } else {
            return new String[]{"ERROR: ArrayList not synchronized."};
        }
    }
    
    public static void main(String[] args) {
        StringResults sr = new StringResults();
        sr.addResult("george", 0, null);
        sr.addResult("error", 3, "Unspecified error.");
        sr.sync();
        System.out.println(sr.getValue(2));
    }
}
